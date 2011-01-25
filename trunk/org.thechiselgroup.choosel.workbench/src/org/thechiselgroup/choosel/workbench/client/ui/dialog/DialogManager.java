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
package org.thechiselgroup.choosel.workbench.client.ui.dialog;

import org.thechiselgroup.choosel.core.client.ui.shade.ShadeManager;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;

/**
 * Implementation note: we need to wait until after the fade out operation
 * before calling dialog.okay/cancelled as these might block UI updates - but we
 * loose 0.5 seconds on that - can we somehow parallize these things so the UI
 * gets updated & the server gets called? TODO
 */
public class DialogManager {

    private AbsolutePanel parentPanel;

    private ShadeManager shadeManager;

    public DialogManager(AbsolutePanel parentPanel, ShadeManager shadeManager) {
        this.parentPanel = parentPanel;
        this.shadeManager = shadeManager;
    }

    @Inject
    public DialogManager(ShadeManager shadeManager) {
        this(RootPanel.get(), shadeManager);
    }

    public void show(Dialog dialog) {
        assert dialog != null;

        new DialogWindowManager(parentPanel, dialog, shadeManager).init();
    }
}