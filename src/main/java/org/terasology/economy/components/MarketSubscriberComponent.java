// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.economy.components;


import com.google.common.collect.Maps;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.reflection.MappedContainer;

import java.util.HashMap;
import java.util.Map;

@MappedContainer
public class MarketSubscriberComponent implements Component<MarketSubscriberComponent> {

    public Map<String, Integer> production = Maps.newHashMap();
    public Map<String, Integer> consumption = Maps.newHashMap();
    public Map<String, Integer> internalBuffer = Maps.newHashMap();
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

    @Override
    public void copy(MarketSubscriberComponent other) {
        this.production = Maps.newHashMap(other.production);
        this.consumption = Maps.newHashMap(other.consumption);
        this.internalBuffer = Maps.newHashMap(other.internalBuffer);

        this.productStorage = other.productStorage;
        this.consumptionStorage = other.consumptionStorage;
        this.productionInterval = other.productionInterval;
    }
}
