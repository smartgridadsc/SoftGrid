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
package it.illinois.adsc.util;

import java.io.*;

/**
 * Created by prageethmahendra on 29/12/2016.
 */
public class GPLHeaderGenerator {

    public static void main(String[] args) {
        File dir = new File("C:\\ADSC\\EMA\\SoftGrid\\SoftGrid\\src\\");
        String headerString = "/* Copyright (C) 2016 Advanced Digital Science Centre\n" +
                "\n" +
                "        * This file is part of Soft-Grid.\n" +
                "        * For more information visit https://www.illinois.adsc.com.sg/cybersage/\n" +
                "        *\n" +
                "        * Soft-Grid is free software: you can redistribute it and/or modify\n" +
                "        * it under the terms of the GNU General Public License as published by\n" +
                "        * the Free Software Foundation, either version 3 of the License, or\n" +
                "        * (at your option) any later version.\n" +
                "        *\n" +
                "        * Soft-Grid is distributed in the hope that it will be useful,\n" +
                "        * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "        * GNU General Public License for more details.\n" +
                "        *\n" +
                "        * You should have received a copy of the GNU General Public License\n" +
                "        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.\n" +
                "\n" +
                "        * @author Prageeth Mahendra Gunathilaka\n" +
                "*/\n";
        if (dir.exists()) {
            addHeaderToAllJava(dir, headerString);
        }
    }

    private static void addHeaderToAllJava(File file, String headerString) {
        if (file.exists() && file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                addHeaderToAllJava(subFile, headerString);
            }
        } else if (file.exists() && file.getName().endsWith(".java")) {
            FileReader fileReader = null;
            FileWriter fileWriter = null;
            try {
                fileReader = new FileReader(file);

                BufferedReader reader = new BufferedReader(fileReader);
                String fileString = "";
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    fileString += temp + "\n";
                }
                fileReader.close();
                reader.close();
                if (!fileString.startsWith(headerString)) {
                    fileString = headerString + fileString;
                }
                fileWriter = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(fileString);
                writer.flush();
                fileWriter.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
