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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotResolverUIModel.InvalidResolverException;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotResolverUIModel.NoAllowableResolverException;

public class SlotResolverUIModelTest {

    private static final String ID_1 = "ID_1";

    private static final String ID_2 = "ID_2";

    private static final String FACT_ID_1 = "FID_1";

    private static final String FACT_ID_2 = "FID_2";

    @Mock
    private ViewItemValueResolver resolver1;

    @Mock
    private ViewItemValueResolver resolver2;

    private SlotResolverUIModel underTest;

    @Mock
    private Slot slot;

    @Mock
    private SlotMappingChangedHandler handler;

    @Mock
    private ViewItemValueResolverFactoryProvider provider;

    @Mock
    private ViewItemValueResolverFactory factory1;

    @Mock
    private ViewItemValueResolverFactory factory2;

    public SlotMappingChangedEvent captureEvent() {
        ArgumentCaptor<SlotMappingChangedEvent> eventCaptor = ArgumentCaptor
                .forClass(SlotMappingChangedEvent.class);
        verify(handler, times(1)).onResourceCategoriesChanged(
                eventCaptor.capture());
        return eventCaptor.getValue();
    }

    @Test
    public void changeCurrentResolverFiresEvent() {
        setFactoryIsApplicable(factory1, true);
        setFactoryIsApplicable(factory2, true);

        underTest.updateAllowableFactories(mockViewItems());

        underTest.setCurrentResolver(resolver2);
        underTest.addEventHandler(handler);
        underTest.setCurrentResolver(resolver1);

        SlotMappingChangedEvent event = captureEvent();
        assertEquals(slot, event.getSlot());
    }

    private void linkFactoryToResolver(ViewItemValueResolverFactory factory,
            ViewItemValueResolver resolver, String id) {

        when(factory.create()).thenReturn(resolver);
        when(factory.getId()).thenReturn(id);
        when(resolver.getResolverId()).thenReturn(id);
    }

    /**
     * creates a mocked view item that can be identified by the id that was
     * passed in in it's creation
     */
    private ViewItem mockViewItem(final String id) {
        ViewItem viewItem = mock(ViewItem.class);
        when(viewItem.getViewItemID()).thenReturn(id);
        return viewItem;
    }

    private LightweightList<ViewItem> mockViewItems(String... ids) {
        LightweightList<ViewItem> list = CollectionFactory
                .createLightweightList();
        for (String id : ids) {
            list.add(mockViewItem(id));
        }
        return list;
    }

    private void setAllowableViewItemsForFactory(
            ViewItemValueResolverFactory factory,
            LightweightList<ViewItem> viewItems) {

        when(
                factory.isApplicable(
                        any(Slot.class),
                        (LightweightList<ViewItem>) argThat(containsExactly(viewItems))))
                .thenReturn(true);
    }

    @SuppressWarnings("unchecked")
    private void setFactoryIsApplicable(ViewItemValueResolverFactory factory,
            boolean isApplicable) {

        when(
                factory.isApplicable(any(Slot.class),
                        (LightweightList<ViewItem>) any())).thenReturn(
                isApplicable);
    }

    private void setProviderFactories(ViewItemValueResolverFactory... factories) {
        LightweightList<ViewItemValueResolverFactory> list = CollectionFactory
                .createLightweightList();
        for (ViewItemValueResolverFactory factory : factories) {
            list.add(factory);
        }
        when(provider.getResolverFactories()).thenReturn(list);
    }

    @Test
    public void setSameResolverDoesNotFireEvent() {
        setFactoryIsApplicable(factory1, true);
        setFactoryIsApplicable(factory2, true);

        underTest.updateAllowableFactories(mockViewItems());

        underTest.setCurrentResolver(resolver1);
        underTest.addEventHandler(handler);
        underTest.setCurrentResolver(resolver1);

        verify(handler, times(0)).onResourceCategoriesChanged(
                any(SlotMappingChangedEvent.class));
    }

