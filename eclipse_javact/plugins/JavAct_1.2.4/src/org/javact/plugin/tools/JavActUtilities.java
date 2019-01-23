package org.javact.plugin.tools;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.JavActPlugin;
import org.javact.plugin.views.JVMView;
import org.osgi.framework.Bundle;



/**
 * Usefull static methods for the plugin
 *
 * @author Vincent Mériochaud, Guillaume Danel, Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class JavActUtilities {
	//~ Static fields/initializers ---------------------------------------------
	
	// the selected javact project (null if no selection or if the selected project is not a javactproject)
	public static IJavaProject javaProject;
	
	// currently running JVM
	public static HashMap processJVM = new HashMap();

	public static IJavaProject javaProjectDebug;
	
	//~ Methods ----------------------------------------------------------------
	
	/**
	 * Finds a file within a plugin.
	 *
	 * @param pluginId the id of the plugin
	 * @param fileName the name of the file
	 * @return the path to the file of null, if not found
	 * @throws IOException will be raised in problem accessing ressource
	 */
	public static IPath findFileInPlugin(String pluginId, String fileName) {
		// get the bundle and its location
		Bundle theBundle = Platform.getBundle(pluginId);
		
		if (theBundle == null) {
			warning("Find in Plugin", "Could not find " + pluginId + " plugin");
			
			return null;
		}
		
		// get an entry in bundle as URL, will return bundleentry://nnn/...
		// resolve the entry as an URL, typically file://...
		URL theFileAsEntry = theBundle.getEntry(fileName);
		
		if (theFileAsEntry == null) {
			return null;
		}
		
		try {
			URL resEntry = Platform.resolve(theFileAsEntry);
			
			if (resEntry == null) {
				warning("Find in Plugin",
						"Could not find the file " + fileName + " in " + pluginId);
				
				return null;
			}
			
			// convert from URL to an IPath
			IPath thePath = new Path(new File(resEntry.getFile()).getAbsolutePath());
			
			return thePath;
		} catch (IOException ex) {
			warning("Find in Plugin",
					"Unable to find the file " + fileName + " in the plugin " +
					pluginId, ex);
			
			return null;
		}
	}
	
	/**
	 * Shows an error popup message and write the exception stack trace in the .log file
	 *
	 * @param title the title of the popup
	 * @param texte the text of the error
	 * @param ex the exception causing the error
	 */
	public static void error(String title, String texte, Exception ex) {
		// create the popup
		MessageDialog.openError(new Shell(), title,
				texte + " :\n\n" + ex.toString() +
		"\n (detailed information in .log file)");
		
		// write the error in the log
		editLog(title, texte, ex);
	}
	
	/**
	 * Writes an error in the .log file
	 *
	 * @param title the title of the error
	 * @param texte the content of the error
	 * @param ex the exception which caused the error
	 */
	public static void editLog(String title, String texte, Exception ex) {
		// the .metadata folder
		String metadataFolder = ResourcesPlugin.getWorkspace().getRoot()
		.getLocation().toOSString() +
		File.separator + ".metadata" + File.separator;
		
		try {
			// write the stack trace of the exception at the end of the .log file
			FileOutputStream fos = new FileOutputStream(metadataFolder +
					".log", true);
			OutputStreamWriter writer = new OutputStreamWriter(fos);
			writer.write(title + "\n" + texte + " :\n" + ex.getMessage());
			writer.close();
		} catch (FileNotFoundException e) {
			error("Creating error .log file",
					"Unable to open the .log file in " + metadataFolder + " : " +
					e.toString());
		} catch (IOException e) {
			error("Writing error in .log file",
					"Unable to write in the .log file :\n" + e.toString());
		}
	}
	
	/**
	 * Shows an error popup message
	 *
	 * @param title the title of the popup
	 * @param text the text of the error
	 */
	public static void error(String title, String text) {
		MessageDialog.openError(new Shell(), title, text);
	}
	
	/**
	 * Shows a warning popup message with an exception
	 *
	 * @param title the title of the popup
	 * @param text the text of the error
	 * @param ex the exception causing the error
	 */
	public static void warning(String title, String text, Exception ex) {
		MessageDialog.openWarning(new Shell(), title,
				text + " :\n\n" + ex.toString());
	}
	
	/**
	 * Shows a warning popup message
	 *
	 * @param title the title of the popup
	 * @param text the text of the warning
	 */
	public static void warning(String title, String text) {
		MessageDialog.openWarning(new Shell(), title, text);
	}
	
	/**
	 * Show an information popup message
	 *
	 * @param title the title of the popup
	 * @param text the text of the information
	 */
	public static void notify(String title, String text) {
		MessageDialog.openInformation(new Shell(), title, text);
	}
	
	/**
	 * Find all the .java files in the project
	 *
	 * @param project Project in which we must find .java files
	 *
	 * @return Array of .java file names
	 *
	 * @throws CoreException when the project isn't open or doesn't exist
	 */
	public static String[] getJavaFiles(IProject project)
	throws CoreException {
		Vector javaFiles = new Vector();
		String[] arrayJavaFiles = null;
		
		findJavaFiles(project.members(), javaFiles);
		
		arrayJavaFiles = new String[javaFiles.size()];
		
		for (int i = 0; i < arrayJavaFiles.length; i++) {
			arrayJavaFiles[i] = (String) javaFiles.elementAt(i);
		}
		
		return arrayJavaFiles;
	}
	
	/**
	 * Recursive method to find .java files in the array of IResource
	 *
	 * If the IResource is an IFile, we add its path in the vector.
	 * If the IResource is an IFolder, we get the IResource[] from this IFolder
	 * and we relaunch the method on this new array and the vector.
	 * At the end, all the .java files path are stored in the vector.
	 *
	 * @param resources Array of resources (IFile and IFolder)
	 * @param javaFiles Vector in which were stored found .java files
	 *
	 * @throws CoreException when the resources don't exist
	 */
	private static void findJavaFiles(IResource[] resources, Vector javaFiles)
	throws CoreException {
		for (int i = 0; i < resources.length; i++) {
			if (resources[i].getType() == IResource.FOLDER) {
				findJavaFiles(((IFolder) resources[i]).members(), javaFiles);
			} else if (resources[i].getType() == IResource.FILE) {
				String fileExt = ((IFile) resources[i]).getFileExtension();
				
				if (fileExt != null) {
					if (fileExt.equals("java")) {
						javaFiles.add(resources[i].getLocation().toOSString());
					}
				}
			} else {
				throw new CoreException(new Status(IStatus.ERROR, "JavAct", 0,
						"Wrong ressource", null));
			}
		}
	}
	
	/**
	 * Creates the file places.txt at the root of the project with a default content
	 *
	 * @param aProject the project in which the file must be created
	 */
	public static void createDefaultPlaces(IProject aProject) {
		final IFile placesFile = aProject.getFile(new Path("places.txt"));
		InputStream placesStream;
		
		// the file containing the default content of places.txt
		String placesDefault = JavActUtilities.findFileInPlugin("JavAct",
				JavActPlugin.pathPlaces).toOSString();
		
		try {
			placesStream = new FileInputStream(placesDefault);
		} catch (FileNotFoundException e) {
			JavActUtilities.error("places.txt creation",
					"Default configuration file places.txt was not found in " +
					placesDefault + " an empty file will be created", e);
			// the file will be empty
			placesStream = new ByteArrayInputStream("".getBytes());
		}
		
		try {
			if (placesFile.exists()) {
				placesFile.setContents(placesStream, true, true, null);
			} else {
				placesFile.create(placesStream, true, null);
			}
			
			placesStream.close();
		} catch (CoreException e) {
			JavActUtilities.error("places.txt creation",
					"Can't write the file places.txt, please create it in the workspace and add places where JVM are to be launched",
					e);
		} catch (IOException e) {
			JavActUtilities.error("places.txt creation",
					"Can't close the file places.txt, please create it in the workspace and add places where JVM are to be launched",
					e);
		}
	}
	
	/**
	 * Creates the file awfullpolicy at the root of the project with a default content
	 *
	 * @param aProject the project in which the file must be created
	 */
	public static void createDefaultPolicy(IProject aProject) {
		final IFile policyFile = aProject.getFile(new Path("awfullPolicy"));
		InputStream policyStream;
		
		// the file containing the default content of awfullPolicy
		String policyDefault = JavActUtilities.findFileInPlugin("JavAct",
				JavActPlugin.pathAwfullpolicy).toOSString();
		
		try {
			policyStream = new FileInputStream(policyDefault);
		} catch (FileNotFoundException e) {
			JavActUtilities.error("awfullPolicy creation",
					"Default configuration file awfullPolicy was not found in " +
					policyDefault + " an empty file will be created", e);
			// the file will be empty
			policyStream = new ByteArrayInputStream("".getBytes());
		}
		
		try {
			if (policyFile.exists()) {
				policyFile.setContents(policyStream, true, true, null);
			} else {
				policyFile.create(policyStream, true, null);
			}
			
			policyStream.close();
		} catch (CoreException e) {
			JavActUtilities.error("awfullPolicy creation",
					"Can't write the file awfullPolicy", e);
		} catch (IOException e) {
			JavActUtilities.error("awfullPolicy creation",
					"Can't close the file awfullPolicy", e);
		}
	}
	
	/**
	 * Parses the places.txt file and launch a JVM on each local place specified in this file
	 *
	 * @param project the project for which the JVM are launched
	 */
	public static final void launchLocalJVM(IProject project) {
		Cursor cursor = new Cursor(null, SWT.CURSOR_WAIT);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		.setCursor(cursor);
		
		String projectPath = project.getLocation().toOSString();
		String projectName = project.getName();
		String awfullPolicyPath = projectPath + File.separator +
		"awfullPolicy";
		
		// Verifying if awfullPolicy exists for the project
		try {
			BufferedReader test = new BufferedReader(new FileReader(
					awfullPolicyPath));
			// Test passed, closing the buffer
			test.close();
		} catch (FileNotFoundException e) {
			error("Launch local JVM",
					"File " + awfullPolicyPath +
					" not found, a new awfullPolicy file will be created with the default policy",
					e);
			createDefaultPolicy(project);
			
			return;
		} catch (IOException e) {
			error("Launch local JVM",
					"IO exception while accessing the file " + awfullPolicyPath, e);
			
			return;
		}
		
		// researching local places in places.txt
		Vector foundPlaces = null;
		
		try {
			foundPlaces = parsePlacesLocal(project);
		} catch (FileNotFoundException e) {
			error("Places.txt parsing",
					"File places.txt not found, a new file places.txt with default content will be created",
					e);
			createDefaultPlaces(project);
			
			return;
		} catch (IOException e) {
			error("Places.txt parsing", "Error while reading places.txt", e);
			
			return;
		}
		
		Iterator iter = foundPlaces.iterator();
		
		/*System.out.println("Searching not JVM launched local places in places.txt ...");
		
		while (iter.hasNext()) {
			System.out.println(iter.next().toString() +
			" found in places.txt");
		}
		
		System.out.println("Testing if the ports are free ...");
		*/
		iter = foundPlaces.iterator();
		
		String error = "";
		Place place;
		TestPlace testPlace;
		Vector testPlaces = new Vector();
		
		// Launching threads that will test is the ports are free
		while (iter.hasNext()) {
			place = (Place) iter.next();
			testPlace = new TestPlace(place);
			testPlaces.add(testPlace);
			testPlace.start();

		}
		
		// create the view for the JVM
		JVMView jvmView = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
		
		if (window != null) {
			try {
				jvmView = (JVMView) window.getActivePage()
				.showView(JVMView.ID,
						project.getName(), IWorkbenchPage.VIEW_VISIBLE);
			} catch (PartInitException e) {
				error("Launch Local JVM", "Error while creating JVMView", e);
				
				return;
			}
		} else {
			error("Launch Local JVM", "Can't find the active workbench window",
					null);
		}
		
		// Retrieving the results of the threads launched above
		iter = testPlaces.iterator();
		JVMLauncher jvml;
		
		while (iter.hasNext()){
			testPlace = (TestPlace) iter.next();
			
			try {
				// wait for the thread to finish
				testPlace.join();
			} catch (InterruptedException e) {
				error += e.getMessage();
			}
			
			if (!testPlace.isFree()) {
				error += ("Port " + testPlace.getPlace().getPort() +
				" is not free on localhost\n");
			} else {
				// The port is free : launching the JVM
				jvml = new JVMLauncher(project, testPlace.getPlace(), jvmView);
				jvml.run();
				processJVM.put(projectName + ":" + testPlace.getPlace().getPort(), jvml);
			}
		}
		
		if (!error.equalsIgnoreCase("")) {
			error("Port free test", error);
		}
		
		cursor.dispose();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		.getShell().setCursor(null);
	}
	
	/**
	 * Test if the JVM specified in places.txt (local and distant) are launched.
	 * return a string which contains the list of the errors.
	 * Changes the cursor while processing.
	 *
	 * @param jProject the project for which the test is done
	 *
	 * @return the string containing errors
	 */
	public static String testJVMButton(IJavaProject jProject) {
		return testJVM(jProject, true);
	}
	
	/**
	 * Test if the JVM specified in places.txt (local and distant) are launched.
	 * return a string which contains the list of the errors.
	 * Changes the cursor while processing.
	 *
	 * @param jProject the project for which the test is done
	 *
	 * @return the string containing the errors
	 */
	public static String testJVMLaunch(IJavaProject jProject) {
		return testJVM(jProject, false);
	}
	
	/**
	 * Test if the JVM specified in places.txt (local and distant) are launched.
	 * return a string which contains the list of the errors.
	 * Changes the cursor while processing is hourGlass is true.
	 *
	 * @param jProject the project for which the test is done
	 *
	 * @return the string containing the errors
	 */
	public static String testJVM(IJavaProject jProject, boolean hourGlass) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
		
		// get the current cursor 
		final Cursor[] cursor = new Cursor[1];
		Cursor oldCursor = cursor[0];
		
		if (hourGlass) {
			// change the cursor during the test
			// no available before the launch (window.getShell() == null)
			cursor[0] = new Cursor(null, SWT.CURSOR_WAIT);
			window.getShell().setCursor(cursor[0]);
		}
		
		IProject project = jProject.getProject();
		Vector foundPlaces = null;
		String error = "";
		
		try {
			// parse places.txt to get a vector of all places
			foundPlaces = parsePlacesAll(project, !hourGlass);
		} catch (FileNotFoundException e) {
			createDefaultPlaces(project);
			error += "File places.txt not found, a new file places.txt with default content will be created\n";
		} catch (IOException e) {
			error += "Error while parsing places.txt\n";
		}
		
		Iterator iter = foundPlaces.iterator();
		Vector launchedPlaces = new Vector();
		
		TestPlace testPlace;
		
		while (iter.hasNext()) {
					// create and run a thread which will test if the place is used or not
					testPlace = new TestPlace((Place) iter.next());
					testPlace.start();
					launchedPlaces.add(testPlace);
					
		}

		iter = launchedPlaces.iterator();
		
		while (iter.hasNext()) {
			testPlace = (TestPlace) iter.next();
			
			try {
				// wait for the thread to finish
				testPlace.join();
			} catch (InterruptedException e) {
				error += e.getMessage();
			}
			
			if (testPlace.isFree()) {
				error += ("There is no JVM launched on " + testPlace.getPlace().getHost() +
						":" + testPlace.getPlace().getPort() + "\n");
			}
		}
		
		// put the old cursor back
		if (hourGlass) {
			cursor[0].dispose();
			window.getShell().setCursor(oldCursor);
		}
		
		return error;
	}
	
	/**
	 * Called when closing eclipse to kill all the JVM launched wherever the current project
	 */
	public static final void killAllLocalJVM() {
		int numberJVMLaunched = processJVM.size();
		int numberJVMKilled = 0;
		String port;
		
		Iterator keyIterator = processJVM.keySet().iterator();
		
		System.out.println("\nKilling all JVM on local places ...");
		
		if (numberJVMLaunched > 1) {
			System.out.println(numberJVMLaunched +
			" JVM launched by JavAct Plugin are still alive");
		} else {
			System.out.println(numberJVMLaunched +
			" JVM launched by JavAct Plugin is still alive");
		}
		
		while (keyIterator.hasNext()) {
			port = (String) keyIterator.next();
			((JVMLauncher) processJVM.get(port)).destroy();
			numberJVMKilled++;
		}
		
		if (numberJVMLaunched > 0) {
			System.out.println("Result : " + numberJVMKilled + " JVM killed\n");
		}
		
		processJVM.clear();
	}
	
	/**
	 * Returns all the entries of the hashmap that correspond to the project
	 *
	 * @param projectName the project for which the the entries are selected
	 *
	 * @return the hashmap with only the selected entries
	 */
	public static final Vector getKeys(String projectName) {
		Vector keys = new Vector();
		String key;
		
		Iterator keyIterator = processJVM.keySet().iterator();
		
		while (keyIterator.hasNext()) {
			key = (String) keyIterator.next();
			
			if (key.startsWith(projectName + ":")) {
				keys.add(key);
			}
		}
		
		return keys;
	}
	
	/**
	 * Kill all the JVM lauched for the project when closing the project's JVMView
	 *
	 * @param projectName the projet for which the JVM will be closed
	 */
	public static final void killLocalProjectJVM(String projectName) {
		String key;
		
		System.out.println("\nKilling " + projectName +
		" JVM on local places ...");
		
		// get the key for the current project
		Vector keyToRemove = getKeys(projectName);
		
		Iterator keyToRemoveIterator = keyToRemove.iterator();
		Vector vecWellKilled = new Vector();
		
		while (keyToRemoveIterator.hasNext()) {
			key = (String) keyToRemoveIterator.next();
			removeLocalJVM(key);
			// to check if the port is free
			vecWellKilled.add(new TestWellKilled(Integer.parseInt(
					key.split(":")[1])));
		}
		
		String warning = "";
		
		// we must assure that the port are free when the method finishes
		for (int i = 0; i < vecWellKilled.size(); i++) {
			try {
				((TestWellKilled) vecWellKilled.elementAt(i)).join();
			} catch (InterruptedException e) {
				// if the thread is interrupted we can't assure that the port is free
				warning = "The thread was interrupted please wait 5 seconds please before closing this dialog";
			}
		}
		
		if (warning != "") {
			warning("Killing JVM", warning);
		}
	}
	
	/**
	 * Kill all the JVM lauched for the project when clicking Kill JVM
	 *
	 * @param projectName the projet for which the JVM will be closed
	 */
	public static final void killLocalProjectJVMfromButton(String projectName) {
		Cursor cursor = new Cursor(null, SWT.CURSOR_WAIT);
		
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
		window.getShell().setCursor(cursor);
		
		JVMView jvmView = null;
		
		// get the running jvm for this project
		Vector keyToRemove = getKeys(projectName);
		String key;
		
		if (window != null) {
			try {
				// hide the JVm vue of this project
				jvmView = (JVMView) window.getActivePage()
				.showView(JVMView.ID, projectName,
						IWorkbenchPage.VIEW_CREATE);
				window.getActivePage().hideView(jvmView);
			} catch (PartInitException e) {
				error("", "Error while hiding JVMView", e);
			}
			
			Iterator keyToRemoveIterator = keyToRemove.iterator();
			Vector vecWellKilled = new Vector();
			TestWellKilled t;
			
			while (keyToRemoveIterator.hasNext()) {
				key = (String) keyToRemoveIterator.next();
				t = new TestWellKilled(Integer.parseInt(key.split(":")[1]));
				vecWellKilled.add(t);
				t.start();
			}
			
			String warning = "";
			
			for (int i = 0; i < vecWellKilled.size(); i++) {
				try {
					((TestWellKilled) vecWellKilled.elementAt(i)).join();
				} catch (InterruptedException e) {
					// if the thread is interrupted we can't assure that the port is free
					warning = "The thread was interrupted please wait 5 seconds please before closing this dialog";
				}
			}
			
			if (warning != "") {
				warning("Killing JVM", warning);
			}
		} else {
			error("", "Can't find the active workbench window", null);
		}
		
		cursor.dispose();
		window.getShell().getShell().setCursor(null);
	}
	
	/**
	 * Kills only one JVM
	 *
	 * @param key the key in the Hashmap of the JVM to kill
	 */
	public static final void removeLocalJVM(String key) {
		((JVMLauncher) processJVM.get(key)).destroy();
		processJVM.remove(key);
	}
	
	/**
	 * Gets the selected java project
	 *
	 * @param sel the selection
	 *
	 * @return the selected java project
	 */
	public static IJavaProject getIJavaProjectFromSelection(ISelection sel) {
		IJavaProject javaProject = null;
		
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) sel;
			
			Object object = structuredSelection.getFirstElement();
			
			if ((object != null) && (object instanceof ClassPathContainer)) {
				javaProject = ((ClassPathContainer) object).getJavaProject();
			} else if ((object != null) &&
					(object instanceof JarPackageFragmentRoot)) {
				javaProject = ((JarPackageFragmentRoot) object).getJavaProject();
			} else if ((object != null) && (object instanceof IAdaptable)) {
				IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
				javaProject = getIJavaProjectContaining(resource);
			}
			
			if (!isJavActProject(javaProject)) {
				javaProject = null;
			}
		}
		
		return javaProject;
	}
	
	/**
	 * Find the javAct project (a JavAct project is a java project containing a .genConfig file) containing the resource in parameter
	 * return null if it's not a JavAct project or if the resource can't be found
	 *
	 * @param res the ressource
	 *
	 * @return the found JavAct project or null
	 */
	public static IJavaProject getIJavaProjectContaining(IResource res) {
		IJavaProject foundProject = null;
		IJavaProject tmpProject = null;
		String javaProjectPath;
		
		IJavaProject[] javaProjects;
		
		try {
			javaProjects = JavaCore.create(ResourcesPlugin.getWorkspace()
					.getRoot())
					.getJavaProjects();
			
			int i = 0;
			
			while ((i < javaProjects.length) && (foundProject == null)) {
				tmpProject = javaProjects[i];
				
				javaProjectPath = tmpProject.getProject().getLocation()
				.toOSString();
				
				if (res.getLocation().toOSString().startsWith(javaProjectPath)) {
					foundProject = tmpProject;
				}
				
				i++;
			}
		} catch (JavaModelException e) {
			error("Internal eror",
					"Unable to find the projects in the workspace", e);
		}
		
		return foundProject;
	}
	
	/**
	 * Return true if the project is a JavAct project
	 *
	 * @param aProject the project
	 *
	 * @return true if the project is a JavAct project
	 */
	private static boolean isJavActProject(IJavaProject aProject) {
		if (aProject != null) {
			return aProject.getProject().getFile(new Path(".genConfig")).exists();
		} else {
			return false;
		}
	}
	
	/**
	 * Parse the file places.txt and return all the JVM
	 *
	 * @param aProject The project which JVM have to be tested
	 *
	 * @return All the machines of places.txt in a Vector of Place
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Vector parsePlacesAll(IProject aProject, boolean isRun)
	throws FileNotFoundException, IOException {
		return parsePlaces(aProject, false, isRun);
	}
	
	/**
	 * Parse the file places.txt and return all the local JVM
	 *
	 * @param aProject The project which JVM have to be tested
	 *
	 * @return All the local machines of places.txt in a Vector of Place
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Vector parsePlacesLocal(IProject aProject)
	throws FileNotFoundException, IOException {
		return parsePlaces(aProject, true, false);
	}
	
	/**
	 * Parse the file places.txt and return all the JVM or only the local JVM
	 * depending on the localParse value
	 *
	 * @param aProject The project which JVM have to be tested
	 * @param localParse true if only local JVM has to be returned
	 * @param isRun true is the method is called during the "run"
	 *
	 * @return All the machines of places.txt in a Vector of Place according to localParse
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static Vector parsePlaces(IProject aProject, boolean localParse, boolean isRun)
	throws FileNotFoundException, IOException {
		String projectPath = aProject.getLocation().toOSString();
		String placesFile = projectPath + File.separator + "places.txt";
		Vector places = new Vector();
		
		BufferedReader in = new BufferedReader(new FileReader(placesFile));
		String s;
		String error = "";
		int index;
		boolean debug;
		String host;
		int port;
		Place place = null;
		int indexSpace;
		int indexTab;
		
		while ((s = in.readLine()) != null) {
			debug = false;
			s = s.trim();
			
			if ((s.length() != 0) && (!s.startsWith("#"))) {
				// skip blanklines and lines starting with #
				
				indexSpace = s.indexOf(" ");
				indexTab = s.indexOf("\t");
				if (indexSpace == -1){
					index = indexTab;
				} else if (indexTab == -1) {
					index = indexSpace;
				} else {
					index = Math.min(indexTab, indexSpace);
				}
				if (index != -1) { // index always != 0 because of the s.trim()
					debug = s.substring(index).trim().equalsIgnoreCase("debug");
					s = s.substring(0,index);
				}
				
				
				// if no port defined : defaut rmi port (1099)
				int i = s.indexOf(":");
				
				if (i == -1) {
					place = new Place(s, 1099, debug);
				} else {
					host = s.substring(0,i);
					try {
						port = Integer.parseInt(s.substring(i+1));
						if ((port < 1099) || (port > 65535)) {
							error += "Error in " + s + " : port out of range\n";
						} else {
							place = new Place(host, port, debug);
						}
					} catch (NumberFormatException e) {
						error += "Error in " + s + " : " + s.substring(i+1) + " is not a well typed port\n";
					}
				}
				if (place != null) {
					if (!places.contains(place)) {
						if (!localParse) {
							places.add(place);                		
						} else if (place.getHost().equals("localhost") || place.getHost().equals("127.0.0.1")) {
							places.add(place);
						}
					} 
				}
			}
		}
		
		in.close();
		
		if (!isRun && error != "") {
			error("places.txt Parse", error);
		}
		
		return places;
	}
}
