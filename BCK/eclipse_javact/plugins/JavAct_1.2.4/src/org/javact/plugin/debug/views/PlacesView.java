package org.javact.plugin.debug.views;


import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.javact.plugin.actions.ActionDebug;
import org.javact.plugin.debug.PlaceDebug;
import org.javact.plugin.debug.actions.ShowPlaceEventConsoleAction;


import java.util.Vector;


/**
 * The Place View used to show the list of debug place
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class PlacesView extends ViewPart {
    //~ Static fields/initializers ---------------------------------------------

    public static String ID = PlacesView.class.getName();

    //~ Instance fields --------------------------------------------------------

    private TableViewer tableViewer;

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        // initialisation of the listViewer
        tableViewer = new TableViewer(parent, SWT.CHECK);
        tableViewer.setContentProvider(new PlaceContentProvider());
        tableViewer.setLabelProvider(new PlaceLabelProvider());
        tableViewer.setSorter(new ViewerSorter());

        //the only one column of the table
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        new TableColumn(table, SWT.LEFT);
        table.getColumn(0).setWidth(200);

        table.setHeaderVisible(false);
        table.setLinesVisible(false);

        // The contextMenu on the Table
        final ShowPlaceEventConsoleAction showPlaceEventConsoleAction = new ShowPlaceEventConsoleAction(tableViewer);
        MenuManager menu_manager = new MenuManager();
        table.setMenu(menu_manager.createContextMenu(table));
        menu_manager.add(showPlaceEventConsoleAction);

        // The listener on the Table to listen when a element is doubleclicked
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    showPlaceEventConsoleAction.run();
                }
            });

        // The listener on the Table to listen when a element is checked
        tableViewer.getTable().addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event event) {
                    if (event.detail == SWT.CHECK) {
                        ((PlaceDebug) ((TableItem) event.item).getData()).changeIsChecked();

                        IWorkbenchPage page = PlatformUI.getWorkbench()
                                                        .getActiveWorkbenchWindow()
                                                        .getActivePage();

                        PrincipalView view = (PrincipalView) page.findView(PrincipalView.ID);

                        view.redraw();
                    }
                }
            });

        if (ActionDebug.debug != null) {
            tableViewer.setInput(ActionDebug.debug.getPlaces());
        }
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
    }

    /**
     * Updates the TableViewer
     */
    public void redraw() {
        tableViewer.setInput(ActionDebug.debug.getPlaces());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Ci-dessous des classes pour le listViewer
     * class ProfilContentProvider implements IStructuredContentProvider
     * class ProfilLabelProvider implements ILabelProvider
     */

    /**
     * This class provides the content for the profil table
     */
    class PlaceContentProvider implements IStructuredContentProvider {
        //~ Methods ------------------------------------------------------------

        /**
         * Returns the Profil objects
         */
        public Object[] getElements(Object inputElement) {
            return ((Vector) inputElement).toArray();
        }

        /**
         * Disposes any created resources
         */
        public void dispose() {
        }

        /**
         * Called when the input changes
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * This class provides the labels for the profil table
     */
    class PlaceLabelProvider implements ITableLabelProvider {
        //~ Methods ------------------------------------------------------------

        /**
         * @see IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub
        }

        /**
         * @see IBaseLabelProvider#dispose()
         */
        public void dispose() {
            // TODO Auto-generated method stub
        }

        /**
         * @see IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        /**
         * @see IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void removeListener(ILabelProviderListener listener) {
        }

        /**
         * @see ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * @see ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object element, int columnIndex) {
            // We search the position of the element in the table 
            // to check the checkbutton if the element is checked
            TableItem[] tabItem = tableViewer.getTable().getItems();
            int pos = 0;

            for (int i = 0; i < tabItem.length; i++) {
                if (((PlaceDebug) tabItem[i].getData()) == element) {
                    pos = i;
                }
            }

            if (element instanceof PlaceDebug) {
                tabItem[pos].setChecked(((PlaceDebug) element).isChecked());

                return ((PlaceDebug) element).getName();
            } else {
                return "";
            }
        }
    }
}
