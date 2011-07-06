package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.user.client.ui.Widget;

public class EmptyWidgetUIController implements
        ViewItemValueResolverUIController {

    @Override
    public Widget asWidget() {
        return new Widget();
    }

    @Override
    public void update(LightweightCollection<ViewItem> viewItems) {
        return;
    }

}
