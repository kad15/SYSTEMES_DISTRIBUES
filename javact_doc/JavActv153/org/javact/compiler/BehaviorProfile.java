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
 * Modified by SL07 - Modif compat AgentPhi (Agent instead of Actor)
 * ###########################################################################
 */
package org.javact.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manipulating behavior profiles. This class is split in two parts: on the
 * one hand a set of static methods dealing with the compiler's other
 * classes; on the other hand private instance attributes &amp; methods for
 * manipulating a <code>BehaviorProfile</code> instance.
 *
 * @author Vincent Hennebert, 
 * modifications by Guillaume Danel (guidanel@free.fr) (marked by a "// New")
 * @version 0.5.0
 */
class BehaviorProfile
{
	/**
	 * Extended <code>Map</code> that can map multiple values to a single key.
	 * The default <code>Map</code> implementation does not allow such
	 * mappings. 
	 */
	static private class MultiMap
	{
		/** All the implementation relies on an embedded <code>Map</code>
		 * instance.
		 */
		private Map map = new HashMap() ;
		
		/**
		 * Returns true if this multi-map contains a mapping for the specified
		 * key.
		 * @return <code>true</code> if this map contains a mapping for the
		 * specified key.
		 */
		public boolean containsKey(Object key)
		{
			return map.containsKey(key) ;
		}
		
		/**
		 * Returns the list of values to which this map maps the specified key.
		 * @param key key whose associated values are to be returned.
		 * @return the list of values mapped to this key.
		 */
		public List get(Object key)
		{
			return (List) map.get(key);
		}
		
		/**
		 * Adds the specified value to the list of values mapped to the
		 * specified key. If the key did previously not exist, then a new entry
		 * is created, that is a new one-element list.
		 */
		public void put(Object key, Object value)
		{
			if (map.containsKey(key))
			{
				((List) map.get(key)).add(value);
			}
			else
			{
				List list = new LinkedList();
				list.add(value);
				map.put(key, list);
			}
		}
	}
	
	
	
	
	/* ------------------------- STATIC PART ------------------------- */
	
	/**
	 * Collection of existing behavior profiles. Map a <code>Class</code>
	 * instance to its corresponding <code>BehaviorProfile</code> instance.
	 */
	static private Map knownProfiles = new HashMap() ;
	
	// New
	/* List of existing behavior profiles. Used to generate the skeleton.*/
	static private List behClassList = new LinkedList();
	
	/**
	 * Collection of behavior profiles requiring for other behavior profiles.
	 * During the creation of a behavior profile, some future BPs (the
	 * parameters of <code>become</code> methods) may not be available yet.
	 * Creating them at once may result to a deadlock, because other BPs may
	 * themselves depend on the currently created BP. To avoid that we use a
	 * subscribing mechanism that will postpone the creation of other BPs.<br>
	 * A <code>MultiMap</code> is a map that allows multiple values to be
	 * mapped to a key (see {@link MultiMap}).
	 */
	static private MultiMap requests = new MultiMap();
	
	/**
	 * Returns the <code>BehaviorProfile</code> instance corresponding to the
	 * given <code>Class</code> object.  Creates a new instance of
	 * <code>BehaviorProfile</code> if it does not already exist.
	 * @param behavProfClass <code>Class</code> object corresponding to a
	 * behavior profile.
	 * @return the corresponding <code>BehaviorProfile</code> instance.
	 *
	 * @throws NotABehaviorProfileException if the given class is malformed.
	 * @throws BecomeException if the interface badly defines a
	 * <code>become</code> method.
	 * @see BehaviorProfile#BehaviorProfile
	 */
	static BehaviorProfile getBehaviorProfile(Class behavProfClass)
	throws NotABehaviorProfileException, BecomeException
	{
		if (knownProfiles.containsKey(behavProfClass))
		{
			return (BehaviorProfile) knownProfiles.get(behavProfClass);
		}
		else
		{
			/* The created profile adds itself to the knownProfiles */
			BehaviorProfile behavProf = new BehaviorProfile(behavProfClass);
			return behavProf;
		}
	}
	
