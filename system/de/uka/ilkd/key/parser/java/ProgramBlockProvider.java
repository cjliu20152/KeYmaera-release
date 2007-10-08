/**
 * 
 */
package de.uka.ilkd.key.parser.java;

import java.util.HashSet;

import de.uka.ilkd.key.java.JavaReader;
import de.uka.ilkd.key.java.Recoder2KeY;
import de.uka.ilkd.key.java.SchemaJavaReader;
import de.uka.ilkd.key.java.SchemaRecoder2KeY;
import de.uka.ilkd.key.java.visitor.DeclarationProgramVariableCollector;
import de.uka.ilkd.key.java.visitor.ProgramVariableCollector;
import de.uka.ilkd.key.logic.JavaBlock;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.parser.ParserConfig;
import de.uka.ilkd.key.util.Debug;

/**
 * TODO Documentation of ProgramBlockProvider since 08.01.2007
 * 
 * @author jdq
 * @since 08.01.2007
 * 
 */
public class ProgramBlockProvider implements
		de.uka.ilkd.key.proof.init.ProgramBlockProvider {

	private JavaReader javaReader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uka.ilkd.key.proof.init.ProgramBlockProvider#getProgramBlock(de.uka.ilkd.key.parser.ParserConfig,
	 *      java.lang.String, boolean)
	 */
	public JavaBlock getProgramBlock(ParserConfig config, String programBlock,
			boolean schemaMode, boolean problemParser,
			boolean globalDeclTermParser) {
		JavaReader jr = javaReader;
		if (schemaMode) {
			if (problemParser) // Alt jr==null;
				jr = new SchemaRecoder2KeY(config.services(), config
						.namespaces());
			((SchemaJavaReader) jr).setSVNamespace(config.namespaces()
					.variables());
		} else {
			if (problemParser) // Alt jr==null;
				jr = new Recoder2KeY(config.services(), config.namespaces());
		}

		if (schemaMode || globalDeclTermParser) {
			return jr.readBlockWithEmptyContext(programBlock);
		} else {
			return jr.readBlockWithProgramVariables(config.namespaces()
					.programVariables(), programBlock);
		}

	}

	public HashSet getProgramVariables(JavaBlock programBlock,
			NamespaceSet nss, boolean globalDeclTermParser, boolean declParser,
			boolean termOrProblemParser) {
		if (globalDeclTermParser) {
			ProgramVariableCollector pvc = new ProgramVariableCollector(
					programBlock.program());
			pvc.start();
			return pvc.result();
		} else if (!declParser) {
			if (termOrProblemParser
					&& programBlock == JavaBlock.EMPTY_JAVABLOCK) {
				return new HashSet();
			}
			DeclarationProgramVariableCollector pvc = new DeclarationProgramVariableCollector(
					programBlock.program());
			pvc.start();
			return pvc.result();
		}
		Debug
				.fail("KeYParser.progVars(): this statement should not be reachable.");
		return null;
	}

	/**
	 * Get javaReader.
	 * 
	 * @return javaReader as JavaReader.
	 */
	public JavaReader getJavaReader() {
		return javaReader;
	}

	/**
	 * Set javaReader.
	 * 
	 * @param javaReader
	 *            the value to set.
	 */
	public void setJavaReader(JavaReader javaReader) {
		this.javaReader = javaReader;
	}
}