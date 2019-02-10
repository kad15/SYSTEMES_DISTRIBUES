package org.javact.plugin.debug.views;

import org.javact.plugin.debug.ActorDebug;

/**
 * An ActorEventView used to show the actor's events in debug perspective
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
  */
public class ActorEventView extends EventView {
    //~ Static fields/initializers ---------------------------------------------

    public final static String ID = ActorEventView.class.getName();

    //~ Instance fields --------------------------------------------------------

    private ActorDebug actor;

    //~ Methods ----------------------------------------------------------------

    /**
     * Sets the actor linked to the view
     *
     * @param _actor an actor
     */
    public void setActor(ActorDebug _actor) {
        actor = _actor;
    }

    /**
     * Gets the actor linked to the view
     *
     * @return the actor linked to the view
     */
    public ActorDebug getActor() {
        return actor;
    }

    /**
     * When the view is disposed, if it's linked
     * to an actor, we set the boolean isTraced of 
     * the actor at false
     */
    public void dispose() {
        if (actor != null) {
            actor.setIsTraced(false);
        }
        super.dispose();
    }
}
