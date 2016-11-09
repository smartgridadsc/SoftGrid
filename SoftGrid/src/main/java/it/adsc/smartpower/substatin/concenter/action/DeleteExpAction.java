package it.adsc.smartpower.substatin.concenter.action;

import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;
import it.adsc.smartpower.substatin.concenter.Experiment;
import it.adsc.smartpower.substatin.concenter.ui.LeftPanel;
import it.adsc.smartpower.substatin.concenter.config.ExpConfigManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class DeleteExpAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (LeftPanel.getInstance().getSelectedExperiment() != null &&
                JOptionPane.showConfirmDialog(ControlCenterWindow.getInstance(),
                "Do you want to permanently delete the selected experiment details?") == JOptionPane.YES_OPTION) {
            Experiment experiment = LeftPanel.getInstance().removeSelectedExp();
            ExpConfigManager.getInstance().removeExperimentConfig(experiment.getName());
        }
    }
}
