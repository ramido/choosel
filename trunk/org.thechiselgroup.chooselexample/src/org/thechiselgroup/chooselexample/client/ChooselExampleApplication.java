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

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_BAR;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_CIRCULAR_BAR;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_DOT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_GRAPH;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_MAP;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_PIE;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_SCATTER;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TEXT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TIME;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TIMELINE;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.WINDOW_CONTENT_CSV_IMPORT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.WINDOW_CONTENT_NOTE;

import java.util.Date;

import org.thechiselgroup.choosel.client.ChooselApplication;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;

public class ChooselExampleApplication extends ChooselApplication {

	public static final String DATA_PANEL = "data";

	// TODO change into command
	private void addTestDataSourceButton() {
		Button b = new Button("T-Data");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
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

				dataSourceResourceSetsPresenter.addResourceSet(resourceSet);

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

				dataSourceResourceSetsPresenter
						.addResourceSet(graphResourceSet);
			}

		});

		addWidget(DEVELOPER_MODE_PANEL, b);
	}

	@Override
	protected void initCustomActions() {
		if (runsInDevelopmentMode()) {
			addTestDataSourceButton();

			addWindowContentButton(DEVELOPER_MODE_PANEL, "Graph", TYPE_GRAPH);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Circular Bar",
					TYPE_CIRCULAR_BAR);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Time", TYPE_TIME);
			addWindowContentButton(DEVELOPER_MODE_PANEL, "Dot", TYPE_DOT);
		}

		addWindowContentButton(VIEWS_PANEL, "Note", WINDOW_CONTENT_NOTE);
		addWindowContentButton(VIEWS_PANEL, "Text", TYPE_TEXT);
		addWindowContentButton(VIEWS_PANEL, "Map", TYPE_MAP);
		addWindowContentButton(VIEWS_PANEL, "Timeline", TYPE_TIMELINE);
		addWindowContentButton(VIEWS_PANEL, "Bar Chart", TYPE_BAR);
		addWindowContentButton(VIEWS_PANEL, "Pie Chart", TYPE_PIE);
		addWindowContentButton(VIEWS_PANEL, "Scatter Plot", TYPE_SCATTER);

		addWindowContentButton(DATA_PANEL, "Import", WINDOW_CONTENT_CSV_IMPORT);
		addWidget(DATA_PANEL, dataSourceResourceSetsPresenter.asWidget());
	}

	@Override
	protected void initCustomPanels() {
		addToolbarPanel(DATA_PANEL, "Data Sources");
		addToolbarPanel(VIEWS_PANEL, "Views");
	}
}