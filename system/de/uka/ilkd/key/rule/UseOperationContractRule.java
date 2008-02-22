//This file is part of KeY - Integrated Deductive Software Design
//Copyright (C) 2001-2005 Universitaet Karlsruhe, Germany
//                      Universitaet Koblenz-Landau, Germany
//                      Chalmers University of Technology, Sweden
//
//The KeY system is protected by the GNU General Public License. 
//See LICENSE.TXT for details.
//
//

package de.uka.ilkd.key.rule;

import java.util.LinkedHashMap;
import java.util.Map;

import de.uka.ilkd.key.gui.ContractConfigurator;
import de.uka.ilkd.key.gui.Main;

import de.uka.ilkd.key.java.*;
import de.uka.ilkd.key.java.statement.LabeledStatement;
import de.uka.ilkd.key.java.statement.MethodBodyStatement;
import de.uka.ilkd.key.java.statement.Throw;
import de.uka.ilkd.key.java.visitor.ProgramContextAdder;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.proof.*;
import de.uka.ilkd.key.proof.mgt.ComplexRuleJustificationBySpec;
import de.uka.ilkd.key.proof.mgt.ContractWithInvs;
import de.uka.ilkd.key.proof.mgt.RuleJustificationBySpec;
import de.uka.ilkd.key.rule.inst.ContextStatementBlockInstantiation;
import de.uka.ilkd.key.rule.updatesimplifier.Update;
import de.uka.ilkd.key.speclang.FormulaWithAxioms;
import de.uka.ilkd.key.speclang.IteratorOfClassInvariant;
import de.uka.ilkd.key.speclang.IteratorOfOperationContract;
import de.uka.ilkd.key.speclang.OperationContract;
import de.uka.ilkd.key.speclang.SetAsListOfClassInvariant;
import de.uka.ilkd.key.speclang.SetOfOperationContract;
import de.uka.ilkd.key.speclang.SignatureVariablesFactory;


/**
 * Implements the rule which inserts operation contracts for a method body
 * statement.
 */
public class UseOperationContractRule implements BuiltInRule {

    private static final Name NAME = new Name("Use Operation Contract");
    private static final TermBuilder TB = TermBuilder.DF;
    private static final SignatureVariablesFactory SVF 
        = SignatureVariablesFactory.INSTANCE;
    private static final AtPreFactory APF = AtPreFactory.INSTANCE;
  
    public static final UseOperationContractRule INSTANCE 
                                            = new UseOperationContractRule();
    
    

    //-------------------------------------------------------------------------
    //constructors
    //------------------------------------------------------------------------- 
    
    private UseOperationContractRule() {
    }
    
    
    
    //-------------------------------------------------------------------------
    //internal methods
    //-------------------------------------------------------------------------
    
