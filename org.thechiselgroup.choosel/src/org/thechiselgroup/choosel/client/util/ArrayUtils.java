/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/

package org.thechiselgroup.choosel.client.util;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class ArrayUtils {

    // @formatter:off
    private native static JavaScriptObject createArray() /*-{
        return new Array();
    }-*/;
    // @formatter:on

    // @formatter:off
    private native static void pushArray(JavaScriptObject array, double d) /*-{
        array.push(d);
    }-*/;
    // @formatter:on

    // @formatter:off
    private native static void pushArray(JavaScriptObject array, int i) /*-{
        array.push(i);
    }-*/;
    // @formatter:on

    // @formatter:off
    private native static void pushArray(JavaScriptObject array, String o) /*-{
        array.push(o);
    }-*/;
    // @formatter:on

    public static double[] toDoubleArray(List<? extends Number> numberList) {
        double[] doubleArray = new double[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            doubleArray[i] = numberList.get(i).doubleValue();
        }
        return doubleArray;
    }

    public static int[] toIntegerArray(List<? extends Number> numberList) {
        int[] intArray = new int[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            intArray[i] = numberList.get(i).intValue();
        }
        return intArray;
    }

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

    public static String[] toStringArray(List<? extends Object> objectList) {
        String[] stringArray = new String[objectList.size()];
        for (int i = 0; i < objectList.size(); i++) {
            stringArray[i] = objectList.get(i).toString();
        }
        return stringArray;
    }

}
