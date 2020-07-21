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
package org.terasology.economy;

import org.terasology.economy.events.WalletTransactionEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;

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
