package org.javact.plugin.tools;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.javact.plugin.views.JVMTab;


import java.awt.Color;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * This class is used to print JVM messages in its tabItem
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class JVMMessagePrinter extends Thread {
    //~ Instance fields --------------------------------------------------------

    private JVMTab tabItem;
    private BufferedReader bufferedReader;
    private boolean error;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JVMMessagePrinter object.
     *
     * @param _tabItem tabItem on which the messages must be printed
     * @param _bufferedReader BufferedReader in which read the messages
     * @param _error true if the messages are errors
     */
    JVMMessagePrinter(JVMTab _tabItem, BufferedReader _bufferedReader,
        boolean _error) {
        tabItem = _tabItem;
        bufferedReader = _bufferedReader;
        error = _error;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Prints the messages in the tabItem
     */
    public void run() {
        String message;

        try {
            if (error) {
                while ((message = bufferedReader.readLine()) != null) {
                    // write the error in the tabItem
                    TextRunnable r = new TextRunnable(message, true);

                    Display.getDefault().asyncExec(r);
                }
            } else {
                while ((message = bufferedReader.readLine()) != null) {
                    // write the message in the tabItem
                    TextRunnable r = new TextRunnable(message, false);

                    Display.getDefault().asyncExec(r);
                }
            }
        } catch (IOException e) {
            JavActUtilities.error("JVMMessagePrinter",
                "An error occured when reading the messages for " +
                tabItem.getPlace().getName(), e);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Runnable used to print a text
     *
     * @author $author$
     * @version $Revision: 1.1 $
      */
    private class TextRunnable implements Runnable {
        //~ Instance fields ----------------------------------------------------

        private String myMessage;
        private boolean isError;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TextRunnable object.
         *
         * @param _myMessage the message to print
         * @param __isError true if the message is an error
         */
        public TextRunnable(String _myMessage, boolean _isError) {
            super();
            myMessage = _myMessage;
            isError = _isError;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Print the message
         */
        public void run() {
            if (isError) {
                tabItem.appendText(myMessage, Color.RED, SWT.BOLD);
            } else {
                tabItem.appendText(myMessage, Color.BLUE, SWT.NORMAL);
            }
        }
    }
}
