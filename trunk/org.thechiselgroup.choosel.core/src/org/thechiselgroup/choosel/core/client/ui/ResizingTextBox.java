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

import org.thechiselgroup.choosel.core.client.util.math.MathUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ResizingTextBox extends TextBox {

    private static final String CSS_TEXT_WIDTH_ESTIMATOR = "choosel-TextWidthEstimator";

    /*
     * The extra width should roughly accommodate another character, because the
     * text is extended when a new character is inserted and this might cause
     * the text to move.
     */
    private static final int EXTRA_WIDTH = 15;

    // TODO move to CSS class
    /**
     * Returns the actual (computed) style for an element. This is necessary,
     * because the regular style accessor returns an empty String if the style
     * was not explicitly set on the corresponding element.
     */
    // @formatter:off
    private static native String getStyle(Element el, String styleProp)/*-{
        var camelize = function (str) {
          return str.replace(/\-(\w)/g, function(str, letter){
            return letter.toUpperCase();
          });
        };

        if (el.currentStyle) {
          return el.currentStyle[camelize(styleProp)];
        } else if ($wnd.document.defaultView && $wnd.document.defaultView.getComputedStyle) {
          return $wnd.document.defaultView.getComputedStyle(el,null).getPropertyValue(styleProp);
        } else {
          return el.style[camelize(styleProp)]; 
        }
    }-*/;
    // @formatter:on

    private final int minWidth;

    private final int maxWidth;

    public ResizingTextBox(int minWidth, int maxWidth) {
        assert minWidth >= 0;
        assert maxWidth >= minWidth;

        this.minWidth = minWidth;
        this.maxWidth = maxWidth;

        addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                updateWidth();
            }
        });
        addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                // TODO we could do more here, e.g. simulate delete & backspace
                // filters out backspace, arrows etc
                if (event.getCharCode() > 46) {
                    updateWidth(getText() + event.getCharCode());
                } else {
                    updateWidth();
                }

            }
        });

        // needed for copy / paste / delete etc.
        addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                updateWidth();
            }
        });
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        updateWidth();
    }

    @Override
    public void setText(String text) {
        updateWidth();
        super.setText(text);
    }

    private void updateWidth() {
        updateWidth(getText());
    }

    private void updateWidth(String text) {
        // create invisible div with no padding, no margin, no border
        Element estimatorElement = DOM.createSpan();
        estimatorElement.setClassName(CSS_TEXT_WIDTH_ESTIMATOR);

        // set font-family and font-size
        DOM.setStyleAttribute(estimatorElement, "fontFamily",
                getStyle(getElement(), "font-family"));
        DOM.setStyleAttribute(estimatorElement, "fontStyle",
                getStyle(getElement(), "font-style"));
        DOM.setStyleAttribute(estimatorElement, "fontWeight",
                getStyle(getElement(), "font-weight"));
        DOM.setStyleAttribute(estimatorElement, "fontSize",
                getStyle(getElement(), "font-size"));
        // XXX this might be required?
        // DOM.setStyleAttribute(span, "borderLeftWidth",
        // getStyle(getElement(), "border-left-width"));
        // DOM.setStyleAttribute(span, "borderRightWidth",
        // getStyle(getElement(), "border-right-width"));
        // DOM.setStyleAttribute(span, "paddingLeft",
        // getStyle(getElement(), "padding-left"));
        // DOM.setStyleAttribute(span, "paddingRight",
        // getStyle(getElement(), "padding-right"));

        estimatorElement.setInnerText(text);

        RootPanel.get().getElement().appendChild(estimatorElement);
        int width = estimatorElement.getOffsetWidth();
        RootPanel.get().getElement().removeChild(estimatorElement);

        // we add some extra width for the focus indicator etc
        width += EXTRA_WIDTH;

        width = MathUtils.restrictToInterval(width, minWidth, maxWidth);
        CSS.setWidth(this, width);
    }

}