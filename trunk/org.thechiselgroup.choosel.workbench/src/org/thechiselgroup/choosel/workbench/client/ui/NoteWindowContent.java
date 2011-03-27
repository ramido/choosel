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
package org.thechiselgroup.choosel.workbench.client.ui;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.dnd.client.windows.AbstractWindowContent;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class NoteWindowContent extends AbstractWindowContent implements
        Persistable {

    private static final String CSS_NOTE = "note";

    private static final String MEMENTO_NOTE = CSS_NOTE;

    private TextArea noteArea;

    public NoteWindowContent() {
        super("Note", MEMENTO_NOTE);
    }

    @Override
    public Widget asWidget() {
        return noteArea;
    }

    @Override
    public void init() {
        super.init();
        noteArea = new TextArea();
        noteArea.addStyleName(CSS_NOTE);
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        noteArea.setText((String) state.getValue(MEMENTO_NOTE));
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento state = new Memento();
        state.setValue(MEMENTO_NOTE, noteArea.getValue());
        return state;
    }
}