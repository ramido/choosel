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
package org.thechiselgroup.choosel.core.client.util.collections;


/**
 * Change: new, changed, and removed elements.
 * 
 * @author Lars Grammel
 */
public class Delta<T> {

    private LightweightCollection<T> addedElements;

    private LightweightCollection<T> removedElements;

    private LightweightCollection<T> updatedElements;

    public Delta(LightweightCollection<T> addedElements,
            LightweightCollection<T> updatedElements,
            LightweightCollection<T> removedElements) {

        assert addedElements != null;
        assert removedElements != null;
        assert updatedElements != null;

        this.addedElements = addedElements;
        this.removedElements = removedElements;
        this.updatedElements = updatedElements;
    }

    public LightweightCollection<T> getAddedElements() {
        return addedElements;
    }

    public LightweightCollection<T> getRemovedElements() {
        return removedElements;
    }

    public LightweightCollection<T> getUpdatedElements() {
        return updatedElements;
    }

    public boolean isEmpty() {
        return addedElements.isEmpty() && updatedElements.isEmpty()
                && removedElements.isEmpty();
    }

    @Override
    public String toString() {
        return "Delta [addedElements=" + addedElements + ", removedElements="
                + removedElements + ", updatedElements=" + updatedElements
                + "]";
    }

}