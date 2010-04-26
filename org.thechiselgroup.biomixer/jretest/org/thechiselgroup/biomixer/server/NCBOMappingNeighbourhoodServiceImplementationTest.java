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
package org.thechiselgroup.biomixer.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.biomixer.server.domain.ncbo.NCBOMappingNeighbourhoodServiceImplementation;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;
import org.thechiselgroup.choosel.client.views.graph.Relationship;
import org.thechiselgroup.choosel.server.urlfetch.DocumentFetchService;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class NCBOMappingNeighbourhoodServiceImplementationTest implements NCBO {

    private static final String TEST_CONCEPT_SHORT_ID = "CHEBI:27594";

    private static final String TEST_ONTOLOGY_ID = "1007";

    private static final String TEST_URL = NCBOMappingNeighbourhoodServiceImplementation.SERVICE_URL
	    + TEST_ONTOLOGY_ID + "/" + TEST_CONCEPT_SHORT_ID;

    @Mock
    private DocumentFetchService documentFetchService;

    private Resource inputConcept;

    private void assertIsOtherConcept(Resource resource) {
	assertEquals("OtherCarbon", resource.getValue(CONCEPT_SHORT_ID));
	assertEquals("OtherCarbonName", resource.getValue(CONCEPT_NAME));
	assertEquals("1083", resource.getValue(CONCEPT_ONTOLOGY_ID));
	assertEquals("NanoParticle Ontology (NPO)", resource
		.getValue(CONCEPT_ONTOLOGY_NAME));
    }

    private void assertMappingToInputConcept(Resource resource) {
	assertEquals("98216", resource.getValue(MAPPING_ID));
	assertEquals(inputConcept.getUri(), resource
		.getValue(MAPPING_DESTINATION));
	assertEquals(NcboUriHelper.toConceptURI("1083", "OtherCarbon"),
		resource.getValue(MAPPING_SOURCE));
	assertEquals("NanoParticle Ontology (NPO)", resource
		.getValue(MAPPING_SOURCE_ONTOLOGY_NAME));
	assertEquals("OtherCarbonName", resource
		.getValue(MAPPING_SOURCE_CONCEPT_NAME));
	assertEquals("Chemical entities of biological interest", resource
		.getValue(MAPPING_DESTINATION_ONTOLOGY_NAME));
	assertEquals("Carbon", resource
		.getValue(MAPPING_DESTINATION_CONCEPT_NAME));
    }

    private void assertMappingToOtherConcept(Resource resource) {
	assertEquals("1801522", resource.getValue(MAPPING_ID));
	assertEquals(NcboUriHelper.toConceptURI("1083", "OtherCarbon"),
		resource.getValue(MAPPING_DESTINATION));
	assertEquals(inputConcept.getUri(), resource.getValue(MAPPING_SOURCE));
    }

    private NeighbourhoodServiceResult executeService()
	    throws XPathExpressionException, ServiceException {

	NCBOMappingNeighbourhoodServiceImplementation service = new NCBOMappingNeighbourhoodServiceImplementation(
		documentFetchService);
	return service.getNeighbourhood(inputConcept);
    }

    @Test
    public void loadBidirectionalMapping() throws Exception {
	stubInput("loadBidirectionalMapping.input");

	NeighbourhoodServiceResult result = executeService();
	List<Resource> neighbours = new ArrayList<Resource>(result
		.getNeighbours());
	List<Relationship> relationships = result.getRelationships();

	// check results
	verify(documentFetchService).fetchXML(TEST_URL);

	assertEquals(3, neighbours.size());
	Resource otherConcept = null;
	Resource mapping1 = null;
	Resource mapping2 = null;
	for (Resource resource : neighbours) {
	    if (resource.getUri().startsWith(NcboUriHelper.NCBO_CONCEPT)) {
		assertIsOtherConcept(resource);
		otherConcept = resource;
	    } else if (resource.getUri().equals(
		    NcboUriHelper.toMappingURI("1801522"))) {
		assertMappingToOtherConcept(resource);
		mapping1 = resource;
	    } else if (resource.getUri().equals(
		    NcboUriHelper.toMappingURI("98216"))) {
		assertMappingToInputConcept(resource);
		mapping2 = resource;
	    } else {
		fail("invalid resource " + resource);
	    }
	}
	assertNotNull(otherConcept);
	assertNotNull(mapping1);
	assertNotNull(mapping2);

	assertEquals(4, relationships.size());
	assertTrue(relationships.contains(new Relationship(inputConcept,
		mapping1)));
	assertTrue(relationships.contains(new Relationship(mapping1,
		otherConcept)));
	assertTrue(relationships.contains(new Relationship(otherConcept,
		mapping2)));
	assertTrue(relationships.contains(new Relationship(mapping2,
		inputConcept)));
    }

    private Document loadInputDocument(String input)
	    throws ParserConfigurationException, SAXException, IOException {
	DocumentBuilderFactory domFactory = DocumentBuilderFactory
		.newInstance();
	domFactory.setNamespaceAware(true);
	DocumentBuilder builder = domFactory.newDocumentBuilder();
	return builder
		.parse(NCBOMappingNeighbourhoodServiceImplementationTest.class
			.getResourceAsStream(input));
    }

    @Test
    public void loadUnidirectionalMappingFromInputConcept() throws Exception {
	stubInput("loadUnidirectionalMappingFromInputConcept.input");

	NeighbourhoodServiceResult result = executeService();
	List<Resource> neighbours = new ArrayList<Resource>(result
		.getNeighbours());
	List<Relationship> relationships = result.getRelationships();

	// check results
	verify(documentFetchService).fetchXML(TEST_URL);

	assertEquals(2, neighbours.size());
	Resource otherConcept = null;
	Resource mapping = null;
	for (Resource resource : neighbours) {
	    if (resource.getUri().startsWith(NcboUriHelper.NCBO_CONCEPT)) {
		assertIsOtherConcept(resource);
		otherConcept = resource;
	    } else if (resource.getUri().startsWith(NcboUriHelper.NCBO_MAPPING)) {
		assertMappingToOtherConcept(resource);
		mapping = resource;
	    } else {
		fail("invalid resource " + resource);
	    }
	}
	assertNotNull(otherConcept);
	assertNotNull(mapping);

	assertEquals(2, relationships.size());
	assertTrue(relationships.contains(new Relationship(mapping,
		otherConcept)));
	assertTrue(relationships.contains(new Relationship(inputConcept,
		mapping)));
    }

    @Test
    public void loadUnidirectionalMappingToInputConcept() throws Exception {
	stubInput("loadUnidirectionalMappingToInputConcept.input");

	NeighbourhoodServiceResult result = executeService();

	List<Resource> neighbours = new ArrayList<Resource>(result
		.getNeighbours());
	List<Relationship> relationships = result.getRelationships();

	// check results
	verify(documentFetchService).fetchXML(TEST_URL);

	assertEquals(2, neighbours.size());
	Resource otherConcept = null;
	Resource mapping = null;
	for (Resource resource : neighbours) {
	    if (resource.getUri().startsWith(NcboUriHelper.NCBO_CONCEPT)) {
		assertIsOtherConcept(resource);
		otherConcept = resource;
	    } else if (resource.getUri().startsWith(NcboUriHelper.NCBO_MAPPING)) {
		assertMappingToInputConcept(resource);
		mapping = resource;

	    } else {
		fail("invalid resource " + resource);
	    }
	}
	assertNotNull(otherConcept);
	assertNotNull(mapping);

	assertEquals(2, relationships.size());
	assertTrue(relationships.contains(new Relationship(otherConcept,
		mapping)));
	assertTrue(relationships.contains(new Relationship(mapping,
		inputConcept)));
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	inputConcept = new Resource(NcboUriHelper.toConceptURI(
		TEST_ONTOLOGY_ID, TEST_CONCEPT_SHORT_ID));

	inputConcept.putValue(CONCEPT_SHORT_ID, TEST_CONCEPT_SHORT_ID);
	inputConcept.putValue(CONCEPT_ONTOLOGY_ID, TEST_ONTOLOGY_ID);
    }

    private void stubInput(String inputFile) throws Exception {
	Document document = loadInputDocument(inputFile);
	when(documentFetchService.fetchXML(TEST_URL)).thenReturn(document);
    }
}
