package org.thechiselgroup.choosel.visualization_component.text.client;

import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.initialization.ViewContentDisplayFactory;

public class TagCloudViewContentDisplayFactory implements
        ViewContentDisplayFactory {

    public static final String ID = "org.thechiselgroup.choosel.visualization_component.TagCloud";

    @Override
    public ViewContentDisplay createViewContentDisplay() {
        return new TextVisualization();
    }

    @Override
    public String getViewContentTypeID() {
        return ID;
    }

}
