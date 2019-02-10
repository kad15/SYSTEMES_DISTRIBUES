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

import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * Class that implements the <code>Message</code> class and provides the
 * handling of asynchronous messages.
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
class AsynchronousMessage extends Message
{
    /**
     * Creates a new message object.
     *
     * @param _method The method corresponding to the message
     * @param _owningBehavInterface The behavior interface that declares this
     *	      message
     */
    AsynchronousMessage(Method _method, Class _owningBehavInterface)
    {
	super(_method, _owningBehavInterface);
	messageClassName = Keywords.ASYNC_MSG + name;
    }

    /**
     * Returns the name of the class that will implement the handling of this
     * message.
     *
     * @return DOCUMENT ME!
     */
    String getMessageClassName()
    {
	return messageClassName;
    }

    /**
     * Prints out the line declaring the class that will handle this message.
     *
     * @param out DOCUMENT ME!
     */
    protected void printClassDeclaration(PrintWriter out)
    {
	out.println( "public class " + getMessageClassName()
		+ " implements org.javact.lang.Message");
	out.println("{");
    }

    /**
     * Prints out the call of the method implementing this message.
     *
     * @param out DOCUMENT ME!
     * @param behavInterfName DOCUMENT ME!
     * @param argsList DOCUMENT ME!
     */
    void printCall(PrintWriter out, String behavInterfName, String argsList)
    {
	out.println("if (_behavior instanceof " + behavInterfName + ")");
	out.println( "\t\t\t\t\t((" + behavInterfName + ") _behavior)." + name
		+ "(" + argsList + ") ;");
    }

    /**
     * Finishes the generation of the class handling this message. It justs
     * prints out the final closing brace and closes the stream.
     *
     * @param out DOCUMENT ME!
     */
    protected void printEndOfGeneration(PrintWriter out)
    {
	/* In the case of an asynchronous message we have nothing to do -
	 * just print the end of the class brace an close the stream */
	out.println("}");

	/* Well, vim is a great text editor - print a modeline for automagic
	 * setting of the tabstop option */
	// out.println("/* vim: set ts=4: */");
	out.close();
    }
}
