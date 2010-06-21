/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.client.resources.ui;

import org.thechiselgroup.choosel.client.resources.DelegatingResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainer;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainerChangedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainerChangedEventHandler;
import org.thechiselgroup.choosel.client.resources.UnmodifiableResourceSet;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class HighlightingResourceSetAvatarFactory extends
        DelegatingResourceSetAvatarFactory {

    private final ResourceSetAvatarDragController dragController;

    private ResourceSet hoverModel;

    private ResourceSetContainer setHoverModel;

    public HighlightingResourceSetAvatarFactory(
            ResourceSetAvatarFactory delegate, ResourceSet hoverModel,
            ResourceSetContainer setHoverModel,
            ResourceSetAvatarDragController dragController) {

        super(delegate);

        assert hoverModel != null;
        assert setHoverModel != null;
        assert dragController != null;

        this.dragController = dragController;
        this.hoverModel = hoverModel;
        this.setHoverModel = setHoverModel;
    }

    public void addDragHandler(DragHandler handler) {
        dragController.addDragHandler(handler);
    }

    private void addToHover(ResourceSetAvatar avatar) {
        hoverModel.addAll(avatar.getResourceSet());
        setHoverModel.setResourceSet(avatar.getResourceSet());
    }

    @Override
    public ResourceSetAvatar createAvatar(ResourceSet resources) {
        final ResourceSetAvatar avatar = delegate.createAvatar(resources);

        final HandlerRegistration mouseOverHandlerRegistration = avatar
                .addMouseOverHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        addToHover(avatar);
                    }
                });
        final HandlerRegistration mouseOutHandlerRegistration = avatar
                .addMouseOutHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        removeFromHover(avatar);
                    }
                });
        final HandlerRegistration containerChangedHandler = setHoverModel
                .addResourceSetContainerChangedEventHandler(new ResourceSetContainerChangedEventHandler() {
                    @Override
                    public void onResourceSetContainerChanged(
                            ResourceSetContainerChangedEvent event) {

                        avatar.setHover(shouldHighlight(avatar,
                                event.getResourceSet()));
                    }

                });

        final DragHandlerAdapter dragHandler = new DragHandlerAdapter() {
            @Override
            public void onDragStart(DragStartEvent event) {
                removeFromHover(avatar);
            }

        };
        addDragHandler(dragHandler);

        avatar.addDisposable(new Disposable() {
            @Override
            public void dispose() {
                removeDragHandler(dragHandler);
                mouseOverHandlerRegistration.removeHandler();
                mouseOutHandlerRegistration.removeHandler();
                containerChangedHandler.removeHandler();
            }
        });

        return avatar;
    }

    public void removeDragHandler(DragHandler handler) {
        dragController.removeDragHandler(handler);
    }

    private void removeFromHover(ResourceSetAvatar avatar) {
        hoverModel.removeAll(avatar.getResourceSet());
        setHoverModel.setResourceSet(null);
    }

    private boolean shouldHighlight(ResourceSetAvatar avatar,
            ResourceSet resources) {
        ResourceSet dragAvatarResources = avatar.getResourceSet();

        while (dragAvatarResources instanceof UnmodifiableResourceSet) {
            dragAvatarResources = ((DelegatingResourceSet) dragAvatarResources)
                    .getDelegate();
        }

        while (resources instanceof UnmodifiableResourceSet) {
            resources = ((DelegatingResourceSet) resources).getDelegate();
        }

        return resources == dragAvatarResources;
    }
}
