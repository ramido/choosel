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
package org.thechiselgroup.choosel.client.ui.dnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.client.geometry.Rectangle;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.ZIndex;
import org.thechiselgroup.choosel.client.ui.shade.ShadeManager;
import org.thechiselgroup.choosel.client.util.RemoveHandle;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowPanel;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

// TODO move area calculation to preview package & use delegation instead
// (composition when creating drag avatar drag controller while setting
// up system)
public class DefaultResourceSetAvatarDragController extends
	AbstractDragController implements ResourceSetAvatarDragController {

    /**
     * Area for shade and drop target calculation.
     */
    private static class Area {

	// can be null, TODO introduce different area classes
	private ResourceSetAvatarDropController dropController;

	private Rectangle r;

	private WindowPanel window;

	public Area(Rectangle r, WindowPanel window,
		ResourceSetAvatarDropController dropController) {

	    assert r != null;
	    assert window != null;

	    this.r = r;
	    this.window = window;
	    this.dropController = dropController;
	}

	public ResourceSetAvatarDropController getDropController() {
	    return dropController;
	}

	private Rectangle getPartHiddenBy(Area windowArea) {
	    return r.intersection(windowArea.r);
	}

	public Rectangle getRectangle() {
	    return r;
	}

	public List<Area> getVisibleParts(List<Area> windowAreas) {
	    List<Rectangle> hiddenParts = new ArrayList<Rectangle>();

	    for (Area windowArea : windowAreas) {
		// FIXME potential bug if hidden by multiple windows
		if (isHiddenBy(windowArea)) {
		    hiddenParts.add(getPartHiddenBy(windowArea));
		}
	    }

	    List<Rectangle> visibleRectangles = r.removeRectangles(hiddenParts);
	    List<Area> result = new ArrayList<Area>();
	    for (Rectangle visibleRectangle : visibleRectangles) {
		result.add(new Area(visibleRectangle, window, dropController));
	    }

	    return result;
	}

	private boolean isHiddenBy(Area windowArea) {
	    return (window != windowArea.window)
		    && (window.getZIndex() < windowArea.window.getZIndex())
		    && (r.intersects(windowArea.r));
	}

	@Override
	public String toString() {
	    return r.toString();
	}

    }

    /**
     * TODO Decide if 100ms is a good number
     */
    private final static int CACHE_TIME_MILLIS = 100;

    private static void checkGWTIssue1813(Widget child, AbsolutePanel parent) {
	if (!GWT.isScript()) {
	    if (child.getElement().getOffsetParent() != parent.getElement()) {
		DOMUtil
			.reportFatalAndThrowRuntimeException("The boundary panel for this drag controller does not appear to have"
				+ " 'position: relative' CSS applied to it."
				+ " This may be due to custom CSS in your application, although this"
				+ " is often caused by using the result of RootPanel.get(\"some-unique-id\") as your boundary"
				+ " panel, as described in GWT issue 1813"
				+ " (http://code.google.com/p/google-web-toolkit/issues/detail?id=1813)."
				+ " Please star / vote for this issue if it has just affected your application."
				+ " You can often remedy this problem by adding one line of code to your application:"
				+ " boundaryPanel.getElement().getStyle().setProperty(\"position\", \"relative\");");
	    }
	}
    }

    private static List<Rectangle> toRectangles(List<Area> areas) {
	List<Rectangle> rectangles = new ArrayList<Rectangle>();
	for (Area dropArea : areas) {
	    rectangles.add(dropArea.getRectangle());
	}
	return rectangles;
    }

    /**
     * The implicit boundary drop controller.
     */
    private BoundaryDropController boundaryDropController;

    private Rectangle boundaryRectangle = new Rectangle(0, 0, 0, 0);

    private Desktop desktop;

    private Widget dragProxy;

    private Map<Widget, ResourceSetAvatarDropController> dropControllers = new HashMap<Widget, ResourceSetAvatarDropController>();

    private List<Widget> invisibleDropTargets = new ArrayList<Widget>();

    private long lastResetCacheTimeMillis;

    private ShadeManager shadeManager;

    private RemoveHandle shadeRemoveHandle;

    // used as shade background for drag avatar drop targets with rounded
    // corners
    private List<Element> shadeSpans = new ArrayList<Element>();

    private List<Area> visibleDropAreas;

    /**
     * Constructor for dependency injection --> TODO change to @Named tags
     */
    @Inject
    public DefaultResourceSetAvatarDragController(Desktop desktop,
	    ShadeManager shadeManager) {

	super(desktop.asWidget());

	assert shadeManager != null;

	this.shadeManager = shadeManager;
	this.desktop = desktop;
	this.boundaryDropController = new BoundaryDropController(desktop
		.asWidget(), false);

	setBehaviorDragStartSensitivity(2);
    }

    private void addDragAvatarDropTargetShadeSpans() {
	for (ResourceSetAvatarDropController dropController : dropControllers
		.values()) {
	    if (canDropOn(dropController)) {
		Widget dropTarget = dropController.getDropTarget();
		if (dropTarget instanceof ResourceSetAvatar) {
		    // inserts element as shaded background
		    Element e = (Element) dropTarget.getElement()
			    .getParentNode();
		    Element shade = DOM.createSpan();

		    WindowPanel window = getWindow(dropTarget);

		    shade.addClassName("shade");
		    ZIndex.setZIndex(shade, 1);

		    DOM.setStyleAttribute(shade, CSS.POSITION, CSS.ABSOLUTE);
		    DOM.setIntStyleAttribute(shade, CSS.LEFT, dropTarget
			    .getAbsoluteLeft()
			    - window.getAbsoluteLeft());
		    DOM.setIntStyleAttribute(shade, CSS.TOP, dropTarget
			    .getAbsoluteTop()
			    - window.getAbsoluteTop());
		    DOM.setIntStyleAttribute(shade, CSS.WIDTH, dropTarget
			    .getOffsetWidth());
		    DOM.setIntStyleAttribute(shade, CSS.HEIGHT, dropTarget
			    .getOffsetHeight());

		    e.appendChild(shade);

		    shadeSpans.add(shade);
		}
	    }
	}
    }

    private void addShade() {
	List<Rectangle> visibleRectangles = toRectangles(visibleDropAreas);
	shadeRemoveHandle = shadeManager.showShade(visibleRectangles);
    }

    private void calculateBoundaryOffset() {
	assert context.boundaryPanel == getBoundaryPanel();

	AbsolutePanel boundaryPanel = getBoundaryPanel();
	Location widgetLocation = new WidgetLocation(boundaryPanel, null);
	Element boundaryElement = boundaryPanel.getElement();

	int left = widgetLocation.getLeft()
		+ DOMUtil.getBorderLeft(boundaryElement);
	int top = widgetLocation.getTop()
		+ DOMUtil.getBorderTop(boundaryElement);
	boundaryRectangle = boundaryRectangle.move(left, top);

    }

    private void calculateBoundaryParameters() {
	calculateBoundaryOffset();

	Element element = getBoundaryPanel().getElement();

	int width = DOMUtil.getClientWidth(element);
	int height = DOMUtil.getClientHeight(element);
	boundaryRectangle = boundaryRectangle.resize(width, height);
    }

    // TODO change to calculate visible areas with reference to drop controller
    private List<Area> calculateDropAreas() {
	List<Area> windowAreas = getWindowAreas();
	List<Area> dropTargetAreas = getDropTargetAreas();

	List<Area> dropAreas = new ArrayList<Area>();
	for (Area dropArea : dropTargetAreas) {
	    dropAreas.addAll(dropArea.getVisibleParts(windowAreas));
	}

	return dropAreas;
    }

    private boolean canDropOn(ResourceSetAvatarDropController dropController) {
	return dropController.canDrop(context);
    }

    private void createMoveablePanel() {
	WidgetLocation currentDraggableLocation = new WidgetLocation(
		context.draggable, context.boundaryPanel);

	dragProxy = newDragProxy(context);
	context.boundaryPanel.add(dragProxy,
		currentDraggableLocation.getLeft(), currentDraggableLocation
			.getTop());
	checkGWTIssue1813(dragProxy, context.boundaryPanel);
	dragProxy.addStyleName(DragClientBundle.INSTANCE.css().movablePanel());
    }

    // XXX not safe for multiple simultaneous drags (e.g. multi touch
    // interfaces)
    @Override
    public void dragEnd() {
	removeDragAvatarDropTargetShadeSpans();

	// XXX HACK
	for (Widget w : invisibleDropTargets) {
	    w.setVisible(false);
	}
	invisibleDropTargets.clear();

	// old code
	shadeRemoveHandle.remove();

	visibleDropAreas = null;

	assert context.finalDropController == null == (context.vetoException != null);

	if (context.vetoException == null) {
	    context.dropController.onDrop(context);
	    context.dropController.onLeave(context);
	    context.dropController = null;
	}

	dragProxy.removeFromParent();
	dragProxy = null;

	super.dragEnd();
    }

    public void dragMove() {
	updateCacheAndBoundary();
	moveProxyElement();
	updateDropController();
    }

    // XXX not safe for multiple simultaneous drags (e.g. multi touch
    // interfaces)
    @Override
    public void dragStart() {
	super.dragStart();

	lastResetCacheTimeMillis = System.currentTimeMillis();

	createMoveablePanel();
	calculateBoundaryParameters();

	// XXX Hack: move to a better location
	for (ResourceSetAvatarDropController dropController : dropControllers
		.values()) {
	    if (canDropOn(dropController)) {
		Widget dropTarget = dropController.getDropTarget();
		if (!dropTarget.isVisible()) {
		    dropTarget.setVisible(true);
		    invisibleDropTargets.add(dropTarget);
		}
	    }
	}

	// old
	visibleDropAreas = calculateDropAreas();
	addShade();
	addDragAvatarDropTargetShadeSpans();
    }

    private ResourceSetAvatar getAvatar(DragContext context) {
	assert context != null;
	assert context.draggable != null;
	assert context.draggable instanceof ResourceSetAvatar;
	return (ResourceSetAvatar) context.draggable;
    }

    private int getDesiredLeft() {
	int desiredLeft = context.desiredDraggableX - boundaryRectangle.getX();
	if (getBehaviorConstrainedToBoundaryPanel()) {
	    desiredLeft = Math.max(0, Math.min(desiredLeft, boundaryRectangle
		    .getWidth()
		    - context.draggable.getOffsetWidth()));
	}
	return desiredLeft;
    }

    private int getDesiredTop() {
	int desiredTop = context.desiredDraggableY - boundaryRectangle.getY();
	if (getBehaviorConstrainedToBoundaryPanel()) {
	    desiredTop = Math.max(0, Math.min(desiredTop, boundaryRectangle
		    .getHeight()
		    - context.draggable.getOffsetHeight()));
	}
	return desiredTop;
    }

    /**
     * @param x
     *            offset left relative to document body
     * @param y
     *            offset top relative to document body
     * @return a drop controller for the intersecting drop target or
     *         <code>null</code> if none are applicable
     */
    private DropController getDropControllerForLocation(int x, int y) {
	// our rectangles/areas have absolute offsets so we are good
	// since we already calculated the visible ones, we don't need ordering

	for (Area area : visibleDropAreas) {
	    if (area.getRectangle().contains(x, y)) {
		return area.getDropController();
	    }
	}

	return boundaryDropController;
    }

    private List<Area> getDropTargetAreas() {
	List<Area> areas = new ArrayList<Area>();
	for (ResourceSetAvatarDropController dropController : dropControllers
		.values()) {
	    if (canDropOn(dropController)) {
		Widget dropTarget = dropController.getDropTarget();
		Rectangle rectangle = Rectangle.fromWidget(dropTarget);
		WindowPanel window = getWindow(dropTarget);

		areas.add(new Area(rectangle, window, dropController));
	    }
	}
	return areas;
    }

    private WindowPanel getWindow(Widget originalWidget) {
	assert originalWidget != null;

	Widget widget = originalWidget;
	while (widget != null) {
	    if (widget instanceof WindowPanel) {
		return (WindowPanel) widget;
	    }
	    widget = widget.getParent();
	}

	throw new RuntimeException("no window found for widget "
		+ originalWidget);
    }

    private List<Area> getWindowAreas() {
	List<Area> windowAreas = new ArrayList<Area>();
	List<WindowPanel> windows = desktop.getWindows();
	for (WindowPanel window : windows) {
	    Rectangle r = Rectangle.fromWidget(window);
	    windowAreas.add(new Area(r, window, null));
	}
	return windowAreas;
    }

    private void moveProxyElement() {
	Style style = dragProxy.getElement().getStyle();

	style.setPropertyPx(CSS.LEFT, getDesiredLeft());
	style.setPropertyPx(CSS.TOP, getDesiredTop());
    }

    protected Widget newDragProxy(DragContext context) {
	return getAvatar(context).createProxy();
    }

    // copied from PickupDragController
    @Override
    public void previewDragEnd() throws VetoDragException {
	assert context.finalDropController == null;
	assert context.vetoException == null;
	try {
	    try {
		// may throw VetoDragException
		context.dropController.onPreviewDrop(context);
		context.finalDropController = context.dropController;
	    } finally {
		// may throw VetoDragException
		super.previewDragEnd();
	    }
	} catch (VetoDragException ex) {
	    context.finalDropController = null;
	    throw ex;
	}
    }

    public void registerDropController(
	    ResourceSetAvatarDropController dropController) {
	dropControllers.put(dropController.getDropTarget(), dropController);
    }

    private void removeDragAvatarDropTargetShadeSpans() {
	for (Element e : shadeSpans) {
	    e.removeFromParent();
	}
	shadeSpans.clear();
    }

    @Override
    public void setDraggable(Widget widget, boolean draggable) {
	if (draggable) {
	    makeDraggable(widget);
	} else {
	    makeNotDraggable(widget);
	}
    }

    public void unregisterDropController(
	    ResourceSetAvatarDropController dropController) {
	dropControllers.remove(dropController.getDropTarget());
    }

    public void unregisterDropControllerFor(Widget dropTarget) {
	unregisterDropController(dropControllers.get(dropTarget));
    }

    private void updateCacheAndBoundary() {
	// may have changed due to scrollIntoView(), developer driven changes
	// or manual user scrolling
	long timeMillis = System.currentTimeMillis();
	if (timeMillis - lastResetCacheTimeMillis >= CACHE_TIME_MILLIS) {
	    lastResetCacheTimeMillis = timeMillis;
	    resetCache();
	    calculateBoundaryOffset();
	}
    }

    private void updateDropController() {
	DropController newDropController = getDropControllerForLocation(
		context.mouseX, context.mouseY);

	if (context.dropController != newDropController) {
	    if (context.dropController != null) {
		context.dropController.onLeave(context);
	    }
	    context.dropController = newDropController;
	    if (context.dropController != null) {
		context.dropController.onEnter(context);
	    }
	}

	if (context.dropController != null) {
	    context.dropController.onMove(context);
	}
    }

}