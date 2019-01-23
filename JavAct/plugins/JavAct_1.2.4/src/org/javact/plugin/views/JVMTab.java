package org.javact.plugin.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Color;
import org.javact.plugin.tools.JVMLauncher;
import org.javact.plugin.tools.JavActUtilities;
import org.javact.plugin.tools.Place;



/**
 * This class is the JVM Tab which is used to show a JVM and its message in the
 * JVM View
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class JVMTab extends CTabItem {
    //~ Instance fields --------------------------------------------------------

    private StyledText textArea;
    private JVMLauncher jvmLauncher;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JVMTab object.
     *
     * @param parent a CTabFolder which will be the parent of the new instance (cannot be null)
     * @param style the style of control to construct
     * @param launcher the JVMLauncher linked with this JVMTab
     *
     */
    public JVMTab(CTabFolder parent, int style, JVMLauncher launcher) {
        super(parent, style);
        jvmLauncher = launcher;
        textArea = new StyledText(parent,
                SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
        setControl(textArea);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is used to append text in this JVMTab
     *
     * @param message the string to append
     * @param color the color of the string
     * @param fontStyle the font style of the string
     */
    public void appendText(String message, java.awt.Color color, int fontStyle) {
        textArea.append(message + "\n");

        StyleRange styleRange = new StyleRange();
        styleRange.start = textArea.getText().length() -
            (message.length() + 1);
        styleRange.length = message.length();
        styleRange.fontStyle = fontStyle;
        styleRange.foreground = new Color(textArea.getDisplay(),
                color.getRed(), color.getGreen(), color.getBlue());
        textArea.setStyleRange(styleRange);

        revealEndOfDocument();
    }

    /**
     * Disposes of the operating system resources associated with
     * the receiver and all its descendents.
     */
    public void dispose() {
        super.dispose();
        JavActUtilities.removeLocalJVM(jvmLauncher.getIdentifier());
    }

    /**
     * Reveals the end of the text area
     */
    private void revealEndOfDocument() {
        StyledTextContent doc = textArea.getContent();
        int docLength = doc.getCharCount();

        if (docLength > 0) {
            textArea.setCaretOffset(docLength);
            textArea.showSelection();
        }
    }

    /**
     * Returns the placename of the JVM showed in the tab
     *
     * @return the placename
     */
    public Place getPlace() {
        return jvmLauncher.getPlace();
    }
}
