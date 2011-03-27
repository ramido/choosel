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
package org.thechiselgroup.choosel.dnd.client.popup;

import org.thechiselgroup.choosel.core.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.dnd.client.DragProxyEventReceiver;

public class DragSupportingPopupManager extends DefaultPopupManager {

    public DragSupportingPopupManager(WidgetFactory widgetFactory) {
        super(widgetFactory);
    }

    protected class DragSupportingPopupPanel extends PopupPanel implements
            DragProxyEventReceiver {

        @Override
        public void dragProxyAttached() {
            // do nothing: popup should be visible during drop operation
        }

        @Override
        public void dragProxyDetached() {
            if (isEnabled()) {
                // TODO use event instead that demands closing
                // hide once drop operation is completed
                setState(INACTIVE_STATE);
            }
        }
    }

    @Override
    protected PopupPanel createPopupPanel() {
        return new DragSupportingPopupPanel();
    }

}