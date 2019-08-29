package regressor_v2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

import org.openml.apiconnector.io.OpenmlConnector;

public class Evolver {

	Random rd = new Random();	



	Module evolve(ArrayList<Module> allModules) {
		System.out.println("Evolved started");
		int numInitInputs = allModules.size();
		Module bestModule = null;
		boolean evolutionIsImproving = true;
		ArrayList<Module> evolvedModules = allModules;
		
		for (int i=0; i< 10; i++){
			int size = allModules.size();
			int inputsSize = 1 + rd.nextInt(size - 1);
			int[] inputIndexes=new int[inputsSize];
			boolean[] taken = new boolean[size];
			int outputIndex = rd.nextInt(size);
			taken[outputIndex]=true;
			System.out.println("Num inputs " + inputsSize);
			for (int j= 0; j<inputsSize; j++){
				
				int inputIndex = rd.nextInt(allModules.size());
				while (taken[inputIndex])
					inputIndex = rd.nextInt(allModules.size());
				System.out.print("" + inputIndex + " ");
				taken[inputIndex] = true;
				inputIndexes[j]= inputIndex;
			}
			System.out.print(" output " + outputIndex + " ");
			
			ArrayList<DataEntry> dataEntries = MV.createDataEntries(inputIndexes, outputIndex);
			MV.setCurrentData(dataEntries);
			
			FunctionsEvolver funcEvolver = new FunctionsEvolver();
			
			int popSize = Math.max(1000, rd.nextInt(MV.POPSIZE));
			int newFunctionMaxDepth = Math.max(3, rd.nextInt(MV.MAX_NEW_FUNC_DEPTH));
			
			Module newModule = funcEvolver.evolveFunctions(evolvedModules, popSize, newFunctionMaxDepth);

			newModule.setAsPrimitive();
			allModules.add(newModule);

			double inputsBestFit = getBestFit(evolvedModules);

			
			
			
	
			System.out.println("Input Module Fit " + inputsBestFit );
			System.out.println("New Module Fit acc " + newModule.fitness);
			
		}
		
		evolvedModules = allModules;
		
		MV.setCurrentData(MV.allData);
		
		while (evolutionIsImproving) {
			System.out.print("\nAllModules " + allModules.size() + " ");

			FunctionsEvolver funcEvolver = new FunctionsEvolver();
			int popSize = Math.max(100000, rd.nextInt(MV.POPSIZE));
			int newFunctionMaxDepth = Math.max(3, rd.nextInt(MV.MAX_NEW_FUNC_DEPTH));
			
			Module newModule = funcEvolver.evolveFunctions(evolvedModules, popSize, newFunctionMaxDepth);

			newModule.setAsPrimitive();
			allModules.add(newModule);

			double inputsBestFit = getBestFit(evolvedModules);

			
			if (bestModule == null || newModule.fitness > bestModule.fitness)
					//|| (newModule.predictiveFitness == bestModule.predictiveFitness && newModule.length < bestModule.length))
				bestModule = newModule;
			
			
	
			System.out.println("Input Module Fit " + inputsBestFit );
			System.out.println("New Module Fit acc " + newModule.fitness);
			System.out.println("Best Modul Fit acc " + bestModule.fitness );
			
			
			
			evolvedModules = selectEvolvedModules(allModules, numInitInputs);
			
			
		}
		
		return bestModule;
	}

	

	private double getBestFit(ArrayList<Module> modules) {
		double bestFit = -1 * Double.MAX_VALUE;
		for ( int i = 0; i< modules.size(); i++){
			if (modules.get(i).fitness > bestFit)
				bestFit = modules.get(i).fitness;
			
		}

		return bestFit;
	}
	
	private double getMedianFit(ArrayList<Module> modules) {
		double[] fitness = new double[modules.size()];

		for (int i = 0; i < modules.size(); i++) {

			fitness[i] = modules.get(i).fitness;

		}

		Arrays.sort(fitness);

		int middle = modules.size() / 2;
		double capMed;

		if (modules.size() % 2 == 1) {
			capMed = fitness[middle];
		} else {
			capMed = (fitness[middle - 1] + fitness[middle]) / 2;
		}

		return capMed;

	}
	
