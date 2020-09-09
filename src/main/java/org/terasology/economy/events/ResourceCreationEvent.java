// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;


import org.terasology.engine.entitySystem.event.Event;

public class ResourceCreationEvent implements Event {
    private final String resource;
    private final int amount;

    public ResourceCreationEvent(String resource, int amount) {
        this.resource = resource;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getResource() {
        return resource;
    }

}