    /**
     * Returns a new PosInOccurrence which points to the same position as the 
     * passed one, except below updates.
     */
    private PosInOccurrence goBelowUpdates(PosInOccurrence pio) {
        if(pio != null && pio.subTerm().op() instanceof IUpdateOperator) {
            int targetPos = ((IUpdateOperator)pio.subTerm().op()).targetPos();
            return goBelowUpdates(pio.down(targetPos));
        } else {
            return pio;
        }
    }
    
    
    /**
     * Returns the active statement of the passed a java block.
     */
    private SourceElement getActiveStatement(JavaBlock jb) {
        assert jb.program() != null;
        
        SourceElement result = jb.program().getFirstElement();
        while(result instanceof ProgramPrefix
              && !(result instanceof StatementBlock 
                   && ((StatementBlock)result).isEmpty())) {
            if(result instanceof LabeledStatement) {
                result = ((LabeledStatement)result).getChildAt(1);
            } else {
                result = result.getFirstElement();
            }
        }
        return result;
    }
    
    
    /**
     * Returns all meta variables occurring in the passed term.
     */
    private SetOfMetavariable getAllMetavariables(Term term) {
        SetOfMetavariable result = SetAsListOfMetavariable.EMPTY_SET;            
        
        if(term.op() instanceof Metavariable) {
            result = result.add((Metavariable) term.op());
        }
            
        for(int i = 0; i < term.arity(); i++) {
            result = result.union(getAllMetavariables(term.sub(i)));
        }

        return result;
    }
    
    
    /**
     * Returns the operation contracts which are applicable for the passed 
     * operation and the passed modality (at the passed PosInOccurrence).
     */
    private SetOfOperationContract getApplicableContracts(Services services, 
                                                          ProgramMethod pm, 
                                                          Modality modality,
                                                          PosInOccurrence pio) {
        SetOfOperationContract result 
                = services.getSpecificationRepository()
                          .getOperationContracts(pm, modality);
        
        //in box modalities, diamond contracts may be applied as well
        if(modality == Op.BOX) {
            result = result.union(services.getSpecificationRepository()
                                          .getOperationContracts(pm, Op.DIA));
        }
        
        //prevent application of contracts with "everything" modifier sets 
        //if metavariables are involved (hackish, see Bug 810)
        if(getAllMetavariables(pio.topLevel().subTerm()).size() > 0) {
            ProgramVariable dummySelfVar 
                = SVF.createSelfVar(services, pm, true);
            ListOfParsableVariable dummyParamVars 
                = SVF.createParamVars(services, pm, true);
            IteratorOfOperationContract it = result.iterator();
            while(it.hasNext()) {
                OperationContract contract = it.next();
                if(contract.getModifies(dummySelfVar, dummyParamVars, services)
                           .contains(EverythingLocationDescriptor.INSTANCE)) {
                    result = result.remove(contract);
                }
            }
        }
        
        return result;
    }
    
    
    /**
     * Chooses a contract to be applied together with invariants to be assumed 
     * and ensured. This is done either automatically or by asking the user.
     */
    private ContractWithInvs configureContract(Services services, 
                                               ProgramMethod pm,
                                               Modality modality,
                                               PosInOccurrence pio) {
        if(Main.getInstance().mediator().autoMode()) {
            SetOfOperationContract contracts
                = getApplicableContracts(services, pm, modality, pio);
            //TODO: Apply *all* contracts here instead of a random one
            return new ContractWithInvs(contracts.iterator().next(), 
                                        SetAsListOfClassInvariant.EMPTY_SET, 
                                        SetAsListOfClassInvariant.EMPTY_SET);
        } else {
            ContractConfigurator cc 
                    = new ContractConfigurator(Main.getInstance(),
                                               services,
                                               pm,
                                               modality,
                                               true,
                                               true,
                                               true);
            if(cc.wasSuccessful()) {
                return cc.getContractWithInvs();
            } else {
                return null;
            }
        }
    }
   
    
    /**
     * Replaces the term at the passed PosInOccurrence with the passed 
     * replacement in the passed goal.
     */
    private void replaceInGoal(Term replacement, Goal goal, PosInOccurrence pio) {
        Map replaceMap = new LinkedHashMap();
        replaceMap.put(pio.subTerm(), replacement);
        OpReplacer or = new OpReplacer(replaceMap);
        
        ConstrainedFormula cf = pio.constrainedFormula();
        ConstrainedFormula newCf 
            = new ConstrainedFormula(or.replace(cf.formula()), 
                                     cf.constraint());
        goal.changeFormula(newCf, pio);
    }
    
    
    private PosInProgram getPosInProgram(JavaBlock jb) {
        ProgramElement pe = jb.program();        
   
        PosInProgram result = PosInProgram.TOP;
        
        if (pe instanceof ProgramPrefix) {
            ProgramPrefix curPrefix = (ProgramPrefix)pe;
       
            final ArrayOfProgramPrefix prefix = curPrefix.getPrefixElements();
            final int length = prefix.size();
                
            // fail fast check      
            curPrefix = prefix.getProgramPrefix(length-1);// length -1 >= 0 as prefix array 
                                                          //contains curPrefix as first element

            pe = curPrefix.getFirstActiveChildPos().getProgram(curPrefix);

            assert pe instanceof MethodBodyStatement;
        
            int i = length - 1;
            do {
                result = curPrefix.getFirstActiveChildPos().append(result);
                i--;
                if (i >= 0) {
                    curPrefix = prefix.getProgramPrefix(i);
                }
            } while (i >= 0);       

        } else {
            assert pe instanceof MethodBodyStatement;
        }
        return result;
    }
    
    
    private StatementBlock replaceStatement(JavaBlock jb, 
                                            StatementBlock replacement) {
        PosInProgram pos = getPosInProgram(jb);
        int lastPos = pos.last();
        ContextStatementBlockInstantiation csbi = 
            new ContextStatementBlockInstantiation(pos, 
                                                   pos.up().down(lastPos+1), 
                                                   null, 
                                                   jb.program());
        final NonTerminalProgramElement result = 
            ProgramContextAdder.INSTANCE.start(
                        (JavaNonTerminalProgramElement)jb.program(), 
                        replacement, 
                        csbi);
        return (StatementBlock) result;
    }

    

    //-------------------------------------------------------------------------
    //public interface
    //------------------------------------------------------------------------- 
    
