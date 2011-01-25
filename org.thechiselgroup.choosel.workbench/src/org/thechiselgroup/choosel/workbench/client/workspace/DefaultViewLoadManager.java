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
package org.thechiselgroup.choosel.workbench.client.workspace;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceManager;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.UnmodifiableResourceSet;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.views.DefaultView;
import org.thechiselgroup.choosel.core.client.windows.Desktop;
import org.thechiselgroup.choosel.core.client.windows.WindowContent;
import org.thechiselgroup.choosel.core.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.ResourceSetDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.ViewDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.ViewPreviewDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.service.ViewPersistenceServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DefaultViewLoadManager implements ViewLoadManager {

    public static interface ViewInitializer {

        void init(WindowContent content);

    }

    private ViewPersistenceServiceAsync service;

    private ResourceSetFactory resourceSetFactory;

    private ResourceManager resourceManager;

    private final WindowContentProducer windowContentProducer;

    private final WorkspaceManager workspaceManager;

    private final Desktop desktop;

    private PersistableRestorationService restorationService;

    @Inject
    public DefaultViewLoadManager(ViewPersistenceServiceAsync service,
            ResourceManager resourceManager,
            ResourceSetFactory resourceSetFactory,
            WindowContentProducer windowContentProducer,
            WorkspaceManager workspaceManager, Desktop desktop,
            PersistableRestorationService restorationService) {

        assert resourceManager != null;
        assert service != null;
        assert resourceSetFactory != null;
        assert restorationService != null;

        this.desktop = desktop;
        this.workspaceManager = workspaceManager;
        this.windowContentProducer = windowContentProducer;
        this.resourceSetFactory = resourceSetFactory;
        this.resourceManager = resourceManager;
        this.service = service;
        this.restorationService = restorationService;
    }

    @Override
    public void deleteView(Long id, final AsyncCallback<Long> callback) {
        assert callback != null;

        service.deleteView(id, new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);

            }

            @Override
            public void onSuccess(Long result) {
                try {
                    callback.onSuccess(result);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    private WindowContent loadResourcesAndView(ViewDTO dto,
            ViewInitializer viewInitializer) {

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

        content.setLabel(dto.getTitle());

        viewInitializer.init(content);

        /*
         * important: we restore the content after the window was created,
         * because different view content objects such as the timeline require
         * the view to be attached to the DOM.
         */
        if (content instanceof Persistable) {
            ((Persistable) content).restore(dto.getViewState(),
                    restorationService, accessor);
        }

        return content;
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
        return (DefaultView) loadResourcesAndView(dto, new ViewInitializer() {
            @Override
            public void init(WindowContent content) {
                content.init();
            }
        });
    }

    @Override
    public void loadViewAsWindow(Long id,
            final AsyncCallback<Workspace> callback) {
        assert callback != null;

        service.loadView(id, new AsyncCallback<ViewDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ViewDTO result) {
                try {
                    Workspace workspace = loadWindow(result);
                    callback.onSuccess(workspace);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    @Override
    public void loadViewAsWorkspace(Long id,
            final AsyncCallback<Workspace> callback) {
        assert callback != null;

        service.loadView(id, new AsyncCallback<ViewDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ViewDTO result) {
                try {
                    Workspace workspace = loadWorkspace(result);
                    callback.onSuccess(workspace);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    @Override
    public void loadViewPreviews(final AsyncCallback<List<ViewPreview>> callback) {
        assert callback != null;

        service.loadViewPreviews(new AsyncCallback<List<ViewPreviewDTO>>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);

            }

            @Override
            public void onSuccess(List<ViewPreviewDTO> result) {
                List<ViewPreview> previews = new ArrayList<ViewPreview>();
                for (ViewPreviewDTO dto : result) {
                    previews.add(new ViewPreview(dto.getId(), dto.getTitle(),
                            dto.getType(), dto.getCreated()));
                }

                callback.onSuccess(previews);
            }

        });
    }

    protected Workspace loadWindow(ViewDTO dto) {
        Workspace workspace = workspaceManager.getWorkspace();

        loadResourcesAndView(dto, new ViewInitializer() {
            @Override
            public void init(WindowContent content) {
                desktop.createWindow(content);
            }
        });

        return workspace;
    }

    protected Workspace loadWorkspace(ViewDTO dto) {
        desktop.clearWindows();
        workspaceManager.createNewWorkspace();

        return loadWindow(dto);
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
