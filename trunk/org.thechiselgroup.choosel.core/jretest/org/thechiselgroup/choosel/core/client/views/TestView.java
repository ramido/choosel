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
package org.thechiselgroup.choosel.core.client.views;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizerToMultiCategorizerAdapter;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.ui.Presenter;
import org.thechiselgroup.choosel.core.client.util.collections.NullLightweightCollection;
import org.thechiselgroup.choosel.core.client.views.slots.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingInitializer;

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
        Presenter resourceModelPresenter = mock(Presenter.class);
        HandlerRegistration selectionChangedHandlerRegistration = mock(HandlerRegistration.class);
        VisualMappingsControl visualMappingsControl = mock(VisualMappingsControl.class);

        // TODO change once relevant tests are migrated
        SlotMappingInitializer slotMappingInitializer = spy(new DefaultSlotMappingInitializer());

        ResourceGrouping resourceGrouping = new ResourceGrouping(
                new ResourceCategorizerToMultiCategorizerAdapter(
                        new ResourceByUriTypeCategorizer()), resourceSetFactory);

        TestView underTest = spy(new TestView(resourceGrouping, contentDisplay,
                "", "", resourceSetToValueResolver, selectionModel,
                selectionModelPresenter, resourceModel, resourceModelPresenter,
                hoverModel, selectionChangedHandlerRegistration,
                visualMappingsControl, slotMappingInitializer,
                mock(ViewItemBehavior.class)));

        when(
                selectionModel
                        .addEventHandler(any(ResourceSetChangedEventHandler.class)))
                .thenReturn(selectionChangedHandlerRegistration);

        when(contentDisplay.getSlots()).thenReturn(slots);
        when(contentDisplay.isReady()).thenReturn(true);

        when(selectionModel.getSelection())
                .thenReturn(new DefaultResourceSet());

        underTest.init();

        ArgumentCaptor<ViewContentDisplayCallback> captor = ArgumentCaptor
                .forClass(ViewContentDisplayCallback.class);
        verify(contentDisplay).init(captor.capture());
        underTest.setCallback(captor.getValue());

        return underTest;
    }

    private final ViewContentDisplay contentDisplay;

    private final HoverModel hoverModel;

    private final HandlerRegistration selectionChangedHandlerRegistration;

    private final Presenter resourceModelPresenter;

    private final Presenter selectionModelPresenter;

    private ViewContentDisplayCallback callback;

    public TestView(ResourceGrouping resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, SlotMappingConfiguration configuration,
            SelectionModel selectionModel, Presenter selectionModelPresenter,
            ResourceModel resourceModel, Presenter resourceModelPresenter,
            HoverModel hoverModel,
            HandlerRegistration selectionChangedHandlerRegistration,
            VisualMappingsControl visualMappingsControl,
            SlotMappingInitializer slotMappingInitializer,
            ViewItemBehavior viewItemBehavior) {

        super(resourceSplitter, contentDisplay, label, contentType,
                configuration, selectionModel, selectionModelPresenter,
                resourceModel, resourceModelPresenter, hoverModel,
                visualMappingsControl, slotMappingInitializer,
                NullLightweightCollection
                        .<SidePanelSection> nullLightweightCollection(),
                viewItemBehavior);

        this.contentDisplay = contentDisplay;
        this.selectionModelPresenter = selectionModelPresenter;
        this.resourceModelPresenter = resourceModelPresenter;
        this.hoverModel = hoverModel;
        this.selectionChangedHandlerRegistration = selectionChangedHandlerRegistration;
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

    public Presenter getTestResourceModelPresenter() {
        return resourceModelPresenter;
    }

    public HandlerRegistration getTestSelectionChangedHandlerRegistration() {
        return selectionChangedHandlerRegistration;
    }

    public Presenter getTestSelectionModelPresenter() {
        return selectionModelPresenter;
    }

    @Override
    protected void initUI() {
    }

    public void setCallback(ViewContentDisplayCallback callback) {
        this.callback = callback;
    }

}