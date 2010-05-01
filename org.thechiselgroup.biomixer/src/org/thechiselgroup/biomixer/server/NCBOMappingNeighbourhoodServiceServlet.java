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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.thechiselgroup.biomixer.client.NCBO;
import org.thechiselgroup.biomixer.client.services.NCBOMappingNeighbourhoodService;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;
import org.thechiselgroup.choosel.server.PMF;
import org.thechiselgroup.choosel.server.urlfetch.CachedDocumentFetchService;

import com.allen_sauer.gwt.log.client.Log;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

// TODO check if there is an aspect-oriented solution to this logging
public class NCBOMappingNeighbourhoodServiceServlet extends
	RemoteServiceServlet implements NCBOMappingNeighbourhoodService {

    private NCBOMappingNeighbourhoodService serviceDelegate = null;

    private DocumentBuilderFactory domFactory;

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	try {
	    domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);

	    // TODO use regular fetch service for production
	    serviceDelegate = new NCBOMappingNeighbourhoodServiceImplementation(
		    new CachedDocumentFetchService(URLFetchServiceFactory
			    .getURLFetchService(), PMF.get(),
			    DocumentBuilderFactory.newInstance()));
	} catch (Exception e) {
	    throw new ServletException(e);
	}

    }

    @Override
    public NeighbourhoodServiceResult getNeighbourhood(Resource concept)
	    throws ServiceException {

	Log.debug("NCBOMappingServiceServlet.getMappings - "
		+ concept.getValue(NCBO.CONCEPT_SHORT_ID));

	try {
	    return serviceDelegate.getNeighbourhood(concept);
	} catch (RuntimeException e) {
	    Log.error("NCBOMappingServiceServlet.getMappings failed: "
		    + e.getMessage(), e);
	    throw new ServiceException(e.getMessage());
	}
    }
}