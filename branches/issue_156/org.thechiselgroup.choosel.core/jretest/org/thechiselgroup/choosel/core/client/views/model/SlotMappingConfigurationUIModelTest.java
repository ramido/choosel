package org.thechiselgroup.choosel.core.client.views.model;

import org.junit.Before;
import org.mockito.Mock;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

public class SlotMappingConfigurationUIModelTest {

    private SlotMappingConfigurationUIModel underTest;

    @Mock
    private ViewItemValueResolverFactoryProvider resolverProvider;

    @Mock
    private SlotMappingInitializer slotMappingInitializer;

    @Mock
    private ViewModel viewModel;

    @Before
    public void setUp() {

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel);

    }

}
