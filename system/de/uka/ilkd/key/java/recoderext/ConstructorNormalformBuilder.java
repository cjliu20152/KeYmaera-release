// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2005 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2004 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
package de.uka.ilkd.key.java.recoderext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import recoder.CrossReferenceServiceConfiguration;
import recoder.abstraction.ClassType;
import recoder.abstraction.Constructor;
import recoder.java.CompilationUnit;
import recoder.java.Expression;
import recoder.java.Statement;
import recoder.java.StatementBlock;
import recoder.java.declaration.*;
import recoder.java.declaration.modifier.Private;
import recoder.java.declaration.modifier.Public;
import recoder.java.expression.operator.CopyAssignment;
import recoder.java.reference.*;
import recoder.kit.ProblemReport;
import recoder.list.generic.*;
import de.uka.ilkd.key.proof.mgt.SpecificationRepository;
import de.uka.ilkd.key.util.Debug;

/**
 * Transforms the constructors of the given class to their
 * normalform. The constructor normalform can then be accessed via a
 * methodcall <code>&lt;init&gt;<cons_args)</code>. The visibility of
 * the normalform is the same as for the original constructor.
 */
public class ConstructorNormalformBuilder 
    extends RecoderModelTransformer {

    public static final String 
	CONSTRUCTOR_NORMALFORM_IDENTIFIER = "<init>";

    public static final String 
	OBJECT_INITIALIZER_IDENTIFIER = "<objectInitializer>";
        
    private HashMap<TypeDeclaration, List<Constructor>> class2constructors;
    private HashMap<TypeDeclaration, ASTList<Statement>> class2initializers;
    private HashMap<TypeDeclaration, ASTList<MethodDeclaration>> class2methodDeclaration;

    private ClassType javaLangObject;

    /** creates the constructor normalform builder */
    public ConstructorNormalformBuilder
	(CrossReferenceServiceConfiguration services, 
	 List<CompilationUnit> units) {	
	super(services, units);
	class2constructors = new HashMap<TypeDeclaration, List<Constructor>>(4*units.size());
	class2initializers = new HashMap<TypeDeclaration, ASTList<Statement>>(10*units.size());
	class2methodDeclaration = new HashMap<TypeDeclaration, ASTList<MethodDeclaration>>(10*units.size());
    }


    /**
     * The list of statements is the smallest list that contains a copy
     * assignment for each instance field initializer of class cd,
     * e.g. <code> i = 0; </code> for <code> public int i = 0; </code> or
     * a reference to the private method
     * <code>&lt;objectInitializer&gt;<i>i</i> refering to the i-th object
     * initializer of cd. These private declared methods are created on
     * the fly. Example for 
     *  <code> 
     *    class C { 
     *        int i = 0; 
     *        { 
     *            int j = 3; 
     *            i = j + 5;
     *        }
     *      
     *        public C () {} ...
     *   }
     *  </code> the following list of size two is returned
     *  <code> 
     *   [ i = 0;,  &lt;objectInitializer&gt;0(); ]
     *  </code>
     *  where <code>
     *    private &lt;objectInitializer&gt;0() {
     *       int j = 3; 
     *       i = j + 5;
     *    }
     *  </code>
     * @param cd the ClassDeclaration of which the initilizers have to
     * be collected
     * @return the list of copy assignments and method references
     * realising the initializers. 
     */
    private ASTList<Statement> collectInitializers(ClassDeclaration cd) {
	ASTList<Statement> result = new ASTArrayList<Statement>(20);
	ASTList<MethodDeclaration> mdl = new ASTArrayList<MethodDeclaration>(5);
	int childCount = cd.getChildCount();
	for (int i = 0; i<childCount; i++) {
	    if (cd.getChildAt(i) instanceof ClassInitializer &&
		!((ClassInitializer)cd.getChildAt(i)).isStatic()) {

		ASTList<DeclarationSpecifier> mods = new ASTArrayList<DeclarationSpecifier>(1);
		mods.add(new Private());
		String name = OBJECT_INITIALIZER_IDENTIFIER + mdl.size();
		MethodDeclaration initializerMethod = 
		    new MethodDeclaration
		    (mods,
		     null, //return type is void
		     new ImplicitIdentifier(name),
		     new ASTArrayList<ParameterDeclaration>(0),
		     null,
		     (StatementBlock)
		     ((ClassInitializer)cd.getChildAt(i)).getBody().deepClone());		
		initializerMethod.makeAllParentRolesValid();
		mdl.add(initializerMethod);
		result.add(new MethodReference
			   (null,
			    new ImplicitIdentifier(name)));			   
	    } else if (cd.getChildAt(i) instanceof FieldDeclaration &&
		       !((FieldDeclaration)cd.getChildAt(i)).isStatic()) {
		ASTList<FieldSpecification> specs =
		    ((FieldDeclaration)cd.getChildAt(i)).getFieldSpecifications();
		for (int j = 0; j < specs.size(); j++) {
		    Expression fieldInit = null;
		    if ((fieldInit = specs.get(j).			 
			 getInitializer()) != null) {
			CopyAssignment fieldCopy = 
			    new CopyAssignment
			    (new FieldReference
			     (new ThisReference(), 
			      specs.get(j).getIdentifier()),
                              (Expression)fieldInit.deepClone());
			result.add(fieldCopy);
		    }
		}
	    }
	}
	class2methodDeclaration.put(cd, mdl);
	return result;
    }
    
    /**
     * Two-pass transformation have to be strictly divided up into two
     * parts. the first part analyzes the model and collects all
     * necessary information. In this case all class declarations are
     * examined and initializers as well as constructors are collected. 
     *   All actions, which may cause a recoder model update have to be
     * done here.
     * @return status report if analyze encountered problems or not
     */
    public ProblemReport analyze() {
        javaLangObject = services.getNameInfo().getJavaLangObject();
	 if (!(javaLangObject instanceof ClassDeclaration)) {
	     Debug.fail("Could not find class java.lang.Object or only as bytecode");
	 }
        for (int unit = 0; unit<units.size(); unit++) {
	    CompilationUnit cu = units.get(unit);
	    int typeCount = cu.getTypeDeclarationCount();
	
	    for (int i = 0; i < typeCount; i++) {
		if (cu.getTypeDeclarationAt(i) instanceof ClassDeclaration)
		    { 
			ClassDeclaration cd = (ClassDeclaration)
			    cu.getTypeDeclarationAt(i);
			if (cd.getTypeDeclarationCount()>0) {
			    Debug.out
				("consNFBuilder: Inner Class detected." + 
				 "No constructor normalform will be built" +
				 "for the inner classes of "+cd.getIdentifier());
			}
			
			// collect constructors for transformation phase
			List<Constructor> constructors = 
			    new ArrayList<Constructor>(10);
			constructors.addAll(services.getSourceInfo().getConstructors(cd));
			class2constructors.put(cd, constructors);
						
			// collect initializers for transformation phase
			class2initializers.put(cd, collectInitializers(cd));
		    }
	    }	
	}
	setProblemReport(NO_PROBLEM);
	return NO_PROBLEM;
    }
    

    /**
     * Creates the normalform of the given constructor, that is declared
     * in class cd. For a detailed description of the normalform to be
     * built see the KeY Manual.
     * @param cd the ClassDeclaration where the cons is declared
     * @param cons the Constructor to be transformed
     * @return the constructor normalform
     */
    private MethodDeclaration normalform(ClassDeclaration cd, 
					 Constructor cons) {	
	
	ASTList<DeclarationSpecifier> mods = new ASTArrayList<DeclarationSpecifier>(5);
	ASTList<ParameterDeclaration> parameters;
	Throws recThrows;
	StatementBlock body;
	
	if (!(cons instanceof ConstructorDeclaration)) {
	    mods.add(new Public());
	    parameters = new ASTArrayList<ParameterDeclaration>(0);
	    recThrows = null;
	    body = null;    
	} else {
	    ConstructorDeclaration consDecl = (ConstructorDeclaration)cons;
	    mods = (ASTList<DeclarationSpecifier>)
		(consDecl.getDeclarationSpecifiers()==null ? null : consDecl.getDeclarationSpecifiers().deepClone());	    
	    parameters = 
		(ASTList<ParameterDeclaration>)consDecl.getParameters().deepClone();
	    recThrows = (Throws) (consDecl.getThrown() == null ? null : 
				  consDecl.getThrown().deepClone());
            
	    StatementBlock origBody = consDecl.getBody();
            if(origBody == null) // may happen if a stub is defined with an empty constructor
                body = null;
            else
                body = (StatementBlock) origBody.deepClone();
	}

	if (cd != javaLangObject && body != null) {
	    // remember original first statement
	    Statement first = body.getStatementCount() > 0 ?
		body.getStatementAt(0) : null;
	    
	    // first statement has to be a this or super constructor call	
	    if (!(first instanceof SpecialConstructorReference)) {
		if (body.getBody() == null) {
		    body.setBody(new ASTArrayList<Statement>());
		}
		attach(new MethodReference
		    (new SuperReference(), new ImplicitIdentifier
			(CONSTRUCTOR_NORMALFORM_IDENTIFIER)), body, 0);
	    } else {
		body.getBody().remove(0);
		ReferencePrefix referencePrefix;
		referencePrefix = 
		    first instanceof ThisConstructorReference ?
		    (ReferencePrefix)new ThisReference() : 
		    (ReferencePrefix)new SuperReference();		
		attach(new MethodReference
		    (referencePrefix, new ImplicitIdentifier
			(CONSTRUCTOR_NORMALFORM_IDENTIFIER), 
		     ((SpecialConstructorReference)first).getArguments()), body, 0);
	    }
	    // if the first statement is not a this constructor reference
	    // the instance initializers have to be added in source code
	    // order
	    if (!(first instanceof ThisConstructorReference)) {
		ASTList<Statement> initializers = class2initializers.get(cd);
		for (int i = 0; i<initializers.size(); i++) {
		    attach((Statement) 
			   initializers.get(i).deepClone(),
			   body, i+1);
		}
	    }
	}

	
	MethodDeclaration nf =  new MethodDeclaration
	    (mods,
	     new TypeReference(cd.getIdentifier()),
	     new ImplicitIdentifier(CONSTRUCTOR_NORMALFORM_IDENTIFIER),
	     parameters,
	     recThrows,
	     body);
	nf.makeAllParentRolesValid();
	return nf;
    }
      
    /**
     * entry method for the constructor normalform builder
     * @param td the TypeDeclaration 
     */
    protected void makeExplicit(TypeDeclaration td) {
	if (td instanceof ClassDeclaration) {
	    List<Constructor> constructors = class2constructors.get(td);
	    for (int i = 0; i < constructors.size(); i++) {
		attach(normalform
		       ((ClassDeclaration)td, 
			constructors.get(i)), td, 0);
	    }	    

	    ASTList<MethodDeclaration> mdl = class2methodDeclaration.get(td);
	    for (int i = 0; i < mdl.size(); i++) {
		attach(mdl.get(i), td, 0);
	    }

/*  	    java.io.StringWriter sw = new java.io.StringWriter();
//  	    //services.getProgramFactory().getPrettyPrinter(sw).visitMethodDeclaration(nf);
  	    services.getProgramFactory().getPrettyPrinter(sw).visitClassDeclaration((ClassDeclaration)td);
  	    System.out.println(sw.toString());
  	    try { sw.close(); } catch (Exception e) {}		*/
	}


    }
    
    


}
