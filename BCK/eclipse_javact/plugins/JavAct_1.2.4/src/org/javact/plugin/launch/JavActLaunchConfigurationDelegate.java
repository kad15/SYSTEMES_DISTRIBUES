package org.javact.plugin.launch;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.javact.plugin.tools.JavActUtilities;


import sun.net.www.protocol.ftp.FtpURLConnection;


/**
 * This class allows one to analyze what is to be executed
 *
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class JavActLaunchConfigurationDelegate extends JavaLaunchDelegate {
    //~ Methods ----------------------------------------------------------------

    /**
     * @see JavaLaunchDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void launch(ILaunchConfiguration configuration, String mode,
        ILaunch launch, IProgressMonitor monitor) throws CoreException {
        // The project that contains the file being to be launched
        IJavaProject javaProject = getJavaProject(configuration);

        // a boolean telling if the application is to be launch in rmi or in local
        boolean rmi = configuration.getWorkingCopy().getAttribute("rmi", true);

        // the VM arguments
        String vmAgruments = configuration.getWorkingCopy()
                                          .getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                "");

        String error = "";

        // Testing if a project has been selected
        if (javaProject == null) {
            abort("There is no project selected\n\nExecution aborted",
                new Throwable(), 1);
        }

        if (rmi) {
            /* Extracting the codebase and its type from the VM arguments
             * example : codebase = D:/eclipse/runtime-EclipseApplication/ppokpok/ and type = file
            */
            int debutType = vmAgruments.indexOf("=") + 1;
            int debutCode = vmAgruments.indexOf(":") + 1;
            int finCode = vmAgruments.indexOf(" ");
            String type = "";
            String codeBase = "";

            try {
                type = vmAgruments.substring(debutType, debutCode - 1);
                codeBase = vmAgruments.substring(debutCode, finCode);
            } catch (IndexOutOfBoundsException e) {
                abort(
                    "Invalid VM arguments (click on \"Run a network application\" in the " +
                    "launch configuration to see well formed ones)\n\nExecution aborted",
                    e, 1);
            }

            // Verifying the codebase
            if (type.equals("file")) {
                if (!codeBase.endsWith("/")) {
                    abort(
                        "The codebase of the VM arguments must end with a \"/\" when " +
                        "it is declared as \"file\"\n\nExecution aborted",
                        new Throwable(), 1);
                }

                File file = new File(codeBase.replaceAll("%20", " "));

                if (file.exists()) {
                    if (!file.isDirectory()) {
                        abort("The codebase of the VM arguments is not a directory\n\nExecution aborted",
                            new Throwable(), 1);
                    }
                } else {
                    abort("The codebase of the VM arguments is not an existing directory\n\nExecution aborted",
                        new Throwable(), 1);
                }
            } else if (type.equals("http")) {
                URL url = null;

                if (!codeBase.startsWith("//")) {
                    abort("The codebase URL in the VM arguments is malformed : http:// expected\n\nExecution aborted",
                        new Throwable(), 1);
                }

                if (codeBase.charAt(2) == '/') { // codebase initially had only one or more than two slashes
                    abort("The codebase URL in the VM arguments is malformed\n\nExecution aborted",
                        new Throwable(), 1);
                }

                try {
                    url = new URL("http:" + codeBase);
                } catch (MalformedURLException e) {
                    abort("The codebase URL in the VM arguments is malformed\n\nExecution aborted",
                        e, 1);
                }

                int code = 0;
                HttpURLConnection connect = null;

                try {
                    connect = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    abort("Unable to connect to the URL specified in the codebase of the VM arguments : server not found\n\nExecution aborted",
                        e, 1);
                }

                try {
                    code = connect.getResponseCode();
                } catch (IOException e) {
                    abort("Unable to connect to the URL specified in the codebase of the VM arguments : server not found\n\nExecution aborted",
                        e, 1);
                }

                try {
                    connect.connect();
                } catch (IOException e) {
                    abort(
                        "Unable to connect to the URL specified in the codebase of the VM arguments : error " +
                        code + "\n\nExecution aborted", new Throwable(), 1);
                }

                if (!(code == HttpURLConnection.HTTP_OK)) {
                    abort(
                        "Unable to connect to the URL specified in the codebase of the VM arguments : error " +
                        code + "\n\nExecution aborted", new Throwable(), 1);
                    ;
                }

                connect.disconnect();
            } else if (type.equals("ftp")) {
                URL url = null;

                if (!codeBase.startsWith("//")) {
                    abort("The codebase URL in the VM arguments is malformed : ftp:// expected\n\nExecution aborted",
                        new Throwable(), 1);
                }

                if (codeBase.charAt(2) == '/') { // codebase initially had more than two slashes
                    abort("The codebase URL in the VM arguments is malformed\n\nExecution aborted",
                        new Throwable(), 1);
                }

                try {
                    url = new URL("ftp:" + codeBase);
                } catch (MalformedURLException e) {
                    abort("The codebase URL in the VM arguments is malformed\n\nExecution aborted",
                        e, 1);
                }

                FtpURLConnection connect = null;

                try {
                    connect = (FtpURLConnection) url.openConnection();
                } catch (IOException e) {
                    abort("Unable to connect to the FTP specified in the codebase of the VM arguments : server not found\n\nExecution aborted",
                        e, 1);
                }

                try {
                    connect.connect();
                } catch (IOException e) {
                    abort("Unable to connect to the FTP specified in the codebase of the VM arguments : server not found\n\nExecution aborted",
                        new Throwable(), 1);
                }

                try {
                    connect.getInputStream();
                } catch (IOException e) {
                    abort("Unable to connect to the FTP specified in the codebase of the VM arguments : directory not found\n\nExecution aborted",
                        new Throwable(), 1);
                }

                connect.close();
            }
        }

        /* Testing if the JVM of places.txt are launched or not only
         * if the user specified he wanted this test to be done
         */
        if (configuration.getWorkingCopy().getAttribute("test", true)) {
            error += JavActUtilities.testJVMLaunch(javaProject);
        }

        if (!error.equals("")) {
            this.abort(error + "\nExecution aborted", new Throwable(), 1);
        } else {
            
            // The debug file must be related to the last run executed, so it is deleted
            // if it already exists
            File debugFile = new File(javaProject.getProject().getLocation().toOSString() + File.separator + ".debug");
            if (debugFile.exists()) {
            	debugFile.delete();
            }        	
        	
            super.launch(configuration, mode, launch, monitor);
        }
    }
}
