package it.adsc.smartpower.substatin.concenter;

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
