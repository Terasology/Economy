// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.economy.components;


import com.google.common.collect.Maps;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Map;

/**
 * This storage component has an infinite stack-size and capacity.
 * Used for testing and simulation purposes.
 */
public class InfiniteStorageComponent implements Component<InfiniteStorageComponent> {

    public Map<String, Integer> inventory = Maps.newHashMap();

    public InfiniteStorageComponent(int i) {
    }

    public InfiniteStorageComponent() {}

    @Override
    public void copyFrom(InfiniteStorageComponent other) {
        this.inventory = Maps.newHashMap(other.inventory);
    }
}
