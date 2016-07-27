/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.economy.systems;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.InfiniteStorageComponent;
import org.terasology.economy.events.ResourceCreationEvent;
import org.terasology.economy.events.ResourceDestructionEvent;
import org.terasology.economy.events.ResourceDrawEvent;
import org.terasology.economy.events.ResourceStoreEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;

import java.util.HashMap;
import java.util.Map;


public class BrokenTestSystem {

    private Logger logger = LoggerFactory.getLogger(BrokenTestSystem.class);

    private EntityManager entityManager;

    private EntityRef storageA;
    private EntityRef storageB;

    InfiniteStorageComponent storageAComponent;
    InfiniteStorageComponent storageBComponent;

    MarketLogisticSystem marketLogisticSystem;

    @Before
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
        Assert.assertEquals(correctInvA, containerA.inventory);
        Assert.assertEquals(correctInvB, containerB.inventory);
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

        Assert.assertEquals(correctInvA, containerA.inventory);
        Assert.assertEquals(correctInvB, containerB.inventory);
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

        Assert.assertEquals(correctInvA, containerA.inventory);
        Assert.assertEquals(correctInvB, containerB.inventory);
    }



}
