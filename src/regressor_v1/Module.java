package regressor_v1;

import java.util.ArrayList;

public class Module {
	private ArrayList<Module> inputModules;
	private boolean primitiveModule;
	private int primitiveInputIndex;
	private int[] function;	
	int PC, length;
	double fitness;

	Module(int primitiveInputIndex) {
		this.primitiveModule = true;
		this.primitiveInputIndex = primitiveInputIndex;
		//System.out.println(primitiveInputIndex);
		length = 0;
		fitness = -1 * Double.MAX_VALUE;
	}

	Module(ArrayList<Module> inputModules, int[] function) {
		this.primitiveModule = false;
		this.inputModules = inputModules;
		this.function = function;
		fitness = -1 * Double.MAX_VALUE;

		length = function.length + inputModules.size();
		for (int i = 0; i < inputModules.size(); i++)
			length += inputModules.get(i).length;
	}
	
	public int[] getFunction() {
		return function.clone();
	}
	
	double getOutput(int caseIndex) {
		//System.out.println(primitiveInputIndex);
		if (primitiveModule)
			return MV.getCurrentInput(caseIndex, primitiveInputIndex);

		PC = 0;
		double output = run(caseIndex);
		return output;
	}

	private double[] runArray(int caseIndex) {
		int primitive = function[PC++];

		if (primitive <= MV.FSET_4_START && primitive >= MV.FSET_4_END)
			return MathCalculator.calculateArray(primitive, runArray(caseIndex), run(caseIndex));
		else if (primitive <= MV.FSET_5_START && primitive >= MV.FSET_5_END)
			return MathCalculator.calculateArray(primitive, runArray(caseIndex), runArray(caseIndex));
		else if (primitive <= MV.FSET_6_START && primitive >= MV.FSET_6_END)
			return MathCalculator.calculateArray(primitive, run(caseIndex), run(caseIndex), run(caseIndex));
		else  if (primitive == MV.IF_THEN_ELSE_ARRAY)
			return MathCalculator.calculateArray(primitive, run(caseIndex), runArray(caseIndex), runArray(caseIndex));
		

		Thread.dumpStack();
		System.exit(0);
		return null;
	}

	private double run(int caseIndex) {
		int primitive = function[PC++];

		if (primitive < 0) {
			if (primitive <= MV.TSET_1_START && primitive >= MV.TSET_1_END) {
				return MV.constants[primitive * -1];
			} else if ((primitive <= MV.FSET_2_START && primitive >= MV.FSET_2_END)
					|| (primitive <= MV.FSET_7_START && primitive >= MV.FSET_7_END)
					|| (primitive <= MV.FSET_8_START && primitive >= MV.FSET_8_END)) {
				return MathCalculator.calculate(primitive, run(caseIndex), run(caseIndex));
			} else if (primitive <= MV.FSET_1_START && primitive >= MV.FSET_1_END) {
				return MathCalculator.calculate(primitive, run(caseIndex));
			} else if (primitive <= MV.FSET_3_START && primitive >= MV.FSET_3_END) {
				return MathCalculator.calculate(primitive, runArray(caseIndex));
			} else if (primitive == MV.IF_THEN_ELSE_DOUBLE || primitive == MV.IF_THEN_ELSE_BOOLEAN) {
				return MathCalculator.calculate(primitive, run(caseIndex), run(caseIndex), run(caseIndex));
			}  else if (primitive==MV.INPUTS_AVG)
				return MathCalculator.calcAvg(inputModules, caseIndex);
			else if (primitive==MV.INPUTS_SD)
				return MathCalculator.calcSD(inputModules, caseIndex);
			else if (primitive==MV.INPUTS_MEDIAN)
				return MathCalculator.calcMEDIAN(inputModules, caseIndex);
			else if (primitive==MV.INPUTS_P25)
				return MathCalculator.calcP25(inputModules, caseIndex);
			else if (primitive==MV.INPUTS_P75)
				return MathCalculator.calcP75(inputModules, caseIndex);

			Thread.dumpStack();
			System.exit(0);
			return -99;
		} else
			return inputModules.get(primitive).getOutput(caseIndex);

	}

	private double calcAvg(ArrayList<Module> inputModules2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAsPrimitive() {		
		double output;
		for (int i = 0; i < MV.allData.size(); i++) {
			output = getOutput(i);
			MV.allData.get(i).addInput(output);
		}
		
		primitiveModule = true;
		MV.numOfCurrInputs++;
		primitiveInputIndex = MV.numOfCurrInputs - 1;
	}
	
	public void setAsNative() {				
		primitiveModule = false;
	}

	
}
