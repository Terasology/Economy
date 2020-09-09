// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.List;

/**
 * This stores references to chests for a building entity. They are distinguished into chests to draw resources from and
 * chests to store into.
 */
public class MultiInvStorageComponent implements Component {
    public List<EntityRef> chests;
}
