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
* modified by S.Leriche (classpath)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An instance of this class manages the processing of files and the automatic
 * generation of message handling classes. More precisely, this class may be
 * used to:
 * 
 * <ul>
 *     <li>parse the options passed to the compiler;</li>
 *     <li>parse the .java files to find ActorProfile and BehaviorProfile
 *     instances;</li>
 *     <li>generate the message handling files;</li>
 *     <li>generate each behavior profile's corresponding abstract
 *     <code>QuasiBehavior</code> class;
 *     <li>compile the automatically generated classes.</li>
 * </ul>
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
class Driver
{
    /**
     * List of the string arguments passed to the driver (options + files to
     * parse). These arguments are passed to the <code>Driver</code> object
     * when it is created by the main method.
     */
    private List argsList;

    /** List of all <code>ActorProfile</code> found in the application. */
    private List actorProfList;

    /** <code>String</code> list of all the generated files. */
    private List generatedFilesList;

    /**
     * Creates a new <code>Driver</code> instance.
     * @param argsList String list of the parameters passed to the compiler,
     *	      including options and .java files to parse.
     */
    Driver(List argsList)
    {
	/* We must duplicate the args list because we want to remove items
	 * as we parse the arguments. Using the same list would result to an
	 * OperationNotSupported exception because it is read-only.
	 */
	this.argsList = new LinkedList(argsList);
	this.actorProfList = new LinkedList();

	/* The generatedFilesList does not need to be initialized: it will be
	 * given the list of generated QuasiBehaviors, then the generated
	 * messages will be appended to (see below).
	 */
    }

    /**
     * Parses the compilation options. The option items are removed from the
     * {@link Driver#argsList} when this method terminates. Thus it should be
     * called <b>before</b> the invocation of the {@link
     * Driver#findActorProfiles} method, unless no options were given.
     *
     * @see Options#parseOptions
     */
    void parseOptions()
    {
	Iterator iter = argsList.iterator();
	Options.parseOptions(iter);
    }

    /**
     * Compiles the given list of java files. Performs a call to the javac
     * compiler via the {@link Runtime#exec} method.
     *
     * @param filenamesList a <code>String</code> list of names of files to
     * compile.
     *
     * @return the result of the compilation
     */
    private int compile(List filenamesList)
    {
	int result = 0; // Result of the compilation

	List compileCommand = filenamesList;

	/* The filenamesList should not be affected by these instructions */
	compileCommand.add(0, "javac");
	if (Options.BINDIR)
	{
	    compileCommand.add(1, "-d");
	    compileCommand.add(2, Options.BINDIRNAME);
	}
	/* See below
	    compileCommand.add(3, "-classpath"); //SL
	    compileCommand.add(4, System.getProperty("java.class.path")); //SL
	*/

	/* We want to convert the list into an array, because it is no use to
	 * make a call to StringTokenizer */
	String[] command = new String[compileCommand.size()];
	compileCommand.toArray(command);

	try
	{
	    /* Is it platform-independent ? TODO */
	    String[] environment =
		{ "CLASSPATH=" + System.getProperty("java.class.path")};
	    Process compileProcess =
		 Runtime.getRuntime().exec(command, environment);

	    /* Redirect the compiler's error messages to the standard error
	     * output */
	    InputStreamReader errorStream =
		new InputStreamReader(compileProcess.getErrorStream());
	    BufferedReader buf = new BufferedReader(errorStream);

	    try
	    {
		String errorMessage;

		while ((errorMessage = buf.readLine()) != null)
		    System.err.println(errorMessage);
	    }
	    catch (IOException exn)
	    {
		/* Could really an exception occur ? */
		System.err.println(
		    "WARNING: unable to get compilation messages.");
	    }

	    compileProcess.waitFor();
	    result = compileProcess.exitValue();
	}
	catch (IOException exn)
	{
	    System.err.println("ERROR: could not launch the java compiler.");
	    System.exit(1);
	}
	catch (InterruptedException exn)
	{
	    System.err.println(
		"WARNING: the java compiler has been interrupted. You may have to relaunch the compilation");
	    result = 1;
	}

	return result;
    }

