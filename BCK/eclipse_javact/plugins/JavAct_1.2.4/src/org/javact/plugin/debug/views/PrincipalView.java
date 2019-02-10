package org.javact.plugin.debug.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.part.ViewPart;
import org.javact.plugin.actions.ActionDebug;
import org.javact.plugin.debug.PlaceDebug;


import java.util.Vector;


/**
 * The principal view used to show the PlaceComposite
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class PrincipalView extends ViewPart {
    //~ Static fields/initializers ---------------------------------------------

    public static String ID = PrincipalView.class.getName();

    //~ Instance fields --------------------------------------------------------

    private static int numColumns = 3;
    private Composite parent;
    private Shell invisibleShell;
    private ScrolledComposite sc;

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        /*
         * Nothing to do
         */
    }

    /**
     * Redraw the view
     */
    public void redraw() {
        int j = 0;
        Control[] childs = parent.getChildren();

        for (int i = 0; i < childs.length; i++) {
            PlaceComposite placecomposite = (PlaceComposite) childs[i];

            if (!placecomposite.getPlaceDebug().isChecked()) {
                placecomposite.setParent(invisibleShell);
            } else {
                j++;
            }
        }

        childs = invisibleShell.getChildren();

        for (int i = 0; i < childs.length; i++) {
            PlaceComposite placecomposite = (PlaceComposite) childs[i];

            if (placecomposite.getPlaceDebug().isChecked()) {
                placecomposite.setParent(parent);
                j++;
            }
        }

        parent.layout();

        if (j < numColumns) {
            sc.setMinSize(new Double(-5 +
                    ((j % numColumns) * (9 + PlaceComposite.width))).intValue(),
                new Double(-5 +
                    (Math.ceil(new Double(j).doubleValue() / numColumns) * (9 +
                    PlaceComposite.heigth))).intValue());
        } else {
            sc.setMinSize(-5 + (numColumns * (9 + PlaceComposite.width)),
                new Double(-5 +
                    (Math.ceil(new Double(j).doubleValue() / numColumns) * (9 +
                    PlaceComposite.heigth))).intValue());
        }
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite _parent) {
        sc = new ScrolledComposite(_parent, SWT.H_SCROLL | SWT.V_SCROLL);
        parent = new Composite(sc, SWT.NONE);
        sc.setContent(parent);

        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;

        parent.setLayout(layout);

        invisibleShell = new Shell(getSite().getShell().getDisplay(), SWT.NULL);

        update();
    }

    /**
     * Update the placeComposite when the placeDebug change
     */
    public void update() {
        Control[] cIn = invisibleShell.getChildren();
        Control[] cVi = parent.getChildren();

        for (int i = 0; i < cIn.length; i++) {
            cIn[i].dispose();
        }

        for (int i = 0; i < cVi.length; i++) {
            cVi[i].dispose();
        }

        if (ActionDebug.debug != null) {
            PlaceDebug place;
            Vector places = ActionDebug.debug.getPlaces();

            for (int i = 0; i < places.size(); i++) {
                place = (PlaceDebug) places.elementAt(i);
                place.setPlaceComposite(new PlaceComposite(invisibleShell,
                        SWT.BORDER, place));
            }
        }

        redraw();
    }
}
