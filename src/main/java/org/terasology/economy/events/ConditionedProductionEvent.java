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
package org.terasology.economy.events;


import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

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
