/***************************************************************************
 *   Copyright (C) 2007 by Jan-David Quesel                                *
 *   quesel@informatik.uni-oldenburg.de                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package de.uka.ilkd.key.dl.arithmetics.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import orbital.math.Arithmetic;
import orbital.math.Fraction;
import orbital.math.Integer;
import orbital.math.Polynomial;
import orbital.math.Real;
import orbital.math.Values;
import orbital.math.Vector;
import de.uka.ilkd.key.dl.arithmetics.impl.csdp.CSDP;
import de.uka.ilkd.key.dl.arithmetics.impl.orbital.PolynomTool;
import de.uka.ilkd.key.dl.arithmetics.impl.sos.PolynomialOrder;
import de.uka.ilkd.key.dl.arithmetics.impl.sos.SimpleOrder;
import de.uka.ilkd.key.dl.formulatools.collector.AllCollector;
import de.uka.ilkd.key.dl.formulatools.collector.FilterVariableSet;
import de.uka.ilkd.key.dl.formulatools.collector.filter.FilterVariableCollector;
import de.uka.ilkd.key.dl.logic.ldt.RealLDT;
import de.uka.ilkd.key.dl.model.Greater;
import de.uka.ilkd.key.dl.model.GreaterEquals;
import de.uka.ilkd.key.dl.model.Less;
import de.uka.ilkd.key.dl.model.LessEquals;
import de.uka.ilkd.key.dl.model.Minus;
import de.uka.ilkd.key.dl.model.MinusSign;
import de.uka.ilkd.key.dl.model.Unequals;
import de.uka.ilkd.key.dl.parser.NumberCache;
import de.uka.ilkd.key.gui.Main;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.logic.op.Equality;
import de.uka.ilkd.key.logic.op.Function;
import de.uka.ilkd.key.logic.op.Op;
import de.uka.ilkd.key.logic.op.Operator;
import de.uka.ilkd.key.logic.op.TermSymbol;

/**
 * @author jdq
 * 
 */
public class SumOfSquaresChecker {

	private enum Result {
		SOLUTION_FOUND, NO_SOLUTION_AVAILABLE, UNKNOWN;
	}

	public enum FormulaStatus {
		VALID, INVALID, UNKNOWN;
	}

	public static class PolynomialClassification<T> {
		public Set<T> f;
		public Set<T> g;
		public Set<T> h;

		public PolynomialClassification(Set<T> f, Set<T> g, Set<T> h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
	}

	public static final SumOfSquaresChecker INSTANCE = new SumOfSquaresChecker();