    public boolean isApplicable(Goal goal, 
                                PosInOccurrence pio, 
                                Constraint userConstraint) {
        Services services = goal.node().proof().getServices();
        
        //pio must be a modality term in succedent
        pio = goBelowUpdates(pio);
        if(pio == null 
           || pio.isInAntec() 
           || !(pio.subTerm().op() instanceof Modality)
           || pio.subTerm().javaBlock().program() == null) {
            return false;
        }
        
        //active statement must be method body statement (TODO: constructor calls, see bug 702)
        Modality modality = (Modality) pio.subTerm().op();
        SourceElement activeStatement 
                = getActiveStatement(pio.subTerm().javaBlock());
        if(!(activeStatement instanceof MethodBodyStatement)) {
            return false;
        }
 
        //there must be applicable contracts for the operation
        MethodBodyStatement mbs = (MethodBodyStatement) activeStatement;
        ProgramMethod pm = mbs.getProgramMethod(services);
        SetOfOperationContract contracts 
                = getApplicableContracts(services, pm, modality, pio);
        if(contracts.size() == 0) {
            return false;
        }
        
        //applying a contract here must not create circular dependencies 
        //between proofs
        if(!goal.proof().mgt().contractApplicableFor(pm, goal)) {
            return false;
        }

        return true;
    }

    
    public ListOfGoal apply(Goal goal, Services services, RuleApp ruleApp) {
        //collect information about sequent
        PosInOccurrence pio = goBelowUpdates(ruleApp.posInOccurrence());
        Modality modality = (Modality) pio.subTerm().op();
        JavaBlock jb = pio.subTerm().javaBlock();
        SourceElement activeStatement = getActiveStatement(jb);
        MethodBodyStatement mbs = (MethodBodyStatement) activeStatement;
        ProgramMethod pm = mbs.getProgramMethod(services);
        Term actualSelf 
            = (mbs.getDesignatedContext() instanceof Expression
               ? services.getTypeConverter()
                         .convertToLogicElement(mbs.getDesignatedContext())
               : null);
        ListOfTerm actualParams = SLListOfTerm.EMPTY_LIST;
        ArrayOfExpression args = mbs.getArguments();
        for(int i = 0; i < args.size(); i++) {
            actualParams = actualParams.append(
                    services.getTypeConverter()
                            .convertToLogicElement(args.getProgramElement(i)));
        }
        ProgramVariable actualResult 
            = (ProgramVariable) mbs.getResultVariable();
        
        //configure contract and assumed / ensured invariants
        ContractWithInvs cwi;
        if(ruleApp instanceof UseOperationContractRuleApp) {
            //the contract and invariants are already fixed 
            //(probably because we're in the process of reading in a 
            //proof from a file)
            cwi = ((UseOperationContractRuleApp) ruleApp).getInstantiation();            
        } else { 
            cwi = configureContract(services, pm, modality, pio);
        }
        if(cwi == null) {
            return null;
        }
        assert cwi.contract.getProgramMethod().equals(pm);

        //create variables for self, parameters, result, exception, and a map 
        //for atPre-functions 
        ProgramVariable selfVar          
            = SVF.createSelfVar(services, pm, true);
        ListOfParsableVariable paramVars 
            = SVF.createParamVars(services, pm, true);
        ProgramVariable resultVar 
            = SVF.createResultVar(services, pm, true);
        ProgramVariable excVar 
            = SVF.createExcVar(services, pm, true);
        Map atPreFunctions               
            = new LinkedHashMap();
        
        //translate the contract and the invariants
        FormulaWithAxioms pre = cwi.contract.getPre(selfVar, 
                                                    paramVars, 
                                                    services);
        FormulaWithAxioms post = cwi.contract.getPost(selfVar, 
                                                      paramVars, 
                                                      resultVar, 
                                                      excVar, 
                                                      atPreFunctions,
                                                      services);
        SetOfLocationDescriptor modifies = cwi.contract.getModifies(selfVar, 
                                                                    paramVars, 
                                                                    services);           
        IteratorOfClassInvariant it = cwi.assumedInvs.iterator();
        while(it.hasNext()) {
            pre = pre.conjoin(it.next().getClosedInv(services));
        }
        it = cwi.ensuredInvs.iterator();
        while(it.hasNext()) {
            post = post.conjoin(it.next().getClosedInv(services));
        }
        
        //split goal into three branches
        ListOfGoal result = goal.split(3);
        Goal preGoal = result.tail().tail().head();
        preGoal.setBranchLabel("Pre");
        Goal postGoal = result.tail().head();
        postGoal.setBranchLabel("Post");
        Goal excPostGoal = result.head();
        excPostGoal.setBranchLabel("Exceptional Post");
        
        //prepare common stuff for the three branches
        UpdateFactory uf = new UpdateFactory(services, goal.simplifier());
        AnonymisingUpdateFactory auf = new AnonymisingUpdateFactory(uf);
        SetOfMetavariable mvs = getAllMetavariables(pio.topLevel().subTerm());
        Term[] mvTerms = new Term[mvs.size()];
        IteratorOfMetavariable it2 = mvs.iterator();
        for(int i = 0; i < mvTerms.length; i++) {
            mvTerms[i] = TermBuilder.DF.func(it2.next());
        }            
        Update anonUpdate = auf.createAnonymisingUpdate(modifies, 
                                                        mvTerms, 
                                                        services);
        Update atPreUpdate = APF.createAtPreDefinitions(atPreFunctions, 
                                                        services);
        Update selfParamsUpdate = (selfVar == null
                                   ? uf.skip()
                                   : uf.elementaryUpdate(TB.var(selfVar), 
                                                         actualSelf));
        IteratorOfParsableVariable paramsIt = paramVars.iterator();
        IteratorOfTerm actualParamsIt = actualParams.iterator();
        while(paramsIt.hasNext()) {
            assert actualParamsIt.hasNext();
            selfParamsUpdate 
                = uf.parallel(selfParamsUpdate, 
                              uf.elementaryUpdate(TB.var(paramsIt.next()), 
                                                  actualParamsIt.next()));
        }
        Term excNullTerm = TB.equals(TB.var(excVar), TB.NULL(services));
       
        //create "Pre" branch
        Term preTerm = uf.apply(selfParamsUpdate, 
                                TB.imp(pre.getAxiomsAsFormula(), 
                                       pre.getFormula()));
        replaceInGoal(preTerm, preGoal, pio);
        
        //create "Post" branch
        StatementBlock postSB = replaceStatement(jb, new StatementBlock());
        Term postTermWithoutUpdate 
            = TB.imp(TB.and(new Term[]{excNullTerm,
                                       post.getAxiomsAsFormula(),
                                       post.getFormula()}),
                     TB.prog(modality,
                             JavaBlock.createJavaBlock(postSB), 
                             pio.subTerm().sub(0)));
        
        Update resultUpdate = (actualResult == null
                               ? uf.skip()
                               : uf.elementaryUpdate(TB.var(actualResult), 
                                                     TB.var(resultVar)));
        
        //case distinction necessary because UpdateFactory is unable  
        //to deal with update compositions involving both normal and 
        //anonym*ous* updates; replace by "else" case once this is fixed
        Term postTerm;
        if(anonUpdate.isAnonymousUpdate()) {
            postTerm = uf.apply(resultUpdate, postTermWithoutUpdate);
            postTerm = TB.tf().createAnonymousUpdateTerm(
                                    AnonymousUpdate.getNewAnonymousOperator(), 
                                    postTerm);
        } else {
            postTerm = uf.apply(uf.sequential(new Update[]{selfParamsUpdate,
                                                           atPreUpdate,
                                                           anonUpdate,
                                                           resultUpdate}),
                                postTermWithoutUpdate);
        }
                                                        
        replaceInGoal(postTerm, postGoal, pio);
        
        //create "Exceptional Post" branch
        StatementBlock excPostSB 
            = replaceStatement(jb, new StatementBlock(new Throw(excVar)));
        Term excPostTermWithoutUpdate
            = TB.imp(TB.and(new Term[]{TB.not(excNullTerm),
                                       post.getAxiomsAsFormula(),
                                       post.getFormula()}),
                     TB.prog(modality,
                             JavaBlock.createJavaBlock(excPostSB), 
                             pio.subTerm().sub(0)));
        Term excPostTerm = uf.apply(uf.sequential(new Update[]{selfParamsUpdate,
                                                               atPreUpdate,
                                                               anonUpdate}),
                                    excPostTermWithoutUpdate);
                                                            
        replaceInGoal(excPostTerm, excPostGoal, pio);
        
        //create justification
        RuleJustificationBySpec just = new RuleJustificationBySpec(cwi);
        ComplexRuleJustificationBySpec cjust 
            = (ComplexRuleJustificationBySpec)
              goal.proof().env().getJustifInfo().getJustification(this);
        cjust.add(ruleApp, just);
        
        return result;
    }
    
    
    public Name name() {
        return NAME;
    }


    public String displayName() { 
        return NAME.toString();
    }
    
    
    public String toString() {
        return displayName();
    }
}