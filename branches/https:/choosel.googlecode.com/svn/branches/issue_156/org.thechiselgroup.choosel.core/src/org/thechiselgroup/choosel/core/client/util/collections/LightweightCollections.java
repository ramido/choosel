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
package org.thechiselgroup.choosel.core.client.util.collections;

public final class LightweightCollections {

    public static <T> LightweightCollection<T> emptyCollection() {
        return NullLightweightCollection.nullLightweightCollection();
    }

    public static <T> LightweightCollection<T> emptySet() {
        return emptyCollection();
    }

    public static <T> LightweightCollection<T> toCollection(T... values) {
        LightweightList<T> result = CollectionFactory.createLightweightList();
        for (T t : values) {
            result.add(t);
        }
        return result;
    }

    private LightweightCollections() {
    }

}