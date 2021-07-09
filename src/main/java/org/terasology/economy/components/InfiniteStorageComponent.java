// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.economy.components;


import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This storage component has an infinite stack-size and capacity.
 * Used for testing and simulation purposes.
 */
public class InfiniteStorageComponent implements Component<InfiniteStorageComponent> {

    public Map<String, Integer> inventory;

    public InfiniteStorageComponent(int i) {
        inventory = new HashMap<>();
    }

    public InfiniteStorageComponent() {}

}
