package org.thechiselgroup.choosel.client.ui.widget.chart;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;

public class ArrayUtils {
    private native static JavaScriptObject createArray() /*-{
	    return new Array();
	}-*/;
	
	private native static void pushArray(JavaScriptObject array, double d) /*-{
	    array.push(d);
	}-*/;
	
	private native static void pushArray(JavaScriptObject array, int i) /*-{
	    array.push(i);
	}-*/;
	
	private native static void pushArray(JavaScriptObject array, String o) /*-{
	    array.push(o);
	}-*/;
	
	public static JavaScriptObject toJsArray(double[] array) {
		JavaScriptObject result = createArray();
		for (int i = 0; i < array.length; i++) {
		    pushArray(result, array[i]);
		}
		return result;
	}
	
	public static JavaScriptObject toJsArray(int[] array) {
		JavaScriptObject result = createArray();
		for (int i = 0; i < array.length; i++) {
		    pushArray(result, array[i]);
		}
		return result;
	}
	
	public static JavaScriptObject toJsArray(String[] array) {
		JavaScriptObject result = createArray();
		for (int i = 0; i < array.length; i++) {
		    pushArray(result, array[i]);
		}
		return result;
	}
	
	public static double[] toDoubleArray(ArrayList<Double> doubleArrayList) {
		Object[] objectArray = doubleArrayList.toArray();
		String[] stringArray = new String[doubleArrayList.size()];
		double[] doubleArray = new double[doubleArrayList.size()];
		for(int i = 0; i < doubleArrayList.size(); i++) {
			stringArray[i] = objectArray[i].toString();
			doubleArray[i] = Double.valueOf(stringArray[i]).doubleValue();
		}
		return doubleArray;
	}
	
	public static int[] toIntegerArray(ArrayList<Integer> integerArrayList) {
		Object[] objectArray = integerArrayList.toArray();
		String[] stringArray = new String[integerArrayList.size()];
		int[] intArray = new int[integerArrayList.size()];
		for(int i = 0; i < integerArrayList.size(); i++) {
			stringArray[i] = objectArray[i].toString();
			intArray[i] = Integer.valueOf(stringArray[i]).intValue();
		}
		return intArray;
	}
}
