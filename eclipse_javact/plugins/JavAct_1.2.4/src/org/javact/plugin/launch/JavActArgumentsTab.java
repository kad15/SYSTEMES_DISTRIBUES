package org.javact.plugin.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
import org.eclipse.jdt.internal.debug.ui.actions.ControlAccessibleListener;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.debug.ui.launcher.WorkingDirectoryBlock;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Arguments tab of the LaunchConfiguration
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
  */
public class JavActArgumentsTab extends JavaArgumentsTab {
    //~ Static fields/initializers ---------------------------------------------

    protected static final String EMPTY_STRING = "";

    //~ Instance fields --------------------------------------------------------

    //	 Program arguments widgets
    protected Label fPrgmArgumentsLabel;
    protected Text fPrgmArgumentsText;

    // VM arguments widgets
    protected JavActVMArgumentsBlock fVMArgumentsBlock;

    // Working directory
    protected WorkingDirectoryBlock fWorkingDirectoryBlock;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JavActArgumentsTab object.
     */
    public JavActArgumentsTab() {
        fVMArgumentsBlock = createJavActVMArgsBlock();
        fWorkingDirectoryBlock = createWorkingDirBlock();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Creation of the Block for the Virtual Machine Arguments
     *
     * @return the block for VM arguments
     */
    protected JavActVMArgumentsBlock createJavActVMArgsBlock() {
        return new JavActVMArgumentsBlock();
    }

    /**
     * @see JavaArgumentsTab#createWorkingDirBlock()
     */
    protected WorkingDirectoryBlock createWorkingDirBlock() {
        return new WorkingDirectoryBlock();
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Font font = parent.getFont();
        Composite comp = new Composite(parent, parent.getStyle());
        GridLayout layout = new GridLayout(1, true);
        comp.setLayout(layout);
        comp.setFont(font);

        GridData gd = new GridData(GridData.FILL_BOTH);
        comp.setLayoutData(gd);
        setControl(comp);
        setHelpContextId();

        Group group = new Group(comp, SWT.NONE);
        group.setFont(font);
        layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        String controlName = (LauncherMessages.JavaArgumentsTab__Program_arguments__5); //$NON-NLS-1$
        group.setText(controlName);

        fPrgmArgumentsText = new Text(group,
                SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 40;
        gd.widthHint = 100;
        fPrgmArgumentsText.setLayoutData(gd);
        fPrgmArgumentsText.setFont(font);
        fPrgmArgumentsText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });
        ControlAccessibleListener.addListener(fPrgmArgumentsText,
            group.getText());

        String buttonLabel = LauncherMessages.JavaArgumentsTab_5; //$NON-NLS-1$
        Button pgrmArgVariableButton = createPushButton(group, buttonLabel, null);
        pgrmArgVariableButton.setLayoutData(new GridData(
                GridData.HORIZONTAL_ALIGN_END));
        pgrmArgVariableButton.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
                    dialog.open();

                    String variable = dialog.getVariableExpression();

                    if (variable != null) {
                        fPrgmArgumentsText.insert(variable);
                    }
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

        fVMArgumentsBlock.createControl(comp);

        fWorkingDirectoryBlock.createControl(comp);
    }

    /**
     * Set the help context id for this launch config tab.  Subclasses may
     * override this method.
     */
    protected void setHelpContextId() {
        PlatformUI.getWorkbench().getHelpSystem()
                  .setHelp(getControl(),
            IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMENTS_TAB);
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration config) {
        return fWorkingDirectoryBlock.isValid(config);
    }

    /**
     * Defaults are empty.
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
            (String) null);
        fVMArgumentsBlock.setDefaults(config);
        fWorkingDirectoryBlock.setDefaults(config);
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            fPrgmArgumentsText.setText(configuration.getAttribute(
                    IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "")); //$NON-NLS-1$
            fVMArgumentsBlock.initializeFrom(configuration);
            fWorkingDirectoryBlock.initializeFrom(configuration);
        } catch (CoreException e) {
            setErrorMessage(LauncherMessages.JavaArgumentsTab_Exception_occurred_reading_configuration___15 +
                e.getStatus().getMessage()); //$NON-NLS-1$
            JDIDebugUIPlugin.log(e);
        }
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
            getAttributeValueFrom(fPrgmArgumentsText));
        fVMArgumentsBlock.performApply(configuration);
        fWorkingDirectoryBlock.performApply(configuration);
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
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        return LauncherMessages.JavaArgumentsTab__Arguments_16; //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setLaunchConfigurationDialog(ILaunchConfigurationDialog)
     */
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
        fWorkingDirectoryBlock.setLaunchConfigurationDialog(dialog);
        fVMArgumentsBlock.setLaunchConfigurationDialog(dialog);
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getErrorMessage()
     */
    public String getErrorMessage() {
        String m = super.getErrorMessage();

        if (m == null) {
            return fWorkingDirectoryBlock.getErrorMessage();
        }

        return m;
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getMessage()
     */
    public String getMessage() {
        String m = super.getMessage();

        if (m == null) {
            return fWorkingDirectoryBlock.getMessage();
        }

        return m;
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
     */
    public Image getImage() {
        return JavaDebugImages.get(JavaDebugImages.IMG_VIEW_ARGUMENTS_TAB);
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#activated(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
        // do nothing when activated
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#deactivated(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
        // do nothing when deactivated
    }
}
