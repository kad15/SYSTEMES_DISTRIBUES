package org.javact.plugin.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jdt.internal.debug.ui.actions.ControlAccessibleListener;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.javact.plugin.tools.JavActUtilities;





/**
 * Editor for VM arguments of a Java launch configuration.
 *
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class JavActVMArgumentsBlock extends JavaLaunchTab {
    //~ Instance fields --------------------------------------------------------

    // VM arguments widgets
    protected Text fVMArgumentsText;
    private Button fPgrmArgVariableButton;
    private Button localButton;
    private Button rmiButton;
    private Button testButton;

    // Useful variables to remember the user's choices
    private IProject project = null;
    private boolean rmi = false;
    private boolean test = true;

    //~ Methods ----------------------------------------------------------------

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Font font = parent.getFont();

        Group group = new Group(parent, SWT.NONE);
        setControl(group);

        GridLayout topLayout = new GridLayout();
        group.setLayout(topLayout);

        GridData gd = new GridData(GridData.FILL_BOTH);
        group.setLayoutData(gd);
        group.setFont(font);
        group.setText(LauncherMessages.JavaArgumentsTab_VM_ar_guments__6); //$NON-NLS-1$

        fVMArgumentsText = new Text(group,
                SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 40;
        gd.widthHint = 100;
        fVMArgumentsText.setLayoutData(gd);
        fVMArgumentsText.setFont(font);
        fVMArgumentsText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });
        ControlAccessibleListener.addListener(fVMArgumentsText, group.getText());

        fPgrmArgVariableButton = createPushButton(group,
                LauncherMessages.VMArgumentsBlock_4, null);
        fPgrmArgVariableButton.setFont(font);
        fPgrmArgVariableButton.setLayoutData(new GridData(
                GridData.HORIZONTAL_ALIGN_END));
        fPgrmArgVariableButton.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
                    dialog.open();

                    String variable = dialog.getVariableExpression();

                    if (variable != null) {
                        fVMArgumentsText.insert(variable);
                    }
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

        // Our Composite for the JavAct plugin
        Composite javActComp = new Composite(group, SWT.NONE);
        javActComp.setLayout(new GridLayout(4, false));
        //javActComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Button for local launch
        localButton = new Button(javActComp, SWT.RADIO);
        localButton.setToolTipText("Used only to simulate a distributed environment,\n" +
				"all your agents will run into the same JVM");

        Label localLabel = new Label(javActComp, SWT.NONE);
        localLabel.setText("Run a local application");
        localLabel.setToolTipText("Used only to simulate a distributed environment,\n" +
        						"all your agents will run into the same JVM");

        // Button for rmi launch
        rmiButton = new Button(javActComp, SWT.RADIO);
        rmiButton.setToolTipText("Default parameters suggest that either you use only one computer or you\n" +
				"have a shared filesystem (like NFS...) between your computers. For a real\n" + 
				"distributed environment you have to modify them to load classes from an\n" + 
				"HTTP server for example, see manual.");

        Label rmiLabel = new Label(javActComp, SWT.NONE);
        rmiLabel.setText("Run a local network application");
        rmiLabel.setToolTipText("Default parameters suggest that either you use only one computer or you\n" +
        						"have a shared filesystem (like NFS...) between your computers. For a real\n" + 
        						"distributed environment you have to modify them to load classes from an\n" + 
        						"HTTP server for example, see manual.");

        // Listeners on the labels and the buttons
        localLabel.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    localButton.setSelection(true);
                    rmiButton.setSelection(false);
                    handleButtonLocal();
                }
            });
        rmiLabel.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    rmiButton.setSelection(true);
                    localButton.setSelection(false);
                    handleButtonRmi();
                }
            });

        localButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleButtonLocal();
                    super.widgetSelected(e);
                }
            });

        rmiButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleButtonRmi();
                    super.widgetSelected(e);
                }
            });

        // Button for the test before the run
        testButton = new Button(javActComp, SWT.CHECK);

        Label testLabel = new Label(javActComp, SWT.NONE);
        testLabel.setText("Test the JVM of places.txt");
        testLabel.setToolTipText("Test the JVM of places.txt before running the\n" +
        						 "application itself. If an error occured, the\n" + 
        						 "execution is aborted.");

        // Listeners on the labels and the buttons
        testLabel.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    testButton.setSelection(!testButton.getSelection());
                    handleTestButton();
                }
            });

        testButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleTestButton();
                    super.widgetSelected(e);
                }
            });
        testButton.setToolTipText("Test the JVM of places.txt before running the\n" +
        						 "application itself. If an error occured, the\n" + 
        						 "execution is aborted.");
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        return LauncherMessages.VMArgumentsBlock_VM_Arguments; //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            // Trying to know which project is concerned by this run configuration
            String projectName = configuration.getWorkingCopy()
                                              .getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
                    "");
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
                                                          .getRoot();

            try {
                project = workspaceRoot.getProject(projectName);
            } catch (Exception e) {
                // No project is selected : puting default value
                project = null;
                fVMArgumentsText.setText("");
                rmiButton.setSelection(false);
                localButton.setSelection(true);
                testButton.setSelection(true);
                rmi = false;
                test = true;

                return;
            }
        } catch (CoreException e1) {
            e1.printStackTrace();
        }

        try {
            // Setting the configuration according to the workingcopy's information
            boolean testSelection = configuration.getWorkingCopy()
                                                 .getAttribute("test", true);
            test = testSelection;
            testButton.setSelection(testSelection);

            if (configuration.getWorkingCopy().getAttribute("rmi", false)) {
                rmi = true;
                rmiButton.setSelection(true);
                localButton.setSelection(false);
                fVMArgumentsText.setText(configuration.getWorkingCopy()
                                                      .getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                        ""));
            } else {
                rmi = false;
                rmiButton.setSelection(false);
                localButton.setSelection(true);
                fVMArgumentsText.setText(configuration.getWorkingCopy()
                                                      .getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                        ""));
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        /* Trying to know what is the project for the configuration
         * The user may have changed it or not, but we have to take care whether
         * it exists or not
         */
        String projectName = "";

        try {
            projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
                    "");
        } catch (CoreException e) {
            e.printStackTrace();
        }

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        try {
            project = workspaceRoot.getProject(projectName);
        } catch (Exception e) {
            // No project is selected : puting default value
            project = null;
            rmiButton.setSelection(false);
            localButton.setSelection(true);
            rmi = false;
            testButton.setSelection(true);
            test = true;

            return;
        }

        // The project exists : saving the chosen values
        configuration.setAttribute("test", test);
        configuration.setAttribute("rmi", rmi);
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
            getAttributeValueFrom(fVMArgumentsText));
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // setDefaults is only called when the configuration file is created
        // These are the values that will be created with the configuration file
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
            (String) null);
        configuration.setAttribute("rmi", false);
        configuration.setAttribute("test", true);
    }

    /**
     * Retuns the string in the text widget, or <code>null</code> if empty.
     *
     * @return text or <code>null</code>
     */
    protected String getAttributeValueFrom(Text text) {
        String content = text.getText().trim();

        if (content.length() > 0) {
            return content;
        }

        return null;
    }

    /**
     * action performed when localButton is selected
     */
    private void handleButtonLocal() {
        // local means no VM arguments
        fVMArgumentsText.setText("");

        localButton.setSelection(true);
        rmiButton.setSelection(false);
        rmi = false;
    }

    /**
     * action performed when rmiButton is selected
     */
    private void handleButtonRmi() {
        /* Setting the configuration in rmi force us to show the JVM arguments
         * These can't be generated if there is no project selected
         */
        if (project != null) {
            String location = "";

            try {
                location = project.getLocation().toString();
            } catch (NullPointerException e) {
                JavActUtilities.error("Configuration",
                    "Selected project not found", e);
                rmiButton.setSelection(false);
                localButton.setSelection(true);
                rmi = false;

                return;
            }

            rmi = true;
            localButton.setSelection(false);
            rmiButton.setSelection(true);

            // generating the default codebase
            String arg1 = "-Djava.rmi.server.codebase=file:" + location + "/";

            // generating the default execution domain
            String arg2 = " -DEXECUTION_DOMAIN=" + "\"" + location + "/" +
                "places.txt" + "\"";

            // %20 replace " " in the codebase because it is a URL
            fVMArgumentsText.setText(arg1.replaceAll(" ", "%20") + arg2);
        } else {
            fVMArgumentsText.setText("");
            JavActUtilities.error("Launch the application in rmi",
                "A JavAct project must be selected before launching the application in rmi");
            rmiButton.setSelection(false);
            localButton.setSelection(true);
            rmi = false;
        }
    }

    /**
     * action performed when testButton is selected
     */
    private void handleTestButton() {
        test = testButton.getSelection();

        if ((rmi == false) && (test == true)) {
            JavActUtilities.warning("Launch the application in local",
                "There is no need to test the JVM of places.txt in local mode");
        }

        // This is just to enable the "Apply" button when the test button is pushed
        fVMArgumentsText.setText(fVMArgumentsText.getText());
    }
}
