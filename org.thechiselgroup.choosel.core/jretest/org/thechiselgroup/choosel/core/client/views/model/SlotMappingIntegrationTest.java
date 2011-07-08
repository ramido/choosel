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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.resolvers.DefaultViewItemResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueViewItemResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ManagedViewItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

// TODO think about test name schema - unit, module, system, integration test etc
public class SlotMappingIntegrationTest {

    private SlotMappingInitializer slotMappingInitializer;

    private DefaultViewItemResolverFactoryProvider resolverProvider = new DefaultViewItemResolverFactoryProvider();

    @Mock
    private VisualItemBehavior viewItemBehavior;

    private Logger logger = Logger.getLogger("");

    private static final String resolverId1 = "resolver-id-1";

    private static final String property1 = "property1";

    private static final String resolverId2 = "resolver-id-2";

    private static final String property2 = "property2";

    private final String property3 = "property3";

    private ResourceGrouping resourceGrouping;

    DefaultViewModelTestHelper helper;

    /**
     * <h3>Changing Property Select</h3>
     * 
     * Resolvers: 1 resolver, able to resolve a Number based on PropertyName<br>
     * Slots: 1 Number slot <br>
     * Grouping: each resource is grouped alone <br>
     * Data: [(x:1, y:2)]<br>
     * 
     * Expected Output: [1] => [2]<br>
     * 
     * <p>
     * We create the view with the resolver and slots as above. The resolver is
     * automatically set to resolve based on property name “x”, and so 2
     * ViewItems are created each with value = 1. We then change the property
     * that is selected to property “y”. The ViewItems are replaced with 2 new
     * ViewItems each with value = 2.
     * </p>
     */
    @Test
    public void changeSelectedPropertyChangesViewItems() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        ViewItemValueResolver resolver = new ManagedViewItemValueResolverDecorator(
                resolverId1, new FirstResourcePropertyResolver(property1,
                        DataType.NUMBER));
        initialSlotMapping.put(requiredSlots[0], resolver);

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource = TestResourceSetFactory.createResource(1);
        resource.putValue(property1, 1);
        resource.putValue(property2, 2);
        resourceGrouping.getResourceSet().add(resource);

        ViewItemValueResolver resolver2 = new ManagedViewItemValueResolverDecorator(
                resolverId1, new FirstResourcePropertyResolver(property2,
                        DataType.NUMBER));
        model.setResolver(requiredSlots[0], resolver2);

        LightweightCollection<VisualItem> viewItems = model.getViewItems();

