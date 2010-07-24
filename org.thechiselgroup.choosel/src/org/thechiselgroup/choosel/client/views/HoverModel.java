package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.CountingResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.DelegatingReadableResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetDelegateChangedEventHandler;
import org.thechiselgroup.choosel.client.resources.SwitchingResourceSet;

import com.google.gwt.event.shared.HandlerRegistration;

public class HoverModel extends DelegatingReadableResourceSet {

    private SwitchingResourceSet highlightedResourceSetContainer;

    private ResourceSet highlightedSingleResources;

    public HoverModel() {
        super(new CombinedResourceSet(new DefaultResourceSet()));

        highlightedResourceSetContainer = new SwitchingResourceSet();

        /*
         * TODO We could a counting resource set, because elements might get
         * removed from the set after they have been added again, e.g. when
         * moving the mouse from over a resource item with popup to over a
         * resource set and the popup removes the resource a bit later.
         */
        highlightedSingleResources = new CountingResourceSet();

        getCombinedResourceSet()
                .addResourceSet(highlightedResourceSetContainer);
        getCombinedResourceSet().addResourceSet(highlightedSingleResources);
    }

    public HandlerRegistration addEventHandler(
            ResourceSetDelegateChangedEventHandler handler) {

        return highlightedResourceSetContainer.addEventHandler(handler);
    }

    public void addHighlightedResources(ResourceSet resource) {
        highlightedSingleResources.addAll(resource);
    }

    private CombinedResourceSet getCombinedResourceSet() {
        return (CombinedResourceSet) delegate;
    }

    public void removeHighlightedResources(ResourceSet resources) {
        highlightedSingleResources.removeAll(resources);
    }

    public void setHighlightedResourceSet(ResourceSet resourceSet) {
        highlightedResourceSetContainer.setDelegate(resourceSet);
    }

}