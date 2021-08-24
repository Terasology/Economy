// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.ServerEvent;

/**
 * Trigger event that sends the required item (as a string/prefab) to the server.
 * It then creates the item entity and gives the item on the server.
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
