// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.events.WalletTransactionEvent;
import org.terasology.economy.events.WalletUpdatedEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.PlayerCharacterComponent;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.time.WorldTimeEvent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * Deals with all server-side wallet operations, such as transactions and initial wallet creation.
 */
@Share(WalletAuthoritySystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class WalletAuthoritySystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(WalletAuthoritySystem.class);

    @In
    public EntityManager entityManager;

    @ReceiveEvent
    public void onWorldTimeEvent(WorldTimeEvent worldTimeEvent, EntityRef entity) {
        for (EntityRef player : entityManager.getEntitiesWith(PlayerCharacterComponent.class)) {
            CurrencyStorageComponent component = player.getComponent(CurrencyStorageComponent.class);

            player.send(new WalletUpdatedEvent(component.amount));
        }
    }

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

        if (component.amount + walletTransactionEvent.getDelta() >= 0) {
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
