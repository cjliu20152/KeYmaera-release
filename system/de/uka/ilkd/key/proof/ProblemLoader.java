// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2005 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License.
// See LICENSE.TXT for details.
//
//

package de.uka.ilkd.key.proof;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import de.uka.ilkd.key.collection.ImmutableList;
import de.uka.ilkd.key.collection.ImmutableSLList;
import de.uka.ilkd.key.collection.ImmutableSet;
import de.uka.ilkd.key.dl.gui.TimeStatisticGenerator;
import de.uka.ilkd.key.dl.options.DLOptionBean;
import de.uka.ilkd.key.dl.rules.ReduceRule;
import de.uka.ilkd.key.dl.rules.ReduceRuleApp;
import de.uka.ilkd.key.pp.LogicPrinter;
import de.uka.ilkd.key.pp.ProgramPrinter;
import de.uka.ilkd.key.gui.*;
import de.uka.ilkd.key.gui.configuration.ProofSettings;
import de.uka.ilkd.key.java.ProgramElement;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.LogicVariable;
import de.uka.ilkd.key.logic.op.NameSV;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.parser.*;
import de.uka.ilkd.key.pp.AbbrevMap;
import de.uka.ilkd.key.proof.init.*;
import de.uka.ilkd.key.proof.mgt.ContractWithInvs;
import de.uka.ilkd.key.rule.*;
import de.uka.ilkd.key.speclang.SLEnvInput;
import de.uka.ilkd.key.util.Array;
import de.uka.ilkd.key.util.ExceptionHandlerException;
import de.uka.ilkd.key.util.KeYExceptionHandler;
import de.uka.ilkd.key.util.ProgressMonitor;


public class ProblemLoader implements Runnable {

    File file;
    private IMain main;    
    KeYMediator mediator;

    Proof proof = null;
    Iterator<Node> children = null;

    Node currNode = null;
    KeYExceptionHandler exceptionHandler = null;
    Goal currGoal = null;
    String currTacletName = null;
    int currFormula = 0;
    PosInTerm currPosInTerm = PosInTerm.TOP_LEVEL;
    ContractWithInvs currContract = null;
    Stack stack = new Stack();
    LinkedList loadedInsts = null;
    ImmutableList<IfFormulaInstantiation> ifFormulaList =
        ImmutableSLList.<IfFormulaInstantiation>nil();
    Constraint matchConstraint = null;


    /* Proofs with meta variables have a special issue.
       When loading, rules are applied in an order different to the original
       one, sometimes yielding shorter proofs. The goal we are looking
       at might already have been closed. Then we need to ignore the
       rest of the current branch in the proof script.
    */
    private int ignoreBranchRest;


    ProblemInitializer init;
    InitConfig iconfig;

    /** if set, uses the current problem instance instead of a new one */
    boolean keepProblem;

    /** the profile to be used */
    private Profile profile;
    
    private SwingWorker worker;
    private ProgressMonitor pm;
    private ProverTaskListener ptl;
    private String quantifierEliminator;

    private final static String PRETEND = "PRETEND QE YIELDS TRUE";
    private final static BuiltInRule PRETENDRULE = new BuiltInRule() {
                @Override
                public boolean isApplicable(Goal goal, PosInOccurrence pio, Constraint userConstraint) {
                    return true;
                }

                @Override
                public ImmutableList<Goal> apply(Goal goal, Services services, RuleApp ruleApp) {
                    return goal.split(0);
                }

                @Override
                public Name name() {
                    return new Name(PRETEND);
                }

                @Override
                public String displayName() {
                    return PRETEND;
                }
            };

    public ProblemLoader(File file, IMain main, Profile profile, 
            boolean keepProblem) {
        this.main = main;
        this.mediator  = main.mediator();        
        this.file = file;
        this.profile = profile;
        this.exceptionHandler = mediator.getExceptionHandler();
        this.keepProblem = keepProblem;

        addProgressMonitor(main.getProgressMonitor());
    }    
      
    public void addProgressMonitor(ProgressMonitor pm) {
        this.pm = pm;
    }

    public void addTaskListener(ProverTaskListener ptl) {
        this.ptl = ptl;
    }
    
    /** 
     * updates a possibly existing status line with the given information
     * @param status the String to be printed in the status line
     */
    public void setStatusLine(String status) {
        if (main != null) {
            main.setStatusLine(status);
        }
    }

