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
package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.ZIndex;
import org.thechiselgroup.choosel.client.ui.dnd.DragProxyEventReceiver;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.windows.Desktop;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class DragEnabler {

    public static class InvisibleResourceSetAvatar extends ResourceSetAvatar
            implements DragProxyEventReceiver {

        private final ResourceItem item;

        private String text;

        public InvisibleResourceSetAvatar(ResourceItem item, String text,
                String enabledCSSClass, ResourceSetAvatarType type,
                Element element) {

            super(text, enabledCSSClass, item.getResourceSet(), type, element);

            this.text = text;
            this.item = item;
        }

        @Override
        public void addStyleName(String style) {
        }

        @Override
        public ResourceSetAvatar createProxy() {
            item.getPopupManager().hidePopup();
            return super.createProxy();
        }

        /**
         * @see http://code.google.com/p/choosel/issues/detail?id=29
         */
        @Override
        public void dragProxyAttached() {
            item.getHighlightingManager().setHighlighting(false);
        }

        @Override
        public void dragProxyDetached() {
            removeFromParent();
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
        }
    }

    private Desktop desktop;

    private ResourceSetAvatarDragController dragController;

    private ResourceItem item;

    private ResourceSetAvatar panel;

    public DragEnabler(ResourceItem item, Desktop desktop,
            ResourceSetAvatarDragController dragController) {

        assert item != null;

        this.desktop = desktop;
        this.dragController = dragController;
        this.item = item;
    }

    private void createDragWidget(int absoluteLeft, int absoluteTop) {
        assert panel == null;

        // add to desktop - need widget container
        // FIXME use dependency injection
        AbsolutePanel desktopWidget = desktop.asWidget();

        int left = absoluteLeft - desktopWidget.getAbsoluteLeft();
        int top = absoluteTop - desktopWidget.getAbsoluteTop();

        Element span = DOM.createSpan();

        CSS.setPosition(span, CSS.ABSOLUTE);
        CSS.setLocation(span, left, top);
        CSS.setZIndex(span, -1);

        // FIXME: we are not at the correct part of the widget hierarchy
        // --> this can cause event forwarding (i.e. to windows) to fail
        // TODO title
        final String text = (String) item
                .getResourceValue(SlotResolver.DESCRIPTION_SLOT);
        panel = new InvisibleResourceSetAvatar(item, text,
                "avatar-resourceSet", ResourceSetAvatarType.SET, span);

        span.setClassName("avatar-invisible");

        desktopWidget.add(panel);

        // make it draggable
        dragController.setDraggable(panel, true);
    }

    public void createTransparentDragProxy(final int absoluteLeft,
            final int absoluteTop) {
        createDragWidget(absoluteLeft, absoluteTop);

        // TODO remove code duplication
        AbsolutePanel desktopWidget = desktop.asWidget();
        int left = absoluteLeft - desktopWidget.getAbsoluteLeft() - 5;
        int top = absoluteTop - desktopWidget.getAbsoluteTop() - 5;

        Element element = panel.getElement();

        CSS.setLocation(element, left, top);
        CSS.setSize(element, 10, 10);
        CSS.setZIndex(element, ZIndex.POPUP - 1);

        panel.addMouseUpHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                removePanel();
            }

        });

        // fake mouse down event on widget
        MouseDownEvent mouseEvent = new MouseDownEvent() {
            @Override
            public int getClientX() {
                return absoluteLeft;
            }

            @Override
            public int getClientY() {
                return absoluteTop;
            }

            @Override
            public int getNativeButton() {
                return NativeEvent.BUTTON_LEFT;
            }

            @Override
            public int getRelativeX(com.google.gwt.dom.client.Element target) {
                return getClientX() - target.getAbsoluteLeft()
                        + target.getScrollLeft()
                        + target.getOwnerDocument().getScrollLeft();
            }

            @Override
            public int getRelativeY(com.google.gwt.dom.client.Element target) {
                return getClientY() - target.getAbsoluteTop()
                        + target.getScrollTop()
                        + target.getOwnerDocument().getScrollTop();
            }

            @Override
            public Object getSource() {
                return panel;
            }

            @Override
            public boolean isControlKeyDown() {
                return false;
            }

            @Override
            public boolean isMetaKeyDown() {
                return false;
            }
        };
        mouseEvent.setRelativeElement(element);
        panel.fireEvent(mouseEvent);
    }

    public void forwardMouseDown(NativeEvent e, int absoluteLeft,
            int absoluteTop) {

        createDragWidget(absoluteLeft, absoluteTop);

        // fake mouse down event on widget
        MouseDownEvent mouseEvent = new MouseDownEvent() {
            @Override
            public Object getSource() {
                return panel;
            }
        };

        mouseEvent.setRelativeElement(panel.getElement());
        mouseEvent.setNativeEvent(e);

        panel.fireEvent(mouseEvent);
    }

    public void forwardMouseDownWithEventPosition(Event e) {
        forwardMouseDown(e, e.getClientX(), e.getClientY());
    }

    public void forwardMouseDownWithTargetElementPosition(NativeEvent e) {
        Element element = e.getCurrentEventTarget().cast();
        int absoluteLeft = element.getAbsoluteLeft();
        int absoluteTop = element.getAbsoluteTop();

        forwardMouseDown(e, absoluteLeft, absoluteTop);
    }

    public void forwardMouseMove(final int absoluteLeft, final int absoluteTop) {
        if (panel == null) {
            return;
        }

        MouseMoveEvent mouseEvent = new MouseMoveEvent() {
            @Override
            public int getClientX() {
                return absoluteLeft;
            }

            @Override
            public int getClientY() {
                return absoluteTop;
            }

            @Override
            public int getRelativeX(com.google.gwt.dom.client.Element target) {
                return getClientX() - target.getAbsoluteLeft()
                        + target.getScrollLeft()
                        + target.getOwnerDocument().getScrollLeft();
            }

            @Override
            public int getRelativeY(com.google.gwt.dom.client.Element target) {
                return getClientY() - target.getAbsoluteTop()
                        + target.getScrollTop()
                        + target.getOwnerDocument().getScrollTop();
            }

            @Override
            public Object getSource() {
                return panel;
            }

        };
        mouseEvent.setRelativeElement(panel.getElement());
        panel.fireEvent(mouseEvent);
    }

    public void forwardMouseMove(NativeEvent e) {
        if (panel == null) {
            return;
        }

        MouseMoveEvent mouseEvent = new MouseMoveEvent() {
            @Override
            public Object getSource() {
                return panel;
            }
        };
        mouseEvent.setRelativeElement(panel.getElement());
        mouseEvent.setNativeEvent(e);
        panel.fireEvent(mouseEvent);
    }

    public void forwardMouseOut(NativeEvent e) {
        if (panel == null) {
            return;
        }

        MouseOutEvent mouseEvent = new MouseOutEvent() {
            @Override
            public Object getSource() {
                return panel;
            }
        };
        mouseEvent.setRelativeElement(panel.getElement());
        mouseEvent.setNativeEvent(e);
        panel.fireEvent(mouseEvent);
    }

    public void forwardMouseUp(NativeEvent e) {
        if (panel == null) {
            return;
        }

        MouseUpEvent mouseEvent = new MouseUpEvent() {
            @Override
            public Object getSource() {
                return panel;
            }
        };
        mouseEvent.setRelativeElement(panel.getElement());
        mouseEvent.setNativeEvent(e);
        panel.fireEvent(mouseEvent);

        removePanel();
    }

    private void removePanel() {
        if (panel != null) {
            panel.removeFromParent();
            panel = null;
        }
    }
}