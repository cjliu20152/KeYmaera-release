package de.uka.ilkd.key.dl.arithmetics.impl.qepcad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.uka.ilkd.key.dl.arithmetics.impl.orbital.PolynomTool;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.Function;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.LogicVariable;
import de.uka.ilkd.key.logic.op.Metavariable;
import de.uka.ilkd.key.logic.op.Op;
import de.uka.ilkd.key.logic.op.QuantifiableVariable;
import de.uka.ilkd.key.logic.op.Quantifier;

/**
 * Converts a term to a QepcadInput readable for the Qepcad-Program.
 * 
 * @author Timo Michelsen
 */
public class Term2QepCadConverter {

	static final String USCOREESCAPE = "uscore";
	static final String DOLLARESCAPE = "dollar";
	private QepCadInput input = new QepCadInput(); // Result
	private ArrayList<String> existingVars = new ArrayList<String>(); // List of

	// existing
	// Variables

	/**
	 * Standardconstructor.
	 */
	public Term2QepCadConverter() {
	}

	/**
	 * Function to start to convert a given term.
	 * 
	 * @param form
	 *            Term to convert
	 * @param variables
	 * @return QepCadInput-Instance of the given term.
	 */
	public static QepCadInput convert(Term form,
			List<QuantifiableVariable> variables) {
		Term2QepCadConverter converter = new Term2QepCadConverter();
		return converter.convertImpl(form, variables);
	}

	/**
	 * Implementation of the convert-algorithm
	 * 
	 * @param variables
	 */
	private QepCadInput convertImpl(Term form,
			List<QuantifiableVariable> variables) {

		// Getting the string-representation
		// String formula = "(" + convert2String( form ) + ")";
		String formula = convert2String(form, null);

		// extracts additional information for qepcad
		List<String> freeVarlist = new ArrayList<String>(existingVars);

		// the first parameter is changed by the function
		this.input.setVariableList("("
				+ array2String(getVariableList(freeVarlist, variables)) + ")");
		this.input.setFreeVariableNum(freeVarlist.size());

		if (!formula.startsWith("(") && !formula.startsWith("[")) {
			formula = "[ " + formula + " ].";
		} else {
			formula += ".";
		}
		// Convert formula-String to QepCad-Notation
		// first ( )-Pair, which is no Quantor, must be replaced
		// by [ ]-Pair and a dot.
		// FIXME: this breaks quantified formulas
		// int counter = 0;
		// for( int i = 0; i < formula.length(); i++ ) {
		// if( formula.charAt(i) == '(') {
		// counter++;
		// if( counter == this.quantifiedVars.size() + 1) {
		// formula = formula.substring(0, i) + "[" + formula.substring(i+1,
		// formula.length() - 1) + "].";
		// break;
		// }
		// }
		// }

		this.input.setFormula(formula);
		return this.input;
	}

	private String convert2String(Term form, NamespaceSet nss) {

		if (form.op() == Op.FALSE) {
			return "FALSE";
		} else if (form.op() == Op.TRUE) {
			return "TRUE";
		} else if (form.op().name().toString().equals("equals")) {
			return eliminateFractions(form, nss, "equals", "=");
		} else if (form.op() instanceof Function) {
			Function f = (Function) form.op();
			if (f.name().toString().equals("gt")) {
				return eliminateFractions(form, nss, "gt", ">");
			} else if (f.name().toString().equals("geq")) {
				return eliminateFractions(form, nss, "geq", ">=");
			} else if (f.name().toString().equals("equals")) {
				return eliminateFractions(form, nss, "equals", "="); // 2x EQUALS?
			} else if (f.name().toString().equals("neq")) {
				return eliminateFractions(form, nss, "neq", "/=");
			} else if (f.name().toString().equals("leq")) {
				return eliminateFractions(form, nss, "leq", "<=");
			} else if (f.name().toString().equals("lt")) {
				return eliminateFractions(form, nss, "lt", "<");
			} else if (f.name().toString().equals("add")) {
				return "(" + convert2String(form.sub(0), nss) + "+" + convert2String(form.sub(1), nss) + ")";
			} else if (f.name().toString().equals("sub")) {
				return "(" + convert2String(form.sub(0), nss) + "-" + convert2String(form.sub(1), nss) + ")";
			} else if (f.name().toString().equals("neg")) {
				return "(-" + convert2String(form.sub(0), nss) + ")";
			} else if (f.name().toString().equals("mul")) {
				return "(" + convert2String(form.sub(0), nss) + " " + convert2String(form.sub(1), nss) + ")";
			} else if (f.name().toString().equals("div")) {
				return "(" + convert2String(form.sub(0), nss) + "/" + convert2String(form.sub(1), nss) + ")";
			} else if (f.name().toString().equals("exp")) {
				return "(" + convert2String(form.sub(0), nss) + "^" + convert2String(form.sub(1), nss) + ")";
			} else {
				String[] args = new String[form.arity()];
				for (int i = 0; i < args.length; i++) {
					args[i] = convert2String(form.sub(i), nss);
				}
				try {
					BigDecimal d = new BigDecimal(form.op().name().toString());
					try {
						return String.valueOf(d.intValueExact());
					} catch (ArithmeticException e) {
						int denominator = 1;
						BigDecimal ten = new BigDecimal(10);
						while (d.intValue() < d.doubleValue()) {
							denominator *= 10;
							d = d.multiply(ten);
						}

						// calculate the greatest common divisor of the
						// fraction
						int numerator = d.intValueExact();
						int tmp = numerator;
						int gcd = denominator;
						int t;
						while (tmp > 0) {
							t = numerator;
							tmp = gcd % tmp;
							gcd = t;
						}
						numerator = numerator / gcd;
						denominator = denominator / gcd;
						return "( " + numerator + " / " + denominator + " )";
					}
				} catch (NumberFormatException e) {
					String name = form.op().name().toString();
					if (name.contains("_")) {
						name = name.replaceAll("_", USCOREESCAPE);

					}
					if (name.contains("$")) {
						name = name.replaceAll("\\$", DOLLARESCAPE);
					}
					addExistingVariable(name);
					if (args.length == 0) {
						return "(" + name + ")";
					}
					return "(" + name + "(" + array2String(args) + "))";
				}
			}
		} else if (form.op() instanceof LogicVariable
				|| form.op() instanceof de.uka.ilkd.key.logic.op.ProgramVariable
				|| form.op() instanceof Metavariable) {
			String name = form.op().name().toString();
			if (name.contains("_")) {
				name = name.replaceAll("_", USCOREESCAPE);
			}
			if (name.contains("$")) {
				name = name.replaceAll("\\$", DOLLARESCAPE);
			}
			addExistingVariable(name);
			return "(" + name + ")";
		} else if (form.op() instanceof Junctor) {
			if (form.op() == Junctor.AND) {
				return "[" + convert2String(form.sub(0), nss) + "/\\"
						+ convert2String(form.sub(1), nss) + "]";
			} else if (form.op() == Junctor.OR) {
				return "[" + convert2String(form.sub(0), nss) + "\\/"
						+ convert2String(form.sub(1), nss) + "]";
			} else if (form.op() == Junctor.IMP) {
				return "[" + convert2String(form.sub(0), nss) + "==>"
						+ convert2String(form.sub(1), nss) + "]";
			} else if (form.op() == Junctor.NOT) {
				return "[~[" + convert2String(form.sub(0), nss) + "]]";
			}
		} else if (form.op() instanceof Quantifier) {

			int varsNum = form.varsBoundHere(0).size();
			String[] vars = new String[varsNum];
			for (int i = 0; i < varsNum; i++) {
				String name = form.varsBoundHere(0).getQuantifiableVariable(i)
						.name().toString();
				if (name.contains("_")) {
					name = name.replaceAll("_", USCOREESCAPE);
				}
				if (name.contains("$")) {
					name = name.replaceAll("\\$", DOLLARESCAPE);
				}
				vars[i] = name;
				addExistingVariable(name);
			}

			if (form.op() == Quantifier.ALL) {
				return "(A " + array2String(vars) + ")"
						+ convert2String(form.sub(0), nss);
			} else if (form.op() == Quantifier.EX) {
				return "(E " + array2String(vars) + ")"
						+ convert2String(form.sub(0), nss);
			}
		}
		throw new IllegalArgumentException("Could not convert Term: " + form
				+ "Operator was: " + form.op());
	}

