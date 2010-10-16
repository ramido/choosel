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
package org.thechiselgroup.chooselexample.client;

import java.util.Date;

import org.thechiselgroup.choosel.client.ChooselApplication;
import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.CreateWindowCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class ChooselExampleApplication extends ChooselApplication {

	public static final String DATA_PANEL = "data";

	// TODO change into command
	private void addTestDataSourceButton() {
		Button b = new Button("T-Data");
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

				int counter = 0;
				ResourceSet resourceSet = createResourceSet();
				resourceSet.setLabel("Test");
				for (int i = 0; i < 50; i++)
					resourceSet.add(TestResourceSetFactory.createResource(i));
				for (Resource resource : resourceSet) {
					resource.putValue("date", new Date(
							1281991537 + 100000 * (counter++)).toString());
					resource.putValue("magnitude", Random.nextInt(10));
					int category = Random.nextInt(10);
					resource.putValue("tagContent", "test" + category);
					resource.putValue("label", "test" + category);
				}

				dataSourcesPresenter.addResourceSet(resourceSet);

				ResourceSet graphResourceSet = createResourceSet();
				graphResourceSet.setLabel("GraphTest");
				for (int i = 0; i < 25; i++) {
					Resource resource = new Resource("graphtest:" + i);
					resource.putValue("title", "graphtest:" + i);
					if (i > 0) {
						resource.putValueAsUriList("parent", "graphtest:"
								+ (i - 1));
					}
					graphResourceSet.add(resource);
				}

				dataSourcesPresenter.addResourceSet(graphResourceSet);
			}

		});

		addWidget(DEVELOPER_MODE_PANEL, b);
	}

	@Override
	protected void initCustomActions() {
		if (runsInDevelopmentMode()) {
			addTestDataSourceButton();

			addWindowContentButton(DEVELOPER_MODE_PANEL, "Tag Cloud",
					ChooselInjectionConstants.TYPE_TAG_CLOUD);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Graph",
					ChooselInjectionConstants.TYPE_GRAPH);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Circular Bar",
					ChooselInjectionConstants.TYPE_CIRCULAR_BAR);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Time",
					ChooselInjectionConstants.TYPE_TIME);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Dot",
					ChooselInjectionConstants.TYPE_DOT);
		}

		addWindowContentButton(VIEWS_PANEL, "Note",
				ChooselInjectionConstants.WINDOW_CONTENT_NOTE);
		addWindowContentButton(VIEWS_PANEL, "List",
				ChooselInjectionConstants.TYPE_LIST);
		addWindowContentButton(VIEWS_PANEL, "Map",
				ChooselInjectionConstants.TYPE_MAP);
		addWindowContentButton(VIEWS_PANEL, "Timeline",
				ChooselInjectionConstants.TYPE_TIMELINE);
		addWindowContentButton(VIEWS_PANEL, "Bar Chart",
				ChooselInjectionConstants.TYPE_BAR);
		addWindowContentButton(VIEWS_PANEL, "Pie Chart",
				ChooselInjectionConstants.TYPE_PIE);
		addWindowContentButton(VIEWS_PANEL, "Scatter Plot",
				ChooselInjectionConstants.TYPE_SCATTER);

		addWindowContentButton(DATA_PANEL, "CSV Import",
				ChooselInjectionConstants.WINDOW_CONTENT_CSV_IMPORT);
	}

	@Override
	protected void initCustomPanels() {
		addToolbarPanel(DATA_PANEL, "Data Sources");
		addToolbarPanel(VIEWS_PANEL, "Views");
	}
}