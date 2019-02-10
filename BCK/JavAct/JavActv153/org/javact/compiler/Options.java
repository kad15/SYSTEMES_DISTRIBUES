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

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * Class that defines the compiler options. The options are defined as static
 * fields which are checked by other classes to know whether a given option is
 * enabled or not.
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
class Options
{
/* The -d option doesn't work: unable to find the compiled classes when they
 * are put in another directory. Needed by the forName method when parsing the
 * behavior profiles. Changing the java.class.path property (via
 * System.setProperty) to add the bindir has no effect.
 * The user would have to change himself the CLASSPATH environment variable,
 * but is it platform independant ?
 * Keep uncommented because _I_ know how to make it work ;-)
 */
//    /**
//     * Name of the bindirectory option. This is the name that must be given to
//     * the compiler to activate the option.
//     *
//     * @see Options#parseOptions
//     */
    static final String OPTBINDIR = "-d";
//
//    /**
//     * true if the class files must be put in a separate directory. Default
//     * false. This is checked by other classes when they want to know whether
//     * the bindir option has been enabled or not.
//     *
//     * @see Options#parseOptions
//     */
    static boolean BINDIR = false;
//
//    /**
//     * Name of the directory where class files are to be stored.
//     *
//     * @see Options#parseOptions
//     */
    static String BINDIRNAME = null;

    /**
     * Name of the subdirectory option. This is the name that must be given to
     * the compiler to activate the option.
     *
     * @see Options#parseOptions
     */
    static final String OPTSUBDIR = "-g";

    /**
     * Name of the long subdirectory option. This is the name that must be
     * given to the compiler to activate the option.
     *
     * @see Options#parseOptions
     */
    static final String OPTLONGSUBDIR = "-generated-dir";

    /**
     * true if the generated files must be stored in a subdirectory. Default
     * false. This is checked by other classes when they want to know whether
     * the subdir option has been enabled or not.
     *
     * @see Options#parseOptions
     */
    static boolean SUBDIR = false;

    /**
     * Name of the directory where generated files are to be stored.
     *
     * @see Options#parseOptions
     */
    static String SUBDIRNAME = null;

    /**
     * Name of the package option. This is the name that must be given to the
     * compiler to activate the option.
     *
     * @see Options#parseOptions
     */
    static final String OPTPACKAGE = "-p";

    /**
     * true if the message handling classes must be stored in a separate
     * package. Default false. This is checked by other classes when they want
     * to know whether the package option has been enabled or not.
     *
     * @see Options#parseOptions
     */
    static boolean PACKAGE = false;

    /**
     * Name of the package that will contain the message handling classes.
     *
     * @see Options#parseOptions
     */
    static String PACKAGENAME = null;

    /**
     * Name of the backup option. This is the name that must be given to the
     * compiler to activate the option.
     *
     * @see Options#parseOptions
     */
    static final String OPTBACKUP = "-b";

    /**
     * true if a backup copy must be made for already existing message handling
     * files. Default false. This is checked by other classes when they want
     * to know whether the backup option has been enabled or not.
     *
     * @see Options#parseOptions
     */
    static boolean BACKUP = false;

    /**
     * Name of the package that will contain the message handling classes.
     *
     * @see Options#parseOptions
     */
    static String BACKUPDIR = null;

    /**
     * Name of the verbose option. This is the name that must be given to the
     * compiler to activate the option.
     *
     * @see Options#parseOptions
     */
    static final String OPTVERBOSE = "-v";

    /**
     * Name of the long verbose option. For compatibilty with the javac
     * compiler. This is the name that must be given to the compiler to
     * activate the option.
     *
     * @see Options#parseOptions
     */
    static final String OPTLONGVERBOSE = "-verbose";

    /**
     * true if the verbose option has been enabled. Default false.
     *
     * @see Options#parseOptions
     */
    static boolean VERBOSE = false;

    /**
     * Name of the short help option. Display a help message and exit.
     *
     * @see Options#parseOptions
     * @see Options#usage
     */
    static final String OPTHELP = "-h";

    /**
     * Name of the long help option. Display a help message and exit.
     *
     * @see Options#parseOptions
     * @see Options#usage
     */
    static final String OPTLONGHELP = "-help";


