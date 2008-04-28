package de.uka.ilkd.key.visualdebugger.watchpoints;

import java.util.*;
import java.util.Map.Entry;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.SourceElement;
import de.uka.ilkd.key.java.StatementContainer;
import de.uka.ilkd.key.java.declaration.VariableSpecification;
import de.uka.ilkd.key.java.reference.ExecutionContext;
import de.uka.ilkd.key.java.reference.IExecutionContext;
import de.uka.ilkd.key.java.reference.ReferencePrefix;
import de.uka.ilkd.key.java.statement.MethodBodyStatement;
import de.uka.ilkd.key.java.statement.MethodFrame;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.LocationVariable;
import de.uka.ilkd.key.logic.op.Modality;
import de.uka.ilkd.key.logic.op.ProgramMethod;
import de.uka.ilkd.key.logic.op.QuanUpdateOperator;
import de.uka.ilkd.key.proof.IteratorOfNode;
import de.uka.ilkd.key.proof.ListOfNode;
import de.uka.ilkd.key.proof.Node;
import de.uka.ilkd.key.proof.SLListOfNode;
import de.uka.ilkd.key.rule.ListOfRuleSet;
import de.uka.ilkd.key.rule.RuleSet;
import de.uka.ilkd.key.rule.Taclet;
import de.uka.ilkd.key.visualdebugger.VisualDebugger;

public class VariableNameTracker {

    /** The current proof tree.*/
    private Node node;
    /** The watchpoints.*/
    private List<WatchPoint> watchpoints;
    private ListOfNode branch = null;
    private Map<ProgramMethod, ListOfRenamingTable> nameMaps = new HashMap<ProgramMethod, ListOfRenamingTable>();
    private Stack<ProgramMethod> methodStack = new Stack<ProgramMethod>();
    private Stack<ReferencePrefix> selfVarStack = new Stack<ReferencePrefix>();
    private ReferencePrefix selfVar = null;
    
    public VariableNameTracker(Node node, List<WatchPoint> watchpoints) {
        super();
        this.node = node;
        this.watchpoints = watchpoints;
        branch = buildBranch(node);
    }


