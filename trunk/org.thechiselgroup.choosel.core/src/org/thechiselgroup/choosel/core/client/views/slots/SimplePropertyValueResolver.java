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
package org.thechiselgroup.choosel.core.client.views.slots;

import org.thechiselgroup.choosel.core.client.resources.Resource;

public class SimplePropertyValueResolver implements ResourceToValueResolver {

    private String propertyName;

    public SimplePropertyValueResolver(String propertyName) {
        super();
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public Object resolve(Resource resource) {
        /*
         * Do not change this to .toString()
         */
        return resource.getValue(propertyName);
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

}
