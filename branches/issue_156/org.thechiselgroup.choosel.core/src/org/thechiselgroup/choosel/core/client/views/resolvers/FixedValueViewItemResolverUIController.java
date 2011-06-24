package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.user.client.ui.Widget;

public class FixedValueViewItemResolverUIController implements
        ViewItemValueResolverUIController {

    /**
     * This class has no associated UI with it
     */
    @Override
    public Widget asWidget() {
        return null;
    }

    /**
     * Since there is no associated UI with this class, you will never need to
     * update the resolver
     */
    @Override
    public void update(LightweightList<ViewItem> viewItems) {
        return;
    }

}
