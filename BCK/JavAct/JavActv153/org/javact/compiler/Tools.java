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
 *     Added getTypeName method to handle arrays (see Signature.java)
 */
package org.javact.compiler;

import java.io.PrintWriter;

/**
 * Class that provides some tools not directly related to the JavAct
 * application.
 *
 * @author Vincent Hennebert
 * @version 0.5.1
 */
class Tools
{
    /**
     * Prints out the class name corresponding to the given primitive type. For
     * example if <code>_type</code> represents the <code>int</code> type then
     * <code>Integer</code> will be printed out.
     *
     * @param _out the stream out of which the class name must be printed
     * @param _type the class corresponding to a primitive type whose wrapper
     *	      class name must be printed out
     */
    static void printType(PrintWriter _out, Class _type)
    {
	if (_type == Boolean.TYPE)
	{
	    _out.print("Boolean");
	}
	else if (_type == Byte.TYPE)
	{
	    _out.print("Byte");
	}
	else if (_type == Character.TYPE)
	{
	    _out.print("Character");
	}
	else if (_type == Double.TYPE)
	{
	    _out.print("Double");
	}
	else if (_type == Float.TYPE)
	{
	    _out.print("Float");
	}
	else if (_type == Integer.TYPE)
	{
	    _out.print("Integer");
	}
	else if (_type == Long.TYPE)
	{
	    _out.print("Long");
	}
	else if (_type == Short.TYPE)
	{
	    _out.print("Short");
	}
    }

    /** Returns the name of the given class object, in a format usable to
     * declare a variable with such a type. If the given type is not an array,
     * then the result will be exactly the same as the one of {@link
     * java.lang.Class#getName}. Else it will be the name of the component type
     * followed by "<code>[]</code>".
     * <p>This method is called by {@link Signature#printConstructor} during
     * the generation of messages. Instead of the
     * "<code>[[Ljava.lang.Integer</code>" format returned by {@link
     * java.lang.Class#getName}, we need
     * "<code>java.lang.Integer[][]</code>".</p>
     *
     * @param type the type we need the name of
     * @return a name usable to declare a variable with such a type.
     *
     * @see Signature#printConstructor
     */
    static String getTypeName(Class type)
    {
	if (!type.isArray())
	    return type.getName();
	else
	    return getTypeName(type.getComponentType()) + "[]";
    }
}
