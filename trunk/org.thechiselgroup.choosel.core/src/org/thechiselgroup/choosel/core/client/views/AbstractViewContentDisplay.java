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

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.Disposable;

import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractViewContentDisplay implements ViewContentDisplay {

    protected ViewContentDisplayCallback callback;

    private boolean restoring = false;

    private Widget widget;

    @Override
    public Widget asWidget() {
        if (widget == null) {
            widget = createWidget();
        }

        return widget;
    }

    @Override
    public void checkResize() {
    }

    protected abstract Widget createWidget();

    @Override
    public void dispose() {
        callback = null;

        if (widget instanceof Disposable) {
            ((Disposable) widget).dispose();
        }
        widget = null;
    }

    @Override
    public void endRestore() {
        restoring = false;
    }

    public ViewContentDisplayCallback getCallback() {
        return callback;
    }

    @Override
    public SidePanelSection[] getSidePanelSections() {
        return new SidePanelSection[0];
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        assert callback != null;
        this.callback = callback;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    public boolean isRestoring() {
        return restoring;
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        return new Memento();
    }

    @Override
    public void startRestore() {
        restoring = true;
    }

}
