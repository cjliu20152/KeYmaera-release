/***************************************************************************
 *   Copyright (C) 2007 by Jan David Quesel                                *
 *   quesel@informatik.uni-oldenburg.de                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
/**
 * 
 */
package de.uka.ilkd.key.dl.formulatools;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermFactory;
import de.uka.ilkd.key.logic.op.ArrayOfQuantifiableVariable;
import de.uka.ilkd.key.logic.op.RigidFunction;

/**
 * TODO jdq documentation since Sep 27, 2007
 * 
 * @author jdq
 * @since Sep 27, 2007
 * 
 */
public class TermRewriter {

	public static class Match {
		public Match(RigidFunction op, Term var) {
			this.skolemFunction = op;
			this.rewriteTo = var;
			assert skolemFunction.isSkolem();
		}

		RigidFunction skolemFunction;
		Term rewriteTo;
	}

	public static Term replace(Term term, Set<Match> matches) {
		return replace(term, matches, new boolean[] { false });
	}

	private static Term replace(Term term, Set<Match> matches,
			boolean[] hasChanged) {
		for (Match m : matches) {
			if (term.op() == m.skolemFunction) {
				hasChanged[0] = true;
				return m.rewriteTo;
			}
		}
		boolean[] thisHasChanged = new boolean[] { false };
		List<Term> arguments = new LinkedList<Term>();
		for (int i = 0; i < term.arity(); i++) {
			arguments.add(replace(term.sub(i), matches, thisHasChanged));
		}
		if (thisHasChanged[0]) {
			hasChanged[0] = true;
			return TermFactory.DEFAULT
					.createTerm(term.op(), arguments.toArray(new Term[0]),
							new ArrayOfQuantifiableVariable[] { term
									.varsBoundHere(0) }, null);
		} else {
			return term;
		}
	}

}
