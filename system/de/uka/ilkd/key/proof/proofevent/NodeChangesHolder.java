// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//

package de.uka.ilkd.key.proof.proofevent;

import de.uka.ilkd.key.collection.ImmutableList;
import de.uka.ilkd.key.collection.ImmutableSLList;
import de.uka.ilkd.key.logic.SequentChangeInfo;


public class NodeChangesHolder {
    public ImmutableList<SequentChangeInfo> scis;

    NodeChangesHolder () {
	this ( ImmutableSLList.<SequentChangeInfo>nil() );
    }

    NodeChangesHolder ( ImmutableList<SequentChangeInfo> p_scis ) {
	scis = p_scis;
    }

    public void addSCI ( SequentChangeInfo p_sci ) {
	scis = scis.prepend ( p_sci );
    }

    public Object clone () {
	return new NodeChangesHolder ( scis );
    }
}
