// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.ServerEvent;
import org.terasology.engine.logic.inventory.events.GiveItemEvent;


/**
 * Trigger event that sends the description of the required item to the server.
 * This allows the item to be created on the server using it's description, which is then given to the player
 * using the GiveItemEvent. Trying to use the GiveItemEvent on the client might prevent the item entity
 * from being replicated properly to the server which might prevent the player from getting the item .
 * @see GiveItemEvent
 */
@ServerEvent
public class GiveItemTypeEvent implements Event {
    Prefab targetPrefab;
    String blockURI;

    private GiveItemTypeEvent() {
    }

    public GiveItemTypeEvent(Prefab targetPrefab) {
        this.targetPrefab = targetPrefab;
    }

    public GiveItemTypeEvent(String blockURI) {
        this.blockURI = blockURI;
    }

    public Prefab getTargetPrefab() {
        return targetPrefab;
    }

    public String getBlockURI() {
        return blockURI;
    }
}
