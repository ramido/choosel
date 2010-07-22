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
package org.thechiselgroup.chooselexample.client;

import org.thechiselgroup.choosel.client.resolver.CountSingleResourceValueResolver;
import org.thechiselgroup.choosel.client.resolver.PropertyValueResolverConverterWrapper;
import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;
import org.thechiselgroup.choosel.client.resolver.SimplePropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.client.util.ConversionException;
import org.thechiselgroup.choosel.client.util.Converter;
import org.thechiselgroup.choosel.client.views.DefaultSlotResolver;

public class ChooselExampleSlotResolver extends DefaultSlotResolver {

    @Override
    public ResourceToValueResolver createDescriptionSlotResolver(String category) {
	// TODO switch based on category -- need category as part of layerModel
	// TODO resources as part of layerModel
	// TODO how to do the automatic color assignment?
	// TODO refactor // extract
	if ("tsunami".equals(category)) {
	    return new SimplePropertyValueResolver("date");
	}

        if ("earthquake".equals(category)) {
            return new SimplePropertyValueResolver("description");
        }

        if ("csv".equals(category)) {
            return new SimplePropertyValueResolver("value");
        }

        if (TestResourceSetFactory.DEFAULT_TYPE.equals(category)) {
            return new SimplePropertyValueResolver(
                    TestResourceSetFactory.LABEL_KEY);
        }

        throw new RuntimeException("failed creating slot mapping");
    }

    @Override
    public ResourceToValueResolver createLabelSlotResolver(String category) {

        Converter<Float, String> converter = new Converter<Float, String>() {
            @Override
            public String convert(Float value) throws ConversionException {
                if (value != null) {
                    int f = (value).intValue();
                    return "" + f;
                }

                return "";
            }
        };

        SimplePropertyValueResolver resolver;
        if ("csv".equals(category)) {
            return new SimplePropertyValueResolver("value");
        } else {
            resolver = new SimplePropertyValueResolver(
                    "magnitude");
        }

        return new PropertyValueResolverConverterWrapper(resolver, converter);
    }

    @Override
    public ResourceToValueResolver createDateSlotResolver(String type) {
        return new SimplePropertyValueResolver("date");
    }

    @Override
    public ResourceToValueResolver createLocationSlotResolver(String category) {
        return new SimplePropertyValueResolver("location");
    }

    @Override
    public ResourceToValueResolver createMagnitudeSlotResolver(String type) {
            return new SimplePropertyValueResolver("magnitude");
    }
    
    @Override
    public ResourceToValueResolver createTagSizeSlotResolver(String category){
        return new CountSingleResourceValueResolver();
    }

    @Override
    public ResourceToValueResolver createTagLabelSlotResolver(String category) {
        return new SimplePropertyValueResolver("description");
    }
}
