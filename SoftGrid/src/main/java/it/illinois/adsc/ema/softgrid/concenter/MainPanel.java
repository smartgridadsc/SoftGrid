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
package it.illinois.adsc.ema.softgrid.concenter;

import it.illinois.adsc.ema.softgrid.concenter.config.ExpConfigManager;
import it.illinois.adsc.ema.softgrid.concenter.ui.CenterPanel;
import it.illinois.adsc.ema.softgrid.concenter.ui.ExperimentListener;
import it.illinois.adsc.ema.softgrid.concenter.ui.LeftPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class MainPanel extends JPanel implements ExperimentListener {
    private static MainPanel mainPanel;
    private ToolBar headerPanel = ToolBar.getInstance();
    private LeftPanel leftPanel;
    private CenterPanel centerPanel;
    private HashMap<Experiment, CenterPanel> experimentPanel = new HashMap<Experiment, CenterPanel>();

    public static MainPanel getInstance() {
        if (mainPanel == null) {
            mainPanel = new MainPanel();
        }
        return mainPanel;
    }

    private MainPanel() {
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        this.setLayout(new BorderLayout());
        leftPanel = LeftPanel.getInstance();

        headerPanel.setBorder(BorderFactory.createEtchedBorder());
        Map<String, String> pathToFileMap = ExpConfigManager.getInstance().getExperimentList();
        Experiment firstExperiment = null;
        for (String path : pathToFileMap.keySet()) {
            Experiment experiment = new Experiment(pathToFileMap.get(path), path);
            firstExperiment = firstExperiment == null ? experiment : firstExperiment;
            experimentPanel.put(experiment, new CenterPanel(experiment));
            leftPanel.addExperiment(experiment);
        }
        centerPanel = new CenterPanel(firstExperiment);
        leftPanel.addExperimentListener(this);
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(leftPanel, BorderLayout.WEST);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void experimentSelected(Experiment experiment) {
        this.remove(centerPanel);
        centerPanel = experimentPanel.get(experiment);
        if (centerPanel == null) {
            centerPanel = new CenterPanel(experiment);
            experimentPanel.put(experiment, centerPanel);
        }
        this.add(centerPanel, BorderLayout.CENTER);
        this.updateUI();
        centerPanel.updateUI();
    }

    @Override
    public void experimentDeleted(Experiment experiment) {
        experimentPanel.remove(experiment);
    }

    public CenterPanel getCenterPanel() {
        return centerPanel;
    }

    public Action bind(String name, final Action action, String iconUrl) throws IOException {
        ImageIcon img = iconUrl == null ? null : new ImageIcon(
                ImageIO.read(getClass().getClassLoader().getResourceAsStream(iconUrl)));
        AbstractAction newAction = new AbstractAction(name, img) {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand()));
            }
        };
        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
        return newAction;
    }

    public void setupToolBar() {
        try {
            headerPanel.setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
