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
package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandlingAsyncCallback;
import org.thechiselgroup.choosel.client.views.DefaultView;

import com.google.inject.Inject;

public class DefaultViewSaver implements ViewSaver {

    private final ErrorHandler errorHandler;

    private final ViewSaveManager viewPersistenceManager;

    @Inject
    public DefaultViewSaver(
            ViewSaveManager viewPersistenceManager,
            ErrorHandler errorHandler) {

        this.viewPersistenceManager = viewPersistenceManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public void saveView(DefaultView view) {
        viewPersistenceManager.saveView(view,
                new ErrorHandlingAsyncCallback<Void>(errorHandler));
    }

}