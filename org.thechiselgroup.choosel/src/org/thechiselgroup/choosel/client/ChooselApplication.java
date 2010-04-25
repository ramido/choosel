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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.client.authentication.AuthenticationManager;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBar;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBasedEnablingStateWrapper;
import org.thechiselgroup.choosel.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.ui.CommandManagerPresenter;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenterFactory;
import org.thechiselgroup.choosel.client.command.ui.DefaultCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenter.ButtonDisplay;
import org.thechiselgroup.choosel.client.domain.other.GeoRSSServiceAsync;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.ui.ActionBar;
import org.thechiselgroup.choosel.client.ui.dialog.DialogManager;
import org.thechiselgroup.choosel.client.views.list.ListViewContentDisplay;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.CreateWindowCommand;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.client.workspace.SaveButtonUpdater;
import org.thechiselgroup.choosel.client.workspace.WorkspaceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePersistenceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter.DefaultWorkspacePresenterDisplay;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceDialogCommand;
import org.thechiselgroup.choosel.client.workspace.command.NewWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.SaveWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.ShareWorkspaceCommand;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ChooselApplication {

    private static class DataSourceCallBack implements
	    AsyncCallback<Set<Resource>> {

	private ResourceSetsPresenter dataSourcesPresenter;

	private String label;

	private final ResourceSetFactory resourceSetFactory;

	public DataSourceCallBack(String label,
		ResourceSetsPresenter dataSourcesPresenter,
		ResourceSetFactory resourceSetsFactory) {

	    this.label = label;
	    this.dataSourcesPresenter = dataSourcesPresenter;
	    this.resourceSetFactory = resourceSetsFactory;
	}

	public void onFailure(Throwable e) {
	    Log.error(e.getMessage(), e);
	}

	public void onSuccess(Set<Resource> resources) {
	    ResourceSet resourceSet = resourceSetFactory.createResourceSet();
	    resourceSet.addAll(resources);
	    resourceSet.setLabel(label);

	    dataSourcesPresenter.addResourceSet(resourceSet);
	}
    }

    public static final String DATA_PANEL = "data";

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
    private CommandManager commandManager;

    @Inject
    private DefaultCommandManagerPresenterDisplay commandManagerPresenterDisplay;

    @Inject
    private CommandPresenterFactory commandPresenterFactory;

    private HorizontalPanel dataPanel;

    @Inject
    private ResourceSetAvatarFactory defaultDragAvatarFactory;

    @Inject
    private Desktop desktop;

    @Inject
    private DialogManager dialogManager;

    @Inject
    private GeoRSSServiceAsync geoRssService;

    @Inject
    private InfoDialog infoDialog;

    @Inject
    private LoadWorkspaceDialogCommand loadWorkspaceDialogCommand;

    @Inject
    private NewWorkspaceCommand newWorkspaceCommand;

    private Map<String, HorizontalPanel> panels = new HashMap<String, HorizontalPanel>();

    @Inject
    private ResourceSetFactory resourceSetsFactory;

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

    private void addDataSourcesButton() {
	Button b = new Button("Tsunami / Earthquake");
	b.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		String title = "Data Sources";
		final ResourceSetsPresenter dataSourcesPresenter = new ResourceSetAvatarResourceSetsPresenter(
			defaultDragAvatarFactory);
		dataSourcesPresenter.init();

		commandManager.execute(new CreateWindowCommand(desktop,
			new AbstractWindowContent(title, "TODO") {
			    @Override
			    public Widget asWidget() {
				return dataSourcesPresenter.asWidget();
			    }
			}));

		geoRssService
			.getGeoRSS(
				"http://earthquake.usgs.gov/eqcenter/catalogs/shakerss.xml",
				"earthquake", new DataSourceCallBack(
					"earthquake", dataSourcesPresenter,
					resourceSetsFactory));
		geoRssService
			.getGeoRSS(
				"http://www.prh.noaa.gov/ptwc/feeds/ptwc_rss_pacific.xml",
				"tsunami", new DataSourceCallBack("tsunami",
					dataSourcesPresenter,
					resourceSetsFactory));
	    }

	});
	dataPanel.add(b);
    }

    protected void addInfoButton() {
	addButton(HELP_PANEL, "About", new ClickHandler() {
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

    // TODO change into command
    private void addTestDataSourceButton() {
	Button b = new Button("Test Data");
	b.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		String title = "TestResources";
		final ResourceSetsPresenter dataSourcesPresenter = new ResourceSetAvatarResourceSetsPresenter(
			defaultDragAvatarFactory);
		dataSourcesPresenter.init();

		commandManager.execute(new CreateWindowCommand(desktop,
			new AbstractWindowContent(title, "TODO") {
			    @Override
			    public Widget asWidget() {
				return dataSourcesPresenter.asWidget();
			    }
			}));

		ResourceSet resourceSet = resourceSetsFactory
			.createResourceSet();
		resourceSet.setLabel("Test");
		resourceSet.addAll(ResourcesTestHelper.createResources(1, 2, 3,
			4, 5));
		for (Resource resource : resourceSet) {
		    resource.putValue("date", new Date().toString());
		}

		dataSourcesPresenter.addResourceSet(resourceSet);
	    }

	});

	dataPanel.add(b);
    }

    public void addWidget(String panelId, Widget widget) {
	assert panelId != null;
	assert panels.containsKey(panelId);
	assert widget != null;

	panels.get(panelId).add(widget);
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

    private void createWindow(String contentType) {
	commandManager.execute(new CreateWindowCommand(desktop,
		windowContentProducer.createWindowContent(contentType)));
    }

    public void init() {
	BrowserDetect.checkBrowser();

	DockPanel mainPanel = createMainPanel();

	initDesktop(mainPanel);
	initActionBar(mainPanel);
	initAuthenticationBar();

	initWorkspacePanel();
	initCommandManagerPresenter();

	initCustomActions();

	loadWorkspaceIfParamSet();
    }

    protected void initCustomActions() {
	addDataSourcesButton();
	addTestDataSourceButton();

	addWindowContentButton(HELP_PANEL, "?", "help");
	addInfoButton();

	addWindowContentButton(VIEWS_PANEL, "Note", "note");
	addWindowContentButton(VIEWS_PANEL, "List", ListViewContentDisplay.TYPE);
	addWindowContentButton(VIEWS_PANEL, "Map", "Map");
	addWindowContentButton(VIEWS_PANEL, "Timeline", "Timeline");
	addWindowContentButton(VIEWS_PANEL, "Graph", "Graph");
    }

    private DockPanel createMainPanel() {
	DockPanel mainPanel = new DockPanel();
	RootPanel.get().add(mainPanel);
	return mainPanel;
    }

    private void initAuthenticationBar() {
	((VerticalPanel) actionBar.asWidget()).add(authenticationBar);
    }

    private void initActionBar(DockPanel mainPanel) {
	mainPanel.add(actionBar.asWidget(), DockPanel.NORTH);

	addPanel(WORKSPACE_PANEL, "Workspace");
	addPanel(EDIT_PANEL, "Edit");

	initCustomPanels();
    }

    protected void initCustomPanels() {
	addPanel(VIEWS_PANEL, "Views");
	addPanel(DATA_PANEL, "Data Sources");
	addPanel(HELP_PANEL, "Help");
    }

    private void initCommandManagerPresenter() {
	CommandManagerPresenter presenter = new CommandManagerPresenter(
		commandManager, commandManagerPresenterDisplay);

	presenter.init();

	addWidget(EDIT_PANEL, commandManagerPresenterDisplay.getUndoButton());
	addWidget(EDIT_PANEL, commandManagerPresenterDisplay.getRedoButton());
    }

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
	ButtonDisplay newButton = commandPresenterFactory.createCommandButton(
		"New", newWorkspaceCommand);
	addWidget(WORKSPACE_PANEL, newButton);

	// load workspace
	ButtonDisplay loadButton = commandPresenterFactory.createCommandButton(
		"Load...", loadWorkspaceDialogCommand);
	addWidget(WORKSPACE_PANEL, loadButton);
	new AuthenticationBasedEnablingStateWrapper(authenticationManager,
		loadButton).init();

	// save workspace
	ButtonDisplay saveButton = commandPresenterFactory.createCommandButton(
		"Save", saveWorkspaceCommand);
	addWidget(WORKSPACE_PANEL, saveButton);
	saveButton.setWidth("60px");
	AuthenticationBasedEnablingStateWrapper authWrapper = new AuthenticationBasedEnablingStateWrapper(
		authenticationManager, saveButton);
	authWrapper.init();
	new SaveButtonUpdater(workspaceManager, saveButton, authWrapper).init();

	// share workspace
	ButtonDisplay shareButton = commandPresenterFactory
		.createCommandButton("Share", shareWorkspaceCommand);
	addWidget(WORKSPACE_PANEL, shareButton);
	new AuthenticationBasedEnablingStateWrapper(authenticationManager,
		shareButton).init();
    }

    private void loadWorkspaceIfParamSet() {
	// TODO extract constant
	String workspaceIdParam = Window.Location.getParameter("workspaceId");
	if (workspaceIdParam != null) {
	    long workspaceID = Long.parseLong(workspaceIdParam);

	    LoadWorkspaceCommand loadWorkspaceCommand = new LoadWorkspaceCommand(
		    workspaceID, "", workspacePersistenceManager);
	    blockingCommandExecutor.execute(loadWorkspaceCommand);
	}
    }

}
