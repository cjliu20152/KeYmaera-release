/*******************************************************************************
 * Copyright (c) 2012 Jan-David Quesel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Jan-David Quesel - initial API and implementation
 ******************************************************************************/
package de.uka.ilkd.key.dl.options;


/**

 * SubCategories used for grouping the different options
 * 
 * @author zacho
 */
public enum EPropertyConstant {
	    
	    DLOPTIONS_INITIAL_TIMEOUT("[DLOptions]initialTimeout", "initial timeout","the timeout used in the first iteration of the IBC strategy (in seconds)", "DL Properties"),
	    DLOPTIONS_DIFFSAT_TIMEOUT("[DLOptions]diffSatTimeout","initial DiffSat timeout", "the timeout used in the first iteration of the differential saturation  strategy (in seconds)", "DL Properties"),
	    DLOPTIONS_LOOPSAT_TIMEOUT("[DLOptions]loopSatTimeout", "initial LoopSat timeout", "the timeout used in the first iteration of the loop saturation strategy (in seconds)", "DL Properties"),	  
	    
	    
	    DLOPTIONS_FO_STRATEGY ("[DLOptions]FOStrategy","First-Order Strategy", "<html><body>choose the strategy for first order goals. Either <br>STOP at first-order or <br>UNFOLD first-order completely and then stop"
	    									 + " <br>EAGER quantifier elimination as soon as first-order or <br>LAZY quantifier elimination at leaves when no more options or <br>IBC Iterative Background"
	    									 + " Closure strategy interleaving arithmetic and logic with incremental timeouts.</body></html>", "DL Properties"),
	    									            
	    DLOPTIONS_CEX_FINDER ("[DLOptions]CexFinder", "counterexample search", "choose a search algorithm for finding counterexamples", "DL Properties"),
	    DLOPTIONS_TRACER_STAT ("[DLOptions]TracerStat", "counterexample history", "whether tracing facility is enabled", "DL Properties"),
	    											 	    									  
	    DLOPTIONS_QUADRIC("[DLOptions]quadricTimeoutIncreaseFactor", "quadratic timeout", "the quadratic part of the IBC timeout. That is, the part c of t_new = a*t_old^2 + b*t_old + c (in seconds)", "DL Properties"),
	    DLOPTIONS_LINEAR("[DLOptions]linearTimeoutIncreaseFactor", "linear timeout", "the linear part of the IBC timeout. That is, the part c of t_new = a*t_old^2 + b*t_old + c (in seconds)", "DL Properties"),
	    DLOPTIONS_CONSTANT("[DLOptions]constantTimeoutIncreaseFactor", "constant timeout","the constant part of the IBC timeout. That is, the part c of t_new = a*t_old^2 + b*t_old + c (in seconds)", "DL Properties"),
            
