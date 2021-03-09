// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

/**
 * A server-to-client event that updates all player wallet UIs with currency information.
 */
@OwnerEvent
public class WalletUpdatedEvent implements Event {

    /**
     * The amount of currency this player's wallet contains.
     */
    public int amount;

    public WalletUpdatedEvent(int amount) {
        this.amount = amount;
    }

    public WalletUpdatedEvent() {
    }

}
