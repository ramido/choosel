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

import org.thechiselgroup.choosel.client.authentication.AuthenticationManager;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBar;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBasedEnablingStateWrapper;
import org.thechiselgroup.choosel.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.client.command.AsyncCommandToCommandAdapter;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.ui.RedoCommandManagerPresenter;
import org.thechiselgroup.choosel.client.command.ui.RedoCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.command.ui.UndoCommandManagerPresenter;
import org.thechiselgroup.choosel.client.command.ui.UndoCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.Action;
import org.thechiselgroup.choosel.client.ui.ActionBar;
import org.thechiselgroup.choosel.client.ui.ImageButton;
import org.thechiselgroup.choosel.client.ui.dialog.DialogManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
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
import com.google.gwt.user.client.Command;
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
    private AsyncCommandExecutor asyncCommandExecutor;

    @Inject
    protected CommandManager commandManager;

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
    private PopupManagerFactory popupManagerFactory;

    @Inject
    private DefaultWorkspacePresenterDisplay workspacePresenterDisplay;

    public Action addActionToToolbar(String panelId, Command command,
            String title, String iconName) {

        Action action = new Action(title, command, iconName);
        getToolbarPanel(panelId).addAction(action);
        return action;
    }

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

    public void addToolbarPanel(String panelId, String title) {
        assert panelId != null;
        assert title != null;

        actionBar
                .addPanel(new ToolbarPanel(panelId, title, popupManagerFactory));
    }

    public void addWidget(String panelId, Widget widget) {
        assert panelId != null;
        assert widget != null;

        ((HorizontalPanel) actionBar.getPanel(panelId).getContentWidget())
                .add(widget);
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

    public ToolbarPanel getToolbarPanel(String panelId) {
        return (ToolbarPanel) actionBar.getPanel(panelId);
    }

    public void init() {
        BrowserDetect.checkBrowser();

        addWindowClosingConfirmationDialog();

        DockPanel mainPanel = createMainPanel();

        initDesktop(mainPanel);
        initActionBar(mainPanel);
        initAuthenticationBar();

        initWorkspaceTitlePresenter();

        initWorkspacePanel();
        initCommandManagerPresenter();

        initCustomActions();

        loadWorkspaceIfParamSet();
    }

    private void initActionBar(DockPanel mainPanel) {
        mainPanel.add(actionBar.asWidget(), DockPanel.NORTH);

        addToolbarPanel(WORKSPACE_PANEL, "Workspace");
        addToolbarPanel(EDIT_PANEL, "Edit");

        initCustomPanels();
    }

    private void initAuthenticationBar() {
        ((VerticalPanel) actionBar.asWidget()).add(authenticationBar);
    }

    private void initCommandManagerPresenter() {
        RedoCommandManagerPresenterDisplay redoCommandManagerPresenterDisplay = new RedoCommandManagerPresenterDisplay(
                popupManagerFactory);
        RedoCommandManagerPresenter redoPresenter = new RedoCommandManagerPresenter(
                commandManager, redoCommandManagerPresenterDisplay);
        redoPresenter.init();
        addWidget(EDIT_PANEL,
                redoCommandManagerPresenterDisplay.getRedoButton());

        UndoCommandManagerPresenterDisplay undoCommandManagerPresenterDisplay = new UndoCommandManagerPresenterDisplay(
                popupManagerFactory);
        UndoCommandManagerPresenter undoPresenter = new UndoCommandManagerPresenter(
                commandManager, undoCommandManagerPresenterDisplay);
        undoPresenter.init();
        addWidget(EDIT_PANEL,
                undoCommandManagerPresenterDisplay.getUndoButton());
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

    protected void initLoadWorkspaceAction() {
        Action loadAction = addActionToToolbar(WORKSPACE_PANEL,
                new AsyncCommandToCommandAdapter(loadWorkspaceDialogCommand,
                        asyncCommandExecutor), "Load workspace",
                "workspace-open");

        new AuthenticationBasedEnablingStateWrapper(authenticationManager,
                loadAction).init();
    }

    protected void initNewWorkspaceAction() {
        addActionToToolbar(WORKSPACE_PANEL, newWorkspaceCommand,
                "New workspace", "workspace-new");
    }

    protected void initSaveWorkspaceAction() {
        Action saveAction = addActionToToolbar(WORKSPACE_PANEL,
                saveWorkspaceCommand, "Save workspace", "workspace-save");
        AuthenticationBasedEnablingStateWrapper authWrapper = new AuthenticationBasedEnablingStateWrapper(
                authenticationManager, saveAction);
        authWrapper.init();

        // XXX updater broken --> needs text
        // new SaveButtonUpdater(workspaceManager, saveButton,
        // authWrapper).init();
    }

    protected void initShareWorkspaceAction() {
        Action action = addActionToToolbar(WORKSPACE_PANEL,
                new AsyncCommandToCommandAdapter(shareWorkspaceCommand,
                        asyncCommandExecutor), "Share workspace",
                "workspace-share");

        new AuthenticationBasedEnablingStateWrapper(authenticationManager,
                action).init();
    }

    protected void initWorkspacePanel() {
        initNewWorkspaceAction();
        initLoadWorkspaceAction();
        initSaveWorkspaceAction();
        initShareWorkspaceAction();
    }

    protected void initWorkspaceTitlePresenter() {
        // TODO refactor title area part
        WorkspacePresenter presenter = new WorkspacePresenter(workspaceManager,
                workspacePresenterDisplay);
        presenter.init();
        // TODO replace with stuff from workspace presenter
        workspacePresenterDisplay.getTextBox().addStyleName(
                "actionbar-titleArea-text");
        actionBar.getActionBarTitleArea().add(
                workspacePresenterDisplay.getTextBox());
    }

    private void loadWorkspaceIfParamSet() {
        String workspaceIdParam = Window.Location.getParameter(WORKSPACE_ID);

        if (workspaceIdParam != null) {
            long workspaceID = Long.parseLong(workspaceIdParam);

            LoadWorkspaceCommand loadWorkspaceCommand = new LoadWorkspaceCommand(
                    workspaceID, workspacePersistenceManager);
            asyncCommandExecutor.execute(loadWorkspaceCommand);
        }
    }

}
