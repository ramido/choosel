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
/**
 * 
 */
package org.thechiselgroup.choosel.client.ui.dialog;

import org.thechiselgroup.choosel.client.command.NullCommandManager;
import org.thechiselgroup.choosel.client.ui.ActionBar;
import org.thechiselgroup.choosel.client.ui.shade.ShadeManager;
import org.thechiselgroup.choosel.client.util.RemoveHandle;
import org.thechiselgroup.choosel.client.windows.AbstractWindowController;
import org.thechiselgroup.choosel.client.windows.WindowPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class DialogController extends AbstractWindowController {

    public static enum State {
        CANCELED, FINISHED, RUNNING
    }

    private Dialog dialog;

    private RemoveHandle shadeHandle;

    private ShadeManager shadeManager;

    DialogController(AbsolutePanel boundaryPanel, Dialog dialog,
            ShadeManager shadeManager) {

        super(boundaryPanel, new NullCommandManager());

        this.dialog = dialog;
        this.shadeManager = shadeManager;
    }

    public void cancelDialog(DialogWindow window) {
        window.setState(State.CANCELED);
        window.close();
    }

    @Override
    public void close(WindowPanel window) {
        assert window instanceof DialogWindow;

        getBoundaryPanel().remove(window);

        State state = ((DialogWindow) window).getState();
        if (State.CANCELED.equals(state)) {
            dialog.cancel();
        } else if (State.FINISHED.equals(state)) {
            dialog.okay();
        }

        hideShade();
    }

    public void finishDialog(DialogWindow window) {
        window.setState(State.FINISHED);
        window.close();
    }

    private void hideShade() {
        shadeHandle.remove();
    }

    public void init() {
        showShade();

        final DialogWindow dialogWindow = new DialogWindow();

        // initialization order important (breaks otherwise)
        dialogWindow.init(this, dialog);
        dialog.init(dialogWindow);

        shadeManager.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelDialog(dialogWindow);
            }
        });

        getBoundaryPanel().add(dialogWindow);

        // display centered below action bar
        // offsets are useless here -- why? -- use content instead (not
        // exact)
        Log.debug("windowOffsetWidth: " + dialogWindow.getOffsetWidth());
        Log.debug("windowContentWidth: " + dialogWindow.getContentWidth());

        int x = (getBoundaryPanel().getOffsetWidth() - dialogWindow
                .getContentWidth()) / 2;

        // TODO extract offset (variable)
        dialogWindow.setLocation(x, ActionBar.ACTION_BAR_HEIGHT_PX + 10);
    }

    private void showShade() {
        shadeHandle = shadeManager.showShade();
    }
}