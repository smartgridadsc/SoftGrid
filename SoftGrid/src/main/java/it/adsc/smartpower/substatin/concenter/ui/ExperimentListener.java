package it.adsc.smartpower.substatin.concenter.ui;

import it.adsc.smartpower.substatin.concenter.Experiment;

/**
 * Created by prageethmahendra on 30/8/2016.
 */
public interface ExperimentListener {
    void experimentSelected(Experiment experiment);
    void experimentDeleted(Experiment experiment);
}
