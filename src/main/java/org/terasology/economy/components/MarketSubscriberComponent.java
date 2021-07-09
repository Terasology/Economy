// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.economy.components;


import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.reflection.MappedContainer;

import java.util.HashMap;
import java.util.Map;

@MappedContainer
public class MarketSubscriberComponent implements Component<MarketSubscriberComponent> {

    public Map<String, Integer> production;
    public Map<String, Integer> consumption;
    public Map<String, Integer> internalBuffer;
    public EntityRef productStorage;
    public EntityRef consumptionStorage;
    public int productionInterval;

    public MarketSubscriberComponent(int init) {
        production = new HashMap<>();
        consumption = new HashMap<>();
        internalBuffer = new HashMap<>();
    }

    public MarketSubscriberComponent() {

    }

}
