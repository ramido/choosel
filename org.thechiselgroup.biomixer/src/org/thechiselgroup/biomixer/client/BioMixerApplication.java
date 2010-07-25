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
package org.thechiselgroup.biomixer.client;

import org.thechiselgroup.biomixer.client.services.NCBOConceptSearchCommand;
import org.thechiselgroup.choosel.client.ChooselApplication;
import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.ui.TextCommandPresenter;

import com.google.inject.Inject;

public class BioMixerApplication extends ChooselApplication {

	public static final String NCBO_SEARCH = "ncbo-search";

	@Inject
	private NCBOConceptSearchCommand ncboConceptSearchCommand;

	@Override
	protected void initCustomActions() {
		initNCBOSearchField();

		addWindowContentButton(VIEWS_PANEL, "Note", "note");
		addWindowContentButton(VIEWS_PANEL, "List", ChooselInjectionConstants.TYPE_LIST);
		addWindowContentButton(VIEWS_PANEL, "Timeline", "Timeline");
		addWindowContentButton(VIEWS_PANEL, "Graph", "Graph");

		addWindowContentButton(HELP_PANEL, "?", "help");
		addInfoButton();
	}

	@Override
	protected void initCustomPanels() {
		addPanel(VIEWS_PANEL, "Views");
		addPanel(NCBO_SEARCH, "NCBO Concept Search");
		addPanel(HELP_PANEL, "Help");
	}

	private void initNCBOSearchField() {
		TextCommandPresenter presenter = new TextCommandPresenter(
				ncboConceptSearchCommand, "Search");

		presenter.init();

		addWidget(NCBO_SEARCH, presenter.getTextBox());
		addWidget(NCBO_SEARCH, presenter.getExecuteButton());
	}

}
