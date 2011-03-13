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
package org.thechiselgroup.choosel.core.client.views;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetAddedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetRemovedEventHandler;

import com.google.gwt.event.shared.HandlerRegistration;

public interface SelectionModel {

    HandlerRegistration addEventHandler(ResourceSetActivatedEventHandler handler);

    HandlerRegistration addEventHandler(ResourceSetAddedEventHandler handler);

    HandlerRegistration addEventHandler(ResourceSetChangedEventHandler handler);

    HandlerRegistration addEventHandler(ResourceSetRemovedEventHandler handler);

    void addSelectionSet(ResourceSet selectionSet);

    boolean containsSelectionSet(ResourceSet resourceSet);

    ResourceSet getSelection();

    ResourceSet getSelectionProxy();

    void removeSelectionSet(ResourceSet selectionSet);

    void setSelection(ResourceSet newSelectionModel);

    void switchSelection(ResourceSet resources);

}