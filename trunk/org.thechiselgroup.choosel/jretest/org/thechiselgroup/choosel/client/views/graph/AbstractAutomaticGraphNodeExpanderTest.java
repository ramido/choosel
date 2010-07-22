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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.UriList;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

public class AbstractAutomaticGraphNodeExpanderTest {

    private static final String PROPERTY = "property";

    @Mock
    private ViewContentDisplayCallback callback;

    @Mock
    private GraphNodeExpansionCallback expansionCallback;

    private AbstractAutomaticGraphNodeExpander underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new AbstractAutomaticGraphNodeExpander() {
            @Override
            public void expand(Resource resource,
                    GraphNodeExpansionCallback expansionCallback) {
            }
        };
    }

    @Test
    public void showArcsDisplaysArcsFromCurrentResourcesToNewlyAddedResource() {
        Resource newResource = TestResourceSetFactory.createResource(1);
        Resource currentResource = TestResourceSetFactory.createResource(2);

        UriList currentUriList = new UriList();
        currentUriList.add(newResource.getUri());
        currentResource.putValue(PROPERTY, currentUriList);

        newResource.putValue(PROPERTY, new UriList());

        when(expansionCallback.getCallback()).thenReturn(callback);
        when(callback.getAllResources()).thenReturn(
                TestResourceSetFactory.toResourceSet(currentResource));

        underTest.showArcs(newResource, expansionCallback, PROPERTY, "arcType",
                false);

        verify(expansionCallback, times(1)).showArc("arcType",
                currentResource.getUri(), newResource.getUri());
    }
}
