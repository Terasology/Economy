// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.events;

import org.terasology.engine.entitySystem.event.Event;

import java.util.Map;

/**
 * A server-side request/response for info about an entities resources.
 */
public class ResourceInfoRequestEvent implements Event {

    public Map<String, Integer> resources;
    public boolean isHandled;

    public ResourceInfoRequestEvent() {

    }

}
