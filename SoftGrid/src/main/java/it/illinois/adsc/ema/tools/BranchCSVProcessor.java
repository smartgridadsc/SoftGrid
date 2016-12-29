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

public class BranchCSVProcessor {
	private static final String PATH="C:\\Users\\daisuke.m\\Desktop\\37-bus_experiments\\random_attack_0_mitigation_5sec_4sec\\result";
	private static final String OUT_PATH=PATH + "\\summary\\";

	private static final double LIMIT = 100d;
	private static String inputPath = null;
	
	public static void main(String args[]) throws IOException{
		
		inputPath = PATH;
		if(args.length > 1){
			inputPath = args[0];
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter(OUT_PATH + "BranchLimitViolation.csv"));
		
		File[] files = new File(inputPath).listFiles();
		String filePrefix = null;
		
		for(File file : files){
			
			if(!file.getName().endsWith("_BRANCH_MWTO.csv")) continue;
			
			String prefix = file.getName().split("_")[0];
			
			if(filePrefix==null || !prefix.equals(filePrefix)) {
				filePrefix = prefix;
				System.out.println();
				pw.println();
				System.out.print(prefix + "," + countLimitViolation(file, LIMIT) + ",");
				pw.print(prefix + "," + countLimitViolation(file, LIMIT) + ",");
			}else{
				System.out.print(countLimitViolation(file, LIMIT) + ",");
				pw.print(countLimitViolation(file, LIMIT) + ",");
			}

//			System.out.println("############## " + file.getName() + " ################");
//			for(int i=0; i <=100; i+=25)
//				System.out.println(i+ "% Limit violation: " + countLimitViolation(file, (double)i));
		}
		
		
		
		pw.flush();
		pw.close();
		
	}
	
	private static int countLimitViolation (File file, double limit){
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			line = br.readLine();	//header row
		
			String tokens[] = line.split(",");
			double[] maxValues = new double[tokens.length-1];
		
			while((line=br.readLine())!= null){

				String values[] = line.split(",");
				for(int i=1; i < values.length; i++){
					
					double value = Double.parseDouble(values[i].trim());
					if(value > maxValues[i-1])maxValues[i-1] = value;
					
				}
			}
		
			int violationCount = 0;
		
			for(int i=0; i < maxValues.length; i++){
				if(maxValues[i] > limit)violationCount++;
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
	
}
