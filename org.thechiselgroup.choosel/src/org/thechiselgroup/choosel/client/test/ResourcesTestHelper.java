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
package org.thechiselgroup.choosel.client.test;

import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public final class ResourcesTestHelper {

    public static final String DEFAULT_TYPE = "type";

    public static final String LABEL = "label";

    public static final String LABEL_KEY = "label-key";

    public static final String X_COORD = "x-coord";

    public static final String Y_COORD = "y-coord";

    public static DefaultResourceSet createLabeledResources(int... indices) {
        return createLabeledResources(LABEL, DEFAULT_TYPE, indices);
    }

    public static DefaultResourceSet createLabeledResources(String type,
            int... indices) {
        return createLabeledResources(LABEL, type, indices);
    }

    public static DefaultResourceSet createLabeledResources(String label,
            String type, int... indices) {

        DefaultResourceSet resources = createResources(type, indices);
        resources.setLabel(label);
        return resources;
    }

    public static Resource createResource(int index) {
        return createResource(DEFAULT_TYPE, index);
    }

    public static Resource createResource(String type, int index) {
        Resource r = new Resource(type + ":" + index);
        r.putValue(X_COORD, Math.random() * 10);
        r.putValue(Y_COORD, Math.random() * 10);
        r.putValue(LABEL_KEY, r.getValue(X_COORD) + " " + r.getValue(Y_COORD));
        return r;
    }

    public static DefaultResourceSet createResources(int... indices) {
        return createResources(DEFAULT_TYPE, indices);
    }

    public static DefaultResourceSet createResources(String type,
            int... indices) {

        DefaultResourceSet resources = new DefaultResourceSet();
        for (int i : indices) {
            resources.add(createResource(type, i));
        }
        return resources;
    }

    public static DefaultResourceSet toLabeledResources(
            ResourceSet... resourceSets) {

        DefaultResourceSet result = toResourceSet(resourceSets);
        result.setLabel(LABEL);
        return result;
    }

    public static DefaultResourceSet toResourceSet(Resource... resources) {
        DefaultResourceSet result = new DefaultResourceSet();
        for (Resource resource : resources) {
            result.add(resource);
        }
        return result;
    }

    public static DefaultResourceSet toResourceSet(ResourceSet... resourceSets) {
        DefaultResourceSet result = new DefaultResourceSet();
        for (ResourceSet resources : resourceSets) {
            result.addAll(resources);
        }
        return result;
    }

    private ResourcesTestHelper() {
    }

}