    /**
     * updates a possibly existing status line with the given status information
     * and sets a maximum value for the progress bar
     * @param status the String with the status information
     * @param nr the int used a maximum value for a progress bar
     */
    public void setStatusLine(String status, int nr) {
        if (main != null) {
            main.setStatusLine(status, nr);
        }
    }
    
    /**
     * resets a possibly existing statusline to its standard text
     */
    public void setStandardStatusLine() {
        if (main != null) {
            main.setStandardStatusLine();
        }        
    }


    public void run() {
        /* Invoking start() on the SwingWorker causes a new Thread
         * to be created that will call construct(), and then
         * finished().  Note that finished() is called even if
         * the worker is interrupted because we catch the
         * InterruptedException in doWork().
         */
        worker = new SwingWorker() {
                private long time;
		public Object construct() {
                    time = System.currentTimeMillis();
                    Object res = doWork();
                    time = System.currentTimeMillis() - time;
		    return res;
		}
		public void finished() {
		    mediator.startInterface(true);
		    final String msg = (String) get();
		    if (ptl != null) {                        
                        final TaskFinishedInfo tfi = new DefaultTaskFinishedInfo(ProblemLoader.this, 
                                msg, proof, time, 
                                (proof != null ? proof.countNodes() : 0),                                
                                (proof != null ? proof.countBranches() -
                                        proof.openGoals().size() : 0));
                        ptl.taskFinished(tfi);
		    }
		}
        };
        // HACK: inform TimeStatistics (otherwise, it will attribute time to the wrong proof)
        TimeStatisticGenerator.INSTANCE.recordFor(null);
        mediator.stopInterface(true);
        if (ptl != null) ptl.taskStarted("Loading problem ...", 0);
        worker.start();
    }


    /**
     * @param file	the file or directory the user has chosen in the Open dialog
     * @return 		the corresponding input object for the selected file/directory
     * @throws FileNotFoundException 
     * @throws IllegalArgumentException if the user has selected a file with an unsupported extension
     *                          an exception is thrown to indicate this
     */
    protected EnvInput createEnvInput(File file) 
    throws FileNotFoundException {                
        
        final String filename = file.getName();
        
        if (filename.endsWith(".java")){ 
            // java file, probably enriched by specifications
            if(file.getParentFile() == null) {
                return new SLEnvInput(".");
            } else {
                return new SLEnvInput(file.getParentFile().getAbsolutePath());
            }            
        } else if (filename.endsWith(".key") || 
                filename.endsWith(".proof")) {
            // KeY problem specification or saved proof
            return new KeYUserProblemFile(filename, file, pm);
            
        } else if (file.isDirectory()){ 
            // directory containing java sources, probably enriched 
            // by specifications
            return new SLEnvInput(file.getPath());
        } else {
            if (filename.lastIndexOf('.') != -1) {
                throw new IllegalArgumentException
                ("Unsupported file extension \'"+
                        filename.substring(filename.lastIndexOf('.'))+
                        "\' of read-in file " + filename +
                        ". Allowed extensions are: .key, .proof, .java or "+
                "complete directories."); 
            } else {
                throw new FileNotFoundException("File or directory\n\t "+
                        filename + "\n not found.");
            }
        }                
    }

