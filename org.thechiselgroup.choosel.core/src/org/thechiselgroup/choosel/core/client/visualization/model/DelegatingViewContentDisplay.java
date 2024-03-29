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
package org.thechiselgroup.choosel.core.client.visualization.model;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.util.NoSuchAdapterException;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Lars Grammel
 */
public class DelegatingViewContentDisplay implements ViewContentDisplay {

    private ViewContentDisplay delegate;

    public DelegatingViewContentDisplay(ViewContentDisplay delegate) {
        assert delegate != null;

        this.delegate = delegate;
    }

    @Override
    public <T> T adaptTo(Class<T> clazz) throws NoSuchAdapterException {
        return delegate.adaptTo(clazz);
    }

    @Override
    public Widget asWidget() {
        return delegate.asWidget();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public void endRestore() {
        delegate.endRestore();
    }

    public ViewContentDisplay getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public <T> T getPropertyValue(String property) {
        return delegate.getPropertyValue(property);
    }

    @Override
    public SidePanelSection[] getSidePanelSections() {
        return delegate.getSidePanelSections();
    }

    @Override
    public Slot[] getSlots() {
        return delegate.getSlots();
    }

    @Override
    public void init(VisualItemContainer container,
            ViewContentDisplayCallback callback) {

        delegate.init(container, callback);
    }

    @Override
    public boolean isAdaptableTo(Class<?> clazz) {
        return delegate.isAdaptableTo(clazz);
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        delegate.restore(state, restorationService, accessor);
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        return delegate.save(resourceSetCollector);
    }

    @Override
    public <T> void setPropertyValue(String property, T value) {
        delegate.setPropertyValue(property, value);
    }

    @Override
    public void setSize(int width, int height) {
        delegate.setSize(width, height);
    }

    @Override
    public void startRestore() {
        delegate.startRestore();
    }

    @Override
    public void update(Delta<VisualItem> delta,
            LightweightCollection<Slot> updatedSlots) {
        delegate.update(delta, updatedSlots);
    }

}
