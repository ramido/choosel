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
package org.thechiselgroup.choosel.visualization_component.graph.client;

import org.thechiselgroup.choosel.core.client.command.UndoableCommand;
import org.thechiselgroup.choosel.core.client.geometry.Point;
import org.thechiselgroup.choosel.core.client.util.HasDescription;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.Node;

public class MoveNodeCommand implements UndoableCommand, HasDescription {

    private final GraphDisplay graphDisplay;

    private final Node node;

    private final Point sourceLocation;

    private final Point targetLocation;

    public MoveNodeCommand(GraphDisplay graphDisplay, Node node,
            Point sourceLocation, Point targetLocation) {

        assert graphDisplay != null;
        assert node != null;
        assert sourceLocation != null;
        assert targetLocation != null;

        this.graphDisplay = graphDisplay;
        this.node = node;
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
    }

    @Override
    public void execute() {
        graphDisplay.animateMoveTo(node, targetLocation);
    }

    @Override
    public String getDescription() {
        return "Move node '" + node.getLabel() + "' to " + targetLocation;
    }

    public GraphDisplay getGraphDisplay() {
        return graphDisplay;
    }

    public Node getNode() {
        return node;
    }

    public Point getSourceLocation() {
        return sourceLocation;
    }

    public Point getTargetLocation() {
        return targetLocation;
    }

    @Override
    public void undo() {
        graphDisplay.animateMoveTo(node, sourceLocation);
    }

}
