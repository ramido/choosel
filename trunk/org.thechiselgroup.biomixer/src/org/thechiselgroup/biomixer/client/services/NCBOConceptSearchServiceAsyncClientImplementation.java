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

import java.util.HashSet;
import java.util.Set;

import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.util.DocumentProcessor;
import org.thechiselgroup.choosel.client.util.URLFetchService;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class NCBOConceptSearchServiceAsyncClientImplementation extends
	AbstractXMLWebResourceService implements NCBOConceptSearchServiceAsync {

    private static final String WEB_SERVICE = "http://rest.bioontology.org/bioportal/search/?query=";

    @Inject
    public NCBOConceptSearchServiceAsyncClientImplementation(
	    DocumentProcessor documentProcessor, URLFetchService urlFetchService) {

	super(documentProcessor, urlFetchService);
    }

    @Override
    public void searchConcepts(String queryText,
	    final AsyncCallback<Set<Resource>> callback) {

	try {
	    analyzeURL(getUrl(queryText),
		    "//success/data/page/contents/searchResultList/searchBean",
		    callback);
	} catch (Exception e) {
	    callback.onFailure(e);
	}
    }

    protected void analyzeURL(String url, final String rootExpression,
	    final AsyncCallback<Set<Resource>> callback) {

	urlFetchService.fetchURL(url, new AsyncCallback<String>() {

	    @Override
	    public void onSuccess(String xmlText) {
		try {
		    Object rootNode = documentProcessor.parseDocument(xmlText);

		    Set<Resource> resources = new HashSet<Resource>();

		    Object[] nodes = documentProcessor.getNodes(rootNode,
			    rootExpression);
		    for (Object node : nodes) {
			resources.add(analyzeNode(node));
		    }

		    callback.onSuccess(resources);
		} catch (Exception e) {
		    callback.onFailure(e);
		}
	    }

	    @Override
	    public void onFailure(Throwable caught) {
		callback.onFailure(caught);
	    }
	});
    }

    protected Resource analyzeNode(Object node) {
	String conceptShortId = documentProcessor.getText(node,
		"conceptIdShort/text()");
	String ontologyId = documentProcessor
		.getText(node, "ontologyId/text()");

	Resource concept = new Resource(NcboUriHelper.toConceptURI(ontologyId,
		conceptShortId));

	concept.putValue(NCBO.CONCEPT_ID, documentProcessor.getText(node,
		"conceptId/text()"));
	concept.putValue(NCBO.CONCEPT_SHORT_ID, conceptShortId);
	concept.putValue(NCBO.CONCEPT_NAME, documentProcessor.getText(node,
		"preferredName/text()"));
	concept.putValue(NCBO.CONCEPT_ONTOLOGY_ID, ontologyId);
	concept.putValue(NCBO.CONCEPT_ONTOLOGY_NAME, documentProcessor.getText(
		node, "ontologyDisplayLabel/text()"));

	return concept;
    }

    private String getUrl(String queryText) {
	return WEB_SERVICE + encode(queryText) + "&isexactmatch=1";
    }

}
