package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.javact.plugin.actions.ActionDebug;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action "NextEvent"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class NextEventAction implements IWorkbenchWindowActionDelegate {
    //~ Methods ----------------------------------------------------------------

    /**
     * Run the NextEvent action
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if (ActionDebug.debug == null) {
            JavActUtilities.error("Debug Mode", "There is no project selected");

            return;
        }

        int step = NumberEventAction.actionChecked;

        for (int i = 0; i < step; i++) {
            if (ActionDebug.debug.getStep() < ActionDebug.debug.getNbEvents()) {
                ActionDebug.debug.takeEventIntoAccount(ActionDebug.debug.getStep());
                ActionDebug.debug.incStep(1);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
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
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        /*
         * Nothing to do
         */
    }
}
