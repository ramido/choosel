package org.thechiselgroup.choosel.client.workspace.service;

import org.thechiselgroup.choosel.client.workspace.dto.ViewDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ViewPersistenceServiceAsync {

    void loadView(Long viewId, AsyncCallback<ViewDTO> callback);

    void saveView(ViewDTO window, AsyncCallback<Long> callback);
}
