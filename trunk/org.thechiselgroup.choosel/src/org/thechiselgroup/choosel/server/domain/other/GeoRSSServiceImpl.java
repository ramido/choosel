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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import org.thechiselgroup.choosel.client.domain.other.GeoRSSService;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.server.XMLCallServlet;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class GeoRSSServiceImpl extends XMLCallServlet implements GeoRSSService {

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	xpath.setNamespaceContext(new NamespaceContext() {

	    public String getNamespaceURI(String prefix) {
		if (prefix == null)
		    throw new NullPointerException("Null prefix");

		if ("georss".equals(prefix)) {
		    return "http://www.georss.org/georss";
		}
		if ("geo".equals(prefix)) {
		    return "http://www.w3.org/2003/01/geo/wgs84_pos#";
		}
		if ("eq".equals(prefix)) {
		    return "http://earthquake.usgs.gov/rss/1.0/";
		}
		if ("xml".equals(prefix)) {
		    return XMLConstants.XML_NS_URI;
		}
		return XMLConstants.NULL_NS_URI;
	    }

	    // This method isn't necessary for XPath processing.
	    public String getPrefix(String uri) {
		throw new UnsupportedOperationException();
	    }

	    // This method isn't necessary for XPath processing either.
	    public Iterator getPrefixes(String uri) {
		throw new UnsupportedOperationException();
	    }

	});

	setupSetExpression("//rss/channel/item");

	registerExpression("latitude", "geo:lat/text()");
	registerExpression("longitude", "geo:long/text()");
	registerExpression("title", "title/text()");
	registerExpression("seconds", "eq:seconds/text()");
	registerExpression("description", "description/text()");
	registerExpression("guid", "guid/text()");
	registerExpression("link", "link/text()");
    }

    @Override
    protected Resource analyzeNode(Node node, String label) throws Exception {
	Resource resource = null;

	if ("earthquake".equalsIgnoreCase(label)) {
	    String link = evaluateString("link", node);
	    link = link.substring(0, link.length() - 2);
	    resource = new Resource("earthquake:"
		    + link.substring(link.lastIndexOf("/")));

	    long milliseconds = evaluateNumber("seconds", node).longValue() * 1000;
	    String titleString = evaluateString("title", node);
	    int firstSplitIndex = titleString.indexOf('-');
	    float magnitude = Float.parseFloat(titleString.substring(0,
		    firstSplitIndex).trim());
	    String locationDescription = titleString.substring(
		    firstSplitIndex + 1).trim();

	    resource.putValue("date", new Date(milliseconds).toString());
	    resource.putValue("description", titleString);
	    resource.putValue("magnitude", magnitude);
	    resource.putValue("details", evaluateString("description", node));
	    resource.putValue("locationDescription", locationDescription);
	} else if ("tsunami".equalsIgnoreCase(label)) {
	    resource = new Resource("tsunami:" + evaluateString("guid", node));

	    String descriptionString = evaluateString("description", node);
	    String[] split = descriptionString.split("\n");

	    extractEarthquakeIssuedAt(resource, split);
	    extractEarthquakeFromTsunami(resource, split);
	    extractTsunamiEvaluation(resource, descriptionString);
	}

	resource.putValue("location", parseLocation(node));

	return resource;
    }

    private void extractEarthquakeIssuedAt(Resource feedEntry, String[] split)
	    throws ParseException {
	String issuedAtLine = split[3];
	String issuedAt = issuedAtLine.substring(10, issuedAtLine.length());
	SimpleDateFormat format = new SimpleDateFormat("kkmm'Z' dd MMM yyyy");
	format.setTimeZone(TimeZone.getTimeZone("UTC")); // assume UTC...
	feedEntry.putValue("date", format.parse(issuedAt).toString());
    }

    private void extractTsunamiEvaluation(Resource r, String descriptionString) {
	int i1 = descriptionString.indexOf("EVALUATION");
	String d2 = descriptionString.substring(i1);
	String[] s2 = d2.split("\n");
	String evaluation = "";
	for (int i = 1; i < s2.length; i++) {
	    if (!(s2[i].length() == 0 || s2[i].charAt(0) == ' ')) {
		break;
	    }
	    evaluation += s2[i].toLowerCase() + "<br>";
	}

	r.putValue("evaluation", evaluation);
    }

    private void extractEarthquakeFromTsunami(Resource r, String[] split) {
	String earthquake = split[20] + "<br/>" + split[21] + "<br/>"
		+ split[22] + "<br/>" + split[23] + "<br/>" + split[24];

	r.putValue("related earthquake description", earthquake);
    }

    private Resource parseLocation(Node node) throws XPathExpressionException {

	float latitude = evaluateNumber("latitude", node).floatValue();
	float longitude = evaluateNumber("longitude", node).floatValue();

	Resource location = new Resource("location:" + latitude + "/"
		+ longitude);

	location.putValue("latitude", latitude);
	location.putValue("longitude", longitude);

	return location;
    }

    @Override
    public Set<Resource> getGeoRSS(String url, String label)
	    throws ServiceException {
	return analyzeXML(url, label);
    }

}
