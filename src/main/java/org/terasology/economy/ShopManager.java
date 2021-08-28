// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.PurchasableComponent;
import org.terasology.economy.components.ValueComponent;
import org.terasology.economy.events.GiveItemTypeEvent;
import org.terasology.economy.events.WalletTransactionEvent;
import org.terasology.economy.systems.WalletAuthoritySystem;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.logic.inventory.events.GiveItemEvent;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockExplorer;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockUri;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.items.BlockItemFactory;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.entitySystem.ComponentContainer;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.module.inventory.systems.InventoryAuthoritySystem;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the purchasing of blocks
 */
@RegisterSystem
@Share(ShopManager.class)
public class ShopManager extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(InventoryAuthoritySystem.class);

    @In
    private AssetManager assetManager;
    @In
    private BlockManager blockManager;
    @In
    private LocalPlayer localPlayer;
    @In
    private WalletAuthoritySystem walletAuthoritySystem;
    @In
    private EntityManager entityManager;

    private BlockItemFactory blockItemFactory;

    private Set<Block> purchasableBlocks = new HashSet<>();
    private Set<Prefab> purchasableItems = new HashSet<>();

    /**
     * Gets how much money a ware will cost.
     * Tries to use the cost on the value component.
     *
     * @param ware The ware to get the price for
     * @return The price of the ware.
     */
    public static int getWareCost(ComponentContainer ware) {
        Preconditions.checkNotNull(ware.getComponent(ValueComponent.class), "Component Container does not contain a Value Component");
        return ware.getComponent(ValueComponent.class).value;
    }

    @Override
    public void postBegin() {
        blockItemFactory = new BlockItemFactory(entityManager);

        purchasableItems = assetManager.getLoadedAssets(Prefab.class)
                .stream()
                .filter(prefab -> prefab.hasComponent(ItemComponent.class)
                        && prefab.hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());

        BlockExplorer blockExplorer = new BlockExplorer(assetManager);
        Set<BlockUri> blocks = new HashSet<>();
        blocks.addAll(blockManager.listRegisteredBlockUris());
        blocks.addAll(blockExplorer.getAvailableBlockFamilies());
        blocks.addAll(blockExplorer.getFreeformBlockFamilies());

        purchasableBlocks = blocks.stream()
                .map(blockManager::getBlockFamily)
                .map(BlockFamily::getArchetypeBlock)
                .filter(block -> block.getPrefab().isPresent())
                .filter(block -> block.getPrefab().get().hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());
    }

    /**
     * @return All the blocks for sale
     */
    public Set<Block> getAllBlocks() {
        return purchasableBlocks;
    }

    /**
     * @return All the items for sale
     */
    public Set<Prefab> getAllItems() {
        return purchasableItems;
    }

    /**
     * Attempt to purchase a block.
     *
     * @param block The block to purchase
     */
    public void purchaseBlock(Block block) {
        EntityRef character = localPlayer.getCharacterEntity();
        String blockURI = block.toString();
        character.send(new GiveItemTypeEvent(blockURI));
    }

    public void purchaseItem(Prefab prefab) {
        EntityRef character = localPlayer.getCharacterEntity();
        character.send(new GiveItemTypeEvent(prefab));
    }

    void performTransaction(EntityRef entity, EntityRef item) {
        int cost = ShopManager.getWareCost(item);
        if (walletAuthoritySystem.isValidTransaction(entity, -cost)) {
            entity.send(new WalletTransactionEvent(-cost));
            item.send(new GiveItemEvent(entity));
        }
    }

    @ReceiveEvent(netFilter = RegisterMode.AUTHORITY)
    public void onPurchaseItem(GiveItemTypeEvent event, EntityRef entity) {
        if (event.getTargetPrefab() != null && event.getTargetPrefab().hasComponent(ValueComponent.class)) {
            EntityRef item = entityManager.create(event.getTargetPrefab());
            performTransaction(entity, item);
        } else if (event.getBlockURI() != null) {
            BlockFamily blockFamily = blockManager.getBlockFamily(event.getBlockURI());
            EntityRef blockItem = blockItemFactory.newInstance(blockFamily);
            performTransaction(entity, blockItem);
        } else {
            logger.warn("Skipping item purchase action for undefined or non-purchasable item: {}", event);
        }
    }
}
