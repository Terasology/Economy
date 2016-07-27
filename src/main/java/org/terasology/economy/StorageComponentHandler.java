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
package org.terasology.economy;


import org.terasology.entitySystem.Component;

public interface StorageComponentHandler<T extends Component> {

    /**
     * @param resource: The resource tag. Use the internal Economy Module representation.
     * @param amount: Size of the resource package to store.
     * @return The amount of resources of that package that could not be stored.
     */
    int store(T storage, String resource, int amount);

    /**
     * @param resource: The resource tag. Use the internal Economy Module representation.
     * @param amount: Size of the resource package to draw out of the storage.
     * @return The amount of resources of that package that could not be drawn out.
     */
    int draw(T storage, String resource, int amount);

    /**
     * @param storage: The custom storage component
     * @param resource: The resource type
     * @return The amount of that resource type available in that container
     */
    int availableResourceAmount(T storage, String resource);

    /**
     * @param storage: The custom storage component
     * @param resource: The resource type
     * @return The amount of that resource type which can be stored inside that container
     */
    int availableResourceCapacity(T storage, String resource);

    Class getStorageComponentClass();

    Component getTestComponent();

    String getTestResource();
}
