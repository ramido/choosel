package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.authentication.AuthenticationManager;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

import com.google.inject.Inject;

public class DefaultShareConfigurationFactory implements
        ShareConfigurationFactory {

    private final ViewSaver viewSaver;

    private final AuthenticationManager manager;

    @Inject
    public DefaultShareConfigurationFactory(ViewSaver viewSaver,
            AuthenticationManager manager) {
        assert viewSaver != null;
        assert manager != null;

        this.viewSaver = viewSaver;
        this.manager = manager;

    }

    @Override
    public ShareConfiguration createShareConfiguration() {
        return new DefaultShareConfiguration(viewSaver, manager);
    }

}