    /**
     * @param node
     */
    private SourceElement getStatement(Node node) {
        try {

            IteratorOfConstrainedFormula iterator = node.sequent().iterator();
            ConstrainedFormula constrainedFormula;
            Term term;
            while (iterator.hasNext()) {
                constrainedFormula = iterator.next();
                term = constrainedFormula.formula();

                while (term.op() instanceof QuanUpdateOperator) {
                    int targetPos = ((QuanUpdateOperator) term.op())
                    .targetPos();
                    term = term.sub(targetPos);
                }
                // proceed to most inner method-frame
                if (term.op() instanceof Modality) {
                    ProgramPrefix programPrefix = (ProgramPrefix) term
                    .javaBlock().program();
                    return programPrefix.getPrefixElementAt(programPrefix
                            .getPrefixLength() - 1);

                }
            }
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
   
    
    /**
     * Gets the indices of all parameters that are used in (all)watchpoints for the
     * given method.
     * 
     * @param programMethod
     *                the program method
     * 
     * @return the parameter indices of method, null if no local variables are
     *         used
     */
    private Set<Integer> getParameterIndicesOfMethod(
            ProgramMethod programMethod) {

        int parameterCount = programMethod.getParameterDeclarationCount();
        Set<Integer> parameterIndices = new HashSet<Integer>();

        for (WatchPoint watchPoint : getWatchpointsForMethod(programMethod)) {

                for (int position : watchPoint.getKeyPositions()) {

                    if( position < parameterCount) {
                        parameterIndices.add(position);
                    }
                }
        }
        return parameterIndices;
    }
 
    /**
     * Checks if the given listOfRuleSet contains the method-expand taclet.
     * 
     * */
    private boolean isMethodExpandRule(ListOfRuleSet listOfRuleSet) {
        return listOfRuleSet.contains(
                new RuleSet(
                        new Name("method_expand")));
    }
    /**
     * Add parameter count.
     *
     *  In this method the we add the parametercount on the position of the variables from the method
     *  body. After that the correct order of the variables is rebuild according to the original ones.
     * 
     * @param programMethod the program method
     * @param variables the variables
     * @param parameterCount the parameter count
     * @param watchpoints the watchpoints
     * 
     * @return the renamed local variables
     */
    private List<LocationVariable> addParameterCount(
            ProgramMethod programMethod, Map<Integer, SourceElement> variables,
            int parameterCount, List<WatchPoint> watchpoints) {

        Set<Entry<Integer, SourceElement>> entrySet = variables.entrySet();
        List<LocationVariable> localVariables = new LinkedList<LocationVariable>();
        
        for (WatchPoint watchPoint : getWatchpointsForMethod(programMethod)){
                for (int position : watchPoint.getKeyPositions()) {
                    for (Entry<Integer, SourceElement> entry : entrySet) {
                        if (entry.getKey() + parameterCount == position) {
                            VariableSpecification varspec = (VariableSpecification) entry.getValue();
                            localVariables.add((LocationVariable) varspec.getProgramVariable());
                        }
                    }
                }
        }
        return localVariables;
    }

    /**
     * builds a branch from the root to the given node
     * 
     * @param n - an arbitrary node
     * @return a list of nodes from the root the passed node
     */
    private ListOfNode buildBranch(Node n) {
        ListOfNode lon = SLListOfNode.EMPTY_LIST;
        while(n.parent() != null){
            lon = lon.append(n);
            n=n.parent();
        }
        return lon.reverse();
    }
    /**
     * updates the MethodStack.
     * checks for a given node pair if methods returned and if
     * so, it removes the corresponding entry from the name map
     * 
     * @param current
     * @param child
     * @param nameMap
     */
    private void updateMethodStack(Node current, Node child, Map<ProgramMethod, ListOfRenamingTable> nameMap) {
        try {
            
            int current_stacksize = VisualDebugger.getVisualDebugger()
                    .getMethodStackSize(current);
            int next_stacksize = VisualDebugger.getVisualDebugger()
                    .getMethodStackSize(child);
            
            if (current_stacksize == -1 || next_stacksize == -1)    return;
            
            if (current_stacksize > next_stacksize) {
                int diff = current_stacksize - next_stacksize;
                for (int k = 0; k < diff; k++) {
                    if (!methodStack.isEmpty()) {
                       selfVarStack.pop();
                       ProgramMethod key =  methodStack.pop();
                       if(nameMap.containsKey(key)){
                       ListOfRenamingTable lort = nameMap.get(key);
                       lort = lort.removeFirst(lort.head());
                       nameMap.put(key, lort);
                       }
                       selfVar.toString();
                       methodStack.toString();
                    }
                }
            }
        } catch (EmptyStackException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Update self variable for local watchpoints. TODO
     * 
     * @param mf the MethodFrame
     */
    private void updateSelfVar(MethodFrame mf) {
        ExecutionContext executionContext = (ExecutionContext) mf.getExecutionContext();
        selfVar = executionContext.getRuntimeInstance();
        selfVarStack.push(selfVar);
        System.out.println(selfVar);
    }


    /**
     * Collect all renamings from the current proof tree.
     * 
     * @return the list of renaming table
     */
    private ListOfRenamingTable collectAllRenamings() {

        ListOfRenamingTable allRenamings = SLListOfRenamingTable.EMPTY_LIST;

        IteratorOfNode it = branch.iterator();
        while (it.hasNext()) {
            Node currentNode = it.next();
            ListOfRenamingTable renamingTables = currentNode.getRenamingTable();
            if (renamingTables != null && renamingTables.size() > 0) {
                System.out.println("found renaming @node: " + currentNode.serialNr());
                IteratorOfRenamingTable i = renamingTables.iterator();

                while (i.hasNext()) {
                    RenamingTable next = i.next();
                    System.out.println(next); //TODO remove
                    allRenamings = allRenamings.append(next);
                }
            }
        }
        return allRenamings;
    }
    
    /**
     * Track variable names.
     * 
     * @param pm the pm
     * @param initialRenamings the initial renamings
     * 
     * @return the renaming table
     */
    private RenamingTable trackVariableNames(ProgramMethod pm, List<LocationVariable> initialRenamings) {

        List<LocationVariable> orginialLocalVariables = getLocalsForMethod(pm);
        HashMap<LocationVariable, SourceElement> nameMap = new HashMap<LocationVariable, SourceElement>();

        assert orginialLocalVariables.size() == initialRenamings.size();

        for(int k = 0; k<orginialLocalVariables.size(); k++) {
            // create standard mapping from original var -> initially renamed var
            LocationVariable originalVar = orginialLocalVariables.get(k);
            LocationVariable initiallyRenamedVar = initialRenamings.get(k);
            nameMap.put(originalVar, initiallyRenamedVar);
            System.out.println("created initial mapping");
            IteratorOfRenamingTable i = collectAllRenamings().iterator();

            while (i.hasNext()) {
                RenamingTable renaming = i.next();

                SourceElement renamedVariable = renaming
                .getRenaming(initiallyRenamedVar);

                if (renamedVariable != null) {
                    // replace entry with the most actual one
                    nameMap.put(originalVar, renamedVariable);
                    System.out.println("created name update");
                }
                //trackHistory(nameMap);
            }
        }
        return new MultiRenamingTable(nameMap);
    }
    //TODO the method is buggy and causes a stack overflow
    // it does not terminate because the values are not "disappearing" from the namemap
    private void trackHistory(HashMap<LocationVariable, SourceElement> nameMap) {
        
        IteratorOfRenamingTable i = collectAllRenamings().iterator();
        boolean allNamesUpToDate = true;
        while (i.hasNext()) {
            RenamingTable renaming = i.next();

            for (SourceElement name : nameMap.values()) {
                SourceElement renamedVariable = renaming
                .getRenaming(name);

                if (renamedVariable != null) {
                    // replace entry with the most actual one
                    allNamesUpToDate = false;
                    System.out.println("traced name...");
                    nameMap.put((LocationVariable) name, renamedVariable);
                }
            }
        }if(allNamesUpToDate){return;} else {trackHistory(nameMap); }
    }
/**
 * some helper methods 
 */

    private void addRenamingTable(ProgramMethod key, Map<ProgramMethod, ListOfRenamingTable> nameMap, RenamingTable newElement){
        ListOfRenamingTable lort = nameMap.get(key);
        lort = nameMap.get(key).prepend(newElement);
        nameMap.put(key, lort);
        }
    
    private List<LocationVariable> getAllLocalVariables(/*List<WatchPoint> watchpoints*/){
        List<LocationVariable> locals = new LinkedList<LocationVariable>();
        for (WatchPoint watchPoint : watchpoints) {
            locals.addAll(watchPoint.getOrginialLocalVariables());
            }
        return locals;
    }
    private List<LocationVariable> getLocalsForMethod(ProgramMethod pm){
        List<LocationVariable> locals = new LinkedList<LocationVariable>();
        for (WatchPoint watchPoint : watchpoints) {
            if(watchPoint.getProgramMethod().equals(pm))
            locals.addAll(watchPoint.getOrginialLocalVariables());
            }
        return locals;
    }
    private List<WatchPoint> getWatchpointsForMethod(ProgramMethod pm){
        List<WatchPoint> wps = new LinkedList<WatchPoint>();
        for (WatchPoint watchPoint : watchpoints) {
            
            ProgramMethod programMethod = watchPoint.getProgramMethod();
            if(programMethod!= null && programMethod.equals(pm))
            wps.add(watchPoint);
            }
        return wps;
    }
    
    /**
     * Starts the name tracker.
     * 
     * When the KeY Prover is started every variable is initially renamed by the ProgVarReplaceVisitor, i.e. it
     * is a new object. If we have used local variables in the watchpoints we have
     * to keep track of these renamings. Therefore this method first looks up all applications of method-expand taclets.
     * In those methods we check first if they contain parameters that are relevant for us and furthermore store the
     * parameter count. Finally the following method-frame is investigated and the parameter count added to rebuild
     * the original order.
     * 
     */
    public void start() {

        try {
            final Services services = node.proof().getServices();

            List<LocationVariable> renamedLocalVariables = null;
            ProgramMethod programMethod = null;
            int parameterCount = 0;

            Iterator<Node> i = branch.iterator();
            assert branch.size() >= 2;
            Node current = i.next();
            while (i.hasNext()) {

                Node child = i.next();
                updateMethodStack(current, child, nameMaps);

                if (current.getAppliedRuleApp().rule() instanceof Taclet) {

                    if (isMethodExpandRule(((Taclet) current
                            .getAppliedRuleApp().rule()).getRuleSets())) {

                        renamedLocalVariables = new LinkedList<LocationVariable>();
                        // treat parent, i.e. the method-body-statement to get
                        // parameter information
                        SourceElement parentElement = getStatement(current);
                        MethodBodyStatement mbs = null;
                        if (parentElement instanceof StatementContainer) {

                            mbs = (MethodBodyStatement) parentElement
                                    .getFirstElement();
                            programMethod = mbs.getProgramMethod(node.proof()
                                    .getServices());
                            methodStack.push(programMethod);
                            System.out.println(methodStack.size()
                                    + "elements on stack after push");
                            if (!nameMaps.containsKey(programMethod)) {
                                System.out.println("added mbs to name map");
                                nameMaps.put(programMethod,
                                        SLListOfRenamingTable.EMPTY_LIST);
                            }
                        }

                        parameterCount = programMethod
                                .getParameterDeclarationCount();
                        Set<Integer> parameterIndices = getParameterIndicesOfMethod(programMethod);

                        for (Integer index : parameterIndices) {

                            LocationVariable programVariable = (LocationVariable) mbs
                                    .getArguments().getExpression(index);
                            renamedLocalVariables.add(programVariable);
                        }

                        // treat currentnode, i.e. the method-frame
                        SourceElement element = getStatement(child);
                        // Before getting the finally renamed variables we have
                        // to get all variables that are declared
                        // in the method body. The resulting positions are not
                        // correct yet since the parameter count is missing.
                        if (element instanceof MethodFrame) {

                            MethodFrame mf = (MethodFrame) element;
                            MethodVisitor mv = new MethodVisitor(mf, services);
                            mv.start();
                            System.out.println(mf.getExecutionContext());
                            updateSelfVar(mf);
                            renamedLocalVariables.addAll(addParameterCount(
                                    programMethod, WatchpointUtil.valueToKey(mv
                                            .result()), parameterCount,
                                    watchpoints));
                        }
                        System.out.println("size of renamed variables: "
                                + renamedLocalVariables.size());
                        if (renamedLocalVariables.isEmpty()) {
                            nameMaps.remove(programMethod);
                            System.out.println("removed mbs");
                        } else {

                            addRenamingTable(programMethod, nameMaps,
                                    trackVariableNames(programMethod,
                                            renamedLocalVariables));
                        }
                    }
                }
                current = child;
            }
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
 public Map<ProgramMethod, ListOfRenamingTable> result (){
     return nameMaps;
 }

public ReferencePrefix getSelfVar() {
    return selfVarStack.peek();
}  
 
}
