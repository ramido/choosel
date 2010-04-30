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
package org.thechiselgroup.choosel.client.resources;

import java.io.Serializable;
import java.util.HashMap;

// TODO introduce resource ID's
// TODO equality / hash based on ID
public class Resource implements Serializable {

    private static final long serialVersionUID = 5652752520235015241L;

    private HashMap<String, Serializable> properties = new HashMap<String, Serializable>();

    // unique resource identifier (URI)
    private String uri;

    // for GWT serialization usage only
    public Resource() {
    }

    public Resource(String uri) {
	assert uri != null;
	this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Resource other = (Resource) obj;
	if (uri == null) {
	    if (other.uri != null)
		return false;
	} else if (!uri.equals(other.uri))
	    return false;
	return true;
    }

    public HashMap<String, Serializable> getProperties() {
	return properties;
    }

    public String getUri() {
	return uri;
    }

    public UriList getUriListValue(String key) {
	UriList result = (UriList) getValue(key);

	if (result == null) {
	    result = new UriList();
	    putValue(key, result);
	}

	return result;
    }

    public Object getValue(String key) {
	return properties.get(key);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((uri == null) ? 0 : uri.hashCode());
	return result;
    }

    public void putValue(String key, Serializable value) {
	properties.put(key, value);
    }

    @Override
    public String toString() {
	return "Resource [uri=" + uri + "]";
    }

}
