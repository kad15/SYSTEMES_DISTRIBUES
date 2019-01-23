package org.javact.plugin.debug.views;

import org.javact.plugin.debug.PlaceDebug;


/**
 * An PlaceEventView used to show the place's events in debug perspective
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
  */
public class PlaceEventView extends EventView {
    //~ Static fields/initializers ---------------------------------------------

    public final static String ID = PlaceEventView.class.getName();

    //~ Instance fields --------------------------------------------------------

    private PlaceDebug place;

    //~ Methods ----------------------------------------------------------------

    /**
     * Sets the place linked to the view
     *
     * @param _place a place
     */
    public void setPlace(PlaceDebug _place) {
        place = _place;
    }

    /**
     * Gets the place linked to the view
     *
     * @return the place linked to the view
     */
    public PlaceDebug getPlace() {
        return place;
    }
}
