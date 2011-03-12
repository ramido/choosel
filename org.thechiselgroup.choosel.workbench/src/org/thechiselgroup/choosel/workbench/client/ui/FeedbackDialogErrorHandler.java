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
package org.thechiselgroup.choosel.workbench.client.ui;

import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.LOG;

import org.thechiselgroup.choosel.core.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.core.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.core.client.error_handling.ExceptionUtil;
import org.thechiselgroup.choosel.workbench.client.feedback.FeedbackDialog;
import org.thechiselgroup.choosel.workbench.client.feedback.FeedbackServiceAsync;
import org.thechiselgroup.choosel.workbench.client.ui.dialog.DialogManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class FeedbackDialogErrorHandler implements ErrorHandler {

    private DialogManager dialogManager;

    private AsyncCommandExecutor executor;

    private FeedbackServiceAsync feedbackService;

    @Inject
    public FeedbackDialogErrorHandler(DialogManager dialogManager,
            @Named(LOG) AsyncCommandExecutor executor,
            FeedbackServiceAsync feedbackService) {

        // we use the log command executor to prevent invite loops

        assert dialogManager != null;
        assert executor != null;
        assert feedbackService != null;

        this.executor = executor;
        this.feedbackService = feedbackService;
        this.dialogManager = dialogManager;
    }

    @Override
    public void handleError(Throwable error) {
        assert error != null;

        error = ExceptionUtil.unwrapCause(error);

        // TODO extract - composite error handler
        Log.error(error.getMessage(), error);

        dialogManager
                .show(new FeedbackDialog(error, executor, feedbackService));
    }
}
