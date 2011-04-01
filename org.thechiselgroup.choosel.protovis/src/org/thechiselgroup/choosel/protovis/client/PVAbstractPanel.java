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
package org.thechiselgroup.choosel.protovis.client;

import com.google.gwt.user.client.Element;

/**
 * 
 * @author Lars Grammel
 */
public abstract class PVAbstractPanel<T extends PVAbstractPanel<T>> extends
        PVAbstractBar<T> {

    protected PVAbstractPanel() {
    }

    public final native T canvas(Element element) /*-{
        return this.canvas(element);
    }-*/;

    public final native T canvas(String elementId) /*-{
        return this.canvas(element);
    }-*/;

    public final native String overflow() /*-{
        return this.overflow();
    }-*/;

    public final native T overflow(String overflow) /*-{
        return this.overflow(overflow);
    }-*/;

    public final native PVTransform transform() /*-{
        return this.transform();
    }-*/;

    public final native T transform(PVTransform transform) /*-{
        return this.transform(transform);
    }-*/;

}