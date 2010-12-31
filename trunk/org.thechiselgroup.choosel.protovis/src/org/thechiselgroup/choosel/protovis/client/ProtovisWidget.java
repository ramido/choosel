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
package org.thechiselgroup.choosel.protovis.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class ProtovisWidget extends Widget {

    private PVPanel pvPanel;

    public ProtovisWidget() {
        setElement(DOM.createDiv());
    }

    protected Element addDescriptionElement(int topPx, int leftPx, String html,
            String cssClass) {

        Element div = DOM.createDiv();
        div.setInnerHTML(html);
        Style style = div.getStyle();
        style.setTop(topPx, Unit.PX);
        style.setLeft(leftPx, Unit.PX);
        style.setPosition(Position.ABSOLUTE);

        if (cssClass != null) {
            div.setClassName(cssClass);
        }

        getElement().appendChild(div);
        return div;
    }

    public PVPanel getPVPanel() {
        return pvPanel;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        getElement().getStyle().setPosition(Position.RELATIVE);
    }

    /**
     * Initializes the Protovis panel. The panel uses the element of this widget
     * and replaces any previous visualization rendered in this widget.
     */
    public void initPVPanel() {
        pvPanel = PVPanel.create(getElement());
    }

    public boolean isInitialized() {
        return pvPanel != null;
    }

}