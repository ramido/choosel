package org.thechiselgroup.choosel.client.views;

import java.util.Collection;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DefaultResourceItem.Status;
import org.thechiselgroup.choosel.client.views.DefaultResourceItem.SubsetStatus;

public class TestResourceItem implements ResourceItem {

    private Object displayObject;

    @Override
    public Object getDisplayObject() {
        return displayObject;
    }

    @Override
    public ResourceSet getHighlightedResources() {
        return null;
    }

    @Override
    public Collection<Resource> getHighlightedSelectedResources() {
        return null;
    }

    @Override
    public HighlightingManager getHighlightingManager() {
        return null;
    }

    @Override
    public SubsetStatus getHighlightStatus() {
        return SubsetStatus.NONE;
    }

    @Override
    public PopupManager getPopupManager() {
        return null;
    }

    @Override
    public ResourceSet getResourceSet() {
        return null;
    }

    @Override
    public Object getResourceValue(String slotID) {
        return null;
    }

    @Override
    public Collection<Resource> getSelectedResources() {
        return null;
    }

    @Override
    public SubsetStatus getSelectionStatus() {
        return SubsetStatus.NONE;
    }

    @Override
    public Status getStatus() {
        return Status.DEFAULT;
    }

    @Override
    public void setDisplayObject(Object displayObject) {
        this.displayObject = displayObject;
    }

}