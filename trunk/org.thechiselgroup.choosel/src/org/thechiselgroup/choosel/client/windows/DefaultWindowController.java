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

/*
 * This class implements algorithms related to WindowPanel and executes them on callback
 * methods. The intention of this class is to enable the testing of error-prone areas
 * of the Window framework. 
 */
public class DefaultWindowController implements WindowController {

    private WindowCallback callback;

    public DefaultWindowController(WindowCallback callback) {
        assert callback != null;

        this.callback = callback;
    }

    @Override
    public int getHeight() {
        return callback.getHeight();
    }

    @Override
    public int getWidth() {
        return callback.getWidth();
    }

    @Override
    public void resize(int deltaX, int deltaY, int targetWidth, int targetHeight) {
        assert targetWidth >= 0;
        assert targetHeight >= 0;

        callback.setPixelSize(targetWidth, targetHeight);

        int newWidth = getWidth();
        int newHeight = getHeight();

        /*
         * adjust move to the extend the resizing worked
         */
        if (deltaX != 0) {
            deltaX += targetWidth - newWidth;
        }

        if (deltaY != 0) {
            deltaY += targetHeight - newHeight;
        }

        callback.moveBy(deltaX, deltaY);
    }

}