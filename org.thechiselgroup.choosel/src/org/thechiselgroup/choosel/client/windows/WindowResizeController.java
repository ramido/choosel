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
package org.thechiselgroup.choosel.client.windows;

import java.util.HashMap;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.windows.ResizeablePanel.DirectionConstant;

import com.google.gwt.user.client.ui.Widget;

public final class WindowResizeController extends WindowDragController {

    private static final String CSS_WINDOW_TRANSPARENT = "windowTransparent";

    private static final int MIN_WIDGET_SIZE = 10;

    // for test
    static void resize(int desiredDraggableX, int desiredDraggableY,
            int draggableLeft, int draggableTop, int direction,
            ResizeablePanel windowPanel) {

        int verticalDelta = 0;
        if ((direction & ResizeablePanel.DIRECTION_NORTH) != 0) {
            verticalDelta = draggableTop - desiredDraggableY;
        } else if ((direction & ResizeablePanel.DIRECTION_SOUTH) != 0) {
            verticalDelta = desiredDraggableY - draggableTop;
        }

        int horizontalDelta = 0;
        if ((direction & ResizeablePanel.DIRECTION_WEST) != 0) {
            horizontalDelta = draggableLeft - desiredDraggableX;
        } else if ((direction & ResizeablePanel.DIRECTION_EAST) != 0) {
            horizontalDelta = desiredDraggableX - draggableLeft;
        }

        if (verticalDelta != 0 || horizontalDelta != 0) {
            int height = windowPanel.getHeight();
            int newHeight = Math.max(height + verticalDelta, MIN_WIDGET_SIZE);

            int width = windowPanel.getWidth();
            int newWidth = Math.max(width + horizontalDelta,
                    windowPanel.getMinimumWidth());

            int horizontalMove = 0;
            if ((direction & ResizeablePanel.DIRECTION_WEST) != 0) {
                horizontalMove = width - newWidth;
            }

            int verticalMove = 0;
            if ((direction & ResizeablePanel.DIRECTION_NORTH) != 0) {
                verticalMove = height - newHeight;
            }

            // TODO call anyways - should be handled in WindowPanel
            if (horizontalMove != 0 || verticalMove != 0) {
                windowPanel.moveBy(horizontalMove, verticalMove);
            }

            // TODO call anyways - should be handled in WindowPanel
            if (newHeight != height || newWidth != width) {
                windowPanel.setPixelSize(newWidth, newHeight);
            }
        }
    }

    private final HashMap<Widget, DirectionConstant> directionMap = new HashMap<Widget, DirectionConstant>();

    public WindowResizeController(WindowController controller,
            CommandManager commandManager) {
        super(controller, commandManager);
    }

    @Override
    public void dragEnd() {
        super.dragEnd();

        windowPanel.removeStyleName(CSS_WINDOW_TRANSPARENT);
    }

    @Override
    protected void dragMove(int desiredDraggableX, int desiredDraggableY) {
        int direction = getDirection(context.draggable).directionBits;

        int top = context.draggable.getAbsoluteTop()
                - getBoundaryPanel().getAbsoluteTop();

        int left = context.draggable.getAbsoluteLeft()
                - getBoundaryPanel().getAbsoluteLeft();

        resize(desiredDraggableX, desiredDraggableY, left, top, direction,
                windowPanel);
    }

    @Override
    public void dragStart() {
        getWindowPanelFromDraggable();

        bringToFront(windowPanel);
        windowPanel.addStyleName(CSS_WINDOW_TRANSPARENT);

        super.dragStart();
    }

    private DirectionConstant getDirection(Widget draggable) {
        return directionMap.get(draggable);
    }

    private void getWindowPanelFromDraggable() {
        Widget draggable = context.draggable;
        while (!(draggable instanceof WindowPanel) || (draggable == null)) {
            draggable = draggable.getParent();
        }
        windowPanel = (WindowPanel) draggable;
    }

    public void makeDraggable(Widget widget,
            WindowPanel.DirectionConstant direction) {
        super.makeDraggable(widget);
        directionMap.put(widget, direction);
    }
}
