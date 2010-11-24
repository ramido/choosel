package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.views.DefaultView;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ViewLoader {

    void loadView(Long id, AsyncCallback<DefaultView> callback);

}