	/**
	 * Given a sequent seperated into antecedent and succedent this methods
	 * generates a classification of all occuring inequalities, unequalities and
	 * equalities into sets f, g and h. Where f contains all inequalities, g the
	 * unequalities and h the equalities.
	 * 
	 * @param ante
	 *            the antecedent of the sequent
	 * @param succ
	 *            the succedent of the sequent
	 * @return a classification of the given terms
	 */
	public PolynomialClassification<Term> classify(Set<Term> ante,
			Set<Term> succ) {
		System.out.println("Computing f, g^2 and h");// XXX
		final Function lt = RealLDT.getFunctionFor(Less.class);
		final Function leq = RealLDT.getFunctionFor(LessEquals.class);
		final Function geq = RealLDT.getFunctionFor(GreaterEquals.class);
		final Function gt = RealLDT.getFunctionFor(Greater.class);
		final Function neq = RealLDT.getFunctionFor(Unequals.class);
		Term zero = TermBuilder.DF.func(NumberCache.getNumber(
				new BigDecimal(0), RealLDT.getRealSort()));
		// handle succedent
		Set<Term> conjunction = new HashSet<Term>();
		for (Term t : succ) {
			Term sub, sub2;
			Operator op;
			if (t.op().equals(Op.NOT)) {
				sub = t.sub(0).sub(0);
				sub2 = t.sub(0).sub(1);
				op = t.sub(0).op();
			} else {
				sub = t.sub(0);
				sub2 = t.sub(1);
				op = negationLookUp(t.op());
			}
			if (!(sub.equals(zero) || sub2.equals(zero))) {
				sub = TermBuilder.DF.func(RealLDT.getFunctionFor(Minus.class), t.sub(0), t
						.sub(1));
				sub2 = zero;
			}
			if (t.sub(0).equals(zero) && !t.sub(1).equals(zero)) {
				Term hold = sub;
				sub = sub2;
				sub2 = hold;
			}
			if (op instanceof Function) {
				if (!op.equals(neq) && t.sub(0).equals(zero)
						&& !t.sub(1).equals(zero)) {
					op = negationLookUp(op);
				}
				conjunction
						.add(TermBuilder.DF.func((TermSymbol) op, sub, sub2));
			} else if (op instanceof Equality) {

				conjunction.add(TermBuilder.DF.equals(sub, sub2));
			}
		}
		for (Term t : ante) {
			Term sub, sub2;
			Operator op;
			if (t.op().equals(Op.NOT)) {
				sub = t.sub(0).sub(0);
				sub2 = t.sub(0).sub(1);
				op = negationLookUp(t.sub(0).op());
			} else {
				sub = t.sub(0);
				sub2 = t.sub(1);
				op = t.op();
			}
			if (!(sub.equals(zero) || sub2.equals(zero))) {
				sub = TermBuilder.DF.func(RealLDT.getFunctionFor(Minus.class), t.sub(0), t
						.sub(1));
				sub2 = zero;
			}
			if (t.sub(0).equals(zero) && !t.sub(1).equals(zero)) {
				Term hold = sub;
				sub = sub2;
				sub2 = hold;
			}
			if (op instanceof Function) {
				if (!op.equals(neq) && t.sub(0).equals(zero)
						&& !t.sub(1).equals(zero)) {
					op = negationLookUp(op);
				}
				conjunction
						.add(TermBuilder.DF.func((TermSymbol) op, sub, sub2));
			} else if (op instanceof Equality) {
				conjunction.add(TermBuilder.DF.equals(sub, sub2));
			}
		}
		System.out.println("Finished computing conjunction");// XXX
		// split to f, g, h
		Set<Term> f = new HashSet<Term>();
		Set<Term> g = new HashSet<Term>();
		Set<Term> h = new HashSet<Term>();
		for (Term t : conjunction) {
			if (t.op() == Equality.EQUALS) {
				// H is the set of equalities thus we just need to add this term
				h.add(t);
			} else if (t.op().equals(neq)) {
				// G is the set of unequalities thus we just need to add this term
				g.add(t);
			} else if (t.op().equals(geq)) {
				// F contains all inequalities of the form x >= y thus we just need to add this term
				f.add(t);
			} else if (t.op().equals(gt)) {
				// the term is x > y thus we add x >= y to F and x != y to G 
				f.add(TermBuilder.DF.func(geq, t.sub(0), t.sub(1)));
				g.add(TermBuilder.DF.func(neq, t.sub(0), t.sub(1)));
			} else if (t.op().equals(leq)) {
				// switch arguments to turn x <= y into y >= x
				f.add(TermBuilder.DF.func(geq, t.sub(1), t.sub(0)));
			} else if (t.op().equals(lt)) {
				// the term is x < y thus we add y >= x to F and y != x to G
				f.add(TermBuilder.DF.func(geq, t.sub(1), t.sub(0)));
				g.add(TermBuilder.DF.func(neq, t.sub(1), t.sub(0)));
			} else if (t.op().equals(TermBuilder.DF.tt().op())
					|| t.op().equals(TermBuilder.DF.ff().op())) {
				// TODO jdq: we need to do something useful with this
				System.err.println("Found " + t.op() + " (" + t.op().getClass()
						+ ") " + " instead of an inequality");
			} else {
				throw new IllegalArgumentException(
						"Dont know how to handle the predicate " + t.op());
			}
		}
		return new PolynomialClassification<Term>(f, g, h);
	}

	/**
	 * @param op
	 * @return
	 */
	private Operator negationLookUp(Operator op) {
		final Function lt = RealLDT.getFunctionFor(Less.class);
		final Function leq = RealLDT.getFunctionFor(LessEquals.class);
		final Function geq = RealLDT.getFunctionFor(GreaterEquals.class);
		final Function gt = RealLDT.getFunctionFor(Greater.class);
		final Function neq = RealLDT.getFunctionFor(Unequals.class);
		if (op.equals(neq)) {
			return Equality.EQUALS;
		} else if (op == Equality.EQUALS) {
			return neq;
		} else if (op.equals(geq)) {
			return lt;
		} else if (op.equals(gt)) {
			return leq;
		} else if (op.equals(lt)) {
			return geq;
		} else if (op.equals(leq)) {
			return gt;
		}
		throw new IllegalArgumentException("Unknown operator " + op);
	}

