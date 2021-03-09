/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.economy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.engine.entitySystem.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Handler for currency stores.
 */
public class CurrencyStorageHandler implements StorageComponentHandler<CurrencyStorageComponent> {
    private final String CURRENCY = "currency";

    private Logger logger = LoggerFactory.getLogger(CurrencyStorageHandler.class);

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
