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
package org.thechiselgroup.choosel.core.client.ui.dnd;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ui.DelegatingResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.views.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.ViewAccessor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class SelectionResourceSetAvatarFactory extends
        DelegatingResourceSetAvatarFactory {

    private ViewAccessor viewAccessor;

    public SelectionResourceSetAvatarFactory(ResourceSetAvatarFactory delegate,
            ViewAccessor viewAccessor) {
        super(delegate);
        this.viewAccessor = viewAccessor;
    }

    @Override
    public ResourceSetAvatar createAvatar(ResourceSet resources) {
        final ResourceSetAvatar avatar = delegate.createAvatar(resources);

        avatar.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SelectionModel selectionModel = viewAccessor.findView(avatar)
                        .getSelectionModel();
                if (avatar.getResourceSet().equals(
                        selectionModel.getSelection())) {
                    selectionModel.setSelection(null);
                } else {
                    selectionModel.setSelection(avatar.getResourceSet());
                }
            }
        });

        return avatar;
    }
}