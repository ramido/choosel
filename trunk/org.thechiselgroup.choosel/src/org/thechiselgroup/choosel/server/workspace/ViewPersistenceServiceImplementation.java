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
package org.thechiselgroup.choosel.server.workspace;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.thechiselgroup.choosel.client.authentication.AuthenticationException;
import org.thechiselgroup.choosel.client.authentication.AuthorizationException;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.workspace.dto.ViewDTO;
import org.thechiselgroup.choosel.client.workspace.service.ViewPersistenceService;

import com.google.inject.Inject;

/**
 * Design rationale: everything that will not be used for querying is just
 * serialized and not mapped to persistable objects (reason: better performance
 * & better development performance)
 * 
 * For more information on App Engine persistence, see
 * 
 * {@linkplain http://code.google.com/appengine/docs/java/datastore/}
 * 
 * {@linkplain http
 * ://code.google.com/events/io/2009/sessions/SofterSideofSchemas.html}
 */
public class ViewPersistenceServiceImplementation implements
        ViewPersistenceService {

    private WorkspaceSecurityManager permissionManager;

    private final PersistenceManagerFactory persistenceManagerFactory;

    @Inject
    public ViewPersistenceServiceImplementation(PersistenceManagerFactory pmf,
            WorkspaceSecurityManager securityManager) {

        assert securityManager != null;
        assert pmf != null;

        permissionManager = securityManager;
        persistenceManagerFactory = pmf;
    }

    private PersistenceManager createPersistanceManager() {
        return persistenceManagerFactory.getPersistenceManager();
    }

    private PersistentView createPersistentView(PersistenceManager pm) {
        PersistentView view = new PersistentView();
        view = pm.makePersistent(view);

        // permissionManager.createWorkspacePermissionForCurrentUser(view, pm);

        return view;
    }

    private PersistentView getPersistentView(Long viewId,
            PersistenceManager manager) throws AuthorizationException {

        PersistentView pView = manager.getObjectById(PersistentView.class,
                viewId);

        // permissionManager.checkAuthorization(pWorkspace, manager);

        return pView;
    }

    @Override
    public ViewDTO loadView(Long viewId) throws ServiceException {
        PersistenceManager pm = createPersistanceManager();

        try {
            return loadView(viewId, pm);
        } finally {
            pm.close();
        }
    }

    private ViewDTO loadView(Long viewId, PersistenceManager pm)
            throws AuthorizationException {
        return toViewDTO(getPersistentView(viewId, pm));
    }

    @Override
    public Long saveView(ViewDTO dto) throws AuthenticationException,
            AuthorizationException {

        // permissionManager.checkAuthenticated();

        PersistenceManager pm = createPersistanceManager();

        try {
            PersistentView view = createPersistentView(pm);

            updateViewWithDTO(view, dto);

            return view.getId();
        } finally {
            pm.close();
        }
    }

    private ViewDTO toViewDTO(PersistentView pView) {
        ViewDTO dto = new ViewDTO();

        dto.setId(pView.getId());
        dto.setTitle(pView.getTitle());
        dto.setResources(pView.getResources());
        dto.setResourceSets(pView.getResourceSets());
        dto.setContentType(pView.getContentType());
        dto.setTitle(pView.getTitle());
        dto.setViewState(pView.getViewState());

        return dto;
    }

    private void updateViewWithDTO(PersistentView view, ViewDTO dto) {

        view.setTitle(dto.getTitle());
        view.setViewState(dto.getViewState());
        view.setResources(dto.getResources());
        view.setResourceSets(dto.getResourceSets());
        view.setContentType(dto.getContentType());
    }

    private boolean viewExists(ViewDTO dto) {
        return dto.getId() != null;
    }
}
