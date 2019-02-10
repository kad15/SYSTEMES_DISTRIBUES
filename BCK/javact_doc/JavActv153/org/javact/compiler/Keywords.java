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
* Initial developer(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contributor(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.compiler;

/**
 * This class contains the definition of JavAct terms external to the compiler
 * and needed by it.
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
class Keywords
{
    /** Prefix for the names of asynchronous messages. */
    static final String ASYNC_MSG = "JAM";

    /** Prefix for the names of synchronous messages. */
    static final String SYNC_MSG = "JSM";

    /**
     * Name of the method that will be invoked when the message will be
     * processed, that is when it will be extracted from the actor's queue.
     */
    static final String INVOKE = "handle";

    /** Name of the JavAct package */
    static final String JAVACT_PACKAGE = "org.javact.lang";


    /** Name of the <code>QuasiBehavior</code> abstract class. */
    static final String QUASI_BEHAVIOR = "QuasiBehavior";

    /** Name of the <code>become</code> method. This method is declared in
     * behavior profiles to give the possible future behavior profiles from
     * this one
     */
    static final String BECOME = "become";

    /** String for the ActorProfile definition pattern. See {@link
     * Driver#findActorProfiles}. */
    static final String ROLE_PATTERN = "ActorProfile";

    /** Name of the <code>ActorProfile</code> marking interface. */
    static final String ROLE = "org.javact.util.ActorProfile";

    /** Class object corresponding to the <code>ActorProfile</code> marking
     * interface. */
    static final Class ROLE_INTERFACE;


    /** Name of the <code>BehaviorProfile</code> marking interface. */
    static final String BEHAVIOR = "org.javact.util.BehaviorProfile";

    /** Class object corresponding to the <code>BehaviorProfile</code>. */
    static final Class BEHAVIOR_INTERFACE;


    /* Initialization of the ROLE_INTERFACE and BEHAVIOR_INTERFACE fields. */
    static {
	Class temp = null;

	try
	{
	    temp = Class.forName(ROLE);
	}
	catch (ClassNotFoundException exn)
	{
	    System.err.println(
		"Fatal error: unable to find interface "
		    + ROLE
		    + ". Aborting.");
	    System.exit(1);
	}

	/* ROLE_INTERFACE must be assigned to even if the class is not found,
	 * or a compile-time error occurs */
	ROLE_INTERFACE = temp;

	try
	{
	    temp = Class.forName(BEHAVIOR);
	}
	catch (ClassNotFoundException exn)
	{
	    System.err.println(
		"Fatal error: unable to find interface "
		    + BEHAVIOR
		    + ". Aborting.");
	    System.exit(1);
	}

	BEHAVIOR_INTERFACE = temp;
    }
}
