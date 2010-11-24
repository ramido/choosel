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

import java.util.Set;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

public class TestView extends DefaultView {

    private final PopupManager popupManager;

    public TestView(ResourceSplitter resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, ResourceItemValueResolver configuration,
            SelectionModel selectionModel, Presenter selectionModelPresenter,
            ResourceModel resourceModel, Presenter resourceModelPresenter,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper, ViewSaver viewPersistence,
            PopupManager popupManager) {

        super(resourceSplitter, contentDisplay, label, contentType,
                configuration, selectionModel, selectionModelPresenter,
                resourceModel, resourceModelPresenter, hoverModel,
                popupManagerFactory, detailsWidgetHelper, viewPersistence);

        this.popupManager = popupManager;
    }

    @Override
    protected PopupManager createPopupManager(ResourceSet resources) {
        return popupManager;
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void updateConfiguration(Set<ResourceItem> addedResourceItems) {
    }
}