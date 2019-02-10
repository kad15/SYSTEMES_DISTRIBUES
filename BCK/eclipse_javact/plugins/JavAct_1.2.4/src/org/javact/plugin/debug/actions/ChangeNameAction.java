package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.javact.plugin.debug.ActorDebug;



/**
 * This class represents the action "ChangeName"
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class ChangeNameAction extends Action {
    //~ Instance fields --------------------------------------------------------

    private TableViewer tableViewer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ChangeNameAction object.
     *
     * @param _tableViewer the TableViewer of the selection
     */
    public ChangeNameAction(TableViewer _tableViewer) {
        tableViewer = _tableViewer;
        setText("Change Actor Name");
        setToolTipText("");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     *  Run the Changename action
     *  
     *  @see Action#run()
     */
    public void run() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        ActorDebug actor = (ActorDebug) selection.getFirstElement();

        if (actor != null) {
            InputDialog dialog = new InputDialog(tableViewer.getTable()
                                                            .getShell(),
                    "Rename Actor", "New Name :", actor.getDisplayName(), null);

            if (dialog.open() == Dialog.OK) {
                actor.setDisplayName(dialog.getValue());
                actor.updateView();
            }

            tableViewer.refresh();
        }
    }
}
