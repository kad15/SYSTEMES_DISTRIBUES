package org.javact.plugin.tools;


import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.swt.SWT;
import org.javact.plugin.JavActPlugin;
import org.javact.plugin.views.JVMTab;
import org.javact.plugin.views.JVMView;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Used to launch a local JVM
 *
 * @author Grégoire Sage
 * @version $Revision: 1.2 $
 */
public class JVMLauncher {
	//~ Instance fields --------------------------------------------------------
	
	private String projectPath;
	private String projectName;
	private Process p;
	private JVMView jvmView;
	private Place myPlace;
	
	//~ Constructors -----------------------------------------------------------
	
	/**
	 * Creates a new JVMLauncher object.
	 *
	 * @param project the JavAct project linked with the JVM
	 * @param _place the place of the JVM
	 * @param _jvmView the JVM view linked with the project
	 */
	public JVMLauncher(IProject project, Place _place, JVMView _jvmView) {
		projectPath = project.getLocation().toOSString();
		projectName = project.getName();
		myPlace = _place;
		jvmView = _jvmView;
	}
	
	//~ Methods ----------------------------------------------------------------
	
	/**
	 * Kills the JVM
	 */
	public void destroy() {
		p.destroy();
	}
	
	/**
	 * Gets the identifier ot the JVM
	 *
	 * @return the identifier = projectname:port
	 */
	public String getIdentifier() {
		return projectName + ":" + myPlace.getPort();
	}
	
	/**
	 * Gets the JVMView of this JVM
	 *
	 * @return the JVMView
	 */
	public JVMView getJVMView() {
		return jvmView;
	}
	
	/**
	 * Gets the place where the JVM is launched
	 *
	 * @return the place
	 */
	public Place getPlace() {
		return myPlace;
	}
	
	/**
	 * Launch the JVM
	 */
	public void run() {
		
		String[] cmd;
		
		if (myPlace.isDebug()) {
			cmd = new String[9];
			cmd[7] = "-d";
			IJavaProject javaProject = JavActUtilities.javaProject;
			cmd[8] = javaProject.getProject().getLocation().toOSString() + File.separator + ".debug";
		} else {
			cmd = new String[7];
		}
		
			cmd[0] = "java";
			cmd[1] = "-classpath";
			cmd[2] = JavActUtilities.findFileInPlugin("JavAct",
					JavActPlugin.pathJAR).toOSString();
			cmd[3] = "-Djava.security.policy=" + projectPath + File.separator +
			"awfullPolicy";
			cmd[4] = "org.javact.net.rmi.Creator";
			cmd[5] = projectPath + File.separator + "places.txt";
			cmd[6] = String.valueOf(myPlace.getPort());
			
		
		try {
			// run the JVM
			p = Runtime.getRuntime().exec(cmd);
			
			if (jvmView != null) {
				JVMTab tabItem = new JVMTab(jvmView.getTabFolder(), SWT.CLOSE,
						this);
				tabItem.setText("localhost:" + myPlace.getPort());
				
				// get the stderr and stdout to print them in the frame
				InputStreamReader messageStream = new InputStreamReader(p.getInputStream());
				BufferedReader bufMessage = new BufferedReader(messageStream);
				
				InputStreamReader errorStream = new InputStreamReader(p.getErrorStream());
				BufferedReader bufError = new BufferedReader(errorStream);
				
				new JVMMessagePrinter(tabItem, bufMessage, false).start();
				new JVMMessagePrinter(tabItem, bufError, true).start();
			}
		} catch (IOException e) {
			JavActUtilities.error("Launching JVM",
					"Unable to launch the JVM on localhost:" + myPlace.getPort(), e);
		}
	}
}
