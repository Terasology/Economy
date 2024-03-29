// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.ui;

import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.widgets.UILabel;

/**
 * The UI class for the wallet HUD
 */
public class WalletHud extends CoreHudWidget {

    private UILabel label;

    @Override
    public void initialise() {
        label = find("walletInfoLabel", UILabel.class);
        label.setText("0");
    }

    public void bind(Binding<String> walletBalance) {
        label.bindText(walletBalance);
    }
}