	/** Returns <code>true</code> if there are QuasiBehaviors to be generated.
	 * It may not be the case when programs were compiled with the
	 * <kbd>&ndash;d</kbd> option but the corresponding directory
	 * was not added in the <var>CLASSPATH</var>. Then the <code>forName</code>
	 * call fails because class objects cannot be found.
	 */
	static boolean areQuasiBehaviorsToGenerate()
	{
		return !knownProfiles.isEmpty();
	}
	
	/**
	 * Generates abstract QuasiBehavior classes and the skeleton of the application.
	 *
	 * @return <code>String</code> list of filenames corresponding to the
	 * generated files.
	 */
	static List generateQuasiBehaviors()
	{
		List fileNamesList = new LinkedList();
		Iterator behavProfIter = knownProfiles.values().iterator();
		
		while (behavProfIter.hasNext())
		{
			BehaviorProfile behavProf =
				((BehaviorProfile) behavProfIter.next());
			fileNamesList.add(behavProf.generate());
		}
		
		// New
		generateSkeleton();
		
		return fileNamesList;
	}
	
	/**
	 * Request for a still not existing <code>BehaviorProfile</code>. Register
	 * the requesting behavior profile that will be informed as soon as the
	 * requested profile is created (via a call to its {@link
	 * BehaviorProfile#answerRequest} method).
	 *
	 * @param requestedBehavProf the requested <code>BehaviorProfile</code>
	 * instance.
	 * @param clientBehavProf the requesting profile.
	 *
	 * @see BehaviorProfile#requests
	 * @see BehaviorProfile#answerRequest
	 * @see BehaviorProfile#registerProfile
	 */
	static private void requestFor(Class requestedBehavProf,
			BehaviorProfile clientBehavProf)
	{
		requests.put(requestedBehavProf, clientBehavProf);
	}
	
	/** Puts the given profile in the collection of known behavior profiles. If
	 * other profiles had requested for this profile, they are noticed that it
	 * now exists.
	 * @param behavProfClass corresponding <code>Class</code> object.
	 * @param behavProfile the behavior profile to register.
	 *
	 * @see BehaviorProfile#answerRequest
	 * @see BehaviorProfile#requestFor
	 * @see BehaviorProfile#requests
	 */
	static private void registerProfile(BehaviorProfile behavProfile,
			Class behavProfClass)
	{
		knownProfiles.put(behavProfClass, behavProfile);
		
		/* Tell other BP requesting this BP that it now exists */
		if (requests.containsKey(behavProfClass))
		{
			List requestList = requests.get(behavProfClass);
			Iterator requestIter = requestList.iterator();
			do
			{
				((BehaviorProfile)
						requestIter.next()).answerRequest(behavProfile);
			}
			while (requestIter.hasNext());
		}
	}
	
	
	
	
	/* ------------------------- NON-STATIC PART ------------------------- */
	
	/** <code>BehaviorProfile</code> list of all possible future behaviors. */
	private List futureBehaviorsList = new LinkedList();
	
	/** <code>Message</code> list of messages accepted by this behavior. */
	private List messagesList = new LinkedList();
	
	/** Name of this behavior profile. This attribute is named
	 * <code>className</code>, to be compared to {@link
	 * BehaviorProfile#packageName}.
	 */
	private String className;
	
	// New
	/* List of the class names representing behavior profile */
	private static List behClassNameList = new LinkedList(); 
	
	/** Package this behavior profile belongs to. By default if the
	 * corresponding interface belongs to a package, the generated
	 * QuasiBehavior will belong to the same package.
	 */
	private String packageName;
	
	// New
	/* List of the package names representing behavior profile */
	private static List behPackageNameList = new LinkedList(); 
	
	/** Path in the file system in which the generated QuasiBehavior must be
	 * placed. Default empty.<br>
	 * If the {@link Options#SUBDIR} option is enabled, it will contains the
	 * value of {@link Options#SUBDIRNAME}.<br>
	 * If the {@link Options#PACKAGE} option is enabled, the value of {@link
	 * Options#PACKAGENAME} will be appended.
	 */
	private String pathName;
	
	
	
