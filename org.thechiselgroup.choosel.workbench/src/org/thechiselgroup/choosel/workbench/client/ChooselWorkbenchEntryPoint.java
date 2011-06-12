/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.workbench.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;

public abstract class ChooselWorkbenchEntryPoint implements EntryPoint {

    /**
     * <p>
     * Choosel applications should override to implement their own
     * {@link ChooselWorkbenchGinjector} that links their custom configuration
     * module. They need to call GWT.create with their own classes, because the
     * GWT compilation requires GWT.create to be called with a class literal.
     * </p>
     * <p>
     * Example <code>
     * return GWT.create(ChooselGinjector.class);</code> with subclass of
     * {@link ChooselWorkbenchGinjector}
     * </p>
     */
    protected abstract ChooselWorkbenchGinjector createChooselGinjector();

    @Override
    public final void onModuleLoad() {
        ChooselWorkbenchGinjector injector = createChooselGinjector();

        try {
            injector.getApplicationInitializer().init();
        } catch (Throwable ex) {
            Logger logger = injector.getLoggerProvider().getLogger();
            logger.log(Level.SEVERE,
                    "Could not initialize Choosel: " + ex.getMessage(), ex);
        }
    }

}