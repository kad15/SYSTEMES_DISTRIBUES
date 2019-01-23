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
import org.javact.plugin.actions.ActionDebug;
import org.javact.plugin.debug.Debug;



/**
 * This class represents the action button "Parameters"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class ParametersAction implements IWorkbenchWindowPulldownDelegate {
    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ParametersAction object.
     */
    public ParametersAction() {
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
    	/*
         * Nothing to do
         */
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
        Debug d = ActionDebug.debug;
        ImageDescriptor image = JavActPlugin.getImageDescriptor(
                "icons/debug/checked.gif");

        ActionContributionItem aci1 = new ActionContributionItem(new ParameterEventItem(
                    Debug.DATE));
        aci1.getAction().setText(Debug.PARAM[Debug.DATE]);

        if (d.getParam(Debug.DATE)) {
            aci1.getAction().setImageDescriptor(image);
        } else {
            aci1.getAction().setImageDescriptor(null);
        }

        aci1.fill(m, -1);

        ActionContributionItem aci2 = new ActionContributionItem(new ParameterEventItem(
                    Debug.MACHINE));
        aci2.getAction().setText(Debug.PARAM[Debug.MACHINE]);

        if (d.getParam(Debug.MACHINE)) {
            aci2.getAction().setImageDescriptor(image);
        } else {
            aci2.getAction().setImageDescriptor(null);
        }

        aci2.fill(m, -2);

        ActionContributionItem aci3 = new ActionContributionItem(new ParameterEventItem(
                    Debug.PORT));
        aci3.getAction().setText(Debug.PARAM[Debug.PORT]);

        if (d.getParam(Debug.PORT)) {
            aci3.getAction().setImageDescriptor(image);
        } else {
            aci3.getAction().setImageDescriptor(null);
        }

        aci3.fill(m, -3);

        ActionContributionItem aci4 = new ActionContributionItem(new ParameterEventItem(
                    Debug.BEHAVIOR));
        aci4.getAction().setText(Debug.PARAM[Debug.BEHAVIOR]);

        if (d.getParam(Debug.BEHAVIOR)) {
            aci4.getAction().setImageDescriptor(image);
        } else {
            aci4.getAction().setImageDescriptor(null);
        }

        aci4.fill(m, -4);

        ActionContributionItem aci5 = new ActionContributionItem(new ParameterEventItem(
                    Debug.ACTOR));
        aci5.getAction().setText(Debug.PARAM[Debug.ACTOR]);

        if (d.getParam(Debug.ACTOR)) {
            aci5.getAction().setImageDescriptor(image);
        } else {
            aci5.getAction().setImageDescriptor(null);
        }

        aci5.fill(m, -5);

        ActionContributionItem aci6 = new ActionContributionItem(new ParameterEventItem(
                    Debug.JVMFREEMEMORY));
        aci6.getAction().setText(Debug.PARAM[Debug.JVMFREEMEMORY]);

        if (d.getParam(Debug.JVMFREEMEMORY)) {
            aci6.getAction().setImageDescriptor(image);
        } else {
            aci6.getAction().setImageDescriptor(null);
        }

        aci6.fill(m, -6);

        ActionContributionItem aci7 = new ActionContributionItem(new ParameterEventItem(
                    Debug.CPU));
        aci7.getAction().setText(Debug.PARAM[Debug.CPU]);

        if (d.getParam(Debug.CPU)) {
            aci7.getAction().setImageDescriptor(image);
        } else {
            aci7.getAction().setImageDescriptor(null);
        }

        aci7.fill(m, -7);

        ActionContributionItem aci8 = new ActionContributionItem(new ParameterEventItem(
                    Debug.FREERAM));
        aci8.getAction().setText(Debug.PARAM[Debug.FREERAM]);

        if (d.getParam(Debug.FREERAM)) {
            aci8.getAction().setImageDescriptor(image);
        } else {
            aci8.getAction().setImageDescriptor(null);
        }

        aci8.fill(m, -8);

        ActionContributionItem aci9 = new ActionContributionItem(new ParameterEventItem(
                    Debug.METHOD));
        aci9.getAction().setText(Debug.PARAM[Debug.METHOD]);

        if (d.getParam(Debug.METHOD)) {
            aci9.getAction().setImageDescriptor(image);
        } else {
            aci9.getAction().setImageDescriptor(null);
        }

        aci9.fill(m, -9);

        ActionContributionItem aci10 = new ActionContributionItem(new ParameterEventItem(
                    Debug.ARGS));
        aci10.getAction().setText(Debug.PARAM[Debug.ARGS]);

        if (d.getParam(Debug.ARGS)) {
            aci10.getAction().setImageDescriptor(image);
        } else {
            aci10.getAction().setImageDescriptor(null);
        }

        aci10.fill(m, -10);

        return m;
    }
}
