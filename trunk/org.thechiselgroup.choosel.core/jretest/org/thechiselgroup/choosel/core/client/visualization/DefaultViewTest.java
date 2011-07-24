/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.visualization;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.core.client.ui.Presenter;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualizationModel;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.ResourceModel;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.SelectionModel;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.persistence.ManagedSlotMappingConfigurationPersistence;
import org.thechiselgroup.choosel.core.client.visualization.ui.VisualMappingsControl;

public class DefaultViewTest {

    public static interface DisposableVisualizationModel extends
            VisualizationModel, Disposable {
    }

    private DefaultView underTest;

    @Mock
    private DisposableVisualizationModel viewModel;

    @Mock
    private Presenter resourceModelPresenter;

    @Mock
    private Presenter selectionModelPresenter;

    @Test
    public void disposeResourceModelPresenter() {
        underTest.dispose();

        verify(resourceModelPresenter, times(1)).dispose();
    }

    @Test
    public void disposeSelectionModelPresenter() {
        underTest.dispose();

        verify(selectionModelPresenter, times(1)).dispose();
    }

    @Test
    public void disposeViewModel() {
        underTest.dispose();

        verify(viewModel, times(1)).dispose();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new DefaultView(mock(ViewContentDisplay.class), "label",
                "contentType", selectionModelPresenter, resourceModelPresenter,
                mock(VisualMappingsControl.class),
                LightweightCollections.<SidePanelSection> emptyCollection(),
                viewModel, mock(ResourceModel.class),
                mock(SelectionModel.class),
                mock(ManagedSlotMappingConfiguration.class),
                mock(ManagedSlotMappingConfigurationPersistence.class),
                mock(ErrorHandler.class));
    }
}