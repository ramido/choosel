package org.thechiselgroup.choosel.client.util;

import java.util.Iterator;

public class SingleItemIterable<T> implements Iterable<T> {

    private final T t;

    public SingleItemIterable(T t) {
        this.t = t;
    }

    @Override
    public Iterator<T> iterator() {
        return new SingleItemIterator<T>(t);
    }

}