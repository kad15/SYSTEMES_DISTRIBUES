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
/* Changelog:
 *
 * 2004-06-17  version 0.5.1
 *     bug fix: messages parameters that are arrays are now correctly printed
 *     out
 */
package org.javact.compiler;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class provides tools for manipulating a message signature. A message
 * signature corresponds to a list of given formal parameters, and does not
 * include a return type (see {@link Message}). A <code>Signature</code>
 * instance contains an array of the formal parameters and a set of all the
 * behavior interfaces declaring a method with such a signature (and whose
 * return type is the one of the <code>Message</code> object containing this
 * signature).
 *
 * @author Vincent Hennebert
 * @version 0.5.1
 */
class Signature
{
    /** The message that owns this signature. */
    private Message owningMessage;

    /**
     * The set of behavior interfaces that declare a message with such a
     * signature.
     */
    private Set owningBehavInterfSet;

    /** The types of the formal parameters of the message. */
    Class[] parameterTypes;

    Signature(Method _method, Class _owningInterface, Message _owningMessage)
    {
	owningMessage = _owningMessage;
	owningBehavInterfSet = new HashSet();
	owningBehavInterfSet.add(_owningInterface);
	parameterTypes = _method.getParameterTypes();
    }

    /**
     * Returns true if the object is equal to the signature. If the object is
     * an instance of <code>Method</code> then it is equal to the signature if
     * the formal parameters are the same. Note that the return type does not
     * matter.<br>
     * In all other cases the answer is true if both objects are physically
     * equal (<code>this == _object</code>).
     *
     * @param _object the object to compare with this signature.
     *
     * @return <code>true</code> if the object is equal to the signature.
     */
    public boolean equals(Object _object)
    {
	boolean result = false;

	if (_object instanceof Method)
	{
	    result =
		Arrays.equals(
		    parameterTypes,
		    ((Method) _object).getParameterTypes());
	}
	else
	{
	    /* TODO: better comparison, but is it really needed ? */
	    result = (this == _object);
	}

	return result;
    }

    /**
     * Adds the given interface to the set of already known behavior interfaces
     * declaring a method with this signature.
     *
     * @param _interface the behavior interface to add
     */
    void addOwningInterface(Class _interface)
    {
	owningBehavInterfSet.add(_interface);
    }

    void printConstructor(PrintWriter out, int signatureNumber)
    {
	/* First print the declaration of attributes */
	for (int idx = 0; idx < parameterTypes.length; idx++)
	{
	    out.println( "\tprivate "
		    + Tools.getTypeName(parameterTypes[idx]) + " sig"
		    + signatureNumber + "attr" + idx + " ;");
	}

	out.println();

	/* Now print the constructor declaration */
	out.print("\tpublic " + owningMessage.getMessageClassName());
	out.print("(");

	if (parameterTypes.length != 0)
	{
	    out.print(Tools.getTypeName(parameterTypes[0]) + " _p0");

	    for (int idx = 1; idx < parameterTypes.length; idx++)
	    {
		out.print(", " + Tools.getTypeName(parameterTypes[idx])
			+ " _p" + idx);
	    }
	}

	out.println(")");
	out.println("\t{");

	/* Assignment of the signatureNumber attribute */
	out.println("\t\tsignatureNumber = " + signatureNumber + " ;");

	/* Assignment of the constructor parameter(s) */
	for (int idx = 0; idx < parameterTypes.length; idx++)
	{
	    out.println("\t\tsig" + signatureNumber + "attr" + idx + " = "
		    + "_p" + idx + " ;");
	}

	out.println("\t}");
	out.println();
    }

    void printInvoke(PrintWriter out, int signatureNumber)
    {
	out.println("\t\t\tcase " + signatureNumber + " :");

	/* The code depends on whether the message is asynchronous or
	 * synchronous. The common parts are:
	 * - the if statement (if (_behavior instanceof <interface>))
	 * - the call to the method with a casting :
	 *   ((<interface>) _behavior).<message>(<args list>)
	 *
	 * The message only needs to know the name of the currently handled
	 * interface and the args list. Then it can print its proper code.
	 */
	/* Process the arguments list; it is common for all the interfaces */
	String argsList = "";

	if (parameterTypes.length > 0)
	{
	    argsList = "sig" + signatureNumber + "attr0";

	    for (int i = 1; i < parameterTypes.length; i++)
		argsList = argsList + ", sig" + signatureNumber + "attr" + i;
	}

	/* Process each interface `if' statement */
	out.print("\t\t\t\t");

	Iterator behavInterfIter = owningBehavInterfSet.iterator();

	/* TODO: empty owningBehavInterfSet ? */
	while (behavInterfIter.hasNext())
	{
	    owningMessage.printCall(
		out,
		((Class) behavInterfIter.next()).getName(),
		argsList);
	    out.print("\t\t\t\telse ");
	}

	/* Print the fallback case : raising an exception */
	out.println();
	out.println(
		"\t\t\t\t\tthrow new org.javact.lang.MessageHandleException() ;");
	out.println("\t\t\t\tbreak ;");
    }
}
