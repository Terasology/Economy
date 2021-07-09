// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Marks an entity as a currency store
 */
public class CurrencyStorageComponent implements Component<CurrencyStorageComponent> {

    @Replicate
    public int amount;

    public CurrencyStorageComponent(int amount) {
        this.amount = amount;
    }

    public CurrencyStorageComponent() {
    }
}
