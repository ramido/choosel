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
package org.thechiselgroup.choosel.client.views.text;

import java.util.List;

import org.thechiselgroup.choosel.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotResolver;

public class TextItem {

    public static final String CSS_HIGHLIGHTED = "textItemHighlighted";

    public static final String CSS_PARTIALLY_HIGHLIGHTED = "textItemPartiallyHighlighted";

    public static final String CSS_LIST = "textItem";

    public static final String CSS_SELECTED = "textItemSelected";

    private TextItemLabel label;

    private ResourceItem resourceItem;

    /**
     * Flag that marks if the label of this text item has already been added to
     * the container panel. Used to increase the performance of adding multiple
     * text items to the view.
     */
    private boolean addedToPanel = false;

    private double fontSizeValue;

    public TextItem(ResourceItem resourceItem) {
        assert resourceItem != null;

        this.resourceItem = resourceItem;
    }

    public double getFontSizeValue() {
        return fontSizeValue;
    }

    public TextItemLabel getLabel() {
        return label;
    }

    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    public void init(TextItemLabel label) {
        this.label = label;

        label.addStyleName(CSS_LIST);

        DefaultPopupManager.linkManagerToSource(resourceItem.getPopupManager(),
                getLabel());

        updateContent();
    }

    public boolean isAddedToPanel() {
        return addedToPanel;
    }

    public void scaleFont(List<Double> fontSizeValues,
            DoubleToGroupValueMapper<String> groupValueMapper) {

        label.setFontSize(groupValueMapper.getGroupValue(getFontSizeValue(),
                fontSizeValues));
    }

    public void setAddedToPanel(boolean addedToPanel) {
        this.addedToPanel = addedToPanel;
    }

    public void setFontSizeValue(double value) {
        fontSizeValue = value;
    }

    public void updateContent() {
        // TODO what is this for
        if (label == null) {
            return;
        }

        label.setText((String) resourceItem
                .getResourceValue(SlotResolver.DESCRIPTION_SLOT));

        double fontSizeValue = ((Double) resourceItem
                .getResourceValue(SlotResolver.FONT_SIZE_SLOT)).doubleValue();

        setFontSizeValue(fontSizeValue);
    }

    public void updateStatusStyling() {
        switch (resourceItem.getHighlightStatus()) {
        case COMPLETE: {
            label.addStyleName(CSS_HIGHLIGHTED);
            label.removeStyleName(CSS_PARTIALLY_HIGHLIGHTED);
        }
            break;
        case PARTIAL: {
            label.removeStyleName(CSS_HIGHLIGHTED);
            label.addStyleName(CSS_PARTIALLY_HIGHLIGHTED);
        }
            break;
        case NONE: {
            label.removeStyleName(CSS_HIGHLIGHTED);
            label.removeStyleName(CSS_PARTIALLY_HIGHLIGHTED);
        }
            break;
        }

        switch (resourceItem.getSelectionStatus()) {
        case COMPLETE: {
            label.addStyleName(CSS_SELECTED);
        }
            break;
        case PARTIAL: {
            label.addStyleName(CSS_SELECTED);
        }
            break;
        case NONE: {
            label.removeStyleName(CSS_SELECTED);
        }
            break;
        }
    }
}