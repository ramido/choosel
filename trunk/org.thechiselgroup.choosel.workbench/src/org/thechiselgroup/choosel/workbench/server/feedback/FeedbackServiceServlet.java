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
package org.thechiselgroup.choosel.workbench.server.feedback;

import org.thechiselgroup.choosel.workbench.client.feedback.FeedbackService;
import org.thechiselgroup.choosel.workbench.client.services.ServiceException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FeedbackServiceServlet extends RemoteServiceServlet implements
        FeedbackService {

    private FeedbackService service = null;

    private FeedbackService getServiceDelegate() {
        if (service == null) {
            service = new DefaultFeedbackService(
                    UserServiceFactory.getUserService(),
                    MailServiceFactory.getMailService());
        }

        assert service != null;

        return service;
    }

    @Override
    public void sendFeedback(String message, String errorMessage)
            throws ServiceException {

        try {
            getServiceDelegate().sendFeedback(message, errorMessage);
        } catch (RuntimeException e) {
            Log.error("sendFeedback failed", e);
            throw new ServiceException(e);
        }
    }
}
