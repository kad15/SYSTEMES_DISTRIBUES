package org.javact.plugin.debug;


import java.util.HashMap;

import org.javact.plugin.debug.views.PlaceComposite;
import org.javact.plugin.debug.views.PlaceEventView;



/**
 * This class is used to manipulate a place launched in debug mode
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class PlaceDebug {
    //~ Instance fields --------------------------------------------------------

	/* The hashmap of ActorDebug : actors which are on this debug place
	 * The key is the real name of the actors
	 */
    private HashMap actors;
    // The visual place in the debug mode
    private PlaceComposite placeComposite;
    // The view which will contain the events
    private PlaceEventView placeEventView;
    // The name of the place
    private String name;
    // isChecked = true if the place is checked on the left pannel of the debug perspective
    private boolean isChecked;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PlaceDebug object.
     *
     * @param _name The place's name
     */
    public PlaceDebug(String _name) {
        name = _name;
        isChecked = false;
        actors = new HashMap();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @param actor The actor to add at the list of present actors
     */
    public void addActor(ActorDebug actor) {
        actors.put(actor.getRealName(), actor);
    }

    /**
     * Changes the value of isChecked (boolean)
     */
    public void changeIsChecked() {
        isChecked = !isChecked;
    }

    /**
     * @return The hashmap of the actors who are on this place
     */
    public HashMap getActors() {
        return actors;
    }

    /**
     * @return The event view of the place
     */
    public PlaceEventView getEventView() {
        return placeEventView;
    }

    /**
     * @return The name of the place
     */
    public String getName() {
        return name;
    }

    /**
     * @return The place's composite
     */
    public PlaceComposite getPlaceComposite() {
        return placeComposite;
    }

    /**
     * @return the value of isChecked
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * @param actor The actor to remove from the hashmap actors
     */
    public void removeActor(ActorDebug actor) {
        actors.remove(actor.getRealName());
    }

    /**
     * @param view The new view
     */
    public void setEventView(PlaceEventView view) {
        placeEventView = view;
    }

    /**
     * @param composite The new place's composite
     */
    public void setPlaceComposite(PlaceComposite composite) {
        placeComposite = composite;
    }
}
