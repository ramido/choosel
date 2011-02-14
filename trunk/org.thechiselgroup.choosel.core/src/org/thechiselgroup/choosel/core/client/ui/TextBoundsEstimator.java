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

    public void applyFontSettings(Element prototypeElement) {
        String fontFamily = CSS.getComputedStyle(prototypeElement,
                CSS.FONT_FAMILY);
        String fontStyle = CSS.getComputedStyle(prototypeElement,
                CSS.FONT_STYLE);
        String fontWeight = CSS.getComputedStyle(prototypeElement,
                CSS.FONT_WEIGHT);
        String fontSize = CSS.getComputedStyle(prototypeElement, CSS.FONT_SIZE);

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

    public int getTextWidth(String text, Element prototype) {
        applyFontSettings(prototype);
        setText(text);
        return getTextWidth();
    }

    public void setText(String text) {
        estimatorElement.setInnerText(text);
    }

}
