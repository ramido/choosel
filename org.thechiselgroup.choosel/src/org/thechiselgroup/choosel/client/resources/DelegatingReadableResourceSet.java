package org.thechiselgroup.choosel.client.resources;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

public class DelegatingReadableResourceSet implements ReadableResourceSet {

    protected ReadableResourceSet delegate;

    public DelegatingReadableResourceSet(ReadableResourceSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesAddedEventHandler handler) {
        return delegate.addEventHandler(handler);
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesRemovedEventHandler handler) {
        return delegate.addEventHandler(handler);
    }

    @Override
    public boolean contains(Resource resource) {
        return delegate.contains(resource);
    }

    @Override
    public boolean containsAll(Iterable<Resource> resources) {
        return delegate.containsAll(resources);
    }

    @Override
    public boolean containsEqualResources(ResourceSet other) {
        return delegate.containsEqualResources(other);
    }

    @Override
    public boolean containsResourceWithUri(String uri) {
        return delegate.containsResourceWithUri(uri);
    }

    @Override
    public Resource getByUri(String uri) {
        return delegate.getByUri(uri);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<Resource> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public List<Resource> toList() {
        return delegate.toList();
    }

}
