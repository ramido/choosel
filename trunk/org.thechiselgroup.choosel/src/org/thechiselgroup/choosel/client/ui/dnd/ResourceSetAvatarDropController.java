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

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.ui.WidgetUtils;
import org.thechiselgroup.choosel.client.ui.popup.DefaultDelayedPopup;
import org.thechiselgroup.choosel.client.ui.popup.DelayedPopup;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.views.DefaultViewAccessor;
import org.thechiselgroup.choosel.client.views.View;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceSetAvatarDropController extends SimpleDropController {

    private ResourceSetAvatarDropCommandFactory commandFactory;

    private CommandManager commandManager;

    private UndoableCommand executedCommand;

    private DelayedPopup popup = null;

    public ResourceSetAvatarDropController(Widget dropTarget,
            ResourceSetAvatarDropCommandFactory commandFactory,
            CommandManager commandManager) {

        super(dropTarget);

        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    // TODO prevent drag source self-drop
    public boolean canDrop(DragContext context) {
        if (!(context.draggable instanceof ResourceSetAvatar)) {
            return false;
        }

        ResourceSetAvatar avatar = getAvatar(context);

        // FIXME: create interface on view content displays that checks if
        // resource set can be displayed, use that interface from drop command
        // factories
        if (isConceptToTimelineDrop(avatar, context)) {
            return false;
        }

        return commandFactory.canDrop(avatar);
    }

    private UndoableCommand createCommand(final DragContext context) {
        return commandFactory.createCommand(getAvatar(context));
    }

    // protected for testing only
    // TODO move to factory
    protected DelayedPopup createPopup(final DragContext context, String message) {
        DefaultDelayedPopup popup = new DefaultDelayedPopup(800, 200) {
            @Override
            protected void updatePosition() {
                setPopupPosition(context.mouseX + 15, context.mouseY + 20);
            }
        };

        WidgetUtils.setMaxWidth(popup, 250);
        popup.setWidget(new HTML(message));

        return popup;
    }

    private void executeCommand(UndoableCommand command) {
        command.execute();
        executedCommand = command;
    }

    private ResourceSetAvatar getAvatar(DragContext context) {
        return (ResourceSetAvatar) context.draggable;
    }

    private boolean hasCommandBeenExecuted() {
        return executedCommand != null;
    }

    private void hidePopup() {
        if (popup != null) {
            popup.hideDelayed();
            popup = null;
        }
    }

    // FIXME: create interface on view content displays that checks if
    // resource set can be displayed, use that interface from drop command
    // factories
    private boolean isConceptToTimelineDrop(ResourceSetAvatar avatar,
            DragContext context) {

        // HERE: test if all resources are concepts & target it timeline
        // REMOVE THIS!

        ResourceSet resourceSet = avatar.getResourceSet();
        if (resourceSet == null) {
            return false;// for test
        }

        for (Resource resource : resourceSet) {
            if (!resource.getUri().startsWith("ncbo-concept")) {
                return false;
            }
        }

        View view = new DefaultViewAccessor().findView(getDropTarget());
        if (view == null) {
            return false;
        }

        return view.getContentType().equals("Timeline");
    }

    // TODO support dragging multiple widgets?
    @Override
    public void onDrop(DragContext context) {
        if (canDrop(context) || hasCommandBeenExecuted()) {
            if (!hasCommandBeenExecuted()) {
                executeCommand(createCommand(context));
            }

            commandManager.addExecutedCommand(executedCommand);
            executedCommand = null; // prevent undo from later onLeave
        }

        hidePopup();

        super.onDrop(context);
    }

    @Override
    public void onEnter(final DragContext context) {
        // TODO support dragging multiple widgets?
        super.onEnter(context);

        if (canDrop(context)) {
            UndoableCommand command = createCommand(context);

            if (command instanceof HasDescription) {
                showPopup(context, ((HasDescription) command).getDescription());
            }

            executeCommand(command);
        }
    }

    // TODO support dragging multiple widgets?
    @Override
    public void onLeave(DragContext context) {
        undoCommand();
        hidePopup();

        super.onLeave(context);
    }

    // TODO refactor
    private void showPopup(final DragContext context, String message) {
        this.popup = createPopup(context, message);
        this.popup.showDelayed();
    }

    private void undoCommand() {
        if (hasCommandBeenExecuted()) {
            executedCommand.undo();
            executedCommand = null;
        }
    }
}