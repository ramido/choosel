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
package org.thechiselgroup.choosel.client.resources.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.views.ResourceModel;
import org.thechiselgroup.choosel.client.views.View;

public class ReplaceSelectionContentsCommandTest {

    private ResourceSet resources;

    private ReplaceSelectionContentsCommand underTest;

    @Mock
    private View view;

    private ResourceSet viewSelection;

    @Mock
    private ResourceModel resourceModel;

    @Test
    public void resourcesReplacedOnExecute() {
        setUpCommand(createResources(1, 2, 3), createResources(4),
                createResources(1, 2, 3, 4));

        underTest.execute();

        assertEquals(true, viewSelection.containsEqualResources(resources));
    }

    @Test
    public void restoreTargetSetOnUndo() {
        setUpCommand(createResources(1, 2, 3), createResources(4),
                createResources(1, 2, 3, 4));

        underTest.execute();
        underTest.undo();

        assertEquals(true,
                viewSelection.containsEqualResources(createResources(4)));
    }

    @Test
    public void selectionContainsOnlyElementsFromView() {
        setUpCommand(createResources(1, 2, 3), createResources(4),
                createResources(3, 4));

        underTest.execute();

        assertEquals(true,
                viewSelection.containsEqualResources(createResources(3)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    private void setUpCommand(DefaultResourceSet resources,
            DefaultResourceSet viewSelection, DefaultResourceSet viewResources) {

        this.resources = resources;
        this.viewSelection = viewSelection;

        when(view.getSelection()).thenReturn(viewSelection);
        when(view.getResourceModel()).thenReturn(resourceModel);
        when(resourceModel.getResources()).thenReturn(viewResources);

        this.underTest = new ReplaceSelectionContentsCommand(resources, view);
    }

}
