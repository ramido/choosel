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

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.IconURLFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;

public abstract class IconResourceItem extends ResourceItem {

    private String defaultIconURL;

    private String grayedOutIconURL;

    private String highlightIconURL;

    private String selectedIconURL;

    public IconResourceItem(ResourceSet resources, ResourceSet hoverModel,
            PopupManager popupManager, Layer layerModel) {

        super(resources, hoverModel, popupManager, layerModel);
        initIconURLs();
    }

    protected String getDefaultIconURL() {
        return defaultIconURL;
    }

    protected String getGrayedOutIconURL() {
        return grayedOutIconURL;
    }

    protected String getHighlightIconURL() {
        return highlightIconURL;
    }

    // TODO enable usage of border color (use different icons)
    private String getIconURL(String color) {
        String label = (String) getResourceValue(SlotResolver.LABEL_SLOT);
        return IconURLFactory.getFlatIconURL(label, color);
    }

    protected String getSelectedIconURL() {
        return selectedIconURL;
    }

    private void initIconURLs() {
        // TODO move colors to color provider
        // TODO use CSS, add border
        defaultIconURL = getIconURL((String) getResourceValue(SlotResolver.COLOR_SLOT));
        highlightIconURL = getIconURL("#FDF49A");
        grayedOutIconURL = getIconURL("#dddddd");
        selectedIconURL = getIconURL("#E7B076");
    }

}
