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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.thechiselgroup.choosel.client.views.DataType;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.map.MapViewContentDisplay;

public final class ResourceSetUtils {

    public static List<String> getPropertyNamesForDataType(ResourceSet resourceSet,
            DataType dataType) {

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

                    if (r.getValue(MapViewContentDisplay.LATITUDE) != null
                            && r.getValue(MapViewContentDisplay.LONGITUDE) != null) {

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

    // TODO move
    // TODO write test cases
    // return: property keys
    public static List<String> getPropertyNamesForDataType(
            Set<ResourceItem> resourceItems, DataType dataType) {

        if (resourceItems.isEmpty()) {
            return new ArrayList<String>();
        }

        /*
         * assertion: first add, no aggregation, homogeneous resource set
         */
        assert resourceItems.size() >= 1;

        // homogeneous resource set --> look only at first item
        ResourceItem resourceItem = (ResourceItem) resourceItems.toArray()[0];

        // TODO this should be a condition of resource item in general
        assert resourceItem.getResourceSet().size() >= 1;

        return getPropertyNamesForDataType(resourceItem.getResourceSet(),
                dataType);
    }

    private ResourceSetUtils() {

    }

}