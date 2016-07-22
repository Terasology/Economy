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

import java.util.HashMap;
import java.util.Map;

public class AdapterLedger implements Component {
    public int currentID;
    public Map<String, MarketSubscriber> adapters;

    /**
     * If created for the first time, pass an int value.
     * The default constructer assures correct un/loading.
     * @param i
     */
    public AdapterLedger(int i) {
        currentID = 0;
        adapters = new HashMap<>();
    }

    public AdapterLedger() { }

    public void addAdapter(MarketSubscriber adapter) {
        adapters.put(Integer.toString(currentID), adapter);
        currentID++;
    }
}