	/**
	 * Generate polynomials from the given term classification. The result
	 * contains only the leftside polynomial of the inequalities, where for f
	 * the omitted part is > 0, for g it is != 0 and for h it is = 0.
	 */
	public PolynomialClassification<Polynomial> classify(
			PolynomialClassification<Term> cla) {
		return classify(cla, false);
	}
	/**
	 * Generate polynomials from the given term classification. The result
	 * contains only the leftside polynomial of the inequalities, where for f
	 * the omitted part is > 0, for g it is != 0 and for h it is = 0.
	 */
	public PolynomialClassification<Polynomial> classify(
			PolynomialClassification<Term> cla, boolean addAddtionalVariable) {
		System.out.println("Try to find monominals");// XXX
		System.out.println("We check the following Terms:");// XXX
		Set<String> variables = new HashSet<String>();
		System.out.println("F contains: "); // XXX
		for (Term t : cla.f) {
			System.out.println(t);// XXX

			// added by Timo Michelsen
			// BEGIN
			FilterVariableSet set = AllCollector.getItemSet(t);
			FilterVariableSet set2 = set.filter(new FilterVariableCollector(
					null));
			variables.addAll(set2.getVariables());
			// END

			// replaced:
			// variables.addAll(VariableCollector.getVariables(t));
		}
		System.out.println("-- end F");
		System.out.println("G contains: "); // XXX
		for (Term t : cla.g) {
			System.out.println(t);// XXX
			// added by Timo Michelsen
			// BEGIN
			FilterVariableSet set = AllCollector.getItemSet(t);
			FilterVariableSet set2 = set.filter(new FilterVariableCollector(
					null));
			variables.addAll(set2.getVariables());
			// END

			// replaced:
			// variables.addAll(VariableCollector.getVariables(t));
		}
		System.out.println("-- end G");
		System.out.println("H contains: "); // XXX
		for (Term t : cla.h) {
			System.out.println(t);// XXX
			// added by Timo Michelsen
			// BEGIN
			FilterVariableSet set = AllCollector.getItemSet(t);
			FilterVariableSet set2 = set.filter(new FilterVariableCollector(
					null));
			variables.addAll(set2.getVariables());
			// END

			// replaced:
			// variables.addAll(VariableCollector.getVariables(t));
		}
		System.out.println("-- end H");
		List<String> vars = new ArrayList<String>();
		vars.addAll(variables);
		if(addAddtionalVariable) {
			vars.add("newVariable$var$blub");
		}
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("orbital.math.UnivariatePolynomial.sparse", "true");
		Values.getInstance(hashMap);
		Polynomial result = null;

		// now we need to construct the different polynomes
		HashSet<Polynomial> polyF = new HashSet<Polynomial>();
		HashSet<Polynomial> polyG = new HashSet<Polynomial>();
		HashSet<Polynomial> polyH = new HashSet<Polynomial>();

		for (Term t : cla.f) {
			Fraction p = PolynomTool.createFractionOfPolynomialsFromTerm(t.sub(0), vars);
			polyF.add((Polynomial) p.numerator());
			if (!p.denominator().isOne()) {
				polyG.add((Polynomial) p.denominator());
			}
		}
		for (Term t : cla.g) {
			Fraction p = PolynomTool.createFractionOfPolynomialsFromTerm(t.sub(0), vars);
			polyG.add((Polynomial) p.numerator());
			if (!p.denominator().isOne()) {
				polyG.add((Polynomial) p.denominator());
			}
		}
		for (Term t : cla.h) {
			Fraction p = PolynomTool.createFractionOfPolynomialsFromTerm(t.sub(0), vars);
			polyH.add((Polynomial) p.numerator());
			if (!p.denominator().isOne()) {
				polyG.add((Polynomial) p.denominator());
			}
		}
		return new PolynomialClassification<Polynomial>(polyF, polyG, polyH);
	}

	/**
	 * Compute a conjunction of inequaltities f of the form f >= 0, g != 0 or
	 * equalities h = 0. Afterwards check if f+g^2+h = 0 is satisfiable. If this
	 * holds the input is satisfiable too.
	 * 
	 * @return true if a combination of f, g and h is found such that f+g^2+h =
	 *         0.
	 */
	public FormulaStatus check(PolynomialClassification<Term> cla) {
		return check(cla.f, cla.g, cla.h);
	}

	/**
	 * Compute a conjunction of inequaltities f of the form f >= 0, g != 0 or
	 * equalities h = 0. Afterwards check if f+g^2+h = 0 is satisfiable. If this
	 * holds the input is satisfiable too.
	 * 
	 * @return true if a combination of f, g and h is found such that f+g^2+h =
	 *         0.
	 */
	public FormulaStatus check(Set<Term> f, Set<Term> g, Set<Term> h) {
		PolynomialClassification<Polynomial> classify = classify(new PolynomialClassification<Term>(
				f, g, h));
		PolynomialOrder order = new SimpleOrder();
		order.setF(classify.f);
		order.setG(classify.g);
		order.setH(classify.h);
		order.setMaxDegree(20);
		while (order.hasNext()) {
			System.out.println("searching");
			Result searchSolution = searchSolution(order.getNext());
			System.out.println("Result: " + searchSolution);
			if (searchSolution == Result.SOLUTION_FOUND) {
				return FormulaStatus.INVALID;
			}
		}
		return FormulaStatus.UNKNOWN;
	}

