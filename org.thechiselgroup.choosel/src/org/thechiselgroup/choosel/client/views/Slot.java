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

import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;

// TODO cloning / serialization for memento & storage support
public class Slot {

    private final String id;

    // TODO allow for several resolvers (heterogeneous collections)
    private PropertyValueResolver resolver;

    public PropertyValueResolver getResolver() {
	return resolver;
    }

    public void setResolver(PropertyValueResolver resolver) {
	this.resolver = resolver;
    }

    private String label;

    public Slot(String id) {
	this.id = id;
    }

    public String getId() {
	return id;
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

}
