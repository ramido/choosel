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

import org.thechiselgroup.choosel.client.ChooselApplication;
import org.thechiselgroup.choosel.client.ChooselClientModule;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsyncClientImplementation;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchServiceAsyncClientImplementation;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsyncClientImplementation;

import com.google.inject.Singleton;

public class BioMixerClientModule extends ChooselClientModule {

    @Override
    protected Class<? extends ChooselApplication> getApplicationClass() {
	return BioMixerApplication.class;
    }

    @Override
    protected void bindCustomServices() {
	bind(NCBOConceptSearchServiceAsync.class).to(
		NCBOConceptSearchServiceAsyncClientImplementation.class).in(
		Singleton.class);
	bind(NCBOConceptNeighbourhoodServiceAsync.class).to(
		NCBOConceptNeighbourhoodServiceAsyncClientImplementation.class)
		.in(Singleton.class);
	bind(NCBOMappingNeighbourhoodServiceAsync.class).to(
		NCBOMappingNeighbourhoodServiceAsyncClientImplementation.class)
		.in(Singleton.class);
    }
}