	/**
	 * @param result
	 * @return
	 */
	private Result searchSolution(Polynomial result) {
		// now we need to translate the polynominal into a matrix representation
		// monominals are iterated x^0y^0, x^0y^1, x^0y^2, ..., x^1y^0, x^1y^1,
		// x^1y^2,..., x^2y^0, x^2y^1,...
		ListIterator mono = result.iterator();
		System.out.println("Degree: " + result.degree());
		System.out.println("Degree-Value: " + result.degreeValue());// XXX
		Iterator indices = result.indices();
		System.out.println("Rank: " + result.rank());// XXX
		List<Vector> monominals = new ArrayList<Vector>();
		while (mono.hasNext()) {
			Object next = mono.next();
			String blub = "";
			Vector v = (Vector) indices.next();
			for (int i = 0; i < v.dimension(); i++) {
				blub += ((char) ('a' + i)) + "^" + v.get(i);
			}
			System.out.println(next + "*" + blub);// XXX
			if (!next.equals(Values.getDefault().ZERO())) {
				boolean ok = true;
				Vector div = Values.getDefault()
						.valueOf(new int[v.dimension()]);
				for (int i = 0; i < v.dimension(); i++) {
					if (v.get(i) instanceof Real) {
						Real in = (Real) v.get(i);
						Real sqrt = in.divide(Values.getDefault().valueOf(2));
						try {
							new BigDecimal(sqrt.doubleValue()).intValueExact();
							System.out.println("Found nice half: " + sqrt);// XXX
							double[] d = new double[v.dimension()];
							d[i] = in.divide(Values.getDefault().valueOf(2))
									.doubleValue();
							div = div.add(Values.getDefault().valueOf(d));
						} catch (Exception e) {
							ok = false;
						}
					}
				}
				if (ok) {
					System.out.println("Adding monominal: " + div);// XXX
					monominals.add(div);
				}
			}
		}
		// now we know the monominals and need to construct the constraints for
		// the matrix
		Vector[][] matrix = new Vector[monominals.size()][monominals.size()];
		for (int i = 0; i < monominals.size(); i++) {
			for (int j = 0; j < monominals.size(); j++) {
				matrix[i][j] = Values.getDefault().valueOf(
						new Integer[] { Values.getDefault().valueOf(i + 1),
								Values.getDefault().valueOf(j + 1) });
			}
		}
		Poly multiplyVec = multiplyVec(multiplyMatrix(monominals, matrix),
				monominals);
		System.out.println("Polynom: " + multiplyVec);// XXX
		mono = result.iterator();
		indices = result.indices();

		List<Constraint> constraints = new ArrayList<Constraint>();
		while (mono.hasNext()) {
			Object next = mono.next();
			Vector v = (Vector) indices.next();
			if (!Values.getDefault().ZERO().equals(next)) {
				System.out.println("Checking: " + next + " and vector " + v);// XXX
				List<Vector> list = multiplyVec.vec.get(v);
				if (list != null) {
					constraints.add(new Constraint(v, list, (Arithmetic) next));
				} else {
					System.out.println("Cannot express: " + v);// XXX
					return Result.UNKNOWN;
				}
			}
		}
		System.out.println(constraints);// XXX
		// outputMatlab(monominals, constraints);

		if (CSDP
				.sdp(monominals.size(), constraints.size(),
						convertConstraintsToResultVector(constraints,
								monominals.size()), convertConstraintsToCSDP(
								constraints, monominals.size()))) {
			return Result.SOLUTION_FOUND;
		} else {
			return Result.NO_SOLUTION_AVAILABLE;
		}
	}

	/**
	 * @param monominals
	 * @param constraints
	 */
	private void outputMatlab(List<Vector> monominals,
			List<Constraint> constraints) {
		System.out.println("Matlab Input: ");// XXX
		System.out.println("n = " + monominals.size() + ";");// XXX
		System.out.println("cvx_begin");// XXX
		System.out.println("variable X(n,n) symmetric;");// XXX
		System.out.println("minimize(0);");// XXX
		System.out.println("subject to");// XXX
		convertConstraints(constraints, monominals.size());
		System.out.println("X == semidefinite(n)");// XXX
		System.out.println("cvx_end");// XXX
		// print out X
		System.out.println("X");// XXX
	}

