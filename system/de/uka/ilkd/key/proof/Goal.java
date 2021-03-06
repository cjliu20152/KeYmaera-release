// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//


package de.uka.ilkd.key.proof;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import de.uka.ilkd.key.collection.DefaultImmutableSet;
import de.uka.ilkd.key.collection.ImmutableList;
import de.uka.ilkd.key.collection.ImmutableSLList;
import de.uka.ilkd.key.collection.ImmutableSet;
import de.uka.ilkd.key.gui.Main;
import de.uka.ilkd.key.gui.RuleAppListener;
import de.uka.ilkd.key.logic.ConstrainedFormula;
import de.uka.ilkd.key.logic.Constraint;
import de.uka.ilkd.key.logic.Named;
import de.uka.ilkd.key.logic.Namespace;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.PosInTerm;
import de.uka.ilkd.key.logic.Sequent;
import de.uka.ilkd.key.logic.SequentChangeInfo;
import de.uka.ilkd.key.logic.op.Metavariable;
import de.uka.ilkd.key.logic.op.ProgramVariable;
import de.uka.ilkd.key.proof.incclosure.BranchRestricter;
import de.uka.ilkd.key.proof.incclosure.Sink;
import de.uka.ilkd.key.proof.proofevent.NodeChangeJournal;
import de.uka.ilkd.key.proof.proofevent.RuleAppInfo;
import de.uka.ilkd.key.rule.BuiltInRule;
import de.uka.ilkd.key.rule.BuiltInRuleApp;
import de.uka.ilkd.key.rule.NoPosTacletApp;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.Taclet;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.UpdateSimplificationRule;
import de.uka.ilkd.key.rule.UpdateSimplifier;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.strategy.AutomatedRuleApplicationManager;
import de.uka.ilkd.key.strategy.QueueRuleApplicationManager;
import de.uka.ilkd.key.strategy.Strategy;

/**
 *  A proof is represented as a tree of nodes containing sequents. The initial
 *  proof consists of just one node -- the root -- that has to be
 *  proved. Therefore it is divided up into several sub goals and so on. A
 *  single goal is not divided into sub goals any longer if the contained
 *  sequent becomes an axiom. A proof is closed if all leaves are closed. As
 *  the calculus works only on the leaves of a tree, the goals are the
 *  additional information needed for the proof is only stored at the leaves
 *  (saves memory) and not in the inner nodes. This class represents now a goal
 *  of the proof, this means a leave whose sequent is not closed. It keeps
 *  track of all applied rule applications on the branch and of the
 *  corresponding rule application index. Furthermore it offers methods for
 *  setting back several proof steps. The sequent has to be changed using the
 *  methods of Goal.  
 */
public class Goal  {

	public static enum GoalStatus {
		UNKNOWN, CLOSED, BLOCKING, COUNTER_EXAMPLE;
	}
	
	private GoalStatus status = GoalStatus.UNKNOWN;
	
    private Node node;

    /** all possible rule applications at this node are managed with this index */ 
    private final RuleAppIndex ruleAppIndex;

    /** list of all applied rule applications at this branch */
    private ImmutableList<RuleApp> appliedRuleApps = ImmutableSLList.<RuleApp>nil();

    /** this object manages the tags for all formulas of the sequent */
    private FormulaTagManager tagManager;

    /** the strategy object that determines automated application of rules */
    private Strategy goalStrategy = null;

    /** */
    private AutomatedRuleApplicationManager ruleAppManager;

    /** goal listeners  */
    private List<GoalListener> listeners = new ArrayList<GoalListener>();

    /** a goal has been excluded from automatic rule application iff automatic == false */
    private boolean automatic = true;
    
    /** list of rule app listeners */
    private static List<RuleAppListener> ruleAppListenerList = 
        Collections.synchronizedList(new ArrayList<RuleAppListener>(10));

    /** creates a new goal referencing the given node */
    private Goal( Node                    node, 
		  RuleAppIndex            ruleAppIndex, 
		  ImmutableList<RuleApp>           appliedRuleApps,
		  FormulaTagManager       tagManager,
		  AutomatedRuleApplicationManager ruleAppManager ) {  
	this.node            = node;
	this.ruleAppIndex    = ruleAppIndex;
	this.appliedRuleApps = appliedRuleApps;
	this.tagManager      = tagManager;
	this.goalStrategy    = null;
        this.ruleAppIndex.setup ( this );
        setRuleAppManager ( ruleAppManager );       
    }

