/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/

package it.adsc.smartpower.substatin.concenter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class ControlCenterWindow extends JFrame {
    private static ControlCenterWindow instance;
    protected MainPanel mainPanel;
    private boolean firstOpen = false;

    private ControlCenterWindow() throws HeadlessException {
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ControlCenterWindow getInstance() {
        if (instance == null) {
            instance = new ControlCenterWindow();
            instance.showIntro();
        }
        return instance;
    }

    private void setupGUI() throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            LoggingSample.getLogger().error("Exception: Unable to set the  ``Look and Feel of the system``", e1);
        }

        this.setTitle("SoftGrid Control Service Client");
        this.getContentPane().setLayout(new BorderLayout());
        mainPanel = MainPanel.getInstance();
        mainPanel.setupToolBar();
//      Logging in most updated path occurs from here onwards
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1280, 840);
        this.setVisible(true);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
//      Updates the frame title
//      ImageIcon img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("CyberSAGE_128_alt.png")));
//      this.setIconImage(img.getImage());
//      this.updateTitle();
    }


    private void showIntro() {
        // decide if the software is opened for the first time
        if (firstOpen) {
            try {
                JFrame panelFrame = instance;
                System.out.println(instance);
                System.out.println(panelFrame);
//              PopUpIntro popUpIntro = new PopUpIntro();
//              popUpIntro.setModal(true);
//              Centers inside the application frame
//              int x = panelFrame.getX() + (panelFrame.getWidth() - popUpIntro.getWidth()) / 2;
//              int y = panelFrame.getY() + (panelFrame.getHeight() - popUpIntro.getHeight()) / 2;
//              popUpIntro.setLocation(x, y);
//              Shows the modal dialog and waits
//              popUpIntro.setVisible(true);
            } catch (Exception e) {
                LoggingSample.getLogger().error("Exception: Unable to create the properties file", e);
            }
        }
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public Action bind(String name, final Action action, String iconUrl) {
//        AbstractAction newAction = new AbstractAction(name,
//                (iconUrl != null) ? new ImageIcon(MainPanel.class.getResource(iconUrl)) : null) {
//            public void actionPerformed(ActionEvent e) {
//                action.actionPerformed(new ActionEvent(mainPanel.getGraphComponent(), e.getID(), e.getActionCommand()));
//            }
//        };
//        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
//        return newAction;
        return null;
    }

    public void updateTitle() {
//        String title = (mainPanel.getCurrentFile() != null) ? mainPanel.getCurrentFile().getAbsolutePath() : mxResources.get("newDiagram");
//        if (mainPanel.getCurrentFile() != null) {
//            // Clear the property sheet for the new file type
//            if (mainPanel.getCurrentFile().getAbsolutePath().contains(mxResources.get("MalActivityPath"))) {
//                mainPanel.setCurrentFileType(mxResources.get("MalActivityPath"));
//            } else if (mainPanel.getCurrentFile().getAbsolutePath().contains(mxResources.get("Topologypath"))) {
//                mainPanel.setCurrentFileType(mxResources.get("Topologypath"));
//            } else if (mainPanel.getCurrentFile().getAbsolutePath().contains(mxResources.get("Attackpath"))) {
//                mainPanel.setCurrentFileType(mxResources.get("Attackpath"));
//            } else if (mainPanel.getCurrentFile().getAbsolutePath().contains(mxResources.get("Argumentpath"))) {
//                mainPanel.setCurrentFileType(mxResources.get("Argumentpath"));
//            } else if (mainPanel.getCurrentFile().getAbsolutePath().contains(mxResources.get("Rulesengine"))) {
//                mainPanel.setCurrentFileType(mxResources.get("Rulesengine"));
//            }
//        } else {
//            mainPanel.setCurrentFileType(mxResources.get("Undefined"));
//        }
//        if (mainPanel.isModified()) {
//            title += "*";
//        }
//        this.setTitle(title + " - " + mainPanel.getAppTitle());
//        System.out.println("mainPanel = " + mainPanel.getCurrentFileType());
    }
    public void displyServiceClient()
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            LoggingSample.getLogger().error("Exception: Unable to set the  ``Look and Feel of the system``", e1);
        }
        // final mxGraph graph = graphComponent.getGraph();
        ControlCenterWindow.getInstance().setVisible(true);
    }

    /**
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
       ControlCenterWindow.getInstance().displyServiceClient();
    }

    public void enableArgumentGraphActions(boolean enable) {
//        menuBar.treeNodeSelected(enable);
    }

}
