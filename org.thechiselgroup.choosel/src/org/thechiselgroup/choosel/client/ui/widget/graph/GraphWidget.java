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
package org.thechiselgroup.choosel.client.ui.widget.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.util.ArrayUtils;

import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class GraphWidget extends SWFWidget implements GraphDisplay {

    public static class Location extends JavaScriptObject {

	protected Location() {
	}

	public final native double getX() /*-{
            return this.x;
        }-*/;

	public final native double getY() /*-{
            return this.y;
        }-*/;

    }

    private static final String CSS_GRAPH_LOADING_INFO = "graph-loading-info";

    public static final String FLASH_VAR_SWFID = "swfid";

    private final static String LAYOUT_NAME = "Force Directed Layout";

    public static final String SWF_FILE = GWT.getModuleBaseURL()
	    + "swf/BasicGraph.swf";

    private static Map<String, GraphWidget> widgets = new HashMap<String, GraphWidget>();

    static {
	try {
	    exportStaticMethods();
	} catch (Exception ex) {
	    Log.error(ex.getMessage(), ex);
	}
    }

    private static native void _addArc(String swfID, String arcId,
	    String sourceNodeId, String targetNodeId, String type) /*-{
        $doc.getElementById(swfID).addArc(arcId, sourceNodeId, targetNodeId, type);
    }-*/;

    private static native void _addNode(String swfID, String id, String type,
	    String label) /*-{
        $doc.getElementById(swfID).addNode(id, type, label);
        // $doc.getElementById(swfID).positionUnconnectedNode(id);
    }-*/;

    private static native void _addNodeMenuItem(String swfID, String itemId,
	    String itemLabel, String nodeType) /*-{
        $doc.getElementById(swfID).addNodeMenuItem("_flexvis_onNodeMenuItemClicked", itemId, itemLabel, nodeType);
    }-*/;

    private static GraphWidget _getGraphWidgetByID(String swfID) {
	return GraphWidget.widgets.get(swfID);
    }

    private static native Location _getNodeLocation(String swfID, String nodeId) /*-{
        return $doc.getElementById(swfID).getNodeLocation(nodeId);
    }-*/;

    private static native String _getSelectedNodeID(String swfID) /*-{
        return $doc.getElementById(swfID).getSelectedNodeID();
    }-*/;

    public static void _log(String message) {
	Log.debug(message);
    }

    public static void _onArcMouseClick(String arcID, int mouseX, int mouseY,
	    String swfID) {
	_getGraphWidgetByID(swfID).onArcMouseClick(arcID, mouseX, mouseY);
    }

    public static void _onArcMouseDoubleClick(String arcID, int mouseX,
	    int mouseY, String swfID) {
	_getGraphWidgetByID(swfID).onArcMouseDoubleClick(arcID, mouseX, mouseY);
    }

    public static void _onArcMouseOut(String arcID, int mouseX, int mouseY,
	    String swfID) {
	_getGraphWidgetByID(swfID).onArcMouseOut(arcID, mouseX, mouseY);
    }

    public static void _onArcMouseOver(String arcID, int mouseX, int mouseY,
	    String swfID) {
	_getGraphWidgetByID(swfID).onArcMouseOver(arcID, mouseX, mouseY);
    }

    public static void _onLoad(String swfID) {
	try {
	    _registerFlexHooks(swfID);
	    _getGraphWidgetByID(swfID).onWidgetReady();
	} catch (Exception ex) {
	    Log.error(ex.getMessage(), ex);
	}
    }

    public static void _onNodeDrag(String nodeID, int startX, int startY,
	    int endX, int endY, String swfID) {

	_getGraphWidgetByID(swfID).onNodeDrag(nodeID, startX, startY, endX,
		endY);
    }

    public static void _onNodeDragHandleMouseDown(String nodeID, int mouseX,
	    int mouseY, String swfID) {

	_getGraphWidgetByID(swfID).onNodeDragHandleMouseDown(nodeID, mouseX,
		mouseY);
    }

    public static void _onNodeDragHandleMouseMove(String nodeID, int mouseX,
	    int mouseY, String swfID) {

	_getGraphWidgetByID(swfID).onNodeDragHandleMouseMove(nodeID, mouseX,
		mouseY);
    }

    public static void _onNodeMenuItemClicked(String itemId, String nodeLabel,
	    String nodeId, int x, int y, String swfID) {
	_getGraphWidgetByID(swfID).onNodeMenuItemClicked(itemId, nodeId);
    }

    public static void _onNodeMouseClick(String nodeID, int mouseX, int mouseY,
	    String swfID) {
	_getGraphWidgetByID(swfID).onNodeMouseClick(nodeID, mouseX, mouseY);
    }

    public static void _onNodeMouseDoubleClick(String nodeID, int mouseX,
	    int mouseY, String swfID) {
	_getGraphWidgetByID(swfID).onNodeMouseDoubleClick(nodeID, mouseX,
		mouseY);
    }

    public static void _onNodeMouseOut(String nodeID, int mouseX, int mouseY,
	    String swfID) {
	_getGraphWidgetByID(swfID).onNodeMouseOut(nodeID, mouseX, mouseY);
    }

    public static void _onNodeMouseOver(String nodeID, int mouseX, int mouseY,
	    String swfID) {
	_getGraphWidgetByID(swfID).onNodeMouseOver(nodeID, mouseX, mouseY);
    }

    private static native void _registerFlexHooks(String swfID) /*-{
        var flexWidget = $doc.getElementById(swfID);

        flexWidget.addNodeMouseOverListener("_flexvis_nodeMouseOver");
        flexWidget.addNodeMouseOutListener("_flexvis_nodeMouseOut");
        flexWidget.addNodeMouseClickListener("_flexvis_nodeMouseClick");
        flexWidget.addNodeMouseDoubleClickListener("_flexvis_nodeMouseDoubleClick");

        flexWidget.addNodeDragHandleMouseDownListener("_flexvis_onNodeDragHandleMouseDown");
        flexWidget.addNodeDragHandleMouseMoveListener("_flexvis_onNodeDragHandleMouseMove");

        flexWidget.addArcMouseOverListener("_flexvis_arcMouseOver");
        flexWidget.addArcMouseOutListener("_flexvis_arcMouseOut");
        flexWidget.addArcMouseClickListener("_flexvis_arcMouseClick");
        flexWidget.addArcMouseDoubleClickListener("_flexvis_arcMouseDoubleClick");

        flexWidget.addNodeDragListener("_flexvis_nodeDrag");
    }-*/;

    private static native void _removeArc(String swfID, String arcId) /*-{
        $doc.getElementById(swfID).removeArc(arcId);
    }-*/;

    private static native void _removeNode(String swfID, String nodeId) /*-{
        $doc.getElementById(swfID).removeNode(nodeId);
    }-*/;

    private static native void _runLayout(String swfID, String layoutName) /*-{
        $doc.getElementById(swfID).runLayout(layoutName);
    }-*/;

    /**
     * @param nodeIds
     *            JavaScript array with node ids (cannot use java array - this
     *            is opaque to java script)
     */
    private static native void _runLayout(String swfID, String layoutName,
	    JavaScriptObject nodeIds) /*-{
        $doc.getElementById(swfID).runLayout(layoutName, nodeIds);
    }-*/;

    private static native void _setArcStyle(String swfID, String arcId,
	    String styleProp, String styleValue) /*-{
        $doc.getElementById(swfID).setArcStyle(arcId, styleProp, styleValue);
    }-*/;

    private static native Location _setNodeLocation(String swfID,
	    String nodeId, int x, int y) /*-{
        return $doc.getElementById(swfID).setNodeLocation(nodeId, x, y);
    }-*/;

    private static native void _setNodeLocation(String swfID, String nodeId,
	    int x, int y, boolean animate) /*-{
        $doc.getElementById(swfID).setNodeLocation(nodeId, x, y, animate);
    }-*/;

    /*
     * public function setNodeStyle(nodeID:String, styleProp:String,
     * styleValue:Object):void
     */
    private static native void _setNodeStyle(String swfID, String nodeId,
	    String styleProp, String styleValue) /*-{
        $doc.getElementById(swfID).setNodeStyle(nodeId, styleProp, styleValue);
    }-*/;

    private static native void exportStaticMethods() /*-{
        $wnd._flexvis_loaded=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onLoad(Ljava/lang/String;);

        $wnd._flexvis_log=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_log(Ljava/lang/String;);

        $wnd._flexvis_nodeMouseOver=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeMouseOver(Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_nodeMouseOut=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeMouseOut(Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_nodeMouseClick=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeMouseClick(Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_nodeMouseDoubleClick=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeMouseDoubleClick(Ljava/lang/String;IILjava/lang/String;);

        $wnd._flexvis_arcMouseOver=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onArcMouseOver(Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_arcMouseOut=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onArcMouseOut(Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_arcMouseClick=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onArcMouseClick(Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_arcMouseDoubleClick=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onArcMouseDoubleClick(Ljava/lang/String;IILjava/lang/String;);

        $wnd._flexvis_onNodeMenuItemClicked=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeMenuItemClicked(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;);
        $wnd._flexvis_onNodeDragHandleMouseDown=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeDragHandleMouseDown(Ljava/lang/String;IILjava/lang/String;)
        $wnd._flexvis_onNodeDragHandleMouseMove=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeDragHandleMouseMove(Ljava/lang/String;IILjava/lang/String;)

        $wnd._flexvis_nodeDrag=
        @org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget::_onNodeDrag(Ljava/lang/String;IIIILjava/lang/String;);
    }-*/;

    private Map<String, Arc> arcsByID = new HashMap<String, Arc>();

    private Element loadingInfoDiv;

    private Map<String, NodeMenuItemClickedHandler> nodeMenuItemClickHandlers = new HashMap<String, NodeMenuItemClickedHandler>();

    private int nodeMenuItemIdCounter = 0;

    private Map<String, Node> nodesByID = new HashMap<String, Node>();

    public GraphWidget(int width, int height) {
	super(SWF_FILE, width, height);

	addAttribute("wmode", "transparent");

	// hack around IE / FF differences with Flash embedding
	addFlashVar(FLASH_VAR_SWFID, getSwfId());
    }

    public void addArc(Arc arc) {
	arcsByID.put(arc.getId(), arc);
	_addArc(getSwfId(), arc.getId(), arc.getSourceNodeId(), arc
		.getTargetNodeId(), arc.getType());
    }

    public <T extends EventHandler> HandlerRegistration addEventHandler(
	    Type<T> type, T handler) {

	if (type instanceof DomEvent.Type) {
	    return addDomHandler(handler, (DomEvent.Type) type);
	} else {
	    return addHandler(handler, type);
	}
    }

    public HandlerRegistration addGraphWidgetReadyHandler(
	    GraphWidgetReadyHandler handler) {

	return addHandler(handler, GraphWidgetReadyEvent.TYPE);
    }

    public void addNode(Node node) {
	_addNode(getSwfId(), node.getId(), node.getType(), node.getLabel());
	nodesByID.put(node.getId(), node);
    }

    public void addNodeMenuItemHandler(String menuLabel,
	    NodeMenuItemClickedHandler handler, String nodeType) {

	String id = "menuItemId-" + (nodeMenuItemIdCounter++);
	nodeMenuItemClickHandlers.put(id, handler);

	_addNodeMenuItem(getSwfId(), id, menuLabel, nodeType);
    }

    @Override
    public void animateMoveTo(Node node, Point targetLocation) {
	_setNodeLocation(getSwfId(), node.getId(), targetLocation.x,
		targetLocation.y, true);

    }

    @Override
    public Widget asWidget() {
	return this;
    }

    private void attachLoadingStateInformation() {
	loadingInfoDiv = DOM.createDiv();
	loadingInfoDiv.setInnerText("Graph viewer loading...");
	loadingInfoDiv.addClassName(CSS_GRAPH_LOADING_INFO);
	getElement().appendChild(loadingInfoDiv);
    }

    @Override
    public boolean containsArc(String arcId) {
	assert arcId != null;
	return arcsByID.containsKey(arcId);
    }

    @Override
    public boolean containsNode(String nodeId) {
	assert nodeId != null;
	return nodesByID.containsKey(nodeId);
    }

    private void detachLoadingStateInformation() {
	getElement().removeChild(loadingInfoDiv);
    }

    @Override
    public Arc getArc(String arcId) {
	assert arcId != null;
	assert arcsByID.containsKey(arcId);
	return arcsByID.get(arcId);
    }

    public Arc getArcByID(String arcID) {
	return arcsByID.get(arcID);
    }

    public Point getLocation(Node node) {
	Location result = _getNodeLocation(getSwfId(), node.getId());
	return new Point((int) result.getX(), (int) result.getY());
    }

    @Override
    public Node getNode(String nodeId) {
	assert nodeId != null;
	assert nodesByID.containsKey(nodeId);
	return nodesByID.get(nodeId);
    }

    public Node getNodeByID(String nodeID) {
	return nodesByID.get(nodeID);
    }

    public String getSelectedNodeID() {
	return _getSelectedNodeID(getSwfId());
    }

    public void layOut() {
	_runLayout(getSwfId(), LAYOUT_NAME);
    }

    public void layOutNodes(Collection<Node> nodes) {
	if (nodes.size() == 0) {
	    return;
	}

	_runLayout(getSwfId(), LAYOUT_NAME, ArrayUtils
		.toJsArray(toNodeIdArray(nodes)));
    }

    private void onArcMouseClick(String arcID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new ArcMouseClickEvent(getArcByID(arcID), x, y));
    }

    private void onArcMouseDoubleClick(String arcID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new ArcMouseDoubleClickEvent(getArcByID(arcID), x, y));
    }

    private void onArcMouseOut(String arcID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new ArcMouseOutEvent(getArcByID(arcID), x, y));
    }

    private void onArcMouseOver(String arcID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new ArcMouseOverEvent(getArcByID(arcID), x, y));
    }

    @Override
    protected void onLoad() {
	super.onLoad();

	attachLoadingStateInformation();
	GraphWidget.widgets.put(getSwfId(), this);
    }

    private void onNodeDrag(String nodeID, int startX, int startY, int endX,
	    int endY) {

	fireEvent(new NodeDragEvent(getNodeByID(nodeID), startX, startY, endX,
		endY));
    }

    private void onNodeDragHandleMouseMove(String nodeID, int mouseX, int mouseY) {
	fireEvent(new NodeDragHandleMouseMoveEvent(getNodeByID(nodeID), mouseX,
		mouseY));
    }

    private void onNodeDragHandleMouseDown(String nodeID, int mouseX, int mouseY) {
	fireEvent(new NodeDragHandleMouseDownEvent(getNodeByID(nodeID), mouseX,
		mouseY));
    }

    private void onNodeMenuItemClicked(String itemId, String nodeId) {
	assert itemId != null;
	assert nodeId != null;

	Node node = getNode(nodeId);
	assert node != null;

	NodeMenuItemClickedHandler handler = nodeMenuItemClickHandlers
		.get(itemId);
	assert handler != null;

	handler.onNodeMenuItemClicked(node);
    }

    // TODO remove arc

    private void onNodeMouseClick(String nodeID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new NodeMouseClickEvent(getNodeByID(nodeID), x, y));
    }

    private void onNodeMouseDoubleClick(String nodeID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new NodeMouseDoubleClickEvent(getNodeByID(nodeID), x, y));
    }

    private void onNodeMouseOut(String nodeID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new NodeMouseOutEvent(getNodeByID(nodeID), x, y));
    }

    private void onNodeMouseOver(String nodeID, int mouseX, int mouseY) {
	int x = getAbsoluteLeft() + mouseX;
	int y = getAbsoluteTop() + mouseY;

	fireEvent(new NodeMouseOverEvent(getNodeByID(nodeID), x, y));
    }

    @Override
    protected void onUnload() {
	GraphWidget.widgets.remove(getSwfId());

	super.onUnload();
    }

    private void onWidgetReady() {
	detachLoadingStateInformation();
	fireEvent(new GraphWidgetReadyEvent(this));
    }

    public void removeArc(Arc arc) {
	_removeArc(getSwfId(), arc.getId());
	arcsByID.remove(arc.getId());
    }

    public void removeNode(Node node) {
	_removeNode(getSwfId(), node.getId());
	nodesByID.remove(node.getId());
    }

    @Override
    public void setArcStyle(Arc arc, String styleProperty, String styleValue) {
	_setArcStyle(getSwfId(), arc.getId(), styleProperty, styleValue);
    }

    public void setLocation(Node node, Point point) {
	_setNodeLocation(getSwfId(), node.getId(), point.x, point.y);
    }

    public void setNodeStyle(Node node, String styleProperty, String styleValue) {
	_setNodeStyle(getSwfId(), node.getId(), styleProperty, styleValue);
    }

    private String[] toNodeIdArray(Collection<Node> nodes) {
	String[] nodeIds = new String[nodes.size()];
	int i = 0;
	for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
	    nodeIds[i++] = it.next().getId();

	}
	return nodeIds;
    }

}