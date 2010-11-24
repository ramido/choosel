package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.views.DefaultView;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ViewSaveManager {

    void saveView(DefaultView view, AsyncCallback<Void> callback);
}
