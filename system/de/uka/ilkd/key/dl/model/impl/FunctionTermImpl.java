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
/* Generated by Together */

package de.uka.ilkd.key.dl.model.impl;

import de.uka.ilkd.key.dl.model.DLProgramElement;
import de.uka.ilkd.key.dl.model.Expression;
import de.uka.ilkd.key.dl.model.FreeFunction;
import de.uka.ilkd.key.dl.model.Function;
import de.uka.ilkd.key.dl.model.FunctionTerm;
import de.uka.ilkd.key.java.PrettyPrinter;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.reference.ExecutionContext;

import java.io.IOException;

/**
 * Implementation of {@link FunctionTerm}
 * 
 * @author jdq
 * @since Tue Jan 16 16:09:56 CET 2007
 */
public class FunctionTermImpl extends DLNonTerminalProgramElementImpl implements
        FunctionTerm {

    /**
     * Creates a new FunctionTerm with the given function and the given
     * Parameters.
     * 
     * @param f
     *                the function to use
     * @param param
     *                the parameter of the function
     */
    public FunctionTermImpl(Function f, Expression... param) {
		if(f == null) {
			throw new IllegalArgumentException("Function cannot be null");
		}
        addChild(f);
        for (Expression e : param) {
            if (e == null) {
                throw new IllegalArgumentException("Param cannot be null");
            }
            addChild(e);
        }
    }

    /**
     * @see de.uka.ilkd.key.dl.model.impl.DLNonTerminalProgramElementImpl#prettyPrint(de.uka.ilkd.key.java.PrettyPrinter)
     *      prettyPrint
     */
    public void prettyPrint(PrettyPrinter arg0) throws IOException {
        if (getChildAt(0) instanceof FreeFunction || getChildCount() < 3) {
            arg0.printCompoundTerm(this, false);
        } else {
            arg0.printCompoundTerm(this, true);
        }
    }

    /**
     * @see de.uka.ilkd.key.dl.model.impl.DLNonTerminalProgramElementImpl#toString()
     *      toString
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getSymbol() + "(");
        for (int i = 0; i < getChildCount(); i++) {
            result.append(getChildAt(i) + ", ");
        }
        result.append(")");
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.java.ReuseableProgramElement#reuseSignature(de.uka.ilkd.key.java.Services,
     *      de.uka.ilkd.key.java.reference.ExecutionContext)
     */
    public String reuseSignature(Services services, ExecutionContext ec) {
        StringBuilder result = new StringBuilder();
        result.append(getSymbol() + "(");
        for (int i = 0; i < getChildCount(); i++) {
            result.append(((DLProgramElement) getChildAt(i)).reuseSignature(
                    services, ec)
                    + ", ");
        }
        result.append(")");
        return result.toString();
    }
}
