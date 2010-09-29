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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WindowResizeControllerTest {

    @Mock
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
    public void northUp() {
        WindowResizeController.resize(500, 590, 500, 600,
                ResizeablePanel.DIRECTION_NORTH, panel);

        verify(panel, times(1)).moveBy(0, -10);
        verify(panel, times(1)).setPixelSize(200, 110);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(panel.getHeight()).thenReturn(100);
        when(panel.getWidth()).thenReturn(200);
    }

    @Test
    public void southDown() {
        // TODO change draggable relative information to window-relative??
        WindowResizeController.resize(500, 710, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH, panel);

        verify(panel, times(1)).setPixelSize(200, 110);
    }

    @Test
    public void southUp() {
        // TODO change draggable relative information to window-relative??
        WindowResizeController.resize(500, 690, 500, 700,
                ResizeablePanel.DIRECTION_SOUTH, panel);

        verify(panel, times(1)).setPixelSize(200, 90);
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
