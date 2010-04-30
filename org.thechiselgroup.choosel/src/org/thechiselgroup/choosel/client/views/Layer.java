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
package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class Layer {

    private String category;

    // TODO move data provider into layermodel
    private ResourceSet resources;

    // TODO serialization, memento
    private Map<String, Slot> slots = new HashMap<String, Slot>();

    public Layer() {
	// TODO Auto-generated constructor stub
    }

    public String getCategory() {
	return category;
    }

    public PropertyValueResolver getResolver(String slotID) {
	return getSlot(slotID).getResolver();
    }

    public ResourceSet getResources() {
	return resources;
    }

    private Slot getSlot(String slotID) {
	assert slotID != null;
	assert slots.containsKey(slotID) : "no such slot: " + slotID
		+ " ( available slots: " + slots.keySet() + " )";

	return slots.get(slotID);
    }

    public <T> T getValue(String slotID, Resource resource) {
	assert getResolver(slotID) != null : "no resolver for slot: " + slotID
		+ " ( available slots: " + slots.keySet() + " )";

	return (T) getResolver(slotID).getValue(resource);
    }

    public void initSlots(Slot[] slots) {
	for (Slot slot : slots) {
	    this.slots.put(slot.getId(), slot);
	}
    }

    public void setCategory(String category) {
	this.category = category;

    }

    public void setResources(ResourceSet resources) {
	this.resources = resources;
    }

}