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
package org.thechiselgroup.choosel.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DialogPanel extends Panel {

    private static final int BUTTON_BAR_CSS_MARGIN = 4;

    public static final String CSS_DIALOG_PANEL = "choosel-DialogPanel";

    public static final String CSS_DIALOG_PANEL_BUTTONBAR = "choosel-DialogPanel-ButtonBar";

    public static final String CSS_DIALOG_PANEL_HEADER = "choosel-DialogPanel-Header";

    public static final String CSS_DIALOG_PANEL_CONTENT = "choosel-DialogPanel-Content";

    private Label headerLabel;

    private Element panelElement;

    private FlowPanel buttonBar;

    private Widget contentWidget;

    public DialogPanel() {
        panelElement = DOM.createDiv();
        panelElement.setClassName(CSS_DIALOG_PANEL);

        initHeaderLabel();
        initButtonBar();

        setElement(panelElement);
    }

    public Button createButton(String label) {
        Button button = new Button(label);
        buttonBar.add(button);
        return button;
    }

    // TODO CSS
    public void initButtonBar() {
        buttonBar = new FlowPanel();
        buttonBar.setStyleName(CSS_DIALOG_PANEL_BUTTONBAR);
        panelElement.appendChild(buttonBar.getElement());
        adopt(buttonBar);
    }

    // TODO set CSS style
    public void initHeaderLabel() {
        headerLabel = new Label();
        headerLabel.setStyleName(CSS_DIALOG_PANEL_HEADER);
        panelElement.appendChild(headerLabel.getElement());
        adopt(headerLabel);
    }

    @Override
    public Iterator<Widget> iterator() {
        return new Iterator<Widget>() {

            private int index = 0;

            private Widget getWidget(int index) {
                int count = 0;

                if (headerLabel != null) {
                    if (index == count) {
                        return headerLabel;
                    }
                    count++;
                }

                if (contentWidget != null) {
                    if (index == count) {
                        return contentWidget;
                    }
                    count++;
                }

                if (buttonBar != null) {
                    if (index == count) {
                        return buttonBar;
                    }
                    count++;
                }

                return null;
            }

            @Override
            public boolean hasNext() {
                return getWidget(index) != null;
            }

            @Override
            public Widget next() {
                Widget w = getWidget(index);
                index++;
                return w;
            }

            @Override
            public void remove() {
                int count = 0;

                if (headerLabel != null) {
                    if (index - 1 == count) {
                        removeHeaderLabel();
                    }
                    count++;
                }

                if (contentWidget != null) {
                    if (index - 1 == count) {
                        removeContentWidget();
                    }
                    count++;
                }

                if (buttonBar != null) {
                    if (index - 1 == count) {
                        removeButtonBar();
                    }
                    count++;
                }

                index--;
            }

        };
    }

    @Override
    public boolean remove(Widget child) {
        if (!(child == buttonBar || child == contentWidget || child == headerLabel)) {
            return false;
        }

        if (child == contentWidget) {
            removeContentWidget();
            return true;
        }

        if (child == buttonBar) {
            removeButtonBar();
            return true;
        }

        if (child == headerLabel) {
            removeHeaderLabel();
            return true;
        }

        return false;
    }

    private void removeButtonBar() {
        try {
            orphan(buttonBar);
        } finally {
            panelElement.removeChild(buttonBar.getElement());
            buttonBar = null;
        }
    }

    private void removeContentWidget() {
        try {
            orphan(contentWidget);
        } finally {
            panelElement.removeChild(contentWidget.getElement());
            contentWidget = null;
        }
    }

    private void removeHeaderLabel() {
        try {
            orphan(headerLabel);
        } finally {
            panelElement.removeChild(headerLabel.getElement());
            headerLabel = null;
        }
    }

    // TODO code from simple panel
    public void setContent(Widget contentWidget) {
        assert contentWidget != null;
        this.contentWidget = contentWidget;
        contentWidget.addStyleName(CSS_DIALOG_PANEL_CONTENT);
        panelElement.insertBefore(contentWidget.getElement(),
                buttonBar.getElement());
        adopt(contentWidget);
    }

    public void setHeader(String header) {
        headerLabel.setText(header);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);

        int contentHeight = getOffsetHeight() - headerLabel.getOffsetHeight()
                - buttonBar.getOffsetHeight() - BUTTON_BAR_CSS_MARGIN;

        if (contentHeight < 0) {
            contentHeight = 0;
        }

        contentWidget.setHeight(contentHeight + CSS.PX);
    }
}
