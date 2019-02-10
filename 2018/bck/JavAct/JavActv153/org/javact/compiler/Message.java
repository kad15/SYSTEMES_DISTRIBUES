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
 * Provides methods for processing the generation of message handling java
 * files.<br>
 * A <code>Message</code> object is described by:
 * 
 * <ul>
 *     <li>its name: this is the name of the method declared in a behavior
 *     interface;</li>
 *     <li>its return type: this is the return type of the method. If the
 *     return type is <code>void</code> the message is an asynchronous message
 *     (see {@link AsynchronousMessage}); otherwise it is a synchronous message
 *     (see {@link SynchronousMessage}).</li>
 *     <li>a list of signatures which represent the possible formal parameters
 *     for the method. When the method is overloaded this list contains more
 *     than one element.</li>
 * </ul>
 * 
 * <p>During the processing of behavior profiles, when a couple &lt;method
 * name&gt; + &lt;return type&gt; is encountered for the first time, a new
 * <code>Message</code> instance is created. Afterwards the signature is just
 * added to the message's list of signatures (see {@link
 * Message#addSignature}).</p>
 * <p>This class is composed of a static part interfacing with the compiler's
 * other classes. It is the same structure as in {@link BehaviorProfile}.</p>
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
abstract class Message
{
	/* ------------------------- STATIC PART ------------------------- */
	
	/** Map of the application's messages. The keys are <code>String</code>
	 * objects composed of the (a)synchronous keyword concatenated with the
	 * message name.
	 */
	static private Map knownMessagesMap = new HashMap();
	
	/** Add the message to the collection of existing messages.
	 * @param method the corresponding method.
	 * @param owningInterface the BehaviorProfile interface
	 * declaring the given method.
	 * @return the corresponding <code>Message</code> instance.
	 */
	static Message addMessage(Method method, Class owningInterface)
	{
		/* Message instance that will be created and returned */
		Message newMessage;
		
		Class returnType = method.getReturnType();
		/* Key that will be mapped to this message in knownMessagesMap */
		String key = returnType + method.getName();
		
		if (knownMessagesMap.containsKey(key))
		{
			newMessage = (Message) knownMessagesMap.get(key);
			newMessage.addSignature(method, owningInterface);
		}
		else
		{
			if (returnType == Void.TYPE)
			{
				newMessage = new AsynchronousMessage(method, owningInterface);
				knownMessagesMap.put(key, newMessage);
			}
			else
			{
				newMessage = new SynchronousMessage(method, owningInterface);
				knownMessagesMap.put(key, newMessage);
			}
		}
		
		return newMessage;
	}
	
	/** Returns <code>true</code> if there are messages to be generated.
	 * It may not be the case when no behavior profile contains messages or
	 * when programs were compiled with the <kbd>&ndash;d</kbd> option but the
	 * corresponding directory was not added in the <var>CLASSPATH</var>.
	 */
	static boolean areMessagesToGenerate()
	{
		return !knownMessagesMap.isEmpty();
	}
	
	/**
	 * Generates the message handling java files.
	 *
	 * @return <code>String</code> list of filenames corresponding to the
	 * generated files.
	 * @see Message#generate()
	 */
	static List generateMessages()
	{
		List messageFileNamesList = new LinkedList();
		Iterator messageIter = knownMessagesMap.values().iterator();
		
		while (messageIter.hasNext())
		{
			Message message = (Message) messageIter.next();
			messageFileNamesList.add(message.generate());
		}
		
		return messageFileNamesList;
	}
	
	
	
	
	/* ------------------------- NON-STATIC PART ------------------------- */
	
	/** Name of the message. Should be accessed only by subclasses. */
	String name;
	
	/**
	 * Name of the java Class that will implement the handling of this
	 * message.  Should be accessed only by subclasses.
	 * See {@link Message#getMessageClassName}.
	 */
	protected String messageClassName;
	
	/** List of all the different signatures of this message. Should be
	 * accessed only by subclasses. */
	protected List signaturesList;
	
