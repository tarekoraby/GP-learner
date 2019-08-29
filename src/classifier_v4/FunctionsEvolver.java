package classifier_v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;



public class FunctionsEvolver {
	Random rd = new Random();
	FunctionCreator FC = new FunctionCreator();

	public Module evolveFunctions(ArrayList<Module> inputModules, int popSize,  int newFunctionMaxDepth) {
		System.out.println("Function Evolution started. No modules " + inputModules.size());
		Module[] modules = createNewPopulation(inputModules, popSize, newFunctionMaxDepth);
		calcFitness(modules);
		Module bestModule = modules[rd.nextInt(modules.length)];
		double prevMedianFit = -1 * Double.MAX_VALUE;
		double prevBestFit = -1 * Double.MAX_VALUE;
		boolean funcEvolutionImproving = true;
		int prevBestCounter=0;
		while (funcEvolutionImproving) {
			Module newModule;
			int[] newFunction = null;
			for (int counter = 0; counter < popSize; counter++) {
				int parentIndex = -1, offspringIndex = -1;
				parentIndex = tournament(modules, MV.TSIZE);
				int[] parentFunction = modules[parentIndex].getFunction();
				double random = rd.nextInt(2);
				//random=-1;
				//newFunction = FC.create_random_func(newFunctionMaxDepth, inputModules.size(),
					//	MV.BOOLEAN_OUTPUT, false);
				if (random == 0)
					newFunction = subtreeMutation(parentFunction, inputModules.size(), newFunctionMaxDepth);
				else if (random == 1)
					newFunction = pointMutation(parentFunction, inputModules.size());
				else if (random == 2){
					int parentIndex2 = tournament(modules,  MV.TSIZE);
					while (parentIndex == parentIndex2){
						parentIndex2 = tournament(modules, MV.TSIZE);
					}
					int[] parentFunction2 = modules[parentIndex2].getFunction();
					newFunction = combine(parentFunction, parentFunction2, inputModules.size());
				}
				newModule = new Module(inputModules, newFunction);
				offspringIndex = negative_tournament(modules,  MV.TSIZE);
				calcFitness(newModule);
				modules[offspringIndex] = newModule;
				// System.out.println(newModule.fitness);
				if ((newModule.predictiveFitness > bestModule.predictiveFitness || (newModule.predictiveFitness == bestModule.predictiveFitness && newModule.length < bestModule.length))) {
					bestModule = newModule;
				} 
				
				
			}
			
			double currMedianFit = getMedianFit(modules);

			if (isImproving(bestModule, prevBestFit))
				prevBestCounter = 0;
			else 
				prevBestCounter++;
			//funcEvolutionImproving = isImproving(bestModule, prevBestFit);
			if (currMedianFit <= prevMedianFit)
				funcEvolutionImproving = false;
			System.out.println("Class fit, med " + currMedianFit + " best " + bestModule.predictiveFitness);
		
			prevMedianFit = currMedianFit;
			prevBestFit = bestModule.predictiveFitness;
			
			if (prevBestCounter >0)
				funcEvolutionImproving = false;
			
			funcEvolutionImproving = false;
			
		}

		return bestModule;
	}

	private double getMedianFit(Module[] modules) {
		double[] fitness = new double[modules.length];

		for (int i = 0; i < modules.length; i++)
			fitness[i] = modules[i].predictiveFitness;

		Arrays.sort(fitness);

		int middle = modules.length / 2;
		double capMed;

		if (modules.length % 2 == 1) {
			capMed = fitness[middle];
		} else {
			capMed = (fitness[middle - 1] + fitness[middle]) / 2;
		}

		return capMed;
	}

	private int[] combine(int[] lBuffer, int[] rBuffer, int size) {
		FunctionCreator FC = new FunctionCreator();
		int[] output;
		int[] leftBuffer = lBuffer.clone();
		int[] rightBuffer = rBuffer.clone();
		if (MV.BOOLEAN_OUTPUT){
			int[] condition = FC.grow(2, size, true, false);
			output = new int[condition.length + leftBuffer.length + rightBuffer.length + 1];
			output[0] = MV.IF_THEN_ELSE_BOOLEAN;
			System.arraycopy(condition, 0, output, 1, condition.length);
			System.arraycopy(leftBuffer, 0, output, (1 + condition.length), leftBuffer.length);
			System.arraycopy(rightBuffer, 0, output, (1 + condition.length + leftBuffer.length), rightBuffer.length);
		} else{
			int[] condition = FC.grow(2 , size, true, false);
			output = new int[condition.length + leftBuffer.length + rightBuffer.length + 1];
			output[0] = MV.IF_THEN_ELSE_DOUBLE;
			System.arraycopy(condition, 0, output, 1, condition.length);
			System.arraycopy(leftBuffer, 0, output, (1 + condition.length), leftBuffer.length);
			System.arraycopy(rightBuffer, 0, output, (1 + condition.length + leftBuffer.length), rightBuffer.length);
		}
		return output;
	}

