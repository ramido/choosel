package org.thechiselgroup.choosel.client.resources;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

public interface ReadableResourceSet extends Iterable<Resource> {

    HandlerRegistration addEventHandler(ResourcesAddedEventHandler handler);

    HandlerRegistration addEventHandler(ResourcesRemovedEventHandler handler);

    boolean contains(Resource resource);

    boolean containsAll(Iterable<Resource> resources);

    boolean containsEqualResources(ResourceSet other);

    boolean containsResourceWithUri(String uri);

    Resource getByUri(String uri);

    boolean isEmpty();

    @Override
    Iterator<Resource> iterator();

    int size();

    // FIXME toList should be unmodifiable copy
    List<Resource> toList();

}