    /**
     * Returns a list of the <code>ActorProfile</code> interfaces contained in
     * the files given as parameters.  Called by {@link
     * Driver#parseActorProfiles}.  Searches in these files for actor profile
     * definitions, compiles the corresponding files and returns the
     * <code>Class</code> objects representing AP.
     *
     * @param fileNamesIter an iterator on the list of file names.
     *
     * @return the list of all <code>Class</code> objects that extend the
     * ActorProfile interface.
     */
    private List findActorProfiles(Iterator fileNamesIter)
    {
	/* String List that will contain the names of found actor profile
	 * interfaces.
	 * If the profile belongs to a package its name will be preceded by
	 * the package name (that is we get the so-called fully qualified name).
	 */
	List actorProfStringList = new LinkedList();

	/* Pattern that will be used to check if a file may be a java file.
	 * See below.
	 * Defined here because we want the compilation of the pattern to be
	 * made just once. */
	Pattern javaFileNamePattern = Pattern.compile(".*\\.java?");

	/* Pattern that will be used to check if a file corresponds to a
	 * message handling file. This is the case when we relaunch the
	 * compilation using "javactc *.java": message handling files may
	 * already have been generated and we want to discard them from this
	 * compilation
	 * This pattern is "((JAM)|(JSM)).+\.java?"
	 */
	Pattern messagePattern = Pattern.compile("((" + Keywords.ASYNC_MSG
		    + ")|(" + Keywords.SYNC_MSG + ")).+\\.java?");


	/* String corresponding to a pattern matching a Java identifier;
	 * according to the specification, currency symbols are allowed
	 */
	//String identPattern = "\\b[\\p{L}\\p{Sc}_](?:\\p{L}\\p{Sc}_\\d)*\\b";
	String identPattern = "\\w+";

	/* Pattern that matches a package declaration. Needed if actor profiles
	 * are embedded in packages, in order to find the class when we call
	 * the forName method
	 */
	Pattern packagePattern =
	    Pattern.compile("^[^(?:/\\*)(?://)]*package\\s+(" + identPattern
		    + "(?:\\." + identPattern + ")*)");
	/* The part of the pattern corresponding to the identifier is set in a
	 * group. Thus we can get the identifier via the Matcher.group method
	 */
	int packageIdGroupNum = 1;
	Matcher packageMatcher = packagePattern.matcher("");

	/* Pattern that matches the definition of an ActorProfile interface. It
	 * matches "<any modifier> interface <name> extends <interfaces>
	 * ActorProfile" We don't want to match a comment line that looks like
	 * an interface definition, thus we exclude comment patterns from the
	 * match */
	Pattern actorProfDefPattern =
	    Pattern.compile(
		"^[^(?:/\\*)(?://)]*interface\\s+(" + identPattern
		+ ")\\s+extends[^(?:/\\*)(?://)]*" + Keywords.ROLE_PATTERN);

	int identifierGroupNum = 1;
	Matcher roleDefMatcher = actorProfDefPattern.matcher("");

	/* We will compile all the files that define a role interface. Store
	 * the corresponding filenames in a list that will be passed to the
	 * compiler
	 */
	List compileList = new LinkedList();

	while (fileNamesIter.hasNext())
	{
	    String filename = (String) fileNamesIter.next();

	    /* Check if the file is a java file. Its name must end with
	     * ".jav" or ".java". This test is not sufficient, but only is a
	     * first check.
	     */
	    if (!javaFileNamePattern.matcher(filename).matches())
	    {
		if (Options.VERBOSE)
		{
		    System.err.println(
			"WARNING: file "
			    + filename
			    + " does not seem to be a java file -- discarded.");
		}

		fileNamesIter.remove();
	    }
	    else if (messagePattern.matcher(filename).matches())
	    {
		/* This filename corresponds to a message handling file. It is
		 * either deprecated (the corresponding message no longer
		 * exists in the application) or it will be regenerated and
		 * recompiled. In any case we don't want to compile this file
		 */
		fileNamesIter.remove();
	    }
	    else
	    {
		/* Check if this file contains one or more ActorProfile
		 * interface definition(s) */
		File file = new File(filename);

		if (!file.exists())
		{
		    System.err.println( "WARNING: file " + filename
			    + " does not exist -- discarded.");
		    fileNamesIter.remove();
		}
		else if (!file.isFile())
		{
		    System.err.println( "WARNING: file " + filename
			    + " does not seem to be a regular file -- discarded.");
		    fileNamesIter.remove();
		}
		else if (!file.canRead())
		{
		    System.err.println( "WARNING: cannot read file " + filename
			    + " -- discarded.");
		    fileNamesIter.remove();
		}
		else
		{
		    try
		    {
			BufferedReader reader =
			    new BufferedReader(new FileReader(file));
			boolean fileDefinesActorProf = false;
			String line = null;
			/* If the file contains a package declaration, store it
			 * to have the fully qualified names of the classes.
			 */
			String packageName = "";

			while ((line = reader.readLine()) != null)
			{
			    packageMatcher.reset(line);
			    if (packageMatcher.find())
			    {
				packageName =
				    packageMatcher.group(packageIdGroupNum)
				    + "."; // Package separator
			    }

			    roleDefMatcher.reset(line);
			    if (roleDefMatcher.find())
			    {
				fileDefinesActorProf = true;
				actorProfStringList.add(packageName +
				    roleDefMatcher.group(identifierGroupNum));
			    }
			}

			/* If this file defines a role, we will have to compile
			 * it before generating the corresponding message. Then
			 * we can remove it from the list of remaining files to
			 * be compiled
			 */
			if (fileDefinesActorProf)
			{
			    compileList.add(filename);
			    fileNamesIter.remove();
			}
		    }
		    catch (java.io.FileNotFoundException exn)
		    {
			/* gha... The FileInputStream constructor should
			 * normally never raise this exception, we made enough
			 * checks. */
			System.err.println(
			    "WARNING: failed to open file "
				+ filename
				+ " -- discarded.");
		    }
		    catch (IOException exn)
		    {
			System.err.println(
			    "WARNING: an I/O exception occured while reading the file "
				+ filename
				+ "; the handling of this file may have been only partial.");
		    }
		}
	    }
	} // End of while iterNext

	if (Options.VERBOSE)
	{
	    if (actorProfStringList.isEmpty())
	    {
		System.out.println("Found no actor profile.");
		/* And that's all: no actor profile => no message => no
		 * generation
		 */
		System.exit(0);
	    }
	    else
	    {
		Iterator ActorProfIter = actorProfStringList.iterator();
		System.out.print("Found the following actor profile(s): ");
		System.out.print((String) ActorProfIter.next());

		while (ActorProfIter.hasNext())
		    System.out.print(", " + (String) ActorProfIter.next());

		System.out.println(".");
	    }
	}

	/* Now compile all the files that defines role interfaces */
	if (!compileList.isEmpty())
	{
	  /*  if (Options.VERBOSE)
	    {
		    System.out.println(
		    "Compiling files defining actor profile interfaces...");
	    }
	    //int result = compile(compileList);

	    /* It is no use to go on if there were compilation errors */
	  /*  if (result != 0)
	    {
		System.exit(result);
	    }*/
	}

	/* Now we have to convert the strings into the corresponding Class
	 * objects. The compilation succeeded, so no ClassNotFoundException
	 * should be raised here
	 */
	List actorProfClassList = new LinkedList();

	Iterator ActorProfIter = actorProfStringList.iterator();

	while (ActorProfIter.hasNext())
	{
	    try
	    {
		    actorProfClassList.add(
			    Class.forName((String) ActorProfIter.next()));
	    }
	    catch (ClassNotFoundException exn)
	    {
		/* However... The pattern matching actor profile definitions is
		 * wrong: it can match lines within multi-line comments that
		 * are similar to actor profile definitions */
		/* Or... if the user specified the -d option he must set his
		 * CLASSPATH properly
		 */
		System.err.println("WARNING: class " + exn.getMessage()
			+ " was not found. It is perhaps actually not an actor"
			+ " profile but you may have to check your classpath.");
		System.err.println("Current classpath: " +
			System.getProperty("java.class.path"));
	    }
	}

	return actorProfClassList;
    }

