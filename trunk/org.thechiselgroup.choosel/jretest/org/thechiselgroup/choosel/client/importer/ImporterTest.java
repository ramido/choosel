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
package org.thechiselgroup.choosel.client.importer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ImporterTest {

    private Importer underTest;

    @Test
    public void sameUriTypeInSameImport() {
        String[] columns = new String[] { "c1" };
        List<String[]> values = new ArrayList<String[]>();
        values.add(new String[] { "v11" });
        values.add(new String[] { "v21" });

        ResourceSet result = underTest.createResources(new StringTable(columns,
                values));

        Set<String> uris = new HashSet<String>();
        for (Resource resource : result) {
            uris.add(resource.getUri().substring(0,
                    resource.getUri().indexOf(':')));
        }

        assertEquals(1, uris.size());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new Importer();
    }

    @Test
    public void uniqueUris() {
        String[] columns = new String[] { "c1" };
        List<String[]> values = new ArrayList<String[]>();
        values.add(new String[] { "v1" });

        ResourceSet result1 = underTest.createResources(new StringTable(
                columns, values));
        ResourceSet result2 = underTest.createResources(new StringTable(
                columns, values));

        Set<String> uris = new HashSet<String>();
        for (Resource resource : result1) {
            uris.add(resource.getUri());
        }
        for (Resource resource : result2) {
            uris.add(resource.getUri());
        }

        assertEquals(result1.size() + result2.size(), uris.size());
    }

}
