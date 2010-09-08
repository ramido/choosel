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
import java.util.Set;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;

import com.google.gwt.user.client.ui.Widget;

public class DelegatingViewContentDisplay implements ViewContentDisplay {

    private ViewContentDisplay delegate;

    public DelegatingViewContentDisplay(ViewContentDisplay delegate) {
        assert delegate != null;

        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return delegate.asWidget();
    }

    @Override
    public void checkResize() {
        delegate.checkResize();
    }

    @Override
    public PopupManager createPopupManager(ResourceItemValueResolver resolver,
            ResourceSet resources) {
        return delegate.createPopupManager(resolver, resources);
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public void endRestore() {
        delegate.endRestore();
    }

    @Override
    public List<ViewContentDisplayAction> getActions() {
        return delegate.getActions();
    }

    @Override
    public List<ViewContentDisplayConfiguration> getConfigurations() {
        return delegate.getConfigurations();
    }

    public ViewContentDisplay getDelegate() {
        return delegate;
    }

    @Override
    public String[] getSlotIDs() {
        return delegate.getSlotIDs();
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        delegate.init(callback);
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void restore(Memento state) {
        delegate.restore(state);
    }

    @Override
    public Memento save() {
        return delegate.save();
    }

    @Override
    public void startRestore() {
        delegate.startRestore();
    }

    @Override
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems) {

        delegate.update(addedResourceItems, updatedResourceItems,
                removedResourceItems);
    }

}
