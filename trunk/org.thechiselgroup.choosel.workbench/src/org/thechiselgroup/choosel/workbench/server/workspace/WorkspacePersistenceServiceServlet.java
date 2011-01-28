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
package org.thechiselgroup.choosel.workbench.server.workspace;

import java.util.List;

import org.thechiselgroup.choosel.core.client.util.ServiceException;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.WorkspaceDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.WorkspacePreviewDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.service.WorkspacePersistenceService;
import org.thechiselgroup.choosel.workbench.server.PMF;
import org.thechiselgroup.choosel.workbench.server.util.StackTraceHelper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

// TODO check if there is an aspect-oriented solution to this logging
public class WorkspacePersistenceServiceServlet extends RemoteServiceServlet
        implements WorkspacePersistenceService {

    private WorkspacePersistenceService service = null;

    private WorkspacePersistenceService getServiceDelegate() {
        if (service == null) {
            service = new WorkspacePersistenceServiceImplementation(PMF.get(),
                    new WorkspaceSecurityManager(
                            UserServiceFactory.getUserService()));
        }

        return service;
    }

    @Override
    public WorkspaceDTO loadWorkspace(Long id) throws ServiceException {
        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("WorkspacePersistenceServiceServlet.loadWorkspace - " + id);

        try {
            return getServiceDelegate().loadWorkspace(id);
        } catch (ServiceException e) {
            Log.error(
                    "loadWorkspace failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "loadWorkspace failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("WorkspacePersistenceServiceServlet.loadWorkspace"
                        + " completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

    @Override
    public List<WorkspacePreviewDTO> loadWorkspacePreviews()
            throws ServiceException {

        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("WorkspacePersistenceServiceServlet.loadWorkspacePreviews");

        try {
            return getServiceDelegate().loadWorkspacePreviews();
        } catch (ServiceException e) {
            Log.error(
                    "loadWorkspacePreviews failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "loadWorkspacePreviews failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("WorkspacePersistenceServiceServlet.loadWorkspacePreviews"
                        + " completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

    // TODO extract surrounding stuff into special executor
    @Override
    public Long saveWorkspace(WorkspaceDTO workspace) throws ServiceException {
        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("WorkspacePersistenceServiceServlet.saveWorkspace - "
                + workspace.getName() + " " + workspace.getId());

        try {
            return getServiceDelegate().saveWorkspace(workspace);
        } catch (ServiceException e) {
            Log.error(
                    "saveWorkspace failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "saveWorkspace failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("WorkspacePersistenceServiceServlet.saveWorkspace"
                        + " completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }
}