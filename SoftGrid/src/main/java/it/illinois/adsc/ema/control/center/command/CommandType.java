package it.illinois.adsc.ema.control.center.command;

/**
 * Created by prageethmahendra on 18/2/2016.
 */
public enum CommandType {
    INTERROGATION,SINGLE_COMMAND,SET_SHORT_FLOAT,ATTACK,CANCEL;

    public static CommandType getCommandType(final String element) throws Exception {
        switch (element.trim().toUpperCase())
        {
            case "INTERROGATION": return INTERROGATION;
            case "SCOMMAND": return SINGLE_COMMAND;
            case "SFLOAT": return SET_SHORT_FLOAT;
            case "ATTACK": return ATTACK;
            case "CANCEL": return CANCEL;
            default:
                throw new Exception("Invalid Command Type : " + element);
        }
    }
}
