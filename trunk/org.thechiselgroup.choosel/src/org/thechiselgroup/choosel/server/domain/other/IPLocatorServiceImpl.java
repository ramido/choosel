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
package org.thechiselgroup.choosel.server.domain.other;

import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.thechiselgroup.choosel.client.domain.other.IPLocatorService;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.server.XMLCallServlet;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class IPLocatorServiceImpl extends XMLCallServlet implements
	IPLocatorService {

    private static final String WEB_SERVICE_NO_IP = "http://ipinfodb.com/ip_query.php";

    private static final String WEB_SERVICE = WEB_SERVICE_NO_IP + "?ip=";

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	setupSetExpression("//Response");

	registerExpression("ip", "Ip/text()");
	registerExpression("latitude", "Latitude/text()");
	registerExpression("longitude", "Longitude/text()");
	registerExpression("city", "City/text()");
    }

    @Override
    protected Resource analyzeNode(Node node, String label) throws Exception {
	String address = evaluateString("ip", node);

	float latitude = evaluateNumber("latitude", node).floatValue();
	float longitude = evaluateNumber("longitude", node).floatValue();

	Resource location = new Resource("location:" + latitude + "/"
		+ longitude);

	location.putValue("latitude", latitude);
	location.putValue("longitude", longitude);

	Resource ipAddress = new Resource("ip:" + address);
	ipAddress.putValue("location", location);
	ipAddress.putValue("address", address);
	ipAddress.putValue("city", evaluateString("city", node));

	return ipAddress;
    }

    @Override
    public Set<Resource> getClientLocation() throws ServiceException {
	return analyzeXML(createURL(), "IP address");
    }

    private String createURL() {
	String ip = getThreadLocalRequest().getRemoteHost();

	// use server IP if no useful ip client is present
	if ("127.0.0.1".equals(ip)) {
	    return WEB_SERVICE_NO_IP;
	}

	return WEB_SERVICE + ip;
    }

}
