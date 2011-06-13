/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.workbench.client.init;

import org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.core.client.error_handling.LoggingErrorHandler;
import org.thechiselgroup.choosel.core.client.util.BrowserDetect;
import org.thechiselgroup.choosel.core.client.views.View;
import org.thechiselgroup.choosel.workbench.client.workspace.ViewLoader;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class EmbedInitializer implements ApplicationInitializer {

    @Inject
    private WindowLocation windowLocation;

    @Inject
    @Named(ChooselInjectionConstants.ROOT_PANEL)
    private AbsolutePanel rootPanel;

    @Inject
    private ViewLoader viewLoader;

    @Inject
    private LoggingErrorHandler loggingErrorHandler;

    @Override
    public void init() throws Exception {
        String viewIdString = windowLocation
                .getParameter(WorkbenchInitializer.VIEW_ID);
        long viewId = Long.parseLong(viewIdString);
        // TODO catch exception, handle in here

        // initGlobalErrorHandler();
        // TODO needs different handler? --> yes, show on info label
        // if there is good error handling in choosel entry point we dont need
        // this

        BrowserDetect.checkBrowser();

        Window.enableScrolling(false);

        final Label informationLabel = new Label();
        rootPanel.add(informationLabel);

        informationLabel.setText("Loading View...");

        viewLoader.loadView(viewId, new AsyncCallback<View>() {

            @Override
            public void onFailure(Throwable caught) {
                loggingErrorHandler.handleError(caught);
                informationLabel
                        .setText("Sorry, the specified view is not available.");
            }

            @Override
            public void onSuccess(final View view) {
                final Widget widget = view.asWidget();

                rootPanel.remove(informationLabel);
                rootPanel.add(widget);

                updateWidgetSize(widget);
                Window.addResizeHandler(new ResizeHandler() {
                    @Override
                    public void onResize(ResizeEvent event) {
                        updateWidgetSize(widget);
                    }
                });
            }

            private void updateWidgetSize(final Widget widget) {
                widget.setPixelSize(Window.getClientWidth(),
                        Window.getClientHeight());
            }

        });

    }

}