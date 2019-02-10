package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.debug.PlaceDebug;
import org.javact.plugin.debug.views.PlaceEventView;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action "ShowActorEventConsole"
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class ShowPlaceEventConsoleAction extends Action {
    //~ Instance fields --------------------------------------------------------

    private TableViewer tableViewer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ShowPlaceEventConsoleAction object.
     *
     * @param _tableViewer the TableViewer of the selection
     */
    public ShowPlaceEventConsoleAction(TableViewer _tableViewer) {
        tableViewer = _tableViewer;
        setText("Show Place Event Console");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see Action#run()
     */
    public void run() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();

        if (selection.size() == 1) {
            PlaceDebug place = (PlaceDebug) selection.getFirstElement();

            try {
            	// We split the placeName, because it contains ":" 
                // and that throws an exception java.lang.IllegalArgumentException: Illegal secondary id (cannot be empty or contain a colon)
                String[] splitname = place.getName().split(":");
                String ident = "";

                for (int i = 0; i < splitname.length; i++) {
                    ident += splitname[i];
                }

                IWorkbenchPage page = PlatformUI.getWorkbench()
                                                .getActiveWorkbenchWindow()
                                                .getActivePage();
                PlaceEventView view = (PlaceEventView) page.showView(PlaceEventView.class.getName(),
                        ident, IWorkbenchPage.VIEW_VISIBLE);
                view.rename(place.getName());
            } catch (PartInitException e) {
                JavActUtilities.error("Show Place Event Console",
                    "Impossible to show the place event console", e);
            }
        }
    }
}
