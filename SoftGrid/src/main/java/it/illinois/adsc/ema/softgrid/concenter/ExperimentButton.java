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

import javax.swing.*;
import java.awt.*;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class ExperimentButton extends JToggleButton{
    private Experiment experiment;

    public ExperimentButton(Experiment experiment) {
        this.experiment = experiment;
        this.setText( experiment.getName());
        this.setSize(100,40);
        this.setMaximumSize(new Dimension(100,40));
        this.setMinimumSize(new Dimension(100,40));
        setOpaque(true);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
        if(b)
        {
            setBackground(Color.GRAY);
        }
        else
        {
            setBackground(Color.WHITE);
        }
    }
}
