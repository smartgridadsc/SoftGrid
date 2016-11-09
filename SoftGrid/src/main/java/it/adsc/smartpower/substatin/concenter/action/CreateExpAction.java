package it.adsc.smartpower.substatin.concenter.action;

import it.adsc.smartpower.substatin.concenter.Experiment;
import it.adsc.smartpower.substatin.concenter.ui.LeftPanel;
import it.adsc.smartpower.substatin.concenter.config.ExpConfigManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class CreateExpAction extends AbstractAction {
    private static final String configFileName = "config/config.properties";

    @Override
    public void actionPerformed(ActionEvent e) {
        String expName = LeftPanel.getInstance().getDefaultExperimentName();
        String configPath = ExpConfigManager.getInstance().crateExperimentDirector(expName);
        LeftPanel.getInstance().addExperiment(new Experiment(expName, configPath));
    }


}
