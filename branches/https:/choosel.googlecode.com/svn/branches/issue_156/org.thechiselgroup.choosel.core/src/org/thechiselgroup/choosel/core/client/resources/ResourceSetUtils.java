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
package org.thechiselgroup.choosel.core.client.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

public final class ResourceSetUtils {

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    @Deprecated
    public static Resource firstResource(
            LightweightCollection<Resource> resources) {
        return resources.iterator().next();
    }

    public static DataTypeToListMap<String> getPropertiesByDataType(
            ResourceSet resourceSet) {

        if (resourceSet.isEmpty()) {
            return new DataTypeToListMap<String>();
        }

        // no aggregation
        DataTypeToListMap<String> result = new DataTypeToListMap<String>();
        Resource resource = resourceSet.getFirstResource();

        if (resource == null) {
            return result;
        }

        for (Entry<String, Serializable> entry : resource.getProperties()
                .entrySet()) {

            Serializable value = entry.getValue();
            String propertyName = entry.getKey();

            if (value instanceof String) {
                result.get(DataType.TEXT).add(propertyName);
            }
            if (value instanceof Double) {
                result.get(DataType.NUMBER).add(propertyName);
            }
            if (value instanceof Resource) {
                Resource r = (Resource) value;

                if (r.getValue(LATITUDE) != null
                        && r.getValue(LONGITUDE) != null) {

                    result.get(DataType.LOCATION).add(propertyName);
                }
            }
            if (value instanceof Date) {
                result.get(DataType.DATE).add(propertyName);
            }

        }

        return result;
    }

    public static List<String> getPropertyNamesForDataType(
            ResourceSet resourceSet, DataType dataType) {

        if (resourceSet.isEmpty()) {
            return Collections.emptyList();
        }

        // no aggregation
        Resource resource = resourceSet.getFirstResource();
        List<String> properties = new ArrayList<String>();

        for (Entry<String, Serializable> entry : resource.getProperties()
                .entrySet()) {

            switch (dataType) {
            case TEXT: {
                if (entry.getValue() instanceof String) {
                    properties.add(entry.getKey());
                }
            }
                break;
            case NUMBER: {
                if (entry.getValue() instanceof Double) {
                    properties.add(entry.getKey());
                }
            }
                break;
            case LOCATION: {
                if (entry.getValue() instanceof Resource) {
                    Resource r = (Resource) entry.getValue();

                    if (r.getValue(LATITUDE) != null
                            && r.getValue(LONGITUDE) != null) {

                        properties.add(entry.getKey());
                    }
                }
            }
                break;
            case DATE: {
                if (entry.getValue() instanceof Date) {
                    properties.add(entry.getKey());
                }
            }
                break;
            }

        }

        return properties;

    }

    public static String[] toResourceIds(ResourceSet resources) {
        String[] resourceIds = new String[resources.size()];
        int i = 0;
        for (Resource resource : resources) {
            resourceIds[i++] = resource.getUri();
        }
        return resourceIds;
    }

    private ResourceSetUtils() {

    }

}
