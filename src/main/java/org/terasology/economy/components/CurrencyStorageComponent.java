// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

/**
 * Marks an entity as a currency store
 */
public class CurrencyStorageComponent implements Component {

    @Replicate
    public int amount;

    public CurrencyStorageComponent(int amount) {
        this.amount = amount;
    }

    public CurrencyStorageComponent() {
    }
}
