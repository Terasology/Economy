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
package org.terasology.economy.systems;

import org.terasology.assets.management.AssetManager;
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.events.UpdateWalletEvent;
import org.terasology.economy.ui.WalletHud;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.network.NetworkSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;

/**
 * Manages the player's wallet/money
 */
@Share(WalletSystem.class)
@RegisterSystem(RegisterMode.CLIENT)
public class WalletSystem extends BaseComponentSystem {

    @In
    private EntityManager entityManager;

    @In
    private AssetManager assetManager;

    @In
    private NUIManager nuiManager;

    @In
    private LocalPlayer localPlayer;

    private WalletHud walletHud;
    private EntityRef walletEntity;

    private final int START_MONEY = 200;

    @Override
    public void postBegin() {
        CurrencyStorageComponent component = new CurrencyStorageComponent(START_MONEY);
        walletEntity = entityManager.create(component);
        walletHud = (WalletHud) nuiManager.getHUD().addHUDElement("walletHud");
        walletHud.setLabelText(component.amount);
    }

    @ReceiveEvent
    public void onUpdateWallet(UpdateWalletEvent event, EntityRef character) {

        // TODO: Probably use the resource draw/delete/create events to handle currency?

        CurrencyStorageComponent component = walletEntity.getComponent(CurrencyStorageComponent.class);
        component.amount += event.getDelta();
        walletEntity.saveComponent(component);
        walletHud.setLabelText(component.amount);
    }

    /**
     * Checks if the requested transaction is valid depending on the balance in the player's wallet
     * @param delta: the change in the wallet after the transaction
     * @return true if the wallet balance isn't negative after the transaction
     */
    public boolean isValidTransaction(int delta) {
        int balance = walletEntity.getComponent(CurrencyStorageComponent.class).amount;
        return (balance + delta >= 0);
    }
}