	private boolean isImproving(Module bestModule,  double prevBestFit) {
		if (bestModule.predictiveFitness > prevBestFit) {
			return true;
		} else
			return false;
	}

	private void calcFitness(Module[] modules) {
		for (int i = 0; i < modules.length; i++) {
			calcFitness(modules[i]);
		}
	}

	private void calcFitness(Module module) {
		double fitness = 0, output;

		for (int i = 0; i < MV.currentData.size(); i++) {
			output = module.getOutput(i);
			if (output == MV.getCurrentOutput(i))
				fitness++;
		}
		module.predictiveFitness = fitness / MV.numOfTestCases_curr;
		
		
		/*double predFitness = 0, output;
		int numOfCorrectPositives = 0, numOfCorrectNegatives = 0;
		for (int i = 0; i < MV.numOfTestCases; i++) {
			MV.currentCaseIndex = i;
			output = module.getOutput();
			//predFitness -= Math.abs(output - MV.getCurrentPrimitiveOutput());
			if (output == MV.getCurrentPrimitiveOutput()) {
				if (output==1)
					numOfCorrectPositives++;
				else
					numOfCorrectNegatives++;
			}
		}
		module.sensitivityFitness = (double) numOfCorrectPositives / (double)MV.numTruePositives;
		module.specificityFitness = (double) numOfCorrectNegatives / (double)MV.numTrueNegatives;
		module.predictiveFitness = (double) (numOfCorrectPositives + numOfCorrectNegatives) / MV.numOfTestCases;
		module.accFitness = (double)(module.sensitivityFitness + module.specificityFitness ) / 2;*/
		//System.out.println(MV.numTruePositives + " " + MV.numTrueNegatives + " " + numOfCorrectPositives  + " " + numOfCorrectNegatives);
		//System.out.println(module.fitness + " " + module.sensitivityFitness + " " + module.specificityFitness + " " + (double)((module.sensitivityFitness + module.specificityFitness) / 2));

		/*
		if (module.sensitivityFitness < minCorrPositives || module.specificityFitness < minCorrNegatives
				|| module.accFitness < minBestFit || module.predictiveFitness < minBesPredtFit ) {
			module.accFitness = -1 * Double.MAX_VALUE;
			module.predictiveFitness = -1 * Double.MAX_VALUE;
			module.sensitivityFitness = -1 * Double.MAX_VALUE;
			module.specificityFitness = -1 * Double.MAX_VALUE;
		}
		
		*/

	}

	int tournament(Module[] modules, int tsize) {
		int best = rd.nextInt(modules.length), i, competitor;
		double fbest = -1 * Double.MAX_VALUE;	

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(modules.length);
			if (modules[competitor].predictiveFitness > fbest || (modules[competitor].predictiveFitness == fbest)
							&& modules[competitor].length < modules[best].length){
				fbest = modules[competitor].predictiveFitness;
				best = competitor;
			}
		}
		
