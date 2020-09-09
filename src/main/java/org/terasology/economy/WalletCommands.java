// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy;

import org.terasology.economy.events.WalletTransactionEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.engine.logic.permission.PermissionManager;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;

/**
 * Adds some commands to change the amount in a player's wallet
 */
@RegisterSystem
public class WalletCommands extends BaseComponentSystem {

    @In
    private LocalPlayer localPlayer;

    @Command(shortDescription = "Add money to your wallet", requiredPermission = PermissionManager.CHEAT_PERMISSION)
    public String giveMoney(@CommandParam(value = "0") int amount) {
        localPlayer.getCharacterEntity().send(new WalletTransactionEvent(amount));
        return "Gave " + amount + " to player.";
    }
}
