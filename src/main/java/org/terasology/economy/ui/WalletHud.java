/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.economy.ui;

import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.systems.WalletSystem;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UILabel;

public class WalletHud extends CoreHudWidget {

    @In
    private LocalPlayer localPlayer;

    private UILabel label;

    @Override
    public void initialise() {
        label = find("walletInfoLabel", UILabel.class);
    }

    @Override
    public void onOpened() {
        super.onOpened();
        if (label != null) {
            label.bindText(new ReadOnlyBinding<String>() {
                @Override
                public String get() {
                    CurrencyStorageComponent component = localPlayer.getCharacterEntity().getComponent(CurrencyStorageComponent.class);
                    if (component != null) {
                        return String.valueOf(component.amount);
                    } else {
                        return "0";
                    }
                }
            });
        }
    }
}
