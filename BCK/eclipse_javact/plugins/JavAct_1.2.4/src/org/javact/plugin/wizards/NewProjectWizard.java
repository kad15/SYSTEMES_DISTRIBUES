package org.javact.plugin.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.javact.plugin.JavActPlugin;
import org.javact.plugin.properties.JavActGenConfiguration;
import org.javact.plugin.tools.JavActUtilities;




/**
 * Wizard which creat a new javact project. Add the javact library
 * to the classpath and create two new files : "places.txt" and "awfullPolicy"
 *
 * @author Vincent Mériochaud
 * @version $Revision: 1.1 $
 */
public class NewProjectWizard extends JavaProjectWizard {
    //~ Constructors -----------------------------------------------------------

    // private JavaProjectWizardFirstPage fFirstPage;
    // private JavaProjectWizardSecondPage fSecondPage;    
    // private IConfigurationElement fConfigElement;

    /**
     * Creates a new NewProjectWizard object.
     */
    public NewProjectWizard() {
        super();
        setWindowTitle("New JavAct Project");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see JavaProjectWizard#addPages
     */
    public void addPages() {
        super.addPages();

        // We give a new title and a new description for the pages of the wizard
        IWizardPage[] pages = getPages();

        if (pages.length == 2) {
            pages[0].setTitle("Create a JavAct project");
            pages[0].setDescription(
                "Create a JavAct project in the workspace or in an external location.\n" +
                "http://www.irit.fr/recherches/ISPR/IAM/JavAct.html");
            pages[1].setTitle("Create a JavAct project");
            pages[1].setDescription("Define the Java build settings.\n" +
                "http://www.irit.fr/recherches/ISPR/IAM/JavAct.html");
        }
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     *
     * @return true to close the wizard, false elsewhere
     */
    public boolean performFinish() {
        boolean res = super.performFinish();

        // get project
        JavaCapabilityConfigurationPage page = (JavaCapabilityConfigurationPage) getPage(
                "JavaCapabilityConfigurationPage");

        if (page == null) {
            return res;
        }

        // update classpath
        updateClasspath(page.getJavaProject());

        // create environement files
        createEnv(page.getJavaProject());

        return res;
    }

    /**
     * Updates the project's classpath with additional JavAct libraries.
     *
     * @param aProject a java project
     */
    private void updateClasspath(IJavaProject aProject) {
        try {
            // get existing classpath
            IClasspathEntry[] existingClasspath = aProject.getRawClasspath();

            // get classpath entries for JavAct
            IPath theJar = JavActUtilities.findFileInPlugin("JavAct",
                    JavActPlugin.pathJAR);
            IClasspathEntry theJavActEntry = null;

            if (theJar == null) {
                JavActUtilities.error("JavAct project wizard",
                    "Could not create a classpath entry for javact.jar, please insert this library in your project");

                return;
            }

            //theJavActEntry = JavaCore.newLibraryEntry(theJar, tools.EclipseUtilities.findFileInPlugin("JavAct", "src/javact-src.zip"),new Path("/"));
            theJavActEntry = JavaCore.newLibraryEntry(theJar, null,
                    new Path(File.separator));

            // complete classpath if necessary
            if (theJavActEntry != null) {
                // create new classpath with additional JavAct library last
                IClasspathEntry[] newClasspath = new IClasspathEntry[existingClasspath.length +
                    1];

                for (int i = 0; i < existingClasspath.length; i++) {
                    newClasspath[i] = existingClasspath[i];
                }

                newClasspath[existingClasspath.length] = theJavActEntry;

                // set new classpath to project
                aProject.setRawClasspath(newClasspath, null);
            }
        } catch (JavaModelException e) {
            JavActUtilities.error("JavAct project wizard",
                "Could not create a classpath entry for javact.jar, please insert this library in your project",
                e);
        }
    }

    /**
     * Creates useful files in the project for javact compiler
     *
     * @param aProject a java project
     */
    private void createEnv(IJavaProject aProject) {
        // fill places.txt and create it
        JavActUtilities.createDefaultPlaces(aProject.getProject());

        // fill awfullPolicy and create it
        JavActUtilities.createDefaultPolicy(aProject.getProject());

        // create .genConfig which will save the code generation arguments
        new JavActGenConfiguration(aProject.getProject().getLocation()
                                           .toOSString()).save(aProject);
    }
}
