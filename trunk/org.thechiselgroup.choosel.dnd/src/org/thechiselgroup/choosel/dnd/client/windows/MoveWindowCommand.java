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
package org.thechiselgroup.choosel.dnd.client.windows;

import org.thechiselgroup.choosel.core.client.command.UndoableCommand;
import org.thechiselgroup.choosel.core.client.util.HasDescription;

public class MoveWindowCommand implements UndoableCommand, HasDescription {

    private final int sourceX;

    private final int sourceY;

    private final int targetX;

    private final int targetY;

    private final WindowPanel windowPanel;

    private final boolean animate;

    /**
     * Constructor with both locations for use from drag controller that moves
     * window while dragging.
     * 
     * @param windowPanel
     * @param sourceX
     * @param sourceY
     * @param targetX
     * @param targetY
     * @param animate
     *            TODO
     */
    public MoveWindowCommand(WindowPanel windowPanel, int sourceX, int sourceY,
            int targetX, int targetY, boolean animate) {

        assert windowPanel != null;

        this.windowPanel = windowPanel;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.animate = animate;
    }

    @Override
    public void execute() {
        windowPanel.setLocation(targetX, targetY, animate);
    }

    @Override
    public String getDescription() {
        return "Move window '" + windowPanel.getTitle() + "' to (" + targetX
                + ", " + targetY + ")";
    }

    @Override
    public void undo() {
        windowPanel.setLocation(sourceX, sourceY, animate);
    }

}
