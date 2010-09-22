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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class CSVImporterTest {

    private CSVImporter underTest;

    @Test
    public void failureNoData() {
        try {
            String data = "";
            underTest.doImport(data);
            fail("no exception thrown");
        } catch (ImportException e) {
            assertEquals(-1, e.getLineNumber());
        }
    }

    @Test
    public void failureNotEnoughValues() {
        try {
            String data = "columnA,columnB\nvalue1A,value1B\nvalue2A";
            underTest.doImport(data);
            fail("no exception thrown");
        } catch (ImportException e) {
            assertEquals(3, e.getLineNumber());
        }
    }

    @Test
    public void failureTooManyValues() {
        try {
            String data = "columnA,columnB\nvalue1A,value1B\nvalue2A,value2B,value2C";
            underTest.doImport(data);
            fail("no exception thrown");
        } catch (ImportException e) {
            assertEquals(3, e.getLineNumber());
        }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new CSVImporter();
    }

    @Test
    public void simpleImport() throws ImportException {
        String data = "columnA,columnB\nvalue1A,value1B\nvalue2A,value2B";

        ImportResult result = underTest.doImport(data);

        assertEquals(2, result.columns.length);
        assertEquals("columnA", result.columns[0]);
        assertEquals("columnB", result.columns[1]);

        assertEquals(2, result.values.size());

        assertEquals(2, result.values.get(0).length);
        assertEquals("value1A", result.values.get(0)[0]);
        assertEquals("value1B", result.values.get(0)[1]);

        assertEquals(2, result.values.get(1).length);
        assertEquals("value2A", result.values.get(1)[0]);
        assertEquals("value2B", result.values.get(1)[1]);
    }

}
