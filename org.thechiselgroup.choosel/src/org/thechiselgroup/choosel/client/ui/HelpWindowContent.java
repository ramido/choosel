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

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HelpWindowContent extends AbstractWindowContent implements
        Persistable {

    public HelpWindowContent() {
        super("Help", "help");
    }

    @Override
    public Widget asWidget() {
        String playList = "<object width=\"700\" height=\"483\">"
                + "<param name=\"movie\" value=\"http://www.youtube-nocookie.com/p/C67C71F54B5E198B&amp;hl=en_US&amp;fs=1&hd=1&rel=0&autoplay=1\"></param>"
                + "<param name=\"allowFullScreen\" value=\"true\"></param>"
                + "<param name=\"allowscriptaccess\" value=\"always\"></param>"
                + "<param name=\"wmode\" value=\"transparent\"></param>"
                + "<embed src=\"http://www.youtube-nocookie.com/p/C67C71F54B5E198B&amp;hl=en_US&amp;fs=1&hd=1&rel=0&autoplay=1\" "
                + "type=\"application/x-shockwave-flash\" "
                + "width=\"700\" height=\"483\" "
                + "allowscriptaccess=\"always\" wmode=\"transparent\" allowfullscreen=\"true\"></embed></object>";

        // String youtube = "<object width=\"425\" height=\"344\">"
        // +
        // "<param name=\"movie\" value=\"http://www.youtube.com/v/8lMDN24TLmE&hl=en_US&fs=1&rel=0\"></param>"
        // + "<param name=\"allowFullScreen\" value=\"true\"></param>"
        // + "<param name=\"allowscriptaccess\" value=\"always\"></param>"
        // + "<param name=\"wmode\" value=\"transparent\"></param>"
        // +
        // "<embed src=\"http://www.youtube.com/v/8lMDN24TLmE&hl=en_US&fs=1&rel=0\" "
        // +
        // "type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" "
        // +
        // "allowfullscreen=\"true\" wmode=\"transparent\" width=\"425\" height=\"344\">"
        // + "</embed></object>";

        final String html = "<p style=\"width: 660px; margin: 10px; padding-bottom: 10px;\"><b>This tutorial contains 20 videos that explain the "
                + "different features of Bio-Mixer. Please use the video controls or hover over the video to"
                + " jump to a specific tutorial.</b></p>" + playList;
        return new HTML(html);
    }

    @Override
    public void restore(Memento state, ResourceSetAccessor accessor) {
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        return new Memento();
    }
}