   private Object doWork() {
       String status = "";
       ProofOblInput po = null;
       try{
           try{
               if (!keepProblem) {
        	   EnvInput envInput = createEnvInput(file);
        	   init = new ProblemInitializer(main); 
        	   InitConfig initConfig = init.prepare(envInput);
        	   
        	   if(envInput instanceof ProofOblInput
                       && !(envInput instanceof KeYFile 
                            && ((KeYFile) envInput).chooseContract())) {
        	       po = (ProofOblInput) envInput;
        	   } else {
                       if(envInput instanceof KeYFile) {
                           initConfig.setOriginalKeYFileName(envInput.name());
                       }
        	       POBrowser poBrowser = POBrowser.showInstance(initConfig);        	       
        	       po = poBrowser.getAndClearPO();
        	       if(po == null) {
        		   return "Aborted.";
        	       }
        	   }
        	   init.startProver(initConfig, po);
               }
               proof = mediator.getSelectedProof();
               mediator.stopInterface(true); // first stop (above) is not enough
               // as there is no problem at that time
               setStatusLine("Loading proof");
               currNode = proof.root(); // initialize loader
               children = currNode.childrenIterator(); // --"--
               iconfig = proof.env().getInitConfig();
               try {
            	   proofParseTree = new ProofParseTree();
            	   currRule = proofParseTree.getBranch().first();
                   if (!keepProblem) {   
                       init.tryReadProof(this, po);
                   } else {
                       setStatusLine("Loading proof", (int)file.length());
                       CountingBufferedInputStream cinp =
                           new CountingBufferedInputStream(
                                   new FileInputStream(file),
                                   pm,
                                   (int)file.length()/100);
                       KeYLexer lexer = new KeYLexer(cinp,
                               proof.getServices().getExceptionHandler());
                       KeYParser parser = new KeYParser(ParserMode.PROBLEM, lexer, 
                               proof.getServices());
                       antlr.Token t;
                       do { t = lexer.getSelector().nextToken();
                       } while (t.getType() != KeYLexer.PROOF);
                       parser.proofBody(this);
                   }
                   
                   List<Branch> activeBranches = new ArrayList<Branch>();
                   
                   Branch b = proofParseTree.getBranch();
                   
                   b.setCurrentNode(currNode);
                   b.setChildren(children);
                   
                   activeBranches.addAll(b.poll().getSubBranches());
                   for(Branch bb: activeBranches) {
                	   bb.setCurrentNode(currNode);
                	   bb.setChildren(children);
                   }
                   
                   
                   while(activeBranches.size() > 0) {
                	   Branch branch = activeBranches.get(0);
                	   currNode = branch.getCurrentNode();
                	   children = branch.getChildren();
                	   
                	   if(branch.isEmpty()) {
                		   activeBranches.remove(branch);
                		   Collections.sort(activeBranches);
                		   continue;
                	   }
                	   
                	   Rule rule = branch.poll();
                	   
                	   for(Pair<Character, String> p: rule.getRuleInfos()) {
                		   beginExprImpl(p.id, p.str);
                	   }
                	   Collections.reverse(rule.getRuleInfos());
                	   for(Pair<Character, String> p: rule.getRuleInfos()) {
                		   endExprImpl(p.id, rule.getLineNumber());
                	   }
                	   
                	   branch.setCurrentNode(currNode);
                	   branch.setChildren(children);
                	   
                	   if(rule.getSubBranches().size() > 0) {
                		   assert branch.isEmpty();
                		   activeBranches.remove(branch);
                		   activeBranches.addAll(rule.getSubBranches());
                		   for(int i = 0; i < rule.getSubBranches().size(); i++) {
                			   Node next = children.next();
                			   rule.getSubBranches().get(i).setCurrentNode(next);
                			   rule.getSubBranches().get(i).setChildren(next.childrenIterator());
                		   }
                		   assert !children.hasNext();
                		   
                	   }
                	   
                	   if(branch.isEmpty()) {
                		   activeBranches.remove(branch);
                	   }
                	   
                	   Collections.sort(activeBranches);
                   }
               } finally {
                    if (constraints.size() > 0) {
                        Term left, right;
                        for (PairOfString p : constraints) {
                            left = parseTerm(p.left, proof);
                            right = parseTerm(p.right, proof);

                            if (left == null || right == null) {
                                continue;
                            }

                            if (!(left.sort().extendsTrans(right.sort()) || right
                                    .sort().extendsTrans(left.sort()))) {
                                continue;
                            }

                            if (!Constraint.BOTTOM.unify(left, right, null)
                                    .isSatisfiable()) {
                                continue;
                            }

                            proof.getUserConstraint().addEquality(left, right);
                        }
                    }
               }
	       setStandardStatusLine();
           
           // Inform the decproc classes that a new problem has been loaded
           // This is done here because all benchmarks resulting from one loaded problem should be
           // stored in the same directory
           //DecisionProcedureSmtAuflia.fireNewProblemLoaded( file, proof );
           
	   } catch (ExceptionHandlerException e) {
	       throw e;
	   } catch (Throwable thr) {
	       exceptionHandler.reportException(thr);
               status =  thr.getMessage();
	   }
       } catch (ExceptionHandlerException ex){
	       setStatusLine("Failed to load problem/proof");
	       status =  ex.toString();
       }
       finally {
           mediator.resetNrGoalsClosedByHeuristics();
           if (po != null && po instanceof KeYUserProblemFile){
               ((KeYUserProblemFile) po).close();
           }
       }
       return status;
   }



    public void loadPreferences(String preferences) {
        final ProofSettings proofSettings = ProofSettings.DEFAULT_SETTINGS;
        proofSettings.loadSettingsFromString(preferences);
    }

    private Vector<PairOfString> constraints = new Vector<PairOfString>();
    private ProofParseTree proofParseTree = new ProofParseTree();
    private Branch currBranch = proofParseTree.getBranch();
	private Stack<Branch> branchStack = new Stack<Branch>();
	private Rule currRule = proofParseTree.getBranch().first();
	private ArrayList<String> reduceVariables;

