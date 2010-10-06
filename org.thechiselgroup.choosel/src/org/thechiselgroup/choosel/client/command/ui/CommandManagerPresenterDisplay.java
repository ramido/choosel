package org.thechiselgroup.choosel.client.command.ui;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface CommandManagerPresenterDisplay {

    HasClickHandlers getClickHandlers();

    void setButtonEnabled(boolean enabled);

    void setCommandDescription(String commandDescription);

}