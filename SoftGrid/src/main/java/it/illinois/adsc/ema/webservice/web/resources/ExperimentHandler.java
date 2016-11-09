package it.illinois.adsc.ema.webservice.web.resources;

import it.adsc.smartpower.substation.monitoring.EntiryFactory;
import it.illinois.adsc.ema.common.webservice.ExperimentRequest;
import it.illinois.adsc.ema.common.webservice.ExperimentResponse;
import it.illinois.adsc.ema.common.webservice.ExperimentStatus;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.webservice.file.FileHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prageethmahendra on 1/9/2016.
 */
@Path("experiment")
public class ExperimentHandler {
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public ExperimentResponse experiment(@HeaderParam("username") String userid,
                                         ExperimentRequest experimentRequest
    ) throws Exception {
        ExperimentResponse experimentResponse = new ExperimentResponse();
        String entity = experimentRequest.getEntity();
        boolean toIEDServer = entity == null || entity.trim().isEmpty() || entity.trim().equalsIgnoreCase("IED");
        if (toIEDServer) {
            switch (experimentRequest.getExperimentType()) {
                case SETUP:
                    // create folder structure
                case INIT:
                    return TestbedController.initializeIEDs(experimentRequest);
                case CHECK:
                    switch (TestbedController.getCurrentStatus(true)) {
                        case "NOT_STARTED":
                            experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
                            break;
                        case "STARTED":
                            experimentResponse.setExperimentStatus(ExperimentStatus.STARTED);
                            break;
                        case "STOPED":
                            experimentResponse.setExperimentStatus(ExperimentStatus.STOPED);
                            break;
                        case "INITIATED":
                            experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
                            break;
                        case "ERROR":
                            experimentResponse.setExperimentStatus(ExperimentStatus.ERROR);
                            break;
                    }
                    experimentResponse.setExperimentRequest(experimentRequest);
                    experimentResponse.setLogFileCount(3);
                    return experimentResponse;

                case RUN:
                    // execute commands

                    return experimentResponse;
                case RESET:
                    EntiryFactory.getCCControler().stopCCClient();
                    TestbedController.stopIEDServers();
//                    EntiryFactory.getIEDControler().stopIEDServers();
//                    EntiryFactory.getIEDControler().stopIEDServers();
                default:
                    experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
                    break;

            }
            experimentResponse.setExperimentStatus(ExperimentStatus.ERROR);
            return experimentResponse;
        } else {
//            experimentRequest.setGatewayPort(ConfigUtil.GATEWAY_CC_PORT);
//            experimentRequest.setGatewayIP(ConfigUtil.IP);
            switch (experimentRequest.getExperimentType()) {
                case SETUP:
                    // create folder structure
                case INIT:
                    return initializeCC(experimentRequest);
                case CHECK:
                    boolean isLive = EntiryFactory.getCCControler().isLive(experimentRequest.getGatewayIP(), experimentRequest.getGatewayPort());
                    experimentResponse.setExperimentStatus(isLive ? ExperimentStatus.STARTED : ExperimentStatus.INIT_STATUS);
//                    switch (TestbedController.getCurrentStatus(false, experimentRequest.getGatewayIP(), experimentRequest.getGatewayPort()).split(" ")[0].trim()) {
//                        case "CC_NOT_STARTED":
//                            experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
//                            break;
//                        case "CC_STARTED":
//                            experimentResponse.setExperimentStatus(ExperimentStatus.STARTED);
//                            break;
//                        case "CC_STOPED":
//                            experimentResponse.setExperimentStatus(ExperimentStatus.STOPED);
//                            break;
//                        case "CC_INITIATED":
//                            experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
//                            break;
//                        case "CC_ERROR":
//                            experimentResponse.setExperimentStatus(ExperimentStatus.ERROR);
//                            break;
//                        default:
//                            experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
//                            break;
//                    }
                    experimentResponse.setExperimentRequest(experimentRequest);
                    experimentResponse.setLogFileCount(3);
                    return experimentResponse;

                case RUN:
                    // execute commands
                    runCCCommand(experimentRequest);
                    return experimentResponse;
                case RESET:
                    EntiryFactory.getCCControler().stopCCClient();
                    return experimentResponse;
            }
            return experimentResponse;
        }
    }

    private void runCCCommand(ExperimentRequest experimentRequest) {
        runCCCommand(experimentRequest.getCommand());
    }

    private void runCCCommand(String command) {
        if (command != null) {
            if (command.equalsIgnoreCase("run script")) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String scriptFilePath = FileHandler.LAST_DOWNLOADED_FILE;
                        if (scriptFilePath != null && !scriptFilePath.isEmpty()) {
                            File file = new File(scriptFilePath);
                            if (file.exists()) {
                                HashMap<Integer, List<String>> commands = new HashMap<Integer, List<String>>();
                                FileReader fr = null;
                                BufferedReader bufferedReader = null;
                                try {
                                    fr = new FileReader(file);
                                    bufferedReader = new BufferedReader(fr);
                                    String line = "";
                                    while ((line = bufferedReader.readLine()) != null) {
                                        for (String command : line.split(";")) {
                                            String[] timedCommand = command.trim().split(">");
                                            if (timedCommand.length >= 2) {
                                                int timeMillis = 0;
                                                try {
                                                    timeMillis = Integer.parseInt(timedCommand[0].trim());
                                                    if (commands.get(timeMillis) == null) {
                                                        commands.put(timeMillis, new ArrayList<String>());
                                                    }
                                                    commands.get(timeMillis).add(timedCommand[1].trim());
                                                } catch (NumberFormatException e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (timedCommand.length == 1) {
                                                if (commands.get(0) == null) {
                                                    commands.put(0, new ArrayList<String>());
                                                }
                                                commands.get(0).add(timedCommand[0].trim());
                                            }
                                        }
                                    }
                                    long startTime = System.currentTimeMillis();
                                    while (commands.size() > 0) {
                                        long currentTimeMillis = System.currentTimeMillis();
                                        for (Integer integer : commands.keySet()) {
                                            long execTime = startTime + integer;
                                            if (execTime <= currentTimeMillis) {
                                                for (String command : commands.get(integer)) {
                                                    runCCCommand(command);
                                                }
                                            } else {
                                                try {
                                                    Thread.sleep(execTime - currentTimeMillis);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        bufferedReader.close();
                                        fr.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }
                    }
                };
                new Thread(runnable).start();
            } else {
                try {
                    EntiryFactory.getCCControler().runCommand(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private ExperimentResponse initializeIEDs(ExperimentRequest experimentRequest) {
//        ExperimentResponse experimentResponse = new ExperimentResponse();
//        System.out.println("Experimenting...!");
//        try {
//            EntiryFactory.getIEDControler().displayMonitorWindow();
//            EntiryFactory.getIEDControler().loadConfigurations();
//            EntiryFactory.getIEDControler().startIEDs();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        experimentResponse.setExperimentRequest(experimentRequest);
//        experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
//        experimentResponse.setLogFileCount(3);
//        return experimentResponse;
//    }

    private ExperimentResponse initializeCC(ExperimentRequest experimentRequest) {
        ExperimentResponse experimentResponse = new ExperimentResponse();
        System.out.println("Experimenting...!");
        try {
            EntiryFactory.getCCControler().startCCClient(experimentRequest.getGatewayIP(), experimentRequest.getGatewayPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
        experimentResponse.setExperimentRequest(experimentRequest);
        experimentResponse.setExperimentStatus(ExperimentStatus.CC_INIT_STATUS);
        experimentResponse.setLogFileCount(3);
        return experimentResponse;
    }



}
