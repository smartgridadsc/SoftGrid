package it.adsc.smartpower.substatin.concenter.ui;

/**
 * Created by prageethmahendra on 12/9/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;
import it.adsc.smartpower.substatin.concenter.MainPanel;
import it.adsc.smartpower.substatin.concenter.ToolBar;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.StringUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

/**
 * This is the main class of the CyberSage which integrate the MainFrame, MainPanel and the MyDoggy Tools and UI
 * Panels. All the System wide configuration constant parameters should be defined here.
 * Created by prageeth.g on 21/5/2015.
 */
public class CCApplication {

    public static final String DOCK_PANEL_EXP_TREE = " Experiments ";
//    public static final String DOCK_PANEL_PROPERTY = " Properties ";
//    public static final String DOCK_PANEL_PALETTE = " Palette ";
//    public static final String DOCK_PANEL_ZOOM = " Zoom ";

    public static final String VERSION = "1.0.1";

    public static boolean DISABLE_LOCKED_PANEL_MODE = false;
    public static boolean ENABLE_AUTO_HIDE = false;
    public static boolean ENABLE_DOCK_BUTTON = false;
    public static boolean ENABLE_FLOAT_BUTTON = false;
    public static boolean ENABLE_PIN_BUTTON = false;
    public static boolean ENABLE_MAXIMIZE_BUTTON = false;
    public static boolean GSA_GENERATOR_VERSION_1 = false;

    private static CCApplication instance;

    private ControlCenterWindow frame;
    private ToolWindowManager toolWindowManager;
    protected DockableDescriptor memoryMonitorDescriptor;
    private boolean tree;


    private CCApplication() {
//        try {
//            mxResources.add("resources/editor");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    static {
//        try {
//            mxResources.add("resources/editor");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static CCApplication getInstance() {
        if (instance == null) {
            instance = new CCApplication();
        }
        return instance;
    }

