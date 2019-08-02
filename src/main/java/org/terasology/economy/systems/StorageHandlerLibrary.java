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
import org.terasology.economy.handler.CurrencyStorageHandler;
import org.terasology.economy.handler.InfiniteStorageHandler;
import org.terasology.economy.handler.StorageComponentHandler;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Share(value = StorageHandlerLibrary.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageHandlerLibrary extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(StorageHandlerLibrary.class);
    private Map<String, StorageComponentHandler> handlerMap = new HashMap<>();

    @In
    TestSystem testSystem;
    @In
    EntityManager entityManager;
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
