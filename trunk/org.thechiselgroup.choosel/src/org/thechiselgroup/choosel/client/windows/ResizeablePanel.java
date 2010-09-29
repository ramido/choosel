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

public interface ResizeablePanel {

    /**
     * WindowPanel direction constant, used in
     * {@link WindowResizeController#makeDraggable(com.google.gwt.user.client.ui.Widget, org.thechiselgroup.Direction.client.windows.demo.client.example.resize.WindowPanel.DirectionConstant)}
     * .
     */
    // TODO full class, include direction constants, maybe enum
    public static class Direction {

        private final int directionBits;

        public final String directionLetters;

        private Direction(int directionBits, String directionLetters) {
            this.directionBits = directionBits;
            this.directionLetters = directionLetters;
        }

        public boolean isEast() {
            return (directionBits & DIRECTION_EAST) != 0;
        }

        public boolean isNorth() {
            return (directionBits & DIRECTION_NORTH) != 0;
        }

        public boolean isSouth() {
            return (directionBits & DIRECTION_SOUTH) != 0;
        }

        public boolean isWest() {
            return (directionBits & DIRECTION_WEST) != 0;
        }
    }

    /**
     * Specifies that resizing occur at the east edge.
     */
    int DIRECTION_EAST = 0x0001;

    /**
     * Specifies that resizing occur at the both edge.
     */
    int DIRECTION_NORTH = 0x0002;

    /**
     * Specifies that resizing occur at the south edge.
     */
    int DIRECTION_SOUTH = 0x0004;

    /**
     * Specifies that resizing occur at the west edge.
     */
    int DIRECTION_WEST = 0x0008;

    /**
     * Specifies that resizing occur at the east edge.
     */
    Direction EAST = new Direction(DIRECTION_EAST, "e");

    Direction EAST_TOP = new Direction(DIRECTION_EAST, "et");

    /**
     * Specifies that resizing occur at the both edge.
     */
    Direction NORTH = new Direction(DIRECTION_NORTH, "n");

    /**
     * Specifies that resizing occur at the north-east edge.
     */
    Direction NORTH_EAST = new Direction(DIRECTION_NORTH | DIRECTION_EAST, "ne");

    /**
     * Specifies that resizing occur at the north-west edge.
     */
    Direction NORTH_WEST = new Direction(DIRECTION_NORTH | DIRECTION_WEST, "nw");

    /**
     * Specifies that resizing occur at the south edge.
     */
    Direction SOUTH = new Direction(DIRECTION_SOUTH, "s");

    /**
     * Specifies that resizing occur at the south-east edge.
     */
    Direction SOUTH_EAST = new Direction(DIRECTION_SOUTH | DIRECTION_EAST, "se");

    /**
     * Specifies that resizing occur at the south-west edge.
     */
    Direction SOUTH_WEST = new Direction(DIRECTION_SOUTH | DIRECTION_WEST, "sw");

    /**
     * Specifies that resizing occur at the west edge.
     */
    Direction WEST = new Direction(DIRECTION_WEST, "w");

    Direction WEST_TOP = new Direction(DIRECTION_WEST, "wt");

    int getHeight();

    int getWidth();

    void moveBy(int relativeX, int relativeY);

    void setPixelSize(int width, int height);

}