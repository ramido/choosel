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
package org.thechiselgroup.biomixer.server.domain.ncbo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOMappingNeighbourhoodService;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;
import org.thechiselgroup.choosel.server.urlfetch.DocumentFetchService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.allen_sauer.gwt.log.client.Log;

public class NCBOMappingNeighbourhoodServiceImplementation implements NCBO,
	NCBOMappingNeighbourhoodService {

    public static final String SERVICE_URL = "http://bioportal.bioontology.org/mappings/service/";

    private static final String XPATH_MAPPING_FROM = "//hash/mapping-from/mapping-from";

    private static final String XPATH_MAPPING_TO = "//hash/mapping-to/mapping-to";

    private final DocumentFetchService documentFetchService;

    private XPathExpression destinationIdExpression;

    private XPathExpression destinationNameExpression;

    private XPathExpression destinationOntologyIdExpression;

    private XPathExpression destinationOntologyVersionIdExpression;

    private XPathExpression mappingFromExpression;

    private XPathExpression mappingIdExpression;

    private XPathExpression mappingToExpression;

    private XPathExpression sourceIdExpression;

    private XPathExpression sourceNameExpression;

    private XPathExpression sourceOntologyIdExpression;

    private XPathExpression sourceOntologyVersionIdExpression;

    private XPathExpression mappingCreatedAtExpression;

    private XPathExpression sourceOntologyNameExpression;

    private XPathExpression destinationOntologyNameExpression;

    public NCBOMappingNeighbourhoodServiceImplementation(
	    DocumentFetchService documentFetchService)
	    throws XPathExpressionException {

	this.documentFetchService = documentFetchService;

	XPathFactory factory = new org.apache.xpath.jaxp.XPathFactoryImpl();
	XPath xpath = factory.newXPath();

	mappingToExpression = xpath.compile(XPATH_MAPPING_TO);
	mappingFromExpression = xpath.compile(XPATH_MAPPING_FROM);

	sourceIdExpression = xpath.compile("source-id/text()");
	sourceOntologyVersionIdExpression = xpath
		.compile("source-version-id/text()");
	sourceOntologyIdExpression = xpath.compile("source-ont/text()");
	sourceOntologyNameExpression = xpath.compile("source-ont-name/text()");
	sourceNameExpression = xpath.compile("source-name/text()");

	destinationIdExpression = xpath.compile("destination-id/text()");
	destinationOntologyVersionIdExpression = xpath
		.compile("destination-version-id/text()");
	destinationOntologyIdExpression = xpath
		.compile("destination-ont/text()");
	destinationOntologyNameExpression = xpath
		.compile("destination-ont-name/text()");
	destinationNameExpression = xpath.compile("destination-name/text()");

	mappingIdExpression = xpath.compile("id/text()");
	mappingCreatedAtExpression = xpath.compile("created-at/text()");
    }

    public NeighbourhoodServiceResult getNeighbourhood(Resource inputConcept)
	    throws ServiceException {

	NeighbourhoodServiceResult result = new NeighbourhoodServiceResult(
		inputConcept);
	try {
	    String url = SERVICE_URL
		    + inputConcept.getValue(CONCEPT_ONTOLOGY_ID) + "/"
		    + inputConcept.getValue(CONCEPT_SHORT_ID);

	    Document document = documentFetchService.fetchXML(url);

	    parseMappingTo(inputConcept, result, document);
	    parseMappingFrom(inputConcept, result, document);

	    return result;
	} catch (Exception e) {
	    Log.error(e.getMessage(), e);
	    return new NeighbourhoodServiceResult(inputConcept);
	}
    }

    private Resource parseMapping(Node node) throws XPathExpressionException,
	    ParseException {

	String mappingId = (String) mappingIdExpression.evaluate(node,
		XPathConstants.STRING);
	String sourceConceptId = (String) sourceIdExpression.evaluate(node,
		XPathConstants.STRING);
	String sourceOntologyId = (String) sourceOntologyIdExpression.evaluate(
		node, XPathConstants.STRING);
	String destinationConceptId = (String) destinationIdExpression
		.evaluate(node, XPathConstants.STRING);
	String destinationOntologyId = (String) destinationOntologyIdExpression
		.evaluate(node, XPathConstants.STRING);
	String sourceOntologyVersionId = (String) sourceOntologyVersionIdExpression
		.evaluate(node, XPathConstants.STRING);

	String sourceConceptName = (String) sourceNameExpression.evaluate(node,
		XPathConstants.STRING);
	String sourceOntologyName = (String) sourceOntologyNameExpression
		.evaluate(node, XPathConstants.STRING);
	String destinationOntologyName = (String) destinationOntologyNameExpression
		.evaluate(node, XPathConstants.STRING);
	String destinationConceptName = (String) destinationNameExpression
		.evaluate(node, XPathConstants.STRING);
	String destinationOntologyVersionId = (String) destinationOntologyVersionIdExpression
		.evaluate(node, XPathConstants.STRING);

	String createdAt = (String) mappingCreatedAtExpression.evaluate(node,
		XPathConstants.STRING);

	Resource mapping = new Resource(NcboUriHelper.toMappingURI(mappingId));

	mapping.putValue(MAPPING_ID, mappingId);
	mapping.putValue(MAPPING_DESTINATION, NcboUriHelper.toConceptURI(
		destinationOntologyId, destinationConceptId));
	mapping.putValue(MAPPING_SOURCE, NcboUriHelper.toConceptURI(
		sourceOntologyId, sourceConceptId));
	mapping.putValue(MAPPING_SOURCE_CONCEPT_ID, sourceConceptId);
	mapping.putValue(MAPPING_SOURCE_CONCEPT_NAME, sourceConceptName);
	mapping.putValue(MAPPING_SOURCE_ONTOLOGY_ID, sourceOntologyId);
	mapping.putValue(MAPPING_SOURCE_ONTOLOGY_VERSION_ID,
		sourceOntologyVersionId);
	mapping.putValue(MAPPING_SOURCE_ONTOLOGY_NAME, sourceOntologyName);

	mapping.putValue(MAPPING_DESTINATION_CONCEPT_ID, destinationConceptId);
	mapping.putValue(MAPPING_DESTINATION_CONCEPT_NAME,
		destinationConceptName);
	mapping
		.putValue(MAPPING_DESTINATION_ONTOLOGY_ID,
			destinationOntologyId);
	mapping.putValue(MAPPING_DESTINATION_ONTOLOGY_VERSION_ID,
		destinationOntologyVersionId);
	mapping.putValue(MAPPING_DESTINATION_ONTOLOGY_NAME,
		destinationOntologyName);

	// FIXME Hack -- should be removed once we have different kinds of
	// Layers in graph
	mapping.putValue(CONCEPT_NAME, "");

	// FIXME assuming UTC...
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	format.setTimeZone(TimeZone.getTimeZone("UTC"));
	mapping.putValue(MAPPING_CREATION_DATE, format.parse(createdAt)
		.toString());

	return mapping;
    }

    private void parseMappingFrom(Resource inputConcept,
	    NeighbourhoodServiceResult result, Document document)
	    throws XPathExpressionException, ParseException {

	NodeList nodes = (NodeList) mappingFromExpression.evaluate(document,
		XPathConstants.NODESET);

	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);

	    Resource targetConcept = parseTargetConcept(node);
	    Resource mapping = parseMapping(node);

	    result.addRelationship(inputConcept, mapping);
	    result.addRelationship(mapping, targetConcept);
	}
    }

    private void parseMappingTo(Resource inputConcept,
	    NeighbourhoodServiceResult result, Document document)
	    throws XPathExpressionException, ParseException {

	NodeList nodes = (NodeList) mappingToExpression.evaluate(document,
		XPathConstants.NODESET);

	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);

	    Resource sourceConcept = parseSourceConcept(node);
	    Resource mapping = parseMapping(node);

	    result.addRelationship(mapping, inputConcept);
	    result.addRelationship(sourceConcept, mapping);
	}
    }

    private Resource parseSourceConcept(Node node)
	    throws XPathExpressionException {
	// FIXME is this the short id?
	String conceptId = (String) sourceIdExpression.evaluate(node,
		XPathConstants.STRING);
	String ontologyId = (String) sourceOntologyIdExpression.evaluate(node,
		XPathConstants.STRING);
	String ontologyName = (String) sourceOntologyNameExpression.evaluate(
		node, XPathConstants.STRING);
	String conceptName = (String) sourceNameExpression.evaluate(node,
		XPathConstants.STRING);

	Resource source = new Resource(NcboUriHelper.toConceptURI(ontologyId,
		conceptId));
	source.putValue(CONCEPT_SHORT_ID, conceptId);
	source.putValue(CONCEPT_ONTOLOGY_ID, ontologyId);
	source.putValue(CONCEPT_ONTOLOGY_NAME, ontologyName);
	source.putValue(CONCEPT_NAME, conceptName);
	return source;
    }

    private Resource parseTargetConcept(Node node)
	    throws XPathExpressionException {

	// FIXME is this the short id or the full id???
	String conceptId = (String) destinationIdExpression.evaluate(node,
		XPathConstants.STRING);
	String ontologyId = (String) destinationOntologyIdExpression.evaluate(
		node, XPathConstants.STRING);
	String conceptName = (String) destinationNameExpression.evaluate(node,
		XPathConstants.STRING);
	String ontologyName = (String) destinationOntologyNameExpression
		.evaluate(node, XPathConstants.STRING);

	Resource targetConcept = new Resource(NcboUriHelper.toConceptURI(
		ontologyId, conceptId));

	targetConcept.putValue(CONCEPT_SHORT_ID, conceptId);
	targetConcept.putValue(CONCEPT_ONTOLOGY_ID, ontologyId);
	targetConcept.putValue(CONCEPT_ONTOLOGY_NAME, ontologyName);
	targetConcept.putValue(CONCEPT_NAME, conceptName);

	return targetConcept;
    }
}
