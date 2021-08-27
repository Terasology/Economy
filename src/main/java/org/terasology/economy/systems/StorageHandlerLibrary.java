// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.handler.CurrencyStorageHandler;
import org.terasology.economy.handler.InfiniteStorageHandler;
import org.terasology.economy.handler.StorageComponentHandler;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Share(value = StorageHandlerLibrary.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageHandlerLibrary extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(StorageHandlerLibrary.class);
    private Map<String, StorageComponentHandler> handlerMap = new HashMap<>();

    @In
    TestSystem testSystem;
    @Override
    public void postBegin() {
        registerHandler(new InfiniteStorageHandler());
        registerHandler(new CurrencyStorageHandler());
    }

    public void registerHandler(StorageComponentHandler handler) {
        testSystem.setStorageHandlerLibrary(this);
        handlerMap.put(handler.getStorageComponentClass().toString(), handler);
        if (testSystem.testStorageComponent(handler)) {
            logger.info("Registered new StorageComponentHandler " + handler.getClass().getName());
            return;
        }
        handlerMap.remove(handler.getStorageComponentClass().toString(), handler);
        logger.error("Could not register StorageComponentHandler " + handler.getClass().getName());
    }

    public Map<Component, StorageComponentHandler> getHandlerComponentMapForEntity(EntityRef entityRef) {
        Map<Component, StorageComponentHandler> handlerComponentMap = new HashMap<>();
        for (Component component : entityRef.iterateComponents()) {
            if (handlerMap.containsKey(component.getClass().toString())) {
                handlerComponentMap.put(component, handlerMap.get(component.getClass().toString()));
            }
        }
        if (handlerComponentMap.isEmpty()) {
            logger.warn("No storage with registered handler found for entity " + entityRef.toString());
        }
        return handlerComponentMap;
    }

    public Optional<StorageComponentHandler> getHandlerForComponent(Component component) {
        if (handlerMap.containsKey(component.getClass().toString())) {
            return Optional.of(handlerMap.get(component.getClass().toString()));
        } else {
            return Optional.empty();
        }
    }


}
