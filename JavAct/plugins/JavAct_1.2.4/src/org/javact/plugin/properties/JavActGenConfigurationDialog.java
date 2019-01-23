package org.javact.plugin.properties;


import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.javact.plugin.JavActPlugin;




/**
 * This class represents the dialog to configure the JavActgen options.
 *
 * @author Nicolas Samson
 * @version $Revision: 1.1 $
 */
public class JavActGenConfigurationDialog extends TitleAreaDialog {
    //~ Instance fields --------------------------------------------------------

    private JavActGenConfiguration genConfig;

    /**
     * the current project
     */
    private IJavaProject javaProject;

    /**
     * the button to check the -h option
     */
   // private Button hButton; -h option useless

    /**
     * the button to check the -v option
     */
    private Button vButton;

    /**
     * the button to check the -b option
     */
    private Button bButton;

    /**
     * the directory for the -b option
     */
    private Text bText;

    /**
     * the button to check the -g option
     */
    private Button gButton;

    /**
     * the directory for the -g option
     */
    private Text gText;

    /**
     * the button to check the -p option
     */
    private Button pButton;

    /**
     * the directory for the -p option
     */
    private Text pText;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JavActGenConfigurationDialog object.
     *
     * @param parentShell
     * @param _javaProject
     */
    public JavActGenConfigurationDialog(Shell parentShell,
        IJavaProject _javaProject) {
        super(parentShell);
        javaProject = _javaProject;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * this method is called when the user has pressed ok.
     * we control if option -b, -g and -p are followed by their directory
     * then we save the parameters.
     */
    protected void okPressed() {
        if ((bButton.getSelection() && bText.getText().equalsIgnoreCase("")) ||
                (gButton.getSelection() &&
                gText.getText().equalsIgnoreCase("")) ||
                (pButton.getSelection() &&
                pText.getText().equalsIgnoreCase(""))) {
            setMessage("If -b, -g or -p option is selected, you have to specify a directory",
                MessageDialog.ERROR);
        } else {
          //  genConfig.setOptionH(hButton.getSelection());
            genConfig.setOptionV(vButton.getSelection());
            genConfig.setOptionB(bButton.getSelection());
            genConfig.setOptionG(gButton.getSelection());
            genConfig.setOptionP(pButton.getSelection());
            genConfig.setPathB(bText.getText());
            genConfig.setPathG(gText.getText());
            genConfig.setPathP(pText.getText());
            genConfig.save(javaProject);
            super.okPressed();
        }
    }

    /**
     * this method defines the contents of the dialogArea.
     *
     * @param parent
     *
     * @return
     */
    protected Control createDialogArea(Composite parent) {
        try {
            getShell()
                .setImage(new Image(null,
                    JavActPlugin.getDefault()
                                .openStream(new Path("icons/javact.gif"))));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        getShell().setText("Configure JavActGen");
        setTitle("Configure the options of JavActGen");
        setMessage("Choose options for the JavAct generation");

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(6, false));


        // widgets for -v option
        vButton = new Button(composite, SWT.CHECK);

        Label vlabel1 = new Label(composite, SWT.NONE);
        vlabel1.setText("-v");

        Label vlabel2 = new Label(composite, SWT.NONE);
        vlabel2.setText("-verbose");

        Label vlabel3 = new Label(composite, SWT.NONE);
        vlabel3.setText("enable the verbose mode");
        // 2 labels too complete the ligne
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        // listeners on the labels
        vlabel1.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    vButton.setSelection(!vButton.getSelection());
                }
            });
        vlabel2.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    vButton.setSelection(!vButton.getSelection());
                }
            });
        vlabel3.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    vButton.setSelection(!vButton.getSelection());
                }
            });

        
        // widgets for -b option
        bButton = new Button(composite, SWT.CHECK);

        Label blabel1 = new Label(composite, SWT.NONE);
        blabel1.setText("-b");

        Label blabel2 = new Label(composite, SWT.NONE);
        blabel2.setText("<dir>");

        Label blabel3 = new Label(composite, SWT.NONE);
        blabel3.setText("backup directory where to store old message\n" +
            "and QuasiBehavior files");

        bText = new Text(composite, SWT.BORDER);

        GridData gridData = new GridData();
        gridData.widthHint = 200;
        bText.setLayoutData(gridData);

        final Button buttonb = new Button(composite, SWT.BORDER | SWT.PUSH);
        buttonb.setText("Browse...");

        // listener on the bButton, when it is checked the fields are enabled
        bButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    super.widgetSelected(e);
                    buttonb.setEnabled(!buttonb.getEnabled());
                    bText.setEnabled(!bText.getEnabled());
                }
            });

        // listener on the browse button
        buttonb.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleBrowseB();
                }
            });

        // listeners on the labels
        blabel1.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    bButton.setSelection(!bButton.getSelection());
                    buttonb.setEnabled(!buttonb.getEnabled());
                    bText.setEnabled(!bText.getEnabled());
                }
            });
        blabel2.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    bButton.setSelection(!bButton.getSelection());
                    buttonb.setEnabled(!buttonb.getEnabled());
                    bText.setEnabled(!bText.getEnabled());
                }
            });
        blabel3.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    bButton.setSelection(!bButton.getSelection());
                    buttonb.setEnabled(!buttonb.getEnabled());
                    bText.setEnabled(!bText.getEnabled());
                }
            });

        
        // widgets for -g option
        gButton = new Button(composite, SWT.CHECK);

        Label glabel1 = new Label(composite, SWT.NONE);
        glabel1.setText("-g");

        Label glabel2 = new Label(composite, SWT.NONE);
        glabel2.setText("<dir>");

        Label glabel3 = new Label(composite, SWT.NONE);
        glabel3.setText("sets the destination directory for\n" +
            " generated files");

        gText = new Text(composite, SWT.BORDER);
        gridData = new GridData();
        gridData.widthHint = 200;
        gText.setLayoutData(gridData);

        final Button buttong = new Button(composite, SWT.BORDER | SWT.PUSH);
        buttong.setText("Browse...");

        // listener on the gButton, when it is checked the fields are enabled
        gButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    super.widgetSelected(e);
                    buttong.setEnabled(!buttong.getEnabled());
                    gText.setEnabled(!gText.getEnabled());
                }
            });

        // listener on the browse button
        buttong.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleBrowseG();
                }
            });

        // listeners on the labels
        glabel1.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    gButton.setSelection(!gButton.getSelection());
                    buttong.setEnabled(!buttong.getEnabled());
                    gText.setEnabled(!gText.getEnabled());
                }
            });
        glabel2.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    gButton.setSelection(!gButton.getSelection());
                    buttong.setEnabled(!buttong.getEnabled());
                    gText.setEnabled(!gText.getEnabled());
                }
            });
        glabel3.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    gButton.setSelection(!gButton.getSelection());
                    buttong.setEnabled(!buttong.getEnabled());
                    gText.setEnabled(!gText.getEnabled());
                }
            });

        
        // widgets for -p option
        pButton = new Button(composite, SWT.CHECK);

        Label plabel1 = new Label(composite, SWT.NONE);
        plabel1.setText("-p");

        Label plabel2 = new Label(composite, SWT.NONE);
        plabel2.setText("<package_name>");

        Label plabel3 = new Label(composite, SWT.NONE);
        plabel3.setText("package in which generated messages\n" +
            "are to be stored");

        pText = new Text(composite, SWT.BORDER);
        gridData = new GridData();
        gridData.widthHint = 200;
        pText.setLayoutData(gridData);

        final Button buttonp = new Button(composite, SWT.BORDER | SWT.PUSH);
        buttonp.setText("Browse...");

        // listener on the pButton, when it is checked the fields are enabled
        pButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    super.widgetSelected(e);
                    buttonp.setEnabled(!buttonp.getEnabled());
                    pText.setEnabled(!pText.getEnabled());
                }
            });

        // listener on the browse button
        buttonp.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleBrowseP();
                }
            });

        // listeners on the labels
        plabel1.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    pButton.setSelection(!pButton.getSelection());
                    buttonp.setEnabled(!buttonp.getEnabled());
                    pText.setEnabled(!pText.getEnabled());
                }
            });
        plabel2.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    pButton.setSelection(!pButton.getSelection());
                    buttonp.setEnabled(!buttonp.getEnabled());
                    pText.setEnabled(!pText.getEnabled());
                }
            });
        plabel3.addMouseListener(new MouseAdapter() {
                public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                    pButton.setSelection(!pButton.getSelection());
                    buttonp.setEnabled(!buttonp.getEnabled());
                    pText.setEnabled(!pText.getEnabled());
                }
            });

        initializeButtonsAndTexts();
        bText.setEnabled(bButton.getSelection());
        buttonb.setEnabled(bButton.getSelection());
        gText.setEnabled(gButton.getSelection());
        buttong.setEnabled(gButton.getSelection());
        pText.setEnabled(pButton.getSelection());
        buttonp.setEnabled(pButton.getSelection());

        return parent;
    }

    /**
     * action performed when pButton is pushed.
     * The user choose a package.
     */
    private void handleBrowseP() {
        SelectionDialog dialog = null;

        try {
            dialog = JavaUI.createPackageDialog(getShell(), javaProject,
                    IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS);
            dialog.setMessage("Select new package container");
        } catch (JavaModelException e) {
            e.printStackTrace();
        }

        if (dialog.open() == SelectionDialog.OK) {
            Object[] result = dialog.getResult();

            if (result.length == 1) {
                pText.setText(((PackageFragment) result[0]).getElementName());
            }
        }
    }

    /**
     * action performed when bButton is pushed
     * The user choose a directory.
     */
    private void handleBrowseB() {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setMessage(LauncherMessages.WorkingDirectoryBlock_7);

        String currentWorkingDir = javaProject.getProject().getLocation()
                                              .toOSString();

        if (!currentWorkingDir.trim().equals("")) {
            File path = new File(currentWorkingDir);

            if (path.exists()) {
                dialog.setFilterPath(currentWorkingDir);
            }
        }

        String selectedDirectory = dialog.open();

        if (selectedDirectory != null) {
            bText.setText((new Path(selectedDirectory)).toString());
        }
    }

    /**
     * action performed when gButton is pushed
     * The user choose a directory.
     */
    private void handleBrowseG() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
                ResourcesPlugin.getWorkspace().getRoot(), false,
                "Select new folder container");

        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();

            if (result.length == 1) {
                gText.setText(Platform.getLocation() +
                    ((Path) result[0]).toString());
            }
        }
    }

    /**
     * initialisation of the texts and the button with the saved configuration.
     */
    private void initializeButtonsAndTexts() {
        genConfig = JavActGenConfiguration.load(javaProject);
       // hButton.setSelection(genConfig.isOptionH());
        vButton.setSelection(genConfig.isOptionV());
        bButton.setSelection(genConfig.isOptionB());
        gButton.setSelection(genConfig.isOptionG());
        pButton.setSelection(genConfig.isOptionP());
        bText.setText(genConfig.getPathB());
        gText.setText(genConfig.getPathG());
        pText.setText(genConfig.getPathP());
    }
}
