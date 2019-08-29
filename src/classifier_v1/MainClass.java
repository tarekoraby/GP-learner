package classifier_v1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;


public class MainClass {

	/*
	 * this program proceeds in the following steps>
	 * 1. read the dataset
	 * 2. store the inputs in classes called Modules
	 */
	
	static String inputFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\GP_X\\input_dataset.csv";
	
	public static void main(String[] args) {
		System.out.println("Program start!!");
		importData();
		
		MV.initialize();
		Evolver evolver = new Evolver();
		Module bestModule=evolver.evolve(MV.primitiveModules);
	}



	private static void importData() {
		String line;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(inputFileNameString));
			int numbuerOfCases = countLines(inputFileNameString) - 1;
			MV.numOfTestCases = numbuerOfCases;
			line = in.readLine();
			StringTokenizer tokens = new StringTokenizer(line, ",");
			int numberOfInputs = tokens.countTokens() - 1;
			//System.out.println(numberOfInputs + " " + numbuerOfCases);
			MV.numOfInputs = numberOfInputs;
			MV.rawInputs = new double[numbuerOfCases][numberOfInputs];
			MV.rawOutputs = new double[numbuerOfCases];

			for (int j = 0; j < numbuerOfCases; j++) {
				line = in.readLine();
				tokens = new StringTokenizer(line, ",");
				for (int k = 0; k < numberOfInputs; k++) {
					MV.rawInputs[j][k] = Double.parseDouble(tokens.nextToken().trim());
					//System.out.print(MV.rawInputs[j][k] + " " );
				}
				MV.rawOutputs[j] = Double.parseDouble(tokens.nextToken().trim());
				//System.out.print(MV.rawOutputs[j] + " " );
				//System.out.println();
			}
			
			in.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
}
