package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.swt.widgets.TableItem;
import org.javact.plugin.debug.ActorDebug;



/**
 * This class represents the action "DeselectAll"
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class DeselectAllAction extends Action {
    //~ Instance fields --------------------------------------------------------

    private TableViewer tableViewer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DeselectAllAction object.
     *
     * @param _tableViewer the TableViewer of the selection
     */
    public DeselectAllAction(TableViewer _tableViewer) {
        tableViewer = _tableViewer;
        setText("Deselect All");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Run the DeselectAll Action
     *
     * @see Action#run()
     */
    public void run() {
        TableItem[] items = tableViewer.getTable().getItems();

        for (int i = 0; i < items.length; i++) {
            items[i].setChecked(false);
            ((ActorDebug) items[i].getData()).setIsChecked(false);
        }
    }
}
