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

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.HandlerRegistrationSet;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.name.Named;

public abstract class AbstractViewContentDisplay implements ViewContentDisplay {

    protected ViewContentDisplayCallback callback;

    private DetailsWidgetHelper detailsWidgetHelper;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    protected ResourceSet hoverModel;

    private PopupManagerFactory popupManagerFactory;

    private boolean restoring = false;

    private Widget widget;

    public AbstractViewContentDisplay(PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel) {

        assert popupManagerFactory != null;
        assert hoverModel != null;

        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;
        this.hoverModel = hoverModel;
    }

    @Override
    public Widget asWidget() {
        if (widget == null) {
            widget = createWidget();
        }

        return widget;
    }

    @Override
    public void checkResize() {
    }

    protected PopupManager createPopupManager(ResourceItemValueResolver layer,
            ResourceSet resource) {

        // TODO mah
        return createPopupManager(resource,
                layer.getResourceSetResolver(SlotResolver.DESCRIPTION_SLOT));
    }

    protected PopupManager createPopupManager(final ResourceSet resources,
            final ResourceSetToValueResolver resolver) {

        WidgetFactory widgetFactory = new WidgetFactory() {
            @Override
            public Widget createWidget() {
                return detailsWidgetHelper.createDetailsWidget(resources,
                        resolver);
            }
        };

        return popupManagerFactory.createPopupManager(widgetFactory);
    }

    protected abstract Widget createWidget();

    @Override
    public void dispose() {
        callback = null;
        detailsWidgetHelper = null;
        hoverModel = null;
        popupManagerFactory = null;

        handlerRegistrations.dispose();
        handlerRegistrations = null;

        if (widget instanceof Disposable) {
            ((Disposable) widget).dispose();
        }
        widget = null;
    }

    @Override
    public void endRestore() {
        restoring = false;
    }

    public ViewContentDisplayCallback getCallback() {
        return callback;
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        this.callback = callback;

        initHoverModelHooks();
    }

    private void initHoverModelHooks() {
        handlerRegistrations.addHandlerRegistration(hoverModel.addHandler(
                ResourcesAddedEvent.TYPE, new ResourcesAddedEventHandler() {
                    @Override
                    public void onResourcesAdded(ResourcesAddedEvent e) {
                        showHover(e.getAddedResources(), true);
                    }

                }));
        handlerRegistrations.addHandlerRegistration(hoverModel.addHandler(
                ResourcesRemovedEvent.TYPE, new ResourcesRemovedEventHandler() {
                    @Override
                    public void onResourcesRemoved(ResourcesRemovedEvent e) {
                        showHover(e.getRemovedResources(), false);
                    }
                }));
    }

    @Override
    public boolean isReady() {
        return true;
    }

    public boolean isRestoring() {
        return restoring;
    }

    protected void showHover(List<Resource> resources, boolean showHover) {
        for (Resource resource : resources) {
            if (!callback.containsResource(resource)) {
                return;
            }

            List<ResourceItem> resourceItems = callback
                    .getResourceItems(resource);
            for (ResourceItem resourceItem : resourceItems) {
                resourceItem.setHighlighted(showHover);
            }
        }
    }

    @Override
    public void startRestore() {
        restoring = true;
    }

}
