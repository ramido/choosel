package org.thechiselgroup.choosel.client.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResource;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

public class ResourceEventsForwarderTest {

    private ResourceSet source;

    @Mock
    private ResourceContainer target;

    @Test
    public void addAllResourcesOnEventFired() {
        ResourceEventsForwarder underTest = new ResourceEventsForwarder(source,
                target);

        underTest.init();

        source.addAll(createResources(1, 2));

        ArgumentCaptor<Iterable> argument = ArgumentCaptor
                .forClass(Iterable.class);

        verify(target, times(1)).addAll(argument.capture());

        List<Resource> result = CollectionUtils.toList(argument.getValue());

        assertEquals(2, result.size());
        assertEquals(true, result.contains(createResource(1)));
        assertEquals(true, result.contains(createResource(2)));

    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new DefaultResourceSet();
    }

}
