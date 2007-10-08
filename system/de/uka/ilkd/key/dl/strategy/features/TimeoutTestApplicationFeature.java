/**
 * 
 */
package de.uka.ilkd.key.dl.strategy.features;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.WeakHashMap;

import de.uka.ilkd.key.dl.arithmetics.MathSolverManager;
import de.uka.ilkd.key.dl.options.DLOptionBean;
import de.uka.ilkd.key.dl.rules.TestableBuiltInRule;
import de.uka.ilkd.key.gui.Main;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.proof.Node;
import de.uka.ilkd.key.rule.Rule;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.strategy.LongRuleAppCost;
import de.uka.ilkd.key.strategy.RuleAppCost;
import de.uka.ilkd.key.strategy.TopRuleAppCost;
import de.uka.ilkd.key.strategy.feature.Feature;

/**
 * @author jdq
 * 
 */
public class TimeoutTestApplicationFeature implements Feature {

    private Map<Node, Long> branchingNodesAlreadyTested = new WeakHashMap<Node, Long>();
    private Map<Node, RuleAppCost> resultCache = new WeakHashMap<Node, RuleAppCost>();

    public static final TimeoutTestApplicationFeature INSTANCE = new TimeoutTestApplicationFeature();

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.strategy.feature.Feature#compute(de.uka.ilkd.key.rule.RuleApp,
     *      de.uka.ilkd.key.logic.PosInOccurrence, de.uka.ilkd.key.proof.Goal)
     */
    public RuleAppCost compute(RuleApp app, PosInOccurrence pos, Goal goal) {
        Node firstNodeAfterBranch = getFirstNodeAfterBranch(goal.node());
        if (branchingNodesAlreadyTested.containsKey(firstNodeAfterBranch)) {
            if(resultCache.containsKey(firstNodeAfterBranch)) {
                return resultCache.get(firstNodeAfterBranch);
            }
            return TopRuleAppCost.INSTANCE;
        } else {
            Long timeout = getLastTimeout(firstNodeAfterBranch);
            if (timeout == null) {
                timeout = DLOptionBean.INSTANCE.getInitialTimeout();
            } else {
                final int a = DLOptionBean.INSTANCE
                        .getQuadraticTimeoutIncreaseFactor();
                final int b = DLOptionBean.INSTANCE
                        .getLinearTimeoutIncreaseFactor();
                final int c = DLOptionBean.INSTANCE
                        .getConstantTimeoutIncreaseFactor();
                timeout = a * timeout * timeout + b * timeout + c;
            }
            branchingNodesAlreadyTested.put(firstNodeAfterBranch, timeout);

            Rule rule = app.rule();
            if (rule instanceof TestableBuiltInRule) {
                TestableBuiltInRule tbr = (TestableBuiltInRule) rule;
                TestThread testThread = new TestThread(goal, tbr, app);
                System.out.println("Trying to execute for: " + timeout + " ms");// XXX
                testThread.start();
                try {
                    testThread.join(timeout);
                    if (testThread.getResult() == TestResult.SUCCESS) {
                        System.out.println("finished returning zero cost");// XXX

                        // only accept formulas that are really better than the
                        // input formula, i.e. contain less variables
                        Term inputFormula = tbr.getInputFormula();
                        Term resultFormula = tbr.getResultFormula();
                        if (inputFormula != null && resultFormula != null) {
                            int variableNumber = FOVariableNumberCollector
                                    .getVariableNumber(inputFormula);
                            int variableNumber2 = FOVariableNumberCollector
                                    .getVariableNumber(resultFormula);
                            int number = variableNumber - variableNumber2;
                            if (number > 0
                                    || resultFormula == TermBuilder.DF.tt()) {
                                // dont put this in the result cache
                                return LongRuleAppCost.ZERO_COST;
                            }
                        }
                        resultCache.put(firstNodeAfterBranch, TopRuleAppCost.INSTANCE);
                        return TopRuleAppCost.INSTANCE;
                    }
                } catch (InterruptedException e) {
                    try {
                        MathSolverManager.getCurrentQuantifierEliminator()
                                .abortCalculation();
                    } catch (RemoteException f) {
                        testThread.interrupt();

                    }
                    System.out.println("Interrupting after " + timeout + " ms");// XXX
                }
                if (testThread.isAlive()) {
                    try {
                        MathSolverManager.getCurrentQuantifierEliminator()
                                .abortCalculation();
                    } catch (RemoteException f) {
                        testThread.interrupt();

                    }
                    System.out.println("Interrupting after " + timeout + " ms");// XXX
                }
            }

            System.out.println("returning max cost for: " + app.rule());// XXX
            resultCache.put(firstNodeAfterBranch, LongRuleAppCost.create(1));
            return LongRuleAppCost.create(1);
        }
    }

    /**
     * @param node
     * @return
     */
    private Long getLastTimeout(Node node) {
        Long result = null;
        if (node != null) {
            result = branchingNodesAlreadyTested.get(node);
            if (result == null) {
                result = getLastTimeout(node.parent());
            }
        }
        return result;
    }

    /**
     * @return
     */
    public static Node getFirstNodeAfterBranch(Node node) {
        if (node.root() || node.parent().root() || node.parent().childrenCount() > 1) {
            return node;
        }
        return getFirstNodeAfterBranch(node.parent());
    }

    private static enum TestResult {
        UNKNOWN, SUCCESS, FAILURE;
    }

    private static class TestThread extends Thread {

        private Goal goal;

        private TestableBuiltInRule rule;

        private RuleApp app;

        private TestResult result;

        public TestThread(Goal goal, TestableBuiltInRule rule, RuleApp app) {
            this.goal = goal;
            this.rule = rule;
            this.app = app;
            this.result = TestResult.UNKNOWN;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            if (rule.test(goal, Main.getInstance().mediator().getServices(),
                    app)) {
                result = TestResult.SUCCESS;
            } else {
                result = TestResult.FAILURE;
            }
        }

        /**
         * @return the result
         */
        public TestResult getResult() {
            return result;
        }
    }

}