		return (best);
	}

	int negative_tournament(Module[] modules, int tsize) {
		int worst = rd.nextInt(modules.length), i, competitor;
		double fworst = Double.MAX_VALUE;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(modules.length);
			if (modules[competitor].predictiveFitness < fworst || (modules[competitor].predictiveFitness == fworst)
					&& modules[competitor].length < modules[worst].length)
				fworst = modules[competitor].predictiveFitness;
			worst = competitor;

		}

		return (worst);
	}

	private Module[] createNewPopulation(ArrayList<Module> inputModules, int popsize, int newFunctionMaxDepth) {
		int inputSize = inputModules.size();
		Module[] newModules = new Module[popsize];
		for (int i = 0; i < popsize; i++) {
			Module newModule = new Module(inputModules, FC.create_random_func(newFunctionMaxDepth, inputSize,
					MV.BOOLEAN_OUTPUT, false));
			newModules[i] = newModule;
		}
		return newModules;
	}
	
	int[] pointMutation(int[] parent, int numOfInputs) {
		int mutsite = rd.nextInt(parent.length);
		int[] parentcopy = parent.clone();

		if (parentcopy[mutsite] >= MV.TSET_1_START && parentcopy[mutsite] <= MV.TSET_1_END) {
			parentcopy[mutsite] = MV.TSET_1_START + rd.nextInt(MV.TSET_1_START - MV.TSET_1_END);
		} else if (parentcopy[mutsite] >= MV.FSET_1_START && parentcopy[mutsite] <= MV.FSET_1_END) {
			parentcopy[mutsite] = MV.FSET_1_START + rd.nextInt(MV.FSET_1_START - MV.FSET_1_END);
		} else if (parentcopy[mutsite] >= MV.FSET_2_START && parentcopy[mutsite] <= MV.FSET_2_END) {
			parentcopy[mutsite] = MV.FSET_2_START + rd.nextInt(MV.FSET_2_START - MV.FSET_2_END);
		} else if (parentcopy[mutsite] >= MV.FSET_3_START && parentcopy[mutsite] <= MV.FSET_3_END) {
			parentcopy[mutsite] = MV.FSET_3_START + rd.nextInt(MV.FSET_3_START - MV.FSET_3_END);
		} else if (parentcopy[mutsite] >= MV.FSET_4_START && parentcopy[mutsite] <= MV.FSET_4_END) {
			parentcopy[mutsite] = MV.FSET_4_START + rd.nextInt(MV.FSET_4_START - MV.FSET_4_END);
		//} else if (parentcopy[mutsite] >= MV.FSET_5_START && parentcopy[mutsite] <= MV.FSET_5_END) {
			//parentcopy[mutsite] = MV.FSET_5_START + rd.nextInt(MV.FSET_5_START - MV.FSET_5_END);
		//} else if (parentcopy[mutsite] >= MV.FSET_6_START && parentcopy[mutsite] <= MV.FSET_6_END) {
			//parentcopy[mutsite] = MV.FSET_6_START + rd.nextInt(MV.FSET_6_START - MV.FSET_6_END);
		} else if (parentcopy[mutsite] >= MV.FSET_7_START && parentcopy[mutsite] <= MV.FSET_7_END) {
			parentcopy[mutsite] = MV.FSET_7_START + rd.nextInt(MV.FSET_7_START - MV.FSET_7_END);
		} else if (parentcopy[mutsite] >= MV.FSET_8_START && parentcopy[mutsite] <= MV.FSET_8_END) {
			parentcopy[mutsite] = MV.FSET_8_START + rd.nextInt(MV.FSET_8_START - MV.FSET_8_END);
		} else if (parentcopy[mutsite] >= MV.FSET_10_START && parentcopy[mutsite] <= MV.FSET_10_END) {
			parentcopy[mutsite] = MV.FSET_10_START + rd.nextInt(MV.FSET_10_START - MV.FSET_10_END);
		}

		return (parentcopy);
	}

	int[] subtreeMutation(int[] parent, int numOfInputs, int newFunctionMaxDepth) {
		int mutStart, mutEnd, parentLen = parent.length, subtreeLen, lenOff;
		int[] newSubtree, offspring;

		// Calculate the mutation starting point.

		mutStart = rd.nextInt(parentLen);
		mutEnd = traverse(parent, mutStart);

		if ( (parent[mutStart] <= MV.FSET_4_START && parent[mutStart] >= MV.FSET_6_END) || parent[mutStart] == MV.IF_THEN_ELSE_ARRAY)
			newSubtree = FC.create_random_func(newFunctionMaxDepth, numOfInputs, false, true);
		else if ((parent[mutStart] <= MV.FSET_7_START && parent[mutStart] >= MV.FSET_8_END) || parent[mutStart] == MV.IF_THEN_ELSE_BOOLEAN)
			newSubtree = FC.create_random_func(newFunctionMaxDepth, numOfInputs, true, false);
		else
			newSubtree = FC.create_random_func(newFunctionMaxDepth, numOfInputs, false, false);

		if (mutStart == 0)
			return newSubtree;
		else {
			lenOff = mutStart + newSubtree.length + (parentLen - mutEnd) - 1;

			offspring = new int[lenOff];

			System.arraycopy(parent, 0, offspring, 0, mutStart);
			System.arraycopy(newSubtree, 0, offspring, mutStart, newSubtree.length);
			System.arraycopy(parent, mutEnd + 1, offspring, (mutStart + newSubtree.length), (parentLen - mutEnd) - 1);

			return (offspring);
		}

	}

	static int traverse(int[] buffer, int buffercount) {

		if (buffer[buffercount] > MV.FSET_1_START
				|| (buffer[buffercount] <= MV.FSET_10_START && buffer[buffercount] >= MV.FSET_10_END))
			return (buffercount);
		else if ((buffer[buffercount] <= MV.FSET_1_START && buffer[buffercount] >= MV.FSET_1_END)
				|| ((buffer[buffercount] <= MV.FSET_3_START && buffer[buffercount] >= MV.FSET_3_END)))
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		else if (buffer[buffercount] <= MV.FSET_6_START && buffer[buffercount] >= MV.FSET_6_END)
			return (traverse(buffer, 1 + traverse(buffer, 1 + traverse(buffer, ++buffercount))));
		else if (buffer[buffercount] <= MV.FSET_9_START && buffer[buffercount] >= MV.FSET_9_END)
			return (traverse(buffer, 1 + traverse(buffer, 1 + traverse(buffer, ++buffercount))));
		else
			return (traverse(buffer, 1 + traverse(buffer, ++buffercount)));

	}

}