	    DLOPTIONS_READD_QUANTIFIERS("[DLOptions]readdQuantifiers", "re-add quantifiers", "During quantifier elimination, re-add the quantfiers of previously quantified variables (i.e. Skolem symbols)", "DL Properties"),
	    DLOPTIONS_SIMPLIFY_BEFORE_REDUCE("[DLOptions]simplifyBeforeReduce", "simplify before QE","simplify formulas passed to quantifier eliminator of the arithmetic solver (careful check advised!)", "DL Properties"),
	    DLOPTIONS_SIMPLIFY_AFTER_REDUCE("[DLOptions]simplifyAfterReduce", "simplify after QE","simplify the results generated by the quantifier eliminator of the arithmetic solver (careful check advised!)", "DL Properties"),
	    DLOPTIONS_APPLY_UPDATES_TO_MODALITIES("[DLOptions]applyToModality", "update modalities", "apply assignments to modalites e.g. to get simpler solutions for differential equations", "DL Properties"),
	    DLOPTIONS_COUNTEREXAMPLE_GENERATOR("[DLOptions]counterExampleGenerator", "counterexample tool", "select the tool for generating counterexamples", "DL Properties"),
	    DLOPTIONS_ODESOLVER("[DLOptions]odeSolver", "differential equations", "select the solver that should be used to solve differential equations or handle them by differential induction", "DL Properties"),
	    DLOPTIONS_QUANTIFIER_ELIMINATOR("[DLOptions]quantifierEliminator", "real arithmetic solver","select the solver for real arithmetic that should be used to eliminate quantifiers by quantifier elimination (QE)", "DL Properties"),
	    DLOPTIONS_SIMPLIFIER("[DLOptions]simplifier", "arithmetic simplifier", "select the simplification algorithm that should be used to simplify arithmetical expressions", "DL Properties"),
	    DLOPTIONS_APPLY_GAMMA_RULES("[DLOptions]applyGammaRules", "apply gamma rules", "choose if and when gamma rules should be applied for existential quantifiers", "DL Properties"),
	    DLOPTIONS_COUNTEREXAMPLE_TEST("[DLOptions]counterexampleTest", "counterexample", "whether to check for counterexamples before trying to prove exhaustively", "DL Properties"),
	    DLOPTIONS_INVARIANT_RULE("[DLOptions]invariantRule", null, null, "DL Properties"),
	    DLOPTIONS_USE_DIFF_SAT("[DLOptions]DiffSat", "Differential Saturation", "<html><body>select the desired automation degree of Differential " 
	    									  + "Saturation for automatic differential induction. Either <br>BLIND solving (careful check of ODE solutions advised!) or <br>OFF stopping at complex differential equations or <br>SIMPLE differential invariants or <br>DIFFAUTO differential saturation for differential equations or <br>AUTO differential saturation and loop saturation or <br>DESPARATE automatic solving with fallback to non-arithmetic solutions (careful check of ODE solutions advised!)</body></html>", "DL Properties"),
	    DLOPTIONS_IGNORE_ANNOTATIONS("[DLOptions]ignoreAnnotations", "ignore @annotations", "Whether to ignore all proof skeleton @annotations, like @invariant, @candidate etc.", "DL Properties"),
	    DLOPTIONS_SIMPLIFY_TIMEOUT("[DLOptions]simplifyTimeout", "simplify timeout", "the timeout used for calls to the simplifier (in seconds), <=0 means no timeout", "DL Properties"),
	    DLOPTIONS_ITERATIVE_REDUCE_RULE("[DLOptions]useIterativeReduceRule", "Iterative Inflation", "whether to activate the Iterative Inflation " 
	    								                              + "Order (IIO) with increasingly bigger formulas.", "DL Properties"),
	    DLOPTIONS_TERM_FACTORY_CLASS("[DLOptions]termFactoryClass", null, null, "DL Properties"),
	    DLOPTIONS_APPLY_LOCAL_REDUCE("[DLOptions]applyLocalReduce", "local QE","try to eliminate quantifiers in single first-order formulas (before trying to quantifier eliminate the complete sequent)", "DL Properties"),
	    DLOPTIONS_APPLY_LOCAL_SIMPLIFY("[DLOptions]applyLocalSimplify", "local simplify", "try to simplify single first-order subformulas (before trying to quantifier eliminate the complete sequent)", "DL Properties"),
	    DLOPTIONS_APPLY_GLOBAL_REDUCE("[DLOptions]applyGlobalReduce", "global QE","try to quantifier eliminate the complete sequent if possible", "DL Properties"),
	    DLOPTIONS_SIMPLIFY_AFTER_ODESOLVE("[DLOptions]simplifyAfterODESolve", "simplify after ODESolve","simplify the results generated by the ODESolve function of the arithmetic solver", "DL Properties"),
	    DLOPTIONS_GROEBNER_BASIS_CALCULATOR("[DLOptions]groebnerBasisCalculator", "equation solver", "select the solver for handling equational theories, e.g., by Gr\u00f6bner bases", "DL Properties"),
	    DLOPTIONS_SOS_CHECKER("[DLOptions]sosChecker", "SOS checker", "select the solver for handling the universal fragment of real arithmetic", "DL Properties"),
	    DLOPTIONS_USE_POWERSET_ITERATIVE_REDUCE("[DLOptions]usePowersetIterativeReduce", "Inflation Powerset", "whether to use the powerset for iterative inflation or not", "DL Properties"),
	    DLOPTIONS_PERCENT_OF_POWERSET_FOR_ITERATIVE_REDUCE("[DLOptions]percentOfPowersetForIterativeReduce", "Inflation Percent", "the percentage of the powerset to use for the iterative inflation", "DL Properties"),
	    DLOPTIONS_BUILT_IN_ARITHMETIC("[DLOptions]BuiltInArithmetic", "built-in arithmetic", "<html><body>select to which degree built-in arithmetic rules should be used. Either <br>OFF or <br>NORMALIZE polynomials or <br>NORMALIZE&REDUCE polynomials with multivariate polynomial division or <br>FULL S-POLYNOMIAL forming like Gr\u00f6bner bases</body></html>", "DL Properties"),
	    DLOPTIONS_BUILT_IN_ARITHMETIC_INEQS("[DLOptions]BuiltInArithmeticIneqs", "built-in inequalities", "select whether built-in rules for inequalities are to be used", "DL Properties"),
	    DLOPTIONS_USE_SOS("[DLOptions]useSOS", "semi-definite programs", "select whether to use semi-definite programming and sum of squares rule, or instead use groebner basis or quantifier elimination rules.", "DL Properties"),
	    
