package classifier_v3;

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
		int counter = 0; 
		boolean classificationMode = true;
		while (evolutionIsImproving) {
			System.out.print("\nAllModules " + allModules.size());
			
			System.out.println(" Metric " + evolvedModules);
			FunctionsEvolver funcEvolver = new FunctionsEvolver();
			int popSize = Math.max(1000, rd.nextInt(MV.POPSIZE));
			int newFunctionMaxDepth = Math.max(3, rd.nextInt(MV.MAX_NEW_FUNC_DEPTH));
			if (classificationMode){
				MV.createBalancedDataSet();
				//MV.createPredictionBasedDataSet(100 + allModules.size());
				//MV.setCurrentData(MV.allData);
			}
			else{
				MV.setCurrentData(MV.allData);
			}
			
			Module newModule = funcEvolver.evolveFunctions(evolvedModules, popSize, newFunctionMaxDepth);
			updateCorrPredictions(newModule);
			
			newModule.classFitness = newModule.predictiveFitness;
			
			MV.setCurrentData(MV.allData);
			newModule.setAsPrimitive();
			newModule.predictiveFitness = calcPredictiveFitness(newModule);
			//if (classificationMode)
			//	newModule.classFitness = newModule.predictiveFitness;
			double inputsBestPredFit = getBestAt(1, evolvedModules);
			double inputsBestClassFit = getBestAt(4, evolvedModules);
			
			if (classificationMode) {
				inputsBestPredFit = 0;
				inputsBestClassFit = 0;
			}
			
			if (classificationMode) {
				if (newModule.classFitness > inputsBestClassFit) {
					allModules.add(newModule);
					counter = 0;
				} else
					counter++;
			} else {
				if (newModule.predictiveFitness > inputsBestPredFit) {
					allModules.add(newModule);
					counter = 0;
				} else
					counter++;
			}
			
			if (counter >20 || allModules.size()==100){
				classificationMode = false;
				counter = 0;
			}
			System.out.println("ClassificationMode " + classificationMode + " Counter " + counter);
			
			if (bestModule == null || newModule.predictiveFitness > bestModule.predictiveFitness)
					//|| (newModule.predictiveFitness == bestModule.predictiveFitness && newModule.length < bestModule.length))
				bestModule = newModule;
			
			
	
			
			double predictiveFitness = calcPredictiveFitness(bestModule);
			System.out.println("Input Module Fit acc " + 0 + " pred " + inputsBestPredFit + " sens " + 0 + " spec " + 0 + " class " + inputsBestClassFit);
			System.out.println("New Module Fit acc " + newModule.accFitness + " pred " + newModule.predictiveFitness + " sens " + newModule.sensitivityFitness + " spec " + newModule.specificityFitness + " class " + newModule.classFitness);
			System.out.println("Best Modul Fit acc " + bestModule.accFitness + " pred " + bestModule.predictiveFitness + " sens " + bestModule.sensitivityFitness + " spec " + bestModule.specificityFitness + " class " + bestModule.classFitness);
			
			
			evolvedModules = selectEvolvedModules(allModules, numInitInputs, bestModule, classificationMode);
			
			if ((counter >11 && classificationMode==false) || allModules.size()==1000){
				//evaluate(allModules);
				System.exit(0);
			}
			
			/*else if (bestModule.predictiveFitness < bestModule.accFitness 
					&& bestModule.predictiveFitness < bestModule.sensitivityFitness
					&& bestModule.predictiveFitness < bestModule.specificityFitness)
				metric = 1;
			else if (bestModule.accFitness < bestModule.predictiveFitness 
					&& bestModule.accFitness < bestModule.sensitivityFitness
					&& bestModule.accFitness < bestModule.specificityFitness)
				metric = 0;
			else if (bestModule.sensitivityFitness < bestModule.accFitness 
					&& bestModule.sensitivityFitness < bestModule.predictiveFitness
					&& bestModule.sensitivityFitness < bestModule.specificityFitness)
				metric = 2;
			else if (bestModule.specificityFitness < bestModule.accFitness 
					&& bestModule.specificityFitness < bestModule.sensitivityFitness
					&& bestModule.specificityFitness < bestModule.predictiveFitness)
				metric = 3;
			else {
				Thread.dumpStack();
				System.exit(0);
			} */
			//if(allModules.size()==10)
				//metric = 2;
				
			if(allModules.size()==1000)
				evolutionIsImproving=false;
			
			continue;
			
			/*if (bestModule.sensitivityFitness < bestModule.specificityFitness) {
			minCorrPositives = bestModule.sensitivityFitness + bestModule.sensitivityFitness * 0.01;
			minCorrNegatives = bestModule.specificityFitness / 2;
			chooseOnPositives = true;
		} else {
			minCorrNegatives = bestModule.specificityFitness + bestModule.specificityFitness * 0.01;
			minCorrPositives = bestModule.sensitivityFitness / 2;
			chooseOnPositives = false;
		}*/
		}
		
		//evaluate(allModules);
		return bestModule;
	}

	private void updateCorrPredictions(Module module) {
		double  output;
		for (int i = 0; i < MV.currentData.size(); i++) {
			output = module.getOutput(i);
			if (output == MV.getCurrentOutput(i))
				MV.currentData.get(i).correctPredictiosNumber++;
		}
	}

	private double getBestAt(int fitType, ArrayList<Module> evolvedModules) {
		double bestFit = -1 * Double.MAX_VALUE;
		for ( int i = 0; i< evolvedModules.size(); i++){
			if (fitType==0 && evolvedModules.get(i).accFitness > bestFit)
				bestFit = evolvedModules.get(i).accFitness;
			else if (fitType==1 && evolvedModules.get(i).predictiveFitness > bestFit)
				bestFit = evolvedModules.get(i).predictiveFitness;
			else if (fitType == 2 && evolvedModules.get(i).sensitivityFitness > bestFit)
				bestFit = evolvedModules.get(i).sensitivityFitness;
			else if (fitType == 3 && evolvedModules.get(i).specificityFitness > bestFit)
				bestFit = evolvedModules.get(i).specificityFitness;
			else if (fitType == 4 && evolvedModules.get(i).classFitness > bestFit)
				bestFit = evolvedModules.get(i).classFitness;
		}

		return bestFit;
	}
	
	private double getMedianAt(int fitType, ArrayList<Module> evolvedModules) {
		double[] fitness = new double[evolvedModules.size()];
		
		for ( int i = 0; i< evolvedModules.size(); i++){
			if (fitType==0 )
				fitness[i] = evolvedModules.get(i).accFitness;
			else if (fitType==1 )
				fitness[i] = evolvedModules.get(i).predictiveFitness;
			else if (fitType==2 )
				fitness[i] = evolvedModules.get(i).sensitivityFitness;
			else if (fitType==3)
				fitness[i] = evolvedModules.get(i).specificityFitness;
			else if (fitType==4)
				fitness[i] = evolvedModules.get(i).classFitness;
		}
		
		Arrays.sort(fitness);
		
		int middle = evolvedModules.size() / 2;
		double capMed;

		if (evolvedModules.size() % 2 == 1) {
			capMed = fitness[middle];
		} else {
			capMed = (fitness[middle - 1] + fitness[middle]) / 2;
		}
		
		return capMed;

	}
	
	private double getWorstAt(int fitType, ArrayList<Module> evolvedModules) {
		double[] fitness = new double[evolvedModules.size()];
		
		for ( int i = 0; i< evolvedModules.size(); i++){
			if (fitType==0 )
				fitness[i] = evolvedModules.get(i).accFitness;
			else if (fitType==1 )
				fitness[i] = evolvedModules.get(i).predictiveFitness;
			else if (fitType==2 )
				fitness[i] = evolvedModules.get(i).sensitivityFitness;
			else if (fitType==3)
				fitness[i] = evolvedModules.get(i).specificityFitness;
			else if (fitType==4)
				fitness[i] = evolvedModules.get(i).classFitness;
		}
		
		Arrays.sort(fitness);
		
		
		return fitness[0];

	}

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
	
	private double calcPredictiveFitness(Module module) {
		double fitness = 0, output;
		MV.setCurrentData(MV.allData);
		for (int i = 0; i < MV.currentData.size(); i++) {
			output = module.getOutput(i);
			if (output == MV.getCurrentOutput(i))
				fitness++;
		}
		return (double) fitness / MV.numOfTestCases_curr;
	}

	private ArrayList<Module> selectEvolvedModules(ArrayList<Module> allModules, int numInitInputs, Module bestModule, boolean classificationMode) {
		int remainingModulesLength;
		remainingModulesLength = allModules.size();
		int evolvedModulesLength;
		if (classificationMode)
		evolvedModulesLength = 1 + rd.nextInt(remainingModulesLength);
		else
			evolvedModulesLength = numInitInputs;
		evolvedModulesLength = Math.min(allModules.size(), 10);
		//int evolvedModulesLength = Math.min(10 , 1 + rd.nextInt(remainingModulesLength));
		//int evolvedModulesLength = Math.min(remainingModulesLength, 1 + rd.nextInt(numInitInputs+ Math.max(0, (int)Math.log(remainingModulesLength))));
		//if (evolvedModulesLength == 1)
			//evolvedModulesLength = 2;
		ArrayList<Module> remainingModules = new ArrayList<Module>();
		for (int i = 0; i < allModules.size(); i++) {
			remainingModules.add(allModules.get(i));
		}
		ArrayList<Module> evolvedModules = new ArrayList<Module>();
		int k = 0;
		int takePrimitive = rd.nextInt(remainingModulesLength);
		//System.out.println(takePrimitive);
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
		
		/*if (takePrimitive == 0) {
			int randomIndex = rd.nextInt(numRemainingPrimtives--);
			evolvedModules.add(remainingModules.get(randomIndex));
			remainingModules.remove(randomIndex);
			remainingModulesLength--;
			k++;
		}*/

		int index, tournmentSize;
		for (int i = k; i < evolvedModulesLength; i++) {
			//System.out.println(i + " " + remainingModulesLength + " " + remainingModules.size() + " " + evolvedModulesLength);
			if (remainingModulesLength > 1){
				tournmentSize = 1 + rd.nextInt(remainingModulesLength);
			//if (tournmentSize == 1)
			
				//tournmentSize = 2;
			
				index = modulesTournment(remainingModules,  tournmentSize, bestModule, classificationMode);
			} else
				index = 0;
			evolvedModules.add(remainingModules.get(index));
			remainingModules.remove(index);
			remainingModulesLength--;
			//System.out.println( "selectEvolvedModules " +i);
		}
		return evolvedModules;
	}



	private int modulesTournment(ArrayList<Module> Modules,  int tournmentSize, Module bestModule, boolean classificationMode) {
		
		int metric = 1;
		if (classificationMode)
			metric = 4;
		metric=4;
		//else 
		//	return rd.nextInt(Modules.size());
		int bestIndex = rd.nextInt(Modules.size());;
		double bFit= -1 * Double.MAX_VALUE;
		
		/*if (bestModule!=null){
		double random = rd.nextDouble();
		double totalProb = bestModule.accFitness + bestModule.predictiveFitness + bestModule.sensitivityFitness +
				bestModule.specificityFitness;
		if ( random < bestModule.accFitness / totalProb) {
			metric = 1;
		} else if (random < (bestModule.predictiveFitness / totalProb) + (bestModule.accFitness / totalProb)) {
			metric = 0;
		}  else if (random < (bestModule.predictiveFitness / totalProb) + (bestModule.accFitness / totalProb)
				+ (bestModule.sensitivityFitness / totalProb)) {
			metric = 2;
		} else if (random < (bestModule.predictiveFitness / totalProb) + (bestModule.accFitness / totalProb)
				+ (bestModule.sensitivityFitness / totalProb) + (bestModule.specificityFitness / totalProb)) {
			metric = 3;
		} else if (totalProb < 0) {
			metric = 0;
		} else {
			Thread.dumpStack();
			System.exit(0);
		}
		}*/
		
		if (metric==0)
			bFit= Modules.get(bestIndex).accFitness;
		else if (metric==1)
			bFit= Modules.get(bestIndex).predictiveFitness;
		else if (metric==2)
			bFit= Modules.get(bestIndex).sensitivityFitness;
		else if (metric==3)
			bFit= Modules.get(bestIndex).specificityFitness;
		else if (metric==3)
			bFit= Modules.get(bestIndex).classFitness;
	
		
		boolean[] checked = new boolean[Modules.size()];
		int index;
		for (int i = 1; i < tournmentSize; i++) {
			//System.out.println( "modulesTournment 1 " +i);
			index =rd.nextInt(Modules.size());
			while (checked[index])
				index = rd.nextInt(Modules.size());
			//System.out.println( "modulesTournment 2 " +i);
			checked[index] = true;
			if (metric ==0 && (Modules.get(index).accFitness > bFit || (Modules.get(index).accFitness == bFit)
					&& Modules.get(index).length < Modules.get(bestIndex).length)) {
				bestIndex=index;
				bFit = Modules.get(index).accFitness;
			} else if (metric ==1 && (Modules.get(index).predictiveFitness > bFit || (Modules.get(index).predictiveFitness == bFit)
					&& Modules.get(index).length < Modules.get(bestIndex).length)) {
				bestIndex=index;
				bFit = Modules.get(index).predictiveFitness;
			} else if (metric ==2 && (Modules.get(index).sensitivityFitness > bFit || (Modules.get(index).sensitivityFitness == bFit)
					&& Modules.get(index).length < Modules.get(bestIndex).length)) {
				bestIndex=index;
				bFit = Modules.get(index).sensitivityFitness;
			} else if (metric==3 && (Modules.get(index).specificityFitness > bFit || (Modules.get(index).specificityFitness == bFit)
					&& Modules.get(index).length < Modules.get(bestIndex).length)) {
				bestIndex=index;
				bFit = Modules.get(index).specificityFitness;
			}  else if (metric==4 && (Modules.get(index).classFitness > bFit || (Modules.get(index).classFitness == bFit)
					&& Modules.get(index).length < Modules.get(bestIndex).length)) {
				bestIndex=index;
				bFit = Modules.get(index).classFitness;
			}
		}
		
		//return bestIndex;
		
		if (classificationMode)
			return rd.nextInt(Modules.size());
		else
			return bestIndex;
			
	}

	

	
}
