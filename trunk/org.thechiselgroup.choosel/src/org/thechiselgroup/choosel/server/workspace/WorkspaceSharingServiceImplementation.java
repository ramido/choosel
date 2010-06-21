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

import java.io.IOException;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.workspace.dto.WorkspaceDTO;
import org.thechiselgroup.choosel.client.workspace.service.WorkspaceSharingService;
import org.thechiselgroup.choosel.server.util.PasswordGenerator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.users.UserService;

// TODO extract superclass for PMF
public class WorkspaceSharingServiceImplementation implements
        WorkspaceSharingService {

    public static final String PARAM_PASSWORD = "password";

    public static final String PARAM_INVITATION = "invitation";

    public static final String PARAM_WORKSPACE_ID = "workspaceId";

    private static final int PASSWORD_LENGTH = 12;

    private final PersistenceManagerFactory persistenceManagerFactory;

    private final WorkspaceSecurityManager securityManager;

    private final MailService mailService;

    private UserService userService;

    private PasswordGenerator passwordGenerator;

    private final String baseURL;

    public WorkspaceSharingServiceImplementation(
            PersistenceManagerFactory persistenceManagerFactory,
            WorkspaceSecurityManager workspaceSecurityManager,
            UserService userService, MailService mailService,
            PasswordGenerator passwordGenerator, String baseURL) {

        assert workspaceSecurityManager != null;
        assert persistenceManagerFactory != null;
        assert mailService != null;
        assert userService != null;
        assert passwordGenerator != null;
        assert baseURL != null;

        this.baseURL = baseURL;
        this.userService = userService;
        this.mailService = mailService;
        this.persistenceManagerFactory = persistenceManagerFactory;
        this.securityManager = workspaceSecurityManager;
        this.passwordGenerator = passwordGenerator;
    }

    private PersistenceManager createPersistanceManager() {
        return persistenceManagerFactory.getPersistenceManager();
    }

    public PersistentSharingInvitation createWorkspaceSharingInvitation(
            PersistentWorkspace workspace, String emailAddress,
            PersistenceManager manager) {

        assert workspace != null;
        assert manager != null;
        assert emailAddress != null;

        PersistentSharingInvitation invitation = new PersistentSharingInvitation();
        invitation.setWorkspace(workspace);
        invitation.setSenderUserId(userService.getCurrentUser().getUserId());
        invitation.setEmail(emailAddress);

        // this gives us 62^12 ~= 3.22E21 passwords - should prevent guessing
        invitation.setPassword(passwordGenerator
                .generatePassword(PASSWORD_LENGTH));

        invitation.setInvitationDate(new Date());
        invitation = manager.makePersistent(invitation);

        return invitation;
    }

    @Override
    public void shareWorkspace(WorkspaceDTO workspaceDTO, String emailAddress)
            throws ServiceException {

        securityManager.checkAuthenticated();

        PersistenceManager manager = createPersistanceManager();
        try {
            PersistentWorkspace workspace = manager.getObjectById(
                    PersistentWorkspace.class, workspaceDTO.getId());

            securityManager.checkAuthorization(workspace, manager);

            PersistentSharingInvitation sharingInvitation = createWorkspaceSharingInvitation(
                    workspace, emailAddress, manager);

            String sender = userService.getCurrentUser().getEmail();
            String to = emailAddress;
            String subject = workspaceDTO.getName();
            String url = baseURL + "?" + PARAM_WORKSPACE_ID + "="
                    + workspaceDTO.getId() + "&" + PARAM_INVITATION + "="
                    + KeyFactory.keyToString(sharingInvitation.getUid()) + "&"
                    + PARAM_PASSWORD + "=" + sharingInvitation.getPassword();

            String textBody = "I have shared the Bio-Mixer workspace" + " \""
                    + workspaceDTO.getName() + "\" with you. "
                    + "You can open it at " + url;

            Log.info(textBody);

            try {
                mailService.send(new MailService.Message(sender, to, subject,
                        textBody));
            } catch (IOException e) {
                Log.error(e.getMessage(), e);
                throw new ServiceException(
                        "Sending email with share notification failed.");
            }
        } finally {
            manager.close();
        }
    }
}
