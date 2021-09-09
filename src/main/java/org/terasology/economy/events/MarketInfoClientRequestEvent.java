// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

/**
 * A client-to-server request for info about a market's available resources. Response arrives
 * through {@link MarketInfoClientResponseEvent}.
 */
@ServerEvent
public class MarketInfoClientRequestEvent implements Event {

    /**
     * The server-side entity ID of the requested market.
     */
    public long marketId;

    public MarketInfoClientRequestEvent(long marketId) {
        this.marketId = marketId;
    }

    public MarketInfoClientRequestEvent() {
    }

}
