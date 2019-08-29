package classifier_v1;

import java.util.ArrayList;
import java.util.Random;

// MASTER VARIABLES CLASS
public final class MV {

	static final boolean  BOOLEAN_OUTPUT = true;
	static final int MAX_NEW_FUNC_LEN = 10000, MAX_NEW_FUNC_DEPTH = 8, TSIZE = 2,
			POPSIZE = 5000, NUMOFCONSTANTS = 50;
	static final double EVO_IMPROV_MARGIN = 0.1, SUB_MUT_PROB = 1;
	
	// Constants indexes
	static final int TSET_1_START = -1, TSET_1_END = (NUMOFCONSTANTS * -1) - 1;

	// FUNCTIONS THAT TAKE A DOUBLE AND RETURNS A DOUBLE
	static final int ABS = TSET_1_END - 1, ACOS = ABS - 1, ASIN = ACOS - 1, ATAN = ASIN - 1, CBRT = ATAN - 1,
			CEIL = CBRT - 1, COS = CEIL - 1, COSH = COS - 1, EXP = COSH - 1, EXPM1 = EXP - 1, FLOOR = EXPM1 - 1,
			LOG = FLOOR - 1, LOG10 = LOG - 1, LOG1P = LOG10 - 1, GETEXP = LOG1P - 1, NEXTUP = GETEXP - 1,
			RINT = NEXTUP - 1, SIGNUM = RINT - 1, SIN = SIGNUM - 1, SINH = SIN - 1, SQRT = SINH - 1, TAN = SQRT - 1, TANH = TAN - 1,
			TODEGREES = TANH - 1, TORADIANS = TODEGREES - 1, ULP = TORADIANS - 1;
	static final int FSET_1_START = ABS, FSET_1_END = ULP;

	// FUNCTIONS THAT TAKE TWO DOUBLES AND RETURNS DOUBLE
	static final int ADD = FSET_1_END - 1, SUB = ADD - 1, MUL = SUB - 1, DIV = MUL - 1, MOD = DIV - 1, POW = MOD - 1,
			ATAN2 = POW - 1, COPYSIGN = ATAN2 - 1, HYPOT = COPYSIGN - 1, IEEEREMAINDER = HYPOT - 1,
			NEXTAFTER = IEEEREMAINDER - 1, MAX = NEXTAFTER - 1, MIN = MAX - 1;
	static final int FSET_2_START = ADD, FSET_2_END = MIN;

	// FUNCTIONS THAT TAKE AN ARRAY AND RETURN DOUBLE
	static final int MEAN = FSET_2_END - 1, MEDIAN = MEAN - 1, P25 = MEDIAN - 1, P75 = P25 - 1, MAX_AR = P75 - 1,
			MIN_AR = MAX_AR - 1;
	static final int FSET_3_START = MEAN, FSET_3_END = MIN_AR;

	// FUNCTIONS THAT TAKE AN ARRAY AND DOUBLE AND RETURN ARRAY
	static final int ARR_GT = FSET_3_END - 1, ARR_LT = ARR_GT - 1;
	static final int FSET_4_START = ARR_GT, FSET_4_END = ARR_LT;

	// FUNCTIONS THAT TAKE TWO ARRAYS AND RETURN ONE ARRAY
	static final int ARR_COMBINE = FSET_4_END - 1;
	static final int FSET_5_START = ARR_COMBINE, FSET_5_END = ARR_COMBINE;

	// FUNCTIONS THAT TAKE THREE DOUBLE AND RETURN ONE ARRAY
	static final int ARR_CREATE = FSET_5_END - 1;
	static final int FSET_6_START = ARR_CREATE, FSET_6_END = ARR_CREATE;

	// FUNCTIONS THAT TAKE TWO DOUBLES AND RETURN BOOLEAN
	static final int GT = FSET_6_END - 1, LT = GT - 1, EQ = LT - 1;
	static final int FSET_7_START = GT, FSET_7_END = EQ;

	// FUNCTIONS THAT TAKE TWO BOOLEANS AND RETURN BOOLEAN
	static final int AND = FSET_7_END - 1, OR = AND - 1, XOR = OR - 1, XNOR = XOR - 1, NAND = XNOR - 1;
	static final int FSET_8_START = AND, FSET_8_END = NAND;
	
	// FUNCTIONS THAT TAKE ONE BOOLEAN AND RETURN TWO BOOLEANS OR DOUBLES
	static final int IF_THEN_ELSE_DOUBLE = FSET_8_END - 1, IF_THEN_ELSE_BOOLEAN = IF_THEN_ELSE_DOUBLE - 1,
			IF_THEN_ELSE_ARRAY = IF_THEN_ELSE_BOOLEAN - 1;
	static final int FSET_9_START = IF_THEN_ELSE_DOUBLE, FSET_9_END = IF_THEN_ELSE_ARRAY;

	// FUNCTIONS THAT TAKE ALL INPUTS AND RETURN DOUBLE
	static final int INPUTS_AVG = FSET_9_END - 1, INPUTS_MEDIAN = INPUTS_AVG - 1, INPUTS_SD = INPUTS_MEDIAN - 1,
			INPUTS_P25 = INPUTS_SD - 1, INPUTS_P75 = INPUTS_P25 - 1;
	static final int FSET_10_START = INPUTS_AVG, FSET_10_END = INPUTS_P75;

	static int currentCaseIndex, numOfInputs, numOfTestCases, numTruePositives, numTrueNegatives;
	static double[] constants;


	static Random rd = new Random();
	static double[][] rawInputs;
	static double[] rawOutputs;
	static ArrayList<double[]> hiOrderInputs = new ArrayList<double[]>();
	static ArrayList<Module> primitiveModules = new ArrayList<Module>();

	public static void initialize() {
		for (int i=0; i<numOfInputs; i++){
			Module newPrimitiveModule = new Module(i);
			primitiveModules.add(newPrimitiveModule);
		}
		
		calcPosNeg();
		
		constants = new double[NUMOFCONSTANTS + 1];
		if (NUMOFCONSTANTS > 4) {
			constants[1] = -1;
			constants[2] = 0;
			constants[3] = Math.E;
			constants[4] = Math.PI;
		}
		
		Random rd = new Random();
		for (int i = 5; i < NUMOFCONSTANTS; i++)
			// create random numbers between -10 and 10
			constants[i] = 20 * (rd.nextDouble() - 1) + 10;

	}


	public static double getCurrentPrimitiveInput(int primitiveInputIndex) {
		// TODO Auto-generated method stub
		if (primitiveInputIndex < numOfInputs)
			return rawInputs[currentCaseIndex][primitiveInputIndex];
		else
			return hiOrderInputs.get(primitiveInputIndex - numOfInputs)[currentCaseIndex];
	}
	
	public static double getCurrentPrimitiveOutput() {
		// TODO Auto-generated method stub
		return rawOutputs[currentCaseIndex];
	}
	
	public static void calcPosNeg(){
		for ( int i = 0; i< numOfTestCases; i++){
			if (rawOutputs[i]==1)
				numTruePositives++;
			else if  (rawOutputs[i]==0)
				numTrueNegatives++;
			else{
				Thread.dumpStack();
				System.exit(0);
			}
		}
		
	}
	
	
}
