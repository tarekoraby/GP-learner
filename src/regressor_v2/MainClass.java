package regressor_v2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class MainClass {

	
	static String inputFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\GP_X\\regress_input_dataset.csv";
	
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
