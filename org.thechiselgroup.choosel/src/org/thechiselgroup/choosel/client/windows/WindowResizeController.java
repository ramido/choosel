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
import org.thechiselgroup.choosel.client.windows.WindowPanel.DirectionConstant;

import com.google.gwt.user.client.ui.Widget;

public final class WindowResizeController extends WindowDragController {

    private static final String CSS_WINDOW_TRANSPARENT = "windowTransparent";

    private static final int MIN_WIDGET_SIZE = 10;

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

        if ((direction & WindowPanel.DIRECTION_NORTH) != 0) {
            int delta = getTop() - desiredDraggableY;
            if (delta != 0) {
                int contentHeight = windowPanel.getContentHeight();
                int newHeight = Math
                        .max(contentHeight + delta, MIN_WIDGET_SIZE);
                if (newHeight != contentHeight) {
                    windowPanel.moveBy(0, contentHeight - newHeight);
                    windowPanel.setContentSize(windowPanel.getContentWidth(),
                            newHeight);
                }
            }
        } else if ((direction & WindowPanel.DIRECTION_SOUTH) != 0) {
            int delta = desiredDraggableY - getTop();

            if (delta != 0) {
                int contentHeight = windowPanel.getContentHeight();
                int newHeight = Math
                        .max(contentHeight + delta, MIN_WIDGET_SIZE);

                if (newHeight != contentHeight) {
                    windowPanel.setContentSize(windowPanel.getContentWidth(),
                            newHeight);
                }
            }
        }
        if ((direction & WindowPanel.DIRECTION_WEST) != 0) {
            int delta = getLeft() - desiredDraggableX;
            if (delta != 0) {
                int contentWidth = windowPanel.getContentWidth();
                int newWidth = Math.max(contentWidth + delta,
                        windowPanel.getMinimumWidth());

                if (newWidth != contentWidth) {
                    windowPanel.moveBy(contentWidth - newWidth, 0);

                    windowPanel.setContentSize(newWidth,
                            windowPanel.getContentHeight());
                }
            }
        } else if ((direction & WindowPanel.DIRECTION_EAST) != 0) {
            int delta = desiredDraggableX - getLeft();
            if (delta != 0) {
                int contentWidth = windowPanel.getContentWidth();
                int newWidth = Math.max(contentWidth + delta,
                        windowPanel.getMinimumWidth());

                if (newWidth != contentWidth) {
                    windowPanel.setContentSize(newWidth,
                            windowPanel.getContentHeight());
                }
            }
        }
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

    private int getLeft() {
        return context.draggable.getAbsoluteLeft()
                - getBoundaryPanel().getAbsoluteLeft();
    }

    private int getTop() {
        return context.draggable.getAbsoluteTop()
                - getBoundaryPanel().getAbsoluteTop();
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
