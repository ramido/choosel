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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOConceptSearchService;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.server.XMLCallServlet;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class NCBOConceptSearchServiceImpl extends XMLCallServlet implements
	NCBOConceptSearchService {

    private static final String WEB_SERVICE = "http://rest.bioontology.org/bioportal/search/";

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	setupSetExpression("//success/data/page/contents/searchResultList/searchBean");

	registerExpression(NCBO.CONCEPT_ID, "conceptId/text()");
	registerExpression(NCBO.CONCEPT_SHORT_ID, "conceptIdShort/text()");
	registerExpression(NCBO.CONCEPT_NAME, "preferredName/text()");
	registerExpression(NCBO.CONCEPT_ONTOLOGY_ID, "ontologyId/text()");
	registerExpression(NCBO.CONCEPT_ONTOLOGY_NAME,
		"ontologyDisplayLabel/text()");
    }

    @Override
    protected Resource analyzeNode(Node node, String label) throws Exception {
	String conceptShortId = evaluateString(NCBO.CONCEPT_SHORT_ID, node);
	String ontologyId = evaluateString(NCBO.CONCEPT_ONTOLOGY_ID, node);

	Resource concept = new Resource(NcboUriHelper.toConceptURI(ontologyId,
		conceptShortId));

	concept
		.putValue(NCBO.CONCEPT_ID,
			evaluateString(NCBO.CONCEPT_ID, node));
	concept.putValue(NCBO.CONCEPT_SHORT_ID, conceptShortId);
	concept.putValue(NCBO.CONCEPT_NAME, evaluateString(NCBO.CONCEPT_NAME,
		node));
	concept.putValue(NCBO.CONCEPT_ONTOLOGY_ID, ontologyId);
	concept.putValue(NCBO.CONCEPT_ONTOLOGY_NAME, evaluateString(
		NCBO.CONCEPT_ONTOLOGY_NAME, node));

	return concept;
    }

    @Override
    public Set<Resource> searchConcepts(String queryText)
	    throws ServiceException {
	try {
	    String url = WEB_SERVICE + URLEncoder.encode(queryText, "UTF-8")
		    + "?isexactmatch=1";
	    return analyzeXML(url, "concept");
	} catch (UnsupportedEncodingException e) {
	    throw new ServiceException(e.getMessage());
	}
    }
}
