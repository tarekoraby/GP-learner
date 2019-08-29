package regressor_v1;

import java.util.ArrayList;

public class DataEntry {
	private double output;
	private ArrayList<Double> inputs = new ArrayList<Double>();
	int correctPredictiosNumber;
	
	public DataEntry(ArrayList<Double> inputs, double output) {
		this.inputs = inputs;
		this.output = output;
		correctPredictiosNumber = 0;
	}
	
	public ArrayList<Double> getInputs() {
		return inputs;
	}
	
	public double getInputAtIndex(int index) {
		return inputs.get(index);
	}
	
	public double getOutput() {
		return output;
	}
	
	public void addInput(double newInput) {
		inputs.add(newInput);
	}
	
	public int getInputsSize(){
		return inputs.size();
	}

}
