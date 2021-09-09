// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.handler;

import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Set;

public interface StorageComponentHandler<T extends Component> {

    /**
     * @param resource The resource tag. Use the internal Economy Module representation.
     * @param amount Size of the resource package to store.
     * @return The amount of resources of that package that could not be stored.
     */
    int store(T storage, String resource, int amount);

    /**
     * @param resource The resource tag. Use the internal Economy Module representation.
     * @param amount Size of the resource package to draw out of the storage.
     * @return The amount of resources of that package that could not be drawn out.
     */
    int draw(T storage, String resource, int amount);

    /**
     * @param storage The custom storage component
     * @param resource The resource type
     * @return The amount of that resource type available in that container
     */
    int availableResourceAmount(T storage, String resource);

    /**
     * @param storage The custom storage component
     * @param resource The resource type
     * @return The amount of that resource type which can be stored inside that container
     */
    int availableResourceCapacity(T storage, String resource);

    Set<String> availableResourceTypes(T storage);

    Class getStorageComponentClass();

    Component getTestComponent();

    String getTestResource();
}
