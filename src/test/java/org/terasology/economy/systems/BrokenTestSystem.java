// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.InfiniteStorageComponent;
import org.terasology.economy.events.ResourceCreationEvent;
import org.terasology.economy.events.ResourceDestructionEvent;
import org.terasology.economy.events.ResourceDrawEvent;
import org.terasology.economy.events.ResourceStoreEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.internal.PojoEntityManager;

import java.util.HashMap;
import java.util.Map;

@Disabled("Outdated")
public class BrokenTestSystem {

    private final Logger logger = LoggerFactory.getLogger(BrokenTestSystem.class);
    InfiniteStorageComponent storageAComponent;
    InfiniteStorageComponent storageBComponent;
    MarketLogisticSystem marketLogisticSystem;
    private EntityManager entityManager;
    private EntityRef storageA;
    private EntityRef storageB;

    @BeforeEach
    public void setup() {
        marketLogisticSystem = new MarketLogisticSystem();
        entityManager = new PojoEntityManager();
        storageA = Mockito.mock(EntityRef.class);
        storageB = Mockito.mock(EntityRef.class);
        storageAComponent = new InfiniteStorageComponent(1);
        storageBComponent = new InfiniteStorageComponent(2);
        Mockito.when(storageA.getComponent(InfiniteStorageComponent.class)).thenReturn(storageAComponent);
        Mockito.when(storageB.getComponent(InfiniteStorageComponent.class)).thenReturn(storageBComponent);

        entityManager = Mockito.mock(EntityManager.class);
    }

    @Test
    public void createAndDestroy() {
        storageA.send(new ResourceCreationEvent("JellyBeans", 10));
        storageB.send(new ResourceCreationEvent("Waffles", 10));
        storageB.send(new ResourceDestructionEvent("Waffles", 10));
        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);

        Map<String, Integer> correctInvA = new HashMap<>();
        Map<String, Integer> correctInvB = new HashMap<>();
        correctInvA.put("JellyBeans", 10);
        correctInvB.put("Waffles", 0);
        Assertions.assertEquals(correctInvA, containerA.inventory);
        Assertions.assertEquals(correctInvB, containerB.inventory);
    }

    @Test
    public void draw() {
        Map<String, Integer> correctInvA = new HashMap<>();
        Map<String, Integer> correctInvB = new HashMap<>();

        storageA.send(new ResourceCreationEvent("JellyBeans", 10));
        storageB.send(new ResourceDrawEvent("JellyBeans", 10, storageA));

        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);

        correctInvA.put("JellyBeans", 0);
        correctInvB.put("JellyBeans", 10);

        Assertions.assertEquals(correctInvA, containerA.inventory);
        Assertions.assertEquals(correctInvB, containerB.inventory);
    }

    @Test
    public void store() {
        Map<String, Integer> correctInvA = new HashMap<>();
        Map<String, Integer> correctInvB = new HashMap<>();

        storageA.send(new ResourceCreationEvent("JellyBeans", 10));
        storageA.send(new ResourceStoreEvent("JellyBeans", 10, storageB));

        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);

        correctInvA.put("JellyBeans", 0);
        correctInvB.put("JellyBeans", 10);

        Assertions.assertEquals(correctInvA, containerA.inventory);
        Assertions.assertEquals(correctInvB, containerB.inventory);
    }
}
