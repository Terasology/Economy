// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

import java.util.Map;

/**
 * A server-to-client response to a {@link MarketInfoClientRequestEvent}, containing information about market
 * resources.
 */
@OwnerEvent
public class MarketInfoClientResponseEvent implements Event {

    /**
     * A list of potential items and prices.
     */
    public Map<String, Integer> resources;

    public MarketInfoClientResponseEvent(Map<String, Integer> resources) {
        this.resources = resources;
    }

    public MarketInfoClientResponseEvent() {
    }

}
