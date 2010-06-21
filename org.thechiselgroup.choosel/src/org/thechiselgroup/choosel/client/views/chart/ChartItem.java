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
package org.thechiselgroup.choosel.client.views.chart;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Event;

public class ChartItem extends ResourceItem {

    public boolean highlighted = false;

    protected String[] colours = { "yellow", "orange", "steelblue" };

    private ChartViewContentDisplay view;

    private BarViewContentDisplay view1;

    private PieViewContentDisplay view2;

    private DotViewContentDisplay view3;

    private ScatterViewContentDisplay view4;

    public DragEnabler enabler;

    public ChartItem(ResourceSet resources, BarViewContentDisplay view1,
            PopupManager popupManager, ResourceSet hoverModel,
            Layer layerModel, DragEnablerFactory dragEnablerFactory) {

        super(resources, hoverModel, popupManager, layerModel);

        this.view1 = view1;
        enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public ChartItem(ResourceSet resources, ChartViewContentDisplay view,
            PopupManager popupManager, ResourceSet hoverModel,
            Layer layerModel, DragEnablerFactory dragEnablerFactory) {

        super(resources, hoverModel, popupManager, layerModel);

        this.view = view;
        enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public ChartItem(ResourceSet resources, DotViewContentDisplay view3,
            PopupManager popupManager, ResourceSet hoverModel,
            Layer layerModel, DragEnablerFactory dragEnablerFactory) {

        super(resources, hoverModel, popupManager, layerModel);

        this.view3 = view3;
        enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public ChartItem(ResourceSet resources, PieViewContentDisplay view2,
            PopupManager popupManager, ResourceSet hoverModel,
            Layer layerModel, DragEnablerFactory dragEnablerFactory) {

        super(resources, hoverModel, popupManager, layerModel);

        this.view2 = view2;
        enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public ChartItem(ResourceSet resources, ScatterViewContentDisplay view4,
            PopupManager popupManager, ResourceSet hoverModel,
            Layer layerModel, DragEnablerFactory dragEnablerFactory) {

        super(resources, hoverModel, popupManager, layerModel);

        this.view4 = view4;
        enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public String getColour() {
        switch (calculateStatus()) {
        case HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED:
            return colours[0];
        case GRAYED_OUT:
        case DEFAULT:
            return colours[2];
        case SELECTED:
            return colours[1];
        }
        throw new RuntimeException("No colour available");
    }

    public void onBrushEvent(boolean isBrushed) {
        if ((isSelected() && isBrushed) || (!isSelected() && !isBrushed)) {
            return;
        }
        if (view != null) {
            view.getCallback().switchSelection(getResourceSet());
        }
        if (view1 != null) {
            view1.getCallback().switchSelection(getResourceSet());
        }
        if (view2 != null) {
            view2.getCallback().switchSelection(getResourceSet());
        }
        if (view3 != null) {
            view3.getCallback().switchSelection(getResourceSet());
        }
        if (view4 != null) {
            view4.getCallback().switchSelection(getResourceSet());
        }
    }

    public void onEvent(Event e) {
        try {
            switch (e.getTypeInt()) {
            case Event.ONCLICK: {
                if (view != null) {
                    view.getCallback().switchSelection(getResourceSet());
                }
                if (view1 != null) {
                    view1.getCallback().switchSelection(getResourceSet());
                }
                if (view2 != null) {
                    view2.getCallback().switchSelection(getResourceSet());
                }
                if (view3 != null) {
                    view3.getCallback().switchSelection(getResourceSet());
                }
                if (view4 != null) {
                    view4.getCallback().switchSelection(getResourceSet());
                }
            }
                break;
            case Event.ONMOUSEMOVE: {
                popupManager.onMouseMove(e.getClientX(), e.getClientY());
                enabler.forwardMouseMove(e);
            }
                break;
            case Event.ONMOUSEDOWN: {
                popupManager.onMouseDown(e.getClientX(), e.getClientY());
                enabler.forwardMouseDownWithEventPosition(e);
            }
                break;
            case Event.ONMOUSEOUT: {
                popupManager.onMouseOut(e.getClientX(), e.getClientY());
                hoverModel.removeAll(getResourceSet());
                enabler.forwardMouseOut(e);
            }
                break;
            case Event.ONMOUSEOVER: {
                popupManager.onMouseOver(e.getClientX(), e.getClientY());
                hoverModel.addAll(getResourceSet());
            }
                break;
            case Event.ONMOUSEUP: {
                enabler.forwardMouseUp(e);
            }
                break;
            }
        } catch (RuntimeException ex) {
            Log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    protected void setStatusStyling(Status status) {
        if (view != null) {
            view.getChartWidget().renderChart();
        }
        if (view1 != null) {
            view1.getChartWidget().renderChart();
        }
        if (view2 != null) {
            view2.getChartWidget().renderChart();
        }
        if (view3 != null) {
            view3.getChartWidget().renderChart();
        }
        if (view4 != null) {
            view4.getChartWidget().renderChart();
        }
    }

}
