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
package org.thechiselgroup.choosel.visualization_component.text.client;

import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.core.client.views.ViewItem;

public class TextItem {

    public static final String CSS_HIGHLIGHTED = "textItemHighlighted";

    public static final String CSS_PARTIALLY_HIGHLIGHTED = "textItemPartiallyHighlighted";

    public static final String CSS_LIST = "textItem";

    public static final String CSS_SELECTED = "textItemSelected";

    private TextItemLabel label;

    private ViewItem resourceItem;

    /**
     * Flag that marks if the label of this text item has already been added to
     * the container panel. Used to increase the performance of adding multiple
     * text items to the view.
     */
    private boolean addedToPanel = false;

    private String lastFontSize;

    private String cachedDescription;

    public TextItem(ViewItem resourceItem) {
        assert resourceItem != null;

        this.resourceItem = resourceItem;
    }

    public String getDescriptionValue() {
        return (String) resourceItem
                .getSlotValue(TextVisualization.DESCRIPTION_SLOT);
    }

    public double getFontSizeValue() {
        return ((Double) resourceItem
                .getSlotValue(TextVisualization.FONT_SIZE_SLOT))
                .doubleValue();
    }

    public TextItemLabel getLabel() {
        return label;
    }

    public ViewItem getResourceItem() {
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

    /**
     * <p>
     * <b>IMPLEMENTATION NOTE</b>: the last calculated font size gets cached and
     * is compared to the result of the current font size calculation to prevent
     * expensive DOM styling operations.
     * </p>
     */
    public void scaleFont(DoubleToGroupValueMapper<String> groupValueMapper) {
        String newFontSizeLabelValue = groupValueMapper
                .getGroupValue(getFontSizeValue());

        setFontSize(newFontSizeLabelValue);
    }

    public void setAddedToPanel(boolean addedToPanel) {
        this.addedToPanel = addedToPanel;
    }

    public void setFontSize(String newFontSize) {
        if (lastFontSize == null || newFontSize.compareTo(lastFontSize) != 0) {
            label.setFontSize(newFontSize);
            lastFontSize = newFontSize;
        }
    }

    public void updateContent() {
        // TODO what is this for
        if (label == null) {
            return;
        }

        /*
         * PERFORMANCE: cache description and font size and only update UI
         * elements when there is a change. This makes a huge difference with
         * several thousand text items.
         */
        String description = getDescriptionValue();

        if (cachedDescription == null || !cachedDescription.equals(description)) {
            label.setText(description);
            cachedDescription = description;
        }
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