	    DLOPTIONS_CSDP_PATH("[DLOptions]csdpPath", "csdp binary", "The path to the csdp binary file. (Used by groebnerSOS and internal sos)", "DL Properties"),
	    DLOPTIONS_CSDP_FORCE_INTERNAL("[DLOptions]csdpForceInternal", "force libcsdp", "Force KeYmaera to use the library version of csdp instead of the binary. This does lead to CSDP being unusable if the natives are not available.", "DL Properties"),
		
	    HOL_OPTIONS_HOLLIGHT_PATH("[HOLLightOptions]hollightPath", "HOL Light Path", "The path to the hol light installation needed to setup the correct environment for the tool","HOL Light Properties"),
	    HOL_OPTIONS_OCAML_PATH("[HOLLightOptions]ocamlPath", "Ocaml Path", "The ocaml binary", "HOL Light Properties"),
	    HOL_OPTIONS_HARRISON_QE_PATH("[HOLLightOptions]harrisonqePath", "Harrison QE Path", "The path to harrisons implementation of quantifier elimination", "HOL Light Properties"),	
	    HOL_OPTIONS_QUANTIFIER_ELIMINATION_METHOD("[HOLLightOptions]qeMethod", "quantifier elimination", "The quantifier elimination method to use","HOL Light Properties"),	
	   
	    MATHEMATICA_OPTIONS_MATHEMATICA_PATH("[MathematicaOptions]mathematicaPath", "Mathematica Path","The path where Mathematica is installed.","Mathematica Properties"), 
	    MATHEMATICA_OPTIONS_MATHKERNEL("[MathematicaOptions]mathKernel", "MathKernel path", "the path to the MathKernel binary","Mathematica Properties"),
	    MATHEMATICA_OPTIONS_JLINK_LIBDIR("com.wolfram.jlink.libdir", "J/Link native dir", "the path where the J/Link natives are located. Restart is required when this setting is changed.","Mathematica Properties"),	    
	    MATHEMATICA_OPTIONS_QUANTIFIER_ELIMINATION_METHOD("[MathematicaOptions]quantifierEliminationMethod", "quantifier elimination","the Mathematica method that is used to perform quantifier elimination","Mathematica Properties"),			    
	    MATHEMATICA_OPTIONS_USE_ELIMINATE_LIST("[MathematicaOptions]useEliminateList", "elimination list", "choose if the list of elimination variables should be passed to Mathematica's Reduce","Mathematica Properties"),
	    MATHEMATICA_OPTIONS_MEMORYCONSTRAINT("[MathematicaOptions]memoryConstraint", "memory limit", "the maximum memory used by the Mathematica server [in bytes], -1 means no limit","Mathematica Properties"),
	    MATHEMATICA_OPTIONS_CONVERT_DECIMAL_FRACTIONS_TO_RATIONALS( "[MathematicaOptions]convertDecimalFractionsToRationals", "convert decimals", "choose if decimal fraction entered by the user should be converted into a rational representation (q/r)","Mathematica Properties"),
	    
	    ORBITAL_OPTIONS_REPRESENTATION("[OrbitalOptions]representation", null, null,"ORBITAL Properties"),
	    ORBITAL_OPTIONS_SPARSEPOLYNOMIALS("[OrbitalOptions]sparsePolynomials", null, null,"ORBITAL Properties"),
	    ORBITAL_OPTIONS_PRECISION("[OrbitalOptions]precision", null, null,"ORBITAL Properties"),

