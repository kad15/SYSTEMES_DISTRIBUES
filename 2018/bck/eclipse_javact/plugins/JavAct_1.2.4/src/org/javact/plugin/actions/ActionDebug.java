package org.javact.plugin.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.javact.plugin.debug.Debug;
import org.javact.plugin.debug.perspective.JavActDebugPerspective;
import org.javact.plugin.tools.JavActUtilities;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * This class represents the action button "Debug"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class ActionDebug implements IWorkbenchWindowActionDelegate {
    //~ Static fields/initializers ---------------------------------------------

    public static Debug debug;

    //~ Instance fields --------------------------------------------------------

    private IWorkbenchWindow window;

    //~ Methods ----------------------------------------------------------------

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
    public void init(IWorkbenchWindow _window) {
        window = _window;
    }

    /**
     * Run the debug action
     *
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if (JavActUtilities.javaProject != null) {
            try {
                JavActUtilities.javaProjectDebug = JavActUtilities.javaProject;

                String debugFilePath = JavActUtilities.javaProjectDebug.getProject()
                                                                       .getLocation()
                                                                       .toOSString() +
                    File.separator + ".debug";

                try {
                    new FileReader(debugFilePath);
                } catch (FileNotFoundException e) {
                    JavActUtilities.error("Debug Mode",
                        "No .debug file was not found in the project.\n" +
                        "You have to run your project with debug places\n" +
                        "to generate it");

                    return;
                }

                debug = new Debug();
                debug.initDebug();
                PlatformUI.getWorkbench()
                          .showPerspective(JavActDebugPerspective.ID, window);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                          .getActivePage()
                          .closePerspective(PlatformUI.getWorkbench()
                                                      .getActiveWorkbenchWindow()
                                                      .getActivePage()
                                                      .getPerspective(), false,
                    false);
                PlatformUI.getWorkbench()
                          .showPerspective(JavActDebugPerspective.ID, window);
            } catch (WorkbenchException e) {
                JavActUtilities.error("Action Debug",
                    "Error attempting to find the debug perspective", e);
            }
        } else {
            JavActUtilities.warning("JavAct Debug",
                "A JavAct project must be selected before launching Debug");
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        JavActUtilities.javaProject = JavActUtilities.getIJavaProjectFromSelection(selection);
    }
}
