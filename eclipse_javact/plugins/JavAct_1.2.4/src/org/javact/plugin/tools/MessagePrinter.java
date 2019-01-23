package org.javact.plugin.tools;

import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.swt.widgets.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


/**
 * A Thread used to print the JavActGen messages in the JavActGen console and
 * the JavActGen logs
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class MessagePrinter extends Thread {
    //~ Instance fields --------------------------------------------------------

    private BufferedReader bufferedReader;
    private boolean error;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MessagePrinter object.
     *
     * @param _bufferedReader the buffer in which read
     * @param _error if the buffer is an error buffer
     */
    public MessagePrinter(BufferedReader _bufferedReader, boolean _error) {
        bufferedReader = _bufferedReader;
        error = _error;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Reads in the buffer and write the message in the console and the logs
     */
    public void run() {
        String message;

        try {
            // get the path of the .metadata folder
            String metadataFolder = ResourcesPlugin.getWorkspace().getRoot()
                                                   .getLocation().toOSString() +
                File.separator + ".metadata" + File.separator;

            if (error) {
                // open the javactgen log file for stderr
                FileOutputStream fos = new FileOutputStream(metadataFolder +
                        "JavActgenStderr.log", true);
                OutputStreamWriter writer = new OutputStreamWriter(fos);

                while ((message = bufferedReader.readLine()) != null) {
                    writer.write(message + "\n");

                    MessageRunnable r = new MessageRunnable(message, true);
                    Display.getDefault().asyncExec(r);
                }

                writer.close();
            } else {
                // open the javactgen log file for stdout
                FileOutputStream fos = new FileOutputStream(metadataFolder +
                        "JavActgenStdout.log", true);
                OutputStreamWriter writer = new OutputStreamWriter(fos);

                while ((message = bufferedReader.readLine()) != null) {
                    writer.write(message + "\n");

                    MessageRunnable r = new MessageRunnable(message, false);
                    Display.getDefault().asyncExec(r);
                }

                writer.close();
            }
        } catch (IOException e) {
            JavActUtilities.warning("JavActgen",
                "Unable to redirect the standard outputs to a file, no log files will be created for JavActgen",
                e);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * A private class which print a message in the JavActGen console
     *
     * @author $author$
     * @version $Revision: 1.1 $
      */
    private class MessageRunnable implements Runnable {
        //~ Instance fields ----------------------------------------------------

        private String myMessage;
        private boolean isError;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MessageRunnable object.
         *
         * @param _myMessage the message to print
         * @param __isError the message is an error
         */
        public MessageRunnable(String _myMessage, boolean _isError) {
            super();
            myMessage = _myMessage;
            isError = _isError;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Prints the message
         */
        public void run() {
            if (isError) {
                ConsoleDisplayMgr.getDefault()
                                 .println(myMessage, ConsoleDisplayMgr.MSG_ERROR);
            } else {
                ConsoleDisplayMgr.getDefault()
                                 .println(myMessage,
                    ConsoleDisplayMgr.MSG_INFORMATION);
            }
        }
    }
}
