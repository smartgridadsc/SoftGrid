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
package it.illinois.adsc.ema.control.center.command;

/**
 * Created by prageethmahendra on 18/2/2016.
 */
public class CommandParser {

    public static Command parseCommandString(String commandString) {
        if (commandString != null) {
            String[] elements = commandString.trim().split(">")[0].trim().split(" ");
            if (elements.length > 1) {
                Command command = new Command();
                for (int i = 0; i < elements.length; i++) {
                    try {
                        switch (i) {
                            case 0:
                                command.setCommandType(CommandType.getCommandType(elements[i].trim()));
                                break;
                            case 1:
                                if (command.getCommandType().equals(CommandType.ATTACK)) {
                                    // attack 100 linestatus=true CB
                                    command.setPercent(Integer.parseInt(elements[i].trim()));
                                } else {
                                    if (elements[i].trim().equalsIgnoreCase("all")) {
                                        command.setSubtationAddressSpace(255);
                                    } else {
                                        command.setSubtationAddressSpace(Integer.parseInt(elements[i].trim()));
                                    }
                                }
                                break;
                            case 2:
                                String[] keyValue = elements[i].split("=");
                                command.setFeild(keyValue[0].trim());
                                if (keyValue.length > 0) {
                                    command.setValue(keyValue[1].trim());
                                }
                                break;
                            case 3:
                                String iedType = elements[i].trim();
                                command.setIEDType(iedType);
                                break;
                            default:
                                System.out.println("Unknown command extension : " + elements[i]);
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid Command...!\n" + e.getMessage());
                        return null;
                    }
                }
                return command;
            }
        }
        return null;
    }
}
