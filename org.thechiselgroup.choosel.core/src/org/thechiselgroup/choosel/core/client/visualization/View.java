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
package org.thechiselgroup.choosel.core.client.visualization;

import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.core.client.util.Adaptable;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.Initializable;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualizationModel;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.ResourceModel;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.SelectionModel;

public interface View extends Adaptable, WidgetAdaptable, Initializable,
        Disposable, Persistable {

    String getContentType();

    String getLabel();

    /**
     * @return {@link VisualizationModel} that is used in this {@link View}.
     */
    VisualizationModel getModel();

    ResourceModel getResourceModel();

    SelectionModel getSelectionModel();

    void setLabel(String label);

}