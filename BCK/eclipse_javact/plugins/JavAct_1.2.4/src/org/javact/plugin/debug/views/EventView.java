package org.javact.plugin.debug.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.part.ViewPart;


/**
 * An EventView used to show the events in debug perspective
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class EventView extends ViewPart {
    //~ Static fields/initializers ---------------------------------------------

    public final static String ID = EventView.class.getName();

    //~ Instance fields --------------------------------------------------------

    private StyledText textArea;

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        textArea = new StyledText(parent,
                SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        /*
        * Nothing to do
        */
    }

    /**
     * Renames the view
     *
     * @param s the newname
     */
    public void rename(String s) {
        setPartName(s);
    }

    /**
    * This method is used to append text in this EventView
    *
    * @param message the string to append
    * @param color the color of the string
    * @param fontStyle the font style of the string
    */
    public void appendText(String message, java.awt.Color color, int fontStyle) {
        if (!textArea.isDisposed()) {
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
     * Clears the text area
     */
    public void clear() {
        textArea.getContent().setText("");
    }
}
