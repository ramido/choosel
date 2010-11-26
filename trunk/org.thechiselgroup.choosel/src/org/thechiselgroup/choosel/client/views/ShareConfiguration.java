package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShareConfiguration implements WidgetAdaptable {

    private VerticalPanel sharePanel;

    private DefaultView view;

    private final ViewSaver viewPersistence;

    public ShareConfiguration(DefaultView view, ViewSaver viewPersistence) {
        this.view = view;
        this.viewPersistence = viewPersistence;

    }

    @Override
    public Widget asWidget() {
        if (sharePanel == null) {
            init();
        }
        return sharePanel;
    }

    public DefaultView getView() {
        return view;
    }

    private void init() {
        sharePanel = new VerticalPanel();

        initShareControls();
    }

    private void initShareControls() {
        if (Window.Location.getParameter("viewId") == null) {

            Button w = new Button("Share this");
            w.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {

                    // TODO Add the call to the WindowPersistenceManager to save
                    // a
                    // copy of this window with a unique ID

                    Label genLabel = new Label();
                    genLabel.setText("Generating Share Information...");

                    sharePanel.add(genLabel);
                    sharePanel.setStyleName("share-panel-generating");

                    viewPersistence.saveView(ShareConfiguration.this);

                }
            });
            sharePanel.add(w);
        }

    }

    public void updateSharePanel(Long id) {
        String url = Window.Location.getHref()
                + (Window.Location.getParameterMap().size() == 0 ? "?" : "&")
                + "viewId=" + id.toString();

        sharePanel.remove(1);

        Label urlLabel = new Label();
        urlLabel.setText("Share Link:");

        TextBox textBox = new TextBox();
        textBox.setText(url);

        sharePanel.add(urlLabel);
        sharePanel.add(textBox);
    }

}
