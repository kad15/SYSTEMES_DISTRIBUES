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
 * handling of synchronous messages.<br>
 * <b>Note</b> that the return type must either be a primitive type
 * (<code>int</code>, <code>boolean</code>...) or a simple class type &mdash;
 * <b>not</b> an array! (this is not yet implemented but might change in the
 * future.)<br>
 * The name of the return type is appended to the name of the message &mdash;
 * thus the message <code>get</code> returning an <code>Integer</code> object
 * will be implemented by a class named <code>[JSM]getInteger</code>. This will
 * lead to a problem when return types coming from different packages have the
 * same name. For example two <code>get</code>messages returning respectively a
 * <code>foo.Type</code> and a <code>bar.Type</code> object both will result in
 * the same <code>getType</code> class. However this should be quite unusual.
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
class SynchronousMessage extends Message
{
    /** Return type of the message. */
    private Class returnType;

    /**
     * Creates a new message object.
     *
     * @param _method The method corresponding to the message
     * @param _owningBehavInterface The behavior interface that declares this
     *	      message
     */
    SynchronousMessage(Method _method, Class _owningBehavInterface)
    {
	super(_method, _owningBehavInterface);

	returnType = _method.getReturnType();

	/* Process the name of the java file that will contain this message
	 * handling
	 */
	/* Either it is a primitive type or a anonymous type (a type that
	 * belongs to no package) -- in both these cases the fully qualified
	 * name of the type is its simple name;
	 * - or it is a "complex type" which belongs to a package. Thus we
	 * keep the simple name for the name of the message Class, in order
	 * to have a right class name without '.'
	 */
	/* For now we don't allow the return type to be an array ; this might
	 * change in the future
	 */
	if (returnType.isArray())
	{
	    System.err.println(
		"Error : the message "
		    + name
		    + " returns an array, which is currently not supported -- exiting.");
	    System.exit(1);
	}
	else if (returnType.isPrimitive() || (returnType.getPackage() == null))
	{
	    messageClassName =
		Keywords.SYNC_MSG + _method.getName() + returnType.getName();
	}
	else
	{
	    /* We suppress the package part of the fully qualified name */
	    String qualifiedName = returnType.getName();

	    /* We search for the position of the last point in the string ;
	     * the simple name is the substring after this position */
	    int pointIndex = qualifiedName.lastIndexOf('.');
	    messageClassName =
		Keywords.SYNC_MSG
		    + _method.getName()
		    + qualifiedName.substring(pointIndex + 1);
	}
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
	out.println(
	    "public class "
		+ getMessageClassName()
		+ " extends org.javact.lang.MessageWithReply");
	out.println("{");

	/* Print the result attribute */
	out.println("\tprivate " + returnType.getName() + " result ;");
	out.println();
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
	/* if statement */
	out.println("if (_behavior instanceof " + behavInterfName + ")");
	out.println("\t\t\t\t{");

	/* call to the method */
	out.println(
	    "\t\t\t\t\tresult = (("
		+ behavInterfName
		+ ") _behavior)."
		+ name
		+ "("
		+ argsList
		+ ") ;");

	/* setReply */
	out.print("\t\t\t\t\tgetServerSideAnswerBack().setReply(");

	if (returnType.isPrimitive())
	{
	    out.print("new ");
	    Tools.printType(out, returnType);
	    out.print("(result)");
	}
	else
	{
	    out.print("result");
	}

	out.println(") ;");
	out.println("\t\t\t\t}");
    }

    /**
     * Finishes the generation of the class handling this message. It prints
     * out the <code>getReply</code> method definition and then closes the
     * stream.
     *
     * @param out DOCUMENT ME!
     */
    protected void printEndOfGeneration(PrintWriter out)
    {
	out.println();

	/* We still have to print the getReply method definition */
	printReply(out);
	out.println("}");

	/* Well, vim is a great text editor - print a modeline for automagic
	 * setting of the tabstop option */
	// out.println("/* vim: set ts=4: */");
	out.close();
    }

    /**
     * Prints out the <code>getReply</code> method definition. Called by {@link
     * SynchronousMessage#printEndOfGeneration}.
     *
     * @param out the stream pointing to the file containing the message
     *	      handling class
     */
    private void printReply(PrintWriter out)
    {
	out.print("\tpublic " + returnType.getName() + " getReply() ");

	/* TODO : handle exceptions ?
	    if (exceptions.length != 0) {
	    out.print("throws " + exceptions[0].getName()) ;
	
	    for (int idx = 1; idx < exceptions.length; idx++) {
		out.print(", " + exceptions[idx].getName()) ;
	    }
	}
	*/
	out.println(" {");
	out.print("\t\treturn ");

	if (returnType.isPrimitive())
	{
	    out.print("((");
	    Tools.printType(out, returnType);
	    out.print(
		") getFuture().getReply())."
		    + returnType.getName()
		    + "Value()");
	}
	else
	{
	    out.print("(");
	    out.print(returnType.getName());
	    out.print(")getFuture().getReply()");
	}

	out.println(" ;");
	out.println("\t}");
    }
}