	/** Creates a new instance corresponding to the given <code>Class</code>
	 * object.
	 * @param behavProfClass the corresponding interface.
	 *
	 * @throws NotABehaviorProfileException if the given class is actually not
	 * a behavior profile (does not implement the <code>BehaviorProfile</code>
	 * interface).
	 * @throws BecomeException if the given interface declares bad become
	 * methods (bad number &amp; type of parameters, bad return type).
	 */
	BehaviorProfile(Class behavProfClass)
	throws NotABehaviorProfileException, BecomeException
	{
		/* First check if the given behavProfClass really is an instance of
		 * BehaviorProfile.
		 */
		if (!Keywords.BEHAVIOR_INTERFACE.isAssignableFrom(behavProfClass))
		{
			throw new NotABehaviorProfileException(behavProfClass.getName() +
					" probably is a behavior profile, however it does not extend "
					+ Keywords.BEHAVIOR);
		}
		
		/* Compute the values of className, packageName & pathName */
		computeNames(behavProfClass.getName());
		
		/* Find messages on the one hand, become specifications on the other
		 * hand.
		 */
		Method[] methodsTab = behavProfClass.getMethods();
		
		for (int i = 0; i < methodsTab.length; i++)
		{
			Method method = methodsTab[i];
			if (!method.getName().equals(Keywords.BECOME))
			{
				/* This is a message, let the Message class handle it */
				Message message = Message.addMessage(method, behavProfClass);
				messagesList.add(message);
			}
			else
			{
				/* This is a become method; let's test if it is right declared:
				 * right number & right type or parameter
				 */
				Class[] parameterTypes = method.getParameterTypes();
				if (method.getReturnType() != Void.TYPE)
				{
					throw new BecomeException("method " + method.getName()
							+ " in interface " + behavProfClass.getName()
							+ ": return type is not void");
				}
				else if (parameterTypes.length != 1)
				{
					throw new BecomeException("method " + method.getName()
							+ " in interface " + behavProfClass.getName()
							+ ": incorrect number of parameters");
				}
				else if (!Keywords.BEHAVIOR_INTERFACE.
						isAssignableFrom(parameterTypes[0]))
				{
					throw new BecomeException("method " + method.getName()
							+ " in interface " + behavProfClass.getName()
							+ ": parameter is not a behavior profile");
				}
				else
				{
					Class futureBehavClass = parameterTypes[0];
					if (!BehaviorProfile.knownProfiles.
							containsKey(futureBehavClass))
					{
						BehaviorProfile.requestFor(futureBehavClass, this);
					}
					else
					{
						BehaviorProfile futureBehavior = BehaviorProfile.
						getBehaviorProfile(futureBehavClass);
						futureBehaviorsList.add(futureBehavior);
					}
				}
			}// endif method == become
		}// endfor each method
		
		/* Add oneself to the map of existing behavior profiles */
		BehaviorProfile.registerProfile(this, behavProfClass);
		
		// New
		/* Add the Class behavProfClass to the vector of existing behaviour profiles */
		behClassList.add(behavProfClass);
		behClassNameList.add(className);
		behPackageNameList.add(packageName);
	}
	
	/** Returns the name of this behavior profile.
	 * @return class name of this behavior profile
	 */
	private String getName()
	{
		return className;
	}
	
	/** Returns the list of possible future behavior profiles from this one.
	 * This is the list of parameters of all the <code>become</code> methods
	 * declared in this profile.
	 * @return <code>BehaviorProfile</code> list of possible futures
	 */
	List getFutureBehaviorsList()
	{
		return futureBehaviorsList;
	}
	
	/** Fulfills a previous request for the given behavior profile. If it was
	 * not available at the creation of this profile, the given BP will call
	 * this method when it is created.
	 * @param behavProfile the <code>BehaviorProfile</code> instance that was
	 * requested by this profile.
	 *
	 * @see BehaviorProfile#registerProfile
	 * @see BehaviorProfile#requestFor
	 * @see BehaviorProfile#requests
	 */
	private void answerRequest(BehaviorProfile behavProfile)
	{
		futureBehaviorsList.add(behavProfile);
	}
	
