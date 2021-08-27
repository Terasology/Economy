// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;


import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;

import java.util.Map;

public class ConditionedProductionEvent implements Event {
    private Map<String, Integer> consumptionResourcePackages;
    private Map<String, Integer> productionResourcePackages;
    private EntityRef consumptionStorage;
    private EntityRef productionStorage;

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
