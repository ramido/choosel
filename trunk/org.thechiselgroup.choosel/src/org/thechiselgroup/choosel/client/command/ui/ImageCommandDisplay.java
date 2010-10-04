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
package org.thechiselgroup.choosel.client.command.ui;

import org.thechiselgroup.choosel.client.command.ui.CommandPresenter.CommandDisplay;
import org.thechiselgroup.choosel.client.ui.HasEnabledState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

// TODO hover
public class ImageCommandDisplay extends Image implements CommandDisplay,
        HasEnabledState {

    public static final String CSS_IMAGE_COMMAND_DISPLAY = "ImageCommandDisplay";

    public static final String HIGHLIGHTED = "highlighted";

    public static final String NORMAL = "normal";

    public static final String PATH = "images/";

    public static final String PREFIX = "icon";

    public static final String SEPARATOR = "-";

    public static final String DISABLED = "disabled";

    public static final String SUFFIX = ".png";

    private String disabledUrl;

    private String normalUrl;

    private String highlightedUrl;

    private boolean enabled = true;

    private boolean mouseOver = false;

    {
        setUrl(disabledUrl);
    }

    public ImageCommandDisplay(String name) {
        assert name != null;

        this.disabledUrl = getIconUrl(name, DISABLED);
        this.normalUrl = getIconUrl(name, NORMAL);
        this.highlightedUrl = getIconUrl(name, HIGHLIGHTED);

        setStyleName(CSS_IMAGE_COMMAND_DISPLAY);
        setUrl(normalUrl);

        addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                mouseOver = true;
                if (enabled) {
                    setUrl(highlightedUrl);
                }
            }
        });
        addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                mouseOver = false;
                if (enabled) {
                    setUrl(normalUrl);
                }
            }
        });
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    protected String getIconUrl(String name, String string) {
        return GWT.getModuleBaseURL() + PATH + PREFIX + SEPARATOR + name
                + SEPARATOR + string + SUFFIX;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == this.enabled) {
            return;
        }

        this.enabled = enabled;

        if (enabled) {
            if (mouseOver) {
                setUrl(highlightedUrl);
            } else {
                setUrl(normalUrl);
            }
        } else {
            setUrl(disabledUrl);
        }
    }

}
