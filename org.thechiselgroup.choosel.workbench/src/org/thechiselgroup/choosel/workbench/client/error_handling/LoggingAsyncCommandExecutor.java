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
package org.thechiselgroup.choosel.workbench.client.error_handling;

import org.thechiselgroup.choosel.core.client.command.AsyncCommand;
import org.thechiselgroup.choosel.core.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.workbench.client.services.NullAsyncCallback;

import com.allen_sauer.gwt.log.client.Log;

public class LoggingAsyncCommandExecutor implements AsyncCommandExecutor {

    public LoggingAsyncCommandExecutor() {
    }

    @Override
    public void execute(AsyncCommand command) {
        command.execute(new NullAsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error(caught.getMessage(), caught);
            }
        });
    }
}
