// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;


import org.terasology.gestalt.entitysystem.event.Event;

public class ResourceCreationEvent implements Event {
    private String resource;
    private int amount;

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
