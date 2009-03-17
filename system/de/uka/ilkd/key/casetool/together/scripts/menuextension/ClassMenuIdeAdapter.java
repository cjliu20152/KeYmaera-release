// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//

/* Generated by Together */


package de.uka.ilkd.key.casetool.together.scripts.menuextension;

import com.togethersoft.openapi.ide.IdeContext;
import com.togethersoft.openapi.ide.command.IdeCommandAdapter;
import com.togethersoft.openapi.ide.command.IdeCommandEvent;
import com.togethersoft.openapi.ide.diagram.IdeDiagramManagerAccess;
import com.togethersoft.openapi.ide.window.IdeWindowManager;
import com.togethersoft.openapi.rwi.RwiDiagram;
import com.togethersoft.openapi.rwi.RwiModel;
import com.togethersoft.openapi.rwi.RwiModelAccess;

import de.uka.ilkd.key.casetool.together.keydebugclassloader.KeyDebugClassLoader;

public class ClassMenuIdeAdapter extends IdeCommandAdapter{

    public ClassMenuIdeAdapter(KeyMenuExtension script, 
			       IdeWindowManager aWinMan, 
			       int ind1,  String className) {
	lnkKeyMenuExtension = script;
	winMan = aWinMan;
        groupElementNo = ind1;
	classMenuRootCN = className;
    }

   private int groupElementNo;
    private String classMenuRootCN;
    private IdeWindowManager winMan;
    private KeyMenuExtension lnkKeyMenuExtension;



    public void checkStatus(IdeCommandEvent event) {
	try{
	    lnkKeyMenuExtension.myClassItem[groupElementNo-1].setText(((ClassMenu) Class.forName(classMenuRootCN + "Point" + groupElementNo).newInstance()).getMenuEntry());
	} catch (ExceptionInInitializerError ei) {
	    System.err.println("classmenuideadapter: the initialization provoked "+
			       "by this method fails.");
	    System.err.println("The exception was: "+ei);
	    ei.printStackTrace();
	} catch (ClassNotFoundException cnfe) {
	    System.err.println("classmenuideadapter: class cannot be located");
	    System.err.println("The exception was: "+cnfe);
	    cnfe.printStackTrace();
	} catch (IllegalAccessException iae) {
	    System.err.println("classmenuideadapter: class or "+
			       "initializer is not accessible."); 
	    System.err.println("The exception was: "+iae); 
	    iae.printStackTrace(); 
	} catch (InstantiationException ie) { 
	    System.err.println
		("classmenuideadapter: class tried to\n"+
		 "instantiate represents an abstract class, an interface,"+
		 "an array class, a primitive type, or void; or if the"+
		 "instantiation fails for some other reason.");  
	    System.err.println("The exception was: "+ie);  
	    ie.printStackTrace();
	} catch (SecurityException se) {
	    System.err.println("classmenuideadapter: no permission to create"+
			       "a new instance"); 
	    System.err.println("The exception was: "+se); 
	    se.printStackTrace(); 
	} catch (LinkageError le) {
	    System.err.println("classmenuideadapter: linkage failed");
	    System.err.println("The exception was: "+le);
	    le.printStackTrace();
	}

	lnkKeyMenuExtension.myClassItem[groupElementNo-1].setEnabled(true);
	lnkKeyMenuExtension.myClassItem[groupElementNo-1].setVisible(true);
    }

    public void actionPerformed(IdeCommandEvent event) { 
	// what should be done be selecting this context menu
	IdeContext context = event.getElementContext(); //getting the element(s) under the cursor
	RwiDiagram activeRwiDiagram = IdeDiagramManagerAccess.getDiagramManager().getActiveDiagram().getRwiDiagram();
	RwiModel currentRwiModel = RwiModelAccess.getModel();


	Class cl;
	ClassLoader classLoader = this.getClass().getClassLoader();
	if ("on".equals(System.getProperty("KeyDebugClassLoader"))){
	    // installing the keydebugclassloader instead
	    // see also de.uka.ilkd.tjext.keydebugclassloader.KeyScript
	    try{
		classLoader = new KeyDebugClassLoader(classLoader);
		((KeyDebugClassLoader) classLoader).setClassAlwaysAskParent(ClassMenu.class);
	    } catch(SecurityException se){
		System.err.println("classmenuideadapter: security manager exists and\n"+
				   "its checkPermission method doesn't allow access\n"+
				   "to the system class loader.");
		System.err.println("KeYError: " + se);
		se.printStackTrace();
	    }
	}
	try{
	    cl = classLoader.loadClass(classMenuRootCN + "Point" + groupElementNo);
	    // now creating an instance of the (re)loaded class and 
	    // invoking the run-method
 	    ClassMenu instance = (ClassMenu) cl.newInstance();
 	    instance.run(winMan, context, currentRwiModel, activeRwiDiagram);
	} catch (ClassNotFoundException cnfe) {
	    System.err.println("classmenuideadapter: class cannot be located");
	    System.err.println("The exception was: "+cnfe);
	    cnfe.printStackTrace();
	} catch (ExceptionInInitializerError ei){
	    System.err.println("classmenuideadapter: the initialization provoked "+
			       "by this method fails.");
	    System.err.println("The exception was: "+ei);
	    ei.printStackTrace();
	} catch (IllegalAccessException iae) {
	    System.err.println("classmenuideadapter: class or "+
			       "initializer is not accessible."); 
	    System.err.println("The exception was: "+iae); 
	    iae.printStackTrace(); 
	} catch (InstantiationException ie) { 
	    System.err.println
		("classmenuideadapter: class tried to\n"+
		 "instantiate represents an abstract class, an interface,"+
		 "an array class, a primitive type, or void; or if the"+
		 "instantiation fails for some other reason.");  
	    System.err.println("The exception was: "+ie);  
	    ie.printStackTrace();
	} catch (SecurityException se) {
	    System.err.println("classmenuideadapter: no permission to create"+
			       "a new instance"); 
	    System.err.println("The exception was: "+se); 
	    se.printStackTrace(); 
	}

    }



}

