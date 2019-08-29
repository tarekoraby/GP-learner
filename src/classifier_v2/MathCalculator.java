package classifier_v2;

import java.util.ArrayList;
import java.util.Arrays;

public class MathCalculator {

	static double calculate(int operation, double input) {
		switch (operation) {
		case MV.ABS:
			return returnDoubleOutput(Math.abs(input));
		case MV.ACOS:
			return returnDoubleOutput(Math.acos(input));
		case MV.ASIN:
			return returnDoubleOutput(Math.asin(input));
		case MV.ATAN:
			return returnDoubleOutput(Math.atan(input));
		case MV.CBRT:
			return returnDoubleOutput(Math.cbrt(input));
		case MV.CEIL:
			return returnDoubleOutput(Math.ceil(input));
		case MV.COS:
			return returnDoubleOutput(Math.cos(input));
		case MV.COSH:
			return returnDoubleOutput(Math.cosh(input));
		case MV.EXP:
			return returnDoubleOutput(Math.exp(input));
		case MV.EXPM1:
			return returnDoubleOutput(Math.expm1(input));
		case MV.FLOOR:
			return returnDoubleOutput(Math.floor(input));
		case MV.LOG:
			return returnDoubleOutput(Math.log(input));
		case MV.LOG10:
			return returnDoubleOutput(Math.log10(input));
		case MV.LOG1P:
			return returnDoubleOutput(Math.log1p(input));
		case MV.GETEXP:
			return returnDoubleOutput(Math.getExponent(input));
		case MV.NEXTUP:
			return returnDoubleOutput(Math.nextUp(input));
		case MV.RINT:
			return returnDoubleOutput(Math.rint(input));
		case MV.SIGNUM:
			return returnDoubleOutput(Math.signum(input));
		case MV.SIN:
			return returnDoubleOutput(Math.sin(input));
		case MV.SINH:
			return returnDoubleOutput(Math.sinh(input));
		case MV.SQRT:
			return returnDoubleOutput(Math.sqrt(input));
		case MV.TAN:
			return returnDoubleOutput(Math.tan(input));
		case MV.TANH:
			return returnDoubleOutput(Math.tanh(input));
		case MV.TODEGREES:
			return returnDoubleOutput(Math.toDegrees(input));
		case MV.TORADIANS:
			return returnDoubleOutput(Math.toRadians(input));
		case MV.ULP:
			return returnDoubleOutput(Math.ulp(input));

		default:
			Thread.dumpStack();
			System.exit(0);
			break;
		}

		Thread.dumpStack();
		System.exit(0);
		return returnDoubleOutput(input);
	}

