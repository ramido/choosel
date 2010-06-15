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
package org.thechiselgroup.choosel.client.resources;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

// TODO update & extend (1, many sets added / removed) test case
public class ResourceSplitter extends AbstractResourceContainer {

    private final ResourceCategorizer categorizer;

    private Map<String, ResourceSet> categorizedSets = new HashMap<String, ResourceSet>();

    private final ResourceSetFactory resourceSetFactory;

    private transient HandlerManager eventBus;

    private final CategoryLabelProvider labelProvider;

    @Inject
    public ResourceSplitter(
	    ResourceCategorizer categorizer, // new categorizer interface
	    ResourceSetFactory resourceSetFactory,
	    CategoryLabelProvider labelProvider) {

	this.categorizer = categorizer;
	this.resourceSetFactory = resourceSetFactory;
	this.labelProvider = labelProvider;

	this.eventBus = new HandlerManager(this);
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
	return new HashMap<String, ResourceSet>(categorizedSets);
    }

    @Override
    public void add(Resource resource) {
	// TODO needs to return Set<String> --> need new interface
	// TODO keep old resource categorizer interface, create wrapper
	String category = categorizer.getCategory(resource);

	// TODO do this in a loop
	ResourceSet resourceSet = getResourceSet(category);
	resourceSet.add(resource);
    }

    private ResourceSet getResourceSet(String category) {
	if (!categorizedSets.containsKey(category)) {
	    ResourceSet resourceSet = resourceSetFactory.createResourceSet();

	    resourceSet.setLabel(labelProvider.getLabel(category));

	    categorizedSets.put(category, resourceSet);

	    eventBus.fireEvent(new ResourceCategoryAddedEvent(category,
		    resourceSet));
	}

	return categorizedSets.get(category);
    }

    @Override
    public void remove(Resource resource) {
	// check if sets exists --> possible bug add/remove events

	// TODO change me - you can figure this one out
	String category = categorizer.getCategory(resource);
	ResourceSet resourceSet = getResourceSet(category);

	resourceSet.remove(resource);

	if (resourceSet.isEmpty()) {
	    categorizedSets.remove(category);
	    eventBus.fireEvent(new ResourceCategoryRemovedEvent(category,
		    resourceSet));
	}
    }

    public <H extends ResourceCategoryContainerEventHandler> HandlerRegistration addHandler(
	    Type<H> type, H handler) {

	return eventBus.addHandler(type, handler);
    }

}
