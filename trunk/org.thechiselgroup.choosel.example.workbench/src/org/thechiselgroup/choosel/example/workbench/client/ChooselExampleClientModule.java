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

import org.thechiselgroup.choosel.core.client.visualization.model.initialization.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.PreconfiguredVisualItemResolverUIFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIControllerFactoryProvider;
import org.thechiselgroup.choosel.visualization_component.graph.client.ArcTypeProvider;
import org.thechiselgroup.choosel.visualization_component.graph.client.DefaultArcTypeProvider;
import org.thechiselgroup.choosel.visualization_component.graph.client.GraphExpansionRegistry;
import org.thechiselgroup.choosel.workbench.client.ChooselWorkbenchClientModule;
import org.thechiselgroup.choosel.workbench.client.init.WorkbenchInitializer;

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
    protected Class<? extends VisualItemValueResolverFactoryProvider> getResolverFactoryProviderClass() {
        return PreconfiguredVisualItemValueResolverFactoryProvider.class;
    }

    @Override
    protected Class<? extends VisualItemValueResolverUIControllerFactoryProvider> getResolverFactoryUIProviderClass() {
        return PreconfiguredVisualItemResolverUIFactoryProvider.class;
    }

    @Override
    protected Class<? extends Provider<ViewContentDisplaysConfiguration>> getViewContentDisplaysConfigurationProvider() {
        return ChooselExampleWorkbenchViewContentDisplaysConfigurationProvider.class;
    }

    @Override
    protected Class<? extends WorkbenchInitializer> getWorkbenchInitializer() {
        return ChooselExampleWorkbench.class;
    }

}