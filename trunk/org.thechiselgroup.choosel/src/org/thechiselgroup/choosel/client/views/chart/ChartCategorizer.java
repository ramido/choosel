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
package org.thechiselgroup.choosel.client.views.chart;

import static org.thechiselgroup.choosel.client.util.CollectionUtils.toSet;

import java.util.Set;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;

public class ChartCategorizer implements ResourceMultiCategorizer {

    private ResourceCategorizer resourceByTypeCategorizer;

    private String propertyName;

    public ChartCategorizer(ResourceCategorizer resourceByTypeCategorizer,
            String propertyName) {

        this.resourceByTypeCategorizer = resourceByTypeCategorizer;
        this.propertyName = propertyName;
    }

    @Override
    public Set<String> getCategories(Resource resource) {
        String category = resourceByTypeCategorizer.getCategory(resource);

        if (category.equals("workitem")) {
            return toSet((String) resource.getValue(propertyName));
        }

        if (category.equals(TestResourceSetFactory.DEFAULT_TYPE)) {
            // TODO split by iteration
            return toSet((String) resource
                    .getValue(TestResourceSetFactory.LABEL));
        }

        return toSet();
    }

    protected String getPropertyName() {
        return propertyName;
    }
}