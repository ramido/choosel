package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

/**
 * This interface is used to return new instances of a
 * {@link ViewItemValueResolver} . This is used so that the application can
 * create new instances on the fly as users create the need for them
 */
public interface ViewItemValueResolverFactory {

    ViewItemValueResolver create();

    String getId();

    String getLabel();

    boolean isApplicable(Slot slot, LightweightList<ViewItem> viewItems);

}
