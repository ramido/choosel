package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandlingAsyncCallback;
import org.thechiselgroup.choosel.client.views.DefaultView;

import com.google.inject.Inject;

public class DefaultViewPersistence implements ViewPersistence {

    private final ErrorHandler errorHandler;

    private final ViewPersistenceManager viewPersistenceManager;

    @Inject
    public DefaultViewPersistence(
            ViewPersistenceManager viewPersistenceManager,
            ErrorHandler errorHandler) {

        this.viewPersistenceManager = viewPersistenceManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public void saveView(DefaultView view) {
        viewPersistenceManager.saveView(view,
                new ErrorHandlingAsyncCallback<Void>(errorHandler));
    }

}
