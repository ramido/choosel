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

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SET;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.DROP_TARGET_MANAGER_VIEW_CONTENT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_BAR;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_CIRCULAR_BAR;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_DOT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_GRAPH;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_LIST;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_MAP;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_PIE;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_SCATTER;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TAG_CLOUD;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TIME;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TIMELINE;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.HelpWindowContentFactory;
import org.thechiselgroup.choosel.client.ui.ImportCSVWindowContentFactory;
import org.thechiselgroup.choosel.client.ui.NoteWindowContentFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.DefaultResourceSetToValueResolverFactory;
import org.thechiselgroup.choosel.client.views.DefaultWindowContentProducer;
import org.thechiselgroup.choosel.client.views.HoverModel;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayFactory;
import org.thechiselgroup.choosel.client.views.ViewFactory;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ChooselWindowContentProducerProvider implements
        Provider<WindowContentProducer> {

    private ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    private ResourceMultiCategorizer categorizer;

    private ResourceSetAvatarDropTargetManager contentDropTargetManager;

    private ResourceSetAvatarFactory dropTargetFactory;

    private CategoryLabelProvider labelProvider;

    private ResourceSetFactory resourceSetFactory;

    private DefaultResourceSetToValueResolverFactory resourceSetToValueResolverFactory;

    private ResourceSetAvatarFactory selectionDragAvatarFactory;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetAvatarFactory userSetsDragAvatarFactory;

    protected final Map<String, WindowContentFactory> windowContentFactories = new HashMap<String, WindowContentFactory>();

    private HoverModel hoverModel;

    private final ResourceCategorizer resourceByTypeCategorizer;

    private final PopupManagerFactory popupManagerFactory;

    private final DetailsWidgetHelper detailsWidgetHelper;

    @Inject
    public ChooselWindowContentProducerProvider(
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
            ResourceCategorizer resourceByTypeCategorizer,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper, Desktop desktop,
            CommandManager commandManager,
            ResourceSetAvatarFactory defaultDragAvatarFactory) {

        assert userSetsDragAvatarFactory != null;
        assert allResourcesDragAvatarFactory != null;
        assert selectionDragAvatarFactory != null;
        assert dropTargetFactory != null;
        assert contentDropTargetManager != null;
        assert resourceSetFactory != null;
        assert selectionModelLabelFactory != null;
        assert categorizer != null;
        assert labelProvider != null;
        assert slotResolver != null;
        assert hoverModel != null;
        assert popupManagerFactory != null;
        assert detailsWidgetHelper != null;

        this.userSetsDragAvatarFactory = userSetsDragAvatarFactory;
        this.allResourcesDragAvatarFactory = allResourcesDragAvatarFactory;
        this.selectionDragAvatarFactory = selectionDragAvatarFactory;
        this.dropTargetFactory = dropTargetFactory;
        this.contentDropTargetManager = contentDropTargetManager;
        this.resourceSetFactory = resourceSetFactory;
        this.selectionModelLabelFactory = selectionModelLabelFactory;
        this.categorizer = categorizer;
        this.labelProvider = labelProvider;
        this.hoverModel = hoverModel;
        this.resourceByTypeCategorizer = resourceByTypeCategorizer;
        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;

        this.resourceSetToValueResolverFactory = new DefaultResourceSetToValueResolverFactory(
                slotResolver, resourceByTypeCategorizer);

        windowContentFactories.put(
                ChooselInjectionConstants.WINDOW_CONTENT_HELP,
                new HelpWindowContentFactory());
        windowContentFactories.put(
                ChooselInjectionConstants.WINDOW_CONTENT_NOTE,
                new NoteWindowContentFactory());
        windowContentFactories.put(
                ChooselInjectionConstants.WINDOW_CONTENT_CSV_IMPORT,
                new ImportCSVWindowContentFactory(desktop,
                        defaultDragAvatarFactory, commandManager));
    }

    @Override
    public WindowContentProducer get() {
        DefaultWindowContentProducer contentProducer = new DefaultWindowContentProducer();
        for (Map.Entry<String, WindowContentFactory> entry : windowContentFactories
                .entrySet()) {
            contentProducer.register(entry.getKey(), entry.getValue());
        }
        return contentProducer;
    }

    @Inject
    public void registerBar(@Named(TYPE_BAR) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_BAR, factory);
    }

    @Inject
    public void registerCircularBar(
            @Named(TYPE_CIRCULAR_BAR) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_CIRCULAR_BAR, factory);
    }

    @Inject
    public void registerDot(@Named(TYPE_DOT) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_DOT, factory);
    }

    @Inject
    public void registerGraph(
            @Named(TYPE_GRAPH) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_GRAPH, factory);
    }

    @Inject
    public void registerList(@Named(TYPE_LIST) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_LIST, factory);
    }

    @Inject
    public void registerMap(@Named(TYPE_MAP) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_MAP, factory);
    }

    @Inject
    public void registerPie(@Named(TYPE_PIE) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_PIE, factory);
    }

    @Inject
    public void registerScatter(
            @Named(TYPE_SCATTER) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_SCATTER, factory);
    }

    @Inject
    public void registerTagCloud(
            @Named(TYPE_TAG_CLOUD) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_TAG_CLOUD, factory);
    }

    @Inject
    public void registerTime(@Named(TYPE_TIME) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_TIME, factory);
    }

    @Inject
    public void registerTimeline(
            @Named(TYPE_TIMELINE) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_TIMELINE, factory);
    }

    private void registerViewContentDisplayFactory(String contentType,
            ViewContentDisplayFactory contentDisplayFactory) {

        windowContentFactories.put(contentType, new ViewFactory(contentType,
                contentDisplayFactory, userSetsDragAvatarFactory,
                allResourcesDragAvatarFactory, selectionDragAvatarFactory,
                dropTargetFactory, resourceSetFactory,
                selectionModelLabelFactory, categorizer, labelProvider,
                contentDropTargetManager, resourceSetToValueResolverFactory,
                resourceByTypeCategorizer, hoverModel, popupManagerFactory,
                detailsWidgetHelper));
    }

}