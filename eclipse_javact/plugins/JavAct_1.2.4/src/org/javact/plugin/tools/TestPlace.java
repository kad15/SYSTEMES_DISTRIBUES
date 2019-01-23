package org.javact.plugin.tools;

import java.io.IOException;

import java.net.Socket;


/**
 * This class is used to test if a place is free or not
 *
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class TestPlace extends Thread {
    //~ Instance fields --------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private Place place;

    /**
     * DOCUMENT ME!
     */
    private boolean isFree;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TestPlace object.
     *
     * @param _place the hostname of the place to test
     */
    public TestPlace(Place _place) {
        super();
        place = _place;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void run() {
        try {
            Socket socket = new Socket(place.getHost(), place.getPort());
            socket.close();
            isFree = false;
        } catch (IOException e) {
            // There is nothing on this port
            isFree = true;
        }
    }

    /**
     * Return true if the place is free after the test Have no sens if the test
     * (run) isn't called before
     *
     * @return true if the place is free else false
     */
    public boolean isFree() {
        return isFree;
    }

	/**
	 * Gets the place where the JVM is launched
	 *
	 * @return the place
	 */
    public Place getPlace() {
        return place;
    }
}
