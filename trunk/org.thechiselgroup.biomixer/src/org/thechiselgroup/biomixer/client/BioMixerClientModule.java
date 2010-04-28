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

import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsyncClientImplementation;
import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOConceptSearchServiceAsync;
import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOConceptSearchServiceAsyncClientImplementation;
import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsyncClientImplementation;
import org.thechiselgroup.choosel.client.ChooselApplication;
import org.thechiselgroup.choosel.client.ChooselClientModule;
import org.thechiselgroup.choosel.client.ChooselWindowContentProducerProvider;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceAsync;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class BioMixerClientModule extends ChooselClientModule {

    @Override
    protected Class<? extends DetailsWidgetHelper> getDetailsWidgetHelperClass() {
	return BioMixerDetailsWidgetHelper.class;
    }

    @Override
    protected Class<? extends SlotResolver> getSlotResolverClass() {
	return BioMixerSlotResolver.class;
    }

    @Override
    protected Class<? extends ChooselWindowContentProducerProvider> getContentProducerProviderClass() {
	return BioMixerWindowContentProducerProvider.class;
    }

    @Override
    protected Class<? extends ChooselApplication> getApplicationClass() {
	return BioMixerApplication.class;
    }

    @Override
    protected void bindCustomServices() {
	bind(NCBOConceptSearchServiceAsync.class).to(
		NCBOConceptSearchServiceAsyncClientImplementation.class).in(
		Singleton.class);
	bind(NeighbourhoodServiceAsync.class).annotatedWith(
		Names.named("concept")).to(
		NCBOConceptNeighbourhoodServiceAsyncClientImplementation.class)
		.in(Singleton.class);
	bind(NeighbourhoodServiceAsync.class).annotatedWith(
		Names.named("mapping")).to(
		NCBOMappingNeighbourhoodServiceAsyncClientImplementation.class)
		.in(Singleton.class);
    }
}