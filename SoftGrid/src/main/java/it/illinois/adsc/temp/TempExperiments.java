package it.illinois.adsc.temp;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by prageeth.g on 2/8/2016.
 */
public class TempExperiments {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\prageeth.g\\Dropbox\\EMA-references (1)\\SmartPower\\experiment\\Attack Experiment without ACM-Pi\\");
        int totalFrequency = 0;
        int totalFrequencyDevice = 0;
        Set<String> frequencyComponents = new HashSet<String>();
        int totalVoltage = 0;
        int totalVoltageDevice = 0;
        Set<String> voltageComponents = new HashSet<String>();
        int totalBranchLimit = 0;
        int totalBranchLimitDevice = 0;
        Set<String> branchLimitComponents = new HashSet<String>();
        int folderCount = 0;
        if(file.exists())
        {
            for (File file1 : file.listFiles()) {
                if(file1.isDirectory())
                {
                    folderCount++;
                    int frequency= 0;
                    int voltage = 0;
                    int branchLimit= 0;
                    for (File file2 : file1.listFiles()) {
                        if(!file2.isDirectory() && file2.getName().contains("ViolationCount.csv"))
                        {
                            try {
                                BufferedReader bufferedReader = new BufferedReader(new FileReader(file2));
                                String line = "";
                                while((line = bufferedReader.readLine()) != null)
                                {
                                    //1470112506782,Over Frequency :60.54,Time :12:35:07:646000, u'Bus   53  Frequency '
                                    //1470112534099,Branch Limit :182.18,Time :12:35:38:644000, u'Branch   14 34 1  TSACLinePercent '
                                    //1470112534242,Under Voltage(5) :0.94,Time :12:35:38:644000, u'Bus   24  TSBusVPU '
                                    if(line.contains("Frequency"))
                                    {
                                        frequency++;
                                        String[] tokens = line.split(",");
                                        frequencyComponents.add(tokens[tokens.length-1]);
                                    }
                                    else  if(line.contains("TSACLinePercent"))
                                    {
                                        branchLimit++;
                                        String[] tokens = line.split(",");
                                        branchLimitComponents.add(tokens[tokens.length - 1]);
                                    } else  if(line.contains("TSBusVPU"))
                                    {
                                        voltage++;
                                        String[] tokens = line.split(",");
                                        voltageComponents.add(tokens[tokens.length-1]);
                                    }
                                }
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    totalFrequency += frequency;
                    totalVoltage += voltage;
                    totalBranchLimit += branchLimit;
                    totalFrequencyDevice += frequencyComponents.size();
                    totalBranchLimitDevice += branchLimitComponents.size();
                    totalVoltageDevice += voltageComponents.size();


                    System.out.println("FRQ = " + frequency + "," + frequencyComponents.size());
                    System.out.println("VOL = " + voltage + "," + voltageComponents.size());
                    System.out.println("BRL = " + branchLimit + "," + branchLimitComponents.size());
                    System.out.println("=========================");
                    frequencyComponents = new HashSet<String>();
                    branchLimitComponents = new HashSet<String>();
                    voltageComponents = new HashSet<String>();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