	/**
	 * Creates a new <code>Message</code> object corresponding to the given
	 * method.
	 *
	 * @param method the method corresponding to the message.
	 * @param owningBehavInterface the behavior interface declaring this
	 *	      message.
	 */
	Message(Method method, Class owningBehavInterface)
	{
		name = method.getName();
		signaturesList = new LinkedList();
		addSignature(method, owningBehavInterface);
	}
	
	/**
	 * Returns the name of the class that will implement the handling of this
	 * message. If it is an asynchronous message it will just be the name of
	 * the message.  Otherwise the name of the return type will also be
	 * appended to the message name.
	 *
	 * @return the name of the message handling class.
	 */
	abstract String getMessageClassName();
	
	/* Note: this method could be implemented here, it justs returns the value
	 * of the field messageClassName. It was made abstract in order to generate
	 * a right Doc comment for the subclasses. Otherwise the Synchronous class
	 * would explain how the name of asynchronous messages is handled, and
	 * vice-versa
	 */
	
	/**
	 * Returns the name of the file that will contain this class handling this
	 * message. It is simply the result of {@link Message#getMessageClassName}
	 * with the string ".java" added.
	 *
	 * @return the filename correspondig to this message.
	 */
	String getMessageFileName()
	{
		return getMessageClassName() + ".java";
	}
	
	/**
	 * Adds the given signature to the list of already known signatures. If
	 * this message already contains the given signature, the behavior
	 * interface will simply be added to the signature's set of known behavior
	 * interfaces. Otherwise a new <code>Signature</code> instance will be
	 * created.
	 *
	 * @param _signature the method whose signature has to be added.
	 * @param _owningBehavInterface the behavior interface declaring the given
	 *	      method.
	 *
	 * @see Signature
	 */
	void addSignature(Method _signature, Class _owningBehavInterface)
	{
		/* We don't want to needlessly create a Signature object if the
		 * signature already is present in the signatures list.  We can't use
		 * the indexOf method because it would call the Method.equals method
		 * for comparison.  Thus we have to run through the list and compare
		 * the signatures with our own Signature.equals method */
		boolean isPresent = false;
		Iterator signListIter = signaturesList.iterator();
		Signature currentSignature = null;
		
		while (!isPresent && signListIter.hasNext())
		{
			currentSignature = (Signature) signListIter.next();
			isPresent = currentSignature.equals(_signature);
		}
		
		if (isPresent)
		{
			currentSignature.addOwningInterface(_owningBehavInterface);
		}
		else
		{
			signaturesList.add(
					new Signature(_signature, _owningBehavInterface, this));
		}
	}
	
	/**
	 * Prints out the line "<code>public class &lt;message name&gt;
	 * implements</code>..." in the given stream. This line depends on whether
	 * the message is asynchronous or synchronous, thus this method is an
	 * abstract method that is implemented in the <code>Message</code>
	 * subclasses (and should only be accessed by those ones).<br>
	 * If the message is synchronous, this method also prints out the
	 * declaration of the field that will contain the result of an invocation
	 * of this message.
	 *
	 * @param out the stream pointing to the file that will contain the message
	 *	      handling class.
	 */
	abstract protected void printClassDeclaration(PrintWriter out);
	
	/**
	 * Prints out the call to the method implementing this message, declared by
	 * the given behavior interface, and owning the given parameters. This
	 * call depends on whether the message is asynchronous or synchronous,
	 * thus this method is an abstract method that is implemented in the
	 * <code>Message</code> subclasses.<br>
	 * In case of a synchronous message, the
	 * <code>getServerSideAnswerBack().setReply</code> line also is printed
	 * out.<br>
	 * This method is invoked by {@link Signature#printInvoke}, as the
	 * <code>Signature</code> instance does not want to know whether its
	 * owning message is synchronous or asynchronous.
	 *
	 * @param out the stream pointing to the file that will contain the message
	 *	      handling class.
	 * @param behavInterfName the name of the behavior interface that declares
	 *	      such a message.
	 * @param argsList the real parameters of the method (that is its
	 *	      signature).
	 *
	 * @see Signature#printInvoke
	 */
	abstract void printCall(
			PrintWriter out,
			String behavInterfName,
			String argsList);
	
