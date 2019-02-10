package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.javact.plugin.JavActPlugin;
import org.javact.plugin.actions.ActionDebug;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action button "Play"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class PlayEventAction implements IWorkbenchWindowPulldownDelegate {
    //~ Constructors -----------------------------------------------------------

    /**
     * The constructor.
     */
    public PlayEventAction() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if (ActionDebug.debug == null) {
            JavActUtilities.error("Debug Mode", "There is no project selected");

            return;
        }

        while (ActionDebug.debug.getStep() < ActionDebug.debug.getNbEvents()) {
            ActionDebug.debug.takeEventIntoAccount(ActionDebug.debug.getStep());
            ActionDebug.debug.incStep(1);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        /*
         * Nothing to do
         */
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

    /**
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu(Control parent) {
        Menu m = new Menu(parent);
        ImageDescriptor image = JavActPlugin.getImageDescriptor(
                "icons/debug/reset.gif");

        ActionContributionItem aci1 = new ActionContributionItem(new ResetAction());
        aci1.getAction().setText("Reset");
        aci1.getAction().setImageDescriptor(image);

        aci1.fill(m, -1);

        return m;
    }
}