    /** 
     * creates a new goal referencing the given node 
     */
    public Goal (Node node, RuleAppIndex ruleAppIndex) {
        this ( node,
               ruleAppIndex,
               ImmutableSLList.<RuleApp>nil(),
               null,
               new QueueRuleApplicationManager () );
        tagManager = new FormulaTagManager ( this );
    }

    /** returns the simplifier that has to be used */
    public UpdateSimplifier simplifier() {
	return proof().simplifier();
    }

    /** this object manages the tags for all formulas of the sequent */
    public FormulaTagManager getFormulaTagManager () {
    	return tagManager;
    }

    /**
     * @return the strategy that determines automated rule applications for this
     * goal
     */
    public Strategy getGoalStrategy () {
        if ( goalStrategy == null )
            goalStrategy = proof ().getActiveStrategy ();
        return goalStrategy;
    }
    
    public void setGoalStrategy ( Strategy p_goalStrategy ) {
        goalStrategy = p_goalStrategy;
        ruleAppManager.clearCache ();
    }

    public AutomatedRuleApplicationManager getRuleAppManager () {
    	return ruleAppManager;
    }

    public void setRuleAppManager(AutomatedRuleApplicationManager manager) {
        if ( ruleAppManager != null ) {
            ruleAppIndex ().removeNewRuleListener ( ruleAppManager );
            ruleAppManager.setGoal ( null );
        }
        
        ruleAppManager = manager;
        
        if ( ruleAppManager != null ) {
            ruleAppIndex ().addNewRuleListener ( ruleAppManager );
            ruleAppManager.setGoal ( this );
        }
    }
    
    /** 
     * returns the referenced node 
     */
    public Node node() {
	return node;
    }

    public ImmutableSet<ProgramVariable> getGlobalProgVars() {
	return node().getGlobalProgVars();
    }
    
    public Namespace createGlobalProgVarNamespace() {
        final Namespace ns = new Namespace();
        for (ProgramVariable programVariable : getGlobalProgVars()) {
            ns.add(programVariable);
        }
        return ns;
    }

    /** 
     * adds the listener l to the list of goal listeners.
     * Attention: A listener added to this goal will be taken over when
     * splitting into subgoals. 
     * @param l the GoalListener to be added
     */
    public void addGoalListener(GoalListener l) {
	listeners.add(l);
    }

    /**
     * removes the listener l from the list of goal listeners.
     * Attention: The listener is just removed from 'this' goal not from the
     * other goals. (All goals can be accessed via proof openGoals())
     * @param l the GoalListener to be removed
     */
    public void removeGoalListener(GoalListener l) {
	listeners.remove(l);
    }

    /** 
     * informs all goal listeners about a change of the sequent
     * to reduce unnecessary object creation the necessary information is passed
     * to the listener as parameters and not through an event object.
     */
    protected void fireSequentChanged(SequentChangeInfo sci) {
	getFormulaTagManager().sequentChanged(this, sci);
	ruleAppIndex()        .sequentChanged(this, sci); 
	for (int i = 0, sz = listeners.size(); i<sz; i++) {
	    listeners.get(i).sequentChanged(this, sci);
	}
    }

    protected void fireGoalReplaced(Goal       goal,
				    Node       parent,
				    ImmutableList<Goal> newGoals) {
	for (int i = 0, sz = listeners.size(); i<sz; i++) {
	    listeners.get(i).goalReplaced(goal, parent, newGoals);
	}
    }

    /**
     * adds the global program variables to a new created variable namespace
     * that contains all the elements of the given namespace.
     */
    public Namespace getVariableNamespace(Namespace exNS) {
	Namespace newNS = exNS;
	final Iterator<ProgramVariable> it=getGlobalProgVars().iterator();
	if (it.hasNext()) {
	    newNS=newNS.extended(it.next());
	}
	while (it.hasNext()) {
	    newNS.add(it.next());
	}
	return newNS;
    }

    public void setGlobalProgVars(ImmutableSet<ProgramVariable> s) {
        ImmutableSet<ProgramVariable> globalProgVars = getGlobalProgVars();
        Namespace ns = proof().getNamespaces().programVariables();
        for (ProgramVariable value : s) {
            if (!globalProgVars.contains(value)) {
                ns.addSafely(value);
            }
        }
	node.setGlobalProgVars(s);
    }