    public void beginExpr(char id, String s) {
    	switch (id) {
    	case 'b' :
    		branchStack.push(currBranch);
    		currBranch = new Branch();
    		currRule.addBranch(currBranch);
    		break;
    	case 'r':
    	case 'n':
    		currRule = new Rule(id, s);
    		currBranch.addRule(currRule);
    		break;
    	case 'x':
    		currRule.setId(Integer.parseInt(s));
    		break;
    	default:
    		currRule.addRuleInfo(id, s);
    	}	
    }
    
    // note: Expressions without parameters only emit the endExpr signal
    public void beginExprImpl(char id, String s) {
        //System.out.println("start "+id+"="+s);
        
        //start no new commands until the ignored branch closes
        //count sub-branches though
        if ((ignoreBranchRest > 0)&&(id!='b')) return; 
        switch (id) {
        case 'b' :
            stack.push(children);
            if (children.hasNext()) currNode = children.next();
            break;
	case 'r' : 
            if (currNode == null) currNode = children.next();
            // otherwise we already fetched the node at branch point
            currGoal      = proof.getGoal(currNode);
            // the goal may already have been closed due to the metavariable
            // issue described in the declaration of ignoreBranchRest
            if (currGoal==null) {
                ignoreBranchRest = stack.size();
                break;
            }
            mediator.getSelectionModel().setSelectedGoal(currGoal);
            currTacletName= s;
            // set default state
            currFormula   = 0;
            currPosInTerm = PosInTerm.TOP_LEVEL;
            loadedInsts   = null;
            ifFormulaList = ImmutableSLList.<IfFormulaInstantiation>nil();
            matchConstraint = Constraint.BOTTOM;
            break;

        case 'f' :
            currFormula   = Integer.parseInt(s);
            break;

        case 't' :
            currPosInTerm = PosInTerm.parseReverseString(s);
            break;

        case 'i' :
            if (loadedInsts == null) loadedInsts = new LinkedList();
            loadedInsts.add(s);
            break;
            
	case 'h' :
	    //             Debug.fail("Detected use of heuristics!");
	    break;
	case 'q' : // ifseqformula      
            Sequent seq = currGoal.sequent();
            ifFormulaList = ifFormulaList.append(
                    new IfFormulaInstSeq(seq, Integer.parseInt(s)));    
            break;
        case 'd' : // ifdirectformula      
            ifFormulaList = ifFormulaList.append(
                new IfFormulaInstDirect(
                    new ConstrainedFormula(parseTerm(s, proof))));
            break;
        case 'u' : //UserLog
            if(proof.userLog==null)
                proof.userLog = new Vector<String>();
            proof.userLog.add(s);
            break;
        case 'v' : //Version log
            if(proof.keyVersionLog==null)
                proof.keyVersionLog = new Vector<String>();
            proof.keyVersionLog.add(s);
            break;
        case 's' : //ProofSettings
            //System.out.println("---------------\n" + s + "------------\n");
            //necessary for downward compatibility of the proof format
            loadPreferences(s);
        break;
        case 'n' : //BuiltIn rules
            if (currNode == null) currNode = children.next();
            currGoal      = proof.getGoal(currNode);
            mediator.getSelectionModel().setSelectedGoal(currGoal);
            currTacletName = s;
            // set default state
            currFormula   = 0;
            currPosInTerm = PosInTerm.TOP_LEVEL;
            break;
        case 'c' : //contract
            currContract = new ContractWithInvs(s, proof.getServices());
            if(currContract == null) {
                throw new RuntimeException("Error loading proof: contract \"" + s + "\" not found.");
            }
            break;
        case 'o' : //userconstraint
            final int i = s.indexOf('=');

            if (i < 0) {
                break;
            }

            constraints.add(new PairOfString(s.substring(0, i),
                    s.substring(i + 1)));
            break;
        case 'm' : //matchconstraint
            final int index = s.indexOf('=');

            if (index < 0) {
                break;
            }

            final Term left = parseTerm(s.substring(0, index), proof);
            final Term right = parseTerm(s.substring(index + 1), proof);

            if (!(left.sort().extendsTrans(right.sort()) || right.sort()
                    .extendsTrans(left.sort()))) {
                break;
            }

            matchConstraint = matchConstraint.unify(left, right, null);
            break;
        case 'w' : //newnames
            final String[] newNames = s.split(",");
            ImmutableList<Name> l = ImmutableSLList.<Name>nil();
            for (String newName : newNames) {
                l = l.append(new Name(newName));
            }
            proof.getServices().getNameRecorder().setProposals(l);
            break;
        case 'e': //autoModeTime
            try {
                proof.addAutoModeTime(Long.parseLong(s));
            } catch (NumberFormatException e) {
                // ignore
            }
            break;
        case 'R':
        	reduceVariables = new ArrayList<String>();
        	for (String m: s.trim().split(",")) {
        		reduceVariables.add(m.trim());
        	}
        	break;
        case 'Q':
            quantifierEliminator = s;
            break;
        }
    }

