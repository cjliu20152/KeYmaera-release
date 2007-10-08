/**
 * 
 */
package de.uka.ilkd.key.dl.arithmetics.impl.orbital;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import orbital.logic.Composite;
import orbital.logic.functor.Functor;
import orbital.logic.functor.VoidFunction;
import orbital.math.AlgebraicAlgorithms;
import orbital.math.Arithmetic;
import orbital.math.Integer;
import orbital.math.Rational;
import orbital.math.Real;
import orbital.math.Scalar;
import orbital.math.Symbol;
import orbital.math.UnivariatePolynomial;
import orbital.math.Values;
import orbital.math.functional.Function;
import orbital.math.functional.Operations;

import org.w3c.dom.Node;

import de.uka.ilkd.key.dl.arithmetics.IODESolver;
import de.uka.ilkd.key.dl.arithmetics.impl.orbital.DL2MatrixFormConverter.MatrixForm;
import de.uka.ilkd.key.dl.formulatools.Prog2LogicConverter;
import de.uka.ilkd.key.dl.logic.ldt.RealLDT;
import de.uka.ilkd.key.dl.model.DLNonTerminalProgramElement;
import de.uka.ilkd.key.dl.model.DLProgramElement;
import de.uka.ilkd.key.dl.model.DiffSystem;
import de.uka.ilkd.key.dl.model.Div;
import de.uka.ilkd.key.dl.model.Dot;
import de.uka.ilkd.key.dl.model.ProgramVariable;
import de.uka.ilkd.key.dl.model.impl.DiffSystemImpl;
import de.uka.ilkd.key.gui.Main;
import de.uka.ilkd.key.java.ProgramElement;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.logic.op.LogicVariable;
import de.uka.ilkd.key.logic.op.SubstOp;
import de.uka.ilkd.key.logic.op.TermSymbol;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.parser.dl.NumberCache;

/**
 * TODO jdq documentation since Aug 20, 2007
 * 
 * @author jdq
 * @since Aug 20, 2007
 * 
 */
public class Orbital implements IODESolver {

    private static Map<Functor, String> operationTable = new HashMap<Functor, String>();

    static {
        operationTable.put(Operations.divide, "div");
        operationTable.put(Operations.plus, "add");
        operationTable.put(Operations.minus, "neg");
        operationTable.put(Operations.subtract, "sub");
        operationTable.put(Operations.product, "mul");
        operationTable.put(Operations.power, "exp");
    }

    /**
     * 
     */
    private static final String NAME = "Orbital";

