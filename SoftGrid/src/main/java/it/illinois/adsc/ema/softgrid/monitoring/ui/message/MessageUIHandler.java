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
package it.illinois.adsc.ema.softgrid.monitoring.ui.message;

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
