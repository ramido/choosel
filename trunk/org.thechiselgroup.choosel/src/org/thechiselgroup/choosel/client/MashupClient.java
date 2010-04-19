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

import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBasedEnablingStateWrapper;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.ui.CommandManagerPresenter;
import org.thechiselgroup.choosel.client.command.ui.DefaultCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenter.ButtonDisplay;
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
import org.thechiselgroup.choosel.client.views.list.ListViewContentDisplay;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.CreateWindowCommand;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.workspace.SaveButtonUpdater;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter.DefaultWorkspacePresenterDisplay;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceCommand;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ColumnChart;

// TODO refactor / split up this whole class to facilitate DI
public class MashupClient implements EntryPoint {

    // TODO better modularization (multiple injectors etc.)
    public static final MashupGinjector injector = GWT
	    .create(MashupGinjector.class);

    {
	// resolve initialization cycles
	injector.getProxyViewFactoryResolver().setDelegate(
		injector.getViewFactory());
    }

    private Desktop desktop;

    private static class DataSourceCallBack implements
	    AsyncCallback<Set<Resource>> {

	private String label;

	private ResourceSetsPresenter dataSourcesPresenter;

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

    private HorizontalPanel viewsPanel;

    private HorizontalPanel workspacePanel;

    private HorizontalPanel dataPanel;

    private HorizontalPanel helpPanel;

    private ResourceSetAvatarFactory defaultDragAvatarFactory;

    private ResourceSetFactory resourceSetsFactory;

    private HorizontalPanel editPanel;

    private CommandManager commandManager;

    private HorizontalPanel searchPanel;

    private ActionBar actionBar;

    public void onModuleLoad() {
	initFields();

	BrowserDetect.checkBrowser();

	// currently disabled testing stuff
	// addGenericSend();
	// loadVisualization();

	DockPanel mainPanel = new DockPanel();
	RootPanel.get().add(mainPanel);

	initDesktop(mainPanel);
	initActionBar(mainPanel);

	((VerticalPanel) actionBar.asWidget()).add(injector
		.createAuthenticationBar());

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

	// addSparqlQueryButton();

	// injector.getIPLocatorService().getClientLocation(

	// dataSourceAddedCallback);

	// TODO extract constant
	String workspaceIdParam = Window.Location.getParameter("workspaceId");
	if (workspaceIdParam != null) {
	    long workspaceID = Long.parseLong(workspaceIdParam);

	    LoadWorkspaceCommand loadWorkspaceCommand = new LoadWorkspaceCommand(
		    workspaceID, "", injector.getWorkspacePersistenceManager());
	    injector.getBlockingCommandExecutor().execute(loadWorkspaceCommand);
	}
    }

    private void initFields() {
	commandManager = injector.getCommandManager();
	defaultDragAvatarFactory = injector.getDefaultDragAvatarFactory();
	resourceSetsFactory = injector.getResourceSetFactory();
    }

    private void initDesktop(DockPanel mainPanel) {
	desktop = injector.getDesktop();

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

    private void initActionBar(DockPanel mainPanel) {
	actionBar = injector.getActionBar();
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

    private void initWorkspacePresenter() {
	// title area
	// TODO refactor title area part
	DefaultWorkspacePresenterDisplay workspacePresenterDisplay = injector
		.createWorkspacePresenterDisplay();
	WorkspacePresenter presenter = new WorkspacePresenter(injector
		.getWorkspaceManager(), workspacePresenterDisplay);
	presenter.init();
	// TODO replace with stuff from workspace presenter
	workspacePresenterDisplay.getTextBox().addStyleName(
		"actionbar-titleArea-text");
	actionBar.getActionBarTitleArea().add(
		workspacePresenterDisplay.getTextBox());

	// new workspace
	final ButtonDisplay newButton = injector.getCommandPresenterFactory()
		.createCommandButton("New", injector.getNewWorkspaceCommand());
	workspacePanel.add(newButton);

	// load workspace
	final ButtonDisplay loadButton = injector.getCommandPresenterFactory()
		.createCommandButton("Load...",
			injector.getLoadWorkspaceDialogCommand());
	workspacePanel.add(loadButton);
	new AuthenticationBasedEnablingStateWrapper(injector
		.getAuthenticationManager(), loadButton).init();

	// save workspace
	final ButtonDisplay saveButton = injector
		.getCommandPresenterFactory()
		.createCommandButton("Save", injector.getSaveWorkspaceCommand());
	workspacePanel.add(saveButton);
	saveButton.setWidth("60px");
	AuthenticationBasedEnablingStateWrapper authWrapper = new AuthenticationBasedEnablingStateWrapper(
		injector.getAuthenticationManager(), saveButton);
	authWrapper.init();
	new SaveButtonUpdater(injector.getWorkspaceManager(), saveButton,
		authWrapper).init();

	// share workspace
	final ButtonDisplay shareButton = injector.getCommandPresenterFactory()
		.createCommandButton("Share",
			injector.getShareWorkspaceCommand());
	workspacePanel.add(shareButton);
	new AuthenticationBasedEnablingStateWrapper(injector
		.getAuthenticationManager(), shareButton).init();
    }

    private void initCommandManagerPresenter() {
	DefaultCommandManagerPresenterDisplay display = injector
		.createCommandManagerPresenterDisplay();

	CommandManagerPresenter presenter = new CommandManagerPresenter(
		commandManager, display);

	presenter.init();

	editPanel.add(display.getUndoButton());
	editPanel.add(display.getRedoButton());
    }

    private void addSparqlQueryButton() {
	Button button = new Button("SPARQL");
	button.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		injector.getSparqlQueryService().runSparqlQuery(
			new AsyncCallback<String>() {
			    @Override
			    public void onSuccess(String result) {
				desktop.asWidget().add(new Label(result));
			    }

			    @Override
			    public void onFailure(Throwable caught) {
				desktop.asWidget().add(
					new Label(caught.getMessage()));
			    }
			});
	    }

	});
	dataPanel.add(button);
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

		injector
			.getGeoRSSService()
			.getGeoRSS(
				"http://earthquake.usgs.gov/eqcenter/catalogs/shakerss.xml",
				"earthquake",
				new DataSourceCallBack("earthquake",
					dataSourcesPresenter,
					resourceSetsFactory));
		injector
			.getGeoRSSService()
			.getGeoRSS(
				"http://www.prh.noaa.gov/ptwc/feeds/ptwc_rss_pacific.xml",
				"tsunami",
				new DataSourceCallBack("tsunami",
					dataSourcesPresenter,
					resourceSetsFactory));
	    }

	});
	dataPanel.add(b);
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

    private void addInfoButton() {
	Button b = new Button("About");
	b.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		injector.getDialogManager().show(injector.getInfoDialog());
	    }
	});
	helpPanel.add(b);
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

    private void initNCBOSearchField() {
	TextCommandPresenter presenter = new TextCommandPresenter(injector
		.getNCBOConceptSearchCommand(), "Search");

	presenter.init();

	searchPanel.add(presenter.getTextBox());
	searchPanel.add(presenter.getExecuteButton());
    }

    private void createHelpWindow() {
	commandManager.execute(new CreateWindowCommand(desktop,
		new HelpWindowContent()));
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

    private void createGraphView() {
	createWindowForViewType("Graph");
    }

    // TODO remove
    private void addGenericSend() {
	final Button sendButton = new Button("Send");
	final TextBox nameField = new TextBox();
	final Label result = new Label();

	desktop.asWidget().add(nameField);
	desktop.asWidget().add(sendButton);
	desktop.asWidget().add(result);

	sendButton.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		String textToServer = nameField.getText();

		injector.getProxyService().fetchURL(textToServer,
			new AsyncCallback<String>() {
			    public void onFailure(Throwable e) {
				result.setText(e.getMessage());
			    }

			    public void onSuccess(String x) {
				result.setText(x);
			    }
			});
	    }
	});
    }

    private void loadVisualization() {
	// Create a callback to be called when the visualization API
	// has been loaded.
	Runnable onLoadCallback = new Runnable() {
	    public void run() {
		Panel panel = injector.getDesktop().asWidget();

		// Create a pie chart visualization.
		ColumnChart barChart = new ColumnChart(createTable(),
			createOptions());

		panel.add(barChart);

		barChart.draw(createTable2(), createOptions());
	    }

	    private ColumnChart.Options createOptions() {
		ColumnChart.Options options = ColumnChart.Options.create();
		options.setWidth(400);
		options.setHeight(240);
		options.set3D(true);
		options.setTitle("My Daily Activities");
		options.setMin(0);
		return options;
	    }

	    private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		// data.addColumn(ColumnType.STRING, "Task");
		// data.addColumn(ColumnType.NUMBER, "Hours per Day");
		data.addRows(0);
		// data.setValue(0, 0, "Work");
		// data.setValue(0, 1, 14);
		// data.setValue(1, 0, "Sleep");
		// data.setValue(1, 1, 10);
		return data;
	    }

	    private AbstractDataTable createTable2() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Task");
		data.addColumn(ColumnType.NUMBER, "Hours per Day");
		data.addRows(2);
		data.setValue(0, 0, "Work");
		data.setValue(0, 1, 14);
		data.setValue(1, 0, "Sleep");
		data.setValue(1, 1, 10);
		return data;
	    }
	};

	// Load the visualization api, passing the onLoadCallback to be called
	// when loading is done.
	VisualizationUtils.loadVisualizationApi(onLoadCallback,
		ColumnChart.PACKAGE);
    }

    private void createWindowForViewType(String viewType) {
	commandManager.execute(new CreateWindowCommand(desktop,
		createView(viewType)));
    }

    private WindowContent createView(String viewType) {
	return injector.getViewFactory().createWindowContent(viewType);
    }

}
