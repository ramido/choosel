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
package org.thechiselgroup.biomixer.client.services;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.biomixer.client.NCBO;
import org.thechiselgroup.biomixer.client.NcboUriHelper;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.util.DocumentProcessor;
import org.thechiselgroup.choosel.client.util.URLFetchService;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class NCBOConceptNeighbourhoodServiceAsyncClientImplementation extends
	AbstractXMLWebResourceService implements
	NCBOConceptNeighbourhoodServiceAsync {

    private static final String REVERSE_PREFIX = "[R]";

    private static final String WEB_SERVICE = "http://rest.bioontology.org/bioportal/concepts/";

    @Inject
    public NCBOConceptNeighbourhoodServiceAsyncClientImplementation(
	    DocumentProcessor documentProcessor, URLFetchService urlFetchService) {
	super(documentProcessor, urlFetchService);
    }

    private String constructURL(String conceptId, String ontologyVersionId) {
	return WEB_SERVICE + encode(ontologyVersionId) + "?conceptid="
		+ encode(conceptId) + "&email=example@example.org";
    }

    private String getConceptId(Object r) {
	return documentProcessor.getText(r, "fullId/text()");
    }

    @Override
    public void getNeighbourhood(final Resource concept,
	    final AsyncCallback<NeighbourhoodServiceResult> callback) {

	final String conceptId = (String) concept
		.getValue(NCBO.CONCEPT_SHORT_ID);
	String ontologyId = (String) concept.getValue(NCBO.CONCEPT_ONTOLOGY_ID);

	try {
	    String urlAsString = "http://rest.bioontology.org"
		    + "/bioportal/virtual/ontology/" + ontologyId
		    + "?email=example@example.org";

	    urlFetchService.fetchURL(urlAsString, new AsyncCallback<String>() {
		@Override
		public void onFailure(Throwable caught) {
		    callback.onFailure(caught);
		}

		@Override
		public void onSuccess(String xmlText) {
		    try {
			Object rootNode = documentProcessor
				.parseDocument(xmlText);
			String latestOntologyVersionId = documentProcessor
				.getText(rootNode,
					"//success/data/ontologyBean/id/text()");

			String url = constructURL(conceptId,
				latestOntologyVersionId);

			urlFetchService.fetchURL(url,
				new AsyncCallback<String>() {
				    @Override
				    public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				    }

				    @Override
				    public void onSuccess(String xmlText) {
					try {
					    callback.onSuccess(analyzeXML(
						    concept, xmlText));
					} catch (Exception e) {
					    callback.onFailure(e);
					}
				    }
				});

		    } catch (Exception e) {
			callback.onFailure(e);
		    }
		}
	    });
	} catch (Exception e) {
	    callback.onFailure(e);
	}

    }

    private String process(Object r, boolean reversed, Resource sourceConcept,
	    NeighbourhoodServiceResult result) {

	String ontologyId = (String) sourceConcept
		.getValue(NCBO.CONCEPT_ONTOLOGY_ID);
	String ontologyName = (String) sourceConcept
		.getValue(NCBO.CONCEPT_ONTOLOGY_NAME);

	String conceptId = getConceptId(r);
	String conceptShortId = documentProcessor.getText(r, "id/text()");
	String label = documentProcessor.getText(r, "label/text()");
	int childCount = Integer.parseInt(documentProcessor.getText(r,
		"relations/entry/int/text()"));

	// retrieve & create concept + relationship
	Resource concept = new Resource(NcboUriHelper.toConceptURI(ontologyId,
		conceptShortId));

	concept.putValue(NCBO.CONCEPT_ID, conceptId);
	concept.putValue(NCBO.CONCEPT_SHORT_ID, conceptShortId);
	concept.putValue(NCBO.CONCEPT_NAME, label);
	concept.putValue(NCBO.CONCEPT_ONTOLOGY_ID, ontologyId);
	concept.putValue(NCBO.CONCEPT_CHILD_COUNT, Integer.valueOf(childCount));
	concept.putValue(NCBO.CONCEPT_ONTOLOGY_NAME, ontologyName);

	if (reversed) {
	    result.addRelationship(concept, sourceConcept);
	} else {
	    result.addRelationship(sourceConcept, concept);
	}

	return conceptId;
    }

    private NeighbourhoodServiceResult analyzeXML(final Resource concept,
	    String xmlText) throws Exception {
	NeighbourhoodServiceResult result = new NeighbourhoodServiceResult(
		concept);

	Object rootNode = documentProcessor.parseDocument(xmlText);

	Object[] nodes = documentProcessor.getNodes(rootNode,
		"//success/data/classBean/relations/entry");

	List<Object> processLater = new ArrayList<Object>();
	List<Object> reversedNodes = new ArrayList<Object>();
	List<String> subclassOrSuperclassConceptIds = new ArrayList<String>();

	for (int i = 0; i < nodes.length; i++) {
	    Object node = nodes[i];

	    // (1) test list/classbean size
	    Object[] relationships = documentProcessor.getNodes(node,
		    "list/classBean");

	    if (relationships.length == 0) {
		continue;
	    }

	    // (2) name, check for inverted
	    String name = documentProcessor.getText(node, "string/text()");
	    boolean reversed = name.startsWith(REVERSE_PREFIX);
	    if (reversed) {
		name = name.substring(REVERSE_PREFIX.length());
	    }

	    for (int j = 0; j < relationships.length; j++) {
		Object r = relationships[j];

		if (reversed) {
		    reversedNodes.add(r);
		}

		if (!("SubClass".equals(name) || "SuperClass".equals(name))) {
		    processLater.add(r);
		    continue;
		}

		String conceptId = process(r, "SuperClass".equals(name),
			concept, result);

		subclassOrSuperclassConceptIds.add(conceptId);
	    }

	}

	for (Object n : processLater) {
	    if (subclassOrSuperclassConceptIds.contains(getConceptId(n))) {
		continue;
	    }

	    process(n, reversedNodes.contains(n), concept, result);
	}
	return result;
    }
}
