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
package org.thechiselgroup.choosel.core.client.ui.popup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.test.TestMouseOverEvent;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;

public class DefaultPopupManagerTest {

    public static class TestDefaultPopupManager extends DefaultPopupManager {

        public TestDefaultPopupManager(Popup popup) {
            super(popup);
        }

        @Override
        protected void cancelTimer() {
        }

        @Override
        protected Timer createTimer() {
            return null;
        }

        @Override
        protected void startTimer(int delayInMs) {
        }

    }

    @Mock
    private Popup popup;

    private DefaultPopupManager underTest;

    @Test
    public void linkRegistersCalls() {
        HasAllMouseHandlers source = mock(HasAllMouseHandlers.class);

        underTest.linkToWidget(source);

        ArgumentCaptor<MouseOverHandler> argument = ArgumentCaptor
                .forClass(MouseOverHandler.class);
        verify(source, times(1)).addMouseOverHandler(argument.capture());

        argument.getValue().onMouseOver(new TestMouseOverEvent(0, 0));

        verify(underTest, times(1)).onMouseOver(0, 0);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        underTest = spy(new TestDefaultPopupManager(popup));
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }
}
