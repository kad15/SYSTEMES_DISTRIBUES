/*
* ###########################################################################
* JavAct: A Java(TM) library for distributed and mobile actor-based computing
* Copyright (C) 2001-2007 I.R.I.T./C.N.R.S.-I.N.P.T.-U.P.S.
*
* This library is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation; either version 2.1 of the License, or any
* later version.
*
* This library is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
* License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this library; if not, write to the Free Software Foundation,
* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
*
* Initial developer(s): SL / The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

/**
 * JavAct run-time exception, superclass for all library-level 
 * exceptions which can be thrown to the application level
 *
 * @author SL
 * @version 0.5.0
 */
public class JavActException extends RuntimeException {
    
    /**
     * Defaut constructor for a JavAct runtime exception
     *
     */
    public JavActException() {
        super();
    }

    /**
     * Constructor for a JavAct runtime exception, with specified message and cause
     * @param m
     * @param cause
     */
    public JavActException(String m, Throwable cause) {
        super(m, cause);
    }

    /**
     * Constructor for a JavAct runtime exception, with specified cause
     * @param cause
     */
    public JavActException(Throwable cause) {
        super(cause);
    }
}
