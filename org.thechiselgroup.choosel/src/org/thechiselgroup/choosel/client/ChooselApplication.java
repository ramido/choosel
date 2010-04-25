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
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchCommand;
import org.thechiselgroup.choosel.client.domain.other.GeoRSSServiceAsync;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.ui.ActionBar;
import org.thechiselgroup.choosel.client.ui.HelpWindowContent;
import org.thechiselgroup.choosel.client.ui.TextCommandPresenter;
import org.thechiselgroup.choosel.client.ui.dialog.DialogManager;
import org.thechiselgroup.choosel.client.views.list.ListViewContentDisplay;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.CreateWindowCommand;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContent;
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

    @Inject
    private ActionBar actionBar;

    @Inject
    private AuthenticationBar authenticationBar;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private CommandManager commandManager;

    @Inject
    private CommandPresenterFactory commandPresenterFactory;

    private HorizontalPanel dataPanel;

    @Inject
    private ResourceSetAvatarFactory defaultDragAvatarFactory;

    @Inject
    private Desktop desktop;

    @Inject
    private DialogManager dialogManager;

    private HorizontalPanel editPanel;

    @Inject
    private GeoRSSServiceAsync geoRssService;

    private HorizontalPanel helpPanel;

    @Inject
    private InfoDialog infoDialog;

    @Inject
    private LoadWorkspaceDialogCommand loadWorkspaceDialogCommand;

    @Inject
    private NewWorkspaceCommand newWorkspaceCommand;

    @Inject
    private ResourceSetFactory resourceSetsFactory;

    @Inject
    private SaveWorkspaceCommand saveWorkspaceCommand;

    private HorizontalPanel searchPanel;

    @Inject
    private ShareWorkspaceCommand shareWorkspaceCommand;

    private HorizontalPanel viewsPanel;

    @Inject
    private WindowContentProducer windowContentProducer;

    @Inject
    private WorkspacePersistenceManager workspacePersistenceManager;

    @Inject
    private WorkspaceManager workspaceManager;

    private HorizontalPanel workspacePanel;

    @Inject
    private NCBOConceptSearchCommand ncboConceptSearchCommand;

    @Inject
    private DefaultCommandManagerPresenterDisplay commandManagerPresenterDisplay;

    @Inject
    private AsyncCommandExecutor blockingCommandExecutor;

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

    private void addGraphButton() {
	Button b = new Button("Graph");
	b.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		createGraphView();
	    }

	});
	viewsPanel.add(b);
    }

    private void addHelpButton() {
	Button b = new Button("?");
	b.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		createHelpWindow();
	    }

	});
	helpPanel.add(b);
    }

    private void addInfoButton() {
	Button b = new Button("About");
	b.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		dialogManager.show(infoDialog);
	    }
	});
	helpPanel.add(b);
    }

    private void addListButton() {
	Button button = new Button(ListViewContentDisplay.TYPE);
	button.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		createWindowForViewType(ListViewContentDisplay.TYPE);
	    }

	});
	viewsPanel.add(button);
    }

    private void addMapButton() {
	Button mapButton = new Button("Map");
	mapButton.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		createWindowForViewType("Map");
	    }

	});
	viewsPanel.add(mapButton);
    }

    private void addNoteButton() {
	Button button = new Button("Note");
	button.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		createWindowForViewType("note");
	    }

	});
	viewsPanel.add(button);
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

    private void addTimelineButton() {
	Button mapButton = new Button("Timeline");
	mapButton.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		createWindowForViewType("Timeline");
	    }

	});
	viewsPanel.add(mapButton);
    }

    private void createGraphView() {
	createWindowForViewType("Graph");
    }

    private void createHelpWindow() {
	commandManager.execute(new CreateWindowCommand(desktop,
		new HelpWindowContent()));
    }

    private WindowContent createWindowContent(String viewType) {
	return windowContentProducer.createWindowContent(viewType);
    }

    private void createWindowForViewType(String viewType) {
	commandManager.execute(new CreateWindowCommand(desktop,
		createWindowContent(viewType)));
    }

    public void init() {
	BrowserDetect.checkBrowser();

	DockPanel mainPanel = new DockPanel();
	RootPanel.get().add(mainPanel);

	initDesktop(mainPanel);
	initActionBar(mainPanel);

	((VerticalPanel) actionBar.asWidget()).add(authenticationBar);

	initWorkspacePresenter();
	initCommandManagerPresenter();

	initNCBOSearchField();
	addDataSourcesButton();
	addHelpButton();
	addInfoButton();

	addNoteButton();
	addListButton();
	addMapButton();
	addTimelineButton();
	addGraphButton();

	addTestDataSourceButton();

	// TODO extract constant
	String workspaceIdParam = Window.Location.getParameter("workspaceId");
	if (workspaceIdParam != null) {
	    long workspaceID = Long.parseLong(workspaceIdParam);

	    LoadWorkspaceCommand loadWorkspaceCommand = new LoadWorkspaceCommand(
		    workspaceID, "", workspacePersistenceManager);
	    blockingCommandExecutor.execute(loadWorkspaceCommand);
	}
    }

    private void initActionBar(DockPanel mainPanel) {
	mainPanel.add(actionBar.asWidget(), DockPanel.NORTH);

	workspacePanel = new HorizontalPanel();
	workspacePanel.setSpacing(2);
	actionBar.addPanel("Workspace", workspacePanel);

	editPanel = new HorizontalPanel();
	editPanel.setSpacing(2);
	actionBar.addPanel("Edit", editPanel);

	viewsPanel = new HorizontalPanel();
	viewsPanel.setSpacing(2);
	actionBar.addPanel("Views", viewsPanel);

	searchPanel = new HorizontalPanel();
	searchPanel.setSpacing(2);
	actionBar.addPanel("NCBO Concept Search", searchPanel);

	dataPanel = new HorizontalPanel();
	dataPanel.setSpacing(2);
	actionBar.addPanel("Data Sources", dataPanel);

	helpPanel = new HorizontalPanel();
	helpPanel.setSpacing(2);
	actionBar.addPanel("Help", helpPanel);
    }

    private void initCommandManagerPresenter() {
	CommandManagerPresenter presenter = new CommandManagerPresenter(
		commandManager, commandManagerPresenterDisplay);

	presenter.init();

	editPanel.add(commandManagerPresenterDisplay.getUndoButton());
	editPanel.add(commandManagerPresenterDisplay.getRedoButton());
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

    private void initNCBOSearchField() {
	TextCommandPresenter presenter = new TextCommandPresenter(
		ncboConceptSearchCommand, "Search");

	presenter.init();

	searchPanel.add(presenter.getTextBox());
	searchPanel.add(presenter.getExecuteButton());
    }

    @Inject
    private DefaultWorkspacePresenterDisplay workspacePresenterDisplay;

    private void initWorkspacePresenter() {
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
	workspacePanel.add(newButton);

	// load workspace
	ButtonDisplay loadButton = commandPresenterFactory.createCommandButton(
		"Load...", loadWorkspaceDialogCommand);
	workspacePanel.add(loadButton);
	new AuthenticationBasedEnablingStateWrapper(authenticationManager,
		loadButton).init();

	// save workspace
	ButtonDisplay saveButton = commandPresenterFactory.createCommandButton(
		"Save", saveWorkspaceCommand);
	workspacePanel.add(saveButton);
	saveButton.setWidth("60px");
	AuthenticationBasedEnablingStateWrapper authWrapper = new AuthenticationBasedEnablingStateWrapper(
		authenticationManager, saveButton);
	authWrapper.init();
	new SaveButtonUpdater(workspaceManager, saveButton, authWrapper).init();

	// share workspace
	ButtonDisplay shareButton = commandPresenterFactory
		.createCommandButton("Share", shareWorkspaceCommand);
	workspacePanel.add(shareButton);
	new AuthenticationBasedEnablingStateWrapper(authenticationManager,
		shareButton).init();
    }

}
