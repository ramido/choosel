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
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractViewContentDisplay implements ViewContentDisplay {

    private ViewContentDisplayCallback callback;

    private DetailsWidgetHelper detailsWidgetHelper;

    protected ResourceSet hoverModel;

    private PopupManagerFactory popupManagerFactory;

    private boolean restoring = false;

    private Widget widget;

    public AbstractViewContentDisplay(PopupManagerFactory popupManagerFactory,
	    DetailsWidgetHelper detailsWidgetHelper, ResourceSet hoverModel) {

	assert popupManagerFactory != null;

	this.popupManagerFactory = popupManagerFactory;
	this.detailsWidgetHelper = detailsWidgetHelper;
	this.hoverModel = hoverModel;
    }

    public Widget asWidget() {
	if (widget == null) {
	    widget = createWidget();
	}

	return widget;
    }

    @Override
    public void checkResize() {
    }

    protected PopupManager createPopupManager(final Layer layer,
	    final Resource resource) {

	final PropertyValueResolver resolver = layer
		.getResolver(SlotResolver.DESCRIPTION_SLOT);

	return createPopupManager(resource, resolver);
    }

    protected PopupManager createPopupManager(final Resource resource,
	    final PropertyValueResolver resolver) {

	WidgetFactory widgetFactory = new WidgetFactory() {
	    @Override
	    public Widget createWidget() {
		return detailsWidgetHelper.createDetailsWidget(resource,
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
    }

    @Override
    public boolean isReady() {
	return true;
    }

    public boolean isRestoring() {
	return restoring;
    }

    @Override
    public void startRestore() {
	restoring = true;
    }

}