    public void endExpr(char id, int linenr) {
    	switch(id) {
    	case 'b':
    		currBranch = branchStack.pop();
    		currRule = currBranch.last();
    		break;
    	case 'r':
    		currRule.setLineNumber(linenr);
    		break;
		case 'n':
			currRule.setLineNumber(linenr);
			break;
		case 'a':
			// we need to execute this later but the userinteraction
			// flag has no parameters therefore it it not pass beginExpr
			currRule.addRuleInfo('a', null);
			break;
    	}
    }

    public void endExprImpl(char id, int linenr) {
        //System.out.println("end "+id);
        //read no new commands until ignored branch closes
        if ((ignoreBranchRest > 0)&&(id!='b')) return; 
        switch (id) {
        case 'b' :
            children = (Iterator<Node>) stack.pop();
            // reached end of ignored branch?
            if (stack.size() < ignoreBranchRest) ignoreBranchRest = 0;
            break;
        case 'a' :
            if (currNode != null) {
                currNode.getNodeInfo().setInteractiveRuleApplication(true);
            }
            break;
        case 'r' :
            try{
               if(quantifierEliminator != null) {
                   // update the current quantifier eliminator
                   DLOptionBean.INSTANCE.setQuantifierEliminator(quantifierEliminator);
                   quantifierEliminator = null;
               }
               currGoal.apply(constructApp());
               children = currNode.childrenIterator();
               currNode = null;
            } catch(Exception e) {
                //throw new RuntimeException("Error loading proof. Line "+
                //    linenr+" rule: "+currTacletName,e);
				// ignore the exception
				System.err.println("Ignoring rule application in line " + linenr + " rule " + currTacletName);
				e.printStackTrace();
            }
            break;
        case 'n' :
            try {
                BuiltInRuleApp p_ruleApp = constructBuiltinApp();
                if(p_ruleApp.getQuantifierEliminator() != null) {
                    // update the current quantifier eliminator
                    DLOptionBean.INSTANCE.setQuantifierEliminator(p_ruleApp.getQuantifierEliminator());
                }
                currGoal.apply(p_ruleApp);
                children = currNode.childrenIterator();
                currNode = null;
            } catch (BuiltInConstructionException e) {
                //throw new RuntimeException("Error loading proof. Line "+
                //    linenr+" rule: "+currTacletName,e);
				// ignore the exception
				System.err.println("Ignoring rule application in line " + linenr + " rule " + currTacletName);
				e.printStackTrace();
            }
            break;
        }

    }

    /**
     * Constructs rule application for UpdateSimplification from
     * current parser information
     *
     * @return current rule application for updateSimplification
     */
    private BuiltInRuleApp constructBuiltinApp()
                               throws BuiltInConstructionException {
        BuiltInRuleApp ourApp = null;
        //PosInSequent posInSeq = null;
        PosInOccurrence pos = null;

        if (currFormula != 0) { // otherwise we have no pos
            pos = PosInOccurrence.findInSequent(currGoal.sequent(),
                                                currFormula,
                                                currPosInTerm);
        } else {
        }

        final Constraint userConstraint = mediator.getUserConstraint()
                        .getConstraint();
        
        if (currContract!=null) {
            ourApp = new UseOperationContractRuleApp(pos, 
                                                     userConstraint, 
                                                     currContract);
            currContract=null;
            return ourApp;
        }

        if(DLOptionBean.INSTANCE.isPretendWhileLoadingQE()
                && (currTacletName.equals(ReduceRule.INSTANCE.name().toString())
                || currTacletName.equals(PRETEND))) {

           if (reduceVariables != null) {
                ourApp = new ReduceRuleApp(PRETENDRULE, pos,
                    Constraint.BOTTOM, reduceVariables);
                reduceVariables = null;
               return ourApp;
            } else {
               return new BuiltInRuleApp(PRETENDRULE, pos, Constraint.BOTTOM);
           }
        } else if(!DLOptionBean.INSTANCE.isPretendWhileLoadingQE() && currTacletName.equals(PRETEND)) {
            currTacletName = ReduceRule.INSTANCE.name().toString();
        }

        final ImmutableSet<RuleApp> ruleApps =
            mediator.getBuiltInRuleApplications(currTacletName, pos);

        if (ruleApps.size() != 1) {
            if (ruleApps.size() < 1) {
                throw new BuiltInConstructionException
                (currTacletName +
                    " is missing. Most probably the binary "+
                    "for this built-in rule is not in your path or " +
                    "you do not have the permission to execute it.");
            } else {
                throw new BuiltInConstructionException
                (currTacletName + ": found " + ruleApps.size() +
                    " applications. Don't know what to do !\n" +
                    "@ " + pos);
            }
        }
        ourApp = (BuiltInRuleApp) ruleApps.iterator().next();
        if (ourApp.rule() instanceof BuiltInRule && reduceVariables != null) {
        	ourApp = new ReduceRuleApp((BuiltInRule) ourApp.rule(), pos,
        			Constraint.BOTTOM, reduceVariables);
        	reduceVariables = null;
        }
        if(quantifierEliminator != null) {
            ourApp.setQuantifierEliminator(quantifierEliminator);
            quantifierEliminator = null;
        }

        return ourApp;
    }

