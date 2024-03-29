/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.resources;

import java.util.EnumMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

public class DataTypeLists<T> {

    private Map<DataType, LightweightList<T>> dataTypeValues = new EnumMap<DataType, LightweightList<T>>(
            DataType.class);

    public DataTypeLists() {
        for (DataType dataType : DataType.values()) {
            dataTypeValues.put(dataType,
                    CollectionFactory.<T> createLightweightList());
        }
    }

    public LightweightList<T> get(DataType dataType) {
        assert dataType != null;
        assert dataTypeValues.containsKey(dataType);

        return dataTypeValues.get(dataType);
    }

}
