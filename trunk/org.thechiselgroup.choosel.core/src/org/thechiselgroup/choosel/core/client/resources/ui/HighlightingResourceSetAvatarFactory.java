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
package org.thechiselgroup.choosel.core.client.resources.ui;

import org.thechiselgroup.choosel.core.client.resources.DelegatingResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetDelegateChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetDelegateChangedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.UnmodifiableResourceSet;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.views.HoverModel;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class HighlightingResourceSetAvatarFactory extends
        DelegatingResourceSetAvatarFactory {

    private final ResourceSetAvatarDragController dragController;

    private HoverModel hoverModel;

    public HighlightingResourceSetAvatarFactory(
            ResourceSetAvatarFactory delegate, HoverModel hoverModel,
            ResourceSetAvatarDragController dragController) {

        super(delegate);

        assert hoverModel != null;
        assert dragController != null;

        this.dragController = dragController;
        this.hoverModel = hoverModel;
    }

    public void addDragHandler(DragHandler handler) {
        dragController.addDragHandler(handler);
    }

    private void addToHover(ResourceSetAvatar avatar) {
        hoverModel.setHighlightedResourceSet(avatar.getResourceSet());
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
                        removeFromHover();
                    }
                });
        final HandlerRegistration containerChangedHandler = hoverModel
                .addEventHandler(new ResourceSetDelegateChangedEventHandler() {
                    @Override
                    public void onResourceSetContainerChanged(
                            ResourceSetDelegateChangedEvent event) {

                        avatar.setHover(shouldHighlight(avatar,
                                event.getResourceSet()));
                    }

                });

        /**
         * Removes the hover at the end of a drag and drop operation. Because
         * the resource set is already hovered, this saves the effort of
         * highlighting the resources again.
         */
        final DragHandlerAdapter dragHandler = new DragHandlerAdapter() {
            @Override
            public void onDragEnd(DragEndEvent event) {
                removeFromHover();
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

    private void removeFromHover() {
        hoverModel.setHighlightedResourceSet(null);
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
