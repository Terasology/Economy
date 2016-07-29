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

package org.terasology.economy.components;


import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.reflection.MappedContainer;

import java.util.HashMap;
import java.util.Map;

@MappedContainer
public class MarketSubscriberComponent implements Component {

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
