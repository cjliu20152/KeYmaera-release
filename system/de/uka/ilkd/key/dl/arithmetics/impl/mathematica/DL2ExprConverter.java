/**
 * File created 13.02.2007
 */
package de.uka.ilkd.key.dl.arithmetics.impl.mathematica;

import java.math.BigDecimal;
import java.util.Map;

import com.wolfram.jlink.Expr;

import de.uka.ilkd.key.dl.model.Constant;
import de.uka.ilkd.key.dl.model.Div;
import de.uka.ilkd.key.dl.model.Dot;
import de.uka.ilkd.key.dl.model.Equals;
import de.uka.ilkd.key.dl.model.Exp;
import de.uka.ilkd.key.dl.model.FreeFunction;
import de.uka.ilkd.key.dl.model.FunctionTerm;
import de.uka.ilkd.key.dl.model.LogicalVariable;
import de.uka.ilkd.key.dl.model.MetaVariable;
import de.uka.ilkd.key.dl.model.Minus;
import de.uka.ilkd.key.dl.model.MinusSign;
import de.uka.ilkd.key.dl.model.Mult;
import de.uka.ilkd.key.dl.model.NamedElement;
import de.uka.ilkd.key.dl.model.Plus;
import de.uka.ilkd.key.dl.model.Predicate;
import de.uka.ilkd.key.dl.model.PredicateTerm;
import de.uka.ilkd.key.dl.model.ProgramVariable;
import de.uka.ilkd.key.java.ProgramElement;
import de.uka.ilkd.key.logic.op.LogicVariable;

/**
 * Converter for the DLProgram to Expr transformation.
 * 
 * @author jdq
 * @since 13.02.2007
 * 
 */
public class DL2ExprConverter implements ExprConstants {

    /**
     * Converts the given DLProgram (has to be an Differential Equation) into an
     * equivalant Mathematica Expr.
     * 
     * @param el
     *                the element to convert
     * @param t
     *                the variable used for the time (this is used to transform
     *                x' to x'[t])
     * @param vars
     *                the variables that are time dependend
     * @return the Expr representing the differential equation
     */
    public static Expr convertDiffEquation(ProgramElement el, LogicVariable t,
            Map<String, Expr> vars) {
        if (el instanceof Equals) {
            return EQUALS;
        } else if (el instanceof PredicateTerm) {
            Expr[] args = new Expr[((PredicateTerm) el).getChildCount() - 1];
            for (int i = 0; i < args.length; i++) {
                args[i] = convertDiffEquation(((PredicateTerm) el)
                        .getChildAt(i + 1), t, vars);
            }
            if (args.length == 0) {
                return new Expr(Expr.SYMBOL, ((Predicate) ((PredicateTerm) el)
                        .getChildAt(0)).getElementName().toString());
            } else {
                return new Expr(convertDiffEquation(((PredicateTerm) el)
                        .getChildAt(0), t, vars), args);
            }
        } else if (el instanceof FunctionTerm) {
            Expr[] args = new Expr[((FunctionTerm) el).getChildCount() - 1];
            for (int i = 0; i < args.length; i++) {
                args[i] = convertDiffEquation(((FunctionTerm) el)
                        .getChildAt(i + 1), t, vars);
            }
            de.uka.ilkd.key.dl.model.Function f = (de.uka.ilkd.key.dl.model.Function) ((FunctionTerm) el)
                    .getChildAt(0);
            if (args.length == 0) {
                String name = f.getElementName().toString();
                name = name.replaceAll("_", USCORE_ESCAPE);
                return new Expr(Expr.SYMBOL, name);
            } else {
                return new Expr(convertDiffEquation(f, t, vars), args);
            }
        } else if (el instanceof Plus) {
            return PLUS;
        } else if (el instanceof Minus) {
            return MINUS;
        } else if (el instanceof MinusSign) {
            return MINUSSIGN;
        } else if (el instanceof Mult) {
            return MULT;
        } else if (el instanceof Div) {
            return DIV;
        } else if (el instanceof Exp) {
            return EXP;
        } else if (el instanceof Constant) {
            BigDecimal d = ((Constant) el).getValue();
            try {
                return new Expr(d.intValueExact());
            } catch (ArithmeticException e) {
                return new Expr(d);
            }
        } else if (el instanceof ProgramVariable) {
            ProgramVariable v = (ProgramVariable) el;
            Expr var = new Expr(Expr.SYMBOL, v.getElementName().toString());
            if (vars.containsKey(v.getElementName().toString())) {
                return new Expr(var, new Expr[] { new Expr(Expr.SYMBOL, t
                        .name().toString()) });
            } else {
                return var;
            }
        } else if (el instanceof MetaVariable) {
            MetaVariable v = (MetaVariable) el;
            return new Expr(Expr.SYMBOL, v.getElementName().toString());

        } else {
            if (el instanceof NamedElement) {
                String name = ((NamedElement) el).getElementName().toString();
                name = name.replaceAll("_", USCORE_ESCAPE);
                if (el instanceof LogicalVariable) {
                    return new Expr(Expr.SYMBOL, name);
                } else if (el instanceof FreeFunction) {
                    return new Expr(Expr.SYMBOL, name);
                }
            } else if (el instanceof Dot) {
                Dot dot = (Dot) el;
                Expr variableName = new Expr(Expr.SYMBOL,
                        ((NamedElement) dot.getChildAt(0))
                                .getElementName().toString());
                Expr differentialSymbol = new Expr(new Expr(new Expr(Expr.SYMBOL, "Derivative"),
                        new Expr[] { new Expr(dot.getOrder()) }),
                        new Expr[] { variableName });
                if (null == t) {
                    // use implicit differential symbols if there is no temporal variable
                    return differentialSymbol;
                } else {
                    return new Expr(
                        differentialSymbol,
                        new Expr[] { new Expr(Expr.SYMBOL, t.name().toString()) });
                }
            }
        }
        throw new IllegalArgumentException("Dont now how to translate: " + el
                + "(" + el.getClass() + ")");
    }
}