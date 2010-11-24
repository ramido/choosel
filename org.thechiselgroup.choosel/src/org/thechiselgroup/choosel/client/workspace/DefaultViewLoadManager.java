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
package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.UnmodifiableResourceSet;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.views.DefaultView;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.client.workspace.dto.ResourceSetDTO;
import org.thechiselgroup.choosel.client.workspace.dto.ViewDTO;
import org.thechiselgroup.choosel.client.workspace.service.ViewPersistenceServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DefaultViewLoadManager implements ViewLoadManager {

    private ViewPersistenceServiceAsync service;

    private ResourceSetFactory resourceSetFactory;

    private ResourceManager resourceManager;

    private final WindowContentProducer windowContentProducer;

    @Inject
    public DefaultViewLoadManager(ViewPersistenceServiceAsync service,
            ResourceManager resourceManager,
            ResourceSetFactory resourceSetFactory,
            WindowContentProducer windowContentProducer) {

        assert resourceManager != null;
        assert service != null;
        assert resourceSetFactory != null;

        this.windowContentProducer = windowContentProducer;
        this.resourceSetFactory = resourceSetFactory;
        this.resourceManager = resourceManager;
        this.service = service;
    }

    @Override
    public void loadView(Long id, final AsyncCallback<DefaultView> callback) {
        assert callback != null;

        service.loadView(id, new AsyncCallback<ViewDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);

            }

            @Override
            public void onSuccess(ViewDTO result) {
                try {
                    DefaultView view = loadView(result);
                    callback.onSuccess(view);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    protected DefaultView loadView(ViewDTO dto) {

        restoreResources(dto);

        ResourceSetDTO[] resourceSetDTOs = dto.getResourceSets();
        final ResourceSet[] resourceSets = new ResourceSet[resourceSetDTOs.length];
        // 1. restore primary resource sets
        for (ResourceSetDTO resourceSetDTO : resourceSetDTOs) {
            if (!resourceSetDTO.isUnmodifiable()) {
                ResourceSet resourceSet = resourceSetFactory
                        .createResourceSet();
                resourceSet.setLabel(resourceSetDTO.getLabel());
                for (String uri : resourceSetDTO.getResourceIds()) {
                    resourceSet.add(resourceManager.getByUri(uri));
                }
                resourceSets[resourceSetDTO.getId()] = resourceSet;
            }
        }
        // 2. restore unmodifiable resource sets
        for (ResourceSetDTO resourceSetDTO : resourceSetDTOs) {
            if (resourceSetDTO.isUnmodifiable()) {
                int delegateId = resourceSetDTO.getDelegateSetId();
                ResourceSet resourceSet = new UnmodifiableResourceSet(
                        resourceSets[delegateId]);
                resourceSets[resourceSetDTO.getId()] = resourceSet;
            }
        }

        ResourceSetAccessor accessor = new ResourceSetAccessor() {
            @Override
            public ResourceSet getResourceSet(int id) {
                assert id >= 0;
                return resourceSets[id];
            }
        };

        final WindowContent content = windowContentProducer
                .createWindowContent(dto.getContentType());

        content.init();

        content.setLabel(dto.getTitle());

        /*
         * important: we restore the content after the window was created,
         * because different view content objects such as the timeline require
         * the view to be attached to the DOM.
         */
        if (content instanceof Persistable) {
            ((Persistable) content).restore(dto.getViewState(), accessor);
        }

        DefaultView view = (DefaultView) content;

        return view;
    }

    private void restoreResources(ViewDTO dto) {
        resourceManager.clear();
        Resource[] resources = dto.getResources();
        for (Resource resource : resources) {
            resourceManager.add(resource);
            // TODO need to allocate once allocation / removal is redone?
        }
    }

}