	static double calculate(int operation, double input1, double input2) {

		switch (operation) {
		case MV.ADD:
			return returnDoubleOutput((input1 + input2));
		case MV.SUB:
			return returnDoubleOutput((input1 - input2));
		case MV.MUL:
			return returnDoubleOutput((input1 * input2));
		case MV.DIV:
			if (Math.abs(input2) <= 4.9E-100)
				return returnDoubleOutput((input1));
			else
				return returnDoubleOutput((input1 / input2));
		case MV.MOD:
			if (Math.abs(input2) <= 4.9E-100)
				return returnDoubleOutput((input1));
			else
				return returnDoubleOutput(input1 % input2);
		case MV.POW:
			return returnDoubleOutput(Math.pow(input1, input2));
		case MV.ATAN2:
			return returnDoubleOutput(Math.atan2(input1, input2));
		case MV.COPYSIGN:
			return returnDoubleOutput(Math.copySign(input1, input2));
		case MV.HYPOT:
			return returnDoubleOutput(Math.hypot(input1, input2));
		case MV.IEEEREMAINDER:
			return returnDoubleOutput(Math.IEEEremainder(input1, input2));
		case MV.NEXTAFTER:
			return returnDoubleOutput(Math.nextAfter(input1, input2));
		case MV.MAX:
			return returnDoubleOutput(Math.max(input1, input2));
		case MV.MIN:
			return returnDoubleOutput(Math.min(input1, input2));

		case MV.GT:
			if (input1 > input2)
				return returnDoubleOutput(1);
			else
				return returnDoubleOutput(0);
		case MV.LT:
			if (input1 < input2)
				return returnDoubleOutput(1);
			else
				return returnDoubleOutput(0);
		case MV.EQ:
			if (Math.abs(input1 - input2) < 1E-100)
				return returnDoubleOutput(1);
			else
				return returnDoubleOutput(0);
		case MV.AND:
			if ((input1 != 0 && input1 != 1) || (input2 != 0 && input2 != 1))
				break;
			if (input1 == 1 && input2 == 1)
				return returnDoubleOutput((1));
			else
				return returnDoubleOutput((0));
		case MV.OR:
			if ((input1 != 0 && input1 != 1) || (input2 != 0 && input2 != 1))
				break;
			if (input1 == 1 || input2 == 1)
				return returnDoubleOutput((1));
			else
				return returnDoubleOutput((0));
		case MV.XOR:
			if ((input1 != 0 && input1 != 1) || (input2 != 0 && input2 != 1))
				break;
			if ((input1 == 0 && input2 == 0) || (input1 == 1 && input2 == 1))
				return returnDoubleOutput((0));
			else
				return returnDoubleOutput((1));
		case MV.XNOR:
			if ((input1 != 0 && input1 != 1) || (input2 != 0 && input2 != 1))
				break;
			if ((input1 == 0 && input2 == 0) || (input1 == 1 && input2 == 1))
				return returnDoubleOutput((1));
			else
				return returnDoubleOutput((0));
		case MV.NAND:
			if ((input1 != 0 && input1 != 1) || (input2 != 0 && input2 != 1))
				break;
			if (input1 == 1 && input2 == 1)
				return returnDoubleOutput((0));
			else
				return returnDoubleOutput((1));

		default:
			break;
		}
		
		System.out.println("NAND "+  MV.NAND + " " + operation + " " + input1 + " " + input2);

		Thread.dumpStack();
		System.exit(0);
		return returnDoubleOutput(input1);
	}

	static double calculate(int operation, double[] inputArray) {

		int len = inputArray.length;
		if (len == 0)
			return returnDoubleOutput(-987654321.123456789);
		if (len == 1)
			return returnDoubleOutput(inputArray[0]);
		double[] newArray;

		switch (operation) {
		case MV.MEAN:
			double mean = 0;
			for (int i = 0; i < len; i++)
				mean += inputArray[i];
			mean /= len;
			return returnDoubleOutput(mean);
		case MV.MEDIAN:
			newArray = inputArray.clone();
			Arrays.sort(newArray);
			double median;
			int middle = len / 2;
			if (len % 2 == 1) {
				median = newArray[middle];
			} else {
				median = (newArray[middle - 1] + newArray[middle]) / 2;
			}
			return returnDoubleOutput(median);
		case MV.P25:
			newArray = inputArray.clone();
			Arrays.sort(newArray);
			return returnDoubleOutput(calculate(MV.MEDIAN, Arrays.copyOfRange(newArray, 0, len / 2 - 1)));
		case MV.P75:
			newArray = inputArray.clone();
			Arrays.sort(newArray);
			if (len % 2 == 1)
				return returnDoubleOutput(calculate(MV.MEDIAN, Arrays.copyOfRange(newArray, len / 2 + 1, newArray.length)));
			else
				return returnDoubleOutput(calculate(MV.MEDIAN, Arrays.copyOfRange(newArray, len / 2, newArray.length)));

		case MV.MAX_AR:
			double max = inputArray[0];
			for (int i = 1; i < len; i++)
				if (inputArray[i] > max)
					max = inputArray[i];
			return returnDoubleOutput(max);
		case MV.MIN_AR:
			double min = inputArray[0];
			for (int i = 1; i < len; i++)
				if (inputArray[i] < min)
					min = inputArray[i];
			return returnDoubleOutput(min);
		default:
			break;
		}

		Thread.dumpStack();
		System.exit(0);
		return inputArray[0];
	}

