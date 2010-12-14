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
package org.thechiselgroup.choosel.client.ui.widget.protovis;

import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that returns a Protovis chart panel for drawing charts and offers a
 * callback mechanism for onAttach and onResize to allow for chart updates.
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public final class ChartWidget extends Widget implements WidgetAdaptable {

    private ChartWidgetCallback callback;

    public ChartWidget() {
        setElement(DOM.createDiv());
        CSS.setBackgroundColor(this, Colors.WHITE);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    // TODO check call tree and remove?
    public void checkResize() {
        callback.onResize(getOffsetWidth(), getOffsetHeight());
    }

    /**
     * Returns new chart panel. The chart panel uses the element of this widget
     * and replaces any previous chart rendered in this widget.
     */
    public Panel createChartPanel() {
        return Panel.createPanel().canvas(getElement());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        callback.onAttach();
    }

    public void setCallback(ChartWidgetCallback callback) {
        this.callback = callback;
    }

}