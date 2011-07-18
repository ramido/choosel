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
package org.thechiselgroup.choosel.core.client.visualization.model;

import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.core.client.util.Adaptable;
import org.thechiselgroup.choosel.core.client.util.Disposable;

/**
 * Interface for generic visualizations that can be displayed in a
 * {@link VisualizationModel}.
 * 
 * @author Lars Grammel
 * 
 * @see VisualizationModel
 * @see VisualItem
 */
// TODO rename to visualization display or visualization renderer
// NOTE: 3 main items: slots, properties, functionality interfaces
// NOTE: has lifecycle (which should be described)
public interface ViewContentDisplay extends VisualItemRenderer,
        WidgetAdaptable, Disposable, Persistable, Adaptable {

    void checkResize();

    void endRestore();

    /**
     * Returns a descriptive name of the visualization component, e.g.
     * 'Scatterplot'. The name will be used to generate visualization
     * descriptions.
     */
    String getName();

    /**
     * Returns the current value of the property.
     * 
     * @see #setPropertyValue(String, Object)
     */
    <T> T getPropertyValue(String property);

    /**
     * @return {@link SidePanelSection}s for configuring this view content
     *         display.
     */
    // XXX view content displays should not expose side panel sections
    // instead they should provide interfaces they can be adapted to
    SidePanelSection[] getSidePanelSections();

    /**
     * @return {@link Slot}s that are supported by this view content display.
     */
    Slot[] getSlots();

    /**
     * Sets callback objects.
     */
    void init(VisualItemContainer container, ViewContentDisplayCallback callback);

    boolean isReady();

    <T> void setPropertyValue(String property, T value);

    void startRestore();

}