package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.views.DefaultView;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DefaultViewLoader implements ViewLoader {

    private final ErrorHandler errorHandler;

    private final ViewLoadManager viewPersistenceManager;

    @Inject
    public DefaultViewLoader(ViewLoadManager viewPersistenceManager,
            ErrorHandler errorHandler) {

        this.viewPersistenceManager = viewPersistenceManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public void loadView(Long id, AsyncCallback<DefaultView> callback) {
        viewPersistenceManager.loadView(id, callback);
    }

}
