package classifier_v1;

public class Module_old {

	Module[] inputs;
	int[] function;
	double output, fitness;
	double[] arrayOutput;
	boolean isPrimitive;
	int PC, primitiveInputID, length;

	Module(Module[] inputs, int[] function) {
		this.inputs = inputs;
		this.function = function;
		isPrimitive = false;
		fitness = 0;
		length = inputs.length;
		for (int i = 0; i < inputs.length; i++)
			length += inputs[i].length;
	}

	Module(int primitiveInputID) {
		this.primitiveInputID = primitiveInputID;
		isPrimitive = true;
		fitness = 0;
		inputs = new Module[0];
		function = null;
		length = 1;
	}

	double getOutput() {
		if (isPrimitive) {
			output = MV.primitiveInputs[primitiveInputID];
			return output;
		} else {
			PC = 0;
			output = run();
			return output;
		}
	}

	private double[] runArray() {
		int primitive = function[PC++];

		if (primitive <= MV.FSET_4_START && primitive >= MV.FSET_4_END)
			return MathCalculator.calculate(primitive, runArray(), run());
		if (primitive <= MV.FSET_5_START && primitive >= MV.FSET_5_END)
			return MathCalculator.calculate(primitive, runArray(), runArray());
		if (primitive <= MV.FSET_6_START && primitive >= MV.FSET_6_END)
			return MathCalculator.calculate(primitive, run(), run(), run());

		System.out.println("\nModule class error - runArray Method!!! \n");
		for (int i=0; i<function.length; i++)
			System.out.print(function[i] + " ");
		System.exit(0);
		return null;
	}

	private double run() {
		int primitive = function[PC++];

		if (primitive < 0) {
			if ((primitive <= MV.FSET_2_START && primitive >= MV.FSET_2_END)
					|| (primitive <= MV.FSET_7_START && primitive >= MV.FSET_7_END)
					|| (primitive <= MV.FSET_8_START && primitive >= MV.FSET_8_END))
				return MathCalculator.calculate(primitive, run(), run());

			if (primitive <= MV.FSET_1_START && primitive >= MV.FSET_1_END)
				return MathCalculator.calculate(primitive, run());

			if (primitive <= MV.FSET_3_START && primitive >= MV.FSET_3_END)
				return MathCalculator.calculate(primitive, runArray());

			System.out.println("\nModule class error !!! run function ");
			System.exit(0);
			return -99;
		} else
			return inputs[primitive].getOutput();
	}

	Module deepCopy() {
		Module copy;
		if (isPrimitive) {
			copy = new Module(primitiveInputID);
			return copy;
		} else {
			Module[] newInputs = new Module[inputs.length];
			for (int i = 0; i < inputs.length; i++) {
				newInputs[i] = inputs[i].deepCopy();
				newInputs[i].length = inputs[i].length;
			}
			copy = new Module(newInputs, function.clone());
			return copy;
		}
	}
}
