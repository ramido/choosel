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

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SET;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.DROP_TARGET_MANAGER_VIEW_CONTENT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET;

import org.thechiselgroup.biomixer.client.services.NCBOSearchWindowContent;
import org.thechiselgroup.choosel.client.ChooselWindowContentProducerProvider;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BioMixerWindowContentProducerProvider extends
		ChooselWindowContentProducerProvider {

	@Inject
	protected NCBOSearchWindowContent nCBOSearchViewContent;

	@Inject
	public BioMixerWindowContentProducerProvider(
			@Named(AVATAR_FACTORY_SET) ResourceSetAvatarFactory userSetsDragAvatarFactory,
			@Named(AVATAR_FACTORY_ALL_RESOURCES) ResourceSetAvatarFactory allResourcesDragAvatarFactory,
			@Named(AVATAR_FACTORY_SELECTION) ResourceSetAvatarFactory selectionDragAvatarFactory,
			@Named(AVATAR_FACTORY_SELECTION_DROP) ResourceSetAvatarFactory dropTargetFactory,
			ResourceSetFactory resourceSetFactory,
			@Named(LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
			ResourceMultiCategorizer categorizer,
			CategoryLabelProvider labelProvider,
			@Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager,
			SlotResolver slotResolver,
			ResourceCategorizer resourceByTypeCategorizer) {

		super(userSetsDragAvatarFactory, allResourcesDragAvatarFactory,
				selectionDragAvatarFactory, dropTargetFactory,
				resourceSetFactory, selectionModelLabelFactory, categorizer,
				labelProvider, contentDropTargetManager, slotResolver,
				resourceByTypeCategorizer);

		windowContentFactories.put("ncbo-search", new WindowContentFactory() {
			@Override
			public WindowContent createWindowContent() {
				return nCBOSearchViewContent;
			}
		});
	}
}
