package org.thechiselgroup.choosel.client.util;

import com.google.gwt.event.shared.HandlerRegistration;

public final class NullHandlerRegistration implements HandlerRegistration {

    public static NullHandlerRegistration NULL_HANDLER_REGISTRATION = new NullHandlerRegistration();

    private NullHandlerRegistration() {
    }

    @Override
    public void removeHandler() {
    }

}