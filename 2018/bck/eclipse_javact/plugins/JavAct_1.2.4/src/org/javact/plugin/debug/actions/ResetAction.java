package org.javact.plugin.debug.actions;


import org.eclipse.jface.action.Action;
import org.javact.plugin.actions.ActionDebug;
import org.javact.plugin.tools.JavActUtilities;



/**
 * This class represents the action button "Reset"
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
 */
public class ResetAction extends Action {
	//~ Methods ----------------------------------------------------------------
	
	/**
	 * @see Action#run()
	 */
	public void run() {        
		if (ActionDebug.debug == null) {
			JavActUtilities.error("Debug Mode", "There is no project selected");
			
			return;
		}
		ActionDebug.debug.reset();
	}
}
