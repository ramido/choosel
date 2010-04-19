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
package org.thechiselgroup.choosel.client.ui.popup;

import org.adamtacy.client.ui.NEffectPanel;
import org.adamtacy.client.ui.effects.core.NMorphScalar;
import org.adamtacy.client.ui.effects.events.EffectCompletedEvent;
import org.adamtacy.client.ui.effects.events.EffectCompletedHandler;
import org.thechiselgroup.choosel.client.fx.FXUtil;
import org.thechiselgroup.choosel.client.fx.Opacity;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.ZIndex;
import org.thechiselgroup.choosel.client.ui.dnd.DragProxyEventReceiver;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class DefaultPopupManager implements Opacity, PopupManager {

    private static class HasAllMouseHandlersLink implements MouseOverHandler,
	    MouseOutHandler, MouseMoveHandler, MouseDownHandler {

	private final PopupManager manager;

	public HasAllMouseHandlersLink(PopupManager manager) {
	    this.manager = manager;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
	    manager.onMouseDown(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
	    manager.onMouseMove(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
	    manager.onMouseOut(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
	    manager.onMouseOver(event.getClientX(), event.getClientY());
	}

    }

    private static class PopupMouseEventsHandler implements MouseOverHandler,
	    MouseOutHandler {

	private final DefaultPopupManager manager;

	public PopupMouseEventsHandler(DefaultPopupManager manager) {
	    this.manager = manager;
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
	    updateMousePosition(event);
	    manager.state.onPopupMouseOut(manager);
	    manager.eventBus.fireEvent(event);

	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
	    updateMousePosition(event);
	    manager.state.onPopupMouseOver(manager);
	    manager.eventBus.fireEvent(event);
	}

	private void updateMousePosition(MouseEvent<?> event) {
	    manager.updateMousePosition(event.getClientX(), event.getClientY());
	}
    }

    private class PopupPanel extends SimplePanel implements
	    DragProxyEventReceiver {

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
	    return addDomHandler(handler, MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
	    return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public void dragProxyAttached() {
	    // do nothing: popup should be visible during drop operation
	}

	@Override
	public void dragProxyDetached() {
	    if (isEnabled()) {
		// hide once drop operation is completed
		setState(INACTIVE_STATE);
	    }
	}
    }

    private static class State {

	public void enter(DefaultPopupManager manager) {
	}

	public void leave(DefaultPopupManager manager) {
	}

	public void onPopupMouseOut(DefaultPopupManager manager) {
	}

	public void onPopupMouseOver(DefaultPopupManager manager) {
	}

	public void onSourceMouseMove(DefaultPopupManager manager) {
	}

	public void onSourceMouseOut(DefaultPopupManager manager) {
	}

	public void onSourceMouseOver(DefaultPopupManager manager) {
	}

	public void onTimeout(DefaultPopupManager manager) {
	}

    }

    private final static State ACTIVE_STATE = new State() {

	@Override
	public void enter(DefaultPopupManager manager) {
	    hide(manager);
	}

	private void hide(DefaultPopupManager manager) {
	    manager.setPopupTransparency(OPACITY_OPAQUE);
	};

	@Override
	public void onPopupMouseOut(DefaultPopupManager manager) {
	    if (manager.isPopupHidingDelayed()) {
		manager.setState(SEMITRANSPARENT_WAITING_STATE);
	    } else {
		manager.setState(INACTIVE_STATE);
	    }
	};

    };

    private static final String CSS_POPUP_CLASS = "popups-Popup";

    public static final int DEFAULT_HIDE_DELAY = 250;

    public static final int DEFAULT_SHOW_DELAY = 500;

    private final static State DISABLED_STATE = new State() {

	@Override
	public void enter(DefaultPopupManager manager) {
	    manager.eventBus.fireEvent(new PopupClosingEvent(manager));
	    manager.hide();
	}

    };

    private final static State INACTIVE_STATE = new State() {

	@Override
	public void enter(DefaultPopupManager manager) {
	    manager.eventBus.fireEvent(new PopupClosingEvent(manager));
	    manager.hide();
	}

	@Override
	public void onSourceMouseOver(DefaultPopupManager manager) {
	    if (manager.isPopupDisplayDelayed()) {
		manager.setState(WAITING_STATE);
	    } else {
		manager.setState(SEMITRANSPARENT_STATE);
	    }
	}

    };

    private static final int POPUP_OFFSET_X = 20;

    private static final int POPUP_OFFSET_Y = 15;

    private final static State SEMITRANSPARENT_STATE = new State() {

	@Override
	public void enter(DefaultPopupManager manager) {
	    manager.setPopupTransparency(OPACITY_SEMI_TRANSPARENT);
	}

	@Override
	public void onPopupMouseOver(DefaultPopupManager manager) {
	    manager.setState(ACTIVE_STATE);
	}

	@Override
	public void onSourceMouseOut(DefaultPopupManager manager) {
	    if (manager.isPopupHidingDelayed()) {
		manager.setState(SEMITRANSPARENT_WAITING_STATE);
	    } else {
		manager.setState(INACTIVE_STATE);
	    }
	}

    };

    private final static State SEMITRANSPARENT_WAITING_STATE = new State() {

	@Override
	public void enter(DefaultPopupManager manager) {
	    manager.setPopupTransparency(OPACITY_SEMI_TRANSPARENT);
	    manager.startTimer(manager.hideDelay);
	};

	@Override
	public void leave(DefaultPopupManager manager) {
	    manager.cancelTimer();
	};

	@Override
	public void onPopupMouseOver(DefaultPopupManager manager) {
	    manager.setState(ACTIVE_STATE);
	};

	@Override
	public void onSourceMouseOver(DefaultPopupManager manager) {
	    manager.setState(SEMITRANSPARENT_STATE);
	};

	@Override
	public void onTimeout(DefaultPopupManager manager) {
	    manager.setState(INACTIVE_STATE);
	};

    };

    private final static State WAITING_STATE = new State() {

	@Override
	public void enter(DefaultPopupManager manager) {
	    manager.startTimer(manager.showDelay);
	}

	@Override
	public void leave(DefaultPopupManager manager) {
	    manager.cancelTimer();
	}

	@Override
	public void onSourceMouseMove(DefaultPopupManager manager) {
	    manager.startTimer(manager.showDelay);
	}

	@Override
	public void onSourceMouseOut(DefaultPopupManager manager) {
	    manager.setState(INACTIVE_STATE);
	}

	@Override
	public void onTimeout(DefaultPopupManager manager) {
	    manager.setState(SEMITRANSPARENT_STATE);
	}
    };

    /**
     * Creates a new popup manager. Needs to be started by calling init()
     * afterwards.
     * 
     * @param source
     *            Event interface that triggers popup
     * @param widgetFactory
     *            Factory that creates the content widget of the popup
     */
    public static DefaultPopupManager createPopupManager(
	    HasAllMouseHandlers source, WidgetFactory widgetFactory) {

	DefaultPopupManager manager = new DefaultPopupManager(widgetFactory);
	linkManagerToSource(manager, source);
	return manager;
    }

    private static RootPanel getRootPanel() {
	return RootPanel.get();
    }

    /**
     * Links a source to the popup manager.
     */
    public static Disposable linkManagerToSource(PopupManager manager,
	    HasAllMouseHandlers source) {

	HasAllMouseHandlersLink link = new HasAllMouseHandlersLink(manager);

	final HandlerRegistration reg1 = source.addMouseOverHandler(link);
	final HandlerRegistration reg2 = source.addMouseOutHandler(link);
	final HandlerRegistration reg3 = source.addMouseMoveHandler(link);
	final HandlerRegistration reg4 = source.addMouseDownHandler(link);

	return new Disposable() {
	    @Override
	    public void dispose() {
		reg1.removeHandler();
		reg2.removeHandler();
		reg3.removeHandler();
		reg4.removeHandler();
	    }
	};
    }

    private NMorphScalar currentEffect;

    private final HandlerManager eventBus = new HandlerManager(this);

    protected int hideDelay = DEFAULT_HIDE_DELAY;

    private int mouseXInClientArea = -1;

    private int mouseYInBrowserClientArea = -1;

    private NMorphScalar nextEffect;

    private PopupPanel popupContentPanel;

    private NEffectPanel popupEffectsPanel = null;

    private final PopupMouseEventsHandler popupMouseEventsHandler;

    private HandlerRegistration popupMouseOutHandlerRegistration;

    private HandlerRegistration popupMouseOverHandlerRegistration;

    protected int showDelay = DEFAULT_SHOW_DELAY;

    private State state = INACTIVE_STATE;

    private Timer timer = new Timer() {
	@Override
	public void run() {
	    state.onTimeout(DefaultPopupManager.this);
	};
    };

    private int transparency = OPACITY_TRANSPARENT;

    private final WidgetFactory widgetFactory;

    public DefaultPopupManager(WidgetFactory widgetFactory) {
	assert widgetFactory != null;

	this.widgetFactory = widgetFactory;
	this.popupMouseEventsHandler = new PopupMouseEventsHandler(this);
    }

    public HandlerRegistration addPopupClosingHandler(
	    PopupClosingHandler handler) {
	return eventBus.addHandler(PopupClosingEvent.TYPE, handler);
    }

    private void addPopupMouseListeners() {
	popupMouseOverHandlerRegistration = popupContentPanel
		.addMouseOverHandler(popupMouseEventsHandler);
	popupMouseOutHandlerRegistration = popupContentPanel
		.addMouseOutHandler(popupMouseEventsHandler);
    }

    public HandlerRegistration addPopupMouseOutHandler(MouseOutHandler handler) {
	return eventBus.addHandler(MouseOutEvent.getType(), handler);
    }

    public HandlerRegistration addPopupMouseOverHandler(MouseOverHandler handler) {
	return eventBus.addHandler(MouseOverEvent.getType(), handler);
    }

    private void attachPopup() {
	NEffectPanel popup = getPopupPanel();

	addPopupMouseListeners();

	Style style = popup.getElement().getStyle();
	style.setProperty(CSS.POSITION, CSS.ABSOLUTE);
	style.setProperty(CSS.Z_INDEX, Integer.toString(ZIndex.POPUP));
	style.setPropertyPx(CSS.LEFT, mouseXInClientArea + POPUP_OFFSET_X);
	style
		.setPropertyPx(CSS.TOP, mouseYInBrowserClientArea
			+ POPUP_OFFSET_Y);

	getRootPanel().add(popup);
    }

    protected void cancelTimer() {
	timer.cancel();
    }

    private NMorphScalar createMorphEffect(int currentTransparency,
	    int newTransparency) {

	NMorphScalar morph = FXUtil.createOpacityMorph(currentTransparency,
		newTransparency);

	morph.addEffectCompletedHandler(new EffectCompletedHandler() {
	    @Override
	    public void onEffectCompleted(EffectCompletedEvent event) {
		effectCompleted();
	    }
	});

	return morph;
    }

    private void detachPopup() {
	if (popupEffectsPanel == null) {
	    return;
	}

	popupMouseOverHandlerRegistration.removeHandler();
	popupMouseOutHandlerRegistration.removeHandler();

	getRootPanel().remove(getPopupPanel());
    }

    private void effectCompleted() {
	getPopupPanel().removeEffect(currentEffect);
	transparency = getNextTransparency();

	if (hasAnotherEffect()) {
	    currentEffect = nextEffect;
	    nextEffect = null;
	    runCurrentEffect();
	} else {
	    currentEffect = null;
	    if (transparency == OPACITY_TRANSPARENT) {
		detachPopup();
	    }
	}
    }

    public int getHideDelay() {
	return hideDelay;
    }

    private int getNextTransparency() {
	assert currentEffect != null;

	return (int) currentEffect.getEndValue();
    }

    private NEffectPanel getPopupPanel() {
	if (popupEffectsPanel == null) {
	    popupContentPanel = new PopupPanel();
	    popupContentPanel.setStyleName(CSS_POPUP_CLASS);
	    popupContentPanel.add(widgetFactory.createWidget());

	    popupEffectsPanel = new NEffectPanel();
	    popupEffectsPanel.add(popupContentPanel);
	}

	return popupEffectsPanel;
    }

    /**
     * @return Delay between showing mouse movement stops and popups gets shown
     *         in ms.
     */
    public int getShowDelay() {
	return showDelay;
    }

    private boolean hasAnotherEffect() {
	return nextEffect != null;
    }

    private void hide() {
	setPopupTransparency(OPACITY_TRANSPARENT);
    }

    public void hidePopup() {
	if (isEnabled()) {
	    setState(INACTIVE_STATE);
	}
    }

    private boolean isEffectRunning() {
	return currentEffect != null;
    }

    private boolean isPopupDisplayDelayed() {
	return showDelay > 0;
    }

    private boolean isPopupHidingDelayed() {
	return hideDelay > 0;
    }

    public void onMouseDown(int clientX, int clientY) {
	updateMousePosition(clientX, clientY);

	if (isEnabled()) {
	    // mouse down triggers click operations, popup gets hidden
	    setState(INACTIVE_STATE);
	}
    }

    public void onMouseDown(Point pointInClientArea) {
	onMouseDown(pointInClientArea.x, pointInClientArea.y);
    }

    public void onMouseMove(int clientX, int clientY) {
	updateMousePosition(clientX, clientY);
	state.onSourceMouseMove(this);
    }

    public void onMouseMove(Point pointInClientArea) {
	onMouseMove(pointInClientArea.x, pointInClientArea.y);
    }

    public void onMouseOut(int clientX, int clientY) {
	updateMousePosition(clientX, clientY);
	state.onSourceMouseOut(DefaultPopupManager.this);
    }

    public void onMouseOut(Point pointInClientArea) {
	onMouseOut(pointInClientArea.x, pointInClientArea.y);
    }

    public void onMouseOver(int clientX, int clientY) {
	updateMousePosition(clientX, clientY);
	state.onSourceMouseOver(DefaultPopupManager.this);
    }

    public void onMouseOver(Point pointInClientArea) {
	onMouseOver(pointInClientArea.x, pointInClientArea.y);
    }

    private void runCurrentEffect() {
	NEffectPanel popup = getPopupPanel();
	popup.addEffect(currentEffect);
	popup.playEffects();
    }

    /**
     * @param delay
     *            Delay between showing mouse out of source/popup and popups
     *            gets hidden in ms.
     */
    public void setHideDelay(int delay) {
	this.hideDelay = delay;
    }

    // for test
    protected void setPopupTransparency(int newTransparency) {
	assert ((newTransparency >= OPACITY_TRANSPARENT) && (newTransparency <= OPACITY_OPAQUE));

	int currentTransparency = isEffectRunning() ? getNextTransparency()
		: this.transparency;

	if (newTransparency == currentTransparency) {
	    return;
	}

	if ((newTransparency == OPACITY_TRANSPARENT)
		&& (popupEffectsPanel == null)) {
	    return;
	}

	// need to attach popup to run effects
	if (!getPopupPanel().isAttached()) {
	    attachPopup();
	}

	NMorphScalar effect = createMorphEffect(currentTransparency,
		newTransparency);

	if (isEffectRunning()) {
	    nextEffect = effect;
	} else {
	    currentEffect = effect;
	    runCurrentEffect();
	}

    }

    /**
     * @param delay
     *            Delay between showing mouse movement stops and popups gets
     *            shown in ms.
     */
    public void setShowDelay(int showDelay) {
	this.showDelay = showDelay;
    }

    private void setState(State newState) {
	assert newState != null;

	this.state.leave(this);
	this.state = newState;
	this.state.enter(this);
    }

    protected void startTimer(int delayInMs) {
	timer.schedule(delayInMs);
    }

    private void updateMousePosition(int clientX, int clientY) {
	this.mouseXInClientArea = clientX;
	this.mouseYInBrowserClientArea = clientY;
    }

    @Override
    public boolean isEnabled() {
	return state != DISABLED_STATE;
    }

    @Override
    public void setEnabled(boolean enabled) {
	if (enabled == isEnabled()) {
	    return;
	}

	if (enabled) {
	    setState(INACTIVE_STATE);
	} else {
	    setState(DISABLED_STATE);
	}
    }

}