    /**
     * 
     */
    public Orbital(Node node) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orbital.math.Scalar.precision", "big");
        Values.setDefault(Values.getInstance(params));
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IODESolver#odeSolve(de.uka.ilkd.key.dl.model.DiffSystem,
     *      de.uka.ilkd.key.logic.op.LogicVariable,
     *      de.uka.ilkd.key.logic.op.LogicVariable, de.uka.ilkd.key.logic.Term,
     *      de.uka.ilkd.key.logic.NamespaceSet)
     */
    public ODESolverResult odeSolve(DiffSystem form, LogicVariable t, LogicVariable ts,
            Term phi, NamespaceSet nss) throws RemoteException {
        List<String> vars = new ArrayList<String>();

        collectDottedProgramVariables(form, vars, t);
        Term invariant = DiffSystemImpl.getInvariant(form);
        List<ProgramElement> equations = DiffSystemImpl.getDifferentialEquations(form);

        // create matrix
        MatrixForm matrixForm = DL2MatrixFormConverter.INSTANCE
                .convertToMatrixForm(vars, equations);
        System.out.println("Solving ODE x'(t) ==\n" + matrixForm.matrix
                + "*x(t) + " + matrixForm.b + "\n" + "  " + matrixForm.eta
                + "'(t) == " + matrixForm.matrix.multiply(matrixForm.eta)
                + " + " + matrixForm.b + "\n" + "\nwith initial value  "
                + matrixForm.eta + " at 0");// XXX
        Function solve = AlgebraicAlgorithms.dSolve(matrixForm.matrix,
                matrixForm.b, Values.getDefault().ZERO(), matrixForm.eta);
        System.out.println("yields " + solve); // XXX
        final Symbol tSym = Values.getDefault().symbol("t");
        System.out
                .println("  solution at " + tSym + " is " + solve.apply(tSym));// XXX
        // Arithmetic apply = (Arithmetic) solve.apply(tSym);
        final UnivariatePolynomial[] componentPolynomials = AlgebraicAlgorithms
                .componentPolynomials((UnivariatePolynomial) solve);
        System.out.println(Arrays.toString(componentPolynomials));// XXX
        List<Term> values = new ArrayList<Term>();
        final Sort sortR = RealLDT.getRealSort();
        final Term zero = TermBuilder.DF.func(NumberCache.getNumber(
                new BigDecimal("0"), sortR));
        int j = 0;
        for (UnivariatePolynomial poly : componentPolynomials) {

            ListIterator<?> iterator = poly.iterator();
            Term value = zero;
            int i = 0;
            while (iterator.hasNext()) {
                Object next = iterator.next();
                Term n = convert(sortR, zero, nss, next);

                if (n != null) {
                    if (i == 0) {
                        value = n;
                    } else if (i == 1) {
                        value = TermBuilder.DF.func((TermSymbol) nss
                                .functions().lookup(new Name("add")), value,
                                TermBuilder.DF.func((TermSymbol) nss
                                        .functions().lookup(new Name("mul")),
                                        n, TermBuilder.DF.var(t)));
                    } else {
                        Term tTerm = TermBuilder.DF.var(t);
                        tTerm = TermBuilder.DF.func((TermSymbol) nss
                                .functions().lookup(new Name("exp")), tTerm,
                                TermBuilder.DF.func(NumberCache.getNumber(
                                        new BigDecimal(i), sortR)));
                        value = TermBuilder.DF.func((TermSymbol) nss
                                .functions().lookup(new Name("add")), value,
                                TermBuilder.DF.func((TermSymbol) nss
                                        .functions().lookup(new Name("mul")),
                                        n, tTerm));
                    }
                }
                i++;
            }
            System.out.println("Solution for " + matrixForm.eta.get(j) + " is "
                    + value);// XXX
            values.add(value);
            j++;
        }
        List<Term> locations = new ArrayList<Term>();
        for (Arithmetic variable : matrixForm.eta.toArray()) {
            de.uka.ilkd.key.logic.op.ProgramVariable lookup = (de.uka.ilkd.key.logic.op.ProgramVariable) nss
                    .programVariables().lookup(
                            new Name(((Symbol) variable).getSignifier()));
            locations.add(TermBuilder.DF.var(lookup));
        }

        invariant = TermBuilder.DF.tf().createSubstitutionTerm(
                SubstOp.SUBST,
                t,
                TermBuilder.DF.var(ts),
                de.uka.ilkd.key.logic.TermFactory.DEFAULT.createUpdateTerm(
                        locations.toArray(new Term[0]), values
                                .toArray(new Term[0]), invariant));
        invariant = ((SubstOp)invariant.op()).apply(invariant);
        // insert 0 <= ts <= t
        de.uka.ilkd.key.logic.op.Function leq = (de.uka.ilkd.key.logic.op.Function) nss
                .functions().lookup(new Name("leq"));
        Term tsRange = TermBuilder.DF.and(TermBuilder.DF.func(leq, zero,
                TermBuilder.DF.var(ts)), TermBuilder.DF.func(leq,
                TermBuilder.DF.var(ts), TermBuilder.DF.var(t)));
        invariant = TermBuilder.DF.imp(tsRange, invariant);
        invariant = TermBuilder.DF.all(ts, invariant);
        return new ODESolverResult(invariant,
                de.uka.ilkd.key.logic.TermFactory.DEFAULT.createUpdateTerm(
                        locations.toArray(new Term[0]), values
                                .toArray(new Term[0]), phi));
    }

    public Term diffInd(DiffSystem form, Term post, NamespaceSet nss)
    throws RemoteException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * TODO jdq documentation since Aug 20, 2007
     * 
     * @param next
     * @param next
     * @param zero
     * @return
     */
    private Term convert(Sort sortR, Term zero, NamespaceSet nss, Object next) {
        if (next instanceof Symbol) {
            Symbol sym = (Symbol) next;
            de.uka.ilkd.key.logic.op.ProgramVariable pvar = (de.uka.ilkd.key.logic.op.ProgramVariable) nss
                    .programVariables().lookup(new Name(sym.getSignifier()));
            if (pvar != null) {
                return TermBuilder.DF.var(pvar);
            } else {
                de.uka.ilkd.key.logic.op.Function f = (de.uka.ilkd.key.logic.op.Function) nss
                        .functions().lookup(new Name(sym.getSignifier()));
                if (f != null) {
                    return TermBuilder.DF.func(f);
                }
            }
        } else if (next instanceof Scalar) {
            if (next instanceof Integer) {
                Integer i = (Integer) next;
                return TermBuilder.DF.func(NumberCache.getNumber(
                        new BigDecimal(i.longValue()), sortR));
            } else if (next instanceof Rational) {
                Rational r = (Rational) next;
                de.uka.ilkd.key.logic.op.Function div = RealLDT.getFunctionFor(Div.class);
                return TermBuilder.DF.func(div, convert(sortR, zero, nss, r
                        .numerator()), convert(sortR, zero, nss, r
                        .denominator()));
            } else if (next instanceof Real) {
                Real r = (Real) next;
                return TermBuilder.DF.func(NumberCache.getNumber(
                        new BigDecimal(r.floatValue()), sortR));
            }
        } else if (next instanceof Composite) {
            System.out.println("Found composite " + next);// XXX
            Composite f = (Composite) next;
            Object[] components;
            if (f.getComponent() instanceof Object[]) {
                components = (Object[]) f.getComponent();
            } else {
                components = new Object[] { f.getComponent() };
            }
            de.uka.ilkd.key.logic.op.Function op = null;
            if (operationTable.containsKey(f.getCompositor())) {
                op = (de.uka.ilkd.key.logic.op.Function) nss
                        .functions()
                        .lookup(new Name(operationTable.get(f.getCompositor())));
                Term[] terms = new Term[components.length];
                int i = 0;
                for (Object arith : components) {
                    terms[i++] = convert(sortR, zero, nss, arith);
                }
                return TermBuilder.DF.func(op, terms);
            }
        } else if (next instanceof VoidFunction) {
            return convert(sortR, zero, nss, ((VoidFunction) next).apply());
            // System.out.println("Found function" + next);//XXX
            // Function of = (Function) next;
            // de.uka.ilkd.key.logic.op.Function f =
            // (de.uka.ilkd.key.logic.op.Function) nss
            // .functions().lookup(new Name(of.toString()));
            // if (f != null) {
            // return TermBuilder.DF.func(f);
            // }
        }
        throw new IllegalArgumentException("Dont know what to do with type: "
                + next.getClass());
    }

    /**
     * Collect all program variables which are children of a Dot.
     * 
     * @param form
     *                the current root element.
     * 
     * @param vars
     *                the Map used for storing the result
     * @param t
     *                the variable used as time
     */
    private void collectDottedProgramVariables(ProgramElement form,
            List<String> vars, LogicVariable t) {
        if (form instanceof Dot) {
            ProgramVariable pv = (ProgramVariable) ((Dot) form).getChildAt(0);
            vars.add(pv.getElementName().toString());
        }
        if (form instanceof DLNonTerminalProgramElement) {
            DLNonTerminalProgramElement dlnpe = (DLNonTerminalProgramElement) form;
            for (ProgramElement p : dlnpe) {
                collectDottedProgramVariables(p, vars, t);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#abortCalculation()
     */
    public void abortCalculation() throws RemoteException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#getCachedAnwserCount()
     */
    public long getCachedAnwserCount() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#getName()
     */
    public String getName() {
        return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#getQueryCount()
     */
    public long getQueryCount() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#getTimeStatistics()
     */
    public String getTimeStatistics() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#getTotalCalculationTime()
     */
    public long getTotalCalculationTime() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.dl.arithmetics.IMathSolver#resetAbortState()
     */
    public void resetAbortState() throws RemoteException {
        // TODO Auto-generated method stub

    }

}