// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2005 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//

/** this class offers some methods for assertions, debug output and so
 * on */ 
package de.uka.ilkd.key.util;


public final class Debug {

    private Debug() {}

    /** has to be set in order to enable assertion */
    public static boolean ENABLE_ASSERTION = true; 
    /** has to be set in order to enable debugging */
    public static boolean ENABLE_DEBUG =
       "on".equals(System.getProperty("KeyDebugFlag")); 

    /** prints given string if in debug mode 
     * @param msg the String to be printed
     */
    public static void out(String msg) {
	if (ENABLE_DEBUG) {
	    System.out.println("DEBUG:: "+msg);
	}
    }

    /** prints given string and the result of calling the toString method of 
     * of the given obj if in debug mode. The advantage of calling the toString
     * here and not before is that if debug mode is disabled no string
     * computation is done
     * @param msg the String to be printed
     * @param obj the Object where to call the toString method
     */
    public static final void out(String msg, Object obj) {
	if (ENABLE_DEBUG) {
		System.out.println("DEBUG:: "+msg+" "+obj);
	}
    }

    /** prints given string and the result of calling the toString method of 
     * of the given objects if in debug mode. The advantage of calling the toString
     * here and not before is that if debug mode is disabled no string
     * computation is done
     * @param msg the String to be printed
     * @param obj1 the first Object where to call the toString method
     * @param obj2 the second Object where to call the toString method
     */
    public static final void out(String msg, Object obj1, Object obj2) {
	if (ENABLE_DEBUG) {
		System.out.println("DEBUG:: "+msg+":("+obj1+", "+obj2+")");
	}
    }



    /** prints given string and the result of calling the toString method of 
     * of the given objects if in debug mode. The advantage of calling the toString
     * here and not before is that if debug mode is disabled no string
     * computation is done
     * @param msg the String to be printed
     * @param obj1 the first Object where to call the toString method
     * @param obj2 the second Object where to call the toString method
     * @param obj3 the third Object where to call the toString method
     */
    public static final void out(String msg, Object obj1, Object obj2, Object obj3) {
	if (ENABLE_DEBUG) {
		System.out.println("DEBUG:: "+msg+":("+obj1+", "+obj2+", "+obj3+")");
	}
    }

    /** prints the given string followed by the int if in debug mode.
     * @param msg the String to be printed
     * @param id the int printed after msg
     */
    public static final void out(String msg, long id) {
	if (ENABLE_DEBUG) {
	    System.out.println("DEBUG:: "+msg+" "+id);
	}
    }

    /** prints the given string followed by the int if in debug mode.
     * @param msg the String to be printed
     * @param id1 the int printed first after msg
     * @param id1 the int printed second after msg
     */
    public static final void out(String msg, long id1, long id2) {
	if (ENABLE_DEBUG) {
	    System.out.println("DEBUG:: "+msg+":("+id1+", "+id2+")");
	}
    }

    /** prints the given string followed by the boolean if in debug mode.
     * @param msg the String to be printed
     * @param b the boolean printed after msg
     */
    public static final void out(String msg, boolean b) {
	if (ENABLE_DEBUG) {
	    System.out.println("DEBUG:: "+msg+" "+b);
	}
    }

    /** prints given string, if the condition cond is true
     * @param msg the String to be printed
     * @param cond the boolean deciding if the message is printed or not
     */
    public static final void outIfEqual(String msg, boolean cond) {
	if (ENABLE_DEBUG) {
	    if (cond) {
		System.out.println("DEBUG:: "+msg);
	    }
	}
    }


    /** prints given string, if calling the equal method of obj1, with obj2 as
     * argument results in true
     * @param msg the String to be printed
     * @param obj1 the Object where to call the equals method
     * @param obj2 the Object given to as parameter of the equals method of obj1
     */
    public static final void outIfEqual(String msg, Object obj1, Object obj2) {
	if (ENABLE_DEBUG) {
	    if ((obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2))) {
		System.out.println("DEBUG:: "+msg);
	    }
	}
    }


    /** prints the stack trace if in debug mode 
     * @author VK
     */
    public static final void out(Exception e) {
	if (ENABLE_DEBUG) {
	    e.printStackTrace();
	}
    }



    /**  an assertion failure is thrown if isOK is evaluated to false
     * @param isOK boolean the assertion that is checked
     */
    public static final void assertTrue(boolean isOK) {
	if (ENABLE_ASSERTION) {
	    if (!isOK) {
		fail();
	    }
	}
    }

    public static final void assertFalse(boolean isNotOK) {
	assertTrue ( !isNotOK );
    }

    /**  an assertion failure is thrown if isOK is evaluated to false
     * the text in message is handed over to this exception
     * @param isOK boolean the assertion that is checked
     * @param message String describes the failed assertion
     */
    public static final void assertTrue(boolean isOK, String message) {
	if (ENABLE_ASSERTION) {
	    if (!isOK) {
		fail(message);
	    }
	}
    }

    /**  an assertion failure is thrown if isOK is evaluated to false
     * the text in message is handed over to this exception
     * @param isOK boolean the assertion that is checked
     * @param message String describes the failed assertion
     */
    public static final void assertTrue(boolean isOK, String message, 
					Object parameter) {
	if (ENABLE_ASSERTION) {
	    if (!isOK) {
		fail(message+":"+parameter);
	    }
	}
    }

    public static final void assertFalse(boolean isNotOK, String message) {
	assertTrue ( !isNotOK, message );
    }



    public static final void fail() {
	fail("No further information available.");
    }

    public static final void fail(String message) {
	if (ENABLE_ASSERTION) {
	    throw new AssertionFailure("\nAssertion failure: " + message);
	}
    }

    public static final void fail(String message, Object o) {
	if (ENABLE_ASSERTION) {
	    throw new AssertionFailure("\nAssertion failure: " + message + ":" + o);
	}
    }

    /**
     * prints the stack trace of the given exception
     */
    public static final void printStackTrace(Exception e) {
	if (ENABLE_DEBUG) {
	    e.printStackTrace(System.out);
	}
    }

    /** 
     * Prints a stack trace (without influencing the execution in any way).
     * @author VK
     */
    public static final void printStackTrace() {
       try{throw new Exception();}
       catch(Exception e) { 
          System.out.println("************* DEBUG::Stacktrace *************" );
          e.printStackTrace(System.out);
       }
    }


    public static String stackTrace() {
        Throwable t = new Throwable();
        java.io.ByteArrayOutputStream baos = 
	    new java.io.ByteArrayOutputStream();
        t.printStackTrace(new java.io.PrintStream(baos));
        return(baos.toString());
    }
    
    public static String getCassAndMethod() {
    	try {
    		throw new Exception();
    	} catch(Exception e) {
    		StackTraceElement[] trace = e.getStackTrace();
    		if(trace.length > 1) {
    			int line = trace[1].getLineNumber();
    			return trace[1].getClass() + "." + trace[1].getMethodName() + (line > 0 ? " [line " + line +"]" : ""); 
    		}
    		return "";
    	}        	
    }

}
