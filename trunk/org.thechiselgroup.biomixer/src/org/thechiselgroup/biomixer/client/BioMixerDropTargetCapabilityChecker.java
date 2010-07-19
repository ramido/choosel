package org.thechiselgroup.biomixer.client;

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.ui.dnd.BlacklistDropTargetCapabilityChecker;

public class BioMixerDropTargetCapabilityChecker extends
		BlacklistDropTargetCapabilityChecker implements
		ChooselInjectionConstants {

	public BioMixerDropTargetCapabilityChecker() {
		disableResourceTypeToViewDrop(TYPE_TIMELINE, NcboUriHelper.NCBO_CONCEPT);
	}

}
