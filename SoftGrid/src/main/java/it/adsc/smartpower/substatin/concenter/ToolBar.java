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

import javax.swing.*;

import it.adsc.smartpower.substatin.concenter.action.CreateExpAction;
import it.adsc.smartpower.substatin.concenter.action.DeleteExpAction;
import it.adsc.smartpower.substatin.concenter.action.SaveAction;

/*
 * Tool bar for the application
 */
public class ToolBar extends JToolBar {
    private static final long serialVersionUID = -8015443128436394471L;
    private static ToolBar instance = null;
    private boolean ignoreZoomChange = false;
    JButton refresh = null;

    private ToolBar(int orientation) {
        super(orientation);
    }

    public static ToolBar getInstance() {
        if(instance == null)
        {
            instance = new ToolBar(JToolBar.HORIZONTAL);
        }
        return instance;
    }

    public void setupGUI() throws Exception {
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);
        JButton newproj = new JButton(MainPanel.getInstance().bind("", new CreateExpAction(), "newEXP.png"));
        newproj.setToolTipText("New Experiment");
        add(newproj);
        JButton deleteExperiments = new JButton(MainPanel.getInstance().bind("", new DeleteExpAction(), "deleteEXP.png"));
        deleteExperiments.setToolTipText("Delete Experiment");
        add(deleteExperiments);
        addSeparator();
        JButton save = new JButton(MainPanel.getInstance().bind("", new SaveAction(), "SaveALL.png"));
        save.setToolTipText("Save All Experiments");
        add(save);
    }

    public void enableArgumentGraphActions(boolean enable) {
        refresh.setEnabled(enable);
    }
}
