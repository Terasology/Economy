// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.handler;

import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Handler for currency stores.
 */
public class CurrencyStorageHandler implements StorageComponentHandler<CurrencyStorageComponent> {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyStorageHandler.class);

    private static final String CURRENCY = "currency";

    @Override
    public int store(CurrencyStorageComponent storage, String resource, int amount) {
        if (Objects.equals(resource, CURRENCY)) {
            storage.amount += amount;
        }
        return 0;
    }

    @Override
    public int draw(CurrencyStorageComponent storage, String resource, int amount) {
        int leftOver = 0;
        if (Objects.equals(resource, CURRENCY)) {
            if (storage.amount >= amount) {
                storage.amount -= amount;
            } else {
                leftOver = amount - storage.amount;
                storage.amount = 0;
            }
        }
        return leftOver;
    }

    @Override
    public int availableResourceAmount(CurrencyStorageComponent storage, String resource) {
        if (Objects.equals(resource, CURRENCY)) {
            return storage.amount;
        } else {
            return 0;
        }
    }

    @Override
    public int availableResourceCapacity(CurrencyStorageComponent storage, String resource) {
        return Integer.MAX_VALUE;
    }

    @Override
    public Set<String> availableResourceTypes(CurrencyStorageComponent storage) {
        Set<String> types = new HashSet<>();
        types.add(CURRENCY);
        return types;
    }

    @Override
    public Class getStorageComponentClass() {
        return CurrencyStorageComponent.class;
    }

    @Override
    public Component getTestComponent() {
        return new CurrencyStorageComponent(0);
    }

    @Override
    public String getTestResource() {
        return CURRENCY;
    }
}
