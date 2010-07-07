package org.thechiselgroup.choosel.client.util;

import java.util.Iterator;

public class SingleItemIterator<T> implements Iterator<T> {

    private T t;

    private boolean hasNext = true;

    public SingleItemIterator(T t) {
        this.t = t;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        hasNext = false;
        return t;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}