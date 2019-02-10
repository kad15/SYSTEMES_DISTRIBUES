package org.javact.plugin.debug.perspective;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.javact.plugin.debug.views.ActorEventView;
import org.javact.plugin.debug.views.EventView;
import org.javact.plugin.debug.views.PlaceEventView;
import org.javact.plugin.debug.views.PlacesView;
import org.javact.plugin.debug.views.PrincipalView;



/**
 * The JavAct Debug Perspective
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class JavActDebugPerspective implements IPerspectiveFactory {
    //~ Static fields/initializers ---------------------------------------------

    public static String ID = JavActDebugPerspective.class.getName();

    //~ Methods ----------------------------------------------------------------

    /**
     * @see IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);

        // Create the folder for the PlacesView
        IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT,
                (float) 0.25, editorArea);
        folder.addView(PlacesView.ID);
        layout.getViewLayout(PlacesView.ID).setCloseable(false);

        // Create the folder for the EventView
        IFolderLayout outputfolder = layout.createFolder("bottom",
                IPageLayout.BOTTOM, (float) 0.75, editorArea);
        outputfolder.addView(EventView.ID);
        layout.getViewLayout(EventView.ID).setCloseable(false);
        outputfolder.addPlaceholder(ActorEventView.ID);
        layout.getViewLayout(ActorEventView.ID).setCloseable(true);
        outputfolder.addPlaceholder(PlaceEventView.ID);
        layout.getViewLayout(PlaceEventView.ID).setCloseable(true);

        // Create the folder for the PrincipalView
        layout.addView(PrincipalView.ID, IPageLayout.TOP, (float) 1, editorArea);
        layout.getViewLayout(PrincipalView.ID).setCloseable(false);

        layout.addActionSet("org.javact.plugin.DebugActionSet");
    }
}
