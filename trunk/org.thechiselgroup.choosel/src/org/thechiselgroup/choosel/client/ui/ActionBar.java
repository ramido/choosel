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
package org.thechiselgroup.choosel.client.ui;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ActionBar implements WidgetAdaptable {

    // TODO remove constant, replace with dynamic size calculation
    public static final int ACTION_BAR_HEIGHT_PX = 93;

    public static final String CSS_ACTIONBAR = "actionbar";

    public static final String CSS_ACTIONBAR_PANEL = "actionbar-panel";

    public static final String CSS_ACTIONBAR_PANEL_CONTENT = "actionbar-panel-content";

    public static final String CSS_ACTIONBAR_PANEL_HEADER = "actionbar-panel-header";

    public static final String CSS_ACTIONBAR_PANELCONTAINER = "actionbar-panelcontainer";

    public static final String CSS_ACTIONBAR_TITLE_AREA = "actionbar-titleArea";

    private HorizontalPanel actionBarPanelContainer;

    private HorizontalPanel actionBarTitleArea;

    private VerticalPanel outerWidget;

    public ActionBar() {
        outerWidget = new VerticalPanel();
        outerWidget.addStyleName(CSS_ACTIONBAR);
        outerWidget.setSpacing(0);

        actionBarTitleArea = new HorizontalPanel();
        actionBarTitleArea.addStyleName(CSS_ACTIONBAR_TITLE_AREA);
        outerWidget.add(actionBarTitleArea);

        actionBarPanelContainer = new HorizontalPanel();
        actionBarPanelContainer.addStyleName(CSS_ACTIONBAR_PANELCONTAINER);
        actionBarPanelContainer.setSpacing(0);
        outerWidget.add(actionBarPanelContainer);
    }

    public void addPanel(String title, Widget contentWidget) {
        VerticalPanel actionBarPanel = new VerticalPanel();
        actionBarPanel.addStyleName(CSS_ACTIONBAR_PANEL);

        SimplePanel contentPanel = new SimplePanel();
        contentPanel.add(contentWidget);
        contentPanel.addStyleName(CSS_ACTIONBAR_PANEL_CONTENT);
        actionBarPanel.add(contentPanel);

        Label header = new Label(title);
        header.addStyleName(CSS_ACTIONBAR_PANEL_HEADER);
        actionBarPanel.add(header);

        actionBarPanelContainer.add(actionBarPanel);
    }

    @Override
    public Widget asWidget() {
        return outerWidget;
    }

    public HorizontalPanel getActionBarTitleArea() {
        return actionBarTitleArea;
    }

}