	/**
	 * @param constraints
	 * @param i
	 */
	private double[] convertConstraintsToCSDP(List<Constraint> constraints,
			int size) {
		double[] result = new double[constraints.size() * size * size];
		int cnum = 0;
		for (Constraint c : constraints) {
			int[][] selectionMatrix = new int[size][size];
			for (Vector v : c.indizes) {
				selectionMatrix[((Integer) v.get(0)).intValue() - 1][((Integer) v
						.get(1)).intValue() - 1] = 1;
			}
			// we generate one big matrix in c representation containing all
			// selection matrices
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					result[cnum * size * size + i * size + j] = selectionMatrix[i][j];
				}
			}
			cnum++;
		}
		return result;
	}

	/**
	 * @param constraints
	 * @param i
	 */
	private double[] convertConstraintsToResultVector(
			List<Constraint> constraints, int size) {
		double[] result = new double[constraints.size()];
		int cnum = 0;
		for (Constraint c : constraints) {
			result[cnum++] = Double.parseDouble(c.pre.toString());
		}
		return result;
	}

	/**
	 * @param constraints
	 * @param i
	 */
	private void convertConstraints(List<Constraint> constraints, int size) {
		for (Constraint c : constraints) {
			int[][] selectionMatrix = new int[size][size];
			for (Vector v : c.indizes) {
				selectionMatrix[((Integer) v.get(0)).intValue() - 1][((Integer) v
						.get(1)).intValue() - 1] = 1;
			}
			StringBuilder matrix = new StringBuilder();
			matrix.append("[ ");
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					matrix.append(selectionMatrix[i][j] + " ");
				}
				if (i + 1 < size) {
					matrix.append("; ");
				}
			}
			matrix.append(" ]");
			System.out.println("trace( " + matrix + " * X) == " + c.pre + ";");// XXX
		}
	}

	private class Constraint {

		Constraint(Vector v, List<Vector> indizes, Arithmetic pre) {
			this.v = v;
			this.indizes = indizes;
			this.pre = pre;
		}

		Vector v;

		List<Vector> indizes;

		Arithmetic pre;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		/* @Override */
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(pre + " = ");
			for (int i = 0; i < v.dimension(); i++) {
				b.append(((char) ('a' + i)) + "^" + v.get(i));
			}
			b.append(" * (");
			for (Vector vec : indizes) {
				b.append("+" + vec);
			}
			b.append(")");
			return b.toString();
		}
	}

	private class Poly {
		HashMap<Vector, List<Vector>> vec = new HashMap<Vector, List<Vector>>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		/* @Override */
		public String toString() {
			StringBuilder b = new StringBuilder();
			String plus = "";
			for (Vector v : vec.keySet()) {
				b.append(plus);
				plus = "+";
				for (int i = 0; i < v.dimension(); i++) {
					b.append(((char) ('a' + i)) + "^" + v.get(i));
				}
				b.append("* (");
				for (Vector w : vec.get(v)) {
					b.append(plus);
					b.append(w);
				}
				b.append(")");
			}
			return b.toString();
		}
	}

	/**
	 * @param multiplyMatrix
	 * @param monominals
	 */
	private Poly multiplyVec(Vec multiplyMatrix, List<Vector> monominals) {
		Poly p = new Poly();
		for (int i = 0; i < monominals.size(); i++) {
			for (Vector vv : multiplyMatrix.vec[0].keySet()) {
				Vector res = vv.add(monominals.get(i));
				List<Vector> result = p.vec.get(res);
				if (result == null) {
					result = new ArrayList<Vector>();
					p.vec.put(res, result);
				}
				result.addAll(multiplyMatrix.vec[i].get(vv));
			}
		}
		return p;
	}

	private class Vec {
		HashMap<Vector, List<Vector>>[] vec;
	}

	/**
	 * @param monominals
	 * @param matrix
	 */
	private Vec multiplyMatrix(List<Vector> monominals, Vector[][] matrix) {
		Vec p = new Vec();
		p.vec = new HashMap[monominals.size()];
		for (int i = 0; i < monominals.size(); i++) {
			p.vec[i] = new HashMap<Vector, List<Vector>>();
			for (int j = 0; j < monominals.size(); j++) {
				List<Vector> list = p.vec[i].get(monominals.get(j));
				System.out.println("Multiplying: " + monominals.get(j)
						+ " with " + matrix[i][j]);// XXX
				if (list == null) {
					list = new ArrayList<Vector>();
					p.vec[i].put(monominals.get(j), list);
				}
				list.add(matrix[i][j]);
			}
		}
		return p;
	}
}
