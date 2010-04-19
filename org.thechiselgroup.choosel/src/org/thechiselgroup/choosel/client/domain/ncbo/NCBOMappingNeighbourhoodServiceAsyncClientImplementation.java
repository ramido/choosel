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
package org.thechiselgroup.choosel.client.domain.ncbo;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.util.DocumentProcessor;
import org.thechiselgroup.choosel.client.util.URLFetchService;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class NCBOMappingNeighbourhoodServiceAsyncClientImplementation extends
	AbstractXMLWebResourceService implements NCBO,
	NCBOMappingNeighbourhoodServiceAsync {

    @Inject
    public NCBOMappingNeighbourhoodServiceAsyncClientImplementation(
	    DocumentProcessor documentProcessor, URLFetchService urlFetchService) {
	super(documentProcessor, urlFetchService);
    }

    public static final String SERVICE_URL = "http://bioportal.bioontology.org/mappings/service/";

    @Override
    public void getNeighbourhood(final Resource inputConcept,
	    final AsyncCallback<NeighbourhoodServiceResult> callback) {

	try {
	    String id = (String) inputConcept.getValue(CONCEPT_SHORT_ID);
	    String ontologyId = (String) inputConcept
		    .getValue(CONCEPT_ONTOLOGY_ID);

	    // the ncbo service does not support id's with special characters in
	    // them, so we return an empty result for now.
	    if (id.contains("/")) {
		callback
			.onSuccess(new NeighbourhoodServiceResult(inputConcept));
		return;
	    }

	    String url = SERVICE_URL + encode(ontologyId) + "/" + encode(id);

	    urlFetchService.fetchURL(url, new AsyncCallback<String>() {

		@Override
		public void onFailure(Throwable caught) {
		    callback.onFailure(caught);
		}

		@Override
		public void onSuccess(String xmlText) {
		    try {
			NeighbourhoodServiceResult result = new NeighbourhoodServiceResult(
				inputConcept);

			Object rootNode = documentProcessor
				.parseDocument(xmlText);

			parseMappingTo(inputConcept, result, rootNode);
			parseMappingFrom(inputConcept, result, rootNode);

			callback.onSuccess(result);
		    } catch (Exception e) {
			callback.onFailure(e);
		    }

		}
	    });
	} catch (Exception e) {
	    callback.onFailure(e);
	}
    }

    private Resource parseMapping(Object node) {
	String mappingId = documentProcessor.getText(node, "id/text()");

	String sourceConceptId = documentProcessor.getText(node,
		"source-id/text()");
	String sourceOntologyId = documentProcessor.getText(node,
		"source-ont/text()");
	String destinationConceptId = documentProcessor.getText(node,
		"destination-id/text()");
	String destinationOntologyId = documentProcessor.getText(node,
		"destination-ont/text()");
	String sourceOntologyVersionId = documentProcessor.getText(node,
		"source-version-id/text()");

	String sourceConceptName = documentProcessor.getText(node,
		"source-name/text()");
	String sourceOntologyName = documentProcessor.getText(node,
		"source-ont-name/text()");
	String destinationOntologyName = documentProcessor.getText(node,
		"destination-ont-name/text()");
	String destinationConceptName = documentProcessor.getText(node,
		"destination-name/text()");
	String destinationOntologyVersionId = documentProcessor.getText(node,
		"destination-version-id/text()");

	String createdAt = documentProcessor.getText(node, "created-at/text()");

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

	DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd hh:mm:ss");
	// FIXME assuming current TZ
	// SimpleDateFormat format = new
	// SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// format.setTimeZone(TimeZone.getTimeZone("UTC"));
	mapping.putValue(MAPPING_CREATION_DATE, format.parse(createdAt)
		.toString());

	return mapping;
    }

    private void parseMappingFrom(Resource inputConcept,
	    NeighbourhoodServiceResult result, Object rootNode) {

	Object[] nodes = documentProcessor.getNodes(rootNode,
		"//hash/mapping-from/mapping-from");

	for (int i = 0; i < nodes.length; i++) {
	    Object node = nodes[i];

	    Resource targetConcept = parseTargetConcept(node);
	    Resource mapping = parseMapping(node);

	    result.addRelationship(inputConcept, mapping);
	    result.addRelationship(mapping, targetConcept);
	}
    }

    private void parseMappingTo(Resource inputConcept,
	    NeighbourhoodServiceResult result, Object rootNode) {

	Object[] nodes = documentProcessor.getNodes(rootNode,
		"//hash/mapping-to/mapping-to");

	for (int i = 0; i < nodes.length; i++) {
	    Object node = nodes[i];

	    Resource sourceConcept = parseSourceConcept(node);
	    Resource mapping = parseMapping(node);

	    result.addRelationship(mapping, inputConcept);
	    result.addRelationship(sourceConcept, mapping);
	}
    }

    private Resource parseSourceConcept(Object node) {
	// FIXME is this the short id?
	String conceptId = documentProcessor.getText(node, "source-id/text()");
	String ontologyId = documentProcessor
		.getText(node, "source-ont/text()");
	String ontologyName = documentProcessor.getText(node,
		"source-ont-name/text()");
	String conceptName = documentProcessor.getText(node,
		"source-name/text()");

	Resource source = new Resource(NcboUriHelper.toConceptURI(ontologyId,
		conceptId));
	source.putValue(CONCEPT_SHORT_ID, conceptId);
	source.putValue(CONCEPT_ONTOLOGY_ID, ontologyId);
	source.putValue(CONCEPT_ONTOLOGY_NAME, ontologyName);
	source.putValue(CONCEPT_NAME, conceptName);
	return source;
    }

    private Resource parseTargetConcept(Object node) {
	// FIXME is this the short id or the full id???
	String conceptId = documentProcessor.getText(node,
		"destination-id/text()");
	String ontologyId = documentProcessor.getText(node,
		"destination-ont/text()");
	String conceptName = documentProcessor.getText(node,
		"destination-name/text()");
	String ontologyName = documentProcessor.getText(node,
		"destination-ont-name/text()");

	Resource targetConcept = new Resource(NcboUriHelper.toConceptURI(
		ontologyId, conceptId));

	targetConcept.putValue(CONCEPT_SHORT_ID, conceptId);
	targetConcept.putValue(CONCEPT_ONTOLOGY_ID, ontologyId);
	targetConcept.putValue(CONCEPT_ONTOLOGY_NAME, ontologyName);
	targetConcept.putValue(CONCEPT_NAME, conceptName);

	return targetConcept;
    }
}
