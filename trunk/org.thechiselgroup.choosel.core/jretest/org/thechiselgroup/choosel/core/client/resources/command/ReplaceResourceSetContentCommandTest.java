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

import static org.junit.Assert.assertThat;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;

import org.junit.Test;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;

public class ReplaceResourceSetContentCommandTest {

    private ReplaceResourceSetContentCommand underTest;

    private ResourceSet resources;

    @Test
    public void resourcesReplacedOnExecute() {
        setUpCommand(ResourceSetTestUtils.createResources(4), ResourceSetTestUtils.createResources(1, 2, 3));

        underTest.execute();

        assertThat(resources, containsExactly(ResourceSetTestUtils.createResources(1, 2, 3)));
    }

    @Test
    public void restoreTargetSetOnUndo() {
        setUpCommand(ResourceSetTestUtils.createResources(4), ResourceSetTestUtils.createResources(1, 2, 3));

        underTest.execute();
        underTest.undo();

        assertThat(resources, containsExactly(ResourceSetTestUtils.createResources(4)));
    }

    private void setUpCommand(ResourceSet resources, ResourceSet newContent) {
        this.resources = resources;
        this.underTest = new ReplaceResourceSetContentCommand(resources,
                newContent);
    }

}