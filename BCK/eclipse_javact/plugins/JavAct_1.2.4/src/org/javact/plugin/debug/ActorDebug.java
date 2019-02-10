package org.javact.plugin.debug;

import org.javact.plugin.debug.views.ActorEventView;

/**
 * This class is used to manipulate an actor that is on a debug place
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class ActorDebug {
    //~ Instance fields --------------------------------------------------------

	// The reference of the actor
    private String realName;
    // The name that will be displayed on screen
    private String displayName;
    // isTraced = true if the actor has a view opened for him
    private boolean isTraced;
    // isChecked = true if the actor is checked on his place
    private boolean isChecked;
    // The behavior of the actor
    private String actorClass;
    // The view associated with the actor
    private ActorEventView view;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ActorDebug object.
     *
     * @param name The reference of the actor
     * @param _actorClass The behavior of the actor
     */
    public ActorDebug(String name, String _actorClass) {
        realName = name;
        displayName = name;
        isTraced = false;
        isChecked = true;
        actorClass = _actorClass;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Changes the value of isChecked (boolean)
     */
    public void changeIsChecked() {
        isChecked = !isChecked;
    }

    /**
     * @param value The new display name
     */
    public void setDisplayName(String value) {
        displayName = value;
    }

    /**
     * @param value The new value of isChecked
     */
    public void setIsChecked(boolean value) {
        isChecked = value;
    }

    /**
     * @param b The new value of isTraced
     */
    public void setIsTraced(boolean b) {
        isTraced = b;
    }

    /**
     * @return The real name (reference) of the actor
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Update the view of the actor (only the displayed name changes)
     */
    public void updateView() {
        if (view != null) {
            view.rename(displayName + " events");
        }
    }

    /**
     * Associate a view to the actor and update it
     *
     * @param _view The new view
     */
    public void setView(ActorEventView _view) {
        view = _view;
        updateView();
    }

    /**
     * @param _actorClass The new behavior of the actor
     */
    public void setActorClass(String _actorClass) {
        actorClass = _actorClass;
    }

    /**
     * @return isTraced
     */
    public boolean isTraced() {
        return isTraced;
    }

    /**
     * Compares two ActorDebug
     * 
     * @param actor An ActorDebug
     *
     * @return True if the actors have the same real name
     */
    public boolean equals(ActorDebug actor) {
        return actor.getRealName().equals(realName);
    }

    /**
     * @return isChecked
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * @return The view of the actor
     */
    public ActorEventView getEventView() {
        return view;
    }
}
