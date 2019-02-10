package org.javact.plugin.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;


/**
 * This class implements the container of tabs for the JavAct Launch Configuration
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class JavActLaunchConfigurationTabGroup
    extends AbstractLaunchConfigurationTabGroup {
    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
     *      java.lang.String)
     */
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
    	// The tab for SourceLookup has been removed
    	// other classes have to be implemented to insert it.
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new JavaMainTab(), new JavActArgumentsTab(), new JavaJRETab(),
                new JavaClasspathTab(), 
                /*new SourceLookupTab(),*/ new EnvironmentTab(), new CommonTab()
            };

        this.setTabs(tabs);
    }
}
