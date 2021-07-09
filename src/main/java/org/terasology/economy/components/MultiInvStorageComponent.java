// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * This stores references to chests for a building entity. They are distinguished into chests to draw resources from and chests to store into.
 */
public class MultiInvStorageComponent implements Component<MultiInvStorageComponent> {
    public List<EntityRef> chests;
}
