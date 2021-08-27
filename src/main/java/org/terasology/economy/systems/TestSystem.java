// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.events.ConditionedProductionEvent;
import org.terasology.economy.events.ResourceCreationEvent;
import org.terasology.economy.events.ResourceDestructionEvent;
import org.terasology.economy.events.ResourceDrawEvent;
import org.terasology.economy.events.ResourceStoreEvent;
import org.terasology.economy.handler.StorageComponentHandler;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;

import java.util.HashMap;
import java.util.Map;

@Share(value = TestSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class TestSystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(TestSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private MarketLogisticSystem marketLogisticSystem;


    private StorageHandlerLibrary storageHandlerLibrary;
    private EntityRef storageA;
    private EntityRef storageB;
    private Class componentClass;
    private StorageComponentHandler componentHandler;
    private String testResource;


    @Override
    public void postBegin() {
    }

    public void setStorageHandlerLibrary(StorageHandlerLibrary storageHandlerLibrary) {
        this.storageHandlerLibrary = storageHandlerLibrary;
    }

    public boolean testStorageComponent(StorageComponentHandler componentHandler) {
        storageA = entityManager.create();
        storageB = entityManager.create();
        this.componentHandler = componentHandler;
        Component storageComponentA = componentHandler.getTestComponent();
        Component storageComponentB = componentHandler.getTestComponent();
        componentClass = componentHandler.getStorageComponentClass();
        storageA.addComponent(storageComponentA);
        storageB.addComponent(storageComponentB);
        this.testResource = componentHandler.getTestResource();
        return (createAndDestroy() && draw() && store() && conditionedProduction());
    }


    private boolean createAndDestroy() {
        storageA.send(new ResourceCreationEvent(testResource, 10));
        storageB.send(new ResourceCreationEvent(testResource, 10));
        storageB.send(new ResourceDestructionEvent(testResource, 10));
        Component containerA = storageA.getComponent(componentClass);
        Component containerB = storageB.getComponent(componentClass);

        if (!(componentHandler.availableResourceAmount(containerA, testResource) == 10)) {
            logger.error("Resource creation unsuccessful for storage component " + componentClass.getName());
            return false;
        }
        if (!(componentHandler.availableResourceAmount(containerB, testResource) == 0)) {
            logger.error("Resource destruction unsuccessful for storage component " + componentClass.getName());
            return false;
        }
        storageA.send(new ResourceDestructionEvent(testResource, 999));
        storageB.send(new ResourceDestructionEvent(testResource, 999));

        return true;
    }

    private boolean draw() {
        storageA.send(new ResourceCreationEvent(testResource, 10));
        storageB.send(new ResourceDrawEvent(testResource, 10, storageA));

        Component containerA = storageA.getComponent(componentClass);
        Component containerB = storageB.getComponent(componentClass);

        if (!(componentHandler.availableResourceAmount(containerA, testResource) == 0)) {
            logger.error("Resource draw unsuccessful for storage component " + componentClass.getName() + ". Container to draw from has not lost the right amount of resources!");
            return false;
        }
        if (!(componentHandler.availableResourceAmount(containerB, testResource) == 10)) {
            logger.error("Resource draw unsuccessful for storage component " + componentClass.getName() + ". Container to draw into has not gained the right amount of resources!");
            return false;
        }
        storageA.send(new ResourceDestructionEvent(testResource, 999));
        storageB.send(new ResourceDestructionEvent(testResource, 999));
        return true;

    }

    private boolean store() {

        storageA.send(new ResourceCreationEvent(testResource, 10));
        storageA.send(new ResourceStoreEvent(testResource, 10, storageB));

        Component containerA = storageA.getComponent(componentClass);
        Component containerB = storageB.getComponent(componentClass);

        if (!(componentHandler.availableResourceAmount(containerA, testResource) == 0)) {
            logger.error("Resource store unsuccessful for storage component " + componentClass.getName() + ". Container to store from has not lost the right amount of resources!");
            return false;
        }
        if (!(componentHandler.availableResourceAmount(containerB, testResource) == 10)) {
            logger.error("Resource draw unsuccessful for storage component " + componentClass.getName() + ". Container to store into has not gained the right amount of resources!");
            return false;
        }
        storageA.send(new ResourceDestructionEvent(testResource, 999));
        storageB.send(new ResourceDestructionEvent(testResource, 999));
        return true;

    }

    private boolean conditionedProduction() {
        storageA.send(new ResourceCreationEvent(testResource, 10));
        Map<String, Integer> consumptionPackage = new HashMap<>();
        Map<String, Integer> productionPackage = new HashMap<>();
        consumptionPackage.put(testResource, 10);
        productionPackage.put(testResource, 10);
        Component containerA = storageA.getComponent(componentClass);
        Component containerB = storageB.getComponent(componentClass);

        storageA.send(new ConditionedProductionEvent(consumptionPackage, productionPackage, storageA, storageB));


        if (!(componentHandler.availableResourceAmount(containerA, testResource) == 0)) {
            logger.error("Resource consumption unsuccessful for storage component " + componentClass.getName() + ". Container to store from has not lost the right amount of resources!");
            return false;
        }
        if (!(componentHandler.availableResourceAmount(containerB, testResource) == 10)) {
            logger.error("Resource production unsuccessful for storage component " + componentClass.getName() + ". Container to store into has not gained the right amount of resources!");
            return false;
        }
        storageA.send(new ResourceDestructionEvent(testResource, 999));
        storageB.send(new ResourceDestructionEvent(testResource, 999));

        //Test insufficient consumption resources
        storageA.send(new ResourceCreationEvent(testResource, 9));
        storageA.send(new ConditionedProductionEvent(consumptionPackage, productionPackage, storageA, storageB));
        if (!(componentHandler.availableResourceAmount(containerA, testResource) == 9)) {
            logger.error("Resource consumption unsuccessful for storage component " + componentClass.getName() + ". Container to store from has not lost the right amount of resources!");
            return false;
        }
        if (!(componentHandler.availableResourceAmount(containerB, testResource) == 0)) {
            logger.error("Resource production unsuccessful for storage component " + componentClass.getName() + ". Container to store into has not gained the right amount of resources!");
            return false;
        }
        return true;
    }



}
