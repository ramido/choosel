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
package org.thechiselgroup.choosel.core.client.ui.popup;

import org.thechiselgroup.choosel.core.client.ui.CSS;
import org.thechiselgroup.choosel.core.client.ui.ZIndex;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;

// TODO unify / replace with new popup system
public class DefaultDelayedPopup extends PopupPanel implements MouseOutHandler,
        MouseOverHandler, DelayedPopup {

    public static final String CSS_ALPHA = "popups-Alpha";

    public static final String CSS_POPUP = "popups-Popup";

    private int hideDelay;

    private Timer hideTimer;

    private int showDelay;

    private Timer showTimer;

    public DefaultDelayedPopup() {
        this(400, 200);
    }

    public DefaultDelayedPopup(int showDelay, int hideDelay) {
        super(true, false);

        this.showDelay = showDelay;
        this.hideDelay = hideDelay;

        hideTimer = new Timer() {
            @Override
            public void run() {
                hide();
            }
        };

        showTimer = new Timer() {
            @Override
            public void run() {
                updatePosition();
                show();
            }
        };

        setStyleName(CSS_POPUP);

        CSS.setZIndex(getElement(), ZIndex.POPUP);

        // TODO buggy, should depend on mouse position because popup might
        // show up below cursor and then it should not be semi-transparent
        addStyleName(CSS_ALPHA);
    }

    @Override
    public void hideDelayed() {
        showTimer.cancel();
        hideTimer.schedule(hideDelay);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        hideDelayed();
        addStyleName(CSS_ALPHA);
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        hideTimer.cancel();
        removeStyleName(CSS_ALPHA);
    }

    @Override
    public void showDelayed() {
        hideTimer.cancel();
        showTimer.schedule(showDelay);
    }

    protected void updatePosition() {

    }

}
