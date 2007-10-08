/**
 * 
 */
package de.uka.ilkd.key.dl;

import de.uka.ilkd.key.dl.rules.DLUpdateSimplifier;
import de.uka.ilkd.key.dl.rules.DebugRule;
import de.uka.ilkd.key.dl.rules.EliminateExistentialQuantifierRule;
import de.uka.ilkd.key.dl.rules.FindInstanceRule;
import de.uka.ilkd.key.dl.rules.ReduceRule;
import de.uka.ilkd.key.dl.strategy.DLStrategy;
import de.uka.ilkd.key.dl.strategy.DLStrategy.Factory;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.logic.op.DLWarySubstOp;
import de.uka.ilkd.key.logic.op.Op;
import de.uka.ilkd.key.parser.dl.ProgramBlockProvider;
import de.uka.ilkd.key.proof.init.AbstractProfile;
import de.uka.ilkd.key.rule.ListOfBuiltInRule;
import de.uka.ilkd.key.rule.UpdateSimplificationRule;
import de.uka.ilkd.key.rule.UpdateSimplifier;
import de.uka.ilkd.key.strategy.FOLStrategy;
import de.uka.ilkd.key.strategy.SetOfStrategyFactory;
import de.uka.ilkd.key.strategy.StrategyFactory;
import de.uka.ilkd.key.util.Debug;

/**
 * This is the profile used to initialise proofs of dL formulas.s
 * 
 * @author jdq
 * @since 08.01.2007
 * 
 */
public class DLProfile extends AbstractProfile {

    /**
     * 
     */
    private static final Factory DEFAULT = new DLStrategy.Factory();

    private static final String DLRULES = "standardRules-dL.key";

    /**
     * @param standardRuleFilename
     */
    public DLProfile() {
        super(DLRULES);
        Op.SUBST = new DLWarySubstOp(new Name("subst"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.AbstractProfile#supportedStrategies()
     */
    @Override
    protected SetOfStrategyFactory getStrategyFactories() {
        SetOfStrategyFactory set = super.getStrategyFactories();
        set = set.add(DEFAULT);
        set = set.add(new FOLStrategy.Factory());
        return set;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.Profile#getDefaultStrategyFactory()
     */
    public StrategyFactory getDefaultStrategyFactory() {
        return DEFAULT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.Profile#name()
     */
    public String name() {
        return "dLProfile";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.Profile#getProgramBlockProvider()
     */
    public ProgramBlockProvider getProgramBlockProvider() {
        return new ProgramBlockProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.Profile#getProgramBlockProvider(de.uka.ilkd.key.java.Services,de.uka.ilkd.key.logic.NamespaceSet)
     */
    public ProgramBlockProvider getProgramBlockProvider(Services services,
            NamespaceSet nss) {
        return new ProgramBlockProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.AbstractProfile#initBuiltInRules()
     */
    @Override
    protected ListOfBuiltInRule initBuiltInRules() {
        ListOfBuiltInRule rule = super.initBuiltInRules().prepend(
                ReduceRule.INSTANCE).prepend(getUpdateSimplificationRule())
                .prepend(FindInstanceRule.INSTANCE).prepend(EliminateExistentialQuantifierRule.INSTANCE);
        if (Debug.ENABLE_DEBUG) {
            rule = rule.prepend(DebugRule.INSTANCE);
        }
        return rule;
    }

    protected UpdateSimplificationRule getUpdateSimplificationRule() {
        return UpdateSimplificationRule.INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.proof.init.AbstractProfile#getDefaultUpdateSimplifier()
     */
    @Override
    public UpdateSimplifier getDefaultUpdateSimplifier() {
        return new DLUpdateSimplifier();
    }

}