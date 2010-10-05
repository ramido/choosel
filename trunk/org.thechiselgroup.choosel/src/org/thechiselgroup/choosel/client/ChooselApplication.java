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
package org.thechiselgroup.choosel.client;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.authentication.AuthenticationManager;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBar;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBasedEnablingStateWrapper;
import org.thechiselgroup.choosel.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.ui.CommandManagerPresenter;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenterFactory;
import org.thechiselgroup.choosel.client.command.ui.DefaultCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.command.ui.ImageCommandDisplay;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.Action;
import org.thechiselgroup.choosel.client.ui.ActionBar;
import org.thechiselgroup.choosel.client.ui.ActionToolbarItem;
import org.thechiselgroup.choosel.client.ui.ImageButton;
import org.thechiselgroup.choosel.client.ui.dialog.DialogManager;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.CreateWindowCommand;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.client.workspace.WorkspaceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePersistenceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter.DefaultWorkspacePresenterDisplay;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceDialogCommand;
import org.thechiselgroup.choosel.client.workspace.command.NewWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.SaveWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.ShareWorkspaceCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public abstract class ChooselApplication {

    public static final String WORKSPACE_ID = "workspaceId";

    public static final String EDIT_PANEL = "edit";

    public static final String HELP_PANEL = "help";

    public static final String VIEWS_PANEL = "views";

    public static final String WORKSPACE_PANEL = "workspace";

    @Inject
    private ActionBar actionBar;

    @Inject
    private AuthenticationBar authenticationBar;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private AsyncCommandExecutor blockingCommandExecutor;

    @Inject
    protected CommandManager commandManager;

    @Inject
    private DefaultCommandManagerPresenterDisplay commandManagerPresenterDisplay;

    @Inject
    private CommandPresenterFactory commandPresenterFactory;

    @Inject
    protected ResourceSetAvatarFactory defaultDragAvatarFactory;

    @Inject
    protected Desktop desktop;

    @Inject
    private DialogManager dialogManager;

    @Inject
    private InfoDialog infoDialog;

    @Inject
    private LoadWorkspaceDialogCommand loadWorkspaceDialogCommand;

    @Inject
    private NewWorkspaceCommand newWorkspaceCommand;

    private Map<String, HorizontalPanel> panels = new HashMap<String, HorizontalPanel>();

    @Inject
    protected ResourceSetFactory resourceSetsFactory;

    @Inject
    private SaveWorkspaceCommand saveWorkspaceCommand;

    @Inject
    private ShareWorkspaceCommand shareWorkspaceCommand;

    @Inject
    private WindowContentProducer windowContentProducer;

    @Inject
    private WorkspaceManager workspaceManager;

    @Inject
    private WorkspacePersistenceManager workspacePersistenceManager;

    @Inject
    private DefaultWorkspacePresenterDisplay workspacePresenterDisplay;

    public void addButton(String panelId, String label, ClickHandler handler) {
        assert panelId != null;
        assert label != null;
        assert handler != null;

        Button button = new Button(label);
        button.addClickHandler(handler);
        addWidget(panelId, button);
    }

    protected void addImageButton(String panelId, String name,
            ClickHandler handler) {

        assert panelId != null;
        assert handler != null;
        assert name != null;

        ImageButton button = ImageButton.createImageButton(name);
        button.addClickHandler(handler);
        addWidget(panelId, button);
    }

    protected void addInfoButton() {
        addImageButton(HELP_PANEL, "help-about", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogManager.show(infoDialog);
            }
        });
    }

    public void addPanel(String id, String name) {
        assert id != null;
        assert name != null;

        HorizontalPanel panel = new HorizontalPanel();
        panel.setSpacing(2);
        actionBar.addPanel(name, panel);
        panels.put(id, panel);
    }

    public void addWidget(String panelId, Widget widget) {
        assert panelId != null;
        assert panels.containsKey(panelId);
        assert widget != null;

        panels.get(panelId).add(widget);
    }

    protected void addWindowClosingConfirmationDialog() {
        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                event.setMessage("Unsaved changes to the workspace will be lost.");
            }
        });
    }

    public void addWindowContentButton(String panelId, String label,
            final String contentType) {

        assert panelId != null;
        assert label != null;
        assert contentType != null;
        // TODO assert factory for content type is available

        addButton(panelId, label, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createWindow(contentType);
            }
        });
    }

    public void addWindowContentImageButton(String panelId, String name,
            final String contentType) {

        assert panelId != null;
        assert name != null;
        assert contentType != null;
        // TODO assert factory for content type is available

        addImageButton(panelId, name, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createWindow(contentType);
            }
        });
    }

    private DockPanel createMainPanel() {
        DockPanel mainPanel = new DockPanel();
        RootPanel.get().add(mainPanel);
        return mainPanel;
    }

    protected ResourceSet createResourceSet() {
        return resourceSetsFactory.createResourceSet();
    }

    protected ResourceSetsPresenter createResourceSetsPresenter() {
        final ResourceSetsPresenter dataSourcesPresenter = new ResourceSetAvatarResourceSetsPresenter(
                defaultDragAvatarFactory);
        dataSourcesPresenter.init();
        return dataSourcesPresenter;
    }

    protected void createWindow(AbstractWindowContent content) {
        commandManager.execute(new CreateWindowCommand(desktop, content));
    }

    private void createWindow(String contentType) {
        commandManager.execute(new CreateWindowCommand(desktop,
                windowContentProducer.createWindowContent(contentType)));
    }

    public void init() {
        BrowserDetect.checkBrowser();

        addWindowClosingConfirmationDialog();

        DockPanel mainPanel = createMainPanel();

        initDesktop(mainPanel);
        initActionBar(mainPanel);
        initAuthenticationBar();

        initWorkspacePanel();
        initCommandManagerPresenter();

        initCustomActions();

        loadWorkspaceIfParamSet();
    }

    private void initActionBar(DockPanel mainPanel) {
        mainPanel.add(actionBar.asWidget(), DockPanel.NORTH);

        addPanel(WORKSPACE_PANEL, "Workspace");
        addPanel(EDIT_PANEL, "Edit");

        initCustomPanels();
    }

    private void initAuthenticationBar() {
        ((VerticalPanel) actionBar.asWidget()).add(authenticationBar);
    }

    private void initCommandManagerPresenter() {
        CommandManagerPresenter presenter = new CommandManagerPresenter(
                commandManager, commandManagerPresenterDisplay);

        presenter.init();

        addWidget(EDIT_PANEL, commandManagerPresenterDisplay.getRedoButton());
        addWidget(EDIT_PANEL, commandManagerPresenterDisplay.getUndoButton());
    }

    protected abstract void initCustomActions();

    protected abstract void initCustomPanels();

    private void initDesktop(DockPanel mainPanel) {
        // absolute root panel required for drag & drop
        // into windows on firefox browser
        desktop.asWidget().setPixelSize(Window.getClientWidth(),
                Window.getClientHeight() - ActionBar.ACTION_BAR_HEIGHT_PX);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                desktop.asWidget().setPixelSize(event.getWidth(),
                        event.getHeight() - ActionBar.ACTION_BAR_HEIGHT_PX);
                // TODO windows need to be moved if they are out of the
                // range
            }
        });

        mainPanel.add(desktop.asWidget(), DockPanel.CENTER);
    }

    private void initWorkspacePanel() {
        // title area
        // TODO refactor title area part
        WorkspacePresenter presenter = new WorkspacePresenter(workspaceManager,
                workspacePresenterDisplay);
        presenter.init();
        // TODO replace with stuff from workspace presenter
        workspacePresenterDisplay.getTextBox().addStyleName(
                "actionbar-titleArea-text");
        actionBar.getActionBarTitleArea().add(
                workspacePresenterDisplay.getTextBox());

        // new workspace
        Action newWorkspaceAction = new Action("New workspace",
                newWorkspaceCommand, "workspace-new");
        addWidget(WORKSPACE_PANEL, new ActionToolbarItem(newWorkspaceAction));

        // load workspace
        ImageCommandDisplay loadButton = commandPresenterFactory
                .createCommandImage("workspace-open",
                        loadWorkspaceDialogCommand);
        addWidget(WORKSPACE_PANEL, loadButton);
        new AuthenticationBasedEnablingStateWrapper(authenticationManager,
                loadButton).init();

        // save workspace
        ImageCommandDisplay saveButton = commandPresenterFactory
                .createCommandImage("workspace-save", saveWorkspaceCommand);
        addWidget(WORKSPACE_PANEL, saveButton);
        AuthenticationBasedEnablingStateWrapper authWrapper = new AuthenticationBasedEnablingStateWrapper(
                authenticationManager, saveButton);
        authWrapper.init();

        // XXX updater broken --> needs text
        // new SaveButtonUpdater(workspaceManager, saveButton,
        // authWrapper).init();

        // share workspace
        ImageCommandDisplay shareButton = commandPresenterFactory
                .createCommandImage("workspace-share", shareWorkspaceCommand);
        addWidget(WORKSPACE_PANEL, shareButton);
        new AuthenticationBasedEnablingStateWrapper(authenticationManager,
                shareButton).init();
    }

    private void loadWorkspaceIfParamSet() {
        String workspaceIdParam = Window.Location.getParameter(WORKSPACE_ID);

        if (workspaceIdParam != null) {
            long workspaceID = Long.parseLong(workspaceIdParam);

            LoadWorkspaceCommand loadWorkspaceCommand = new LoadWorkspaceCommand(
                    workspaceID, workspacePersistenceManager);
            blockingCommandExecutor.execute(loadWorkspaceCommand);
        }
    }

}
