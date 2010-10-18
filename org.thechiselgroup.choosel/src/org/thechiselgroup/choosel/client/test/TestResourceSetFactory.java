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

import java.util.Date;

import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

import com.google.gwt.user.client.Random;

public final class TestResourceSetFactory {

    public static final String TYPE_1 = "type-1";

    public static final String TYPE_2 = "type-2";

    public static final String LABEL = "label";

    public static final String LABEL_KEY = "label-key";

    public static final String X_COORD = "x-coord";

    public static final String Y_COORD = "y-coord";

    /**
     * for manual testing in app
     */
    public static ResourceSet addGraphTestData(ResourceSet graphResourceSet) {
        graphResourceSet.setLabel("GraphTest");
        for (int i = 0; i < 25; i++) {
            Resource resource = new Resource("graphtest:" + i);
            resource.putValue("title", "graphtest:" + i);
            if (i > 0) {
                resource.putValueAsUriList("parent", "graphtest:" + (i - 1));
            }
            graphResourceSet.add(resource);
        }
        return graphResourceSet;
    }

    /**
     * for manual testing in app
     */
    public static ResourceSet addTestData(ResourceSet resourceSet) {
        int counter = 0;
        resourceSet.setLabel("Test");
        for (int i = 0; i < 50; i++) {
            resourceSet.add(createResource(i));
        }
        for (Resource resource : resourceSet) {
            resource.putValue("date", new Date(
                    1281991537 + 100000 * (counter++)).toString());
            resource.putValue("magnitude", Random.nextInt(10));
            int category = Random.nextInt(10);
            resource.putValue("tagContent", "test" + category);
            resource.putValue("label", "test" + category);
        }
        return resourceSet;
    }

    public static ResourceSet createLabeledResources(int... indices) {
        return createLabeledResources(LABEL, TYPE_1, indices);
    }

    public static ResourceSet createLabeledResources(String type,
            int... indices) {
        return createLabeledResources(LABEL, type, indices);
    }

    public static ResourceSet createLabeledResources(String label, String type,
            int... indices) {

        ResourceSet resources = createResources(type, indices);
        resources.setLabel(label);
        return resources;
    }

    public static Resource createResource(int index) {
        return createResource(TYPE_1, index);
    }

    /**
     * Creates test resources with random x, y coords and label.
     */
    public static Resource createResource(String type, int index) {
        Resource r = new Resource(type + ":" + index);
        r.putValue(X_COORD, Math.random() * 10);
        r.putValue(Y_COORD, Math.random() * 10);
        r.putValue(LABEL_KEY, r.getValue(X_COORD) + " " + r.getValue(Y_COORD));
        return r;
    }

    public static ResourceSet createResources(int... indices) {
        return createResources(TYPE_1, indices);
    }

    public static ResourceSet createResources(String type, int... indices) {

        DefaultResourceSet resources = new DefaultResourceSet();
        for (int i : indices) {
            resources.add(createResource(type, i));
        }
        return resources;
    }

    public static ResourceSet toLabeledResources(ResourceSet... resourceSets) {

        ResourceSet result = toResourceSet(resourceSets);
        result.setLabel(LABEL);
        return result;
    }

    public static ResourceSet toResourceSet(Resource... resources) {
        DefaultResourceSet result = new DefaultResourceSet();
        for (Resource resource : resources) {
            result.add(resource);
        }
        return result;
    }

    public static ResourceSet toResourceSet(ResourceSet... resourceSets) {
        DefaultResourceSet result = new DefaultResourceSet();
        for (ResourceSet resources : resourceSets) {
            result.addAll(resources);
        }
        return result;
    }

    private TestResourceSetFactory() {
    }

}
