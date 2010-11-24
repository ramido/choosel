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

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.UnmodifiableResourceSet;
import org.thechiselgroup.choosel.client.resources.persistence.DefaultResourceSetCollector;
import org.thechiselgroup.choosel.client.services.ForwardingAsyncCallback;
import org.thechiselgroup.choosel.client.views.DefaultView;
import org.thechiselgroup.choosel.client.workspace.dto.ResourceSetDTO;
import org.thechiselgroup.choosel.client.workspace.dto.ViewDTO;
import org.thechiselgroup.choosel.client.workspace.service.ViewPersistenceServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DefaultViewSaveManager implements ViewSaveManager {

    private ViewPersistenceServiceAsync service;

    private ResourceSetFactory resourceSetFactory;

    private ResourceManager resourceManager;

    @Inject
    public DefaultViewSaveManager(ViewPersistenceServiceAsync service,
            ResourceManager resourceManager,
            ResourceSetFactory resourceSetFactory) {

        assert resourceManager != null;
        assert service != null;
        assert resourceSetFactory != null;

        this.resourceSetFactory = resourceSetFactory;
        this.resourceManager = resourceManager;
        this.service = service;
    }

    private ViewDTO createViewDTO(DefaultView view) {

        assert view instanceof Persistable;

        ViewDTO viewDTO = new ViewDTO();

        DefaultResourceSetCollector persistanceManager = new DefaultResourceSetCollector();

        // TODO use view content label instead of window title
        // viewDTO.setTitle(window.getWindowTitle()); We will eventually store
        // the title

        viewDTO.setContentType(view.getContentType());

        Persistable persistable = view;
        viewDTO.setViewState(persistable.save(persistanceManager));

        // Resource set DTOs
        // 1. resolved unmodified sets --> changes list size
        List<ResourceSet> resourceSets = new ArrayList<ResourceSet>(
                persistanceManager.getResourceSets());
        for (ResourceSet resourceSet : resourceSets) {
            if (resourceSet instanceof UnmodifiableResourceSet) {
                persistanceManager
                        .storeResourceSet(((UnmodifiableResourceSet) resourceSet)
                                .getDelegate());
            }
        }

        // 2. store sets
        ResourceSet resourceCollector = new DefaultResourceSet();
        ResourceSetDTO[] resourceSetDTOs = new ResourceSetDTO[persistanceManager
                .getResourceSets().size()];
        for (int i = 0; i < persistanceManager.getResourceSets().size(); i++) {
            ResourceSetDTO dto = new ResourceSetDTO();
            ResourceSet resourceSet = persistanceManager.getResourceSets().get(
                    i);

            if (resourceSet.hasLabel()) {
                dto.setLabel(resourceSet.getLabel());
            }

            dto.setId(i);

            if (resourceSet instanceof UnmodifiableResourceSet) {
                ResourceSet sourceSet = ((UnmodifiableResourceSet) resourceSet)
                        .getDelegate();

                dto.setDelegateSetId(persistanceManager
                        .storeResourceSet(sourceSet));
            } else {
                List<String> resourceIds = new ArrayList<String>();
                for (Resource resource : resourceSet) {
                    resourceCollector.add(resource);
                    resourceIds.add(resource.getUri());
                }
                dto.setResourceIds(resourceIds);
            }

            resourceSetDTOs[i] = dto;
        }
        viewDTO.setResourceSets(resourceSetDTOs);

        viewDTO.setResources(resourceCollector
                .toArray(new Resource[resourceCollector.size()]));

        viewDTO.setTitle(view.getLabel());

        return viewDTO;
    }

    @Override
    public void saveView(final DefaultView view,
            final AsyncCallback<Void> callback) {
        assert callback != null;

        ViewDTO viewDTO = createViewDTO(view);

        service.saveView(viewDTO, new ForwardingAsyncCallback<Long>(callback) {
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Long result) {
                view.setId(result);
                view.updateSharePanel();
                super.onSuccess(result);
            }
        });
    }

}