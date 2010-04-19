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

import java.util.List;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;

import com.google.gwt.user.client.ui.Widget;

public class DelegatingViewContentDisplay implements ViewContentDisplay {

    private ViewContentDisplay delegate;

    public DelegatingViewContentDisplay(ViewContentDisplay delegate) {
	assert delegate != null;

	this.delegate = delegate;
    }

    public Widget asWidget() {
	return delegate.asWidget();
    }

    public void checkResize() {
	delegate.checkResize();
    }

    public ResourceItem createResourceItem(Layer layer, Resource resource) {
	return delegate.createResourceItem(layer, resource);
    }

    public Slot[] createSlots() {
	return delegate.createSlots();
    }

    public void dispose() {
	delegate.dispose();
    }

    public void endRestore() {
	delegate.endRestore();
    }

    public ViewContentDisplay getDelegate() {
	return delegate;
    }

    public void init(ViewContentDisplayCallback callback) {
	delegate.init(callback);
    }

    public void initLayer(Layer layerModel, List<Layer> layers) {
	delegate.initLayer(layerModel, layers);
    }

    public boolean isReady() {
	return delegate.isReady();
    }

    public void removeResourceItem(ResourceItem resourceItem) {
	delegate.removeResourceItem(resourceItem);
    }

    @Override
    public void restore(Memento state) {
	delegate.restore(state);
    }

    @Override
    public Memento save() {
	return delegate.save();
    }

    public void startRestore() {
	delegate.startRestore();
    }

}