    protected void run(final Runnable runnable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start(runnable);
            }
        });
    }

    protected void setUp() {
        initComponents();
        // initSplash();
        initToolWindowManager();
    }

    private void initSplash() {
        final java.awt.SplashScreen splash = java.awt.SplashScreen
                .getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("Graphics2D is null");
            return;
        }
        for (int i = 0; i < 100; i++) {
            renderSplashFrame(g, i);
            splash.update();
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
            }
        }
        splash.close();
    }

    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"Meta Data"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120, 140, 400, 40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 120, 150);
    }

    protected void start(final Runnable runnable) {
        // myDoggySetContext.put(MyDoggySet.class, null);
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
        memoryMonitorDescriptor.setAvailable(true);
        memoryMonitorDescriptor.setAnchor(ToolWindowAnchor.BOTTOM, 0);
        memoryMonitorDescriptor.setAnchorPositionLocked(true);
        if (runnable != null) {
            Thread t = new Thread(runnable);
            t.start();
        }
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = ControlCenterWindow.getInstance();
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        this.memoryMonitorDescriptor = new MemoryMonitorDockableDescriptor(
                (MyDoggyToolWindowManager) toolWindowManager,
                ToolWindowAnchor.BOTTOM);

        // Register a Tool Panels.
//        toolWindowManager.registerToolWindow(DOCK_PANEL_PALETTE, // Id
//                "", // Title
//                null, // Icon
//                frame.getMainPanel().getLibraryPane(), // Component
//                ToolWindowAnchor.RIGHT); // Anchor

//        toolWindowManager.registerToolWindow(DOCK_PANEL_PROPERTY, // Id
//                "", // Title
//                null, // Icon
//                LeftPanel.getInstance(), // Component
//                ToolWindowAnchor.RIGHT); // Anchor

//        toolWindowManager.registerToolWindow(DOCK_PANEL_ZOOM, // Id
//                "", // Title
//                null, // Icon
//                frame.getMainPanel().getGraphOutline(), // Component
//                ToolWindowAnchor.BOTTOM); // Anchor
        toolWindowManager.registerToolWindow(DOCK_PANEL_EXP_TREE, // Id
                "", // Title
                null, // Icon
                LeftPanel.getInstance(), // Component
                ToolWindowAnchor.LEFT); // Anchor

        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        showDockPanels(DOCK_PANEL_EXP_TREE, true);

        // to let the application to control the docks
        DISABLE_LOCKED_PANEL_MODE = false;
        initContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager
        // is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager,
                BorderLayout.CENTER); // "1,1,");
        this.frame.getContentPane().add(ToolBar.getInstance(),
                BorderLayout.NORTH);

        enableAutoHide();
        enableDockButton();
    }

    public void enableAutoHide() {
//        toolWindowManager.getToolWindow(DOCK_PANEL_PALETTE).setAutoHide(
//                ENABLE_AUTO_HIDE);
//        toolWindowManager.getToolWindow(DOCK_PANEL_PROPERTY).setAutoHide(
//                ENABLE_AUTO_HIDE);
//        toolWindowManager.getToolWindow(DOCK_PANEL_ZOOM).setAutoHide(
//                ENABLE_AUTO_HIDE);
    }

    public void enableDockButton() {
//        SlidingTypeDescriptor desc = (SlidingTypeDescriptor) toolWindowManager
//                .getToolWindow(DOCK_PANEL_PALETTE).getTypeDescriptor(
//                        ToolWindowType.SLIDING);
//        desc.setTransparentMode(true); // false to disable
//        desc = (SlidingTypeDescriptor) toolWindowManager.getToolWindow(
//                DOCK_PANEL_PROPERTY).getTypeDescriptor(ToolWindowType.SLIDING);
//        desc.setTransparentMode(true); // false to disable
//        desc = (SlidingTypeDescriptor) toolWindowManager.getToolWindow(
//                DOCK_PANEL_ZOOM).getTypeDescriptor(ToolWindowType.SLIDING);
//        desc.setTransparentMode(true); // false to disable
    }

    public void enableFloatButton() {
//        toolWindowManager.getToolWindow(DOCK_PANEL_PALETTE).setAutoHide(
//                ENABLE_FLOAT_BUTTON);
//        toolWindowManager.getToolWindow(DOCK_PANEL_PROPERTY).setAutoHide(
//                ENABLE_FLOAT_BUTTON);
//        toolWindowManager.getToolWindow(DOCK_PANEL_ZOOM).setAutoHide(
//                ENABLE_FLOAT_BUTTON);
    }

    public void enablePinButton() {
//        toolWindowManager.getToolWindow(DOCK_PANEL_PALETTE).setAutoHide(
//                ENABLE_PIN_BUTTON);
//        toolWindowManager.getToolWindow(DOCK_PANEL_PROPERTY).setAutoHide(
//                ENABLE_PIN_BUTTON);
//        toolWindowManager.getToolWindow(DOCK_PANEL_ZOOM).setAutoHide(
//                ENABLE_PIN_BUTTON);
    }

    public void enableMaximizeButton() {
//        toolWindowManager.getToolWindow(DOCK_PANEL_PALETTE).setAutoHide(
//                ENABLE_MAXIMIZE_BUTTON);
//        toolWindowManager.getToolWindow(DOCK_PANEL_PROPERTY).setAutoHide(
//                ENABLE_MAXIMIZE_BUTTON);
//        toolWindowManager.getToolWindow(DOCK_PANEL_ZOOM).setAutoHide(
//                ENABLE_MAXIMIZE_BUTTON);
    }

    public void showDockPanels(String dockPanelID, boolean visible) {
        ToolWindow toolWindow = toolWindowManager == null ? null
                : toolWindowManager.getToolWindow(dockPanelID);
        if (toolWindow != null && !DISABLE_LOCKED_PANEL_MODE) {
            toolWindow.setActive(visible);
        }
    }

    protected void initContentManager() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        Content content = contentManager.addContent("Canvas", "Canvas", null, // An
                // icon
                MainPanel.getInstance());
        content.setToolTipText("Tree tip");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e1) {
            e1.printStackTrace();

        }
        final CCApplication test = CCApplication.getInstance();
        try {
            test.run(new Runnable() {
                @Override
                public void run() {
                    System.out.println("test = ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public File getSelectedGSAFile() {
//        // todo return the selected node type
//        File selectedFile = MainPanel.getInstance().getProjecttree().getLastSelectedFile();
//        if(selectedFile!= null && selectedFile.getName().endsWith(".mxe")
//                && selectedFile.getPath().contains(mxResources.get("Argumentpath")))
//        {
//            return selectedFile;
//        }
//        return null;
//    }

//    public File[] getAllGSAFiles() {
//        File selectedFile = new File(MainPanel.getInstance().getProjectPath(),
//                File.separator + MainPanel.getInstance().getProject().getProjectName() +
//                        File.separator + mxResources.get("Argumentpath"));
//        if(selectedFile.exists())
//        {
//            return selectedFile.listFiles();
//        }
//        else
//        {
//            System.out.println("Error in argument graph file path.");
//            return null;
//        }
//    }


    public static class MemoryMonitorDockableDescriptor extends
            CustomDockableDescriptor {

        public MemoryMonitorDockableDescriptor(
                MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
            super(manager, anchor);
        }

        public void updateRepresentativeAnchor() {
        }

        public JComponent getRepresentativeAnchor(Component parent) {
            if (representativeAnchor == null) {
                representativeAnchor = new MemoryMonitorPanel(anchor);
            }
            return representativeAnchor;
        }

        public boolean isAvailableCountable() {
            return false;
        }

        public class MemoryMonitorPanel extends JPanel {
            int sleepTime;

            public MemoryMonitorPanel(ToolWindowAnchor anchor) {
                sleepTime = 1000;
                final JProgressBar memoryUsage = new JProgressBar();
                memoryUsage.setStringPainted(true);

                JLabel statusLabel = new JLabel();
                JButton gcButton = new JButton(new ImageIcon("..\\ControlCenter\\images\\gc.png"));
                gcButton.setBorderPainted(true);
                gcButton.setFocusable(false);
                gcButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                gcButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.gc();
                    }
                });

                Thread memoryThread = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            String grabbed = StringUtil.bytes2MBytes(Runtime
                                    .getRuntime().totalMemory()
                                    - Runtime.getRuntime().freeMemory());
                            String total = StringUtil.bytes2MBytes(Runtime
                                    .getRuntime().totalMemory());

                            memoryUsage.setMaximum(Integer.parseInt(total));
                            memoryUsage.setValue(Integer.parseInt(grabbed));

                            memoryUsage.setString(grabbed + " MB of " + total + " MB");
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                });
                memoryThread.setDaemon(true);
                memoryThread.setPriority(Thread.MIN_PRIORITY);
                memoryThread.start();

                switch (anchor) {
                    case BOTTOM:
                    case TOP:
                        memoryUsage.setOrientation(SwingConstants.HORIZONTAL);
                        setLayout(new GridBagLayout());
                        add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                        add(memoryUsage, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                        add(gcButton, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                        break;
                    case LEFT:
                        memoryUsage.setOrientation(SwingConstants.VERTICAL);
                        setLayout(new GridBagLayout());
                        add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
                                GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
                        add(memoryUsage, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                        add(gcButton, new GridBagConstraints(0, 3, 1, 1, 0, 0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                        break;

                    case RIGHT:
                        memoryUsage.setOrientation(SwingConstants.VERTICAL);
                        setLayout(new GridBagLayout());
                        add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
                                GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
                        add(memoryUsage, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                        add(gcButton, new GridBagConstraints(0, 3, 1, 1, 0, 0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                        break;
                }
                registerDragListener(memoryUsage);
                registerDragListener(gcButton);
                registerDragListener(this);
            }

            public void setSleepTime(int sleepTime) {
                this.sleepTime = sleepTime;
            }
        }

    }

}
