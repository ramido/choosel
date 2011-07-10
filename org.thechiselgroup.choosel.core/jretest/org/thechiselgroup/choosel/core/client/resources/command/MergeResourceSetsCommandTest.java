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
package org.thechiselgroup.choosel.core.client.resources.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.ResourceModel;

public class MergeResourceSetsCommandTest {

    private MergeResourceSetsCommand command;

    private ResourceSet sourceSet;

    private ResourceSet targetSet;

    @Mock
    private ResourceModel resourceModel;

    @Test
    public void addSourceSetToViewOnUndo() {
        setUpCommand(createResources(1, 2, 3), createResources());

        command.execute();
        command.undo();

        verify(resourceModel, times(1)).addResourceSet(eq(sourceSet));
    }

    @Test
    public void removeOnlyAdditionalResourcesFromTargetSetOnUndo() {
        setUpCommand(createResources(1, 2, 3), createResources(1));

        command.execute();
        command.undo();

        assertEquals(1, targetSet.size());
        assertEquals(true, targetSet.contains(createResource(1)));
    }

    @Test
    public void removeSourceSetFromViewOnExecute() {
        setUpCommand(createResources(1, 2, 3), createResources());

        command.execute();

        verify(resourceModel, times(1)).removeResourceSet(eq(sourceSet));
    }

    @Test
    public void resourceAddedToTargetSetOnExecute() {
        setUpCommand(createResources(1, 2, 3), createResources());

        command.execute();

        assertEquals(true, targetSet.containsAll(sourceSet));
    }

    @Test
    public void resourceAddedToTargetSetOnExecuteAfterUndo() {
        setUpCommand(createResources(1, 2, 3), createResources());

        command.execute();
        command.undo();
        command.execute();

        assertEquals(true, targetSet.containsAll(sourceSet));
    }

    @Test
    public void resourceRemovedFromTargetSetOnUndo() {
        setUpCommand(createResources(1, 2, 3), createResources());

        command.execute();
        command.undo();

        assertEquals(true, targetSet.isEmpty());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    private void setUpCommand(ResourceSet sourceSet, ResourceSet targetSet) {
        this.sourceSet = sourceSet;
        this.targetSet = targetSet;

        this.command = new MergeResourceSetsCommand(sourceSet, targetSet,
                resourceModel);
    }

}
