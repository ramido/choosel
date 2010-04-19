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
package org.thechiselgroup.choosel.server.domain.ncbo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodService;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;
import org.thechiselgroup.choosel.server.XMLCallServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.allen_sauer.gwt.log.client.Log;

// TODO convert to service & clean up
@SuppressWarnings("serial")
public class NCBOConceptNeighbourhoodServiceServlet extends XMLCallServlet
	implements NCBOConceptNeighbourhoodService {

    private static final String REVERSED_DATA_KEY = "reversed";

    private static final String REVERSE_PREFIX = "[R]";

    private static final String WEB_SERVICE = "http://rest.bioontology.org/bioportal/concepts/";

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	setupSetExpression("//success/data/classBean/relations/entry");

	registerExpression(NCBO.CONCEPT_ID, "fullId/text()");
	registerExpression(NCBO.CONCEPT_SHORT_ID, "id/text()");
	registerExpression(NCBO.CONCEPT_NAME, "label/text()");

	// TODO might not be precise enough, sibling of entry/string/text() =
	// "ChildCount"
	registerExpression(NCBO.CONCEPT_CHILD_COUNT,
		"relations/entry/int/text()");
    }

    // TODO refactor in superclass, pull up
    private void analyzeXML(String url, Resource concept,
	    NeighbourhoodServiceResult result) throws ServiceException {

	Log.debug("calling " + url.toString());

	try {
	    NodeList nodes = getSetExpressionNodes(url);

	    List<Node> processLater = new ArrayList<Node>();
	    List<String> subclassOrSuperclassConceptIds = new ArrayList<String>();

	    for (int i = 0; i < nodes.getLength(); i++) {
		Node node = nodes.item(i);

		// (1) test list/classbean size
		XPathExpression pathExpression = xpath
			.compile("list/classBean");
		NodeList relationships = (NodeList) pathExpression.evaluate(
			node, XPathConstants.NODESET);

		if (relationships.getLength() == 0) {
		    continue;
		}

		// (2) name, check for inverted
		XPathExpression nameExpression = xpath.compile("string/text()");
		String name = (String) nameExpression.evaluate(node,
			XPathConstants.STRING);
		boolean reversed = name.startsWith(REVERSE_PREFIX);
		if (reversed) {
		    name = name.substring(REVERSE_PREFIX.length());
		}

		for (int j = 0; j < relationships.getLength(); j++) {
		    Node r = relationships.item(j);

		    r.setUserData(REVERSED_DATA_KEY, Boolean.valueOf(reversed),
			    null);

		    if (!("SubClass".equals(name) || "SuperClass".equals(name))) {
			processLater.add(r);
			continue;
		    }

		    String conceptId = process(r, "SuperClass".equals(name),
			    concept, result);

		    subclassOrSuperclassConceptIds.add(conceptId);
		}

	    }

	    for (Node n : processLater) {
		if (subclassOrSuperclassConceptIds.contains(getConceptId(n))) {
		    continue;
		}

		process(n, ((Boolean) n.getUserData(REVERSED_DATA_KEY))
			.booleanValue(), concept, result);
	    }
	} catch (Exception e) {
	    throw new ServiceException(e.getMessage());
	}
    }

    private String process(Node r, boolean reversed, Resource sourceConcept,
	    NeighbourhoodServiceResult result) throws XPathExpressionException {

	String ontologyId = (String) sourceConcept
		.getValue(NCBO.CONCEPT_ONTOLOGY_ID);
	String ontologyName = (String) sourceConcept
		.getValue(NCBO.CONCEPT_ONTOLOGY_NAME);

	String conceptId = getConceptId(r);
	String conceptShortId = evaluateString(NCBO.CONCEPT_SHORT_ID, r);
	String label = evaluateString(NCBO.CONCEPT_NAME, r);
	int childCount = evaluateNumber(NCBO.CONCEPT_CHILD_COUNT, r).intValue();

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

    private String getConceptId(Node r) throws XPathExpressionException {
	return evaluateString(NCBO.CONCEPT_ID, r);
    }

    @Override
    protected Resource analyzeNode(Node node, String label) throws Exception {
	// TODO
	throw new Exception("remove this method");
    }

    // TODO need to generalize beyond ontology ids: need keys
    @Override
    public NeighbourhoodServiceResult getNeighbourhood(Resource concept)
	    throws ServiceException {

	String conceptId = (String) concept.getValue(NCBO.CONCEPT_SHORT_ID);
	String ontologyId = (String) concept.getValue(NCBO.CONCEPT_ONTOLOGY_ID);

	try {
	    String latestOntologyVersionId = getLatestOntologyVersionId(ontologyId);

	    String url = constructURL(conceptId, latestOntologyVersionId);
	    NeighbourhoodServiceResult result = new NeighbourhoodServiceResult(
		    concept);

	    analyzeXML(url, concept, result);

	    return result;
	} catch (UnsupportedEncodingException e) {
	    throw new ServiceException(e.getMessage());
	}
    }

    private String getLatestOntologyVersionId(String ontologyId)
	    throws ServiceException {

	String urlAsString = "http://rest.bioontology.org"
		+ "/bioportal/virtual/ontology/" + ontologyId
		+ "?email=example@example.org";

	try {
	    Document document = documentFetchService
		    .fetchXML(urlAsString);
	    XPathExpression pathExpression = xpath
		    .compile("//success/data/ontologyBean/id/text()");
	    return (String) pathExpression.evaluate(document,
		    XPathConstants.STRING);
	} catch (Exception e) {
	    throw new ServiceException(e.getMessage());
	}

    }

    private String constructURL(String conceptId, String ontologyVersionId)
	    throws UnsupportedEncodingException {

	return WEB_SERVICE + URLEncoder.encode(ontologyVersionId, "UTF-8")
		+ "?conceptid=" + URLEncoder.encode(conceptId, "UTF-8")
		+ "&email=example@example.org";
    }

}
