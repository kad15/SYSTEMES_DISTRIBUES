package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.javact.plugin.JavActPlugin;



/**
 * This class represents the action button "NumberEvent"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class NumberEventAction implements IWorkbenchWindowPulldownDelegate {
    //~ Static fields/initializers ---------------------------------------------

    public static int actionChecked = 1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NumberEventAction object.
     */
    public NumberEventAction() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
    	/*
         * Nothing to do
         */
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {
        /*
         * Nothing to do
         */
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        /*
         * Nothing to do
         */
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu(Control parent) {
        Menu m = new Menu(parent);
        ImageDescriptor image = JavActPlugin.getImageDescriptor(
                "icons/debug/select.gif");

        ActionContributionItem aci1 = new ActionContributionItem(new NumberEventItem(
                    1));
        aci1.getAction().setText("  1 Event/Step");

        if (actionChecked == 1) {
            aci1.getAction().setImageDescriptor(image);
        } else {
            aci1.getAction().setImageDescriptor(null);
        }

        aci1.getAction().setId(ActionContributionItem.class.getName() + "1");
        aci1.fill(m, -1);
        

        ActionContributionItem aci5 = new ActionContributionItem(new NumberEventItem(
                    5));
        aci5.getAction().setText("  5 Events/Step");

        if (actionChecked == 5) {
            aci5.getAction().setImageDescriptor(image);
        } else {
            aci5.getAction().setImageDescriptor(null);
        }

        aci5.getAction().setId(ActionContributionItem.class.getName() + "5");
        aci5.fill(m, -2);

        ActionContributionItem aci25 = new ActionContributionItem(new NumberEventItem(
        		25));
        aci25.getAction().setText(" 25 Events/Step");

        if (actionChecked == 25) {
            aci25.getAction().setImageDescriptor(image);
        } else {
            aci25.getAction().setImageDescriptor(null);
        }

        aci25.getAction().setId(ActionContributionItem.class.getName() +
            "25");
        aci25.fill(m, -3);
        
        ActionContributionItem aci100 = new ActionContributionItem(new NumberEventItem(
        		100));
        aci100.getAction().setText("100 Events/Step");

        if (actionChecked == 100) {
            aci100.getAction().setImageDescriptor(image);
        } else {
            aci100.getAction().setImageDescriptor(null);
        }

        aci100.getAction().setId(ActionContributionItem.class.getName() +
            "100");
        aci100.fill(m, -4);

        
        return m;
    }
}
