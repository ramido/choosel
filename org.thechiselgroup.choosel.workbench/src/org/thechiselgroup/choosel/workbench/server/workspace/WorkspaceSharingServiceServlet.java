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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.thechiselgroup.choosel.workbench.client.DefaultBranding;
import org.thechiselgroup.choosel.workbench.client.services.ServiceException;
import org.thechiselgroup.choosel.workbench.client.workspace.dto.WorkspaceDTO;
import org.thechiselgroup.choosel.workbench.client.workspace.service.WorkspaceSharingService;
import org.thechiselgroup.choosel.workbench.server.PMF;
import org.thechiselgroup.choosel.workbench.server.util.PasswordGenerator;
import org.thechiselgroup.choosel.workbench.server.util.StackTraceHelper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WorkspaceSharingServiceServlet extends RemoteServiceServlet
        implements WorkspaceSharingService {

    // TODO move
    public static String constructURL(String path, HttpServletRequest request,
            ServletContext servletContext) {

        String scheme = request.getScheme();
        String server = request.getServerName();
        int port = request.getServerPort();
        // starts with /, ends without /
        String contextPath = servletContext.getContextPath();

        return scheme + "://" + server + ":" + port + contextPath + "/" + path;
    }

    private WorkspaceSharingService service = null;

    private String constructURL(String servlet) {
        HttpServletRequest request = perThreadRequest.get();
        ServletContext servletContext = getServletContext();

        return constructURL(servlet, request, servletContext);
    }

    private WorkspaceSharingService getServiceDelegate()
            throws NoSuchAlgorithmException {

        if (service == null) {
            // TODO inject branding
            service = new WorkspaceSharingServiceImplementation(PMF.get(),
                    new WorkspaceSecurityManager(UserServiceFactory
                            .getUserService()),
                    UserServiceFactory.getUserService(),
                    MailServiceFactory.getMailService(), new PasswordGenerator(
                            SecureRandom.getInstance("SHA1PRNG")),
                    constructURL("acceptInvitation"), new DefaultBranding());
        }

        return service;
    }

    @Override
    public void shareWorkspace(WorkspaceDTO workspaceDTO, String emailAddress)
            throws ServiceException {

        long startTime = -1;
        if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
            startTime = System.currentTimeMillis();
        }

        Log.debug("WorkspaceSharingServiceServlet.shareWorkspace - "
                + workspaceDTO.getName() + " " + workspaceDTO.getId());

        try {
            getServiceDelegate().shareWorkspace(workspaceDTO, emailAddress);
        } catch (ServiceException e) {
            Log.error(
                    "shareWorkspace failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw e;
        } catch (Exception e) {
            Log.error(
                    "shareWorkspace failed: "
                            + StackTraceHelper.getStackTraceAsString(e), e);
            throw new ServiceException(e);
        } finally {
            if (Log.getCurrentLogLevel() <= Log.LOG_LEVEL_DEBUG) {
                Log.debug("shareWorkspace completed in "
                        + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }
}