	/** Computes the values of {@link BehaviorProfile#className}, {@link
	 * BehaviorProfile#packageName} &amp; {@link BehaviorProfile#pathName}.
	 * @param qualifiedName fully qualified name of the corresponding interface.
	 */
	void computeNames(String qualifiedName)
	{
		pathName = "";
		
		/* Now separate package from class */
		int dotPos = qualifiedName.lastIndexOf('.');
		if (dotPos == -1)
		{
			className = qualifiedName;
			packageName = null;
		}
		else
		{
			className = qualifiedName.substring(dotPos + 1);
			packageName = qualifiedName.substring(0, dotPos);
			pathName += packageName.replace('.', File.separatorChar)
			+ File.separator;
		}
		/* First set the pathname to subdirname if it exists */
		if (Options.SUBDIR)
			pathName = Options.SUBDIRNAME + File.separator;
		
	}
	
	/**
	 * Generates the corresponding abstract QuasiBehavior class. Called by
	 * {@link BehaviorProfile#generateQuasiBehaviors}.
	 * @return name of the generated file.
	 */
	private String generate()
	{
		/* A shorthand for Keywords.QUASI_BEHAVIOR */
		String quasiBeh = Keywords.QUASI_BEHAVIOR;
		String fileName = className + quasiBeh + ".java";
		/* File that will contain the QuasiBehavior class */
		File file = new File(pathName + fileName);
		PrintWriter out = null;
		
		if (file.exists())
		{
			/* Make a backup copy if the corresponding option has been
			 * enabled */
			if (!Options.BACKUP)
			{
				/* We should check the return flag of this method. It should
				 * be true. Otherwise we can do nothing. Let's just hope that
				 * an I/O exception will not occur when the new file is
				 * created
				 */
				file.delete();
			}
			else
			{	// New
				/* This message is quite annoying
				 if (Options.VERBOSE)
				 System.out.println("\t" + fileName +
				 " already exists; making a backup copy into " +
				 Options.BACKUPDIR) ;
				 */
				
				File backupFile = new File(Options.BACKUPDIR + File.separator + fileName);
				
				// New
				try{
					// copying the content of file in backupFile
					BufferedReader br = new BufferedReader(new FileReader(file));
					BufferedWriter bw = new BufferedWriter(new FileWriter(backupFile));
					String data = null;
					while( (data = br.readLine()) != null ){
						bw.write(data,0,data.length());
						bw.newLine();
					}
					br.close();
					bw.close();
					br = null;
					bw = null;
					file.delete();
				}catch(FileNotFoundException fnfe){
					fnfe.printStackTrace();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}
				
				// New
				/* the renameTo method acting weirdly, the above copying method has been used 
				 if (!file.renameTo(backupFile))
				 {
				 if (Options.VERBOSE)
				 {
				 System.err.println("WARNING: backup copy of "
				 + fileName + " failed. Deleting the file.");
				 }
				 file.delete(); // See above
				 }*/
			}
			
			file = new File(pathName + fileName);
		}
		else if (!pathName.equals(""))
		{
			/* Create the subdirectory if it does not exist */
			File dir = new File(pathName);
			if ((!dir.exists()) && (!dir.mkdirs()))
			{
				System.err.println("ERROR: could not create the " + pathName
						+ " directory. Given up.");
				System.exit(1);
			}
		}
		
		/* Now actually begin the generation */
		try
		{
			/* FileWriter can throw an IOException */
			out = new PrintWriter(new FileWriter(file));
			
			if (Options.SUBDIR){
				pathName += Options.SUBDIRNAME + File.separator;
				
				// New
				String initialPackage = packageName;

				String defaultPackagePath = "";
				String initialPackagePath = "";
				String destinationPath = Options.SUBDIRNAME;
				if (System.getProperty("os.name").regionMatches(true, 0, "Windows", 0, 7)){
					defaultPackagePath = System.getProperty("java.class.path").split(";")[0];
					initialPackagePath = defaultPackagePath.replaceAll("\\\\","/");
				} else {
					defaultPackagePath = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
					initialPackagePath = defaultPackagePath;
				}
				
				if (initialPackage != null) {
					initialPackagePath += "/" + initialPackage.replaceAll(".","/");
				}
				String destinationPackageName = "";
				if (Options.SUBDIRNAME.length()>defaultPackagePath.length()){
					destinationPackageName = Options.SUBDIRNAME.substring(defaultPackagePath.length()+1).replaceAll("/",".");
				}
				if (!destinationPath.equals(defaultPackagePath)){
					out.println("package " + destinationPackageName + ";");
					out.println();
				}	
				if (initialPackage==null){
					//initialPackage was default package
					if (!destinationPath.equals(defaultPackagePath)){
						out.println("/* Java files from default package can not be imported since  j2sdk 1.4");
						out.println(" * You have to move " + className + ".java into this package or put your file in");
						out.println(" * the default package");
						out.println("*/\n\n");
					}
				} else {
					if (!initialPackagePath.equals(destinationPath)){
						out.println("import " + initialPackage + "." + className + ";\n");
					}
				}
			}
			
			out.print(
					"import " + Keywords.JAVACT_PACKAGE + "." + quasiBeh + ";\n"		+
					"\n"									+
					"public abstract class " + className + quasiBeh + " extends "		+
					quasiBeh + " implements " + className + "\n"			+
			"{\n");
			
			
			/* Print out the become wrappers for each future behavior */
			Iterator futureIter = futureBehaviorsList.iterator();
			while (futureIter.hasNext())
			{
				String futureName =
					((BehaviorProfile) futureIter.next()).getName();
				out.println(
						"\tpublic void " + Keywords.BECOME + "(" + futureName + " b)\n"		+
						"\t{\n"									+
						"\t\ttry\n"								+
						"\t\t{ becomeAny((" + quasiBeh + ") b); }\n"				+
						"\t\tcatch (RuntimeException e)\n"					+
						"\t\t{ throw new org.javact.lang.BecomeException(e);}\n"			+
				"\t}\n");
			}
			/* End-of-class brace */
			out.println("}");
			/* Well, vim is a great text editor - print a modeline for automagic
			 * setting of the tabstop option */ 
			// out.println("/* vim: set ts=4: */");
			out.close();
		}
		catch (IOException exn)
		{
			System.err.println(
					"ERROR: An I/0 exception occured when creating the "
					+ fileName + " file.");
			exn.printStackTrace();
			System.exit(1);
		}
		
		return pathName + fileName;
	}
	
	
	// New
	/**
	 * Generates the skeleton of the class which defines all the behaviors.
	 */
	static void generateSkeleton(){
	
		System.out.println("Generating the skeleton...");
		
		String destinationPath = Options.SUBDIRNAME;
		String defaultPackagePath = "";
		String destinationPackageName = "";
		
		if (Options.SUBDIR){
			
			defaultPackagePath = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
			if (Options.SUBDIRNAME.length()>defaultPackagePath.length()){
				destinationPackageName = Options.SUBDIRNAME.substring(defaultPackagePath.length()+1).replaceAll("/",".");
			}
		}
		
		//To be used outside from eclipse
		if (destinationPath==null) destinationPath=".";
		
		/* File that will contain the skeleton file */
		File file;
		PrintWriter out = null;
		
		int version = 2;
		
		file = new File(destinationPath + File.separator +  "Skeleton1.java");
		
		/* The name of the Skeleton file is the first Skeleton<number>.java available
		 * Be aware that if there is a Skeleton2.java and no Skeleton1.java, the new
		 * file will be Skeleton1.java
		 */
		while (file.exists()) {
			file = new File(destinationPath + File.separator + "Skeleton" + version + ".java");
			version++;
		}
		
		try {
			out = new PrintWriter(new FileWriter(file));
		}	catch (IOException exn)
		{
			System.err.println(
					"ERROR: An I/0 exception occured when creating the "
					+ "Skeleton file.");
			exn.printStackTrace();
			System.exit(1);
		}		
		
		/* Creation of the head of the file*/
		
		// writing the name of the destination package
		if (!destinationPath.equals(defaultPackagePath)){
			out.println("package " + destinationPackageName + ";");
			out.println();
		}
		
		out.println("/*\n"
				+ "* ###########################################################################\n"
				+ "* JavAct: A Java(TM) library for distributed and mobile actor-based computing\n"
				+ "* Copyright (C) 2001-2007 I.R.I.T./C.N.R.S.-I.N.P.T.-U.P.S.\n"
				+ "*\n"
				+ "* This library is free software; you can redistribute it and/or modify it\n"
				+ "* under the terms of the GNU Lesser General Public License as published by\n"
				+ "* the Free Software Foundation; either version 2.1 of the License, or any\n"
				+ "* later version.\n"
				+ "*\n"
				+ "* This library is distributed in the hope that it will be useful, but\n"
				+ "* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY\n"
				+ "* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public\n"
				+ "* License for more details.\n"
				+ "*\n"
				+ "* You should have received a copy of the GNU Lesser General Public License\n"
				+ "* along with this library; if not, write to the Free Software Foundation,\n"
				+ "* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA\n"
				+ "*\n"
				+ "* Initial developer(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007\n"
				+ "* Contributor(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007\n"
				+ "* Contact: javact@irit.fr\n"
				+ "* ###########################################################################\n"
				+ "*/\n");
		
		/* Creation of the basic imports
		 * Normally, no more imports than these are needed, as the Skeleton file should
		 * always be in the same package as the quasiBehavior files
		 */
		out.println("import org.javact.lang.*;");
		out.println("import org.javact.local.CreateCt;");
		out.println("import org.javact.local.SendCt;");
		out.println();
		
		Iterator behClassIter = behClassList.iterator();
		Iterator behClassNameIter = behClassNameList.iterator();
		
		// Writing the skeleton for each behavior
		
		while (behClassIter.hasNext())
		{
			String className = (String) behClassNameIter.next();
			
			
			Class behClass = (Class) behClassIter.next();
			out.println("/**\n* Behaviour for the actor " + className + "\n*/");
			out.println("class " + className+"Beh extends " + className
					+"QuasiBehavior {\n");
			
			writeMethods(behClass, out);	
			out.println("}\n");		
		}
		
		out.println("public class Skeleton" + (version-1) + " {");
		out.println("\n\tpublic static void main(String[] args) {\n\n\t}\n\n}");
		
		out.close();
	}

