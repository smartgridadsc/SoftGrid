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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//determine if the simulation has been terminated in the middle or not, based on line count in the CSv.
public class IncompleteSimulationCounter {

	private static final String PATH="C:\\Users\\daisuke.m\\Desktop\\37-bus_experiments\\random_attack_0_mitigation_5sec_4sec\\result";
	private static final String OUT_PATH=PATH + "\\summary\\";
	private static final int LINE_COUNT_THRESHOLD =1200;
	
	private static String inputPath = null;

	public static void main(String args[]) throws IOException{
		
		inputPath = PATH;
		if(args.length > 1){
			inputPath = args[0];
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter(OUT_PATH + "IncompleteSimulation.csv"));
		
		File[] files = new File(inputPath).listFiles();
		String filePrefix = null;
		
		for(File file : files){
			
			if(!file.getName().endsWith("_BRANCH_MWTO.csv")) continue;
			
			String prefix = file.getName().split("_")[0];
			
			if(filePrefix==null || !prefix.equals(filePrefix)) {
				filePrefix = prefix;
				System.out.println();
				pw.println();
				System.out.print(prefix + "," + (int)isSimulationIncompleted(file) + ",");
				pw.print(prefix + "," + (int)isSimulationIncompleted(file) + ",");
			}else{
				System.out.print(isSimulationIncompleted(file) + ",");
				pw.print(isSimulationIncompleted(file) + ",");
			}

//			System.out.println("############## " + file.getName() + " ################");
//			for(int i=0; i <=100; i+=25)
//				System.out.println(i+ "% Limit violation: " + countLimitViolation(file, (double)i));
		}
		
		
		
		pw.flush();
		pw.close();
		
	}

	
	private static int isSimulationIncompleted(File file){
		
		BufferedReader br = null;
		int lineCount = 0;
		
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
		
			while((line=br.readLine())!= null)
				lineCount++;
		
			if(lineCount < LINE_COUNT_THRESHOLD)return 1;
			else return 0;
			
		}catch(IOException e){
			e.printStackTrace();
			return 0;
		}finally{
			try{
				br.close();
			}catch(IOException ie){}
		}
	}

	
}
