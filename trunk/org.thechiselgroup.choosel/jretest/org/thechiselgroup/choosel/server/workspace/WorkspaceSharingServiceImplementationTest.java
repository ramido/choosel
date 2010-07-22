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

import javax.jdo.PersistenceManagerFactory;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.server.util.PasswordGenerator;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.users.UserService;

public class WorkspaceSharingServiceImplementationTest {

    private static final String BASEURL = "BASEURL";

    @Mock
    private MailService mailService;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private PersistenceManagerFactory persistenceManagerFactory;

    private WorkspaceSharingServiceImplementation underTest;

    @Mock
    private UserService userService;

    @Mock
    private WorkspaceSecurityManager workspaceSecurityManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new WorkspaceSharingServiceImplementation(
                persistenceManagerFactory, workspaceSecurityManager,
                userService, mailService, passwordGenerator, BASEURL);
    }

}