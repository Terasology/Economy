// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.handler;


import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.MultiInvStorageComponent;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.entity.BlockCommands;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.engine.world.block.items.BlockItemFactory;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.inventory.logic.InventoryComponent;
import org.terasology.inventory.logic.InventoryManager;
import org.terasology.inventory.logic.InventoryUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This handles entities with multiple storage entities, mainly building-entities with chests TODO: Delete
 * consumption/production chest differentiation
 */
@Share(MultiInvStorageHandler.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class MultiInvStorageHandler extends BaseComponentSystem implements StorageComponentHandler<MultiInvStorageComponent> {

    private final Logger logger = LoggerFactory.getLogger(MultiInvStorageHandler.class);
    @In
    private AssetManager assetManager;
    @In
    private EntityManager entityManager;
    @In
    private BlockCommands blockCommands;
    @In
    private BlockManager blockManager;
    @In
    private InventoryManager inventoryManager;
    private BlockItemFactory blockItemFactory;
    private ResourceUrn blockItemBase;

    @Override
    public void initialise() {
        blockItemFactory = new BlockItemFactory(entityManager);
    }

    @Override
    public void postBegin() {
        blockItemBase = Assets.getPrefab("engine:blockItemBase").get().getUrn();
    }

    @Override
    public int store(MultiInvStorageComponent multiInvStorageComponent, String resource, int amount) {
        EntityRef item = getItemEntity(resource);
        byte byteAmount;
        if (item == EntityRef.NULL) {
            return amount;
        }


        for (EntityRef entityRef : multiInvStorageComponent.chests) {
            int amountForChest = getItemCapacityForChest(entityRef, item);
            amountForChest = (amountForChest > amount) ? amount : amountForChest;
            if (amountForChest >= Byte.MAX_VALUE) {
                byteAmount = Byte.MAX_VALUE;
            } else {
                byteAmount = (byte) amountForChest;
            }
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            itemComponent.stackCount = byteAmount;
            item.saveComponent(itemComponent);
            inventoryManager.giveItem(entityRef, EntityRef.NULL, item);
            amount -= amountForChest;

            if (amount == 0) {
                return 0;
            }
        }
        return amount;

    }

    @Override
    public int draw(MultiInvStorageComponent multiInvStorageComponent, String resource, int amount) {
        EntityRef item = getItemEntity(resource);

        for (EntityRef entityRef : multiInvStorageComponent.chests) {
            int amountForChest = getItemCountForChest(entityRef, item);
            while (amountForChest != 0) {
                int slot = getSlotWithItem(entityRef, item);
                if (slot == -1) {
                    break;
                }
                int amountForSlot = InventoryUtils.getStackCount(InventoryUtils.getItemAt(entityRef, slot));
                if (inventoryManager.removeItem(entityRef, EntityRef.NULL, slot, true, amountForSlot) != null) {
                    amount -= amountForSlot;
                }
            }

        }
        return amount;
    }

    @Override
    public int availableResourceAmount(MultiInvStorageComponent multiInvStorageComponent, String resource) {
        int amount = 0;
        EntityRef item = getItemEntity(resource);
        for (EntityRef entityRef : multiInvStorageComponent.chests) {
            amount += getItemCountForChest(entityRef, item);
        }
        return amount;
    }

    @Override
    public Set<String> availableResourceTypes(MultiInvStorageComponent multiInvStorageComponent) {
        Set<String> result = new HashSet<>();
        int amount = 0;
        for (EntityRef entityRef : multiInvStorageComponent.chests) {
            for (String resource : getResourceTypesOfInventory(entityRef)) {
                result.add(resource);
            }
        }
        return result;
    }

    @Override
    public int availableResourceCapacity(MultiInvStorageComponent multiInvStorageComponent, String resource) {

        int capacity = 0;
        EntityRef item = getItemEntity(resource);
        for (EntityRef entityRef : multiInvStorageComponent.chests) {
            capacity += getItemCapacityForChest(entityRef, item);
        }
        return capacity;
    }

    @Override
    public Class getStorageComponentClass() {
        return MultiInvStorageComponent.class;
    }

    @Override
    public Component getTestComponent() {
        MultiInvStorageComponent multiInvStorageComponent = new MultiInvStorageComponent();
        multiInvStorageComponent.chests = new ArrayList<>();
        ResourceUrn resourceUrn = assetManager.resolve("CoreAdvancedAssets:chest", Prefab.class).iterator().next();
        Prefab chestPrefab = assetManager.getAsset(resourceUrn, Prefab.class).get();
        EntityRef chest = entityManager.create(chestPrefab);
        multiInvStorageComponent.chests.add(chest);
        return multiInvStorageComponent;
    }

    @Override
    public String getTestResource() {
        return "CoreAssets:torch";
    }

    private EntityRef getItemEntity(String resource) {
        Set<ResourceUrn> matches = assetManager.resolve(resource, Prefab.class);
        switch (matches.size()) {
            case 0:
                logger.error("No item found matching resource string " + resource);
                return EntityRef.NULL;
            case 1:
                Prefab prefab = assetManager.getAsset(matches.iterator().next(), Prefab.class).orElse(null);
                if (prefab != null && prefab.getComponent(ItemComponent.class) != null) {
                    return entityManager.create(prefab);
                } else {
                    BlockFamily blockFamily = blockManager.getBlockFamily(resource);
                    EntityRef item = blockItemFactory.newInstance(blockFamily, 1);
                    if (!item.exists()) {
                        return EntityRef.NULL;
                    }
                    return item;
                }
            default:
                StringBuilder builder = new StringBuilder();
                builder.append("Requested item \"");
                builder.append(resource);
                builder.append("\": matches ");
                Joiner.on(" and ").appendTo(builder, matches);
                builder.append(". Please fully specify one.");
                logger.error(builder.toString());
                return EntityRef.NULL;
        }
    }

    private int getItemCountForChest(EntityRef entityRef, EntityRef item) {
        int amount = 0;
        int slotCount = InventoryUtils.getSlotCount(entityRef);
        for (int i = 0; i < slotCount; i++) {
            EntityRef targetItem = InventoryUtils.getItemAt(entityRef, i);
            if (isSameItem(item, targetItem)) {
                amount += InventoryUtils.getStackCount(targetItem);
            }
        }
        return amount;
    }

    private int getItemCapacityForChest(EntityRef entityRef, EntityRef item) {
        int capacity = 0;
        InventoryComponent inventoryComponent = entityRef.getComponent(InventoryComponent.class);
        int slotCount = InventoryUtils.getSlotCount(entityRef);
        ItemComponent itemComponent = item.getComponent(ItemComponent.class);
        if (itemComponent == null) {
            logger.error("No item component found for item " + item.toFullDescription());
            return 0;
        }
        for (int i = 0; i < slotCount; i++) {
            EntityRef targetItem = InventoryUtils.getItemAt(entityRef, i);
            if (InventoryUtils.canStackInto(item, targetItem)) {
                capacity += itemComponent.maxStackSize - InventoryUtils.getStackCount(targetItem);
            }
        }
        return capacity;
    }

    private int getSlotWithItem(EntityRef entityRef, EntityRef item) {
        int slotCount = InventoryUtils.getSlotCount(entityRef);
        for (int i = 0; i < slotCount; i++) {
            EntityRef slotItem = InventoryUtils.getItemAt(entityRef, i);
            if (isSameItem(item, slotItem)) {
                return i;
            }
        }
        return -1;
    }

    private Set<String> getResourceTypesOfInventory(EntityRef entity) {
        Set<String> set = new HashSet<>();
        InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);
        if (!entity.exists() || !entity.isActive()) {
            return set;
        }
        for (EntityRef item : inventoryComponent.itemSlots) {
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            if (itemComponent != null && !set.contains(itemComponent.stackId)) {
                ResourceUrn uri = item.getParentPrefab().getUrn();
                if (uri == blockItemBase) {
                    set.add(item.getComponent(BlockItemComponent.class).blockFamily.getURI().toString());
                } else {
                    set.add(uri.toString());
                }
            }
        }
        return set;
    }

    private boolean isSameItem(EntityRef item, EntityRef targetItem) {
        if (!item.hasComponent(ItemComponent.class) || !targetItem.hasComponent(ItemComponent.class)) {
            return false;
        }
        return targetItem.getComponent(ItemComponent.class).stackId.equals(item.getComponent(ItemComponent.class).stackId);
    }
}
