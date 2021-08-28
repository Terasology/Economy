// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import com.google.common.base.MoreObjects;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.ServerEvent;

/**
 * A trigger event to start a purchase transaction on the authority for the specified item or block.
 * <p>
 * @see org.terasology.economy.ShopManager#onPurchaseItem(PurchaseItemEvent, EntityRef)
 */
@ServerEvent
public class PurchaseItemEvent implements Event {
    Prefab targetPrefab;
    String blockURI;

    private PurchaseItemEvent() {
    }

    public PurchaseItemEvent(Prefab targetPrefab) {
        this.targetPrefab = targetPrefab;
    }

    public PurchaseItemEvent(String blockURI) {
        this.blockURI = blockURI;
    }

    public Prefab getTargetPrefab() {
        return targetPrefab;
    }

    public String getBlockURI() {
        return blockURI;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
