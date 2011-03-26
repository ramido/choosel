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
package org.thechiselgroup.choosel.core.client.views;

import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.views.model.ResourceModel;
import org.thechiselgroup.choosel.core.client.views.model.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewModel;
import org.thechiselgroup.choosel.core.client.windows.WindowContent;

public interface View extends WindowContent, Disposable, Persistable {

    /**
     * @return {@link ViewModel} that is used in this {@link View}.
     */
    ViewModel getModel();

    ResourceModel getResourceModel();

    SelectionModel getSelectionModel();

}