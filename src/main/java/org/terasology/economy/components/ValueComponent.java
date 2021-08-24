// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;

/**
 * Indicates that the given entity has a value.
 */
@AddToBlockBasedItem
public class ValueComponent implements Component {

    /**
     * How much money one instance of this entity is worth
     */
    public int value;
}
