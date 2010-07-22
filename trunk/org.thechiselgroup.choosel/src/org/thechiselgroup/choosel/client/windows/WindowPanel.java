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
        DragProxyEventReceiver, Opacity {

    /**
     * WindowPanel direction constant, used in
     * {@link WindowResizeController#makeDraggable(com.google.gwt.user.client.ui.Widget, org.thechiselgroup.mashups.client.windows.demo.client.example.resize.WindowPanel.DirectionConstant)}
     * .
     */
    public static class DirectionConstant {

        public final int directionBits;

        public final String directionLetters;

        private DirectionConstant(int directionBits, String directionLetters) {
            this.directionBits = directionBits;
            this.directionLetters = directionLetters;
        }
    }

    private static final int BORDER_THICKNESS = 3;

    private static final String CSS_WINDOW = "window";

    private static final String CSS_WINDOW_BUTTON_PANEL = "window-button-panel";

    private static final String CSS_WINDOW_HEADER = "window-header";

    private static final String CSS_WINDOW_HEADER_LABEL = "window-header-label";

    private static final String CSS_WINDOW_RESIZE_EDGE = "window-resize-edge";

    /**
     * Specifies that resizing occur at the east edge.
     */
    public static final int DIRECTION_EAST = 0x0001;

    /**
     * Specifies that resizing occur at the both edge.
     */
    public static final int DIRECTION_NORTH = 0x0002;

    /**
     * Specifies that resizing occur at the south edge.
     */
    public static final int DIRECTION_SOUTH = 0x0004;

    /**
     * Specifies that resizing occur at the west edge.
     */
    public static final int DIRECTION_WEST = 0x0008;

    /**
     * Specifies that resizing occur at the east edge.
     */
    public static final DirectionConstant EAST = new DirectionConstant(
            DIRECTION_EAST, "e");

    private static final String IMAGE_CLOSE_ACTIVE = "images/close_active.gif";

    private static final String IMAGE_CLOSE_INVISIBLE = "images/close_invisible.gif";

    private static final String IMAGE_CLOSE_VISIBLE = "images/close_visible.gif";

    /**
     * Specifies that resizing occur at the both edge.
     */
    public static final DirectionConstant NORTH = new DirectionConstant(
            DIRECTION_NORTH, "n");

    /**
     * Specifies that resizing occur at the north-east edge.
     */
    public static final DirectionConstant NORTH_EAST = new DirectionConstant(
            DIRECTION_NORTH | DIRECTION_EAST, "ne");

    /**
     * Specifies that resizing occur at the north-west edge.
     */
    public static final DirectionConstant NORTH_WEST = new DirectionConstant(
            DIRECTION_NORTH | DIRECTION_WEST, "nw");

    /**
     * Specifies that resizing occur at the south edge.
     */
    public static final DirectionConstant SOUTH = new DirectionConstant(
            DIRECTION_SOUTH, "s");

    /**
     * Specifies that resizing occur at the south-east edge.
     */
    public static final DirectionConstant SOUTH_EAST = new DirectionConstant(
            DIRECTION_SOUTH | DIRECTION_EAST, "se");

    /**
     * Specifies that resizing occur at the south-west edge.
     */
    public static final DirectionConstant SOUTH_WEST = new DirectionConstant(
            DIRECTION_SOUTH | DIRECTION_WEST, "sw");

    /**
     * Specifies that resizing occur at the west edge.
     */
    public static final DirectionConstant WEST = new DirectionConstant(
            DIRECTION_WEST, "w");

    private static final int WINDOW_BORDER_FROM_CSS = 2;

    private Image closeImage;

    private int contentHeight;

    private Widget contentWidget;

    private int contentWidth;

    private Widget eastTopWidget;

    private Widget eastWidget;

    private Grid grid;

    private FocusPanel headerContainer;

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

    public void adjustSize() {
        int height = contentWidget.getOffsetHeight();
        int width = contentWidget.getOffsetWidth();

        setContentSize(width, height);
    }

    // TODO refactor
    public void animateMoveToLocation(final int x, final int y) {
        WidgetLocation location = new WidgetLocation(this, getParent());

        Move move = new Move(x - location.getLeft(), y - location.getTop()) {
            @Override
            public void tearDownEffect() {
                // do not super.tearDownEffects as this resets to original state
                // reset root panel position as this is affected by move
                CSS.setPosition(rootPanel, 0, 0);
            };
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

    public int getContentHeight() {
        return contentHeight;
    }

    public int getContentWidth() {
        return contentWidth;
    }

    private String getInvisibleCloseImageUrl() {
        return getModuleBase() + IMAGE_CLOSE_INVISIBLE;
    }

    public int getMinimumWidth() {
        int minimumWidth = 20;

        return headerWidget.getOffsetWidth() > minimumWidth ? headerWidget
                .getOffsetWidth() : minimumWidth;
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

    public String getWindowTitle() {
        return windowTitle;
    }

    public int getZIndex() {
        return DOM.getIntStyleAttribute(getElement(), CSS.Z_INDEX);
    }

    public void init(WindowController windowController, String title,
            Widget contentWidget) {

        initShowEvent();

        this.windowTitle = title;
        this.rootPanel = new FocusPanel();
        setWidget(this.rootPanel);

        this.windowController = windowController;

        rootPanel.addStyleName(CSS_WINDOW);

        this.headerWidget = new Label(title);
        headerWidget.addStyleName(CSS_WINDOW_HEADER_LABEL);

        this.contentWidget = contentWidget;

        HorizontalPanel headerBar = createHeaderBar();

        headerContainer = new FocusPanel();
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

        westTopWidget = setupCell(1, 0, WEST);
        grid.setWidget(1, 1, headerContainer);
        eastTopWidget = setupCell(1, 2, EAST);

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
            }
        });
        addEffect(showEffect);
    }

    public void moveBy(int right, int down) {
        AbsolutePanel parent = (AbsolutePanel) getParent();
        Location location = new WidgetLocation(this, parent);
        int left = location.getLeft() + right;
        int top = location.getTop() + down;
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
        updateToContentSize();
        playEffects();
    }

    public void setAbsoluteSize(int offsetWidth, int offsetHeight) {
        setPixelSize(offsetWidth, offsetHeight);

        int headerHeight = headerContainer.getOffsetHeight();
        int contentOffsetWidth = offsetWidth - 2 * BORDER_THICKNESS
                - WINDOW_BORDER_FROM_CSS;
        int contentOffsetHeight = offsetHeight - 2 * BORDER_THICKNESS
                - headerHeight - WINDOW_BORDER_FROM_CSS;

        contentWidget.setPixelSize(contentOffsetWidth, contentOffsetHeight);
        contentWidth = contentOffsetWidth;
        contentHeight = contentOffsetHeight;

        setBorderWidths(contentOffsetWidth, contentOffsetHeight, headerHeight);
    }

    private void setBorderWidths(int contentOffsetWidth,
            int contentOffsetHeight, int headerHeight) {
        headerContainer.setPixelSize(contentOffsetWidth - 4, headerWidget
                .getOffsetHeight()); // subtract header padding from
        // CSS
        // file

        northWidget.setPixelSize(contentOffsetWidth, BORDER_THICKNESS);
        southWidget.setPixelSize(contentOffsetWidth, BORDER_THICKNESS);
        westTopWidget.setPixelSize(BORDER_THICKNESS, headerHeight);
        westWidget.setPixelSize(BORDER_THICKNESS, contentOffsetHeight);
        eastTopWidget.setPixelSize(BORDER_THICKNESS, headerHeight);
        eastWidget.setPixelSize(BORDER_THICKNESS, contentOffsetHeight);
    }

    public void setContentSize(int width, int height) {
        // Log.debug("WindowPanel.setContentSize(" + width + "," + height +
        // ")");

        contentWidget.setPixelSize(width, height);
        contentWidth = width;
        contentHeight = height;

        // use offset for adjusting other widgets as it includes margin +
        // padding
        int contentOffsetWidth = contentWidget.getOffsetWidth();
        int contentOffsetHeight = contentWidget.getOffsetHeight();
        int headerHeight = headerContainer.getOffsetHeight();

        setBorderWidths(contentOffsetWidth, contentOffsetHeight, headerHeight);

        int maxInnerWidth = contentOffsetWidth;

        // header wider than content?
        if (headerContainer.getOffsetWidth() > maxInnerWidth) {
            maxInnerWidth = headerContainer.getOffsetWidth();
        }

        setPixelSize(maxInnerWidth + 2 * BORDER_THICKNESS
                + WINDOW_BORDER_FROM_CSS, contentOffsetHeight
                + headerWidget.getOffsetHeight() + 2 * BORDER_THICKNESS
                + WINDOW_BORDER_FROM_CSS);

        assert contentHeight == height;
        assert contentWidth == width;
    }

    public void setLocation(int x, int y) {
        AbsolutePanel parent = (AbsolutePanel) getParent();
        parent.setWidgetPosition(this, x, y);

        assert x == new WidgetLocation(this, getParent()).getLeft();
        assert y == new WidgetLocation(this, getParent()).getTop();
    }

    private Widget setupCell(int row, int col, DirectionConstant direction) {
        final FocusPanel widget = new FocusPanel();
        widget.setPixelSize(BORDER_THICKNESS, BORDER_THICKNESS);
        grid.setWidget(row, col, widget);
        windowController.getResizeDragController().makeDraggable(widget,
                direction);
        removeFromDragControllerOnDispose.add(widget);
        grid.getCellFormatter().addStyleName(
                row,
                col,
                CSS_WINDOW_RESIZE_EDGE + " window-resize-"
                        + direction.directionLetters);
        return widget;
    }

    public void setViewContent(WindowContent viewContent) {
        this.viewContent = viewContent;
    }

    public void setZIndex(final int zIndex) {
        // Bugfix: need to set zIndex manually because of timeline/firefox issue
        DOM.setIntStyleAttribute(getElement(), CSS.Z_INDEX, zIndex);
    }

    public void updateToContentSize() {
        int offsetHeight = contentWidget.getOffsetHeight();
        if (offsetHeight != 0) {
            // check for desktop size limitations
            int newHeight = Math.min(offsetHeight, getParent()
                    .getOffsetHeight()
                    - headerContainer.getOffsetHeight()
                    - (2 * BORDER_THICKNESS) - 2 // additional border
                    - 20 // FIXME bottom offset
                    - (getAbsoluteTop() - getParent().getAbsoluteTop()));

            setContentSize(contentWidget.getOffsetWidth(), newHeight);
        }
    }
}