    /**
     * Parses the files given at this <code>Driver</code> instance creation.
     * Finds the ActorProfile interfaces, extracts their behavior interfaces
     * and their corresponding messages. <b>Note</b> that the {@link
     * Driver#parseOptions} should already have been invoked, such that the
     * option items are removed from the argsList.
     */
    void parseActorProfiles()
    {
	/* Options should have been parsed. Now argsList only contains java
	 * file names
	 */
	Iterator fileNamesIter = argsList.iterator();

	/* Get all the actor profiles defined in the files */
	List actorProfClassList = findActorProfiles(fileNamesIter);
	Iterator actorProfIter = actorProfClassList.iterator();

	/* Get the messages defined in each actor profile */
	if (Options.VERBOSE)
	{
	    System.out.println("Extracting messages...");
	}

	while (actorProfIter.hasNext())
	{
	    Class actorProfClass = (Class) actorProfIter.next();

	    if (Options.VERBOSE)
	    {
		System.out.println("Handling the actor profile "
			+ actorProfClass.getName() + "...");
	    }

	    try
	    {
		actorProfList.add(new ActorProfile(actorProfClass));
	    }
	    catch (ActorProfileException exn)
	    {
		System.err.println("Parse error: " + exn.getMessage());
		System.exit(1);
	    }
	    catch (NotABehaviorProfileException exn)
	    {
		System.err.println("Parse error: " + exn.getMessage());
		System.exit(1);
	    }
	    catch (BecomeException exn)
	    {
		System.err.println("Parse error: " + exn.getMessage());
		System.exit(1);
	    }
	}
    }