    /** 
     * set the node the goal is related to
     * @param p_node the Node in the proof tree to which this goal 
     * refers to
     */
    private void setNode(Node p_node) {
	if ( node ().sequent () != p_node.sequent () ) {
	    node = p_node;
	    resetTagManager();
	} else
	    node = p_node;
	ruleAppIndex.setup ( this );
    }

    /** 
     * returns the index of possible rule applications at this node
     */
    public RuleAppIndex ruleAppIndex() {
	return ruleAppIndex;
    }
     
    /**
     * returns the Taclet index for this goal. This is just a shortcut to the
     * Taclet index of the RuleAppIndex
     * @return the Taclet index assigned to this goal
     */
    public TacletIndex indexOfTaclets() {
	return ruleAppIndex.tacletIndex();
    }
   
    /** adds a formula to the sequent before the given position
     * and informs the rule appliccation index about this change
     * @param cf the ConstrainedFormula to be added
     * @param p PosInOccurrence encodes the position 
     */
    public void addFormula(ConstrainedFormula cf, PosInOccurrence p) {
	setSequent(sequent().addFormula(cf, p));	
    }

    /** adds a list of formulas to the sequent before the given position
     * and informs the rule appliccation index about this change
     * @param insertions the IList<ConstrainedFormula> to be added
     * @param p PosInOccurrence encodes the position 
     */
    public void addFormula(ImmutableList<ConstrainedFormula> insertions, PosInOccurrence p) {
	if ( !insertions.isEmpty() ) {	  
	    setSequent(sequent().addFormula(insertions, p));
	}
    }

    /** adds a list of formulas to the antecedent or succedent of a  
     * sequent. Either at its front or back.
     * and informs the rule appliccation index about this change
     * @param insertions the IList<ConstrainedFormula> to be added
     * @param inAntec boolean true(false) if ConstrainedFormula has to be
     * added to antecedent (succedent) 
     * @param first boolean true if at the front, if false then cf is
     * added at the back
     */
    public void addFormula ( ImmutableList<ConstrainedFormula> insertions, 
			     boolean inAntec, boolean first ) {
	if ( !insertions.isEmpty() ) {
	    setSequent(sequent().
                    addFormula(insertions, inAntec, first));
	}
    }

    /** adds a formula to the antecedent or succedent of a
     * sequent. Either at its front or back
     * and informs the rule appliccation index about this change
     * @param cf the ConstrainedFormula to be added
     * @param inAntec boolean true(false) if ConstrainedFormula has to be
     * added to antecedent (succedent) 
     * @param first boolean true if at the front, if false then cf is
     * added at the back
     */
    public void addFormula ( ConstrainedFormula cf, boolean inAntec,
			     boolean first ) {
	setSequent(sequent().addFormula(cf, inAntec, first));
    }

    /** returns set of rules applied at this branch 
     * @return IList<RuleApp> applied rule applications
     */
    public ImmutableList<RuleApp> appliedRuleApps() {
	return appliedRuleApps;
    }
	
	
    /**
     * @return the current time of this goal (which is just the number of
     * applied rules)
     */
    public long getTime () {
    	return appliedRuleApps().size();
    }

    /** returns the proof the goal belongs to 
     * @return the Proof the goal belongs to
     */
    public Proof proof() {
        return node().proof();
    }
    
    /** returns the sequent of the node 
     * @return the Sequent to be proved
     */
    public Sequent sequent() {
	return node().sequent();
    }
    
    /**
     * Checks if is an automatic goal.
     * 
     * @return true, if is automatic
     */
    public boolean isAutomatic() {
        return automatic;
    }
    
    /**
     * Sets the automatic status of this goal.
     * 
     * @param t
     *                the new status: true for automatic, false for interactive
     */
    public void setEnabled(boolean t) {
        automatic = t;
    }

    
    /** 
     * sets the sequent of the node 
     * @param sci SequentChangeInfo containing the sequent to be set and 
     * desribing the applied changes to the sequent of the parent node
     */    
    public void setSequent(SequentChangeInfo sci) {
        node().setSequent(sci.sequent());
//VK reminder: now update the index        
       	fireSequentChanged(sci);
    }


