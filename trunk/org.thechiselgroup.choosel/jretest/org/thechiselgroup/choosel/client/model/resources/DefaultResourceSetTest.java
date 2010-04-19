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
package org.thechiselgroup.choosel.client.model.resources;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;

public class DefaultResourceSetTest {

    private DefaultResourceSet resources;

    @Test
    public void hasLabelIsFalseWhenLabelNull() {
	resources.setLabel(null);

	assertEquals(false, resources.hasLabel());
    }

    @Test
    public void hasLabelIsTrueWhenLabelText() {
	resources.setLabel("some text");

	assertEquals(true, resources.hasLabel());
    }

    @Test
    public void returnEmptyStringIfLabelNull() {
	resources.setLabel(null);

	assertEquals("", resources.getLabel());
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	resources = new DefaultResourceSet();
    }
}
