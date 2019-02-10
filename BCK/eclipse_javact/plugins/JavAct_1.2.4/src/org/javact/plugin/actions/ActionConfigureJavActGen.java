package org.javact.plugin.actions;


import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.properties.JavActGenConfigurationDialog;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action button "Configure JavActGen..."
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class ActionConfigureJavActGen extends Action
    implements IWorkbenchWindowActionDelegate {
    //~ Methods ----------------------------------------------------------------

    /**
     * Launch the "Configure JavActGen..."
     */
    public void run() {
        // the SelectionChanged method from the ActionJavActGen class ensures that JavActUtilities.javaProject is the selected project
        IJavaProject javaProject = JavActUtilities.javaProject;

        if (javaProject != null) {
            JavActGenConfigurationDialog tad = new JavActGenConfigurationDialog(PlatformUI.getWorkbench()
                                                                                          .getActiveWorkbenchWindow()
                                                                                          .getShell(),
                    javaProject);
            tad.open();
        } else {
            JavActUtilities.warning("Configure JavActGen",
                "A JavAct project must be selected before configuring JavActGen");
        }
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
     * @see org.eclipse.ui.IActionDelegate#run
     */
    public void run(IAction action) {
        run();
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
}
