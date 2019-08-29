package classifier_v1;

import java.util.Random;



public class FunctionCreator {
	static Random rd = new Random();

	int[] create_random_func(int maxDepth, int numOfInputs, boolean returnBoolean, boolean returnArray) {
		int depth = 1 + rd.nextInt(maxDepth);
		int[] indiv = grow(depth, numOfInputs, returnBoolean, returnArray);
		while (indiv.length > MV.MAX_NEW_FUNC_LEN)
			indiv = grow(depth, numOfInputs, returnBoolean, returnArray);
		return (indiv);
	}

	int[] grow(int depth, int numOfInputs, boolean returnBoolean, boolean returnArray) {
		/*if(depth<0){
			Thread.dumpStack();
			System.exit(0);
		}*/
		int[] buffer;
		if (returnBoolean) {
			int prim = rd.nextInt(3);
			if (prim == 0 || depth == 1) {
				int[] leftBuffer = grow(depth - 1, numOfInputs, false, false);
				int[] rightBuffer = grow(depth - 1, numOfInputs, false, false);
				buffer = new int[leftBuffer.length + rightBuffer.length + 1];
				prim = MV.FSET_7_START - rd.nextInt(MV.FSET_7_START - MV.FSET_7_END + 1);
				switch (prim) {
				case MV.GT:
				case MV.LT:
				case MV.EQ:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}

			} else if (prim == 1) {
				int[] leftBuffer = grow(depth - 1, numOfInputs, true, false);
				int[] rightBuffer = grow(depth - 1, numOfInputs, true, false);
				buffer = new int[leftBuffer.length + rightBuffer.length + 1];
				prim = MV.FSET_8_START - rd.nextInt(MV.FSET_8_START - MV.FSET_8_END + 1);
				switch (prim) {
				case MV.AND:
				case MV.OR:
				case MV.XOR:
				case MV.XNOR:
				case MV.NAND:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			} else {
				int[] condition = grow(depth - 1, numOfInputs, true, false);
				int[] leftBuffer = grow(depth - 1, numOfInputs, true, false);
				int[] rightBuffer = grow(depth - 1, numOfInputs, true, false);
				buffer = new int[condition.length + leftBuffer.length + rightBuffer.length + 1];
				buffer[0] = MV.IF_THEN_ELSE_BOOLEAN;
				System.arraycopy(condition, 0, buffer, 1, condition.length);
				System.arraycopy(leftBuffer, 0, buffer, (1 + condition.length), leftBuffer.length);
				System.arraycopy(rightBuffer, 0, buffer, (1 + condition.length + leftBuffer.length), rightBuffer.length);
			}
		} else if (returnArray) {
			int prim = rd.nextInt(3);
			if (prim == 0 || depth == 1) {
				int[] subBuffer1 = grow(depth - 1, numOfInputs, false, false);
				int[] subBuffer2 = grow(depth - 1, numOfInputs, false, false);
				int[] subBuffer3 = grow(depth - 1, numOfInputs, false, false);
				buffer = new int[subBuffer1.length + subBuffer2.length + subBuffer3.length + 1];
				prim = MV.FSET_6_START - rd.nextInt(MV.FSET_6_START - MV.FSET_6_END + 1);
				switch (prim) {
				case MV.ARR_CREATE:
					buffer[0] = prim;
					System.arraycopy(subBuffer1, 0, buffer, 1, subBuffer1.length);
					System.arraycopy(subBuffer2, 0, buffer, (1 + subBuffer1.length), subBuffer2.length);
					System.arraycopy(subBuffer3, 0, buffer, (1 + subBuffer1.length + subBuffer2.length),
							subBuffer3.length);
				}
			} else if (prim == 1 || depth == 2){
				prim = rd.nextInt(2);
				if (prim == 0) {
					int[] subBuffer1 = grow(depth - 1, numOfInputs, false, true);
					int[] subBuffer2 = grow(depth - 1, numOfInputs, false, false);
					buffer = new int[subBuffer1.length + subBuffer2.length + 1];
					prim = MV.FSET_4_START - rd.nextInt(MV.FSET_4_START - MV.FSET_4_END + 1);
					switch (prim) {
					case MV.ARR_GT:
					case MV.ARR_LT:
						buffer[0] = prim;
						System.arraycopy(subBuffer1, 0, buffer, 1, subBuffer1.length);
						System.arraycopy(subBuffer2, 0, buffer, (1 + subBuffer1.length), subBuffer2.length);
					}
				} else {
					int[] subBuffer1 = grow(depth - 1, numOfInputs, false, true);
					int[] subBuffer2 = grow(depth - 1, numOfInputs, false, true);
					buffer = new int[subBuffer1.length + subBuffer2.length + 1];
					prim = MV.FSET_5_START - rd.nextInt(MV.FSET_5_START - MV.FSET_5_END + 1);
					switch (prim) {
					case MV.ARR_COMBINE:
						buffer[0] = prim;
						System.arraycopy(subBuffer1, 0, buffer, 1, subBuffer1.length);
						System.arraycopy(subBuffer2, 0, buffer, (1 + subBuffer1.length), subBuffer2.length);
					}
				}
			} else {
				int[] condition = grow(depth - 1, numOfInputs, true, false);
				int[] leftBuffer = grow(depth - 1, numOfInputs, false, true);
				int[] rightBuffer = grow(depth - 1, numOfInputs, false, true);
				buffer = new int[condition.length + leftBuffer.length + rightBuffer.length + 1];
				buffer[0] = MV.IF_THEN_ELSE_ARRAY;
				System.arraycopy(condition, 0, buffer, 1, condition.length);
				System.arraycopy(leftBuffer, 0, buffer, (1 + condition.length), leftBuffer.length);
				System.arraycopy(rightBuffer, 0, buffer, (1 + condition.length + leftBuffer.length),
						rightBuffer.length);
			}
		} else {
			int prim = (int) rd.nextInt(2);
			if (prim == 0 || depth == 0) {
				prim = (int) rd.nextInt(numOfInputs);
				buffer = new int[1];
				buffer[0] = prim;
			} else {
				prim = (int) rd.nextInt(6);
				if (prim == 0 && depth < 2)
					prim = 2 + rd.nextInt(3);
				if (prim == 0) {
					int[] subBuffer = grow(depth - 1, numOfInputs, false, true);
					buffer = new int[subBuffer.length + 1];
					prim = MV.FSET_3_START - rd.nextInt(MV.FSET_3_START - MV.FSET_3_END + 1);
					switch (prim) {
					case MV.MEAN:
					case MV.MEDIAN:
					case MV.P25:
					case MV.P75:
					case MV.MAX_AR:
					case MV.MIN_AR:
						buffer[0] = prim;
						System.arraycopy(subBuffer, 0, buffer, 1, subBuffer.length);
					}
				} else if (prim == 1 && depth > 1) {
					int[] condition = grow(depth - 1, numOfInputs, true, false);
					int[] leftBuffer = grow(depth - 1, numOfInputs, false, false);
					int[] rightBuffer = grow(depth - 1, numOfInputs, false, false);
					buffer = new int[condition.length + leftBuffer.length + rightBuffer.length + 1];
					buffer[0] = MV.IF_THEN_ELSE_DOUBLE;
					System.arraycopy(condition, 0, buffer, 1, condition.length);
					System.arraycopy(leftBuffer, 0, buffer, (1 + condition.length), leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + condition.length + leftBuffer.length),
							rightBuffer.length);
				} else if (prim == 2) {
					int[] subBuffer = grow(depth - 1, numOfInputs, false, false);
					buffer = new int[subBuffer.length + 1];
					prim = MV.FSET_1_START - rd.nextInt(MV.FSET_1_START - MV.FSET_1_END + 1);
					switch (prim) {
					case MV.ABS:
					case MV.ACOS:
					case MV.ASIN:
					case MV.ATAN:
					case MV.CBRT:
					case MV.CEIL:
					case MV.COS:
					case MV.COSH:
					case MV.EXP:
					case MV.EXPM1:
					case MV.FLOOR:
					case MV.LOG:
					case MV.LOG10:
					case MV.LOG1P:
					case MV.GETEXP:
					case MV.NEXTUP:
					case MV.RINT:
					case MV.SIGNUM:
					case MV.SIN:
					case MV.SINH:
					case MV.SQRT:
					case MV.TAN:
					case MV.TANH:
					case MV.TODEGREES:
					case MV.TORADIANS:
					case MV.ULP:
						buffer[0] = prim;
						System.arraycopy(subBuffer, 0, buffer, 1, subBuffer.length);
					}
				} else if (prim == 3) {
					int[] leftBuffer = grow(depth - 1, numOfInputs, false, false);
					int[] rightBuffer = grow(depth - 1, numOfInputs, false, false);
					buffer = new int[leftBuffer.length + rightBuffer.length + 1];
					prim = MV.FSET_2_START - rd.nextInt(MV.FSET_2_START - MV.FSET_2_END + 1);
					switch (prim) {
					case MV.ADD:
					case MV.SUB:
					case MV.MUL:
					case MV.DIV:
					case MV.MOD:
					case MV.POW:
					case MV.ATAN2:
					case MV.COPYSIGN:
					case MV.HYPOT:
					case MV.IEEEREMAINDER:
					case MV.NEXTAFTER:
					case MV.MAX:
					case MV.MIN:
						buffer[0] = prim;
						System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
						System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
					}
				} else if (prim == 4) {
					prim = MV.FSET_10_START - rd.nextInt(MV.FSET_10_START - MV.FSET_10_END + 1);
					buffer = new int[1];
					buffer[0] = prim;
				} else {
					// return constant
					prim = -1 * (1 + rd.nextInt(MV.NUMOFCONSTANTS));
					buffer = new int[1];
					buffer[0] = prim;
				}
			}
		}

		return buffer;
	}

	private void checkErrors(int level, int popSize, int depth) {
		if (popSize < 1 || depth < 0) {
			System.out.println("StrategyCreator Class error!!!");
			System.exit(0);
		}
	}

	private static void checkErrors(int popSize, int depth) {
		if (popSize < 1 || depth < 0) {
			System.out.println("StrategyCreator Class error!!!");
			System.exit(0);
		}
	}
}
