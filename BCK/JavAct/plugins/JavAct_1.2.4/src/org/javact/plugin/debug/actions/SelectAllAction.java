package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.swt.widgets.TableItem;
import org.javact.plugin.debug.ActorDebug;



/**
 * This class represents the action "SelectAll"
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class SelectAllAction extends Action {
    //~ Instance fields --------------------------------------------------------

    private TableViewer tableViewer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SelectAllAction object.
     *
     * @param _tableViewer the TableViewer of the selection
     */
    public SelectAllAction(TableViewer _tableViewer) {
        tableViewer = _tableViewer;
        setText("Select All");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see Action#run()
     */
    public void run() {
        TableItem[] items = tableViewer.getTable().getItems();

        for (int i = 0; i < items.length; i++) {
            items[i].setChecked(true);
            ((ActorDebug) items[i].getData()).setIsChecked(true);
        }
    }
}
