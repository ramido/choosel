package org.thechiselgroup.choosel.visualization_component.text.client;

import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.initialization.ViewContentDisplayFactory;

public class NonTagCloudTextViewContentDisplayFactory implements
        ViewContentDisplayFactory {

    public static final String ID = "org.thechiselgroup.choosel.visualization_component.text.client.NonTagCloudTextViewContentDisplayFactory";

    @Override
    public ViewContentDisplay createViewContentDisplay() {
        TextVisualization visualization = new TextVisualization();
        visualization.setTagCloud(false);
        return visualization;
    }

    @Override
    public String getViewContentTypeID() {
        return ID;
    }

}
