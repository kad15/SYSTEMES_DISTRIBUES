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
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class MethodsToShowAction implements IWorkbenchWindowPulldownDelegate {
	//~ Constructors -----------------------------------------------------------
	
	/**
	 * Creates a new ParametersAction object.
	 */
	public MethodsToShowAction() {
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
		
		ActionContributionItem aci1 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.createLocalDebugTxt));
		aci1.getAction().setText(Debug.createLocalDebugTxt);
		
		if (d.getMethod(Debug.createLocalDebugTxt)) {
			aci1.getAction().setImageDescriptor(image);
		} else {
			aci1.getAction().setImageDescriptor(null);
		}
		
		aci1.fill(m, -1);
		
		ActionContributionItem aci2 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.createLocalFromControlerDebugTxt));
		aci2.getAction().setText(Debug.createLocalFromControlerDebugTxt);
		
		if (d.getMethod(Debug.createLocalFromControlerDebugTxt)) {
			aci2.getAction().setImageDescriptor(image);
		} else {
			aci2.getAction().setImageDescriptor(null);
		}
		
		aci2.fill(m, -2);
		
		
		ActionContributionItem aci3 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.goToTxt));
		aci3.getAction().setText(Debug.goToTxt);
		
		if (d.getMethod(Debug.goToTxt)) {
			aci3.getAction().setImageDescriptor(image);
		} else {
			aci3.getAction().setImageDescriptor(null);
		}
		
		aci3.fill(m, -3);
		
		ActionContributionItem aci4 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.sendTxt));
		aci4.getAction().setText(Debug.sendTxt);
		
		if (d.getMethod(Debug.sendTxt)) {
			aci4.getAction().setImageDescriptor(image);
		} else {
			aci4.getAction().setImageDescriptor(null);
		}
		
		aci4.fill(m, -4);
		
		ActionContributionItem aci5 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.sendWithReplyTxt));
		aci5.getAction().setText(Debug.sendWithReplyTxt);
		
		if (d.getMethod(Debug.sendWithReplyTxt)) {
			aci5.getAction().setImageDescriptor(image);
		} else {
			aci5.getAction().setImageDescriptor(null);
		}
		
		aci5.fill(m, -5);
		
		ActionContributionItem aci6 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.mailBoxTxt));
		aci6.getAction().setText(Debug.mailBoxTxt);
		
		if (d.getMethod(Debug.mailBoxTxt)) {
			aci6.getAction().setImageDescriptor(image);
		} else {
			aci6.getAction().setImageDescriptor(null);
		}
		
		aci6.fill(m, -6);
		
		ActionContributionItem aci7 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.becomeTxt));
		aci7.getAction().setText(Debug.becomeTxt);
		
		if (d.getMethod(Debug.becomeTxt)) {
			aci7.getAction().setImageDescriptor(image);
		} else {
			aci7.getAction().setImageDescriptor(null);
		}
		
		aci7.fill(m, -7);
		
		ActionContributionItem aci8 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.suicideTxt));
		aci8.getAction().setText(Debug.suicideTxt);
		
		if (d.getMethod(Debug.suicideTxt)) {
			aci8.getAction().setImageDescriptor(image);
		} else {
			aci8.getAction().setImageDescriptor(null);
		}
		
		aci8.fill(m, -8);
		
		ActionContributionItem aci9 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.createDebug1Txt));
		aci9.getAction().setText(Debug.createDebug1Txt);
		
		if (d.getMethod(Debug.createDebug1Txt)) {
			aci9.getAction().setImageDescriptor(image);
		} else {
			aci9.getAction().setImageDescriptor(null);
		}
		
		aci9.fill(m, -9);
		
		ActionContributionItem aci10 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.createDebug2Txt));
		aci10.getAction().setText(Debug.createDebug2Txt);
		
		if (d.getMethod(Debug.createDebug2Txt)) {
			aci10.getAction().setImageDescriptor(image);
		} else {
			aci10.getAction().setImageDescriptor(null);
		}
		
		aci10.fill(m, -10);
		
		ActionContributionItem aci11 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.withBoxTxt));
		aci11.getAction().setText(Debug.withBoxTxt);
		
		if (d.getMethod(Debug.withBoxTxt)) {
			aci11.getAction().setImageDescriptor(image);
		} else {
			aci11.getAction().setImageDescriptor(null);
		}
		
		aci11.fill(m, -11);
		
		ActionContributionItem aci12 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.withBecTxt));
		aci12.getAction().setText(Debug.withBecTxt);
		
		if (d.getMethod(Debug.withBecTxt)) {
			aci12.getAction().setImageDescriptor(image);
		} else {
			aci12.getAction().setImageDescriptor(null);
		}
		
		aci12.fill(m, -12);
		
		ActionContributionItem aci13 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.withCrtTxt));
		aci13.getAction().setText(Debug.withCrtTxt);
		
		if (d.getMethod(Debug.withCrtTxt)) {
			aci13.getAction().setImageDescriptor(image);
		} else {
			aci13.getAction().setImageDescriptor(null);
		}
		
		aci13.fill(m, -13);
		
		ActionContributionItem aci14 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.withMveTxt));
		aci14.getAction().setText(Debug.withMveTxt);
		
		if (d.getMethod(Debug.withMveTxt)) {
			aci14.getAction().setImageDescriptor(image);
		} else {
			aci14.getAction().setImageDescriptor(null);
		}
		
		aci14.fill(m, -14);
		
		ActionContributionItem aci15 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.withSndTxt));
		aci15.getAction().setText(Debug.withSndTxt);
		
		if (d.getMethod(Debug.withSndTxt)) {
			aci15.getAction().setImageDescriptor(image);
		} else {
			aci15.getAction().setImageDescriptor(null);
		}
		
		aci15.fill(m, -15);
		
		ActionContributionItem aci16 = new ActionContributionItem(new MethodToShowEventItem(
				Debug.withLifTxt));
		aci16.getAction().setText(Debug.withLifTxt);
		
		if (d.getMethod(Debug.withLifTxt)) {
			aci16.getAction().setImageDescriptor(image);
		} else {
			aci16.getAction().setImageDescriptor(null);
		}
		
		aci16.fill(m, -16);
		
		return m;
	}
}
