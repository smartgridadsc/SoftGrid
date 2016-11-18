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
    }


    private void showIntro() {
        // decide if the software is opened for the first time
        if (firstOpen) {
            try {
                JFrame panelFrame = instance;
                System.out.println(instance);
                System.out.println(panelFrame);
            } catch (Exception e) {
                LoggingSample.getLogger().error("Exception: Unable to create the properties file", e);
            }
        }
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }


    public Action bind(String name, final Action action, String iconUrl) {
        return null;
    }

    public void displyServiceClient()
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            LoggingSample.getLogger().error("Exception: Unable to set the  ``Look and Feel of the system``", e1);
        }
        ControlCenterWindow.getInstance().setVisible(true);
    }

    /**
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
       ControlCenterWindow.getInstance().displyServiceClient();
    }

}
