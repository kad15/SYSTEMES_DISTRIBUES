package org.javact.plugin.debug.actions;

import org.eclipse.jface.action.Action;


/**
 * This class represents the item Number Events
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class NumberEventItem extends Action {
    //~ Instance fields --------------------------------------------------------

    private int nbEvent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NumberEventItem object.
     *
     * @param _nbEvent the number of events / step for this item
     */
    public NumberEventItem(int _nbEvent) {
        super();
        nbEvent = _nbEvent;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see Action#run()
     */
    public void run() {
        NumberEventAction.actionChecked = nbEvent;
    }
}
