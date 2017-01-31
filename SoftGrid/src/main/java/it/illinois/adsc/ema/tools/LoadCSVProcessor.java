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