    /**
     * Generates the behavior profiles' abstract classes. For each
     * <code>BehaviorProfile</code> interface <code>BP</code>, generates the
     * <code>BPQuasiBehavior</code> abstract class.
     */
    void generateQuasiBehaviors()
    {
	if (BehaviorProfile.areQuasiBehaviorsToGenerate())
	{
	    if (Options.VERBOSE)
		System.out.println("Generating QuasiBehaviors...");

	    /* Generated files must then be compiled. Keep a trace of them */
	    generatedFilesList = BehaviorProfile.generateQuasiBehaviors();
	}
	else
	{
	    /* We must at least initialize generatedFilesList to an empty list,
	     * or a NullPointerException occurs when compileFiles is called
	     */
	    generatedFilesList = new LinkedList();
	}
    }

    /**
     * Generates the message handling java files.
     *
     * @see Message#generateMessages()
     */
    void generateMessages()
    {
	if (Message.areMessagesToGenerate())
	{
	    if (Options.VERBOSE)
		System.out.println("Generating messages...");

	    /* Add the generated messages to the list of files to be then
	    * compiled */
	    generatedFilesList.addAll(Message.generateMessages());
	}
    }

    /**
     * Compile generated messages and all the remaining files of the
     * command-line.
     *
     * @return the result of the compilation
     */
    int compileFiles()
    {
	int result = 0;

	if (!generatedFilesList.isEmpty()) // Could it be empty?
	{
	 /*   if (Options.VERBOSE)
	    {
		System.out.println("Compiling generated files...");
	    } */

	    // The generation was automatic. This compilation should succeed
	    //compile(generatedFilesList);
	}

	/* CHANGELOG
	 * Below is out-of-date code
	 */
	/*
	if (!argsList.isEmpty())
	{
	    if (Options.VERBOSE)
	    {
		System.out.println("Compiling remaining files...");
	    }

	    result = compile(argsList);
	}
	*/

	/*
	if ((result == 0) && (!messageExtractingResult))
	{
	    System.err.print(
		"Errors may have occured when extracting messages. The compilation is OK but you may encounter run-time errors because of bad generated files. Suggest check your program");

	    if (!Options.VERBOSE)
	    {
		System.err.print(" and enable the verbose option");
	    }

	    System.err.println(".");
	}
	*/

	return result;
    }
}