    /** 
     * replaces a formula at the given position  
     * and informs the rule application index about this change
     * @param cf the ConstrainedFormula replacing the old one
     * @param p the PosInOccurrence encoding the position 
     */
    public void changeFormula(ConstrainedFormula cf, PosInOccurrence p) {	
	setSequent(sequent().changeFormula(cf, p));
    }

    /** 
     * replaces a formula at the given position  
     * and informs the rule appliccation index about this change
     * @param replacements the ConstrainedFormula replacing the old one
     * @param p PosInOccurrence encodes the position 
     */
    public void changeFormula(ImmutableList<ConstrainedFormula> replacements, 
			      PosInOccurrence p) {
	setSequent(sequent().changeFormula(replacements, p));
    }

    /** removes a formula at the given position from the sequent 
     * and informs the rule appliccation index about this change
     * @param p PosInOccurrence encodes the position 
     */
    public void removeFormula(PosInOccurrence p) {	
	setSequent(sequent().removeFormula(p));
    }

    /**
     * puts the NoPosTacletApp to the set of TacletApps at the node
     * of the goal and to the current RuleAppIndex.
     * @param app the TacletApp
     */
    public void addNoPosTacletApp(NoPosTacletApp app) {
	node().addNoPosTacletApp(app);
	ruleAppIndex.addNoPosTacletApp(app);      
    }

    /**
     * creates a new TacletApp and puts it to the set of TacletApps at the node
     * of the goal and to the current RuleAppIndex.
     * @param rule the Taclet of the TacletApp to create
     * @param insts the given instantiations of the TacletApp to be created
     * @param constraint the constraint under which the taclet can be applied
     */
    public void addTaclet(Taclet           rule,
			  SVInstantiations insts,
			  Constraint       constraint,
                          boolean          isAxiom) {		
	NoPosTacletApp tacletApp =
	    NoPosTacletApp.createFixedNoPosTacletApp(rule, insts, constraint);
	if (tacletApp != null) {
	    addNoPosTacletApp(tacletApp);
 	    if (proof().env()!=null) { // do not break everything
                                       // because of ProofMgt
		proof().env().registerRuleIntroducedAtNode(
		        tacletApp, 
		        node.parent() != null ? node.parent() : node, 
		        isAxiom);
	    }
	}
    }

    /**
     * Rebuild all rule caches
     */
    public void updateRuleAppIndex () {
        getRuleAppManager ().clearCache ();
        ruleAppIndex.clearIndexes ();
    }

    /**
     * Rebuild all rule caches
     */
    public void clearAndDetachRuleAppIndex () {
        getRuleAppManager ().clearCache ();
        ruleAppIndex.clearAndDetachCache ();
    }

    public void addProgramVariable(ProgramVariable pv) {
        proof().getNamespaces().programVariables().addSafely(pv);
	node.setGlobalProgVars(getGlobalProgVars().add(pv));
    }

    public void setProgramVariables(Namespace ns) {
	final Iterator<Named> it=ns.elements().iterator();
	ImmutableSet<ProgramVariable> s = DefaultImmutableSet.<ProgramVariable>nil();
	while (it.hasNext()) {
	    s = s.add((ProgramVariable)it.next());
	}
        proof().getNamespaces().programVariables().reset();
        node().setGlobalProgVars(DefaultImmutableSet.<ProgramVariable>nil());
	setGlobalProgVars(s);
    }


    /** 
     * clones the goal (with copy of tacletindex and ruleAppIndex)
     * @return Object the clone
     */
    public Object clone() {	
	Goal clone = new Goal ( node,
	                        ruleAppIndex.copy (),
	                        appliedRuleApps,
	                        getFormulaTagManager ().copy (),
				ruleAppManager.copy () );
	clone.listeners = (List<GoalListener>)
	    ((ArrayList<GoalListener>) listeners).clone();
	clone.automatic = this.automatic;
	return clone;
    }

    /** like the clone method but returns right type
     * @return Goal clone of this Goal
     */
    public Goal copy() {
	return (Goal)clone();
    }

    /**
     * puts a RuleApp to the list of the applied rule apps at this goal
     * and stores it in the node of the goal
     * @param app the applied rule app
     */
    public void addAppliedRuleApp(RuleApp app) {
	// Last app first makes inserting and searching faster
	appliedRuleApps = appliedRuleApps.prepend(app);
	node().setAppliedRuleApp(app);
    }

