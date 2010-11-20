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

public class DefaultViewPersistenceManager implements ViewPersistenceManager {

    private ViewPersistenceServiceAsync service;

    private ResourceSetFactory resourceSetFactory;

    private ResourceManager resourceManager;

    @Inject
    public DefaultViewPersistenceManager(ViewPersistenceServiceAsync service,
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

        return viewDTO;
    }

    @Override
    public void saveView(DefaultView view, final AsyncCallback<Void> callback) {
        assert callback != null;

        ViewDTO viewDTO = createViewDTO(view);

        service.saveView(viewDTO, new ForwardingAsyncCallback<Long>(callback) {
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Long result) {
                // workspace.setId(result);
                super.onSuccess(result);
            }
        });
    }

}
