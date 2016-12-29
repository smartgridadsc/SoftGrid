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
