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
package org.thechiselgroup.choosel.client.views;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.SwitchingResourceSet;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.Initializable;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public class DefaultSelectionModel implements SelectionModel, Disposable,
        Persistable, Initializable {

    static final String MEMENTO_SELECTION = "selection";

    static final String MEMENTO_SELECTION_SET_COUNT = "selectionSetCount";

    static final String MEMENTO_SELECTION_SET_PREFIX = "selectionSet-";

    private List<ResourceSet> selectionSets = new ArrayList<ResourceSet>();

    private SwitchingResourceSet selection = new SwitchingResourceSet();

    private ResourceSetsPresenter selectionDropPresenter;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetsPresenter selectionPresenter;

    private final ResourceSetFactory resourceSetFactory;

    public DefaultSelectionModel(ResourceSetsPresenter selectionDropPresenter,
            LabelProvider selectionModelLabelFactory,
            ResourceSetsPresenter selectionPresenter,
            ResourceSetFactory resourceSetFactory) {

        assert selectionModelLabelFactory != null;
        assert selectionPresenter != null;
        assert selectionDropPresenter != null;
        assert resourceSetFactory != null;

        this.selectionDropPresenter = selectionDropPresenter;
        this.selectionModelLabelFactory = selectionModelLabelFactory;
        this.selectionPresenter = selectionPresenter;
        this.resourceSetFactory = resourceSetFactory;
    }

    public HandlerRegistration addEventHandler(
            ResourcesAddedEventHandler handler) {
        return selection.addEventHandler(handler);
    }

    public HandlerRegistration addEventHandler(
            ResourcesRemovedEventHandler handler) {
        return selection.addEventHandler(handler);
    }

    @Override
    public void addSelectionSet(ResourceSet selectionSet) {
        assert selectionSet != null;

        selectionSets.add(selectionSet);
        selectionPresenter.addResourceSet(selectionSet);
    }

    public Widget asDropPresenterWidget() {
        return selectionDropPresenter.asWidget();
    }

    public Widget asSelectionPresenterWidget() {
        return selectionPresenter.asWidget();
    }

    @Override
    public boolean containsSelectionSet(ResourceSet resourceSet) {
        return selectionSets.contains(resourceSet);
    }

    @Override
    public void dispose() {
        selectionPresenter.dispose();
        selectionPresenter = null;
        selectionDropPresenter.dispose();
        selectionDropPresenter = null;
        selection.dispose();
        selection = null;
    }

    @Override
    public ResourceSet getSelection() {
        return selection.getDelegate();
    } // for test

    public List<ResourceSet> getSelectionSets() {
        return selectionSets;
    }

    @Override
    public void init() {
        selectionPresenter.init();

        selectionDropPresenter.init();

        DefaultResourceSet resources = new DefaultResourceSet();
        resources.setLabel("add selection");
        selectionDropPresenter.addResourceSet(resources);
    }

    @Override
    public void removeSelectionSet(ResourceSet selectionSet) {
        assert selectionSet != null;
        this.selectionSets.remove(selectionSet);
        selectionPresenter.removeResourceSet(selectionSet);
    }

    @Override
    public void restore(Memento state, ResourceSetAccessor accessor) {
        int selectionSetCount = (Integer) state
                .getValue(MEMENTO_SELECTION_SET_COUNT);
        for (int i = 0; i < selectionSetCount; i++) {
            addSelectionSet(restoreResourceSet(state, accessor,
                    MEMENTO_SELECTION_SET_PREFIX + i));
        }

        if (state.getValue(MEMENTO_SELECTION) != null) {
            setSelection(restoreResourceSet(state, accessor, MEMENTO_SELECTION));
        }
    }

    private ResourceSet restoreResourceSet(Memento state,
            ResourceSetAccessor accessor, String key) {
        int id = (Integer) state.getValue(key);
        ResourceSet resourceSet = accessor.getResourceSet(id);
        return resourceSet;
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();

        memento.setValue(MEMENTO_SELECTION_SET_COUNT, selectionSets.size());

        for (int i = 0; i < selectionSets.size(); i++) {
            storeResourceSet(resourceSetCollector, memento,
                    MEMENTO_SELECTION_SET_PREFIX + i, selectionSets.get(i));
        }

        if (selection.hasDelegate()) {
            storeResourceSet(resourceSetCollector, memento, MEMENTO_SELECTION,
                    getSelection());
        }

        return memento;
    }

    @Override
    public void setSelection(ResourceSet newSelectionModel) {
        assert newSelectionModel == null
                || selectionSets.contains(newSelectionModel);

        selection.setDelegate(newSelectionModel);
        selectionPresenter.setSelectedResourceSet(newSelectionModel);
    }

    private void storeResourceSet(ResourceSetCollector persistanceManager,
            Memento memento, String key, ResourceSet resources) {
        memento.setValue(key, persistanceManager.storeResourceSet(resources));
    }

    // TODO this means that we need a wrapper around resource set
    // to make this happen
    @Override
    public void switchSelection(ResourceSet resources) {
        // XXX HACK TODO cleanup --> we create selections when stuff
        // gets selected...
        if (!selection.hasDelegate()) {
            ResourceSet set = resourceSetFactory.createResourceSet();
            set.setLabel(selectionModelLabelFactory.nextLabel());
            addSelectionSet(set);
            setSelection(set);
        }

        assert selection != null;

        getSelection().switchContainment(resources);
    }
}
