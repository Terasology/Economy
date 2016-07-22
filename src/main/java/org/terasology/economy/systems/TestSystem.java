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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.InfiniteStorageComponent;
import org.terasology.economy.events.RequestConditionedProduction;
import org.terasology.economy.events.RequestResourceCreation;
import org.terasology.economy.events.RequestResourceDestruction;
import org.terasology.economy.events.RequestResourceDraw;
import org.terasology.economy.events.RequestResourceStore;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TestSystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(TestSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private MarketLogisticSystem marketLogisticSystem;

    private EntityRef storageA;
    private EntityRef storageB;

    InfiniteStorageComponent storageAComponent;
    InfiniteStorageComponent storageBComponent;

    @Override
    public void postBegin() {
        storageAComponent = new InfiniteStorageComponent(1);
        storageBComponent = new InfiniteStorageComponent(2);

        storageA = entityManager.create(storageAComponent);
        storageB = entityManager.create(storageBComponent);
        createAndDestroy();
        draw();
        store();
        conditionedProduction();
    }


    public void createAndDestroy() {
        storageA.send(new RequestResourceCreation("JellyBeans", 10));
        storageB.send(new RequestResourceCreation("Waffles", 10));
        storageB.send(new RequestResourceDestruction("Waffles", 10));
        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);

        Map<String, Integer> correctInvA = new HashMap<>();
        Map<String, Integer> correctInvB = new HashMap<>();
        correctInvA.put("JellyBeans", 10);
        correctInvB.put("Waffles", 0);
    }

    public void draw() {
        Map<String, Integer> correctInvA = new HashMap<>();
        Map<String, Integer> correctInvB = new HashMap<>();

        storageA.send(new RequestResourceCreation("Rocks", 10));
        storageB.send(new RequestResourceDraw("Rocks", 10, storageA));

        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);

        correctInvA.put("JellyBeans", 0);
        correctInvB.put("JellyBeans", 10);

    }

    public void store() {
        Map<String, Integer> correctInvA = new HashMap<>();
        Map<String, Integer> correctInvB = new HashMap<>();

        storageA.send(new RequestResourceCreation("Logs", 10));
        storageA.send(new RequestResourceStore("Logs", 10, storageB));

        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);

        correctInvA.put("JellyBeans", 0);
        correctInvB.put("JellyBeans", 10);

    }

    public void conditionedProduction() {
        Map<String, Integer> consumptionPackage = new HashMap<>();
        Map<String, Integer> productionPackage = new HashMap<>();
        consumptionPackage.put("JellyBeans", 10);
        productionPackage.put("Wombats", 10);
        storageA.send(new RequestConditionedProduction(consumptionPackage, productionPackage, storageB, storageA));
        storageB.send(new RequestConditionedProduction(productionPackage, consumptionPackage, storageA, storageB));
        InfiniteStorageComponent containerA = storageA.getComponent(InfiniteStorageComponent.class);
        InfiniteStorageComponent containerB = storageB.getComponent(InfiniteStorageComponent.class);
    }



}
