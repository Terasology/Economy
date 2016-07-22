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
import org.terasology.economy.StorageComponentHandler;
import org.terasology.economy.events.RequestConditionedProduction;
import org.terasology.economy.events.RequestResourceDraw;
import org.terasology.economy.events.RequestResourceStore;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;

import java.util.Map;

/**
 * This system handles transfer and production events.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class MarketLogisticSystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(MarketLogisticSystem.class);

    @In
    private StorageHandlerLibrary storageHandlerLibrary;

    @SuppressWarnings("unchecked")
    @ReceiveEvent
    public int processResourceDraw(RequestResourceDraw event, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> targetStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(event.getTarget());
        Map<Component, StorageComponentHandler> originStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);

        if(targetStorageComponents.isEmpty()) {
            logger.warn("Attempted to draw out resources from a target with no valid storages. Entity: " + event.getTarget().toString());
            return -1;
        }
        if(originStorageComponents.isEmpty()) {
            logger.warn("Attempted to store resources in an origin with no valid storages. Entity: " + entityRef.toString());
            return -1;
        }
        int availableCapacity = 0;
        for (Component component : originStorageComponents.keySet()) {
            availableCapacity += originStorageComponents.get(component).availableResourceCapacity(component, event.getResource());
        }
        int amountLeft = (availableCapacity < event.getAmount()) ? availableCapacity : event.getAmount();
        for (Component component : targetStorageComponents.keySet()) {
            amountLeft = targetStorageComponents.get(component).draw(component, event.getResource(), amountLeft);
            event.getTarget().saveComponent(component);
            if (amountLeft == 0) {
                break;
            }
        }
        int storageAmount = event.getAmount() - amountLeft;
        for (Component component : originStorageComponents.keySet()) {
            storageAmount = originStorageComponents.get(component).store(component, event.getResource(), storageAmount);
            entityRef.saveComponent(component);
            if (storageAmount == 0) {
                break;
            }
        }
        return amountLeft + storageAmount;
    }

    @SuppressWarnings("unchecked")
    @ReceiveEvent
    public int processResourceStore(RequestResourceStore event, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> targetStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(event.getTarget());
        Map<Component, StorageComponentHandler> originStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);

        if(targetStorageComponents.isEmpty()) {
            logger.warn("Attempted to store resources in a target with no valid storages. Entity: " + event.getTarget().toString());
            return -1;
        }
        if(originStorageComponents.isEmpty()) {
            logger.warn("Attempted to draw out resources from an origin with no valid storages. Entity: " + entityRef.toString());
            return -1;
        }
        int availableCapacity = 0;
        for (Component component : targetStorageComponents.keySet()) {
            availableCapacity += targetStorageComponents.get(component).availableResourceCapacity(component, event.getResource());
        }
        int amountLeft = (availableCapacity < event.getAmount()) ? availableCapacity : event.getAmount();
        for (Component component : originStorageComponents.keySet()) {
            amountLeft = originStorageComponents.get(component).draw(component, event.getResource(), amountLeft);
            entityRef.saveComponent(component);
            if (amountLeft == 0) {
                break;
            }
        }
        int storageAmount = event.getAmount() - amountLeft;
        for (Component component : targetStorageComponents.keySet()) {
            storageAmount = targetStorageComponents.get(component).store(component, event.getResource(), storageAmount);
            entityRef.saveComponent(component);
            if (storageAmount == 0) {
                break;
            }
        }
        return storageAmount + amountLeft;
    }

    @ReceiveEvent
    public void processConditionedProduction(RequestConditionedProduction event, EntityRef entityRef) {

    }


}
