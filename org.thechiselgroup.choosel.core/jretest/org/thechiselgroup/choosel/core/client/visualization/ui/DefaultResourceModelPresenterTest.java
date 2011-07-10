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
package org.thechiselgroup.choosel.core.client.visualization.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.DefaultResourceModel;
import org.thechiselgroup.choosel.core.client.visualization.ui.DefaultResourceModelPresenter;

public class DefaultResourceModelPresenterTest {

    @Mock
    private ResourceSetsPresenter allResourcesPresenter;

    @Mock
    private ResourceSetsPresenter inputResourceSetsPresenter;

    private DefaultResourceModel resourceModel;

    private DefaultResourceModelPresenter underTest;

    @Test
    public void addingUnlabeledSetDoesNotChangeOriginalSetsPresenter() {
        Resource resource = createResource(1);
        DefaultResourceSet resources = new DefaultResourceSet();
        resources.add(resource);

        resourceModel.addResourceSet(resources);

        verify(inputResourceSetsPresenter, never()).addResourceSet(resources);
    }

    @Test
    public void allResourcesPresenterContainsSetWithAllResources() {
        resourceModel.addResourceSet(createLabeledResources(1));
        resourceModel.addUnnamedResources(createResources(2));

        ArgumentCaptor<ResourceSet> argument = ArgumentCaptor
                .forClass(ResourceSet.class);
        verify(allResourcesPresenter, times(1)).addResourceSet(
                argument.capture());

        assertEquals(true, argument.getValue().contains(createResource(1)));
        assertEquals(true, argument.getValue().contains(createResource(2)));
        assertEquals(2, argument.getValue().size());
    }

    @Test
    public void callOriginalSetsPresenterOnLabeledResourcesAdded() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        resourceModel.addResourceSet(resources);

        verify(inputResourceSetsPresenter, times(1)).addResourceSet(resources);
    }

    @Test
    public void callOriginalSetsPresenterOnLabeledResourcesAddedOnlyOnce() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        resourceModel.addResourceSet(resources);
        resourceModel.addResourceSet(resources);

        verify(inputResourceSetsPresenter, times(1)).addResourceSet(resources);
    }

    @Test
    public void callResourceSetsPresenterOnLabeledResourcesRemoved() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        resourceModel.addResourceSet(resources);
        resourceModel.removeResourceSet(resources);

        verify(inputResourceSetsPresenter, times(1)).removeResourceSet(
                resources);
    }

    @Test
    public void doNotCallOriginalSetsPresenterOnAddingUnlabeledResources() {
        Resource resource = createResource(1);
        DefaultResourceSet resources = new DefaultResourceSet();
        resources.add(resource);

        resourceModel.addResourceSet(resources);

        verify(inputResourceSetsPresenter, never()).addResourceSet(
                any(ResourceSet.class));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resourceModel = new DefaultResourceModel(
                new DefaultResourceSetFactory());
        underTest = new DefaultResourceModelPresenter(allResourcesPresenter,
                inputResourceSetsPresenter, resourceModel);

        underTest.init();
    }

}
