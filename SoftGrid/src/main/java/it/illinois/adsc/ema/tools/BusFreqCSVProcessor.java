package it.illinois.adsc.ema.tools;

import java.io.*;

public class BusFreqCSVProcessor {
	private static final String PATH="C:\\Users\\daisuke.m\\Desktop\\37-bus_experiments\\random_attack_0_mitigation_5sec_4sec\\result";
	private static final String OUT_PATH=PATH + "\\summary\\";

	private static String inputPath = null;
	
	private static double NOMINAL=60;

	public static void main(String args[]) throws IOException{
		
		inputPath = PATH;
		if(args.length > 1){
			inputPath = args[0];
		}
		
		File[] files = new File(inputPath).listFiles();
		
		String filePrefix = null;

		PrintWriter ofout = new PrintWriter(new FileWriter(OUT_PATH+"OFViolationCount.csvTODO"));
		PrintWriter ufout = new PrintWriter(new FileWriter(OUT_PATH+"UFViolationCount.csvTODO"));
		
		
		for(File file : files){
			
			if(!file.getName().endsWith("_BUS_FREQ.csv")) continue;

			String prefix = file.getName().split("_")[0];
			
			if(filePrefix==null || !prefix.equals(filePrefix)) {
				filePrefix = prefix;
				//System.out.println();
				ofout.println();
				ufout.println();
				//System.out.print(prefix + "," + countUpperLimitViolation(file, 0.5) + ",");
				ofout.print(prefix + "," + countUpperLimitViolation(file, 0.5) + ",");
				//System.out.print(prefix + "," + countLowerLimitViolation(file, 0.5) + ",");
				ufout.print(prefix + "," + countLowerLimitViolation(file, 0.5) + ",");
			}else{
				//System.out.print(countUnservedLoad(file) + ",");
				ofout.print(countUpperLimitViolation(file, 0.5) + ",");
				ufout.print(countLowerLimitViolation(file, 0.5) + ",");
			}
			
			//System.out.println("############## " + file.getName() + " ################");
			//System.out.println("Upper Limit violation: " + countUpperLimitViolation(file, 0.5));
			//System.out.println("Lower Limit violation: " + countLowerLimitViolation(file, 0.5));
		}
		
		ofout.flush();
		ofout.close();
		ufout.flush();
		ufout.close();

		
		
	}
	
	private static int countUpperLimitViolation (File file, double limit){
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			line = br.readLine();	//header row
		
			String tokens[] = line.split(",");
			double[] maxValues = new double[tokens.length-1];
			double[] initialValues = new double[tokens.length-1];
			
			boolean initial = true;
			
			while((line=br.readLine())!= null){

				String values[] = line.split(",");
				for(int i=1; i < values.length; i++){
					
					double value = Double.parseDouble(values[i].trim());

					if(initial)initialValues[i-1] = value;
					
					if(value > maxValues[i-1])maxValues[i-1] = value;
					
				}
				initial=false;
			}
		
			int violationCount = 0;
		
			for(int i=0; i < maxValues.length; i++){
				if(maxValues[i] > NOMINAL+limit)violationCount++;
				//System.out.println(maxValues[i] + "," + (maxValues[i] > NOMINAL+limit));
			}
			//System.out.println();
			return violationCount;
			
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}finally{
			try{
				br.close();
			}catch(IOException ie){}
		}
	}

	private static int countLowerLimitViolation (File file, double limit){
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			line = br.readLine();	//header row
		
			String tokens[] = line.split(",");
			double[] minValues = new double[tokens.length-1];
			double[] initialValues = new double[tokens.length-1];
			
			boolean initial = true;
			
			while((line=br.readLine())!= null){

				String values[] = line.split(",");
				for(int i=1; i < values.length; i++){
					
					double value = Double.parseDouble(values[i].trim());

					if(initial){
						initialValues[i-1] = value;
						minValues[i-1] = value;
					}
					
					if(!initial && (value < minValues[i-1]))minValues[i-1] = value;
					
				}
				initial=false;
			}
		
			int violationCount = 0;
		
			for(int i=0; i < minValues.length; i++){
				if(minValues[i] < NOMINAL-limit)violationCount++;
				//System.out.println(minValues[i] + "," + (minValues[i] < NOMINAL-limit));
			}
			//System.out.println();
			return violationCount;
			
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}finally{
			try{
				br.close();
			}catch(IOException ie){}
		}
	}

}
