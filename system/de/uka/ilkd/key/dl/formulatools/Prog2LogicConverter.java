/*
 * Prog2LogicConverter.java 1.00 Tue Jan 23 16:48:01 CET 2007
 */
package de.uka.ilkd.key.dl.formulatools;

import java.math.BigDecimal;

import de.uka.ilkd.key.dl.logic.ldt.RealLDT;
import de.uka.ilkd.key.dl.model.And;
import de.uka.ilkd.key.dl.model.Biimplies;
import de.uka.ilkd.key.dl.model.Constant;
import de.uka.ilkd.key.dl.model.DLNonTerminalProgramElement;
import de.uka.ilkd.key.dl.model.DLProgramElement;
import de.uka.ilkd.key.dl.model.FunctionTerm;
import de.uka.ilkd.key.dl.model.Implies;
import de.uka.ilkd.key.dl.model.LogicalVariable;
import de.uka.ilkd.key.dl.model.MetaVariable;
import de.uka.ilkd.key.dl.model.NamedElement;
import de.uka.ilkd.key.dl.model.Not;
import de.uka.ilkd.key.dl.model.Or;
import de.uka.ilkd.key.dl.model.PredicateTerm;
import de.uka.ilkd.key.dl.model.Variable;
import de.uka.ilkd.key.java.ProgramElement;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.logic.TermFactory;
import de.uka.ilkd.key.logic.op.AbstractMetaOperator;
import de.uka.ilkd.key.logic.op.LogicVariable;
import de.uka.ilkd.key.logic.op.Metavariable;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.parser.dl.NumberCache;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * The Prog2LogicConverter is used when a program element is dragged to the
 * logic level, thus it converts the DL data structures into Term objects.
 * 
 * @version 1.00
 * @author jdq
 */
public class Prog2LogicConverter extends AbstractMetaOperator {
    public static final Name NAME = new Name("#prog2logic");

    public Prog2LogicConverter() {
        super(NAME, 1);
    }

    /**
     * Just returns the first sub term of the given term. The real conversion is
     * done by the Typeconverter using the static methods of this class.
     * 
     * @param arg0
     *                the prog2logic term
     * @param arg1
     *                unused
     * @param services
     *                unused
     * @return the first subterm of arg0
     * 
     * @see de.uka.ilkd.key.logic.op.AbstractMetaOperator#calculate(de.uka.ilkd.key.logic.Term,
     *      de.uka.ilkd.key.rule.inst.SVInstantiations,
     *      de.uka.ilkd.key.java.Services) calculate
     */
    @Override
    public Term calculate(Term arg0, SVInstantiations arg1, Services services) {
        return arg0.sub(0);
    }

    /**
     * Converts the given program element into a logic term
     * 
     * @param pe
     *                the program element to convert
     * @return the logic representation of the program element
     */
    public static Term convert(DLProgramElement pe, Services services) {
        return convertRecursivly(pe, services);
    }

    public static de.uka.ilkd.key.logic.op.Function getFunction(Name name,
            NamespaceSet namespaces, int arity, Sort sort) {
        de.uka.ilkd.key.logic.op.Function result = (de.uka.ilkd.key.logic.op.Function) namespaces
                .functions().lookup(name);

        if (result == null) {
            Sort[] sorts = new Sort[arity];
            Sort sortR = RealLDT.getRealSort();

            for (int i = 0; i < sorts.length; i++) {
                sorts[i] = sortR;
            }
            if (arity == 0) {
                try {
                    BigDecimal b = new BigDecimal(name.toString());
                    result = NumberCache.getNumber(b, sortR);
                } catch (Exception e) {
                    // not a number
                }
            }
            if (result == null) {
                result = new de.uka.ilkd.key.logic.op.RigidFunction(name, sort,
                        sorts);
            }
            namespaces.functions().add(result);
        }

        return result;
    }