	private double getWorstFit(ArrayList<Module> modules) {
		double[] fitness = new double[modules.size()];

		for (int i = 0; i < modules.size(); i++) {

			fitness[i] = modules.get(i).fitness;

		}

		Arrays.sort(fitness);

		return fitness[0];

	}
	
/*
	private void evaluate(ArrayList<Module> allModules) {
		MV.setCurrentData(MV.allData);
		int bestPredictiveModule  = -1;
		double bFit = -1 * Double.MAX_VALUE;;
		for (int i =MV.numOfInitialInputs ; i<allModules.size(); i++ ){
			allModules.get(i).setAsNative();
			
			if (allModules.get(i).predictiveFitness > bFit){
				bFit = allModules.get(i).predictiveFitness;
				bestPredictiveModule  = i ;
			}
		}
		
		Module bestModule = allModules.get(bestPredictiveModule);
		
			String line;
			BufferedReader in;
			try {
	
				in = new BufferedReader(new FileReader("C:\\Users\\taor9299\\Box Sync\\Java workspace\\GP_X\\evaluation_dataset.csv"));
				int numbuerOfCases = countLines("C:\\Users\\taor9299\\Box Sync\\Java workspace\\GP_X\\evaluation_dataset.csv") - 1;
				MV.numOfTestCases_all = numbuerOfCases;
				line = in.readLine();
				StringTokenizer tokens = new StringTokenizer(line, ",");
				int numberOfInputs = tokens.countTokens() - 1;
				//System.out.println(numberOfInputs + " " + numbuerOfCases);
				MV.numOfInitialInputs = numberOfInputs;
				MV.allData = new ArrayList<DataEntry>();
			

				for (int j = 0; j < numbuerOfCases; j++) {
					double output;
					ArrayList<Double> inputs = new ArrayList<Double>();
					
					line = in.readLine();
					tokens = new StringTokenizer(line, ",");
					for (int k = 0; k < numberOfInputs; k++) {
						inputs.add(Double.parseDouble(tokens.nextToken().trim()));
						//System.out.print(MV.rawInputs[j][k] + " " );
					}
					output  = Double.parseDouble(tokens.nextToken().trim());
					
					DataEntry DE = new DataEntry(inputs, output);
					MV.allData.add(DE);
					//System.out.print(MV.rawOutputs[j] + " " );
					//System.out.println();
				}
				
				in.close();


			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			MV.initialize();
		
	
			double output;
			for (int i = 0; i < MV.currentData.size(); i++) {
				output = bestModule.getOutput(i);
				System.out.println(MV.getCurrentInput(i, 0) + " " + MV.getCurrentInput(i, 1) + " " + output);
			}
			
		
	}
	*/
	
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}
	
	

	private ArrayList<Module> selectEvolvedModules(ArrayList<Module> allModules, int numInitInputs) {
		int remainingModulesLength;
		remainingModulesLength = allModules.size();
		int evolvedModulesLength;
		evolvedModulesLength = Math.min(3+ rd.nextInt(allModules.size()), numInitInputs);

		ArrayList<Module> remainingModules = new ArrayList<Module>();
		for (int i = 0; i < allModules.size(); i++) {
			remainingModules.add(allModules.get(i));
		}
		
		ArrayList<Module> evolvedModules = new ArrayList<Module>();
		int k = 0;
		int takePrimitive = rd.nextInt(remainingModulesLength);

		int numRemainingPrimtives = numInitInputs;
		while (takePrimitive < numInitInputs && k < evolvedModulesLength && numRemainingPrimtives > 0) {
			int randomIndex = rd.nextInt(numRemainingPrimtives--);
			evolvedModules.add(remainingModules.get(randomIndex));
			remainingModules.remove(randomIndex);
			remainingModulesLength--;
			if (remainingModulesLength > 0)
				takePrimitive = rd.nextInt(remainingModulesLength);
			k++;
			//System.out.println("here " + takePrimitive + " " + remainingModulesLength + " " + numRemainingPrimtives);
		}

		int index;
		for (int i = k; i < evolvedModulesLength; i++) {

		
				index = rd.nextInt(remainingModulesLength);

			evolvedModules.add(remainingModules.get(index));
			remainingModules.remove(index);
			remainingModulesLength--;
			//System.out.println( "selectEvolvedModules " +i);
		}
		return evolvedModules;
	}



	

	

	
}
