/*
 * Copyright 2016 MovingBlocks
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


import org.terasology.economy.components.InfiniteStorageComponent;
import org.terasology.engine.entitySystem.Component;

import java.util.Map;
import java.util.Set;

public class InfiniteStorageHandler implements StorageComponentHandler<InfiniteStorageComponent> {

    public InfiniteStorageHandler() {}
    @Override
    public int store(InfiniteStorageComponent infiniteStorageComponent, String resource, int amount) {
        Map<String, Integer> inventory = infiniteStorageComponent.inventory;
        if (inventory.containsKey(resource)) {
            inventory.put(resource, inventory.get(resource) + amount);
        } else {
            inventory.put(resource, amount);
        }

        return 0;
    }

    @Override
    public int draw(InfiniteStorageComponent infiniteStorageComponent, String resource, int amount) {
        Map<String, Integer> inventory = infiniteStorageComponent.inventory;
        if (inventory.containsKey(resource)) {
            if (inventory.get(resource) >= amount) {
                inventory.put(resource, inventory.get(resource) - amount);
                return 0;
            } else {
                inventory.put(resource, 0);
                return amount - inventory.get(resource);
            }
        } else {
            return 0;
        }
    }

    @Override
    public int availableResourceAmount(InfiniteStorageComponent infiniteStorageComponent, String resource) {
        return infiniteStorageComponent.inventory.getOrDefault(resource, 0);
    }

    @Override
    public int availableResourceCapacity(InfiniteStorageComponent infiniteStorageComponent, String resource) {
        return Integer.MAX_VALUE;
    }

    @Override
    public Set<String> availableResourceTypes(InfiniteStorageComponent infiniteStorageComponent) {
        return infiniteStorageComponent.inventory.keySet();
    }

    @Override
    public Class getStorageComponentClass() {
        return InfiniteStorageComponent.class;
    }

    @Override
    public Component getTestComponent() {
        return new InfiniteStorageComponent(1);
    }
    @Override
    public String getTestResource() {
        return "testResource";
    }
}
