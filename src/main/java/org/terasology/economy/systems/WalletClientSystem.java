// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.terasology.economy.events.WalletUpdatedEvent;
import org.terasology.economy.ui.WalletHud;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

/**
 * Manages the player's wallet UI.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class WalletClientSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;

    private WalletHud walletHud;

    public void postBegin() {
        walletHud = (WalletHud) nuiManager.getHUD().addHUDElement("walletHud");
    }

    @ReceiveEvent
    public void onUpdateWallet(WalletUpdatedEvent event, EntityRef character) {
        walletHud.setLabelText(event.amount);
    }
}
