package org.javact.plugin.properties;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IJavaProject;
import org.javact.plugin.tools.JavActUtilities;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * This class represents the configuration of the options chosen for the JavAct generation
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class JavActGenConfiguration implements Serializable {
    //~ Static fields/initializers ---------------------------------------------

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //~ Instance fields --------------------------------------------------------

    /**
     * the diretive for the -b option
     */
    private String pathB = "";

    /**
     * the diretive for the -g option
     */
    private String pathG = "";

    /**
     * the diretive for the -p option
     */
    private String pathP = "";

    /**
     * true if option -b is checked
     */
    private boolean optionB = false;

    /**
     * true if option -g is checked
     */
    private boolean optionG = false;

    /**
     * true if option -h is checked
     */
    private boolean optionH = false;

    /**
     * true if option -p is checked
     */
    private boolean optionP = false;

    /**
     * true if option -v is checked
     */
    private boolean optionV = false;
    private String projectPath;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JavActGenConfiguration object with default options.
     *
     * @param _projectPath the project path
     */
    public JavActGenConfiguration(String _projectPath) {
        optionH = false;
        optionV = true;
        optionB = false;

        optionG = false;
        optionP = false;

        projectPath = _projectPath;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPathB() {
        return pathB;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPathG() {
        return pathG;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPathP() {
        return pathP;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOptionB() {
        return optionB;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOptionG() {
        return optionG;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOptionH() {
        return optionH;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOptionP() {
        return optionP;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOptionV() {
        return optionV;
    }

    /**
     * Load the file .genConfig from the workspace of the project
     *
     * @param aProject the project from which the .genConfig is launched
     *
     * @return the saved configuration of the JavAct generation
     */
    public static JavActGenConfiguration load(IJavaProject aProject) {
        try {
            // open the .genConfig file
            FileInputStream entree = new FileInputStream(aProject.getProject()
                                                                 .getLocation()
                                                                 .toOSString() +
                    File.separator + ".genConfig");
            ObjectInputStream s = new ObjectInputStream(entree);

            // .genConfig is a serialization of JavActGenConfiguration
            return (JavActGenConfiguration) s.readObject();
        } catch (FileNotFoundException e) {
            JavActUtilities.warning("JavActgen load configuration",
                "Can't open the file .genConfig the default configuration will be used instead",
                e);

            // create a new default configuration
            JavActGenConfiguration defaultConfig = new JavActGenConfiguration(aProject.getProject()
                                                                                      .getLocation()
                                                                                      .toOSString());
            // save this configuration
            defaultConfig.save(aProject);

            return defaultConfig;
        } catch (IOException e) {
            JavActUtilities.warning("JavActgen load configuration",
                "Error while openning .genConfig the default configuration will be used",
                e);

            // create a new default configuration
            JavActGenConfiguration defaultConfig = new JavActGenConfiguration(aProject.getProject()
                                                                                      .getLocation()
                                                                                      .toOSString());
            // save this configuration
            defaultConfig.save(aProject);

            return defaultConfig;
        } catch (ClassNotFoundException e) {
            JavActUtilities.warning("JavActgen load configuration",
                "Can't load .genConfig the default configuration will be used",
                e);

            // create a new default configuration
            JavActGenConfiguration defaultConfig = new JavActGenConfiguration(aProject.getProject()
                                                                                      .getLocation()
                                                                                      .toOSString());
            // save this configuration
            defaultConfig.save(aProject);

            return defaultConfig;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return The options in a string[]
     */
    public String[] getOptions() {
        String[] options = new String[9];
        int indice = 0;

        if (isOptionV()) {
            options[indice++] = "-v";
        }

        if (isOptionB()) {
            options[indice++] = "-b";
            options[indice++] = getPathB();
        }

        if (isOptionG()) {
            options[indice++] = "-g";
            options[indice++] = getPathG();
        } else {
            options[indice++] = "-g";
            options[indice++] = projectPath;
        }

        if (isOptionP()) {
            options[indice++] = "-p";
            options[indice++] = getPathP();
        }

        /* "options" being able to contain 9 string, indice here is lesser or equal than 8
        the null value will be useful to now where the options end in "options" */
        for (int i = indice; i < 9; i++) {
            options[i] = null;
        }

        return options;
    }

    /**
     * Save in .genConfig the current instace of JavActGenConfig in the project workspace
     *
     * @param aProject the project in which the file .genConfig will be created
     */
    public void save(IJavaProject aProject) {
        try {
            // create the file (it override an old file if existing)
            FileOutputStream sortie = new FileOutputStream(aProject.getProject()
                                                                   .getLocation()
                                                                   .toOSString() +
                     File.separator + ".genConfig");
            ObjectOutputStream s = new ObjectOutputStream(sortie);

            // serialize the current instance of JavActGenConfig
            s.writeObject(this);

            s.flush();

            // refresh the workspace in order to take into account the changes of .genConfig
            aProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (FileNotFoundException e) {
            JavActUtilities.warning("JavActgen save configuration",
                "Can't create the file .genConfig, the current configuration will not be saved",
                e);
        } catch (IOException e) {
            JavActUtilities.warning("JavActgen save configuration",
                "Error while creating .genConfig, the current configuration will not be saved",
                e);
        } catch (CoreException e) {
            JavActUtilities.warning("JavActgen save configuration",
                "Error while refreshing project, press F5 to be sure to keep our configuration",
                e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param optionB DOCUMENT ME!
     */
    public void setOptionB(boolean optionB) {
        this.optionB = optionB;
    }

    /**
     * DOCUMENT ME!
     *
     * @param optionG DOCUMENT ME!
     */
    public void setOptionG(boolean optionG) {
        this.optionG = optionG;
    }

    /**
     * DOCUMENT ME!
     *
     * @param optionH DOCUMENT ME!
     */
    public void setOptionH(boolean optionH) {
        this.optionH = optionH;
    }

    /**
     * DOCUMENT ME!
     *
     * @param optionP DOCUMENT ME!
     */
    public void setOptionP(boolean optionP) {
        this.optionP = optionP;
    }

    /**
     * DOCUMENT ME!
     *
     * @param optionV DOCUMENT ME!
     */
    public void setOptionV(boolean optionV) {
        this.optionV = optionV;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pathB DOCUMENT ME!
     */
    public void setPathB(String pathB) {
        this.pathB = pathB;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pathG DOCUMENT ME!
     */
    public void setPathG(String pathG) {
        this.pathG = pathG;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pathP DOCUMENT ME!
     */
    public void setPathP(String pathP) {
        this.pathP = pathP;
    }
}
