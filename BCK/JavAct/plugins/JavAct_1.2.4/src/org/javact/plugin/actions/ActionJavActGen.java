package org.javact.plugin.actions;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.javact.plugin.JavActPlugin;
import org.javact.plugin.properties.JavActGenConfiguration;
import org.javact.plugin.tools.ConsoleDisplayMgr;
import org.javact.plugin.tools.JavActUtilities;
import org.javact.plugin.tools.MessagePrinter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * This class represents the action button "JavActGen..."
 *
 * @author Grégoire Sage
 * @version $Revision: 1.2 $
 */
public class ActionJavActGen implements IWorkbenchWindowPulldownDelegate {
    //~ Methods ----------------------------------------------------------------

    /**
    * @see org.eclipse.ui.IActionDelegate#run
    */
    public void run(IAction action) {
        // We get the resource from the selection (SelectionChanged assure that JavActUtilities.javaProject is the selected project)
        IJavaProject javaProject = JavActUtilities.javaProject;

        if (javaProject != null) {
            try {
                // We get the project from the ressource
                IProject project = javaProject.getProject();
                new ConsoleDisplayMgr("JavActGen : " + project.getName());

                if (project.isOpen()) {
                    // We get the project .java files
                    String[] arrayJavaFiles = JavActUtilities.getJavaFiles(project);

                    // We execute the generation
                    executeJavActGen(arrayJavaFiles, javaProject);

                    // We refresh the eclipse project path
                    project.refreshLocal(IResource.DEPTH_INFINITE, null);
                } else {
                    JavActUtilities.warning("JavActGen",
                        "The project is closed.\nYou must open it in order to launch JavActGen");
                }
            } catch (CoreException e) {
                JavActUtilities.error("JavActGen",
                    "Unable to find .java files", e);
            } catch (InterruptedException e) {
                JavActUtilities.error("JavActGen",
                    "Error while generating files", e);
            } catch (IOException e) {
                JavActUtilities.error("JavActGen",
                    "Unable to run the javact generation", e);
            }
        } else {
            JavActUtilities.warning("JavActGen",
                "A JavAct project must be selected before launching JavActGen");
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        JavActUtilities.javaProject = JavActUtilities.getIJavaProjectFromSelection(selection);
    }

    /**
     * Execute the QuasiBehaviour, JAM and squeleton generation of the .java files
     * in the specific directory
     *
     * @param javaFiles the String tab of java files in which find the actors
     * @param javaProject the javact project linked with the generation
     */
    private void executeJavActGen(String[] javaFiles, IJavaProject javaProject)
        throws IOException, InterruptedException {
        String projectPath = javaProject.getProject().getLocation().toOSString();

        JavActGenConfiguration genConfig = JavActGenConfiguration.load(javaProject);
        String[] options = new String[9];

        options = genConfig.getOptions();

        String[] cmdBegin = new String[14];

        int indice;

        // construct the command
        if (System.getProperty("os.name").regionMatches(true, 0, "Windows", 0, 7)) {
            //   java -classpath projectPath;javactJarPath -v -generated-dir projectPath
            cmdBegin[0] = "java";
            cmdBegin[1] = "-classpath";
            cmdBegin[2] = "\"" + projectPath + "\";\"" +
                JavActUtilities.findFileInPlugin("JavAct", JavActPlugin.pathJAR)
                               .toOSString() + "\"";
            cmdBegin[3] = "org.javact.compiler.Main";
            indice = 0;

            while (options[indice] != null) {
                cmdBegin[4 + indice] = options[indice];
                indice++;
            }

            cmdBegin[4 + indice] = null;
        } else {
            cmdBegin[0] = "java";
            cmdBegin[1] = "-classpath";
            cmdBegin[2] = projectPath + System.getProperty("path.separator") +
                JavActUtilities.findFileInPlugin("JavAct", JavActPlugin.pathJAR)
                               .toOSString();
            cmdBegin[3] = "org.javact.compiler.Main";
            indice = 0;

            while (options[indice] != null) {
                cmdBegin[4 + indice] = options[indice];
                indice++;
            }

            cmdBegin[4 + indice] = null;
        }

        // We create the cmd line by concataining cmdBegin and javaFiles
        String[] cmd = new String[4 /* 4 is cmdBegin minimum length */ +
            indice + javaFiles.length];

        for (int i = 0; cmdBegin[i] != null; i++) {
            cmd[i] = cmdBegin[i];
        }

        for (int i = 0; i < javaFiles.length; i++) {
            cmd[i + 4 /* 4 is cmdBegin minimum length */ + indice] = javaFiles[i];
        }

        // We execute the command
        Process p = Runtime.getRuntime().exec(cmd);

        // We redirect the ErrorStream end the InputStream to the standard output with Threads
        InputStreamReader errorStream = new InputStreamReader(p.getErrorStream());
        BufferedReader bufError = new BufferedReader(errorStream);

        InputStreamReader messageStream = new InputStreamReader(p.getInputStream());
        BufferedReader bufMessage = new BufferedReader(messageStream);

        new MessagePrinter(bufError, true).start();
        new MessagePrinter(bufMessage, false).start();

        // We wait for the end execution of the command
        p.waitFor();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {
        /*
         * Nothing to do
         */
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        /*
         * Nothing to do
         */
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu
     */
    public Menu getMenu(Control parent) {
        Menu m = new Menu(parent);
        ActionContributionItem aci = new ActionContributionItem(new ActionConfigureJavActGen());
        aci.getAction().setText("Configure JavActGen...");
        aci.getAction()
           .setImageDescriptor(JavActPlugin.getImageDescriptor(
                "icons/javactgenconf.gif"));
        aci.fill(m, -1);

        return m;
    }
}
