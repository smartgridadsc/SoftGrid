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

package it.illinois.adsc.ema.softgrid.concenter.ui;

import it.illinois.adsc.ema.softgrid.concenter.Experiment;
import it.illinois.adsc.ema.softgrid.concenter.ui.tree.ExpTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.*;
import java.util.Collections;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class LeftPanel extends JPanel implements ActionListener {
    private ArrayList<ExpTree> experimentTrees = new ArrayList<ExpTree>();
    private ArrayList<ExperimentListener> experimentListeners = new ArrayList<ExperimentListener>();
    private ExpTree selecteTree = null;
    private static LeftPanel instance;

    private LeftPanel() {
        super();
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LeftPanel getInstance() {
        if (instance == null) {
            instance = new LeftPanel();
        }
        return instance;
    }

    public void setupGUI() {
        this.setLayout(new GridBagLayout());
    }

    public void addExperiment(Experiment experiment) {
        ExpTree expTree = new ExpTree(experiment);
        experimentTrees.add(expTree);
        expTree.addActionListener(this);

        Collections.sort(experimentTrees, (o1, o2) -> o1.getExperiment().getName().compareTo(o2.getExperiment().getName()));
        reloadExperimentPanel();
    }

    private void reloadExperimentPanel() {
        this.removeAll();
        int count = 0;
        for (ExpTree expTree : experimentTrees) {
            expTree.setBorder(BorderFactory.createEtchedBorder());
            this.add(expTree, new GridBagConstraints(0, count++, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        if (selecteTree == null && experimentTrees.size() > 0) {
            experimentTrees.get(0).setSelected(true);
        }
        this.add(new JLabel(), new GridBagConstraints(0, count++, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.updateUI();
    }

    public String getDefaultExperimentName() {
        return "Experiment " + (experimentTrees.size() + 1);
    }

    public Experiment removeSelectedExp() {
        if (selecteTree != null) {
            experimentTrees.remove(selecteTree);
            reloadExperimentPanel();
            Experiment experiment = selecteTree.getExperiment();
            fireExpDeleted(selecteTree);
            selecteTree = null;
            return experiment;
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof ExpTree) {
            for (ExpTree expTree : experimentTrees) {
                expTree.removeActionListener(this);
                expTree.setSelected(false);
                expTree.addActionListener(this);
            }
            selecteTree = (ExpTree) e.getSource();
            selecteTree.removeActionListener(this);
            selecteTree.setSelected(true);
            selecteTree.addActionListener(this);
            fireExpSelected();
        }
    }

    public Experiment getSelectedExperiment() {
        return selecteTree == null ? null : selecteTree.getExperiment();
    }

    public void addExperimentListener(ExperimentListener experimentListener) {
        if (experimentListener == null) {
            System.out.println("Invalid Experiment Listener : " + experimentListener.toString());
        }
        experimentListeners.add(experimentListener);
    }

    public void removeExperimentListener(ExperimentListener experimentListener) {
        if (experimentListener == null) {
            System.out.println("Invalid Experiment Listener : " + experimentListener.toString());
        }
        experimentListeners.remove(experimentListener);
    }

    private void fireExpSelected() {
        if (selecteTree != null) {
            for (ExperimentListener experimentListener : experimentListeners) {
                experimentListener.experimentSelected(selecteTree.getExperiment());
            }
        }
    }

    private void fireExpDeleted(ExpTree deletedExperimenExpTree) {
        if (deletedExperimenExpTree != null) {
            for (ExperimentListener experimentListener : experimentListeners) {
                experimentListener.experimentDeleted(deletedExperimenExpTree.getExperiment());
            }
        }
    }
}