	/**
	 * Finishes the generation of the message handling class. In case of a
	 * synchronous message the <code>getReply</code> method still has to be
	 * printed out.<br>
	 * The stream is closed within this method.<br>
	 * This method should only be accessed by subclasses.
	 * @param out the stream that will be printed out.
	 */
	abstract protected void printEndOfGeneration(PrintWriter out);
	
	/**
	 * Starts the generation of the class handling this message.
	 * @return the corresponding filename
	 */
	private String generate()
	{
		/* File that will contain the message handling code */
		String fileName = getMessageFileName();
		File file;
		PrintWriter out;
		
		String pathName = "";
		if (Options.SUBDIR){
			pathName += Options.SUBDIRNAME + File.separator;
		}
		if (Options.PACKAGE)
		{
			
			// New
			// The default packages path
			if (System.getProperty("os.name").regionMatches(true, 0, "Windows", 0, 7)){
				pathName = System.getProperty("java.class.path").split(";")[0];
			} else {
				pathName = System.getProperty("java.class.path").split(":")[0];
			}
			pathName += File.separator + Options.PACKAGENAME.replace('.', File.separatorChar)
			+ File.separator;
		}
		
		file = new File(pathName + fileName);
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
			{
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
			out = new PrintWriter(new FileWriter(file));
			
			// New
			/* Creation of the head of the file*/
			
			if (Options.SUBDIR && !Options.PACKAGE){
				String destinationPath = Options.SUBDIRNAME;
				String defaultPackagePath = "";
				defaultPackagePath = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
				String destinationPackageName = "";
				if (Options.SUBDIRNAME.length()>defaultPackagePath.length()){
					destinationPackageName = Options.SUBDIRNAME.substring(defaultPackagePath.length()+1).replaceAll("/",".");
				}
				if (!destinationPath.equals(defaultPackagePath)){
					out.println("package " + destinationPackageName + ";");
					out.println();
				}
			}
			if (Options.PACKAGE) {
				out.println("package " + Options.PACKAGENAME + ";") ;
				out.println() ;
			}
			
			
			/* Print out the line "public class ..."
			 * This depends on whether the message is synchronous or
			 * asynchronous, so we use an abstract method */
			printClassDeclaration(out);
			
			/* Line signatureNumber, for the different signatures of the same
			 * message */
			out.println("\tprivate int signatureNumber ;");
			out.println();
			
			/* Now print out each signature attributes & constructor */
			Iterator signatureIter = signaturesList.iterator();
			int signatureNumber = 0;
			
			while (signatureIter.hasNext())
			{
				((Signature) signatureIter.next()).printConstructor(
						out,
						signatureNumber);
				signatureNumber++;
			}
			
			/* Print out the "handle" method definition */
			out.println( "\tpublic final void " + Keywords.INVOKE + "("
					+ Keywords.JAVACT_PACKAGE + "." + Keywords.QUASI_BEHAVIOR
					+ " _behavior)");
			out.println("\t{");
			out.println("\t\tswitch (signatureNumber)");
			out.println("\t\t{");
			
			/* Print out the "case" statements for each signature */
			signatureIter = signaturesList.iterator();
			signatureNumber = 0;
			
			while (signatureIter.hasNext())
			{
				((Signature) signatureIter.next()).printInvoke(
						out,
						signatureNumber);
				signatureNumber++;
			}
			
			/* Print out the "default" statement */
			out.println("\t\t\tdefault :");
			out.println("\t\t\t\tthrow new org.javact.lang.MessageHandleException() ;");
			
			/* End of switch brace */
			out.println("\t\t}");
			
			/* End of function brace */
			out.println("\t}");
			
			/* The closing of the stream will be done in the subclass; if the
			 * message is a synchronous message we still have to print out the
			 * getReply function : call to an abstract method */
			printEndOfGeneration(out);
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
}
