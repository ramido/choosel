package org.thechiselgroup.choosel.core.client.views.resolvers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    private static final String FACT_ID_1 = "FID_1";

    private static final String FACT_ID_2 = "FID_2";

    private static final String FACT_ID_3 = "FID_3";

    private static final String FACT_ID_4 = "FID_4";

    private static final String FACT_ID_5 = "FID_5";

    @Mock
    public ViewItemValueResolver resolver1;

    @Mock
    public ViewItemValueResolver resolver2;

    @Mock
    public ViewItemValueResolver resolver3;

    @Mock
    public ViewItem viewItem1;

    public SlotResolverUIModel underTest;

    @Mock
    public Slot slot;

    @Mock
    public SlotMappingChangedHandler handler;

    @Mock
    public ViewItemValueResolverFactoryProvider provider;

    @Mock
    public ViewItemValueResolverFactory factory1;

    @Mock
    public ViewItemValueResolverFactory factory2;

    @Mock
    public ViewItemValueResolverFactory factory3;

    public SlotMappingChangedEvent captureEvent() {
        ArgumentCaptor<SlotMappingChangedEvent> eventCaptor = ArgumentCaptor
                .forClass(SlotMappingChangedEvent.class);
        verify(handler, times(1)).onResourceCategoriesChanged(
                eventCaptor.capture());
        SlotMappingChangedEvent event = eventCaptor.getValue();

        return event;
    }

    /**
     * creates a mocked view item that can be identified by the id that was
     * passed in in it's creation
     */
    public ViewItem createViewItem(String id) {
        ViewItem viewItem = mock(ViewItem.class);
        when(viewItem.getViewItemID()).thenReturn(id);
        return viewItem;
    }

    public LightweightList<ViewItem> createViewItemList(String... ids) {
        LightweightList<ViewItem> list = CollectionFactory
                .createLightweightList();

        for (String id : ids) {
            list.add(createViewItem(id));
        }
        return list;
    }

    private void linkFactoryToResolver(ViewItemValueResolverFactory factory,
            ViewItemValueResolver resolver, String id) {
        when(factory.create()).thenReturn(resolver);
        when(factory.getId()).thenReturn(id);
        when(resolver.getResolverId()).thenReturn(id);
    }

    private void setFactoriesAcceptedViewItems(
            ViewItemValueResolverFactory factory,
            LightweightList<ViewItem> viewItems) {
        when(factory.isApplicable(any(Slot.class), eq(viewItems))).thenReturn(
                true);
    }

    @Test
    public void setNewCurrentResolverFiresEvent() {
        underTest.addEventHandler(handler);
        underTest.setCurrentResolver(resolver1);

        SlotMappingChangedEvent event = captureEvent();
        assertEquals(slot, event.getSlot());
    }

    private void setProvidersFactories(
            ViewItemValueResolverFactory... factories) {
        LightweightList<ViewItemValueResolverFactory> list = CollectionFactory
                .createLightweightList();

        for (ViewItemValueResolverFactory factory : factories) {
            list.add(factory);
        }
        when(provider.getResolverFactories()).thenReturn(list);
    }

    @Test
    public void setSameResolverDoesNotFireEvent() {
        ViewItemValueResolverFactory factory4 = mock(ViewItemValueResolverFactory.class);
        ViewItemValueResolver resolver4 = mock(ViewItemValueResolver.class);
        setToAlwaysApplicableFactory(factory4);
        linkFactoryToResolver(factory4, resolver4, FACT_ID_4);

        setProvidersFactories(factory4);
        underTest.updateAllowableFactories(createViewItemList("ID_1"));
        underTest.addEventHandler(handler);
        underTest.setCurrentResolver(resolver4);

        verify(handler, times(0)).onResourceCategoriesChanged(
                any(SlotMappingChangedEvent.class));
    }

    private void setToAlwaysApplicableFactory(
            ViewItemValueResolverFactory factory) {
        @SuppressWarnings("unchecked")
        LightweightList<ViewItem> anylist = (LightweightList<ViewItem>) Mockito
                .any();
        when(factory.isApplicable(any(Slot.class), anylist)).thenReturn(true);
    }

    @Test(expected = NoAllowableResolverException.class)
    public void setToNoAllowableResolverFactoriesThrowsNoAllowableResolverException() {
        setToNonApplicableFactory(factory1);
        setToNonApplicableFactory(factory2);
        setToNonApplicableFactory(factory3);

        underTest.updateAllowableFactories(createViewItemList(ID_1));
        assertTrue(underTest.getResolverFactories().isEmpty());
    }

    private void setToNonApplicableFactory(ViewItemValueResolverFactory factory) {
        @SuppressWarnings("unchecked")
        LightweightList<ViewItem> anylist = (LightweightList<ViewItem>) Mockito
                .any();
        when(factory.isApplicable(any(Slot.class), anylist)).thenReturn(false);
    }

    @Test(expected = InvalidResolverException.class)
    public void setUnallowableResolverThrowsInvalidResolverException() {
        setToNonApplicableFactory(factory1);

        LightweightList<ViewItem> list = createViewItemList(ID_1);
        underTest.updateAllowableFactories(list);
        underTest.setCurrentResolver(resolver1);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setUpFactories();
        setProvidersFactories(factory1, factory2, factory3);
        underTest = new SlotResolverUIModel(slot, provider);
    }

    private void setUpFactories() {
        linkFactoryToResolver(factory1, resolver1, FACT_ID_1);
        linkFactoryToResolver(factory2, resolver2, FACT_ID_2);
        linkFactoryToResolver(factory3, resolver3, FACT_ID_3);

        setToAlwaysApplicableFactory(factory1);
        setToAlwaysApplicableFactory(factory2);
        setToAlwaysApplicableFactory(factory3);
    }

    @Test
    public void updateAllowableResolverFactoriesMixed() {
        setToNonApplicableFactory(factory1);
        setToAlwaysApplicableFactory(factory2);
        setToAlwaysApplicableFactory(factory3);

        LightweightList<ViewItem> list = createViewItemList(ID_1);
        underTest.updateAllowableFactories(list);

        assertThat(underTest.getResolverFactories(),
                containsExactly(factory2, factory3));
    }

    @Test
    public void updatedFactoriesThatMatchNotAllViewItemsAreNotAdded() {
        LightweightList<ViewItem> viewItems = createViewItemList("ID_1",
                "ID_2", "ID_3");

        ViewItemValueResolverFactory factory4 = mock(ViewItemValueResolverFactory.class);
        ViewItemValueResolver resolver4 = mock(ViewItemValueResolver.class);
        linkFactoryToResolver(factory4, resolver4, FACT_ID_4);

        ViewItemValueResolverFactory factory5 = mock(ViewItemValueResolverFactory.class);
        ViewItemValueResolver resolver5 = mock(ViewItemValueResolver.class);
        linkFactoryToResolver(factory5, resolver5, FACT_ID_5);

        LightweightList<ViewItem> acceptedViewItems = CollectionFactory
                .createLightweightList();
        acceptedViewItems.add(viewItems.get(0));
        acceptedViewItems.add(viewItems.get(2));
        setFactoriesAcceptedViewItems(factory4, acceptedViewItems);
        setFactoriesAcceptedViewItems(factory5, viewItems);

        setProvidersFactories(factory4, factory5);
        underTest.updateAllowableFactories(viewItems);
        assertThat(underTest.getResolverFactories(), containsExactly(factory5));
    }

    @Test
    public void updateResolverFactoriesSetsAllResolverFactories() {
        LightweightList<ViewItem> list = createViewItemList(ID_1);
        underTest.updateAllowableFactories(list);

        assertThat(underTest.getResolverFactories(),
                containsExactly(factory1, factory2, factory3));
    }

    @Test
    public void updateResolversChangeCurrentResolverFiresEvent() {
        setToNonApplicableFactory(factory1);
        setToNonApplicableFactory(factory2);
        setToNonApplicableFactory(factory3);

        ViewItemValueResolverFactory factory4 = mock(ViewItemValueResolverFactory.class);
        ViewItemValueResolver resolver4 = mock(ViewItemValueResolver.class);
        setToAlwaysApplicableFactory(factory4);
        linkFactoryToResolver(factory4, resolver4, FACT_ID_4);

        setProvidersFactories(factory1, factory2, factory3, factory4);

        underTest.addEventHandler(handler);
        underTest.updateAllowableFactories(createViewItemList(ID_1));

        SlotMappingChangedEvent event = captureEvent();
        assertEquals(slot, event.getSlot());
    }

    @Test
    public void updateResolversDontCurrentResolverDoesNotFiresEvent() {
        ViewItemValueResolverFactory factory4 = mock(ViewItemValueResolverFactory.class);
        ViewItemValueResolver resolver4 = mock(ViewItemValueResolver.class);
        setToAlwaysApplicableFactory(factory4);
        linkFactoryToResolver(factory4, resolver4, FACT_ID_4);

        setProvidersFactories(factory1, factory2, factory3, factory4);

        underTest.addEventHandler(handler);
        underTest.updateAllowableFactories(createViewItemList(ID_1));
    }

    @Test
    public void updateResolversToOneResolverSetsCurrentResolver() {
        setToNonApplicableFactory(factory1);
        setToNonApplicableFactory(factory2);
        setToAlwaysApplicableFactory(factory3);

        underTest.updateAllowableFactories(createViewItemList(ID_1));
        assertThat(underTest.getCurrentResolver(), equalTo(resolver3));

        verify(handler, times(0)).onResourceCategoriesChanged(
                any(SlotMappingChangedEvent.class));
    }
}
