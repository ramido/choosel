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

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;

public class ActionToolbarIcon extends Image {

    public static final String CSS_ACTION_TOOLBAR_BUTTON = "ActionToolbarButton";

    private Action action;

    private boolean mouseOver = false;

    public ActionToolbarIcon(Action action) {
        assert action != null;

        setStyleName(CSS_ACTION_TOOLBAR_BUTTON);

        initMouseHandlers();
        initActionChangeHandler();

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
    }

}