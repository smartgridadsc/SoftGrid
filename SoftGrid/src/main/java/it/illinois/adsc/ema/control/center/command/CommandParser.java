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