	    QEPCAD_OPTIONS_QEPCAD_PATH("[QepcadOptions]qepcadPath", "Qepcad Path", "The path to the qepcad installation needed to setup the correct environment for the tool (it must contain bin/qepcad binary)","Qepcad Properties"),
	    QEPCAD_OPTIONS_SACLIB_PATH("[QepcadOptions]saclibPath", "Saclib Path", "The path to the saclib installation needed to setup the correct environment for Qepcad","Qepcad Properties"),
	    QEPCAD_OPTIONS_QEPCAD_MEMORYLIMIT("[QepcadOptions]qepcadMemoryLimit", "memory limit", "The number of kilobytes qepcad may use for its computation. (set to -1 for default value)","Qepcad Properties"),
	    
	    COHENHORMANDER_OPTIONS_MODE("[CohenhormanderOptions]eliminatorMode", "elimination mode", "Enable or disable disjunctive normal form conversion","CohenHormander Properties"),

	    OPTIONS_REDUCE_BINARY("[ReduceOptions]reduceBinary", "Reduce Binary", "<html>The path to the Redlog/Reduce binary installation needed<br>"
										+ "to setup the correct environment for the tool</html>","Redlog Properties"), 
	    OPTIONS_REDUCE_QUANTIFIER_ELIMINATION_METHOD("[ReduceOptions]quantifierEliminationMethod", "quantifier elimination", "<html>The method to use for quantifier elimination<br>"
		    								+ "(virtual substitution (rlqe), Cylindrical algebraic<br>"
		    								+ "decomposition (rlcad)...)</html>","Redlog Properties"), 
	    OPTIONS_REDUCE_ELIMINATE_FRACTIONS("[ReduceOptions]eliminateFractions", null, null, "Redlog Properties"), 
	    OPTIONS_REDUCE_RLALL("[ReduceOptions]rlall", "universal closure", "Whether to form the universal closure of formulas when passing formulas to redlog.", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlanuexsgnopt("[ReduceOptions]rlanuexsgnopt", "rlanuexsgnopt","<html>Sign optimization. This is turned off by default.<br>"
											  + "If turned on, it is tried to determine the sign of a constant polynomial<br>"
											  + "by calculating a containment.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlanuexgcdnormalize("[ReduceOptions]rlanuexgcdnormalize", "rlanuexgcdnormalize", "<html>GCD normalize. This is turned on by default.<br>"
		    													 + "If turned on, the GCD is normalized to 1, if it is a constant polynomial.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlanuexpsremseq("[ReduceOptions]rlanuexpsremseq", "rlanuexpsremseq", "<html>Pseudo remainder sequences. This is turned off by default.<br>"
		    										+ "This switch decides, whether division or pseudo division is used for<br>"
		    										+ "sturm chains.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadhongproj("[ReduceOptions]rlcadhongproj", "rlcadhongproj", "<html>Hong projection. This is on by default.<br>"
		    									  + "If turned on, Hong's improvement for the projection operator is used.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadaprojalways("[ReduceOptions]rlcadaprojalways", "rlcadaprojalways", "<html>Augmented projection (always). By default, rlcadaproj is turned on and<br>"
												   + "rlcadaprojalways is turned off. If rlcadaproj is turned off, no augmented<br>"
												   + "projection is performed. Otherwerwise, if turned on, augmented projection<br>"
												   + "is performed always (if rlcadaprojalways is turned on) or just for the<br>"
												   + "free variable space (rlcadaprojalways turned off).</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadaproj("[ReduceOptions]rlcadaproj", "rlcadaproj","<html>Augmented projection (always). By default, rlcadaproj is turned on and<br>"
										+ "rlcadaprojalways is turned off. If rlcadaproj is turned off, no augmented<br>"
										+ "projection is performed. Otherwerwise, if turned on, augmented projection<br>"
										+ "is performed always (if rlcadaprojalways is turned on) or just for the<br>"
										+ "free variable space (rlcadaprojalways turned off).</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadisoallroots("[ReduceOptions]rlcadisoallroots", "rlcadisoallroots","<html>Isolate all roots. This is off by default. Turning this switch on allows<br>"
												  + "to find out, how much time is consumed more without incremental root<br>"
												  + "isolation.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadrawformula("[ReduceOptions]rlcadrawformula", "rlcadrawformula", "<html>Raw formula. Turned off by default.<br>"
		    										+ "If turned on, a variable-free DNF is returned (if simple solution<br>"
		    										+ "formula construction succeeds). Otherwise, the raw result is simplified<br>"
		    										+ "with rldnf.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadtrimtree("[ReduceOptions]rlcadtrimtree", "rlcadtrimtree", "<html>Trim tree. This is turned on by default.<br>"
		    									  + "Frees unused part of the constructed partial CAD-tree, and hence<br>"
		    									  + "saves space. However, afterwards it is not possible anymore to find<br>"
		    									  + "out how many cells were calculated beyond free variable space.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadfulldimonly("[ReduceOptions]rlcadfulldimonly", "rlcadfulldimonly", "<html>Full dimensional cells only. This is turned off by default.<br>"
												   + "Only stacks over full dimensional cells are built. Such cells have<br>"
												   + "rational sample points. To do this ist sound only in special cases<br>"
												   + "as consistency problems (existenially quantified, strict inequalities).</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadpbfvs("[ReduceOptions]rlcadpbfvs", "rlcadpbfvs", "<html>Propagation below free variable space, the second improvement to partial CAD.<br>"
		    								 + "This is turned on by default.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadte("[ReduceOptions]rlcadte", "rlcadte", "<html>Trial evaluation, the first improvement to partial CAD.<br>"
									+ " This is turned on by default.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadpartial("[ReduceOptions]rlcadpartial", "rlcadpartial", "Partial CAD. This is turned on by default. ", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadextonly("[ReduceOptions]rlcadextonly",  "rlcadextonly", "Extension phase only. Turned off by default. ", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadprojonly("[ReduceOptions]rlcadprojonly", "rlcadprojonly", "Projection phase only. Turned off by default. ", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadbaseonly("[ReduceOptions]rlcadbaseonly", "rlcadbaseonly", "Base phase only. Turned off by default. ", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlcadfac("[ReduceOptions]rlcadfac", "rlcadfac", "Factorisation. This is on by default. ", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlqepnf("[ReduceOptions]rlqepnf", "rlqepnf", "<html>Quantifier elimination compute prenex normal form. By default this switch<br>"
									+ "is on, which causes that rlpnf (see Miscellaneous Normal Forms) is applied to formula<br>"
									+ "before starting the elimination process. If the argument formula to rlqe/rlqea or<br>"
									+ "rlgqe/rlgqea (see Generic Quantifier Elimination) is already prenex, this switch can<br>"
									+ "be turned off. This may be useful with formulas containing equiv since rlpnf applies rlnnf,<br>"
									+ "(see Miscellaneous Normal Forms), and resolving equivalences can double the size of a formula.<br>"
									+ "rlqepnf is ignored in acfsf, since nnf is necessary for elimination there.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlqeheu("[ReduceOptions]rlqeheu", "rlqeheu", "<html>Quantifier elimination search heuristic. By default this switch is on<br>"
		    							+ "in ofsf and off in dvfsf. It is ignored in acfsf. Turning rlqeheu on causes<br>"
		    							+ "the switch rlqedfs to be ignored. rlqe/rlqea and rlgqe/rlgqea (see Generic<br>"
		    							+ "Quantifier Elimination) will then decide between breadth first search and depth<br>"
		    							+ "first search for each quantifier block, where dfs is chosen when the problem<br>"
		    							+ "is a decision problem.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlqedfs("[ReduceOptions]rlqedfs", "rlqedfs", "<html>Quantifier elimination depth first search. By default this switch is off.<br>"
		    							+ "It is also ignored in the acfsf context. It is ignored with the switch<br>"
		    							+ "rlqeheu on, which is the default for ofsf. Turning rlqedfs on makes<br>"
		    							+ "rlqe/rlqea and rlgqe/rlgqea (see Generic Quantifier Elimination) work<br>"
		    							+ "in a depth first search manner instead of breadth first search. This saves<br>"
		    							+ "space, and with decision problems, where variable-free atomic formulas can<br>"
		    							+ "be evaluated to truth values, it might save time. In general, it leads to<br>"
		    							+ "larger results.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlqesqsc("[ReduceOptions]rlqesqsc", "rlqesqsc", "<html>Quantifier elimination (super) quadratic special case. By default<br>"
		    							   + "these switches are off. They are relevant only in ofsf. If turned on,<br>"
		    							   + "alternative elimination sets are used for certain special cases by<br>"
		    							   + "rlqe/rlqea and rlgqe/rlgqea. (see Generic Quantifier Elimination).<br>"
		    							   + "They will possibly avoid violations of the degree restrictions, but lead<br>"
		    							   + "to larger results in general. Former versions of redlog without these<br>"
		    							   + "switches behaved as if rlqeqsc was on and rlqesqsc was off.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_rlqeqsc("[ReduceOptions]rlqeqsc", "rlqeqsc", "<html>Quantifier elimination (super) quadratic special case.<br>"
		    							+ "By default these switches are off. They are relevant only in ofsf.<br>"
									+ "If turned on, alternative elimination sets are used for certain<br>"
									+ "special cases by rlqe/rlqea and rlgqe/rlgqea. (see Generic Quantifier<br>"
									+ "Elimination). They will possibly avoid violations of the degree<br>"
									+ "restrictions, but lead to larger results in general. Former versions<br>"
									+ "of redlog without these switches behaved as if rlqeqsc was on and<br>"
									+ "rlqesqsc was off.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_RLSIMPL("[ReduceOptions]rlsimpl",  "simplify formulas", "<html>Simplify. By default this switch is off.<br>" 
		    								   +"With this switch on, the function rlsimpl is applied at the expression<br>" 
		    								   +"evaluation stage. See rlsimpl.<br>" 
		    								   + "Automatically performing formula simplification at the evaluation stage<br>" 
		    								   + "is very similar to the treatment of polynomials or rational functions,<br>" 
		    								   + "which are converted to some normal form. For formulas, however, the<br>" 
		    								   + "simplified equivalent is by no means canonical.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_RLNZDEN("[ReduceOptions]rlnzden",  "non-zero denominators", "<html>Non-zero denominators. By default this switch is off.<br>" 
		    								   +"Activating rlnzden, allows the input of reciprocal terms. These terms are<br>"
		                                       + "assumed to be non-zero, and resolved by multiplication. When occurring with<br>"
		                                       + "ordering relations the reciprocals are resolved by multiplication with their<br>"
		                                       + "square preserving the sign.</html>", "Redlog Properties"), 
	    OPTIONS_REDUCE_RLPOSDEN("[ReduceOptions]rlposden",  "positive denominators", "<html>Positive denominators (soundness critical!). By default this switch is changed to off.<br>" 
		    								   +"Activating rlposden, allows the input of reciprocal terms but assumes the<br>"
		                                       +"reciprocals to be strictly positive. These terms are resolved by simple,<br>"
		                                       +"i.e. non-square, multiplication also with ordering relations.</html>", "Redlog Properties"), 
		
	    
	    INITIAL_DIALOG_CHECKBOX("[StartUpOptions]skipInitialDialog", null,"Check to skip this dialog in the future.", "CheckBox"), 
	    DLOPTIONS_RESET_STRATEGY_AFTER_EVERY_RUN("[DLOptions]resetStrategyAfterEveryRun", "reset strategy", "Check this option to reset the strategy knowledge about applicable rules after every execution of the strategy. Clears caches after every step.", "DL Properties"), 
	    DLOPTIONS_USE_ODE_IND_FIN_METHODS("[DLOptions]useODEIndFinMethods", "external differential invariant", "Check this option to use the differential invariant and differential variant implementations provides by the current ODE Solver instead of the internal ones implemented directly in KeYmaera.", "DL Properties"), 
	    Z3_OPTIONS_Z3_BINARY("[Z3Options]z3Binary", "Z3 Binary", "The path to the z3 binary.", "Z3 Properties"),
	    Z3_OPTIONS_PRENEX_FORM("[Z3Options]z3Prenex", "prenex form", "Convert the formula into prenex form before passing it to Z3.", "Z3 Properties"),
	    Z3_OPTIONS_ELIMINATE_EXISTENTIAL_PREFIX("[Z3Options]z3ElimExPrefix", "existential prefix", "Convert leading existential quantifiers into variable declarations.", "Z3 Properties"),
	    ;

	    private String groupName;
	    private String key;
	    private String label;
	    private String toolTip;

	    EPropertyConstant(String key, String label, String toolTip, String groupName) {
	        this.label = label;
	        this.key = key;
	        this.toolTip = toolTip;
	        this.groupName = groupName;
	    }
	    
	    public String getLabel() {
		return label;
	    }
	    public String getKey() {
		return key;
	    }
	    public String getToolTip() {
		return toolTip;
	    }
	    public String getGroupName() {
		return groupName;
	    }

}
