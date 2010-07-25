package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingHandler;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class HighlightingManager implements MouseOverHandler, MouseOutHandler,
        PopupClosingHandler {

    private boolean highlighted = false;

    private ResourceSet resources;

    private HoverModel hoverModel;

    public HighlightingManager(HoverModel hoverModel, ResourceSet resources) {
        this.hoverModel = hoverModel;
        this.resources = resources;
    }

    @Override
    public void onMouseOut(MouseOutEvent e) {
        setHighlighting(false);
    }

    @Override
    public void onMouseOver(MouseOverEvent e) {
        setHighlighting(true);
    }

    @Override
    public void onPopupClosing(PopupClosingEvent event) {
        setHighlighting(false);
    }

    private void setHighlighting(boolean shouldHighlight) {
        if (shouldHighlight == highlighted) {
            return;
        }

        if (shouldHighlight) {
            hoverModel.addHighlightedResources(resources);
        } else {
            hoverModel.removeHighlightedResources(resources);
        }

        highlighted = shouldHighlight;
    }
}