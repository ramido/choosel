package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.util.Initializable;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ResourceModelPresenter implements Initializable, WidgetAdaptable {

    private final ResourceSetsPresenter inputResourceSetsPresenter;

    private final ResourceSetsPresenter allResourcesPresenter;

    private final ResourceModel resourceModel;

    public ResourceModelPresenter(ResourceSetsPresenter allResourcesPresenter,
            ResourceSetsPresenter inputResourceSetsPresenter,
            ResourceModel resourceModel) {

        this.allResourcesPresenter = allResourcesPresenter;
        this.inputResourceSetsPresenter = inputResourceSetsPresenter;
        this.resourceModel = resourceModel;
    }

    @Override
    public Widget asWidget() {
        HorizontalPanel widget = new HorizontalPanel();
        widget.add(allResourcesPresenter.asWidget());
        widget.add(inputResourceSetsPresenter.asWidget());
        return widget;
    }

    @Override
    public void init() {
        initAllResourcePresenter();
        initInputResourceSetsPresenter();
    }

    private void initAllResourcePresenter() {
        allResourcesPresenter.init();
        allResourcesPresenter.addResourceSet(resourceModel.getResources());
    }

    private void initInputResourceSetsPresenter() {
        inputResourceSetsPresenter.init();

        resourceModel.getCombinedUserResourceSets().addSetEventsHandler(
                ResourceSetAddedEvent.TYPE, new ResourceSetAddedEventHandler() {
                    @Override
                    public void onResourceSetAdded(ResourceSetAddedEvent e) {
                        ResourceSet resources = e.getResourceSet();
                        inputResourceSetsPresenter.addResourceSet(resources);
                    }
                });
        resourceModel.getCombinedUserResourceSets().addSetEventsHandler(
                ResourceSetRemovedEvent.TYPE,
                new ResourceSetRemovedEventHandler() {
                    @Override
                    public void onResourceSetRemoved(ResourceSetRemovedEvent e) {
                        ResourceSet resources = e.getResourceSet();
                        inputResourceSetsPresenter.removeResourceSet(resources);
                    }
                });
    }

}
