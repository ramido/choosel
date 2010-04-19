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
package org.thechiselgroup.choosel.client.views.graph;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.graph.MoveNodeCommand;

// TODO might break if redone after window open is redone (thus graphDisplay
// differs...)
public class MoveNodeCommandTest {

    private MoveNodeCommand command;

    @Mock
    private GraphDisplay graphDisplay;

    @Mock
    private Node node;

    @Mock
    private Point sourceLocation;

    @Mock
    private Point targetLocation;

    @Test
    public void setNodeLocationOnExecute() {
	command.execute();
	verify(graphDisplay, times(1)).animateMoveTo(eq(node),
		eq(targetLocation));
    }

    @Test
    public void setNodeLocationOnUndo() {
	command.undo();
	verify(graphDisplay, times(1)).animateMoveTo(eq(node),
		eq(sourceLocation));
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	command = new MoveNodeCommand(graphDisplay, node, sourceLocation,
		targetLocation);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

}
