package org.javact.plugin.debug.views;


import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.debug.ActorDebug;
import org.javact.plugin.debug.PlaceDebug;
import org.javact.plugin.debug.actions.ChangeNameAction;
import org.javact.plugin.debug.actions.DeselectAllAction;
import org.javact.plugin.debug.actions.SelectAllAction;
import org.javact.plugin.debug.actions.ShowActorEventConsoleAction;
import org.javact.plugin.tools.JavActUtilities;


import java.util.HashMap;


/**
 * A composite used to show a place and its caracteristics
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class PlaceComposite extends Composite {
    //~ Static fields/initializers ---------------------------------------------

    public static int heigth;
    public static int width;

    //~ Instance fields --------------------------------------------------------

    private PlaceDebug place;
    private TableViewer tabTableViewer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PlaceComposite object.
     *
     * @param parent the parent
     * @param style the style
     * @param _place the place linked with this composite
     */
    public PlaceComposite(Composite parent, int style, PlaceDebug _place) {
        super(parent, style);
        setLayout(new GridLayout(1, true));
        place = _place;
        createControl();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the place linked with this composite
     *
     * @return the place linked with this composite
     */
    public PlaceDebug getPlaceDebug() {
        return place;
    }

    /**
     * Create the composite
     */
    private void createControl() {
        // Name + ShowButton
        Composite placeNameComposite = new Composite(this, SWT.NONE);

        placeNameComposite.setLayout(new GridLayout(2, false));

        Label placeName = new Label(placeNameComposite, SWT.NONE);
        placeName.setText(place.getName());

        Button showPlaceButton = new Button(placeNameComposite, SWT.PUSH);
        showPlaceButton.setText("Show");
        showPlaceButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    try {
                        // We split the placeName, because it contains ":" 
                        // and that throws an exception java.lang.IllegalArgumentException: Illegal secondary id (cannot be empty or contain a colon)
                        String[] splitname = place.getName().split(":");
                        String ident = "";

                        for (int i = 0; i < splitname.length; i++) {
                            ident += splitname[i];
                        }

                        IWorkbenchPage page = PlatformUI.getWorkbench()
                                                        .getActiveWorkbenchWindow()
                                                        .getActivePage();

                        PlaceEventView view = (PlaceEventView) page.showView(PlaceEventView.class.getName(),
                                ident, IWorkbenchPage.VIEW_VISIBLE);
                        place.setEventView(view);
                        view.rename(place.getName());
                    } catch (PartInitException e1) {
                        JavActUtilities.error("Show Place Event Console",
                            "Impossible to show the place event console", e1);
                    }

                    super.widgetSelected(e);
                }
            });

        // Actors List
        Group actorGroup = new Group(this, SWT.NONE);
        actorGroup.setText("Actors :");

        tabTableViewer = new TableViewer(actorGroup,
                SWT.CHECK | SWT.SINGLE | SWT.BORDER);
        tabTableViewer.setContentProvider(new ActorContentProvider());
        tabTableViewer.setLabelProvider(new ActorLabelProvider());
        tabTableViewer.setSorter(new ViewerSorter());
        tabTableViewer.setInput(place.getActors());

        Table tabTable = tabTableViewer.getTable();
        tabTable.setBounds(5, 25, 200, 140);

        // The only column of the table
        new TableColumn(tabTable, SWT.LEFT);
        tabTable.getColumn(0).setWidth(150);
        tabTable.setHeaderVisible(false);
        tabTable.setLinesVisible(false);

        // The contextMenu on the Table
        ChangeNameAction changeNameAction = new ChangeNameAction(tabTableViewer);
        final ShowActorEventConsoleAction showActorEventConsoleAction = new ShowActorEventConsoleAction(tabTableViewer);
        SelectAllAction selectAllAction = new SelectAllAction(tabTableViewer);
        DeselectAllAction deselectAllAction = new DeselectAllAction(tabTableViewer);
        MenuManager menu_manager = new MenuManager();
        tabTable.setMenu(menu_manager.createContextMenu(tabTable));
        menu_manager.add(changeNameAction);
        menu_manager.add(showActorEventConsoleAction);
        menu_manager.add(new Separator());
        menu_manager.add(selectAllAction);
        menu_manager.add(deselectAllAction);

        // The listener on the Table to listen when a element is checked
        tabTableViewer.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    showActorEventConsoleAction.run();
                }
            });

        tabTable.addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event event) {
                    if (event.detail == SWT.CHECK) {
                        ((ActorDebug) ((TableItem) event.item).getData()).changeIsChecked();
                    }
                }
            });

        // The parameters of the place
        Composite parametersComposite = new Composite(this, SWT.NONE);
        parametersComposite.setLayout(new GridLayout(2, false));

        new Label(parametersComposite, SWT.NONE).setText("Memory : ");

        Label labelMemory = new Label(parametersComposite, SWT.NONE);
        labelMemory.setText("xxx Mo");

        new Label(parametersComposite, SWT.NONE).setText("CPU : ");

        Label labelCPU = new Label(parametersComposite, SWT.NONE);
        labelCPU.setText("xxx %");

        pack();
        heigth = getSize().y;
        width = getSize().x;
    }

    /**
     * Refresh the composite
     */
    public void refresh() {
        tabTableViewer.refresh();

        /*
         * Add here CPU, RAM .....
         */
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * This class provides the content for the profil table
     */
    class ActorContentProvider implements IStructuredContentProvider {
        //~ Methods ------------------------------------------------------------

        /**
         * Returns the Profil objects
         */
        public Object[] getElements(Object inputElement) {
            return ((HashMap) inputElement).values().toArray();
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
    class ActorLabelProvider implements ITableLabelProvider {
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
        	TableItem[] tabItem = tabTableViewer.getTable().getItems();
            int pos = 0;

            for (int i = 0; i < tabItem.length; i++) {
                if (((ActorDebug) tabItem[i].getData()) == element) {
                    pos = i;
                }
            }

            if (element instanceof ActorDebug) {
                tabItem[pos].setChecked(((ActorDebug) element).isChecked());

                return ((ActorDebug) element).getDisplayName();
            } else {
                return "";
            }
        }
    }
}
