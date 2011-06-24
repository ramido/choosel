package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.transform.NullTransformer;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class PropertyListBoxResolverUIController implements
        ViewItemValueResolverUIController {

    private final PropertyDependantViewItemValueResolver resolver;

    private List<String> properties;

    private ListBoxControl<String> selector;

    /**
     * This change handler will automatically synchronize the resolvers changes
     * with the property that is selected in the UI
     */
    private ChangeHandler changeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
            String selectedProperty = selector.getSelectedValue();
            if (!selectedProperty.equals(resolver.getProperty())) {
                resolver.setProperty(selectedProperty);
            }
        }
    };

    public PropertyListBoxResolverUIController(ViewItemValueResolver resolver) {
        this.resolver = (PropertyDependantViewItemValueResolver) resolver;
        selector = new ListBoxControl<String>(new ExtendedListBox(false),
                new NullTransformer<String>());
        selector.setChangeHandler(changeHandler);
    }

    /**
     * @return A Widget representation of this UI, as created by the controller.
     */
    @Override
    public Widget asWidget() {
        // if properties is not set, then this UI does not make sense
        // the resolver should be unapplicable, and this should never get called
        assert properties.contains(resolver.getProperty());

        // TODO, need to add a change handles to this selector
        selector.setValues(properties);
        selector.setSelectedValue(resolver.getProperty());
        return selector.asWidget();
    }

    /**
     * This method will return the properties that it would be ok to select from
     * the view items. All of the resources must contain that same property
     * 
     * 
     */
    private List<String> getSharedPropertiesFromViewItems(
            LightweightList<ViewItem> viewItems) {
        List<String> properties = new ArrayList<String>();

        // intialize properties to be the ones in the first resource
        ViewItem firstItem = viewItems.get(0);
        Resource firstResource = firstItem.getResources().iterator().next();
        properties.addAll(firstResource.getProperties().keySet());

        // only keep properties that are shared by all of the resource
        for (ViewItem viewItem : viewItems) {
            for (Resource resource : viewItem.getResources()) {
                properties.retainAll(resource.getProperties().keySet());
            }
        }
        return properties;
    }

    /**
     * this method will set the properties of both this UI Controller and the
     * properties of the Selector Widget
     */
    public void setProperties(List<String> properties) {
        assert properties != null;
        this.properties = properties;
        this.selector.setValues(properties);
    }

    /**
     * This method assumes that it will be possible to find some way of creating
     * a valid resolver given the new work items. If it isn't, the factory
     * should say that it is not applicable, and so we should never get here
     */
    @Override
    public void update(LightweightList<ViewItem> viewItems) {
        setProperties(getSharedPropertiesFromViewItems(viewItems));

        // the new view items can not be resolver by the current resolver, and
        // the property field should be set to something that is valid
        if (!properties.contains(resolver.getProperty())) {
            resolver.setProperty(properties.get(0));
            selector.setSelectedValue(properties.get(0));
        }
    }
}
