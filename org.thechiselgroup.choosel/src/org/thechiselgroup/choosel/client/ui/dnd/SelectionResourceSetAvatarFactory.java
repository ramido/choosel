package org.thechiselgroup.choosel.client.ui.dnd;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DelegatingResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class SelectionResourceSetAvatarFactory extends
        DelegatingResourceSetAvatarFactory {

    private ViewAccessor viewAccessor;

    public SelectionResourceSetAvatarFactory(ResourceSetAvatarFactory delegate,
            ViewAccessor viewAccessor) {
        super(delegate);
        this.viewAccessor = viewAccessor;
    }

    @Override
    public ResourceSetAvatar createAvatar(ResourceSet resources) {
        final ResourceSetAvatar avatar = delegate.createAvatar(resources);

        avatar.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                View v = viewAccessor.findView(avatar);
                if (avatar.getResourceSet().equals(v.getSelection())) {
                    v.setSelection(null);
                } else {
                    v.setSelection(avatar.getResourceSet());
                }
            }
        });

        return avatar;
    }
}