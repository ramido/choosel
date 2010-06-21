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

import org.thechiselgroup.choosel.client.resources.ResourceAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.event.shared.HandlerRegistration;

public class DisableResourceSetAvatarIfEmptyManager implements
        ResourceAddedEventHandler, ResourceRemovedEventHandler,
        ResourceSetAvatarResourcesChangedEventHandler, Disposable {

    private HandlerRegistration addedHandlerRegistration;

    private final ResourceSetAvatar avatar;

    private HandlerRegistration removedHandlerRegistration;

    private HandlerRegistration avatarChangeHandlerRegistration;

    public DisableResourceSetAvatarIfEmptyManager(ResourceSetAvatar avatar) {
        assert avatar != null;
        this.avatar = avatar;
    }

    private void deregisterResourceSetHandlers() {
        addedHandlerRegistration.removeHandler();
        addedHandlerRegistration = null;
        removedHandlerRegistration.removeHandler();
        removedHandlerRegistration = null;
    }

    @Override
    public void dispose() {
        deregisterResourceSetHandlers();
        avatarChangeHandlerRegistration.removeHandler();
        avatarChangeHandlerRegistration = null;
    }

    private ResourceSet getResources() {
        return avatar.getResourceSet();
    }

    public void init() {
        avatarChangeHandlerRegistration = avatar
                .addResourceChangedHandler(this);
        registerResourceSetHandlers(getResources());
        avatar.addDisposable(this);
        updateAvatarState(getResources());
    }

    @Override
    public void onResourceAdded(ResourceAddedEvent e) {
        updateAvatarState(e.getResourceSet());
    }

    @Override
    public void onResourceRemoved(ResourceRemovedEvent e) {
        updateAvatarState(e.getResourceSet());
    }

    @Override
    public void onResourcesChanged(ResourceSetAvatarResourcesChangedEvent event) {
        deregisterResourceSetHandlers();
        registerResourceSetHandlers(event.getNewResources());
        updateAvatarState(event.getNewResources());
    }

    private void registerResourceSetHandlers(ResourceSet resourceSet) {
        addedHandlerRegistration = resourceSet.addHandler(
                ResourceAddedEvent.TYPE, this);
        removedHandlerRegistration = resourceSet.addHandler(
                ResourceRemovedEvent.TYPE, this);
    }

    private void updateAvatarState(ResourceSet resources) {
        avatar.setEnabled(!resources.isEmpty());
    }
}
