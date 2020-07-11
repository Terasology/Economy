// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.events.WalletTransactionEvent;
import org.terasology.economy.events.WalletUpdatedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.registry.Share;

/**
 * Deals with all server-side wallet operations, such as transactions and initial wallet creation.
 */
@Share(WalletAuthoritySystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class WalletAuthoritySystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(WalletAuthoritySystem.class);

    @ReceiveEvent
    public void onPlayerJoin(OnPlayerSpawnedEvent onPlayerSpawnedEvent, EntityRef player) {
        CurrencyStorageComponent component = new CurrencyStorageComponent();
        component.amount = 200;

        player.addComponent(component);
        player.send(new WalletUpdatedEvent(component.amount));
    }

    @ReceiveEvent
    public void walletUpdatedEvent(WalletTransactionEvent walletTransactionEvent, EntityRef player) {
        CurrencyStorageComponent component = player.getComponent(CurrencyStorageComponent.class);

        if (component.amount - walletTransactionEvent.getDelta() >= 0) {
            component.amount += walletTransactionEvent.getDelta();
            logger.info(String.format("Transaction of %d currency has succeeded.", walletTransactionEvent.getDelta()));
        } else {
            logger.warn(String.format("Transaction of %d currency has failed.", walletTransactionEvent.getDelta()));
        }

        player.saveComponent(component);
        player.send(new WalletUpdatedEvent(component.amount));
    }

    /**
     * Checks if the requested transaction is valid depending on the balance in the player's wallet
     * @param delta: the change in the wallet after the transaction
     * @return true if the wallet balance isn't negative after the transaction
     */
    public boolean isValidTransaction(EntityRef characterEntity, int delta) {
        int balance = characterEntity.getComponent(CurrencyStorageComponent.class).amount;
        return (balance + delta >= 0);
    }

}
