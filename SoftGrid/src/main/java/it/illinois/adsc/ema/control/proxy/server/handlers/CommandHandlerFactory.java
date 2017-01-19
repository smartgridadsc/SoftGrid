package it.illinois.adsc.ema.control.proxy.server.handlers;

import org.openmuc.j60870.TypeId;

/**
 * Created by prageethmahendra on 16/1/2017.
 */
public class CommandHandlerFactory {
    public static CommandHandler createCommandHandler(TypeId typeId) {
        switch (typeId) {
            case C_IC_NA_1:
                InterrogationHandler.getInstance();
                break;
        }
        return null;
    }
}
