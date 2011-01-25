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


public abstract class AbstractDialog implements Dialog {

    private DialogCallback callback;

    @Override
    public void handleException(Exception ex) {
        throw new RuntimeException(ex); // TODO better handling
    }

    @Override
    public void init(DialogCallback callback) {
        assert callback != null;

        this.callback = callback;

    }

    protected void setOkayButtonEnabled(boolean enabled) {
        // TODO find better initialization order
        if (callback == null) {
            return;
        }

        callback.setOkayButtonEnabled(enabled);
    }

}
