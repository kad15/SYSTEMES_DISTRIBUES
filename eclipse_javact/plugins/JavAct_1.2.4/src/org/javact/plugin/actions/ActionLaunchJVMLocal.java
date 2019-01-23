package org.javact.plugin.actions;


import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.JavActPlugin;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action button "Launch JVM Local"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class ActionLaunchJVMLocal implements IWorkbenchWindowPulldownDelegate {
    //~ Constructors -----------------------------------------------------------

    /**
     * The constructor.
     */
    public ActionLaunchJVMLocal() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.ui.IActionDelegate#run
     */
    public void run(IAction action) {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                                            .getActiveWorkbenchWindow();

        Cursor cursor = new Cursor(null, SWT.CURSOR_WAIT);
        window.getShell().setCursor(cursor);

        // the SelectionChanged method from the ActionJavActGen class assure that JavActUtilities.javaProject is the selected project
        IJavaProject javaProject = JavActUtilities.javaProject;

        if (javaProject != null) {
            JavActUtilities.killLocalProjectJVMfromButton(javaProject.getProject()
                                                                     .getName());
            JavActUtilities.launchLocalJVM(javaProject.getProject());
        } else {
            JavActUtilities.warning("Launch JVM",
                "A JavAct project must be selected before launching JVM");
        }

        cursor.dispose();
        window.getShell().getShell().setCursor(null);
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

    /**
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu
     */
    public Menu getMenu(Control parent) {
        Menu m = new Menu(parent);
        ActionContributionItem aci = new ActionContributionItem(new ActionKillJVMLocal());
        aci.getAction().setText("Kill JVM on the localhost");
        aci.getAction()
           .setImageDescriptor(JavActPlugin.getImageDescriptor(
                "icons/killjvm.gif"));
        aci.fill(m, -1);

        ActionContributionItem aci2 = new ActionContributionItem(new ActionTestAllJVM());
        aci2.getAction().setText("Test the JVM of places.txt");
        aci2.getAction()
            .setImageDescriptor(JavActPlugin.getImageDescriptor(
                "icons/testjvm.gif"));
        aci2.fill(m, -2);

        return m;
    }
}
