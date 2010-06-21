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

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.util.Disposable;

// TODO might need view as parameter for a few of those...
public interface ViewContentDisplay extends WidgetAdaptable, Disposable {

    void checkResize();

    ResourceItem createResourceItem(Layer layer, ResourceSet resources);

    void endRestore();

    String[] getSlotIDs();

    void init(ViewContentDisplayCallback callback);

    boolean isReady();

    void removeResourceItem(ResourceItem resourceItem);

    void restore(Memento state);

    Memento save();

    void startRestore();

}