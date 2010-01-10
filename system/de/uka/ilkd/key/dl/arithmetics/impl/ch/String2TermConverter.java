package de.uka.ilkd.key.dl.arithmetics.impl.ch;

import de.uka.ilkd.key.dl.parser.QepcadLexer;
import de.uka.ilkd.key.dl.parser.QepcadParser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.strategy.termProjection.TermBuffer;

/**
 * Converts an given String to a Term-Instance
 * using the Qepcad-Parser. (uses Grammar: Qepcad.g)
 * 
 * @author Timo Michelsen
 *
 */
public class String2TermConverter {

    /**
     * Convert-implementation
     * 
     * @param formula Formula to parse
     * @param nss Namespace
     * @return Terminstance
     */
    public static Term convert(String formula, NamespaceSet nss) {
    	formula = formula.replaceAll(Term2CHConverter.USCOREESCAPE, "_");
    	formula = formula.replaceAll(Term2CHConverter.DOLLARESCAPE, "$");
    	System.out.println("replaced result is: " + formula);//XXX
    	if(formula.equalsIgnoreCase("TRUE")) {
    		return TermBuilder.DF.tt();
    	} else if(formula.equalsIgnoreCase("FALSE")) {
    		return TermBuilder.DF.ff();
    	}
        QepcadLexer lexer = new QepcadLexer(new ANTLRStringStream(formula));
        CommonTokenStream tok = new CommonTokenStream(lexer);
        QepcadParser parser = new QepcadParser(tok);
        parser.setNamespaceSet(nss);

        // TODO: implement better exeption-handling
        Term result = null;
        try {
            result = parser.formula();
            // System.out.println("PARSER: Parsing successful");
        } catch (RecognitionException e) {
            e.printStackTrace();
        }

        return result;
    }

}
