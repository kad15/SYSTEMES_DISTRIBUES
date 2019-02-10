package org.javact.plugin.actions;


import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action button "Kill JVM Local"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class ActionKillJVMLocal extends Action
    implements IWorkbenchWindowActionDelegate {
    //~ Methods ----------------------------------------------------------------

    /**
    * @see org.eclipse.ui.IActionDelegate#run
    */
    public void run(IAction action) {
        run();
    }

    /**
     * Kills the local JVM linked with selected project
     * 
     * @see Action#run()
     */
    public void run() {
        // the SelectionChanged method from the ActionJavActGen class assure that JavActUtilities.javaProject is the selected project
        IJavaProject javaProject = JavActUtilities.javaProject;

        if (javaProject != null) {
            JavActUtilities.killLocalProjectJVMfromButton(javaProject.getProject()
                                                                     .getName());
        } else {
            JavActUtilities.warning("Killing JVM",
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
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {
        /*
         * Nothing to do
         */
    }

    /**
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        /*
         * Nothing to do
         */
    }
}
