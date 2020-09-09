// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;


import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;

public class ResourceDrawEvent implements Event {
    private final String resource;
    private final int amount;
    private final EntityRef target;

    public ResourceDrawEvent(String resource, int amount, EntityRef target) {
        this.resource = resource;
        this.amount = amount;
        this.target = target;
    }

    public int getAmount() {
        return amount;
    }

    public EntityRef getTarget() {
        return target;
    }

    public String getResource() {
        return resource;
    }

}
