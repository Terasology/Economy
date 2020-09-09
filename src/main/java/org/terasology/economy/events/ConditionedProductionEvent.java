// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;


import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;

import java.util.Map;

public class ConditionedProductionEvent implements Event {
    private final Map<String, Integer> consumptionResourcePackages;
    private final Map<String, Integer> productionResourcePackages;
    private final EntityRef consumptionStorage;
    private final EntityRef productionStorage;

    public ConditionedProductionEvent(Map<String, Integer> consumptionResourcePackages,
                                      Map<String, Integer> productionResourcePackages,
                                      EntityRef consumptionStorage, EntityRef productionStorage) {
        this.consumptionResourcePackages = consumptionResourcePackages;
        this.productionResourcePackages = productionResourcePackages;
        this.consumptionStorage = consumptionStorage;
        this.productionStorage = productionStorage;
    }

    public Map<String, Integer> getConsumptionResourcePackages() {
        return consumptionResourcePackages;
    }

    public Map<String, Integer> getProductionResourcePackages() {
        return productionResourcePackages;
    }

    public EntityRef getConsumptionStorage() {
        return consumptionStorage;
    }

    public EntityRef getProductionStorage() {
        return productionStorage;
    }
}
