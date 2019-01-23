package org.javact.plugin;


import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.javact.plugin.tools.JavActUtilities;
import org.osgi.framework.BundleContext;




/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class JavActPlugin extends AbstractUIPlugin {
    //~ Static fields/initializers ---------------------------------------------

    //The shared instance.
    private static JavActPlugin plugin;
    public final static String pathJAR = "lib" + File.separator + "javact.jar";
    public final static String pathPlaces = "lib" + File.separator + "places.txt";
    public final static String pathAwfullpolicy = "lib" + File.separator + "awfullPolicy";

    //~ Constructors -----------------------------------------------------------

    /**
     * The constructor.
     */
    public JavActPlugin() {
        plugin = this;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
    	// kill all the launched JVM
        JavActUtilities.killAllLocalJVM();
        
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static JavActPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("JavAct", path);
    }
}
