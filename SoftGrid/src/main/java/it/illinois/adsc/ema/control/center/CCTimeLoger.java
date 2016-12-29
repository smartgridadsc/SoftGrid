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
package it.illinois.adsc.ema.control.center;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by prageethmahendra on 3/2/2016.
 */
public class CCTimeLoger {
    private static volatile long startTime = System.currentTimeMillis();
    private static volatile String currentCommand = "";
    private static BufferedWriter bufferedWriter = null;

    static {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File("CC_Res_Time.csv")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logDuration(String prfix) {
        try {
            bufferedWriter.newLine();
            if(prfix != null)
            {
                bufferedWriter.write(currentCommand + " : " + prfix + ": ");
            }
            bufferedWriter.write(String.valueOf(System.currentTimeMillis() - startTime));
            bufferedWriter.flush();
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetStartTime(String command) {
        try {
            bufferedWriter.newLine();
            bufferedWriter.write("Request Send..." + command);
            bufferedWriter.flush();
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentCommand = command;
        startTime = System.currentTimeMillis();
    }
}
