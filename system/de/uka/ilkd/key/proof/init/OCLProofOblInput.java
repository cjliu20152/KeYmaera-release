// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//
package de.uka.ilkd.key.proof.init;

import java.util.Iterator;

import de.uka.ilkd.key.casetool.ModelClass;
import de.uka.ilkd.key.casetool.ModelManager;
import de.uka.ilkd.key.java.Recoder2KeY;
import de.uka.ilkd.key.logic.Named;
import de.uka.ilkd.key.logic.op.Function;
import de.uka.ilkd.key.logic.op.ProgramVariable;
import de.uka.ilkd.key.proof.ProofAggregate;
import de.uka.ilkd.key.proof.RuleSource;

/** Represents the proof obligation input for the prover as an OCL 
 * constraint in textual format.
 * @deprecated Replaced by AbstractPO.
 */
public abstract class OCLProofOblInput implements ProofOblInput {

    /** The initial configuration for a prover to use for reading and modify*/
    protected InitConfig initConfig = null;
    private String name;
    private String proofHeader;

    private static ModelManager modelManager;

    public static void setModelManager(ModelManager modMan) {
	if (modelManager==null) {
	    modelManager = modMan;
	}
    }

    public static ModelManager getModelManager() {
	return modelManager;
    }

    /** Creates a new representation of an OCL input with a name describing
     * the kind of input.
     */
    public OCLProofOblInput(String name) {
	this.name=name;
    }


    /** returns the problems as generated by reading the OCL constraint
     * and translating it to DL formulas.
     */
    public abstract ProofAggregate getPO();
    
    
    public boolean implies(ProofOblInput po) {
        return equals(po);
    }
    
    
    public void readActivatedChoices() throws ProofInputException{
	//nothing to do
    }

    public Includes readIncludes() throws ProofInputException{
	RuleSource standard = RuleSource.initRuleFile("standardRules.key");
	Includes includes = new Includes();
	includes.put("standardRules", standard);
	return includes;
    }
    

    String getProofHeader(String javaPath) {
        if (proofHeader==null) {
            proofHeader=createProofHeader(initConfig, javaPath);
        }
        return proofHeader;
    }
    
    /** creates declarations necessary to save/load proof in textual form */
    static String createProofHeader(InitConfig initConfig, String javaPath) {
        String s;
        s = "\\javaSource \""+javaPath+"\";\n\n";

        Iterator<Named> it;

/* program sorts need not be declared and 
 * there are no user-defined sorts with this kind of PO (yes?)
        s += "sorts {\n"; // create declaration header for the proof
        it = initConfig.sortNS().getProtocolled();
        while (it.hasNext()) {
	    String n=it.next().toString();
	    int i;
	    if ((i=n.indexOf("."))<0 || 
		initConfig.sortNS().lookup(new Name(n.substring(0,i)))==null) {
		//the line above is for inner classes.
		//KeY does not want to handle them anyway...
		s = s+"object "+n+";\n";
	    }
	}
        s+="}
*/
        s+="\n\n\\programVariables {\n";
        it = initConfig.progVarNS().getProtocolled();
        while (it.hasNext()) 
           s = s+((ProgramVariable)(it.next())).proofToString();

        s+="}\n\n\\functions {\n";
        it = initConfig.funcNS().getProtocolled();
        while (it.hasNext()) {
            Function f = (Function)it.next();
            // only declare @pre-functions, others will be generated automat.
            if (f.name().toString().indexOf("@pre")!=-1) {
                s += f.proofToString();
            }
        }

        s+="}\n\n";
        return s;
    }


    /** returns the initial configuration that is used to read the OCL
     * input and that is used to be modified during reading.
     */
    public InitConfig getInitConfig() {
	return initConfig;
    }

    /** reads the OCL constraint and modifies the initial prover configuration
     * according to the given modification strategy.
     */
    public abstract void readProblem(ModStrategy mod) throws ProofInputException;

    /** returns false, that is the input never asks the user which
     * environment he prefers
     */
    public boolean askUserForEnvironment() {
	return false;
    }

    /** sets the initial configuration that is used to read the OCL
     * input and that is used to be modified during reading.
     */
    public void setInitConfig(InitConfig conf) {
	this.initConfig=conf;
    }

    public void startProtocol() {
        initConfig.sortNS().startProtocol();
        initConfig.funcNS().startProtocol();
        initConfig.progVarNS().startProtocol();	
    }

    /** returns the name of the OCL proof obligation input.
     */
    public String name() {
	return name;
    }

    public abstract ModelClass getModelClass();

    /** computes the method specifications and stores the results in the
     * SpecificationRepository which belongs to the ProofEnvironment of InitCfg
     */
    public void readSpecs(){
	// do nothing, class obsolete
    }


    protected Recoder2KeY getKeYJavaASTConverter() {       
        return new Recoder2KeY(initConfig.getServices(), 
                initConfig.namespaces());
    }
    

    public String getJavaPath() {
        return getModelClass().getRootDirectory();
    }


     /**
     * Converts an empty string to "true" and keeps it unchanged otherwise.
     */
    public static String normalizeConstraint(String aText) {
        if (aText.equals("")){
            return "true";
        }else{
            return aText;
        }
    }


}
