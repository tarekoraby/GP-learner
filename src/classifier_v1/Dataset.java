package classifier_v1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Dataset {
	private int numberOfVariables;
	private ArrayList<ArrayList<Integer>> dataList=new ArrayList<ArrayList<Integer>>();
	
	public void setNumberOfVariables(int numberOfVariables) {
		this.numberOfVariables = numberOfVariables;
	}
	
	public int getNumberOfVariables() {
		return numberOfVariables;
	}
	
	public void addDataEntry(ArrayList<Integer> dataEntry){	
		dataList.add(dataEntry);
	}
	
	public ArrayList<Integer> getDataEntry(int entryIndex){
		return dataList.get(entryIndex);
	}
	
	public ArrayList<Integer> getInputsOfDataEntry(int entryIndex){
		return dataList.get(entryIndex).subList(0, dataList.get(entryIndex).size());
	}
	
	public int getOutputOfDataEntry(int entryIndex){
		int output = dataList.get(entryIndex)[dataList.get(entryIndex).length-1] ;
		return output;
	}
	
	createSubDataset (int inputvariable)
}