    /**
     * Parses the compilation options. These options are:
     * 
     * <table cellspacing="5">
     *     <tbody>
     *     <tr>
     *         <th align="left"
     *         valign="top"><kbd>&ndash;h<br>&ndash;help</kbd></th>
     *         <td valign="top">show a help message: compiler and options
     *         usage;</td>
     *     </tr>
     *     <tr>
     *         <th align="left"
     *         valign="top"><kbd>&ndash;v<br>&ndash;verbose</kbd></th>
     *         <td valign="top">verbose. Gives informations about the file
     *         being parsed, the roles found, behavior interfaces, etc.;</td>
     *     </tr>
     *     <!--<tr>
     *         <th align="left"
     *         valign="top"><kbd>&ndash;d&nbsp;&lt;dir&gt;</kbd></th>
     *         <td> stores the class files in the given directory (like the
     *         &ndash;d option of the javac compiler);</td>
     *     </tr>-->
     *     <tr>
     *         <th align="left"
     *         valign="top"><kbd>&ndash;b&nbsp;&lt;backup_dir&gt;</kbd></th>
     *         <td> saves already existing generated files in the given
     *         directory, before overwriting them.<br>
     *         <b>Note</b> that it will not search for all generated files, but
     *         only those that otherwise would be overwrited by a new
     *         generation.</td>
     *     </tr>
     *     <tr>
     *         <th align="left" valign="top">
     *         <kbd>&ndash;g&nbsp;&lt;dir&gt;<br>&ndash;generated&ndash;dir&nbsp;&lt;dir&gt;</kbd></th>
     *         <td valign="top">stores the generated files in the given
     *         subdirectory;</td>
     *     </tr>
     *     <tr>
     *         <th align="left"
     *         valign="top"><kbd>&ndash;p&nbsp;&lt;package_name&gt;</kbd></th>
     *         <td> puts generated messages in a package with the given package
     *         name.</td>
     *     </tr>
     *     </tbody>
     * </table>
     * 
     *
     * @param argsIter an iterator on the arguments string that was passed to
     *	      the compiler at its invocation.
     */
    static void parseOptions(Iterator argsIter)
    {
	boolean endOfOptions = false;

	while (!endOfOptions && argsIter.hasNext())
	{
	    String arg = (String) argsIter.next();

	    if (arg.equals(OPTBINDIR))
	    {
		BINDIR = true ;
		argsIter.remove();

		if (argsIter.hasNext())
		{
		    BINDIRNAME = new String((String) argsIter.next());
		    argsIter.remove();
		}
		else
		{
		    System.err.println(
			"ERROR: while processing option " + OPTBINDIR);
		    System.err.println("Usage: " + OPTBINDIR + " <dir>");
		    System.exit(1);
		}
	    }
	    else if (arg.equals(OPTSUBDIR) || arg.equals(OPTLONGSUBDIR))
	    {
		SUBDIR = true ;
		argsIter.remove();

		if (argsIter.hasNext())
		{
		    SUBDIRNAME = new String((String) argsIter.next());
		    argsIter.remove();
		}
		else
		{
		    System.err.println(
			"ERROR: while processing option " + OPTSUBDIR);
		    System.err.println("Usage: " + OPTSUBDIR + " <dir>");
		    System.exit(1);
		}
	    }
	    else if (arg.equals(OPTPACKAGE))
	    {
		PACKAGE = true ;
		argsIter.remove();

		if (argsIter.hasNext())
		{
		    PACKAGENAME = new String((String) argsIter.next());
		    argsIter.remove();
		}
		else
		{
		    System.err.println(
			"ERROR: while processing option " + OPTPACKAGE);
		    System.err.println("Usage: " + OPTPACKAGE + " <package>");
		    System.exit(1);
		}
	    }
	    else if (arg.equals(OPTBACKUP))
	    {
		/* Well, let's check errors before actually enabling the
		 * option
		 */
		argsIter.remove();

		if (argsIter.hasNext())
		{
		    BACKUPDIR = new String((String) argsIter.next());
		    argsIter.remove();

		    File backupDir = new File(BACKUPDIR);

		    if (!backupDir.exists())
		    {
			if (backupDir.mkdirs())
			{
			    BACKUP = true;
			}
			else
			{
			    System.err.println(
				"WARNING: Error while processing option "
				+ OPTBACKUP
				+ ": failed to create the backup directory.");
			    System.err.println("Option discarded.");
			}
		    }
		    else if (!backupDir.isDirectory())
		    {
			System.err.println(
			    "WARNING: Error while processing option "
			    + OPTBACKUP +": " + BACKUPDIR
			    + " is not a directory.");
			System.err.println("Option discarded.");
		    }
		    else if (!backupDir.canWrite())
		    {
			System.err.println(
			    "WARNING: Error while processing option "
			    + OPTBACKUP + ": " + BACKUPDIR
			    + " is not writeable.");
			System.err.println("Option discarded.");
		    }
		    else
		    {
			/* Pfuu... Now there should be no more problem */
			BACKUP = true;
		    }
		}
		else
		{
		    System.err.println(
			"ERROR: while processing option " + OPTBACKUP);
		    System.err.println("Usage: " + OPTBACKUP + " <backup dir>");
		    System.exit(1);
		}
	    }
	    else if (arg.equals(OPTVERBOSE) || arg.equals(OPTLONGVERBOSE))
	    {
		VERBOSE = true;
		argsIter.remove();
	    }
	    else if (arg.equals(OPTHELP) || arg.equals(OPTLONGHELP))
	    {
		usage(System.out);
		System.exit(0);
	    }
	    else
	    {
		endOfOptions = true;
	    }

	}
    }

    /** Display a help message. Usage of all existing options.
     * @param out stream to which the message has to be printed out.
     */
    static void usage(PrintStream out)
    {
	out.println(
"Usage: org.javact.compiler.Main <options> <list of java files>\n"		+
"Options are:\n"							+
"    -h -help     - show this message\n"				+
"    -v -verbose  - enable the verbose mode\n"				+
//"    -d <dir>     - sets the destination directory for class files\n"	+
//"                   (like the javac compiler)\n"			+
"    -b <dir>     - backup directory where to store old message\n"	+
"                   and QuasiBehavior files\n"				+
"    -g <dir>\n"							+
"    -generated-dir <dir> - sets the destination directory for\n"	+
"                           generated files\n"				+
"    -p <package_name>    - package in which generated messages\n"	+
"                           are to be stored"				);
    }
}
