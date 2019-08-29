package classifier_v1;

import java.util.ArrayList;

public class Module {
	private ArrayList<Module> inputModules;
	private boolean primitiveModule;
	private int primitiveInputIndex;
	private int[] function;	
	int PC, length;
	double accFitness, predictiveFitness, sensitivityFitness, specificityFitness;

	Module(int primitiveInputIndex) {
		this.primitiveModule = true;
		this.primitiveInputIndex = primitiveInputIndex;
		length = 0;
		accFitness = -1 * Double.MAX_VALUE;
		predictiveFitness = -1 * Double.MAX_VALUE;
		sensitivityFitness = -1 * Double.MAX_VALUE;
		specificityFitness = -1 * Double.MAX_VALUE;
	}

	Module(ArrayList<Module> inputModules, int[] function) {
		this.primitiveModule = false;
		this.inputModules = inputModules;
		this.function = function;
		accFitness = -1 * Double.MAX_VALUE;
		predictiveFitness = -1 * Double.MAX_VALUE;
		sensitivityFitness = -1 * Double.MAX_VALUE;
		specificityFitness = -1 * Double.MAX_VALUE;
		length = function.length + inputModules.size();
		for (int i = 0; i < inputModules.size(); i++)
			length += inputModules.get(i).length;
	}
	
	public int[] getFunction() {
		return function.clone();
	}
	
	double getOutput() {
		if (primitiveModule)
			return MV.getCurrentPrimitiveInput(primitiveInputIndex);

		PC = 0;
		double output = run();
		return output;
	}

	private double[] runArray() {
		int primitive = function[PC++];

		if (primitive <= MV.FSET_4_START && primitive >= MV.FSET_4_END)
			return MathCalculator.calculateArray(primitive, runArray(), run());
		else if (primitive <= MV.FSET_5_START && primitive >= MV.FSET_5_END)
			return MathCalculator.calculateArray(primitive, runArray(), runArray());
		else if (primitive <= MV.FSET_6_START && primitive >= MV.FSET_6_END)
			return MathCalculator.calculateArray(primitive, run(), run(), run());
		else  if (primitive == MV.IF_THEN_ELSE_ARRAY)
			return MathCalculator.calculateArray(primitive, run(), runArray(), runArray());
		

		Thread.dumpStack();
		System.exit(0);
		return null;
	}

	private double run() {
		int primitive = function[PC++];

		if (primitive < 0) {
			if (primitive <= MV.TSET_1_START && primitive >= MV.TSET_1_END) {
				return MV.constants[primitive * -1];
			} else if ((primitive <= MV.FSET_2_START && primitive >= MV.FSET_2_END)
					|| (primitive <= MV.FSET_7_START && primitive >= MV.FSET_7_END)
					|| (primitive <= MV.FSET_8_START && primitive >= MV.FSET_8_END)) {
				return MathCalculator.calculate(primitive, run(), run());
			} else if (primitive <= MV.FSET_1_START && primitive >= MV.FSET_1_END) {
				return MathCalculator.calculate(primitive, run());
			} else if (primitive <= MV.FSET_3_START && primitive >= MV.FSET_3_END) {
				return MathCalculator.calculate(primitive, runArray());
			} else if (primitive == MV.IF_THEN_ELSE_DOUBLE || primitive == MV.IF_THEN_ELSE_BOOLEAN) {
				return MathCalculator.calculate(primitive, run(), run(), run());
			}  else if (primitive==MV.INPUTS_AVG)
				return MathCalculator.calcAvg(inputModules);
			else if (primitive==MV.INPUTS_SD)
				return MathCalculator.calcSD(inputModules);
			else if (primitive==MV.INPUTS_MEDIAN)
				return MathCalculator.calcMEDIAN(inputModules);
			else if (primitive==MV.INPUTS_P25)
				return MathCalculator.calcP25(inputModules);
			else if (primitive==MV.INPUTS_P75)
				return MathCalculator.calcP75(inputModules);

			Thread.dumpStack();
			System.exit(0);
			return -99;
		} else
			return inputModules.get(primitive).getOutput();

	}

	private double calcAvg(ArrayList<Module> inputModules2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAsPrimitive() {		
		double[] outputs= new double[MV.numOfTestCases];
		for (int i = 0; i < MV.numOfTestCases; i++) {
			MV.currentCaseIndex = i;
			outputs[i] = getOutput();

			//System.out.println(outputs[i]);
		}
		MV.hiOrderInputs.add(outputs); 
		primitiveModule = true;
		primitiveInputIndex = MV.numOfInputs + MV.hiOrderInputs.size() - 1;
	}
	
	public void setAsNative() {				
		primitiveModule = false;
	}

	
}