    private TacletApp constructApp() throws AppConstructionException {

        TacletApp ourApp = null;
        PosInOccurrence pos = null;

        Taclet t = iconfig.lookupActiveTaclet(new Name(currTacletName));
        if (t==null) {
            ourApp = currGoal.indexOfTaclets().lookup(currTacletName);
        } else {
            ourApp = NoPosTacletApp.createNoPosTacletApp(t);
        }

        if (ourApp == null) constructAppError("Did not succeed in constructing rule application because taclet lookup failed");

        if (matchConstraint != Constraint.BOTTOM) {
            ourApp = ourApp.setMatchConditions(new MatchConditions(ourApp
                    .instantiations(), matchConstraint, ourApp
                    .newMetavariables(), RenameTable.EMPTY_TABLE));
        }

        if (ourApp == null) constructAppError("Did not succeed in constructing rule application because constraint matching failed");

        Constraint userC = mediator.getUserConstraint().getConstraint();
        Services services = mediator.getServices();

        if (currFormula != 0) { // otherwise we have no pos
            pos = PosInOccurrence.findInSequent(currGoal.sequent(),
                                                currFormula,
                                                currPosInTerm);
//System.err.print("Want to apply "+currTacletName+" at "+currGoal);
             //this is copied from TermTacletAppIndex :-/

            Constraint c = pos.constrainedFormula().constraint();
            if ( pos.termBelowMetavariable() != null ) {
                c = c.unify(
                   pos.constrainedFormula().formula().subAt(pos.posInTerm()),
                   pos.termBelowMetavariable(), mediator.getServices());
                if (!c.isSatisfiable()) return null;
            }
            ourApp = ((NoPosTacletApp)ourApp).matchFind(pos, c, services, userC);
            ourApp = ourApp.setPosInOccurrence(pos);

            if (ourApp == null) constructAppError("Did not succeed in constructing rule application");
        }

        ourApp = constructInsts(ourApp, services);

        if (ourApp == null) constructAppError("Did not succeed in constructing rule application because instantiation failed");
        
        ourApp = ourApp.setIfFormulaInstantiations(ifFormulaList,
                                                   services, userC);

        if (ourApp == null) constructAppError("Did not succeed in constructing rule application because \\if instantiations failed");

        if (!ourApp.sufficientlyComplete()) {
            ourApp = ourApp.tryToInstantiate(currGoal, proof.getServices());
        }

        if (ourApp == null) constructAppError("Did not succeed in constructing rule application because completing the instantiation failed");

        return ourApp;
    }
    
