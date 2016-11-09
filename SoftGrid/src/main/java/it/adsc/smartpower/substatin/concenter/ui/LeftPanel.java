package it.adsc.smartpower.substatin.concenter.ui;

import it.adsc.smartpower.substatin.concenter.Experiment;
import it.adsc.smartpower.substatin.concenter.ui.tree.ExpTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class LeftPanel extends JPanel implements ActionListener {
    //    private ArrayList<ExperimentButton> experimentButtons = new ArrayList<ExperimentButton>();
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

        Collections.sort(experimentTrees, new Comparator<ExpTree>() {
            @Override
            public int compare(ExpTree o1, ExpTree o2) {
                return o1.getExperiment().getName().compareTo(o2.getExperiment().getName());
            }
        });

//        ExperimentButton experimentButton = new ExperimentButton(experiment);
//        experimentButtons.add(experimentButton);
//        experimentButton.addActionListener(this);

//        Collections.sort(experimentButtons, new Comparator<ExperimentButton>() {
//            @Override
//            public int compare(ExperimentButton o1, ExperimentButton o2) {
//                return o1.getExperiment().getName().compareTo(o2.getExperiment().getName());
//            }
//        });
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
//        if (e.getSource() instanceof ExperimentButton) {
//            for (ExperimentButton experimentButton : experimentButtons) {
//                experimentButton.setSelected(false);
//            }
//            selectedButton = (ExperimentButton) e.getSource();
//            selectedButton.setSelected(true);
//            fireExpSelected();
//        }
//        else
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