	private static void writeMethods(Class behClass, PrintWriter out) {

		Class[] inheritedClasses = behClass.getInterfaces() ;
		for (int i = 0; i<inheritedClasses.length; i++){
			writeMethods(inheritedClasses[i], out);
		}
		
		Method[] methods = behClass.getDeclaredMethods(); 
		String vect[];
		String returnType;
		for (int i = 0; i < methods.length; i++){
			Method method = methods[i];
			if (!(method.getName().equals("become"))){
				// method called "become" already implemented in the QuasiBehavior
				out.print("\tpublic ");
				vect = method.getReturnType().getName().split("\\.");
				returnType = vect[vect.length-1];
				out.print(returnType + " ");
				out.print(method.getName() + "(");
				Class parameters[] = method.getParameterTypes();
				for (int j = 0; j < parameters.length-1; j++){
					vect = (parameters[j].getName()).split("\\.");
					out.print(vect[vect.length-1] + " parameter" + j + ", ");							
				}
				if (parameters.length>0){
					vect = (parameters[parameters.length-1].getName()).split("\\.");
					out.print(vect[vect.length-1] + " parameter" + (parameters.length-1) + ")");
				}
				else{
					out.print(")");
				}
				out.println(" {\n\n\t}\n");
			}
		}
	}
	
}


