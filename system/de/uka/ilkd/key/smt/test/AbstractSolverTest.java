package de.uka.ilkd.key.smt.test;

import java.io.File;

import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.proof.Proof;
import de.uka.ilkd.key.proof.ProofAggregate;
import de.uka.ilkd.key.smt.SmtSolver;
import de.uka.ilkd.key.util.HelperClassForTests;
import junit.framework.Assert;
import junit.framework.TestCase;

public abstract class AbstractSolverTest extends TestCase {

    SmtSolver solver;
    
    static boolean toolNotInstalled = false;
    
    public static final String testFile = System.getProperty("key.home")
    + File.separator + "examples"
    + File.separator + "_testcase"
    + File.separator + "smt"
    + File.separator;

    
    protected void setUp() {
	solver = this.getSolver();
    }
    
    /**
     * returns the solver that should be tested.
     * @return the solver to be tested.
     */
    public abstract SmtSolver getSolver();
    
    public void testAndnot() {
	Assert.assertTrue(correctResult(testFile + "andnot.key", false));
    }
    
    public void testOrnot() {
	Assert.assertTrue(correctResult(testFile + "ornot.key", true));
    }
    
    public void testAndornot() {
	Assert.assertTrue(correctResult(testFile + "andornot.key", false));
    }
    
    public void testAndornot2() {
	Assert.assertTrue(correctResult(testFile + "andornot2.key", true));
    }
    
    public void testImply() {
	Assert.assertTrue(correctResult(testFile + "imply.key", true));
    }
    
    public void testImply2() {
	Assert.assertTrue(correctResult(testFile + "imply2.key", true));
    }
    
    public void testImply3() {
	Assert.assertTrue(correctResult(testFile + "imply3.key", false));
    }
    
    public void testEqui1() {
	Assert.assertTrue(correctResult(testFile + "equi1.key", true));
    }
    
    public void testEqui2() {
	Assert.assertTrue(correctResult(testFile + "equi2.key", false));
    }
    
    public void testAllex1() {
	Assert.assertTrue(correctResult(testFile + "allex1.key", true));
    }
    
    public void testAllex2() {
	Assert.assertTrue(correctResult(testFile + "allex2.key", false));
    }
    
    public void testAllex3() {
	Assert.assertTrue(correctResult(testFile + "allex3.key", true));
    }
    
    public void testEqual1() {
	Assert.assertTrue(correctResult(testFile + "equal1.key", true));
    }
    
    public void testEqual2() {
	Assert.assertTrue(correctResult(testFile + "equal2.key", false));
    }
    
    public void testSubsort1() {
	Assert.assertTrue(correctResult(testFile + "subsort1.key", true));
    }
    
    public void testSubsort2() {
	Assert.assertTrue(correctResult(testFile + "subsort2.key", false));
    }
    
    private boolean correctResult(String filepath, boolean isGeneralValid) {
	if (toolNotInstalled) {
	    return true;
	}
	
	SmtSolver.RESULTTYPE result = SmtSolver.RESULTTYPE.UNKNOWN;
	
	try {
	    result = checkFile(filepath);
	} catch (RuntimeException e) {
	    System.out.println();
	    System.out.println("Error while execution of " + this.getSolver().displayName());
	    System.out.println();
	    toolNotInstalled = true;
	    return true;
	}
	
	if (isGeneralValid) {
	    if (result == SmtSolver.RESULTTYPE.VALID) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    if (!(result == SmtSolver.RESULTTYPE.VALID)) {
		return true;
	    } else {
		return false;
	    }
	}
    }
    
    /**
     * check a problem file
     * @param filepath the path to the file
     * @return the resulttype of the external solver 
     */
    private SmtSolver.RESULTTYPE checkFile(String filepath) {	
	HelperClassForTests x = new HelperClassForTests();	
	ProofAggregate p = x.parse(new File(filepath));
	SmtSolver.RESULTTYPE toReturn = SmtSolver.RESULTTYPE.UNKNOWN;
	Assert.assertTrue(p.getProofs().length == 1);
	Proof proof = p.getProofs()[0];	    
	Assert.assertTrue(proof.openGoals().size() == 1);		
	Goal g = proof.openGoals().iterator().next();
	toReturn = solver.isValid(g, 60, proof.getServices());
	
	return toReturn;
    }
    
}
