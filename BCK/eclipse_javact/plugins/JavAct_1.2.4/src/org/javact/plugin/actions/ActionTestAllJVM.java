package org.javact.plugin.actions;


import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action button "Test JVM Local"
 *
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class ActionTestAllJVM extends Action
    implements IWorkbenchWindowActionDelegate {
    //~ Methods ----------------------------------------------------------------

    /**
    * @see org.eclipse.ui.IActionDelegate#run
    */
    public void run(IAction action) {
        run();
    }

    /**
     * Launches the test on local JVM
     */
    public void run() {
        // the SelectionChanged method from the ActionJavActGen class assure that JavActUtilities.javaProject is the selected project
        IJavaProject javaProject = JavActUtilities.javaProject;

        if (javaProject != null) {
            String error = JavActUtilities.testJVMButton(javaProject);

            if (error == "") {
                JavActUtilities.notify("Testing JVM",
                    "The test is a success : all the JMV of places.txt are running");
            } else {
                JavActUtilities.notify("Testing JVM", error);
            }
        } else {
            JavActUtilities.warning("Testing JVM",
                "A JavAct project must be selected");
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        JavActUtilities.javaProject = JavActUtilities.getIJavaProjectFromSelection(selection);
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
}
