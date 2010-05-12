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
package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;

public class DefaultWindowContentProducer implements WindowContentProducer {

    private Map<String, WindowContentFactory> windowContentFactories = new HashMap<String, WindowContentFactory>();

    @Override
    public WindowContent createWindowContent(String contentType) {
	assert contentType != null;
	assert windowContentFactories.containsKey(contentType);

	return windowContentFactories.get(contentType).createWindowContent();
    }

    public void register(String contentType,
	    WindowContentFactory windowContentFactory) {

	assert contentType != null;
	assert windowContentFactory != null;

	windowContentFactories.put(contentType, windowContentFactory);
    }
}