    /**
     * PRECONDITION: appliedRuleApps.size () > 0
     */
    public void removeAppliedRuleApp () {
	appliedRuleApps = appliedRuleApps.tail ();
	node ().setAppliedRuleApp ( null );
    }

    
    /** creates n new nodes as children of the
     * referenced node and new 
     * n goals that have references to these new nodes.
     * @return the list of new created goals.
     */
    public ImmutableList<Goal> split(int n) {
	ImmutableList<Goal> goalList=ImmutableSLList.<Goal>nil();
	Node parent = node(); // has to be stored because the node
	                      // of this goal will be replaced
        if (n>0) {
	    Iterator<Sink> itSinks = parent.reserveSinks ( n );
	    BranchRestricter br;
	    Node newNode;
	    Goal newGoal;

	    for (int i=0;i<n;i++) {
		if (i==0) { // first new goal is this one 
		    newGoal = this;
		} else { // otherwise it is a copy
		    newGoal = copy();
		}
		// create new node and add to tree
		if ( n > 1 ) {
		    br = new BranchRestricter ( itSinks.next () );
		    newNode = new Node(parent.proof(),
				       parent.sequent(),
				       null,
				       parent,
				       br);
		    br.setNode ( newNode );
		} else
		    newNode = new Node(parent.proof(),
				       parent.sequent(),
				       null,
				       parent,
				       itSinks.next ());

		// newNode.addNoPosTacletApps(parent.getNoPosTacletApps());
		newNode.setGlobalProgVars(parent.getGlobalProgVars());
		parent.add(newNode);

		// make new Goal and add to list
		newGoal.setNode(newNode);

		goalList = goalList.prepend(newGoal);
	    }
	} else {
		status = GoalStatus.CLOSED;
	}
	
	fireGoalReplaced ( this, parent, goalList );

	return goalList;
    }

    /**
     * sets back the proof step that led to this goal. This goal is set to
     * the parent node of the node corresponding to this goal. Goals given in
     * the goal list parameter are removed from that list, if their
     * corresponding nodes are leaves of the parent node of this goal.
     * @param goalList the IList<Goal> with the goals to be removed 
     * @return the new list of goals where goals mapped to the leaves of
     * the parent to this goal are removed from compared to the given list.
     */
    public ImmutableList<Goal> setBack(ImmutableList<Goal> goalList) {
	final Node parent = node.parent();
	final Iterator<Node> leavesIt = parent.leavesIterator();
	while (leavesIt.hasNext()) {
	    Node n = leavesIt.next();

        for (final Goal g : goalList) {
            if (g.node() == n && g != this) {
                goalList = goalList.removeFirst(g);
            }
        }
	}

	//	ruleAppIndex.tacletIndex().setTaclets(parent.getNoPosTacletApps());

        removeTaclets();
	setGlobalProgVars(parent.getGlobalProgVars());

	parent.cutChildrenSinks ();
	if (node.proof().env()!=null) { // do not break everything
	                                // because of ProofMgt
	    node.proof().mgt().ruleUnApplied(parent.getAppliedRuleApp());
	}

	Iterator<Node> siblings=parent.childrenIterator();
	Node[] sibls=new Node[parent.childrenCount()];
	int i=0;
	while (siblings.hasNext()) {
	    sibls[i]=siblings.next(); 
	    i++;
	}

	for (i=0; i<sibls.length; i++) {
	    sibls[i].remove();
	}

	setNode(parent);
	removeAppliedRuleApp ();
        
        updateRuleAppIndex();
        
	return goalList;
    }

    private void resetTagManager() {
        tagManager = new FormulaTagManager ( this );
    }

    private void removeTaclets() {
        for (NoPosTacletApp noPosTacletApp : node.getNoPosTacletApps())
            ruleAppIndex.removeNoPosTacletApp(noPosTacletApp);
    }

    
    public Constraint getClosureConstraint () {
	return node ().getClosureConstraint ();
    }

    public void addClosureConstraint ( Constraint c ) {
	node ().addClosureConstraint ( c );
    }

    public void addRestrictedMetavariable ( Metavariable mv ) {
	node ().addRestrictedMetavariable ( mv );
    }
    
    public void setBranchLabel(String s) {
        node.getNodeInfo().setBranchLabel(s);
    }
    

