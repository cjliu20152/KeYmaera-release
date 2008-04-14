// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2005 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//
package de.uka.ilkd.key.java.recoderext;

import recoder.CrossReferenceServiceConfiguration;
import recoder.ProgramFactory;
import recoder.io.*;
import recoder.service.*;
import de.uka.ilkd.key.java.KeYProgModelInfo;
import de.uka.ilkd.key.util.KeYExceptionHandler;

public class KeYCrossReferenceServiceConfiguration 
    extends CrossReferenceServiceConfiguration{

    protected KeYProgModelInfo kpmi = null;

    public KeYCrossReferenceServiceConfiguration(KeYExceptionHandler keh ) {
	super(); // initialises servConf
	// better not: it might add to the input path of recoder
	// getProjectSettings().ensureSystemClassesAreInPath();
	assert keh != null : "The exception handler must not be null";
	getProjectSettings().setErrorHandler( (recoder.service.ErrorHandler)(keh) );
    }

    public KeYCrossReferenceServiceConfiguration(KeYProgModelInfo kpmi) {
	this(kpmi.getExceptionHandler());
	this.kpmi = kpmi;
    }

    // TODO  MU they only call super.samename. cant we delete all these methods?
    protected void makeServices() { 
	super.makeServices();
    }

    protected void initServices() { 
	super.initServices();	
    }

    protected ProgramFactory makeProgramFactory() {
	return ProofJavaProgramFactory.getInstance();
    }

    protected ChangeHistory makeChangeHistory() {
	return new ChangeHistory(this);
    }

    /**
       The cross reference source info is a subclass of the source info,
       so this class simply overrides the source info factory method.
     */
    protected SourceInfo makeSourceInfo() {
 	return new KeYCrossReferenceSourceInfo(this);
    }  

    protected ProjectSettings makeProjectSettings() {
	return new ProjectSettings(this);
    }

    protected SourceFileRepository makeSourceFileRepository() {
	return new DefaultSourceFileRepository(this);
    }
    
    protected ClassFileRepository makeClassFileRepository() {
	return new DefaultClassFileRepository(this);
    }

    protected ByteCodeInfo makeByteCodeInfo() {
	return new DefaultByteCodeInfo(this);
    }

    protected ImplicitElementInfo makeImplicitElementInfo() {
	return new DefaultImplicitElementInfo(this);
    }

    protected NameInfo makeNameInfo() {
	return super.makeNameInfo();
    }

    protected ConstantEvaluator makeConstantEvaluator() {
	return new DefaultConstantEvaluator(this);
    }

    public KeYProgModelInfo getKeYProgModelInfo(){
	return kpmi;
    }
}
