package org.javact.plugin.views;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.part.ViewPart;


/**
 * This class is the JVM View which is used to show the JVM and their message in the
 * JavAct perspective
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class JVMView extends ViewPart {
    //~ Static fields/initializers ---------------------------------------------

    public static String ID = "org.javact.plugin.views.JVMView";

    //~ Instance fields --------------------------------------------------------

    private CTabFolder tabFolder;

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the CTabFolder containing the JVMTab
     *
     * @return the CTabFolder
     */
    public CTabFolder getTabFolder() {
        return tabFolder;
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
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        String projectName = this.getViewSite().getSecondaryId();
        tabFolder = new CTabFolder(parent, 0);
        this.setPartName(projectName + " JVM");
    }
}
