package org.javact.plugin.debug.actions;

import org.eclipse.jface.action.Action;
import org.javact.plugin.actions.ActionDebug;




/**
 * This class represents the item Parameter Event
 *
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class MethodToShowEventItem extends Action {
    //~ Instance fields --------------------------------------------------------

    private String methodName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ParameterEventItem object.
     *
     * @param _nbParameter the parameter number for this item
     */
    public MethodToShowEventItem(String _methodName) {
        super();
        methodName = _methodName;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see Action#run()
     */
    public void run() {
        ActionDebug.debug.changeMethod(methodName);
    }
}
