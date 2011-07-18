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

import static org.thechiselgroup.choosel.core.client.util.collections.Delta.createAddedDelta;
import static org.thechiselgroup.choosel.core.client.util.collections.Delta.createRemovedDelta;

import java.util.Map;
import java.util.NoSuchElementException;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.util.DisposeUtil;
import org.thechiselgroup.choosel.core.client.util.NoSuchAdapterException;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractViewContentDisplay implements ViewContentDisplay,
        VisualItemContainer {
    protected ViewContentDisplayCallback callback;

    private boolean restoring = false;

    private Widget widget;

    private final Map<String, ViewContentDisplayProperty<?>> properties = CollectionFactory
            .createStringMap();

    private VisualItemContainer container;

    @Override
    public <T> T adaptTo(Class<T> clazz) throws NoSuchAdapterException {
        throw new NoSuchAdapterException(this, clazz);
    }

    @Override
    public HandlerRegistration addHandler(
            VisualItemContainerChangeEventHandler handler) {
        return container.addHandler(handler);
    }

    @Override
    public Widget asWidget() {
        if (widget == null) {
            widget = createWidget();

            widget.addAttachHandler(new Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached()) {
                        onAttach();
                    } else {
                        onDetach();
                    }
                }
            });
        }

        return widget;
    }

    @Override
    public void checkResize() {
    }

    @Override
    public boolean containsVisualItem(String viewItemId) {
        return container.containsVisualItem(viewItemId);
    }

    protected abstract Widget createWidget();

    @Override
    public void dispose() {
        callback = null;
        container = null;
        widget = DisposeUtil.dispose(widget);
    }

    @Override
    public void endRestore() {
        restoring = false;
    }

    public ViewContentDisplayCallback getCallback() {
        return callback;
    };

    public VisualItemContainer getContainer() {
        return container;
    }

    @SuppressWarnings("unchecked")
    private <T> ViewContentDisplayProperty<T> getProperty(String property) {
        return (ViewContentDisplayProperty<T>) properties.get(property);
    }

    @Override
    public <T> T getPropertyValue(String property) {
        // TODO NoSuchPropertyException extends RuntimeException
        if (!properties.containsKey(property)) {
            throw new IllegalArgumentException("Property '" + property
                    + "' does not exist.");
        }

        return this.<T> getProperty(property).getValue();
    }

    @Override
    public SidePanelSection[] getSidePanelSections() {
        return new SidePanelSection[0];
    }

    @Override
    public VisualItem getVisualItem(String viewItemId)
            throws NoSuchElementException {
        return container.getVisualItem(viewItemId);
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItems() {
        return container.getVisualItems();
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItems(
            Iterable<Resource> resources) {
        return container.getVisualItems(resources);
    }

    @Override
    public void init(VisualItemContainer container,
            ViewContentDisplayCallback callback) {

        assert container != null;
        assert callback != null;

        this.callback = callback;
        this.container = container;
    }

    @Override
    public boolean isAdaptableTo(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    public boolean isRestoring() {
        return restoring;
    }

    protected void onAttach() {
        assert container != null;
        assert callback != null;

        if (!getVisualItems().isEmpty()) {
            /*
             * XXX the lifecycle should be exposed and the visualization model
             * should respect the lifecycle (this should be removed)
             */
            update(createAddedDelta(getVisualItems()),
                    LightweightCollections.<Slot> emptyCollection());
        }
    }

    protected void onDetach() {
        // might have been disposed (then callback would be null)
        if (container != null && !getVisualItems().isEmpty()) {
            /*
             * XXX this might be problematic, because view items are removed
             * when the visualization model is disposed.
             */
            update(createRemovedDelta(getVisualItems()),
                    LightweightCollections.<Slot> emptyCollection());
        }
    }

    /**
     * Adds a {@link ViewContentDisplayProperty} to the properties of this
     * {@link ViewContentDisplay}.
     */
    protected void registerProperty(ViewContentDisplayProperty<?> property) {
        assert property != null;
        this.properties.put(property.getPropertyName(), property);
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        return new Memento();
    }

    @Override
    public <T> void setPropertyValue(String property, T value) {
        // TODO NoSuchPropertyException extends RuntimeException
        if (!properties.containsKey(property)) {
            throw new IllegalArgumentException("Property '" + property
                    + "' does not exist.");
        }

        getProperty(property).setValue(value);
    }

    @Override
    public void startRestore() {
        restoring = true;
    }

}
