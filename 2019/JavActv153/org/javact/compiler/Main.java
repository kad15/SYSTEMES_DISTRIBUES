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

import java.util.Arrays;

/**
 * Main class. Creates a new <code>Driver</code> object, passing to it the
 * command-line arguments and successively calling the {@link
 * Driver#parseOptions}, {@link Driver#parseActorProfiles}, {@link
 * Driver#generateQuasiBehaviors}, {@link
 * Driver#generateMessages} and {@link Driver#compileFiles} methods.
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
public class Main
{
    /**
     * Main method to generate adapted messages for JavAct applications.
     *
     * @param _args options and names of files that contains ActorProfile
     * definitions or Behavior Interface declarations.
     */
    public static void main(String[] _args)
    {
	if (_args.length < 1)
	{
	    /* Display the help message then exit */
	    Options.usage(System.err);
	    System.exit(1);
	}

	Driver driver = new Driver(Arrays.asList(_args));
	/* Should we encapsulate all those methods in a single one ? */
	driver.parseOptions();
	driver.parseActorProfiles();
	driver.generateQuasiBehaviors();
	driver.generateMessages();
	driver.compileFiles();

	/* We make an explicit call to exit in order to return the result of
	 * the compilation.
	 * CHANGELOG: the javactc compiler no longer compiles the user's
	 * files, only the automatically generated files which compile
	 * without error.
	 * */
	//System.exit(result);
    }
}
