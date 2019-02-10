package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.debug.ActorDebug;
import org.javact.plugin.debug.views.ActorEventView;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action "ShowActorEventConsole"
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class ShowActorEventConsoleAction extends Action {
    //~ Instance fields --------------------------------------------------------

    private TableViewer tableViewer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ShowActorEventConsoleAction object.
     *
     * @param _tableViewer the TableViewer of the selection
     */
    public ShowActorEventConsoleAction(TableViewer _tableViewer) {
        tableViewer = _tableViewer;
        setText("Show/Hide Actor Event Console");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see Action#run()
     */
    public void run() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();

        if (selection.size() == 1) {
            ActorDebug actor = (ActorDebug) selection.getFirstElement();

            try {
                if (!actor.isTraced()) {
                    actor.setIsTraced(true);

                    IWorkbenchPage page = PlatformUI.getWorkbench()
                                                    .getActiveWorkbenchWindow()
                                                    .getActivePage();
                    ActorEventView view = (ActorEventView) page.showView(ActorEventView.class.getName(),
                            actor.getRealName(), IWorkbenchPage.VIEW_VISIBLE);
                    actor.setView(view);
                    view.setActor(actor);
                } else {
                    actor.setIsTraced(false);

                    IWorkbenchPage page = PlatformUI.getWorkbench()
                                                    .getActiveWorkbenchWindow()
                                                    .getActivePage();
                    ActorEventView view = (ActorEventView) page.showView(ActorEventView.class.getName(),
                            actor.getRealName(), IWorkbenchPage.VIEW_VISIBLE);
                    page.hideView(view);
                }
            } catch (PartInitException e) {
            	JavActUtilities.error("Show Actor Event Console",
                        "Impossible to show the actor event console", e);
            }
        }
    }
}