    @Test(expected = NoAllowableResolverException.class)
    public void setToNoAllowableResolverFactoriesThrowsNoAllowableResolverException() {
        setFactoryIsApplicable(factory1, false);
        setFactoryIsApplicable(factory2, false);

        underTest.updateAllowableFactories(mockViewItems(ID_1));
    }

    @Test(expected = InvalidResolverException.class)
    public void setUnallowableResolverThrowsInvalidResolverException() {
        setFactoryIsApplicable(factory1, true);
        setFactoryIsApplicable(factory2, true);

        underTest.setCurrentResolver(resolver2);

        setFactoryIsApplicable(factory1, false);

        underTest.updateAllowableFactories(mockViewItems(ID_1));
        underTest.setCurrentResolver(resolver1);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new SlotResolverUIModel(slot, provider);

        linkFactoryToResolver(factory1, resolver1, FACT_ID_1);
        linkFactoryToResolver(factory2, resolver2, FACT_ID_2);

        setProviderFactories(factory1, factory2);
    }

    @Test
    public void updateAllowableFactoriesForTheFirstTimesSetsResolver() {
        setFactoryIsApplicable(factory1, false);
        setFactoryIsApplicable(factory2, true);

        underTest.updateAllowableFactories(mockViewItems(ID_1));

        assertThat(underTest.getCurrentResolver(), equalTo(resolver2));
        verify(handler, times(0)).onResourceCategoriesChanged(
                any(SlotMappingChangedEvent.class));
    }

    @Test
    public void updateAllowableResolverFactoriesMixed() {
        setFactoryIsApplicable(factory1, false);
        setFactoryIsApplicable(factory2, true);

        underTest.updateAllowableFactories(mockViewItems(ID_1));

        assertThat(underTest.getResolverFactories(), containsExactly(factory2));
    }

    @Test
    public void updatedFactoriesThatMatchNotAllViewItemsAreNotAdded() {
        linkFactoryToResolver(factory1, resolver1, FACT_ID_1);
        linkFactoryToResolver(factory2, resolver2, FACT_ID_2);

        /*
         * NOTE: we cannot stub equals() and thus have to rely on instance
         * tests.
         */
        LightweightList<ViewItem> allowableViewItems = mockViewItems(ID_1, ID_2);

        setAllowableViewItemsForFactory(factory1, mockViewItems(ID_1));
        setAllowableViewItemsForFactory(factory2, allowableViewItems);

        underTest.updateAllowableFactories(allowableViewItems);

        assertThat(underTest.getResolverFactories(), containsExactly(factory2));
    }

    @Test
    public void updateResolverFactoriesSetsAllResolverFactories() {
        setFactoryIsApplicable(factory1, true);
        setFactoryIsApplicable(factory2, true);

        underTest.updateAllowableFactories(mockViewItems(ID_1));

        assertThat(underTest.getResolverFactories(),
                containsExactly(factory1, factory2));
    }

    @Test
    public void updateResolversThatChangesCurrentResolverFiresEvent() {
        setFactoryIsApplicable(factory1, true);
        setFactoryIsApplicable(factory2, true);

        underTest.updateAllowableFactories(mockViewItems(ID_1));
        underTest.setCurrentResolver(resolver1);

        setFactoryIsApplicable(factory1, false);

        underTest.addEventHandler(handler);
        underTest.updateAllowableFactories(mockViewItems(ID_1));

        SlotMappingChangedEvent event = captureEvent();
        assertEquals(slot, event.getSlot());
    }

    @Test
    public void updateResolversThatDontChangeCurrentResolverDoesNotFiresEvent() {
        setFactoryIsApplicable(factory1, true);
        setProviderFactories(factory1);

        underTest.updateAllowableFactories(mockViewItems(ID_1));
        underTest.addEventHandler(handler);
        underTest.updateAllowableFactories(mockViewItems(ID_1));

        verify(handler, times(0)).onResourceCategoriesChanged(
                any(SlotMappingChangedEvent.class));
    }
}
