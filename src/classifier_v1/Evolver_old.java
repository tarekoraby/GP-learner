package classifier_v1;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Evolver_old {

	static Random rd = new Random();
	static FunctionCreator FC = new FunctionCreator();
	double bestFit, bestFitSubmut, bestFitSubsum, bestFitCliqueborrow;
	double prevWorstFit;
	int consecutiveWorst;
	Module[] currentModules, newModules;
	static int prevDeme = -1, prevLevel = -1;
	static WriteFile fileWriter;

	public Evolver(ArrayList<Module> primitiveModules) {
		// TODO Auto-generated constructor stub
	}

	void initializeEvolver() {
		MV.SUB_MUT_PROB = (double) 1 / 3;
		MV.CLIQUE_BORROW_PROB = (double) 1 / 3;
		MV.SUBSUME_PROB = (double) 1 - MV.SUB_MUT_PROB - MV.CLIQUE_BORROW_PROB;
		bestFitSubmut = -1.0e34;
		bestFitSubsum = -1.0e34;
		bestFitCliqueborrow = -1.0e34;

		MV.currentRound = 0;

		currentModules = MV.masterModules[MV.currentLevel][MV.currentDeme];
		newModules = new Module[currentModules.length];

		prevWorstFit = -1 * Double.MAX_VALUE;

		consecutiveWorst = 0;

	}

	Module evolve() {

		initializeEvolver();

	

		

		do {
			MV.generateRandomInutArrays(MV.FIT_SAMPLE_SIZE);
			calculateFitness(currentModules);
			currentModules = sortbyFitness(currentModules);
			
			newModules= evolveModules();
			calculateFitness(newModules);
			newModules = sortbyFitness(newModules);
			updateCurrentModules();
			printStats();
			MV.currentRound++;
		} while (evolutionIsImproving());

		currentModules = Arrays.copyOfRange(currentModules, currentModules.length - 10, currentModules.length);
		for (int i = 0; i < currentModules.length; i++) {
			currentModules[i].fitness = 0;
		}

		printBestToFile();

		MV.masterModules[MV.currentLevel][MV.currentDeme] = currentModules;
	}

	private void updateCurrentModules() {
		Module[] brandNewModules = new Module[currentModules.length];
		int maxCurrIndex = currentModules.length - 1;
		int maxNewIndex = newModules.length - 1;

		for (int i = currentModules.length - 1; i >= 0; i--) {
			if (currentModules[maxCurrIndex].fitness >= newModules[maxNewIndex].fitness) {
				brandNewModules[i] = currentModules[maxCurrIndex];
				maxCurrIndex--;
			} else {
				brandNewModules[i] = newModules[maxNewIndex];
				maxNewIndex--;
			}
		}
		currentModules = brandNewModules;
	}

	private Module[] sortbyFitness(Module[] moduleArray) {
		Module[] sortedModules = new Module[moduleArray.length];
		double[] fitnessArray = new double[moduleArray.length];
		boolean[] taken = new boolean[moduleArray.length];
		for (int i = 0; i < moduleArray.length; i++) {
			fitnessArray[i] = moduleArray[i].fitness;
		}

		Arrays.sort(fitnessArray);
		for (int i = 0; i < moduleArray.length; i++) {
			for (int k = 0; k < moduleArray.length; k++) {
				if (taken[k] == false && fitnessArray[i] == moduleArray[k].fitness) {
					sortedModules[i] = moduleArray[k];
					taken[k] = true;
					break;
				}
			}
		}

		return sortedModules;
	}

	private void printBestToFile() {

		try {

			String outputString = "";// = MV.currentLevel + " " + MV.currentDeme
										// + " " + MV.currentLevel + "\n";
			fileWriter = new WriteFile("output.txt", false);
			double[] output = new double[10];
			for (int i = 0; i < 10000; i++) {
				MV.retreiveRandommInput();
				for (int k = 0; k < output.length; k++)
					output[k] = currentModules[currentModules.length - 1 - k].getOutput();

				outputString += Double.toString(mode(output)) + " " + MV.currentWin + " \n";
			}

			fileWriter.writeToFile(outputString + " \n");

		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Please provide a setup and data files");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("ERROR: Incorrect data format");
			System.exit(0);
		}

	}

	public static double mode(double a[]) {
		double maxValue = -99;
		int maxCount = 0;

		for (int i = 0; i < a.length; ++i) {
			int count = 0;
			for (int j = 0; j < a.length; ++j) {
				if (a[j] == a[i])
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				maxValue = a[i];
			}
		}

		return maxValue;
	}

	private void printStats() {
		if (MV.currentDeme != prevDeme) {
			prevDeme = MV.currentDeme;
			System.out.println("***************************************");
			System.out.println("***************************************");
			System.out.println("***************************************");
			System.out.println("Working on deme " + MV.currentDeme);
		}
		if (MV.currentLevel != prevLevel) {
			prevLevel = MV.currentLevel;
			System.out.println("***************************************");
			System.out.println("Working on level " + MV.currentLevel);
		}

		System.out.println("\nDeme " + MV.currentDeme + " level " + MV.currentLevel + " gen " + MV.currentRound);
		System.out.println("Best fit " + currentModules[currentModules.length - 1].fitness + " median fit "
				+ currentModules[currentModules.length / 2].fitness);
		System.out.println("Worst fit " + currentModules[0].fitness);

	}

	Module createRandomModule() {
		int index, rand1, rand2;

		int moduleSize = 1 + rd.nextInt(MV.MAX_SUB_MODULES);
		Module[] subModules = new Module[moduleSize];
		// takes at least one input
		index = MV.numOfConstants + rd.nextInt(MV.numOfInputs);
		subModules[0] = MV.primitiveModules[index].deepCopy();
		for (int j = 1; j < moduleSize; j++) {
			rand1 = rd.nextInt(2);
			if (rand1 == 0 || MV.currentLevel == 0) {
				rand1 = rd.nextInt(2);
				if (rand1 == 0)
					index = MV.numOfConstants + rd.nextInt(MV.numOfInputs);
				else
					index = rd.nextInt(MV.numOfConstants);
				subModules[j] = MV.primitiveModules[index].deepCopy();
			} else {
				rand1 = rd.nextInt(MV.currentLevel);
				rand2 = rd.nextInt(MV.DEMES);
				index = rd.nextInt(MV.masterModules[rand1][rand2].length);
				subModules[j] = MV.masterModules[rand1][rand2][index].deepCopy();
			}
		}
		Module newModule = new Module(subModules, FC.create_random_func(MV.MAX_NEW_FUNC_DEPTH, moduleSize,
				MV.BOOLEAN_OUTPUT, false));

		return newModule;
	}

	private Module[] evolveModules() {
		Module[] newModules = new Module[currentModules.length];
		int index;
		for (int i = 0; i < currentModules.length; i++) {
			double random = rd.nextDouble();
			if (random < MV.CLIQUE_BORROW_PROB) {
				newModules[i] = cliqueBorrow();
			} else if (random < MV.CLIQUE_BORROW_PROB + MV.SUBSUME_PROB) {
				newModules[i] = subsume();
			} else if (random < MV.CLIQUE_BORROW_PROB + MV.SUBSUME_PROB + MV.SUB_MUT_PROB) {
				newModules[i] = subtree_mut();
			} else {
				System.out.println("Evolution Error - evolveModules method!!!");
				System.out.println(MV.CLIQUE_BORROW_PROB + " " + MV.SUBSUME_PROB + " " + MV.SUB_MUT_PROB);
				System.exit(0);
			}
		}
		return newModules;
	}

	private void calculateFitness(Module[] moduleArray) {
		// reset fitness
		for (int i = 0; i < moduleArray.length; i++) {
			moduleArray[i].fitness = 0;
		}

		double output;
		for (int k = 0; k < MV.FIT_SAMPLE_SIZE; k++) {
			MV.setInput(k);
			for (int i = 0; i < moduleArray.length; i++) {
				output = moduleArray[i].getOutput();
				if (output == 0 && MV.currentWin < 0) {
					moduleArray[i].fitness++;
				} else if (output == 1 && MV.currentWin > 0) {
					moduleArray[i].fitness++;
				}
			}
		}
	}

	private void calculateFitness(Module module) {
		// reset fitness
		module.fitness = 0;

		double output;
		for (int k = 0; k < MV.FIT_SAMPLE_SIZE; k++) {
			MV.setInput(k);
			output = module.getOutput();
			if (output == 0 && MV.currentWin < 0) {
				module.fitness++;
			} else if (output == 1 && MV.currentWin > 0) {
				module.fitness++;
			}
		}
	}

	private Module cliqueBorrow() {
		int index;
		int size = 1 + rd.nextInt(MV.MAX_SUB_MODULES);

		Module[] subModules = new Module[size];
		index = MV.numOfConstants + rd.nextInt(MV.numOfInputs);
		subModules[0] = MV.primitiveModules[index].deepCopy();
		for (int j = 1; j < size; j++) {
			index = rd.nextInt(currentModules.length);
			int rand = rd.nextInt(currentModules[index].inputs.length);
			subModules[j] = currentModules[index].deepCopy();
		}

		Module newModule = new Module(subModules, FC.create_random_func(MV.MAX_NEW_FUNC_DEPTH, size, MV.BOOLEAN_OUTPUT,
				false));
		return newModule;
	}

	private boolean evolutionIsImproving() {
		if (currentModules[0].fitness > prevWorstFit + Math.abs(prevWorstFit) * 0 || MV.MIN_ROUNDS > MV.currentRound) {
			prevWorstFit = currentModules[0].fitness;
			consecutiveWorst = 0;
		} else {
			consecutiveWorst++;
		}
		if (consecutiveWorst > MV.REQUIRED_IMPROVMENT_ROUNDS)
			return false;
		else
			return true;
	}

	private void reAdjustProb() {
		// bestFitSubmut, bestFitSubsum, bestFitCliqueborrow
		double totalBestFit = bestFitSubmut + bestFitSubsum + bestFitCliqueborrow;

		MV.CLIQUE_BORROW_PROB = bestFitCliqueborrow / totalBestFit;
		MV.SUBSUME_PROB = bestFitSubsum / totalBestFit;
		MV.SUB_MUT_PROB = 1 - MV.CLIQUE_BORROW_PROB - MV.SUBSUME_PROB;
		System.out.println("***************************************");
		System.out.println(bestFitCliqueborrow + " " + bestFitSubsum + " " + bestFitSubmut);
		System.out.println(MV.CLIQUE_BORROW_PROB + " " + MV.SUBSUME_PROB + " " + MV.SUB_MUT_PROB);

		if (MV.CLIQUE_BORROW_PROB != MV.CLIQUE_BORROW_PROB) {
			MV.SUB_MUT_PROB = (double) 1 / 3;
			MV.CLIQUE_BORROW_PROB = (double) 1 / 3;
			MV.SUBSUME_PROB = (double) 1 - MV.SUB_MUT_PROB - MV.CLIQUE_BORROW_PROB;
		}
		if (MV.SUBSUME_PROB != MV.SUBSUME_PROB) {
			MV.SUB_MUT_PROB = (double) 1 / 3;
			MV.CLIQUE_BORROW_PROB = (double) 1 / 3;
			MV.SUBSUME_PROB = (double) 1 - MV.SUB_MUT_PROB - MV.CLIQUE_BORROW_PROB;
		}
		if (MV.SUB_MUT_PROB != MV.SUB_MUT_PROB) {
			MV.SUB_MUT_PROB = (double) 1 / 3;
			MV.CLIQUE_BORROW_PROB = (double) 1 / 3;
			MV.SUBSUME_PROB = (double) 1 - MV.SUB_MUT_PROB - MV.CLIQUE_BORROW_PROB;
		}

		if (MV.CLIQUE_BORROW_PROB < MV.MIN_PROB) {
			MV.CLIQUE_BORROW_PROB = MV.MIN_PROB;
			if (MV.SUB_MUT_PROB < MV.MIN_PROB) {
				MV.SUB_MUT_PROB = MV.MIN_PROB;
				MV.SUBSUME_PROB = 1 - MV.SUB_MUT_PROB - MV.CLIQUE_BORROW_PROB;
			} else if (MV.SUBSUME_PROB < MV.MIN_PROB) {
				MV.SUBSUME_PROB = MV.MIN_PROB;
				MV.SUB_MUT_PROB = 1 - MV.SUBSUME_PROB - MV.CLIQUE_BORROW_PROB;
			} else {
				MV.SUB_MUT_PROB = bestFitSubmut / (totalBestFit - bestFitCliqueborrow);
				MV.SUBSUME_PROB = 1 - MV.SUB_MUT_PROB - MV.CLIQUE_BORROW_PROB;
			}
		} else if (MV.SUB_MUT_PROB < MV.MIN_PROB) {
			MV.SUB_MUT_PROB = MV.MIN_PROB;
			if (MV.SUBSUME_PROB < MV.MIN_PROB) {
				MV.SUBSUME_PROB = MV.MIN_PROB;
			} else {
				MV.SUBSUME_PROB = bestFitSubsum / (totalBestFit - bestFitSubmut);
			}
			MV.CLIQUE_BORROW_PROB = 1 - MV.SUBSUME_PROB - MV.SUB_MUT_PROB;
		} else if (MV.SUBSUME_PROB < MV.MIN_PROB) {
			MV.SUBSUME_PROB = MV.MIN_PROB;
			MV.SUB_MUT_PROB = bestFitSubmut / (totalBestFit - bestFitSubsum);
			MV.CLIQUE_BORROW_PROB = 1 - MV.SUBSUME_PROB - MV.SUB_MUT_PROB;
		}

		bestFitSubmut = -1.0e34;
		bestFitSubsum = -1.0e34;
		bestFitCliqueborrow = -1.0e34;

		System.out.println(MV.CLIQUE_BORROW_PROB + " " + MV.SUBSUME_PROB + " " + MV.SUB_MUT_PROB);

	}

	private Module subtree_mut() {
		int index = rd.nextInt(currentModules.length);
		Module newModule = currentModules[index].deepCopy();

		int[] function = newModule.function.clone();
		function = subtreeMutation(function, newModule.inputs.length);
		newModule.function = function;

		return newModule;
	}

	private Module subsume() {
		if (MV.currentLevel == 0)
			return createRandomModule();

		int index = -1;
		int size = 1 + rd.nextInt(MV.MAX_SUB_MODULES);
		Module[] subModules = new Module[size];
		for (int j = 0; j < size; j++) {
			int rand = rd.nextInt(MV.currentLevel);
			int rand2 = rd.nextInt(MV.DEMES);
			int rand3 = rd.nextInt(MV.masterModules[rand][rand2].length);
			subModules[j] = MV.masterModules[rand][rand2][rand3].deepCopy();
		}
		Module newModule = new Module(subModules, FC.create_random_func(MV.MAX_NEW_FUNC_DEPTH, size, MV.BOOLEAN_OUTPUT,
				false));
		return newModule;
	}

	static int[] subtreeMutation(int[] parent, int numOfInputs) {
		int mutStart, mutEnd, parentLen = parent.length, subtreeLen, lenOff;
		int[] newSubtree, offspring;

		// Calculate the mutation starting point.

		mutStart = rd.nextInt(parentLen);
		mutEnd = traverse(parent, mutStart);

		if (parent[mutStart] <= MV.FSET_4_START && parent[mutStart] >= MV.FSET_6_END)
			newSubtree = FC.create_random_func(MV.MAX_NEW_FUNC_DEPTH, numOfInputs, false, true);
		else if (parent[mutStart] <= MV.FSET_7_START && parent[mutStart] >= MV.FSET_8_END)
			newSubtree = FC.create_random_func(MV.MAX_NEW_FUNC_DEPTH, numOfInputs, true, false);
		else
			newSubtree = FC.create_random_func(MV.MAX_NEW_FUNC_DEPTH, numOfInputs, false, false);

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

		if (buffer[buffercount] > MV.FSET_1_START)
			return (buffercount);
		else if ((buffer[buffercount] <= MV.FSET_1_START && buffer[buffercount] >= MV.FSET_1_END)
				|| ((buffer[buffercount] <= MV.FSET_3_START && buffer[buffercount] >= MV.FSET_3_END)))
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		else if (buffer[buffercount] <= MV.FSET_6_START && buffer[buffercount] >= MV.FSET_6_END)
			return (traverse(buffer, 1 + traverse(buffer, 1 + traverse(buffer, ++buffercount))));
		else
			return (traverse(buffer, 1 + traverse(buffer, ++buffercount)));

	}

	/*
	 * static int tournament(int tsize, Module[] population) { int best =
	 * rd.nextInt(population.length), i, competitor; double fbest = -1.0e34;
	 * 
	 * for (i = 0; i < tsize; i++) { competitor = rd.nextInt(population.length);
	 * if (population[competitor].fitness > fbest) { fbest =
	 * population[competitor].fitness; best = competitor; }
	 * 
	 * } return (best); }
	 */

}