    /** fires the event that a rule has been applied */
    protected void fireRuleApplied(final ProofEvent p_e ) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				synchronized(ruleAppListenerList) {
				for (final RuleAppListener aRuleAppListenerList : ruleAppListenerList) {
					aRuleAppListenerList.ruleApplied(p_e);
				}
				}
			}
		};
		if (Main.getInstance().mediator().autoMode() || SwingUtilities.isEventDispatchThread()) {
        	runnable.run();
        } else {
        	try {

				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }    
    
    
    public ImmutableList<Goal> apply( RuleApp p_ruleApp ) {
//System.err.println(Thread.currentThread());    

        final Proof proof = proof();
                
        final NodeChangeJournal journal = new NodeChangeJournal(proof, this);
        addGoalListener(journal);
        
        final RuleApp ruleApp = completeRuleApp( p_ruleApp ); 

        final Node n = node;
        
        final ImmutableList<Goal> goalList = ruleApp.execute(this,  
                proof.getServices());

        proof.getServices().saveNameRecorder(n);
        
        if ( goalList == null ) {
            // this happens for the simplify decision procedure
            // we do nothing in this case
        } else if ( goalList.isEmpty() ) {
            proof.closeGoal ( this, ruleApp.constraint () );           
        } else {
            proof.replace ( this, goalList );
            if ( ruleApp instanceof TacletApp &&
                    ((TacletApp)ruleApp).taclet ().closeGoal () )
                // the first new goal is the one to be closed
                proof.closeGoal ( goalList.head (), ruleApp.constraint () );
        }

        final RuleAppInfo ruleAppInfo = journal.getRuleAppInfo(p_ruleApp);
        if ( goalList != null )
            fireRuleApplied( new ProofEvent ( proof, ruleAppInfo ) );
        return goalList;
    }




    public static void applyUpdateSimplifier( ImmutableList<Goal> goalList ) {
		if (goalList == null)
			return;
    	
        for (Goal aGoalList : goalList) {
            aGoalList.applyUpdateSimplifier();
        }
    }
    
    public void applyUpdateSimplifier() {
        applyUpdateSimplifier(true);
        applyUpdateSimplifier(false);
    }


    private void applyUpdateSimplifier (boolean antec) {
	    final Constraint userConstraint =
	        proof().getUserConstraint ().getConstraint ();
	    final BuiltInRule rule = UpdateSimplificationRule.INSTANCE;

        for (Object o : (antec ? sequent().antecedent()
                : sequent().succedent())) {
            final ConstrainedFormula cfma = (ConstrainedFormula) o;
            final PosInOccurrence pos = new PosInOccurrence(cfma,
                    PosInTerm.TOP_LEVEL,
                    antec);
            if (rule.isApplicable(this, pos, userConstraint)) {
                BuiltInRuleApp app = new BuiltInRuleApp(rule,
                        pos,
                        userConstraint);
                apply(app);
            }
        }
	}





    /** toString */
    public String toString() {
	return (node.sequent().prettyprint(proof().getServices()).toString());
    }

    /** make Taclet instantiations complete with regard to metavariables and
     * Skolemfunctions
     */ 
    private RuleApp completeRuleApp ( RuleApp ruleApp ) {
        final Proof proof = proof();
        if (ruleApp instanceof TacletApp) {
            TacletApp tacletApp = (TacletApp)ruleApp;
            
            tacletApp = tacletApp.instantiateWithMV ( this );
            
            ruleApp = tacletApp.createSkolemFunctions 
                ( proof.getNamespaces().functions(), 
                       proof.getServices() );
        }
        return ruleApp;
    }
    
    
    public static void addRuleAppListener(RuleAppListener p) { 
	synchronized(ruleAppListenerList) {
	    ruleAppListenerList.add(p);
	}
    }
    
    public static void removeRuleAppListener(RuleAppListener p) { 
	synchronized(ruleAppListenerList) {	
	    ruleAppListenerList.remove(p);
	}
    }
    // %%%%%%%% HACK !!! REMOVE AS SOON AS POSSIBLE %%%%%%
    public static List getRuleAppListener(){
        return ruleAppListenerList;
    }
    public static void setRuleAppListenerList(List ruleAppListenerList){
        Goal.ruleAppListenerList = ruleAppListenerList;
    }
    // %%%%%%%%%%%%%%%%

	/**
	 * @return the status
	 */
	public GoalStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(GoalStatus status) {
		this.status = status;
	}
}
