package it.illinois.adsc.ema.tools;

import java.io.*;

public class LoadCSVProcessor {
	private static final String PATH="C:\\Users\\daisuke.m\\Desktop\\37-bus_experiments\\random_attack_0_mitigation_5sec_4sec\\result";
	private static final String OUT_PATH=PATH + "\\summary\\";

	private static String inputPath = null;
	
	public static void main(String args[]) throws IOException{
		
		inputPath = PATH;
		if(args.length > 1){
			inputPath = args[0];
		}
		
		File[] files = new File(inputPath).listFiles();

		String filePrefix = null;
		PrintWriter pw = new PrintWriter(OUT_PATH + "UnservedLoad.csv");
		
		for(File file : files){
			
			if(!file.getName().endsWith("_LOAD_MW.csv")) continue;
			
			String prefix = file.getName().split("_")[0];
			
			if(filePrefix==null || !prefix.equals(filePrefix)) {
				filePrefix = prefix;
				System.out.println();
				pw.println();
				System.out.print(prefix + "," + countUnservedLoad(file) + ",");
				pw.print(prefix + "," + countUnservedLoad(file) + ",");
			}else{
				System.out.print(countUnservedLoad(file) + ",");
				pw.print(countUnservedLoad(file) + ",");
			}
			
			//System.out.println("############## " + file.getName() + " ################");
			//System.out.println("Total Unserved Load: " + countUnservedLoad(file));
		
		}
	
		pw.flush();
		pw.close();
	}
	
	private static double countUnservedLoad (File file){
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			line = br.readLine();	//header row
		
			String tokens[] = line.split(",");
			double[] initialValues = new double[tokens.length-1];
			double[] lastValues = new double[tokens.length-1];
			boolean initial = true;
			while((line=br.readLine())!= null){

				String values[] = line.split(",");
				for(int i=1; i < values.length; i++){
					
					double value = Double.parseDouble(values[i].trim());
					if(initial)initialValues[i-1] = value;
					else lastValues[i-1] = value;
						
				}
				initial = false;
			}
		
			double totalUnservedLoad = 0d;
		
			for(int i=0; i < initialValues.length; i++){
				double unservedLoad = initialValues[i] - lastValues[i];
				if(unservedLoad > 0)totalUnservedLoad+= unservedLoad;
				
			}
			//System.out.println();
			return totalUnservedLoad;
			
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
