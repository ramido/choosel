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
package org.thechiselgroup.choosel.core.client.views.behaviors;

import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemContainerChangeEvent;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

public class ViewInteractionLogger implements ViewItemBehavior {

    private Logger logger;

    public ViewInteractionLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        logger.info("onInteraction[Interaction=" + interaction + " ; ViewItem="
                + viewItem + "]");
    }

    @Override
    public void onViewItemContainerChanged(ViewItemContainerChangeEvent event) {
    }

}