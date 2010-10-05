/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.client.ui;

import org.thechiselgroup.choosel.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ActionToolbarItem extends Image {

    public static final String CSS_ACTION_TOOLBAR_BUTTON = "ActionToolbarButton";

    private Action action;

    private boolean mouseOver = false;

    private Label popupLabel;

    public ActionToolbarItem(Action action,
            PopupManagerFactory popupManagerFactory) {

        assert action != null;
        assert popupManagerFactory != null;

        this.action = action;

        setStyleName(CSS_ACTION_TOOLBAR_BUTTON);

        initMouseHandlers();
        initActionChangeHandler();
        initPopup(popupManagerFactory);

        update();
    }

    private void initActionChangeHandler() {
        action.addActionChangedHandler(new ActionChangedEventHandler() {
            @Override
            public void onActionChanged(ActionChangedEvent event) {
                update();
            }
        });
    }

    private void initMouseHandlers() {
        addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                mouseOver = true;
                if (action.isEnabled()) {
                    setUrl(action.getHighlightedIconUrl());
                }
            }
        });
        addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                mouseOver = false;
                if (action.isEnabled()) {
                    setUrl(action.getNormalIconUrl());
                }
            }
        });
    }

    private void initPopup(PopupManagerFactory popupManagerFactory) {
        popupLabel = new Label();
        PopupManager popupManager = popupManagerFactory
                .createPopupManager(new WidgetFactory() {
                    @Override
                    public Widget createWidget() {
                        return popupLabel;
                    }
                });
        DefaultPopupManager.linkManagerToSource(popupManager, this);
    }

    // TODO do not show popup if disabled?
    protected void update() {
        if (action.isEnabled()) {
            if (mouseOver) {
                setUrl(action.getHighlightedIconUrl());
            } else {
                setUrl(action.getNormalIconUrl());
            }
        } else {
            setUrl(action.getDisabledIconUrl());
        }

        // TODO name in bold, break, description if available
        popupLabel.setText(action.getName());
    }

}