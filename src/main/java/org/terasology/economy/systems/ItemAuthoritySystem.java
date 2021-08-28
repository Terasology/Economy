// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.ValueComponent;
import org.terasology.economy.events.WalletTransactionEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.events.GiveItemEvent;
import org.terasology.engine.registry.In;
import org.terasology.economy.events.GiveItemTypeEvent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.items.BlockItemFactory;
import org.terasology.module.inventory.systems.InventoryAuthoritySystem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ItemAuthoritySystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(InventoryAuthoritySystem.class);

    @In
    private WalletAuthoritySystem walletAuthoritySystem;
    @In
    private EntityManager entityManager;
    @In
    private BlockManager blockManager;

    private BlockItemFactory blockItemFactory;

    @Override
    public void postBegin() {
        blockItemFactory = new BlockItemFactory(entityManager);
    }

    @ReceiveEvent
    public void onGiveItemToPlayer(GiveItemTypeEvent event, EntityRef entity) {
        if (event.getTargetPrefab() != null && event.getTargetPrefab().hasComponent(ValueComponent.class)) {
            EntityRef item = entityManager.create(event.getTargetPrefab());
            buyItem(entity, item);
        } else if (event.getBlockURI() != null) {
            BlockFamily blockFamily = blockManager.getBlockFamily(event.getBlockURI());
            EntityRef blockItem = blockItemFactory.newInstance(blockFamily);
            buyItem(entity, blockItem);
        } else {
            logger.warn("Prefab/String is null");
        }
    }

    public void buyItem(EntityRef entity, EntityRef item) {
        if (walletAuthoritySystem.isValidTransaction(entity, -item.getComponent(ValueComponent.class).value)) {
            entity.send(new WalletTransactionEvent(-item.getComponent(ValueComponent.class).value));
            item.send(new GiveItemEvent(entity));
        }
    }
}

