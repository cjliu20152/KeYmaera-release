/* Generated by Together */

package de.uka.ilkd.key.dl.rules.metaconstruct;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.AbstractMetaOperator;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

public abstract class AbstractDLMetaOperator extends AbstractMetaOperator {
	
	/**
	 * @param name
	 * @param arity
	 */
	public AbstractDLMetaOperator(Name name, int arity) {
		super(name, arity);
	}
	/* (non-Javadoc)
	 * @see de.uka.ilkd.key.logic.op.AbstractMetaOperator#calculate(de.uka.ilkd.key.logic.Term, de.uka.ilkd.key.rule.inst.SVInstantiations, de.uka.ilkd.key.java.Services)
	 */
	public abstract Term calculate(Term term, SVInstantiations svInst, Services services);
}