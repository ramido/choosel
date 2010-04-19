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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.command.ReplaceSelectionCommand;
import org.thechiselgroup.choosel.client.views.View;

public class ReplaceSelectionCommandTest {

    private ResourceSet sourceSet;

    private ResourceSet targetSet;

    private ReplaceSelectionCommand command;

    @Mock
    private View view;

    @Test
    public void setNewSelectionSetOnExecute() {
	setUpCommand(createResources(1, 2, 3), createResources());

	command.execute();

	verify(view, times(1)).setSelection(eq(targetSet));
    }

    @Test
    public void setOldSelectionSetOnUndo() {
	setUpCommand(createResources(1, 2, 3), createResources());

	command.execute();
	command.undo();

	verify(view, times(1)).setSelection(eq(targetSet)); // from execute
	verify(view, times(1)).setSelection(eq(sourceSet));
    }

    private void setUpCommand(ResourceSet sourceSet, ResourceSet targetSet) {
	this.sourceSet = sourceSet;
	this.targetSet = targetSet;

	this.command = new ReplaceSelectionCommand(view, targetSet);

	when(view.getSelection()).thenReturn(sourceSet);
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

    }

}