	static double[] calculateArray(int operation, double[] inputArray, double input) {

		int len = inputArray.length, numOfSatisfiers = 0;
		double[] result = new double[len];
		switch (operation) {
		case MV.ARR_GT:
			for (int i = 0; i < len; i++)
				if (inputArray[i] > input) {
					result[numOfSatisfiers++] = inputArray[i];
				}
			result = Arrays.copyOf(result, numOfSatisfiers);
			return result;
		case MV.ARR_LT:
			for (int i = 0; i < len; i++)
				if (inputArray[i] < input) {
					result[numOfSatisfiers++] = inputArray[i];
				}
			result = Arrays.copyOf(result, numOfSatisfiers);
			return result;

		default:
			break;
		}

		Thread.dumpStack();
		System.exit(0);
		return inputArray;
	}

	static double[] calculateArray(int operation, double[] input1, double[] input2) {
		int len1 = input1.length;
		int len2 = input2.length;
		double result[] = null;
		switch (operation) {
		case MV.ARR_COMBINE:
			result = new double[len1 + len2];
			for (int i = 0; i < len1; i++)
				result[i] = input1[i];
			for (int i = 0; i < len2; i++)
				result[i + len1] = input2[i];
			return result;
		}
		Thread.dumpStack();
		System.exit(0);
		return null;
	}

	static double[] calculateArray(int operation, double input1, double input2, double input3) {
		switch (operation) {
		case MV.ARR_CREATE:
			double[] result = { input1, input2, input3 };
			return result;
		}

		Thread.dumpStack();
		System.exit(0);
		return null;
	}
	
	

	private static double returnDoubleOutput(double output) {
		if (output != output || output == Double.MIN_VALUE || output == Double.POSITIVE_INFINITY
				|| output == Double.NEGATIVE_INFINITY)
			return 0;
		else
			return output;
	}

	public static double calculate(int primitive, double condition, double left, double right) {
		if (condition==1)
			return left;
		else {
			return right;
		}
	}
	
	public static double[] calculateArray(int primitive, double condition, double[] left, double[] right) {
		if (condition==1)
			return left;
		else {
			return right;
		}
	}

	public static double calcAvg(ArrayList<Module> inputModules, int caseIndex) {
		int size = inputModules.size();
		double total = 0;
		for (int i=0; i< size; i++){
			total += inputModules.get(i).getOutput(caseIndex);
		}
		return (double) total / size;
	}

	public static double calcSD(ArrayList<Module> inputModules, int caseIndex) {
		int size = inputModules.size();
		double mean = calcAvg(inputModules, caseIndex);
		double capSS = 0;
		for (int i = 0; i < size; i++) {
			capSS += (inputModules.get(i).getOutput(caseIndex) - mean) * (inputModules.get(i).getOutput(caseIndex) - mean);
		}

		double capSD = Math.sqrt(capSS / size);
		return capSD;

	}

	public static double calcMEDIAN(ArrayList<Module> inputModules, int caseIndex) {
		int size = inputModules.size();
		double capMed;
		double[] temp = new double[size];
		for (int i = 0; i < size; i++) {
			temp[i] = inputModules.get(i).getOutput(caseIndex);
		}

		Arrays.sort(temp);
		int middle = size / 2;

		if (size % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}
		
		return capMed;
	}

	public static double calcP25(ArrayList<Module> inputModules, int caseIndex) {
		int size = inputModules.size();
		if (size<4)
			return calcMEDIAN(inputModules, caseIndex);
		double capP25;
		double[] temp = new double[size];
		for (int i = 0; i < size; i++) {
			temp[i] = inputModules.get(i).getOutput(caseIndex);
		}

		Arrays.sort(temp);
		int P25 = size / 4;

		if (size % 2 == 1) {
			capP25 = temp[P25];
		} else {
			capP25 = (temp[P25 - 1] + temp[P25]) / 2;
		}

		return capP25;
	}

	public static double calcP75(ArrayList<Module> inputModules, int caseIndex) {
		int size = inputModules.size();
		if (size<4)
			return calcMEDIAN(inputModules, caseIndex);
		double capP75;
		double[] temp = new double[size];
		for (int i = 0; i < size; i++) {
			temp[i] = inputModules.get(i).getOutput(caseIndex);
		}

		Arrays.sort(temp);
		int P75 = (3 * size) / 4;

		if (size % 2 == 1) {
			capP75 = temp[P75];
		} else {
			capP75 = (temp[P75 - 1] + temp[P75]) / 2;
		}
		return capP75;
	}
}
