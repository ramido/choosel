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

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

// TODO test cases that test minimum sizes
public class WindowResizeControllerTest {

    private static class TestResizeablePanel implements ResizeablePanel {

        private int height;

        private int width;

        public TestResizeablePanel(int width, int height) {
            this.height = height;
            this.width = width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getMinimumWidth() {
            return 0;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public void moveBy(int right, int down) {
        }

        @Override
        public void setPixelSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }

    private ResizeablePanel panel;

    @Test
    public void eastLeft() {
        // TODO change draggable relative information to window-relative??
        WindowResizeController.resize(690, 600, 700, 600,
                ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).setPixelSize(190, 100);
    }

    @Test
    public void eastRight() {
        // TODO change draggable relative information to window-relative??
        WindowResizeController.resize(710, 600, 700, 600,
                ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).setPixelSize(210, 100);
    }

    @Test
    public void northDown() {
        WindowResizeController.resize(500, 610, 500, 600,
                ResizeablePanel.DIRECTION_NORTH, panel);

        verify(panel, times(1)).moveBy(0, 10);
        verify(panel, times(1)).setPixelSize(200, 90);
    }

    @Test
    public void northEastDownLeft() {
        WindowResizeController.resize(690, 610, 700, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).moveBy(0, 10);
        verify(panel, times(1)).setPixelSize(190, 90);
    }

    @Test
    public void northEastDownRight() {
        WindowResizeController.resize(710, 610, 700, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).moveBy(0, 10);
        verify(panel, times(1)).setPixelSize(210, 90);
    }

    @Test
    public void northEastUpLeft() {
        WindowResizeController.resize(690, 590, 700, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).moveBy(0, -10);
        verify(panel, times(1)).setPixelSize(190, 110);
    }

    @Test
    public void northEastUpRight() {
        WindowResizeController.resize(710, 590, 700, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).moveBy(0, -10);
        verify(panel, times(1)).setPixelSize(210, 110);
    }

    @Test
    public void northUp() {
        WindowResizeController.resize(500, 590, 500, 600,
                ResizeablePanel.DIRECTION_NORTH, panel);

        verify(panel, times(1)).moveBy(0, -10);
        verify(panel, times(1)).setPixelSize(200, 110);
    }

    @Test
    public void northWestDownLeft() {
        WindowResizeController.resize(490, 610, 500, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(0, 10);
        verify(panel, times(1)).moveBy(-10, 0);
        verify(panel, times(1)).setPixelSize(210, 90);
    }

    @Test
    public void northWestDownRight() {
        WindowResizeController.resize(510, 610, 500, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(0, 10);
        verify(panel, times(1)).moveBy(10, 0);
        verify(panel, times(1)).setPixelSize(190, 90);
    }

    @Test
    public void northWestUpLeft() {
        WindowResizeController.resize(490, 590, 500, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(0, -10);
        verify(panel, times(1)).moveBy(-10, 0);
        verify(panel, times(1)).setPixelSize(210, 110);
    }

    @Test
    public void northWestUpRight() {
        WindowResizeController.resize(510, 590, 500, 600,
                ResizeablePanel.DIRECTION_NORTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(0, -10);
        verify(panel, times(1)).moveBy(10, 0);
        verify(panel, times(1)).setPixelSize(190, 110);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        panel = spy(new TestResizeablePanel(200, 100));

        when(panel.getMinimumWidth()).thenReturn(0);
    }

    @Test
    public void southDown() {
        // TODO change draggable relative information to window-relative??
        WindowResizeController.resize(500, 710, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH, panel);

        verify(panel, times(1)).setPixelSize(200, 110);
    }

    @Test
    public void southEastDownLeft() {
        WindowResizeController.resize(690, 710, 700, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).setPixelSize(190, 110);
    }

    @Test
    public void southEastDownRight() {
        WindowResizeController.resize(710, 710, 700, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).setPixelSize(210, 110);
    }

    @Test
    public void southEastUpLeft() {
        WindowResizeController.resize(690, 690, 700, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).setPixelSize(190, 90);
    }

    @Test
    public void southEastUpRight() {
        WindowResizeController.resize(710, 690, 700, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_EAST, panel);

        verify(panel, times(1)).setPixelSize(210, 90);
    }

    @Test
    public void southUp() {
        // TODO change draggable relative information to window-relative??
        WindowResizeController.resize(500, 690, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH, panel);

        verify(panel, times(1)).setPixelSize(200, 90);
    }

    @Test
    public void southWestDownLeft() {
        WindowResizeController.resize(490, 710, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(-10, 0);
        verify(panel, times(1)).setPixelSize(210, 110);
    }

    @Test
    public void southWestDownRight() {
        WindowResizeController.resize(510, 710, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(10, 0);
        verify(panel, times(1)).setPixelSize(190, 110);
    }

    @Test
    public void southWestUpLeft() {
        WindowResizeController.resize(490, 690, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(-10, 0);
        verify(panel, times(1)).setPixelSize(210, 90);
    }

    @Test
    public void southWestUpRight() {
        WindowResizeController.resize(510, 690, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH
                        | ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(10, 0);
        verify(panel, times(1)).setPixelSize(190, 90);
    }

    @Test
    public void westLeft() {
        WindowResizeController.resize(490, 600, 500, 600,
                ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(-10, 0);
        verify(panel, times(1)).setPixelSize(210, 100);
    }

    @Test
    public void westRight() {
        WindowResizeController.resize(510, 600, 500, 600,
                ResizeablePanel.DIRECTION_WEST, panel);

        verify(panel, times(1)).moveBy(10, 0);
        verify(panel, times(1)).setPixelSize(190, 100);
    }
}
