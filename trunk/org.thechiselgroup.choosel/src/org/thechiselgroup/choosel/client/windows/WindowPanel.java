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

import java.util.ArrayList;
import java.util.List;

import org.adamtacy.client.ui.NEffectPanel;
import org.adamtacy.client.ui.effects.NEffect;
import org.adamtacy.client.ui.effects.events.EffectCompletedEvent;
import org.adamtacy.client.ui.effects.events.EffectCompletedHandler;
import org.adamtacy.client.ui.effects.impl.Move;
import org.thechiselgroup.choosel.client.fx.FXUtil;
import org.thechiselgroup.choosel.client.fx.Opacity;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.dnd.DragProxyEventReceiver;
import org.thechiselgroup.choosel.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.client.util.MathUtils;

import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WindowPanel extends NEffectPanel implements
        DragProxyEventReceiver, Opacity, ResizeablePanel {

    private static final int BORDER_THICKNESS = 7;

    private static final String CSS_WINDOW = "choosel-WindowPanel";

    private static final String CSS_WINDOW_BUTTON_PANEL = "choosel-WindowPanel-ButtonPanel";

    private static final String CSS_WINDOW_HEADER = "choosel-WindowPanel-Header";

    private static final String CSS_WINDOW_HEADER_LABEL = "choosel-WindowPanel-HeaderLabel";

    private static final String CSS_WINDOW_RESIZE = "choosel-WindowPanel-Resize-";

    private static final String CSS_WINDOW_RESIZE_EDGE = "choosel-WindowPanel-ResizeEdge";

    private static final String IMAGE_CLOSE_ACTIVE = "images/close_active.gif";

    private static final String IMAGE_CLOSE_INVISIBLE = "images/close_invisible.gif";

    private static final String IMAGE_CLOSE_VISIBLE = "images/close_visible.gif";

    private static final int WINDOW_BORDER_FROM_CSS = 0 + 2; // outer + inner

    private static final int TOTAL_BORDER_THICKNESS = 2 * BORDER_THICKNESS
            + WINDOW_BORDER_FROM_CSS;

    private Image closeImage;

    private Widget contentWidget;

    private Widget eastTopWidget;

    private Widget eastWidget;

    private Grid grid;

    private Label headerWidget;

    private Widget northWidget;

    private final List<Widget> removeFromDragControllerOnDispose = new ArrayList<Widget>();

    protected FocusPanel rootPanel;

    private Widget southWidget;

    // TODO move into a better place as this is relevant for persistency only
    private WindowContent viewContent;

    private Widget westTopWidget;

    private Widget westWidget;

    private WindowController windowController;

    private String windowTitle;

    /**
     * Panel that contains the title of the window and window buttons such as
     * the close button.
     */
    private HorizontalPanel headerBar;

    /**
     * Adjusts the size of the window or the size of its content. If the window
     * is larger than its content, then the content size is increased. If the
     * window is smaller, its size is increased. It also checks for maximum
     * sizes because of desktop limitations.
     */
    public void adjustSize() {
        /*
         * Calculate target width: max of header, adjusted window, content
         * 
         * TODO: restrict to available desktop space
         */
        int targetWidth = TOTAL_BORDER_THICKNESS
                + MathUtils.max(headerBar.getOffsetWidth(),
                        contentWidget.getOffsetWidth(), getWidth()
                                - TOTAL_BORDER_THICKNESS);
        /*
         * Calculate target height: max of adjusted window, content
         * 
         * TODO: restrict to available desktop space
         */
        int targetHeight = TOTAL_BORDER_THICKNESS
                + MathUtils.max(contentWidget.getOffsetHeight(), getHeight()
                        - TOTAL_BORDER_THICKNESS - headerBar.getOffsetHeight());

        setPixelSize(targetWidth, targetHeight);
    }

    // TODO refactor
    public void animateMoveToLocation(final int x, final int y) {
        WidgetLocation location = new WidgetLocation(this, getParent());

        Move move = new Move(x - location.getLeft(), y - location.getTop()) {
            @Override
            public void tearDownEffect() {
                // do not super.tearDownEffects as this resets to original state
                // reset root panel position as this is affected by move
                CSS.setLocation(rootPanel, 0, 0);
            }
        };

        move.addEffectCompletedHandler(new EffectCompletedHandler() {
            @Override
            public void onEffectCompleted(EffectCompletedEvent event) {
                removeEffects();
                setLocation(x, y);
                assert x == new WidgetLocation(WindowPanel.this, getParent())
                        .getLeft();
                assert y == new WidgetLocation(WindowPanel.this, getParent())
                        .getTop();
                assert 0 == new WidgetLocation(rootPanel, WindowPanel.this)
                        .getLeft();
                assert 0 == new WidgetLocation(rootPanel, WindowPanel.this)
                        .getTop();
            }
        });

        move.setTransitionType(FXUtil.EASE_OUT);
        move.setDuration(FXUtil.MORPH_DURATION_IN_SECONDS);

        addEffect(move);
        playEffects();
    }

    /**
     * Fades out the window and removes it from the window controller
     * afterwards.
     */
    // TODO rename to animate hide, add effect completed handler in method
    public void close() {
        NEffect fade = createHideEffect();

        fade.addEffectCompletedHandler(new EffectCompletedHandler() {
            @Override
            public void onEffectCompleted(EffectCompletedEvent event) {
                removeEffects();
                windowController.close(WindowPanel.this);
            }
        });

        addEffect(fade);
        playEffects();
    }

    // hook
    protected ClickHandler createCloseButtonClickHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        };
    }

    private HorizontalPanel createHeaderBar() {
        HorizontalPanel headerBar = new HorizontalPanel();

        headerBar.setSize("100%", "");
        headerBar.add(headerWidget);
        headerBar.setCellHorizontalAlignment(headerWidget,
                HasAlignment.ALIGN_LEFT);

        closeImage = new Image(getInvisibleCloseImageUrl());
        closeImage.addStyleName(CSS_WINDOW_BUTTON_PANEL);

        closeImage.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                closeImage.setUrl(getActiveCloseImageUrl());
            }
        });

        closeImage.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                closeImage.setUrl(getVisibleCloseImageUrl());
            }
        });

        rootPanel.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                closeImage.setUrl(getVisibleCloseImageUrl());
            }
        });

        rootPanel.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                closeImage.setUrl(getInvisibleCloseImageUrl());
            }
        });

        // disable dragging / transparency on mouse down over image
        closeImage.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });

        closeImage.addClickHandler(createCloseButtonClickHandler());

        DefaultPopupManager manager = DefaultPopupManager.createPopupManager(
                closeImage, new WidgetFactory() {
                    @Override
                    public Widget createWidget() {
                        return new Label(getClosePopupLabel());
                    }
                });
        manager.setHideDelay(0);

        headerBar.add(closeImage);
        headerBar.setCellHorizontalAlignment(closeImage,
                HasAlignment.ALIGN_RIGHT);
        headerBar.setCellVerticalAlignment(closeImage,
                HasAlignment.ALIGN_MIDDLE);

        return headerBar;
    }

    // hook
    protected NEffect createHideEffect() {
        return FXUtil.createOpacityMorph(Opacity.OPACITY_OPAQUE,
                OPACITY_TRANSPARENT);
    }

    protected NEffect createShowEffect() {
        return FXUtil.createOpacityMorph(OPACITY_TRANSPARENT, OPACITY_OPAQUE);
    }

    @Override
    public void dragProxyAttached() {
        closeImage.setUrl(getInvisibleCloseImageUrl());
    }

    @Override
    public void dragProxyDetached() {
        // ignored
    }

    public int getAbsoluteX() {
        return getAbsoluteLeft() - getParent().getAbsoluteLeft();
    }

    public int getAbsoluteY() {
        return getAbsoluteTop() - getParent().getAbsoluteTop();
    }

    private String getActiveCloseImageUrl() {
        return getModuleBase() + IMAGE_CLOSE_ACTIVE;
    }

    /*
     * hook method
     */
    protected String getClosePopupLabel() {
        return "Close";
    }

    @Override
    public int getHeight() {
        return grid.getOffsetHeight();
    }

    private String getInvisibleCloseImageUrl() {
        return getModuleBase() + IMAGE_CLOSE_INVISIBLE;
    }

    protected String getModuleBase() {
        return GWT.getModuleBaseURL();
    }

    // TODO move into model
    public WindowContent getViewContent() {
        return this.viewContent;
    }

    private String getVisibleCloseImageUrl() {
        return getModuleBase() + IMAGE_CLOSE_VISIBLE;
    }

    @Override
    public int getWidth() {
        return grid.getOffsetWidth();
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public int getZIndex() {
        return DOM.getIntStyleAttribute(getElement(), CSS.Z_INDEX);
    }

    public void init(WindowController windowController, String title,
            Widget contentWidget) {

        initShowEvent();

        DOM.setStyleAttribute(getElement(), "border", "0px"); // TODO move to
                                                              // CSS class

        this.windowTitle = title;
        this.rootPanel = new FocusPanel();
        setWidget(this.rootPanel);

        DOM.setStyleAttribute(rootPanel.getElement(), "border", "0px"); // TODO

        this.windowController = windowController;

        rootPanel.addStyleName(CSS_WINDOW);

        this.headerWidget = new Label(title);
        headerWidget.addStyleName(CSS_WINDOW_HEADER_LABEL);

        this.contentWidget = contentWidget;
        // TODO move to CSS
        // this.contentWidget.getElement().setAttribute("overflow", "hidden");

        headerBar = createHeaderBar();

        FocusPanel headerContainer = new FocusPanel();
        headerContainer.addStyleName(CSS_WINDOW_HEADER);
        headerContainer.add(headerBar);

        windowController.getMoveDragController().makeDraggable(this,
                headerContainer);

        rootPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO fix bug: window not to front
                // deactivated because of list box issue
                // // force our panel to the top of our z-index context
                // AbsolutePanel boundaryPanel = windowController
                // .getBoundaryPanel();
                // WidgetLocation location = new
                // WidgetLocation(WindowPanel.this,
                // boundaryPanel);
                // boundaryPanel.add(WindowPanel.this, location.getLeft(),
                // location.getTop());
            }
        });

        grid = new Grid(4, 3);
        grid.setBorderWidth(0);
        grid.setCellSpacing(0);
        grid.setCellPadding(0);
        rootPanel.add(grid);

        setupCell(0, 0, NORTH_WEST);
        northWidget = setupCell(0, 1, NORTH);
        setupCell(0, 2, NORTH_EAST);

        westTopWidget = setupCell(1, 0, WEST_TOP);
        grid.setWidget(1, 1, headerContainer);
        eastTopWidget = setupCell(1, 2, EAST_TOP);

        westWidget = setupCell(2, 0, WEST);
        grid.setWidget(2, 1, contentWidget);
        eastWidget = setupCell(2, 2, EAST);

        setupCell(3, 0, SOUTH_WEST);
        southWidget = setupCell(3, 1, SOUTH);
        setupCell(3, 2, SOUTH_EAST);
    }

    private void initShowEvent() {
        NEffect showEffect = createShowEffect();
        showEffect.addEffectCompletedHandler(new EffectCompletedHandler() {
            @Override
            public void onEffectCompleted(EffectCompletedEvent event) {
                removeEffects();

                // TODO extract constant
                // DOM.setStyleAttribute(rootPanel.getElement(), "opacity",
                // null);
            }
        });
        addEffect(showEffect);
    }

    @Override
    public void moveBy(int relativeX, int relativeY) {
        if (relativeX == 0 && relativeY == 0) {
            return;
        }

        AbsolutePanel parent = (AbsolutePanel) getParent();
        Location location = new WidgetLocation(this, parent);

        int left = location.getLeft() + relativeX;
        int top = location.getTop() + relativeY;

        parent.setWidgetPosition(this, left, top);
    }

    @Override
    protected void onDetach() {
        windowController.getMoveDragController().makeNotDraggable(this);

        for (Widget w : removeFromDragControllerOnDispose) {
            windowController.getResizeDragController().makeNotDraggable(w);
        }

        // DragController#unregisterDropController

        super.onDetach();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        adjustSize();
        playEffects();
    }

    private void setBorderWidths(int contentWidth, int contentHeight,
            int headerHeight) {

        northWidget.setPixelSize(contentWidth, BORDER_THICKNESS);
        southWidget.setPixelSize(contentWidth, BORDER_THICKNESS);
        westTopWidget.setPixelSize(BORDER_THICKNESS, headerHeight);
        westWidget.setPixelSize(BORDER_THICKNESS, contentHeight);
        eastTopWidget.setPixelSize(BORDER_THICKNESS, headerHeight);
        eastWidget.setPixelSize(BORDER_THICKNESS, contentHeight);
    }

    public void setLocation(int x, int y) {
        AbsolutePanel parent = (AbsolutePanel) getParent();
        parent.setWidgetPosition(this, x, y);

        assert x == new WidgetLocation(this, getParent()).getLeft();
        assert y == new WidgetLocation(this, getParent()).getTop();
    }

    @Override
    public void setPixelSize(int width, int height) {
        /*
         * setPixelSize calculates the expected size of the content widget and
         * sets it to this size. It then requests the real size of the content
         * widget, because we have no better way to find out about the minimum
         * size. Afterwards, we change the border width and finally adjust the
         * window size.
         */
        assert width >= 0;
        assert height >= 0;

        if (width == getWidth() && height == getHeight()) {
            return;
        }

        int headerHeight = headerBar.getOffsetHeight();
        int contentWidth = width - TOTAL_BORDER_THICKNESS;
        int contentHeight = height - TOTAL_BORDER_THICKNESS - headerHeight;

        contentWidget.setPixelSize(contentWidth, contentHeight);

        int realContentWidth = contentWidget.getOffsetWidth();
        int realContentHeight = contentWidget.getOffsetHeight();

        /* -4: subtract header padding from CSS file */
        headerBar.setPixelSize(contentWidth - 4, headerHeight);

        /*
         * adjust for the case where headerWidth > contentContentWidth
         * (otherwise content does not fill the available space)
         */
        int headerWidth = headerBar.getOffsetWidth();
        if (headerWidth > realContentWidth) {
            realContentWidth = headerWidth;
            contentWidget.setPixelSize(realContentWidth, realContentHeight);
        }

        setBorderWidths(realContentWidth, realContentHeight, headerHeight);

        // XXX not only header, but also bar --> widget internal

        int attempedWidth = realContentWidth + TOTAL_BORDER_THICKNESS;
        int attempedHeight = realContentHeight + TOTAL_BORDER_THICKNESS
                + headerHeight;

        super.setPixelSize(attempedWidth, attempedHeight);
    }

    private Widget setupCell(int row, int col, Direction direction) {
        final FocusPanel widget = new FocusPanel();
        widget.setPixelSize(BORDER_THICKNESS, BORDER_THICKNESS);
        grid.setWidget(row, col, widget);
        windowController.getResizeDragController().makeDraggable(widget,
                direction);
        removeFromDragControllerOnDispose.add(widget);

        /*
         * both CSS classes need to be set in one call due to limitations in
         * getCellFormatter().addStyleName
         */
        grid.getCellFormatter().addStyleName(
                row,
                col,
                CSS_WINDOW_RESIZE_EDGE + " " + CSS_WINDOW_RESIZE
                        + direction.directionLetters);

        return widget;
    }

    public void setViewContent(WindowContent viewContent) {
        this.viewContent = viewContent;
    }

    public void setZIndex(final int zIndex) {
        // Bugfix: need to set zIndex manually because of timeline/firefox issue
        CSS.setZIndex(this, zIndex);
    }
}
