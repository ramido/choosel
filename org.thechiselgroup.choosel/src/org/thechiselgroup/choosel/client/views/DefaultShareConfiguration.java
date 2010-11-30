package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.authentication.AuthenticationManager;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DefaultShareConfiguration implements ShareConfiguration {

    private VerticalPanel sharePanel;

    private View view;

    private final ViewSaver viewPersistence;

    private Button button;

    private Label label;

    private TextBox textBox;

    private final String EMBED_POSTTEXT = "Created with <a href=\"http://choosel-mashups.appspot.com\">Choosel</a>";

    private final int EMBED_HEIGHT = 400;

    private final int EMBED_WIDTH = 480;

    private TextArea textArea;

    private Label embedLabel;

    private final AuthenticationManager authenticationManager;

    @Inject
    public DefaultShareConfiguration(ViewSaver viewPersistence,
            AuthenticationManager authenticationManager) {
        this.viewPersistence = viewPersistence;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Widget asWidget() {
        if (sharePanel == null) {
            init();
        }
        return sharePanel;
    }

    @Override
    public void attach(View view, StackPanel panel) {
        this.view = view;
        if (authenticationManager.isAuthenticated()) {
            panel.add(asWidget(), "Share");
        }
    }

    public View getView() {
        return view;
    }

    private void init() {
        sharePanel = new VerticalPanel();

        initShareControls();
    }

    private void initShareControls() {

        button = new Button("Share this");
        label = new Label("Generating Share Information...");
        label.setVisible(false);
        textBox = new TextBox();
        textBox.setVisible(false);
        embedLabel = new Label();
        embedLabel.setVisible(false);
        textArea = new TextArea();
        textArea.setVisible(false);

        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                label.setVisible(false);
                textBox.setVisible(false);
                embedLabel.setVisible(false);
                textArea.setVisible(false);

                label.setText("Generating Share Information...");
                label.setVisible(true);

                viewPersistence.saveView(DefaultShareConfiguration.this);

            }
        });
        sharePanel.add(button);
        sharePanel.add(label);
        sharePanel.add(textBox);
        sharePanel.add(embedLabel);
        sharePanel.add(textArea);

    }

    public void notLoggedIn() {
        label.setText("Sorry, you are not currently authenticated.  Please log in to share views");
        label.setVisible(true);
        textBox.setVisible(false);
        embedLabel.setVisible(false);
        textArea.setVisible(false);
    }

    @Override
    public void updateSharePanel(Long id) {
        String url = Window.Location.getHref()
                + (Window.Location.getParameterMap().size() == 0 ? "?" : "&")
                + "viewId=" + id.toString();

        String embed = "<iframe src=\""
                + url
                + "\" width=\""
                + EMBED_WIDTH
                + "\" height=\""
                + EMBED_HEIGHT
                + "\">Sorry, your browser doesn't support iFrames</iframe><br /><a href=\""
                + url + "&nw\">Open in Choosel</a>. " + EMBED_POSTTEXT;

        // Hide things while we change them
        label.setVisible(false);

        label.setText("Share Link:");
        textBox.setText(url);
        embedLabel.setText("Embed Source:");
        textArea.setText(embed);

        label.setVisible(true);
        textBox.setVisible(true);
        embedLabel.setVisible(true);
        textArea.setVisible(true);

    }

}
