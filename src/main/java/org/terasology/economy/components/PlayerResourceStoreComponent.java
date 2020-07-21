// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

/**
 * Added to a player to connect a player to their resource store entity.
 */
public class PlayerResourceStoreComponent implements Component {

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
