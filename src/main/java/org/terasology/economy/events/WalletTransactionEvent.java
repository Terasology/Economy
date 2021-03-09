// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.entitySystem.event.Event;

/**
 * Fired when a currency transaction must occur.
 */
public class WalletTransactionEvent implements Event {

    /**
     * The difference in money caused by this transaction.
     */
    private int delta = 0;

    public WalletTransactionEvent(int delta) {
        this.delta = delta;
    }

    public WalletTransactionEvent() {
    }

    public int getDelta() {
        return delta;
    }
}
