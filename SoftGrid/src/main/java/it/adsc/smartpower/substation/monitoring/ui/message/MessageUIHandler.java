package it.adsc.smartpower.substation.monitoring.ui.message;

import javax.swing.*;

/**
 * Created by prageethmahendra on 5/8/2016.
 */
public class MessageUIHandler {

    private JTextArea resultArea;
    private JScrollPane scrollPane;
    private String postFixNewLines = "\n\n\n\n\n\n\n";

    public MessageUIHandler(JTextArea resultArea, JScrollPane scrollPane) {
        this.resultArea = resultArea;
        this.scrollPane = scrollPane;
    }

    public synchronized void addLogMessage(String message) {
        resultArea.setText(resultArea.getText().replace(postFixNewLines, "") + "\n" + message);// + postFixNewLines);
        resultArea.updateUI();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressScroll();
            }
        });
    }

    private void progressScroll() {
        scrollPane.updateUI();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        scrollPane.updateUI();
    }
}
