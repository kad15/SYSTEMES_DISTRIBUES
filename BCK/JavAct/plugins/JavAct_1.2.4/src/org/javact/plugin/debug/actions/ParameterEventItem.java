package org.javact.plugin.debug.actions;

import org.eclipse.jface.action.Action;
import org.javact.plugin.actions.ActionDebug;




/**
 * This class represents the item Parameter Event
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class ParameterEventItem extends Action {
    //~ Instance fields --------------------------------------------------------

    private int nbParameter;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ParameterEventItem object.
     *
     * @param _nbParameter the parameter number for this item
     */
    public ParameterEventItem(int _nbParameter) {
        super();
        nbParameter = _nbParameter;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see Action#run()
     */
    public void run() {
        ActionDebug.debug.changeParameter(nbParameter);
    }
}
