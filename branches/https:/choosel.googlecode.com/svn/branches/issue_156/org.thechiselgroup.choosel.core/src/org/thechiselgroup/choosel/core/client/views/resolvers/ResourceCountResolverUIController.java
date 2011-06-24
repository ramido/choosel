package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.user.client.ui.Widget;

public class ResourceCountResolverUIController implements
        ViewItemValueResolverUIController {

    public ResourceCountResolverUIController() {

    }

    /**
     * {@linkResourceCountResolver} does not have an associated @link{Widget}
     * with it
     */
    @Override
    public Widget asWidget() {
        return null;
    }

    /**
     * Since this class has no UI element associated with it, it should never
     * have to update the resolver
     */
    @Override
    public void update(LightweightList<ViewItem> viewItems) {
        return;
    }
}