	/**
	 * @param form
	 * @param nss
	 * @param operator
	 * @param qOperator
	 * @return
	 * TODO documentation since Jan 15, 2009
	 */
	private String eliminateFractions(Term form, NamespaceSet nss,
			String operator, String qOperator) {
		Set<Term> fractionFree = PolynomTool
				.eliminateFractionsFromInequality(form, nss);
		String result = null;
		for (Term t : fractionFree) {
			if (t.op().name().toString().equals(operator)) {
				String eq = "[ " + convert2String(t.sub(0), nss) + " " + qOperator
						+ " " + convert2String(t.sub(1), nss) + " ]";
				if (result == null) {
					result = eq;
				} else {
					result = "[ " + result + " /\\ " + eq + " ]";
				}
			} else if (t.op() instanceof Function
					&& t.op().name().toString().equals("neq")) {
				String eq = "[ " + convert2String(t.sub(0), nss) + " /= "
						+ convert2String(t.sub(1), nss) + " ]";
				if (result == null) {
					result = eq;
				} else {
					result = "[ " + result + " /\\ " + eq + " ]";
				}
			} else {
				throw new IllegalArgumentException(
						"Elimination of fractions returned an unsupported toplevel operator in "
								+ t);
			}
		}
		return result;
	}

	// Converts an array of Strings in
	// one string. The elements are seperated by
	// ','
	private String array2String(String[] args) {
		if (args == null)
			return "";

		String result = "";
		for (int i = 0; i < args.length; i++) {
			result += args[i];
			if (i != args.length - 1)
				result += ",";
		}

		return result;
	}

	// Inserts a new variable in the list of existing variables,
	// if is not in the list
	private void addExistingVariable(String varName) {
		if (!existingVars.contains(varName)) {
			this.existingVars.add(varName);
		}

	}

	// Gets the variable-list, which is important for qepcad
	private String[] getVariableList(List<String> allVariables,
			List<QuantifiableVariable> quantifiedVars) {

		ArrayList<String> freeVars = new ArrayList<String>();
		List<String> quantified = new ArrayList<String>();

		for (QuantifiableVariable var : quantifiedVars) {
			String name = var.name().toString();
			if (name.contains("_")) {
				name = name.replaceAll("_", USCOREESCAPE);
			}
			if (name.contains("$")) {
				name = name.replaceAll("\\$", DOLLARESCAPE);
			}
			allVariables.remove(name);
			quantified.add(name);
		}
		Collections.reverse(quantified);

		for (String var : allVariables) {
			freeVars.add(var);
		}

		String[] result = new String[allVariables.size() + quantified.size()];
		for (int i = 0; i < freeVars.size(); i++) {
			result[i] = freeVars.get(i);
		}
		for (int i = 0; i < quantified.size(); i++) {
			result[freeVars.size() + i] = quantified.get(i);
		}

		return result;
	}
}
