package org.javact.plugin.tools;


/**
 * This class is used to test if a port is free on the localhost.
 * When the JVM are killed, we create an instance of this class
 * and we wait for the end of the execution of the run() method
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class TestWellKilled extends Thread {
    //~ Instance fields --------------------------------------------------------

    private int port;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TestWellKilled object.
     *
     * @param _port the port to test
     */
    public TestWellKilled(int _port) {
        super();
        port = _port;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Launches the test
     * If the test is false after 15 seconds, we return an error
     */
    public void run() {
        boolean correct = false;
        TestPlace tp;
        int timeout = 15000; //we configure a timeout of 15 seconds
        int timeElapsed = 0;

        while (!correct && timeElapsed<timeout) {
            try {
                Thread.sleep(50);
                timeElapsed += 50;
                tp = new TestPlace(new Place("localhost", port, false));
                tp.start();
                tp.join();

                if (tp.isFree()) {
                    correct = true;
                }
            } catch (InterruptedException e) {
                JavActUtilities.error("Kill JVM","A problem occurs when testing the JVM kill on localhost:"+port,e);
                e.printStackTrace();
            }
        }
        
        if(!correct)
        	JavActUtilities.error("Kill JVM","The JVM kill on localhost:" + port + " can't be killed after 15 seconds");
        	
    }
}
