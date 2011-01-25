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

import org.thechiselgroup.choosel.core.client.views.DefaultView;
import org.thechiselgroup.choosel.workbench.client.error_handling.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DefaultViewLoader implements ViewLoader {

    private final ErrorHandler errorHandler;

    private final ViewLoadManager viewPersistenceManager;

    @Inject
    public DefaultViewLoader(ViewLoadManager viewPersistenceManager,
            ErrorHandler errorHandler) {

        this.viewPersistenceManager = viewPersistenceManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public void deleteView(Long viewId, AsyncCallback<Long> asyncCallback) {
        viewPersistenceManager.deleteView(viewId, asyncCallback);

    }

    @Override
    public void loadView(Long id, AsyncCallback<DefaultView> callback) {
        viewPersistenceManager.loadView(id, callback);
    }

    @Override
    public void loadViewAsWindow(Long id, AsyncCallback<Workspace> callback) {
        viewPersistenceManager.loadViewAsWindow(id, callback);
    }

    @Override
    public void loadViewAsWorkspace(Long id, AsyncCallback<Workspace> callback) {
        viewPersistenceManager.loadViewAsWorkspace(id, callback);

    }

}
