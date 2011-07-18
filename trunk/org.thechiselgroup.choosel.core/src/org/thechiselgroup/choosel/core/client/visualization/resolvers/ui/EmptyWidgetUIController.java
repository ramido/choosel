package org.thechiselgroup.choosel.core.client.visualization.resolvers.ui;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmptyWidgetUIController implements
        VisualItemValueResolverUIController {

    @Override
    public Widget asWidget() {
        return new Label("");
    }

    @Override
    public void update(LightweightCollection<VisualItem> viewItems) {
        return;
    }

}
