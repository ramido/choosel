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
package org.thechiselgroup.choosel.client.views;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizerToMultiCategorizerAdapter;
import org.thechiselgroup.choosel.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

import com.google.gwt.event.shared.HandlerRegistration;

public class TestView extends DefaultView {

    public static TestView createTestView(Slot... slots) {
        DefaultResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();

        DefaultResourceModel resourceModel = new DefaultResourceModel(
                resourceSetFactory);
        HoverModel hoverModel = new HoverModel();

        ViewContentDisplay contentDisplay = mock(ViewContentDisplay.class);
        SlotMappingConfiguration resourceSetToValueResolver = spy(new SlotMappingConfiguration());
        SelectionModel selectionModel = mock(SelectionModel.class);
        Presenter selectionModelPresenter = mock(Presenter.class);
        DetailsWidgetHelper detailsWidgetHelper = mock(DetailsWidgetHelper.class);
        PopupManagerFactory popupManagerFactory = mock(PopupManagerFactory.class);
        PopupManager popupManager = mock(PopupManager.class);
        ViewSaver viewPersistence = mock(ViewSaver.class);
        Presenter resourceModelPresenter = mock(Presenter.class);
        HandlerRegistration selectionAddedHandlerRegistration = mock(HandlerRegistration.class);
        HandlerRegistration selectionRemovedHandlerRegistration = mock(HandlerRegistration.class);
        VisualMappingsControl visualMappingsControl = mock(VisualMappingsControl.class);

        ResourceGrouping resourceSplitter = new ResourceGrouping(
                new ResourceCategorizerToMultiCategorizerAdapter(
                        new ResourceByUriTypeCategorizer()), resourceSetFactory);

        TestView underTest = spy(new TestView(resourceSplitter, contentDisplay,
                "", "", resourceSetToValueResolver, selectionModel,
                selectionModelPresenter, resourceModel, resourceModelPresenter,
                hoverModel, popupManagerFactory, detailsWidgetHelper,
                viewPersistence, popupManager,
                selectionAddedHandlerRegistration,
                selectionRemovedHandlerRegistration, visualMappingsControl));

        when(
                selectionModel
                        .addEventHandler(any(ResourcesAddedEventHandler.class)))
                .thenReturn(selectionAddedHandlerRegistration);
        when(
                selectionModel
                        .addEventHandler(any(ResourcesRemovedEventHandler.class)))
                .thenReturn(selectionRemovedHandlerRegistration);

        when(contentDisplay.getSlots()).thenReturn(slots);
        when(contentDisplay.isReady()).thenReturn(true);

        underTest.init();

        ArgumentCaptor<ViewContentDisplayCallback> captor = ArgumentCaptor
                .forClass(ViewContentDisplayCallback.class);
        verify(contentDisplay).init(captor.capture());
        underTest.setCallback(captor.getValue());

        return underTest;
    }

    private final PopupManager popupManager;

    private final ViewContentDisplay contentDisplay;

    private final HoverModel hoverModel;

    private final HandlerRegistration selectionAddedHandlerRegistration;

    private final HandlerRegistration selectionRemovedHandlerRegistration;

    private final Presenter resourceModelPresenter;

    private final Presenter selectionModelPresenter;

    private ViewContentDisplayCallback callback;

    private final ResourceGrouping resourceSplitter;

    public TestView(ResourceGrouping resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, SlotMappingConfiguration configuration,
            SelectionModel selectionModel, Presenter selectionModelPresenter,
            ResourceModel resourceModel, Presenter resourceModelPresenter,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper, ViewSaver viewPersistence,
            PopupManager popupManager,
            HandlerRegistration selectionAddedHandlerRegistration,
            HandlerRegistration selectionRemovedHandlerRegistration,
            VisualMappingsControl visualMappingsControl) {

        super(resourceSplitter, contentDisplay, label, contentType,
                configuration, selectionModel, selectionModelPresenter,
                resourceModel, resourceModelPresenter, hoverModel,
                popupManagerFactory, detailsWidgetHelper, viewPersistence,
                visualMappingsControl);

        this.resourceSplitter = resourceSplitter;
        this.contentDisplay = contentDisplay;
        this.selectionModelPresenter = selectionModelPresenter;
        this.resourceModelPresenter = resourceModelPresenter;
        this.hoverModel = hoverModel;
        this.popupManager = popupManager;
        this.selectionAddedHandlerRegistration = selectionAddedHandlerRegistration;
        this.selectionRemovedHandlerRegistration = selectionRemovedHandlerRegistration;
    }

    @Override
    protected PopupManager createPopupManager(ResourceSet resources) {
        return popupManager;
    }

    public ViewContentDisplayCallback getCallback() {
        return callback;
    }

    public ViewContentDisplay getContentDisplay() {
        return contentDisplay;
    }

    public HoverModel getHoverModel() {
        return hoverModel;
    }

    public ResourceGrouping getResourceSplitter() {
        return resourceSplitter;
    }

    public Presenter getTestResourceModelPresenter() {
        return resourceModelPresenter;
    }

    public HandlerRegistration getTestSelectionAddedHandlerRegistration() {
        return selectionAddedHandlerRegistration;
    }

    public Presenter getTestSelectionModelPresenter() {
        return selectionModelPresenter;
    }

    public HandlerRegistration getTestSelectionRemovedHandlerRegistration() {
        return selectionRemovedHandlerRegistration;
    }

    @Override
    protected void initUI() {
    }

    public void setCallback(ViewContentDisplayCallback callback) {
        this.callback = callback;
    }

}