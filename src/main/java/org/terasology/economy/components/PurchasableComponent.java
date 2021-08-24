// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;

/**
 * Indicates that the item can be bought and thus will be available in the shop
 */
@AddToBlockBasedItem
public class PurchasableComponent implements Component {
}
