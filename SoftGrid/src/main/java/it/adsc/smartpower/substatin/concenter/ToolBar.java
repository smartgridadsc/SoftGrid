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
