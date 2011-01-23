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
package org.thechiselgroup.choosel.core.client.fx;

import org.adamtacy.client.ui.effects.core.NMorphScalar;
import org.adamtacy.client.ui.effects.transitionsphysics.EaseInOutTransitionPhysics;

public final class FXUtil {

    public static final EaseInOutTransitionPhysics EASE_OUT = new EaseInOutTransitionPhysics();

    public static final double MORPH_DURATION_IN_SECONDS = 0.5;

    public static final String OPACITY_MORPH = "opacity";

    public static NMorphScalar createOpacityMorph(int start, int end) {
        NMorphScalar morph = new NMorphScalar(FXUtil.OPACITY_MORPH) {
            @Override
            public void tearDownEffect() {
                // do not tear down as this sets original state
            };
        };

        morph.setStartValue(Integer.toString(start));
        morph.setEndValue(Integer.toString(end));
        morph.setTransitionType(FXUtil.EASE_OUT);
        morph.setDuration(FXUtil.MORPH_DURATION_IN_SECONDS);

        return morph;
    }

    private FXUtil() {

    }

}
