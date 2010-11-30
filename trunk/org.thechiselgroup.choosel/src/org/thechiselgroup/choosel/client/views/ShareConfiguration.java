package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;

import com.google.gwt.user.client.ui.StackPanel;

public interface ShareConfiguration extends WidgetAdaptable {

    public abstract void attach(View view, StackPanel panel);

    public abstract void updateSharePanel(Long id);

}