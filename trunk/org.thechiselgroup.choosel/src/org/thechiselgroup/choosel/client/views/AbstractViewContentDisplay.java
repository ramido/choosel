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

import java.util.Collections;
import java.util.List;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractViewContentDisplay implements ViewContentDisplay {

    protected ViewContentDisplayCallback callback;

    private DetailsWidgetHelper detailsWidgetHelper;

    private PopupManagerFactory popupManagerFactory;

    private boolean restoring = false;

    private Widget widget;

    public AbstractViewContentDisplay(PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper) {

        assert popupManagerFactory != null;

        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;
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

    private PopupManager createPopupManager(ResourceItemValueResolver resolver,
            ResourceSet resources) {

        return createPopupManager(resources,
                resolver.getResourceSetResolver(SlotResolver.DESCRIPTION_SLOT));
    }

    // for test
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

    @Override
    public final ResourceItem createResourceItem(
            ResourceItemValueResolver resolver, String category,
            ResourceSet resources, HoverModel hoverModel) {

        assert resolver != null;
        assert resources != null;

        return new ResourceItem(category, resources, hoverModel,
                createPopupManager(resolver, resources), resolver);
    }

    protected abstract Widget createWidget();

    @Override
    public void dispose() {
        callback = null;
        detailsWidgetHelper = null;
        popupManagerFactory = null;

        if (widget instanceof Disposable) {
            ((Disposable) widget).dispose();
        }
        widget = null;
    }

    @Override
    public void endRestore() {
        restoring = false;
    }

    @Override
    public List<ViewContentDisplayAction> getActions() {
        return Collections.emptyList();
    }

    public ViewContentDisplayCallback getCallback() {
        return callback;
    }

    @Override
    public List<ViewContentDisplayConfiguration> getConfigurations() {
        return Collections.emptyList();
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    public boolean isRestoring() {
        return restoring;
    }

    @Override
    public final void removeResourceItem(ResourceItem resourceItem) {
    }

    @Override
    public void startRestore() {
        restoring = true;
    }

}
