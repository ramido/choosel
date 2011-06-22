package org.thechiselgroup.choosel.visualization_component.heat_bars.client;

import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplayFactory;

public class HeatBarsViewContentDisplayFactory implements
        ViewContentDisplayFactory {

    @Override
    public ViewContentDisplay createViewContentDisplay() {
        return new HeatBars();
    }

    @Override
    public String getViewContentTypeID() {
        return HeatBars.ID;
    }

}
