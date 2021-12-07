// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.terasology.economy.events.WalletUpdatedEvent;
import org.terasology.economy.ui.WalletHud;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;

/**
 * Manages the player's wallet UI.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class WalletClientSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;

    private Binding<String> walletBalance = new DefaultBinding<>("");

    public void preBegin() {
        WalletHud walletHud = (WalletHud) nuiManager.getHUD().addHUDElement("walletHud");
        walletHud.bind(walletBalance);
    }

    @ReceiveEvent
    public void onUpdateWallet(WalletUpdatedEvent event, EntityRef character) {
        if (localPlayer.getCharacterEntity().equals(character)) {
            walletBalance.set(String.valueOf(event.amount));
        }
    }
}
