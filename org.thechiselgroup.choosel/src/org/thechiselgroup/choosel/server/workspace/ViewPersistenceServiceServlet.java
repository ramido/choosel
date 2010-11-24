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

import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.workspace.dto.ViewDTO;
import org.thechiselgroup.choosel.client.workspace.service.ViewPersistenceService;
import org.thechiselgroup.choosel.server.PMF;
import org.thechiselgroup.choosel.server.util.StackTraceHelper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ViewPersistenceServiceServlet extends RemoteServiceServlet
        implements ViewPersistenceService {

    private ViewPersistenceService service = null;

    private ViewPersistenceService getServiceDelegate() {
        if (service == null) {
            service = new ViewPersistenceServiceImplementation(PMF.get(),
                    new WorkspaceSecurityManager(
                            UserServiceFactory.getUserService()));
        }

        return service;
    }

    @Override
    public ViewDTO loadView(Long viewId) throws ServiceException {

        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("WorkspacePersistenceServiceServlet.loadWorkspace - "
                + viewId);

        try {
            return getServiceDelegate().loadView(viewId);
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
    public Long saveView(ViewDTO view) throws ServiceException {
        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("ViewPersistenceServiceServlet.saveView - Needs an ID");

        try {
            return getServiceDelegate().saveView(view);
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
