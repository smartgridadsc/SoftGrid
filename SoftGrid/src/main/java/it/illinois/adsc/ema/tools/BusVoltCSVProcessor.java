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

public class BusVoltCSVProcessor {
	private static final String PATH="C:\\Users\\daisuke.m\\Desktop\\37-bus_experiments\\random_attack_0_mitigation_5sec_4sec\\result";
	private static final String OUT_PATH=PATH + "\\summary\\";

	private static final int ALLOWANCE = 20;
	
	private static String inputPath = null;
	

	public static void main(String args[]) throws IOException{
		
		inputPath = PATH;
		if(args.length > 1){
			inputPath = args[0];
		}
		
		File[] files = new File(inputPath).listFiles();
		
		PrintWriter ovout = new PrintWriter(new FileWriter(OUT_PATH+"OVViolationCount_" + ALLOWANCE +"p.csv"));
		PrintWriter uvout = new PrintWriter(new FileWriter(OUT_PATH+"UVViolationCount_" + ALLOWANCE +"p.csv"));

		String filePrefix = null;
		
		for(File file : files){
			
			if(!file.getName().endsWith("_BUS_VOLT.csv")) continue;
			
//			System.out.println("############## " + file.getName() + " ################");
//			for(int i=5; i <=20; i+=5){
//				System.out.println(i+ "% Upper Limit violation: " + countUpperLimitViolation(file, (double)i));
//				System.out.println(i+ "% Lower Limit violation: " + countLowerLimitViolation(file, (double)i));
//			}	
			
			String prefix = file.getName().split("_")[0];
			
			if(filePrefix==null || !prefix.equals(filePrefix)) {
				filePrefix = prefix;
				//System.out.println();
				ovout.println();
				uvout.println();
				//System.out.print(prefix + "," + countUpperLimitViolation(file, 0.5) + ",");
				ovout.print(prefix + "," + countUpperLimitViolation(file, ALLOWANCE) + ",");
				//System.out.print(prefix + "," + countLowerLimitViolation(file, 0.5) + ",");
				uvout.print(prefix + "," + countLowerLimitViolation(file, ALLOWANCE) + ",");
			}else{
				//System.out.print(countUnservedLoad(file) + ",");
				ovout.print(countUpperLimitViolation(file, ALLOWANCE) + ",");
				uvout.print(countLowerLimitViolation(file, ALLOWANCE) + ",");
			}

			
			
			
		}
		
		ovout.flush();
		ovout.close();
		uvout.flush();
		uvout.close();

		
		
		
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
				if(maxValues[i] > ((double)(100+limit)/(double)100))violationCount++;
				//System.out.print(maxValues[i] + ",");
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
				if(minValues[i] < ((double)(100-limit)/(double)100))violationCount++;
				//System.out.print(minValues[i] + ",");
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
