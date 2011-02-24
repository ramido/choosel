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
package org.thechiselgroup.choosel.core.client.ui;

import java.util.Collection;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class TextBoundsEstimator {

    private Element estimatorElement;

    private Element rootElement;

    public TextBoundsEstimator() {
        this.estimatorElement = createEstimatorElement();
        this.rootElement = RootPanel.get().getElement();
    }

    public TextBoundsEstimator(Element prototype) {
        this();
        applyFontSettings(prototype);
    }

    public TextBoundsEstimator(String fontFamily, String fontStyle,
            String fontWeight, String fontSize) {
        this();
        applyFontSettings(fontFamily, fontStyle, fontWeight, fontSize);
    }

    public void applyFontSettings(Element prototype) {
        String fontFamily = CSS.getComputedStyle(prototype, CSS.FONT_FAMILY);
        String fontStyle = CSS.getComputedStyle(prototype, CSS.FONT_STYLE);
        String fontWeight = CSS.getComputedStyle(prototype, CSS.FONT_WEIGHT);
        String fontSize = CSS.getComputedStyle(prototype, CSS.FONT_SIZE);

        applyFontSettings(fontFamily, fontStyle, fontWeight, fontSize);
    }

    public void applyFontSettings(String fontFamily, String fontStyle,
            String fontWeight, String fontSize) {

        CSS.setFontFamily(estimatorElement, fontFamily);
        CSS.setFontStyle(estimatorElement, fontStyle);
        CSS.setFontWeight(estimatorElement, fontWeight);
        CSS.setFontSize(estimatorElement, fontSize);
    }

    private Element createEstimatorElement() {
        Element estimatorElement = DOM.createSpan();

        CSS.setPosition(estimatorElement, CSS.ABSOLUTE);
        CSS.setLeft(estimatorElement, 0);
        CSS.setTop(estimatorElement, 0);
        CSS.setZIndex(estimatorElement, -1000);
        CSS.setBorder(estimatorElement, "0px none");
        CSS.setPadding(estimatorElement, 0);
        CSS.setMargin(estimatorElement, 0);

        return estimatorElement;
    }

    public int getTextWidth() {
        rootElement.appendChild(estimatorElement);
        int width = estimatorElement.getOffsetWidth();
        rootElement.removeChild(estimatorElement);
        return width;
    }

    public int getTextWidth(String text) {
        setText(text);
        return getTextWidth();
    }

    public int getTextWidth(String text, Element prototype) {
        applyFontSettings(prototype);
        return getTextWidth(text);
    }

    /**
     * Calculates the text widths for the texts and returns a mapping of the
     * texts to their widths.
     */
    public Map<String, Integer> getTextWidths(Collection<String> texts) {
        assert texts != null;

        Map<String, Integer> result = CollectionFactory.createStringMap();
        for (String value : texts) {
            setText(value);
            result.put(value, getTextWidth());
        }
        return result;
    }

    public void setText(String text) {
        estimatorElement.setInnerText(text);
    }

}
