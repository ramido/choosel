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
package org.thechiselgroup.choosel.example.workbench.client;

import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.visualization_component.graph.client.ArcTypeProvider;
import org.thechiselgroup.choosel.visualization_component.graph.client.DefaultArcTypeProvider;
import org.thechiselgroup.choosel.visualization_component.graph.client.GraphExpansionRegistry;
import org.thechiselgroup.choosel.workbench.client.ChooselWorkbench;
import org.thechiselgroup.choosel.workbench.client.ChooselWorkbenchClientModule;

import com.google.inject.Provider;
import com.google.inject.Singleton;

public class ChooselExampleClientModule extends ChooselWorkbenchClientModule {

    @Override
    protected void bindCustomServices() {
        // Graph visualization bindings
        bind(ArcTypeProvider.class).to(DefaultArcTypeProvider.class).in(
                Singleton.class);
        bind(GraphExpansionRegistry.class).to(
                ChooselExampleGraphExpansionRegistry.class).in(Singleton.class);
    }

    @Override
    protected Class<? extends Provider<ViewContentDisplaysConfiguration>> getViewContentDisplaysConfigurationProvider() {
        return ChooselExampleWorkbenchViewContentDisplaysConfigurationProvider.class;
    }

    @Override
    protected Class<? extends ChooselWorkbench> getApplicationInitializer() {
        return ChooselExampleWorkbench.class;
    }

}