        assertEquals(viewItems.size(), 1);
        Iterator<VisualItem> iterator = viewItems.iterator();
        VisualItem first = iterator.next();
        assertEquals(first.getValue(requiredSlots[0]), 2);
    }

    /**
     * <h3>Changing Data changes Resolver and Resolution</h3>
     * 
     * Resolvers: 2 Resolvers, resolver1: first resources => 1 resolver2: second
     * resources => 2 (specified by initializer) <br>
     * Slots: one slot, val1 => Number <br>
     * Grouping: each resource is grouped alone <br>
     * Data: 2 resources<br>
     * 
     * Expected Output: [1] with first one => [] after removal => [2] after new
     * add<br>
     * 
     * <p>
     * We create the view with the resolver and slots as above. We then add the
     * first resource above. We then set the resolver to a resolver which can
     * not resolve the last resource, and the first two are resolved to 1. We
     * then remove the data, and the resolver should not change. We then add new
     * data, which the current resolver can not resolve, and it should
     * automatically switch to the resolver specified by the initializer, which
     * should resolve both to 2.
     * </p>
     */
    // XXX this is no longer the required functionality. Because we made the
    // decision to only reset unconfigured slots, and not invalid ones, and
    // since that slot has in fact been configured.
    @Ignore("No longer specified funcionality")
    @Test
    public void changeToUnresolvableDataChangesToResolverSpecifiedByInitializer() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        // TODO factories should be mocked, they arent even the same type hah
        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId2));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FirstResourcePropertyResolver(property1,
                                DataType.NUMBER)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource1 = TestResourceSetFactory.createResource(1);
        resource1.putValue(property1, 1);
        resource1.putValue(property2, 2);

        resourceGrouping.getResourceSet().add(resource1);

        /* Should have 1 View Item with Value 1 */
        model.setResolver(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId2,
                        new FirstResourcePropertyResolver(property2,
                                DataType.NUMBER)));

        /* Should have 1 View Item with Value 2 */
        LightweightCollection<VisualItem> viewItems = model.getViewItems();
        assertTrue(viewItems.size() == 1);
        VisualItem item = viewItems.iterator().next();
        assertEquals(2, item.getValue(requiredSlots[0]));

        resourceGrouping.setResourceSet(new DefaultResourceSet());

        /* Now should be empty View Items */
        viewItems = model.getViewItems();
        assertTrue(viewItems.size() == 0);

        Resource resource2 = createResource(2);
        resource2.putValue(property1, 1);

        resourceGrouping.getResourceSet().add(resource2);

        /* Now the current resolver should switch and resolve to 1 */
        viewItems = model.getViewItems();
        assertTrue(viewItems.size() == 1);
        item = viewItems.iterator().next();
        assertEquals(1, item.getValue(requiredSlots[0]));
    }

    @Test
    public void changingResolverManuallyChangesResolution() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId2));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FirstResourcePropertyResolver(property1,
                                DataType.NUMBER)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource1 = createResource(1);
        resource1.putValue(property1, 1);
        resource1.putValue(property2, 2);

        resourceGrouping.getResourceSet().add(resource1);

        /* Should have 1 View Item with Value 1 */
        model.setResolver(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId2,
                        new FirstResourcePropertyResolver(property2,
                                DataType.NUMBER)));

        /* Should have 1 View Item with Value 2 */
        LightweightCollection<VisualItem> viewItems = model.getViewItems();
        assertTrue(viewItems.size() == 1);
        VisualItem item = viewItems.iterator().next();
        assertEquals(2, item.getValue(requiredSlots[0]));
    }

    private DefaultViewModel createViewModel(ResourceGrouping resourceGrouping) {

        DefaultViewModel model = new DefaultViewModel(
                helper.getViewContentDisplay(), new DefaultResourceSet(),
                new DefaultResourceSet(), viewItemBehavior, resourceGrouping,
                logger);
        new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, model, model);
        return model;
    }

    /**
     * <h3>Scenario 6: Failed Resolution on Multiple Slots</h3>
     * 
     * Resolvers: 1 resolver, able to resolve Number on all resources. 1
     * resolver not able to resolve Text. <br>
     * Slots: 1 Text slot, 1 Number slot <br>
     * Grouping: each resource is grouped alone <br>
     * Data: [x,x]<br>
     * 
     * Expected Output: error thrown<br>
     * 
     * <p>
     * We create the view with the resolver and slots as above. We then add the
     * data, and because there are no applicable resolvers for the Text slot, an
     * error should be thrown to show that the data cannot be shown.
     * </p>
     */
    @Test
    public void errorStateSetWhenResolversCannotResolveOneOfTwoSlots()
            throws Throwable {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER,
                DataType.TEXT);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.TEXT, resolverId2));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FirstResourcePropertyResolver(property1,
                                DataType.NUMBER)));
        initialSlotMapping.put(requiredSlots[1],
                new ManagedViewItemValueResolverDecorator(resolverId2,
                        new FirstResourcePropertyResolver(property3,
                                DataType.TEXT)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);
        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel viewModel = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource = TestResourceSetFactory.createResource(1);
        resource.putValue(property1, 1);
        // unresolvable by either resolvers
        resource.putValue(property2, "a");
        resourceGrouping.getResourceSet().add(resource);

        assertTrue(viewModel.hasErrors());
        assertThat(viewModel.getSlotsWithErrors(),
                containsExactly(requiredSlots[1]));
        // assertThat()

    }

    /**
     * <h3>Scenario 5: Failed Resolution of Resources on 1 Slot</h3>
     * 
     * Resolvers: 1 resolver, not applicable to anything <br>
     * Slots: 1 Text slot<br>
     * Grouping: each resource is grouped alone<br>
     * Data: [1]<br>
     * 
     * Expected Output: error thrown<br>
     * 
     * <p>
     * We create the view with the resolver and slots as above. We then add the
     * data, and because there are no applicable resolvers, an error should be
     * thrown to show that the data cannot be shown.
     * </p>
     * 
     */
    public void errorStateSetWhenResolvingOneUnresolvableResource()
            throws Throwable {

        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FirstResourcePropertyResolver(property1,
                                DataType.NUMBER)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel viewModel = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource = TestResourceSetFactory.createResource(1);

        resourceGrouping.getResourceSet().add(resource);

        assertTrue(viewModel.hasErrors());
        assertThat(viewModel.getSlotsWithErrors(),
                containsExactly(requiredSlots[0]));
    }

    /**
     * <h3>Automatically Change Resolver When Selected Property Not Applicable</h3>
     * 
     * Resolvers: 1 resolver, which resolves a Number based on a property, 1
     * default resolver that resolves to 2<br>
     * Slots: 1 Number slot <br>
     * Grouping: each resource is grouped alone <br>
     * Data: [(x:1, y:2),(y:2)]<br>
     * 
     * Expected Output: [1] => [2,2]<br>
     * 
     * <p>
     * We create the view with the single slot and set up the resolver to select
     * on property x. We then add the first resource, and the View will create 1
     * ViewItem with value 1. We then add the second resource, and the resolver
     * will automatically change to resolve on property y, and the View will
     * have 2 ViewItems in it each with value 2.
     * </p>
     */
    /*
     * XXX The property of the resolver gets switched in this test case. Since
     * resolvers are immutable, this needs to change - it should be impossible
     * to switch to an invalid property. Instead, we should test that the UI
     * does not provide the possibility to switch to an invalid resolver.
     */
    // TODO fix issue 156
    @Ignore("issue 156 need to fix")
    @Test
    public void reinitialzeResolverWhenPropertySelectedIsNotValid() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        ViewItemValueResolver resolver = new ManagedViewItemValueResolverDecorator(
                resolverId1, new FirstResourcePropertyResolver(property1,
                        DataType.NUMBER));
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FixedValueResolver(1, DataType.NUMBER)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);
        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        slotMappingConfiguration.setResolver(requiredSlots[0], resolver);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource = TestResourceSetFactory.createResource(1);
        resource.putValue(property1, 2);
        resourceGrouping.getResourceSet().add(resource);

        // resolver.setProperty(property2);

        LightweightCollection<VisualItem> viewItems = model.getViewItems();

        assertEquals(viewItems.size(), 1);
        Iterator<VisualItem> iterator = viewItems.iterator();
        VisualItem first = iterator.next();
        assertEquals(1, first.getValue(requiredSlots[0]));
    }

    @Test
    public void removingAllResourceDoesNotChangeResolver() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId2));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        ViewItemValueResolver resolver = new ManagedViewItemValueResolverDecorator(
                resolverId1, new FirstResourcePropertyResolver(property1,
                        DataType.NUMBER));
        initialSlotMapping.put(requiredSlots[0], resolver);

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource1 = TestResourceSetFactory.createResource(1);
        resource1.putValue(property1, 1);
        resource1.putValue(property2, 2);

        resourceGrouping.getResourceSet().add(resource1);
        /* Should have 1 View Item with Value 1 */
        resourceGrouping.setResourceSet(new DefaultResourceSet());

        assertEquals(resolver, model.getResolver(requiredSlots[0]));
    }

    @Test
    public void removingAllResourcesResultsInNoViewItems() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId2));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        ViewItemValueResolver resolver = new ManagedViewItemValueResolverDecorator(
                resolverId1, new FirstResourcePropertyResolver(property1,
                        DataType.NUMBER));
        initialSlotMapping.put(requiredSlots[0], resolver);

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource1 = TestResourceSetFactory.createResource(1);
        resource1.putValue(property1, 1);
        resource1.putValue(property2, 2);

        resourceGrouping.getResourceSet().add(resource1);
        /* Should have 1 View Item with Value 1 */
        resourceGrouping.setResourceSet(new DefaultResourceSet());

        assertEquals(model.getViewItems().size(), 0);
    }

    /**
     * <h3>Scenario 2: Simple Resolution of Number Property (Tested)</h3>
     * 
     * Resolvers: 1 resolver: can only resolve items with a Number field, just
     * takes the slot values<br>
     * Slots: only 1, val1 => Number<br>
     * Grouping: each Resource is grouped by itself <br>
     * Data: 1 Resource each with Number Slot [2]<br>
     * 
     * Expected Outcome: [2]<br>
     * 
     * <p>
     * We create a simple view that has one resolver and one slot. The slot and
     * resolver are as above. We then initialize the View and add the data item.
     * Each of those resources should be converted into a ViewItem, with a slot
     * value = 2.
     * </p>
     */
    @Test
    public void resolverWithNumberPropertyResolver() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FirstResourcePropertyResolver(property1,
                                DataType.NUMBER)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource = TestResourceSetFactory.createResource(1);
        resource.putValue(property1, 1);
        resourceGrouping.getResourceSet().add(resource);

        LightweightCollection<VisualItem> viewItems = model.getViewItems();

        assertEquals(viewItems.size(), 1);

        Iterator<VisualItem> iterator = viewItems.iterator();
        VisualItem first = iterator.next();
        assertEquals(first.getValue(requiredSlots[0]), 1);

    }

    /**
     * <h3>Scenario 3: Resolution of 2 Fields of different Type (Tested)</h3>
     * 
     * Resolvers: 2 resolvers, one for a NumberSlot and one for a TextSlot<br>
     * Slots: val1 => Number, val2 => Text <br>
     * Grouping: each Resource grouped on its own <br>
     * Data: 1 resource [ (1,”a”)]<br>
     * 
     * Expected Outcome: [ (1,”a”)]<br>
     * 
     * <p>
     * We create the view with the resolver and slots as above. We then add the
     * 2 resources above. Each of the resources result in a similar ViewItem
     * being created.
     * </p>
     */
    @Test
    public void resolveTwoFieldsWithTwoResolvers() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER,
                DataType.TEXT);

        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.TEXT, resolverId2));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FirstResourcePropertyResolver(property1,
                                DataType.NUMBER)));
        initialSlotMapping.put(requiredSlots[1],
                new ManagedViewItemValueResolverDecorator(resolverId2,
                        new FirstResourcePropertyResolver(property2,
                                DataType.TEXT)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                requiredSlots);
        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        Resource resource1 = TestResourceSetFactory.createResource(1);
        resource1.putValue(property1, 1);
        resource1.putValue(property2, "a");

        resourceGrouping.getResourceSet().add(resource1);

        /* Test results */
        LightweightCollection<VisualItem> viewItems = model.getViewItems();
        assertTrue(viewItems.size() == 1);

        Iterator<VisualItem> iterator = viewItems.iterator();
        VisualItem first = iterator.next();
        assertEquals(first.getValue(requiredSlots[0]), 1);
        assertEquals(first.getValue(requiredSlots[1]), "a");

    }

    /**
     * <h3>Scenario 1: Simple Resolution (Tested)</h3>
     * 
     * Resolvers: only one, can resolve anything (Fixed to 1)<br>
     * Slots: only one, val1 => Number<br>
     * Grouping: each Resource is grouped by itself <br>
     * Data: 1 Resource, any data allowed<br>
     * 
     * Expected Outcome: [1]<br>
     * 
     * <p>
     * We create a simple view that has one resolver and one slot. The slot is a
     * Number and the resolver is a FixedResolver, so it can resolve any
     * ResourceSet . We then initialize the View and add the data items. Each of
     * those resources should be converted into a ViewItem, with a slot value of
     * 1.
     * </p>
     */
    @Test
    public void resolveWithOneFixedResolver() {
        /* set up the slots */
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        /* set up the provider to return the correct resolvers */
        resolverProvider.registerFactory(new FixedValueViewItemResolverFactory(
                1, DataType.NUMBER, resolverId1));

        /* define and create initializer */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        initialSlotMapping.put(requiredSlots[0],
                new ManagedViewItemValueResolverDecorator(resolverId1,
                        new FixedValueResolver(1, DataType.NUMBER)));

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        /*
         * create the ViewModel, as well as initialize the
         * slotMappingConfiguration
         */

        DefaultViewModel model = createViewModel(resourceGrouping);

        resourceGrouping.setResourceSet(new DefaultResourceSet());
        resourceGrouping.getResourceSet().add(
                TestResourceSetFactory.createResource(1));

        /* Test results */
        LightweightCollection<VisualItem> viewItems = model.getViewItems();
        assertTrue(viewItems.size() == 1);

        VisualItem item = viewItems.iterator().next();
        assertEquals(1, item.getValue(requiredSlots[0]));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultViewModelTestHelper();

        ResourceByUriMultiCategorizer uriCategorizer = new ResourceByUriMultiCategorizer();
        resourceGrouping = new ResourceGrouping(uriCategorizer,
                new DefaultResourceSetFactory());
    }

    @Test
    public void unconfiguredSlotsGetConfugredAutomaticallyWhenResourcesAdded() {
        Slot[] requiredSlots = helper.createSlots(DataType.NUMBER);
        when(helper.getViewContentDisplay().getSlots()).thenReturn(
                requiredSlots);

        resolverProvider
                .registerFactory(new FirstResourcePropertyResolverFactory(
                        DataType.NUMBER, resolverId1));

        /* define initialization mapping */
        final Map<Slot, ViewItemValueResolver> initialSlotMapping = new HashMap<Slot, ViewItemValueResolver>();
        ViewItemValueResolver resolver = new ManagedViewItemValueResolverDecorator(
                resolverId1, new FirstResourcePropertyResolver(property1,
                        DataType.NUMBER));
        initialSlotMapping.put(requiredSlots[0], resolver);

        slotMappingInitializer = new TestSlotMappingInitializer(
                initialSlotMapping);

        ResourceMultiCategorizer multiCategorizer = new ResourceByUriMultiCategorizer();
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                multiCategorizer, new DefaultResourceSetFactory());

        DefaultViewModel model = createViewModel(resourceGrouping);
        resourceGrouping.setResourceSet(new DefaultResourceSet());

        Resource resource = TestResourceSetFactory.createResource(1);
        resource.putValue(property1, 1);
        resourceGrouping.getResourceSet().add(resource);

        LightweightCollection<VisualItem> viewItems = model.getViewItems();

        assertEquals(1, viewItems.size());
        Iterator<VisualItem> iterator = viewItems.iterator();
        VisualItem first = iterator.next();
        assertEquals(first.getValue(requiredSlots[0]), 1);
    }

}
