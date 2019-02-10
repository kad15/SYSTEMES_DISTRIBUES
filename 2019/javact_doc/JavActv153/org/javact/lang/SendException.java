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
* Initial developer(s): Sébastien LERICHE / The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

/**
 * A SendException is thrown when a RuntimeException occurs when a send fails.

 * @author SL
 * @version 0.5.0
 */
public class SendException extends JavActException {

	/**
	 * Default send runtime exception
	 */
	public SendException() {
		super();
	}

	/**
	 * Send runtime exception, with specified error message and cause 
	 * @param m an error message
	 * @param cause the cause of this exception, null if unknown
	 */
	public SendException(String m, Throwable cause) {
		super(m, cause);
	}

}