    /**
     * Converts the given DLFormula recursivly into a logic formula
     * 
     * @param form
     *                the formula to convert
     * @return the converted formula
     */
    public static Term convertRecursivly(ProgramElement form, Services services) {
        Sort sortR = RealLDT.getRealSort();

        TermBuilder termBuilder = services.getTypeConverter();
        if (form instanceof PredicateTerm) {
            PredicateTerm p = (PredicateTerm) form;
            Term[] subTerms = new Term[p.getChildCount() - 1];

            for (int i = 1; i < p.getChildCount(); i++) {
                subTerms[i - 1] = convertRecursivly(p.getChildAt(i), services);
            }
            return termBuilder.func(getFunction(
                    ((NamedElement) p.getChildAt(0)).getElementName(), services
                            .getNamespaces(), subTerms.length, Sort.FORMULA),
                    subTerms);
        } else if (form instanceof FunctionTerm) {
            FunctionTerm p = (FunctionTerm) form;
            Term[] subTerms = new Term[p.getChildCount() - 1];

            for (int i = 1; i < p.getChildCount(); i++) {
                subTerms[i - 1] = convertRecursivly(p.getChildAt(i), services);
            }

            return termBuilder
                    .func(getFunction(((NamedElement) p.getChildAt(0))
                            .getElementName(), services.getNamespaces(),
                            subTerms.length, sortR), subTerms);
        } else if (form instanceof DLNonTerminalProgramElement) {
            DLNonTerminalProgramElement p = (DLNonTerminalProgramElement) form;
            Term[] subTerms = new Term[p.getChildCount()];

            for (int i = 0; i < subTerms.length; i++) {
                subTerms[i] = convertRecursivly(p.getChildAt(i), services);
            }

            if (p instanceof And) {
                return termBuilder.and(subTerms);
            } else if (p instanceof Or) {
                return termBuilder.or(subTerms);
            } else if (p instanceof Implies) {
                Term result = termBuilder.tt();

                for (Term element : subTerms) {
                    result = termBuilder.imp(result, element);
                }

                return result;
            } else if (p instanceof Biimplies) {
                Term result = termBuilder.tt();

                for (Term element : subTerms) {
                    result = termBuilder.equiv(result, element);
                }

                return result;
            } else if (p instanceof Not) {
                return termBuilder.not(subTerms[0]);
            }
        } else if (form instanceof Variable) {
            Name elementName = ((Variable) form).getElementName();
            if (form instanceof de.uka.ilkd.key.dl.model.ProgramVariable) {
                de.uka.ilkd.key.logic.op.ProgramVariable var;
                var = (de.uka.ilkd.key.logic.op.ProgramVariable) services
                        .getNamespaces().programVariables().lookup(
                                elementName);
                if (var == null) {
                    throw new IllegalStateException("ProgramVariable " + form
                            + " is not declared");
                }
                return termBuilder.var(var);
            } else if (form instanceof LogicalVariable) {
                LogicVariable var = (LogicVariable) services.getNamespaces()
                        .variables().lookup(elementName);
                if (var == null) { // XXX
                    // TODO: find a way to locate bound variable objects
                    System.out.println("Could not find logic variable " + elementName);//XXX 
                    var = new LogicVariable(elementName,
                            sortR);
                }
                return termBuilder.var(var);
            } else if (form instanceof MetaVariable) {
                Metavariable var = (Metavariable) services.getNamespaces()
                        .variables().lookup(elementName);
                if (var == null) { // XXX
                    throw new IllegalStateException(
                            "Could not find meta variable " + form);
                }
                return TermFactory.DEFAULT
                        .createFunctionTerm((Metavariable) var);
            }
        } else if (form instanceof Constant) {
            return termBuilder.func(getFunction(new Name(""
                    + ((Constant) form).getValue()), services.getNamespaces(),
                    0, sortR));
        }

        throw new IllegalArgumentException("Cannot convert " + form);
    }

    /**
     * @see de.uka.ilkd.key.logic.op.AbstractMetaOperator#sort(de.uka.ilkd.key.logic.Term[])
     *      sort
     */
    @Override
    public Sort sort(de.uka.ilkd.key.logic.Term[] arg0) {
        return Sort.FORMULA;
    }

    /**
     * @see de.uka.ilkd.key.logic.op.AbstractMetaOperator#validTopLevel(de.uka.ilkd.key.logic.Term)
     *      validTopLevel
     */
    @Override
    public boolean validTopLevel(Term arg0) {
        return true;
    }
}