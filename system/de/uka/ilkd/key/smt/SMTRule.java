package de.uka.ilkd.key.smt;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.ConstrainedFormula;
import de.uka.ilkd.key.logic.Constraint;
import de.uka.ilkd.key.logic.IteratorOfConstrainedFormula;
import de.uka.ilkd.key.logic.MVCollector;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.PIOPathIterator;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.Semisequent;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.IteratorOfMetavariable;
import de.uka.ilkd.key.logic.op.Modality;
import de.uka.ilkd.key.logic.op.SetAsListOfMetavariable;
import de.uka.ilkd.key.logic.op.SetOfMetavariable;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.proof.ListOfGoal;
import de.uka.ilkd.key.proof.decproc.AbstractDecisionProcedure;
import de.uka.ilkd.key.proof.decproc.ConstraintSet;
import de.uka.ilkd.key.proof.decproc.DecisionProcedureYices;
import de.uka.ilkd.key.proof.decproc.JavaDecisionProcedureTranslationFactory;
import de.uka.ilkd.key.proof.decproc.SimplifyException;
import de.uka.ilkd.key.proof.decproc.SimplifyTranslation;
import de.uka.ilkd.key.rule.BuiltInRule;
import de.uka.ilkd.key.rule.BuiltInRuleApp;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.YicesIntegerRule;

public class SMTRule implements BuiltInRule {

        /**
         * This rule's name.
         */
        public String displayName() {
                return "SMTRule";
        }
        
        /**
         * This rule's name as Name object.
         */
        public Name name() {
                return new Name(this.displayName());
        }
        
        
        public boolean isApplicable(Goal goal, PosInOccurrence pio, Constraint userConstraint) {
                boolean hasModality = false;
                
                IteratorOfConstrainedFormula ante = goal.sequent().antecedent().iterator();
                IteratorOfConstrainedFormula succ = goal.sequent().succedent().iterator();
                
                while (ante.hasNext()) {
                        ConstrainedFormula currentForm = ante.next();
                        Term t = currentForm.formula();
                        ModalityChecker v = new ModalityChecker();
                        t.execPreOrder(v);
                        hasModality = hasModality || v.hasModality();
                }
                
                while (succ.hasNext()) {
                        ConstrainedFormula currentForm = succ.next();
                        Term t = currentForm.formula();
                        ModalityChecker v = new ModalityChecker();
                        t.execPreOrder(v);
                        hasModality = hasModality || v.hasModality();
                }
                
                /*
                while (!modalityFound && pioiter.hasNext()) {
                        Term t = pioiter.getSubTerm();
                        ModalityChecker v = new ModalityChecker();
                        t.execPreOrder(v);
                        modalityFound = modalityFound && v.hasModality();
                        
                        pioiter.next();
                }
                */
                //TODO remove dummy-return
                //return !modalityFound;
                return true;
        }
        
        public ListOfGoal apply(Goal goal, Services services, RuleApp ruleApp) {
                
                //collect all Metavariable appearing in the goal
                //                MVCollector mvc = new MVCollector();
                //                IteratorOfConstrainedFormula ante = goal.sequent().antecedent().iterator();
                //IteratorOfConstrainedFormula succ = goal.sequent().succedent().iterator();
                
//                while (ante.hasNext()) {
//                        ConstrainedFormula currentForm = ante.next();
//                        Term t = currentForm.formula();
//                        t.execPostOrder(mvc);
                //                 }
                //                
                //while (succ.hasNext()) {
                //ConstrainedFormula currentForm = succ.next();
                //      Term t = currentForm.formula();
                //      t.execPreOrder(mvc);
                //}
                
                //                IteratorOfMetavariable iom = mvc.mv();
              //  SetAsListOfMetavariable setofmv = new SetAsListOfMetavariable();
                try {
                        SMTTranslator trans = new SMTTranslator(goal.sequent(), new ConstraintSet(goal, null), SetAsListOfMetavariable.EMPTY_SET, services);
                        StringBuffer s = trans.translate(goal.sequent());
                        System.out.println("Final Formular: " + s);
                } catch (SimplifyException e) {
                        System.out.println("!!!    Simplify Exception thrown");
                }
                return null;
        }

}