    private void constructAppError(String problem) throws AppConstructionException {
      Services services = mediator.getServices();
      String currGoalPrint;
      try {
          final LogicPrinter lp = new LogicPrinter(new ProgramPrinter(null), 
                  Main.getInstance().mediator().getNotationInfo(),
                  services);
          lp.printSequent(currGoal.sequent());
          currGoalPrint = lp.toString();
      }
      catch (Exception ignore) {
          currGoalPrint = currGoal.toString();
      }
      String ifFormulaPrint = "";
      for (IfFormulaInstantiation ifFormula : ifFormulaList)
        try {
            final LogicPrinter lp = new LogicPrinter(new ProgramPrinter(null), 
                    Main.getInstance().mediator().getNotationInfo(),
                    services);
            lp.printTerm(ifFormula.getConstrainedFormula().formula());
            ifFormulaPrint += "assume at:     " + lp.toString();
        }
        catch (Exception ignore) {
            ifFormulaPrint += "assume at:     " + ifFormula.getConstrainedFormula().toString();
        }
      if (currFormula != 0) { // otherwise we have no pos
        PosInOccurrence pos = PosInOccurrence.findInSequent(currGoal.sequent(),
                                          currFormula,
                                          currPosInTerm);
        String currFormulaPrint;
        try {
            final LogicPrinter lp = new LogicPrinter(new ProgramPrinter(null), 
                    Main.getInstance().mediator().getNotationInfo(),
                    services);
            lp.printTerm(pos.constrainedFormula().formula());
            currFormulaPrint = lp.toString();
        }
        catch (Exception ignore) {
            currFormulaPrint = pos.constrainedFormula().formula().toString();
        }
        String currPosInTermPrint;
        try {
            final LogicPrinter lp = new LogicPrinter(new ProgramPrinter(null), 
                    Main.getInstance().mediator().getNotationInfo(),
                    services);
            lp.printTerm(pos.subTerm());
            currPosInTermPrint = lp.toString();
        }
        catch (Exception ignore) {
            currPosInTermPrint = pos.subTerm().toString();
        }
        throw new AppConstructionException(problem + " applying\ntaclet " + currTacletName + " to goal\n" + currGoalPrint + "at formula " + currFormula + ":  " + currFormulaPrint + "at " + currPosInTerm + ":  " + currPosInTermPrint + ifFormulaPrint);
      } else
        throw new AppConstructionException(problem + " applying\ntaclet " + currTacletName + " to goal\n" + currGoalPrint + "at formula " + currFormula + "at " + currPosInTerm + ifFormulaPrint);
    }



    /** 1st pass: only VariableSV */
    public static TacletApp parseSV1(TacletApp app, SchemaVariable sv,
                                     String value, Services services) {
        LogicVariable lv=new LogicVariable(new Name(value),
                                           app.getRealSort(sv, services));
        Term instance = TermFactory.DEFAULT.createVariableTerm(lv);
        return app.addCheckedInstantiation(sv, instance, services,true);
    }



    /** 2nd pass: all other SV */
    public static TacletApp parseSV2(TacletApp app, SchemaVariable sv,
                                     String value, Goal targetGoal) {        
        final Proof p = targetGoal.proof();
        final Services services = p.getServices();
        TacletApp result;
        if (sv.isVariableSV()) {
            // ignore -- already done
            result = app;
        } else if (sv.isProgramSV()) {
            try {
	    final ProgramElement pe = 
	        TacletInstantiationsTableModel.getProgramElement(
		    app, value, sv, services);
	    result = app.addCheckedInstantiation(sv, pe, services, true);
            } catch(IllegalStateException e) {
                // try to just parse is as a term
                Namespace varNS = p.getNamespaces().variables();
        	    Namespace progNS = targetGoal.proof().getNamespaces().programVariables();
        	    progNS.add(varNS);
        	    final Term t = parseTerm(value, p, varNS, 
	            targetGoal.getVariableNamespace(progNS));
        	    result = app.addCheckedInstantiation(sv, t, services, true);
            }
        } else if ( sv.isSkolemTermSV() ) {
	    result = app.createSkolemConstant ( value, sv, true, services );
        } else if (sv.isListSV()) {
            ImmutableSet<LocationDescriptor> s = parseLocationList(value, targetGoal);
            result = app.addInstantiation(sv, Array.reverse(s.toArray(new LocationDescriptor[s.size()])), true);
        } else {
            Namespace varNS = p.getNamespaces().variables();
	    varNS = app.extendVarNamespaceForSV(varNS, sv);
	    Namespace progNS = targetGoal.proof().getNamespaces().programVariables();
	    progNS.add(varNS);
//	    Term instance = parseTerm(value, p, varNS, 
//	            targetGoal.getVariableNamespace(varNS));
	    Term instance = parseTerm(value, p, varNS, 
	            targetGoal.getVariableNamespace(progNS));
	    result = app.addCheckedInstantiation(sv, instance, services, true); 
        }
        return result;
    }




