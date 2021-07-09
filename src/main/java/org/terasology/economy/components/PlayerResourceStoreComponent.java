// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Added to a player to connect a player to their resource store entity.
 */
public class PlayerResourceStoreComponent implements Component<PlayerResourceStoreComponent> {

    /**
     * This player's resource store entity.
     */
    public EntityRef resourceStore;

    public PlayerResourceStoreComponent(EntityRef resourceStore) {
        this.resourceStore = resourceStore;
    }

    public PlayerResourceStoreComponent() {

    }

}
