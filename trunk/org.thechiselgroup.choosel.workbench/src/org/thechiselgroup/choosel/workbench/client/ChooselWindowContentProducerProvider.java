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
package org.thechiselgroup.choosel.workbench.client;

import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SET;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.DROP_TARGET_MANAGER_VIEW_CONTENT;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.TYPE_TEXT;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.views.DefaultWindowContentProducer;
import org.thechiselgroup.choosel.core.client.views.HoverModel;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayFactory;
import org.thechiselgroup.choosel.core.client.views.ViewFactory;
import org.thechiselgroup.choosel.core.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.core.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.workbench.client.ui.HelpWindowContentFactory;
import org.thechiselgroup.choosel.workbench.client.ui.NoteWindowContentFactory;
import org.thechiselgroup.choosel.workbench.client.workspace.ShareConfigurationFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ChooselWindowContentProducerProvider implements
        Provider<WindowContentProducer> {

    protected ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    protected ResourceMultiCategorizer categorizer;

    protected ResourceSetAvatarDropTargetManager contentDropTargetManager;

    protected ResourceSetAvatarFactory dropTargetFactory;

    protected CategoryLabelProvider labelProvider;

    protected ResourceSetFactory resourceSetFactory;

    protected ResourceSetAvatarFactory selectionDragAvatarFactory;

    protected LabelProvider selectionModelLabelFactory;

    protected ResourceSetAvatarFactory userSetsDragAvatarFactory;

    /**
     * Maps the content type to a factory that produces a window content of that
     * content type.
     */
    private final Map<String, WindowContentFactory> windowContentFactories = CollectionFactory
            .createStringMap();

    protected HoverModel hoverModel;

    protected PopupManagerFactory popupManagerFactory;

    protected DetailsWidgetHelper detailsWidgetHelper;

    protected ShareConfigurationFactory shareConfigurationFactory;

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
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            ShareConfigurationFactory shareConfigurationFactory) {

        assert userSetsDragAvatarFactory != null;
        assert allResourcesDragAvatarFactory != null;
        assert selectionDragAvatarFactory != null;
        assert dropTargetFactory != null;
        assert contentDropTargetManager != null;
        assert resourceSetFactory != null;
        assert selectionModelLabelFactory != null;
        assert categorizer != null;
        assert labelProvider != null;
        assert hoverModel != null;
        assert popupManagerFactory != null;
        assert detailsWidgetHelper != null;
        assert shareConfigurationFactory != null;

        this.shareConfigurationFactory = shareConfigurationFactory;
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
        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;

        putWindowContentFactory(WINDOW_CONTENT_HELP,
                new HelpWindowContentFactory());
        putWindowContentFactory(WINDOW_CONTENT_NOTE,
                new NoteWindowContentFactory());
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

    protected void putWindowContentFactory(String contentType,
            WindowContentFactory factory) {

        assert contentType != null;
        assert factory != null;

        windowContentFactories.put(contentType, factory);
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
    public void registerText(@Named(TYPE_TEXT) ViewContentDisplayFactory factory) {
        registerViewContentDisplayFactory(TYPE_TEXT, factory);
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

        putWindowContentFactory(contentType, new ViewFactory(contentType,
                contentDisplayFactory, userSetsDragAvatarFactory,
                allResourcesDragAvatarFactory, selectionDragAvatarFactory,
                dropTargetFactory, resourceSetFactory,
                selectionModelLabelFactory, categorizer, labelProvider,
                contentDropTargetManager, hoverModel, popupManagerFactory,
                detailsWidgetHelper, shareConfigurationFactory));
    }

}