    private TacletApp constructInsts(TacletApp app, Services services) {
        if (loadedInsts == null) return app;
        ImmutableSet<SchemaVariable> uninsts = app.uninstantiatedVars();

        // first pass: add variables
        Iterator it = loadedInsts.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            int eq = s.indexOf('=');
            String varname = s.substring(0, eq);
            String value = s.substring(eq+1, s.length());

            // reklov
            // START TEMPORARY DOWNWARD COMPATIBILITY

            if (varname.startsWith("_NAME")) {
                app = app.addInstantiation(de.uka.ilkd.key.rule.inst.
                        SVInstantiations.EMPTY_SVINSTANTIATIONS.add(
                        new NameSV(varname), new Name(value)));
                continue;
            }

            // END TEMPORARY DOWNWARD COMPATIBILITY

            SchemaVariable sv = lookupName(uninsts, varname);
            if (sv==null) {
//                throw new IllegalStateException(
//                    varname+" from \n"+loadedInsts+"\n is not in\n"+uninsts);
                System.err.println(varname+" from "+app.rule().name()+" is not in uninsts");
                continue;
            }

            if (sv.isVariableSV()) {
                app = parseSV1(app, sv, value, services);
            }
        }

        // second pass: add everything else
        uninsts = app.uninstantiatedVars();
        it = loadedInsts.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            int eq = s.indexOf('=');
            String varname = s.substring(0, eq);
            String value = s.substring(eq+1, s.length());
            SchemaVariable sv = lookupName(uninsts, varname);
            if (sv==null) continue;
            app = parseSV2(app, sv, value, currGoal);
        }

        return app;
    }


    public static Term parseTerm(String value, Proof proof,
            Namespace varNS, Namespace progVar_ns) {
        try {
            return TermParserFactory.createInstance().
                parse(new StringReader(value), null,
                      proof.getServices(),
                      varNS,
                      proof.getNamespaces().functions(),
                      proof.getNamespaces().sorts(),
                      progVar_ns,
                      new AbbrevMap());
        } catch(ParserException e) {
            throw new RuntimeException("Error while parsing value "+value+
                                       "\nVar namespace is: "+varNS+"\n", e);
        }
    }
    public static Term parseTerm(String value, Services services,
            Namespace varNS, Namespace progVar_ns) {
        try { 
            return TermParserFactory.createInstance().
                parse(new StringReader(value), null,
                      services,
                      varNS,
                      services.getNamespaces().functions(),
                      services.getNamespaces().sorts(),
                      progVar_ns,
                      new AbbrevMap());
        } catch(ParserException e) {
            throw new RuntimeException("Error while parsing value "+value+
                                       "\nVar namespace is: "+varNS+"\n", e);
        }
    }

    public static ImmutableSet<LocationDescriptor> parseLocationList(String value, Goal targetGoal) {
        ImmutableSet<LocationDescriptor> result = null;
        Proof p = targetGoal.proof();
        Namespace varNS = p.getNamespaces().variables();
        NamespaceSet nss = new NamespaceSet(
            varNS,
            p.getNamespaces().functions(),
            p.getNamespaces().sorts(),
            new Namespace(),
            new Namespace(),
            targetGoal.getVariableNamespace(varNS));
        Services services = p.getServices();
        try{
            result = (new KeYParser(ParserMode.TERM,new KeYLexer(new StringReader(value),
                                             services.getExceptionHandler()),
                                null, TermFactory.DEFAULT, null, services,
                                nss, new AbbrevMap())).
                location_list();
        } catch (antlr.RecognitionException re) {
            throw new RuntimeException("Cannot parse location list "+value, re);
        } catch (antlr.TokenStreamException tse) {
            throw new RuntimeException("Cannot parse location list "+value, tse);
        }
        return result;
    }



    public static Term parseTerm(String value, Proof proof) {
        return parseTerm(value, proof, proof.getNamespaces().variables(),
                proof.getNamespaces().programVariables());
    }


    private SchemaVariable lookupName(ImmutableSet<SchemaVariable> set, String name) {
        for (SchemaVariable aSet : set) {
            SchemaVariable v = aSet;
            if (v.name().toString().equals(name)) return v;
        }
        return null; // handle this better!
    }

    private class AppConstructionException extends Exception {

        AppConstructionException(String s) {
            super(s);
        }

        AppConstructionException(Throwable t) {
            super(t);
        }

        AppConstructionException(String s, Throwable t) {
            super(s, t);
        }

    }

    private class BuiltInConstructionException extends Exception {

        BuiltInConstructionException(String s) {
            super(s);
    }
    }

    public KeYExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    private static class PairOfString {
        public String left;
        public String right;

        public PairOfString ( String p_left, String p_right ) {
            left  = p_left;
            right = p_right;
        }

    }

}
