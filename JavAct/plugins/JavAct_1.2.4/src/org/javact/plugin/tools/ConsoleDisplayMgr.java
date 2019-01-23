package org.javact.plugin.tools;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
 * Create an instance of this class in any of your plugin classes.
 *
 * Use it as follows ...<br>
 * ConsoleDisplayMgr.getDefault().println("Some error msg", ConsoleDisplayMgr.MSG_ERROR);<br>
 * ConsoleDisplayMgr.getDefault().clear();
 * 
 * @author Web sources, Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class ConsoleDisplayMgr {
    //~ Static fields/initializers ---------------------------------------------

    private static ConsoleDisplayMgr fDefault = null;
    public static final int MSG_INFORMATION = 1;
    public static final int MSG_ERROR = 2;
    public static final int MSG_WARNING = 3;

    //~ Instance fields --------------------------------------------------------

    private String fTitle = null;
    private MessageConsole fMessageConsole = null;
    private IConsoleView fIConsoleView = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ConsoleDisplayMgr object.
     *
     * @param messageTitle console title
     */
    public ConsoleDisplayMgr(String messageTitle) {
        fDefault = this;
        fTitle = messageTitle;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the static console
     *
     * @return the static console
     */
    public static ConsoleDisplayMgr getDefault() {
        return fDefault;
    }

    /**
     * Prints a message in the console
     *
     * @param msg message
     * @param msgKind kind of the message
     */
    public void println(String msg, int msgKind) {
        if (msg == null) {
            return;
        }

        /* if console-view in Java-perspective is not active, then show it and
         * then display the message in the console attached to it */
        if (!displayConsoleView()) {
            /*If an exception occurs while displaying in the console, then just diplay atleast the same in a message-box */
            MessageDialog.openError(PlatformUI.getWorkbench()
                                              .getActiveWorkbenchWindow()
                                              .getShell(), "Error", msg);

            return;
        }

        /* display message on console */
        getNewMessageConsoleStream(msgKind).println(msg);
        fIConsoleView.display(fMessageConsole);
    }

    /**
     * Clear the console
     */
    public void clear() {
        IDocument document = getMessageConsole().getDocument();

        if (document != null) {
            document.set("");
        }
    }

    /**
     * Display the console view if it's not visible
     *
     * @return true if there is no PartInitException
     */
    private boolean displayConsoleView() {
        try {
            IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                                                               .getActiveWorkbenchWindow();

            if (activeWorkbenchWindow != null) {
                IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

                if (activePage != null) {
                    fIConsoleView = (IConsoleView) activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW,
                            null, IWorkbenchPage.VIEW_VISIBLE);
                }
            }
        } catch (PartInitException partEx) {
            return false;
        }

        return true;
    }

    /**
     * Gets a new message console stream
     *
     * @param msgKind kind of the message
     *
     * @return the message console stream
     */
    private MessageConsoleStream getNewMessageConsoleStream(int msgKind) {
        int swtColorId = SWT.COLOR_DARK_GREEN;

        switch (msgKind) {
        case MSG_INFORMATION:
            swtColorId = SWT.COLOR_BLACK;

            break;

        case MSG_ERROR:
            swtColorId = SWT.COLOR_RED;

            break;

        case MSG_WARNING:
            swtColorId = SWT.COLOR_BLUE;

            break;

        default:
        }

        MessageConsoleStream msgConsoleStream = getMessageConsole()
                                                    .newMessageStream();
        msgConsoleStream.setColor(Display.getCurrent().getSystemColor(swtColorId));

        return msgConsoleStream;
    }

    /**
     * Gets the message console
     *
     * @return the message console
     */
    private MessageConsole getMessageConsole() {
        if (fMessageConsole == null) {
            createMessageConsoleStream(fTitle);
        }

        return fMessageConsole;
    }

    /**
     * Creates a message console stream
     *
     * @param title of the message console
     */
    private void createMessageConsoleStream(String title) {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();

        for (int i = 0; i < existing.length; i++) {
            if (title.equals(existing[i].getName())) {
                fMessageConsole = (MessageConsole) existing[i];

                return;
            }
        }
        
        //no console found, so create a new one
        fMessageConsole = new MessageConsole(title, null);
        conMan.addConsoles(new IConsole[] { fMessageConsole });
    }
}
