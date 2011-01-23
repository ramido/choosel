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
package org.thechiselgroup.choosel.core.client.windows;

import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.PROXY;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ProxyWindowContentFactoryResolver {

    private ProxyWindowContentFactory proxyFactory;

    @Inject
    public ProxyWindowContentFactoryResolver(
            @Named(PROXY) WindowContentProducer proxyFactory) {
        this.proxyFactory = (ProxyWindowContentFactory) proxyFactory;
    }

    public void setDelegate(WindowContentProducer delegate) {
        proxyFactory.setDelegate(delegate);
    }

}
