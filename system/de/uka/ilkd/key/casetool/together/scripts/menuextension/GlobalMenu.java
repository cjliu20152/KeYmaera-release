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

// All implementing classes should have a name of 
// "GlobalMenu" + ...  

package de.uka.ilkd.key.casetool.together.scripts.menuextension;

import com.togethersoft.openapi.ide.window.IdeWindowManager;



public interface  GlobalMenu {
    String getMenuEntry();

    void run(IdeWindowManager winMan);

}
