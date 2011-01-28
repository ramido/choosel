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
import org.thechiselgroup.choosel.workbench.client.workspace.dto.ViewDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.ViewPreviewDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.service.ViewPersistenceService;
import org.thechiselgroup.choosel.workbench.server.PMF;
import org.thechiselgroup.choosel.workbench.server.util.StackTraceHelper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ViewPersistenceServiceServlet extends RemoteServiceServlet
        implements ViewPersistenceService {

    private ViewPersistenceService service = null;

    @Override
    public Long deleteView(Long viewId) throws ServiceException {

        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("ViewPersistenceServiceServlet.deleteView - " + viewId);

        try {
            return getServiceDelegate().deleteView(viewId);
        } catch (ServiceException e) {
            Log.error(
                    "deleteView failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "deleteView failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("ViewPersistenceServiceServlet.deleteView"
                        + " completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

    private ViewPersistenceService getServiceDelegate() {
        if (service == null) {
            service = new ViewPersistenceServiceImplementation(PMF.get(),
                    UserServiceFactory.getUserService());
        }

        return service;
    }

    @Override
    public ViewDTO loadView(Long viewId) throws ServiceException {

        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("ViewPersistenceServiceServlet.loadView - " + viewId);

        try {
            return getServiceDelegate().loadView(viewId);
        } catch (ServiceException e) {
            Log.error(
                    "loadView failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "loadView failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("ViewPersistenceServiceServlet.loadView"
                        + " completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

    @Override
    public List<ViewPreviewDTO> loadViewPreviews() throws ServiceException {
        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("ViewPersistenceServiceServlet.loadViewPreviews");

        try {
            return getServiceDelegate().loadViewPreviews();
        } catch (ServiceException e) {
            Log.error(
                    "loadViewPreviews failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "loadViewPreviews failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("ViewPersistenceServiceServlet.loadViewPreviews"
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
                    "saveView failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "saveView failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("ViewPersistenceServiceServlet.saveView"
                        + " completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

}
