package org.thechiselgroup.choosel.core.client.visualization.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.transform.Transformer;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.ViewItemValueResolverUIController;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

//Slot name is [________] + [uiControllerWidget]
public class DefaultSlotControl extends SlotControl {

    private ViewItemValueResolverUIController uiController;

    private ListBoxControl<VisualItemValueResolverFactory> resolverFactorySelector;

    private ChangeHandler factorySelectedChangeHandler;

    private VerticalPanel panel;

    private Widget currentUIControllerWidget;

    // TODO I don't like how I need this here just to get the viewItems. I feel
    // like this class should not know about view items. It could always keep
    // track of updates, but then it is possible to introduce inconsistencies
    private ManagedSlotMappingConfiguration configuration;

    public DefaultSlotControl(Slot slot,
            ManagedSlotMappingConfiguration configurationUIModel,
            ViewItemValueResolverUIController uiController) {

        super(slot);
        this.uiController = uiController;
        this.configuration = configurationUIModel;

        panel = new VerticalPanel();

        /**
         * Only responsible for changing the selected resolver, the change in UI
         * will handled elsewhere
         */
        factorySelectedChangeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                VisualItemValueResolverFactory resolverFactory = resolverFactorySelector
                        .getSelectedValue();

                LightweightCollection<VisualItem> visualItems = configuration
                        .getVisualItems();

                configuration.getManagedSlotMapping(getSlot()).setResolver(
                        resolverFactory.create(visualItems));

                updateOptions(visualItems);
            }
        };

        resolverFactorySelector = new ListBoxControl<VisualItemValueResolverFactory>(
                new ExtendedListBox(false),
                new Transformer<VisualItemValueResolverFactory, String>() {
                    @Override
                    public String transform(
                            VisualItemValueResolverFactory factory) {
                        return factory.getLabel();
                    }
                });
    }

    // TODO add special UI stuff for resolvers that are in error
    @Override
    public Widget asWidget() {
        // updateFactorySelector();

        Label slotNameLabel = new Label(getSlot().getName() + " is ");

        Widget factorySelectorWidget = resolverFactorySelector.asWidget();

        currentUIControllerWidget = uiController.asWidget();

        assert factorySelectorWidget != null;
        assert currentUIControllerWidget != null;

        panel.add(slotNameLabel);
        panel.add(factorySelectorWidget);
        panel.add(currentUIControllerWidget);

        return panel;
    }

    @Override
    public String getCurrentResolverID() {
        return configuration.getCurrentResolver(getSlot()).getResolverId();
    }

    private List<VisualItemValueResolverFactory> getFactoryList() {
        List<VisualItemValueResolverFactory> result = new ArrayList<VisualItemValueResolverFactory>();
        Collection<VisualItemValueResolverFactory> allowableResolverFactories = configuration
                .getManagedSlotMapping(getSlot())
                .getAllowableResolverFactories();

        for (VisualItemValueResolverFactory factrory : allowableResolverFactories) {
            result.add(factrory);
        }

        return result;
    }

    @Override
    public void setNewUIModel(ViewItemValueResolverUIController resolverUI) {
        this.uiController = resolverUI;
        panel.remove(currentUIControllerWidget);
        currentUIControllerWidget = uiController.asWidget();
        panel.add(currentUIControllerWidget);
    }

    private void updateFactorySelector() {
        // TODO there is a mem bug here because we do not remove the old handler
        resolverFactorySelector.setChangeHandler(factorySelectedChangeHandler);

        resolverFactorySelector.setValues(getFactoryList());
        resolverFactorySelector.setSelectedValue(configuration
                .getManagedSlotMapping(getSlot()).getCurrentFactory());
    }

    @Override
    public void updateOptions(LightweightCollection<VisualItem> viewItems) {
        updateFactorySelector();
        uiController.update(viewItems);
    }
}
