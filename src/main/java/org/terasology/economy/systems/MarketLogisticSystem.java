// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.economy.components.InfiniteStorageComponent;
import org.terasology.economy.events.ConditionedProductionEvent;
import org.terasology.economy.events.MarketInfoClientRequestEvent;
import org.terasology.economy.events.MarketInfoClientResponseEvent;
import org.terasology.economy.events.ResourceCreationEvent;
import org.terasology.economy.events.ResourceDestructionEvent;
import org.terasology.economy.events.ResourceDrawEvent;
import org.terasology.economy.events.ResourceInfoRequestEvent;
import org.terasology.economy.events.ResourceStoreEvent;
import org.terasology.economy.handler.StorageComponentHandler;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO: Negative number check

/**
 * This system handles transfer and production events.
 * It will also suppress resource requests if either
 *      A. the prior produced resources couldn't be stored (there are items in the internal buffer)
 *      B. the resources it consumes were not available
 */
@Share(MarketLogisticSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class MarketLogisticSystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(MarketLogisticSystem.class);

    @In
    private StorageHandlerLibrary storageHandlerLibrary;

    @In
    private EntityManager entityManager;
    @Override
    public void postBegin() {
        Iterable<EntityRef> infiniteStorageEntities = entityManager.getEntitiesWith(InfiniteStorageComponent.class);
        if (infiniteStorageEntities.iterator().hasNext()) {
            for (EntityRef storageEntity : infiniteStorageEntities) {
                InfiniteStorageComponent infiniteStorageComponent = storageEntity.getComponent(InfiniteStorageComponent.class);
                if (infiniteStorageComponent.inventory == null) {
                    infiniteStorageComponent.inventory = new HashMap<>();
                }
            }
        }
    }
    @ReceiveEvent
    public void passResourceDrawRequest(ResourceDrawEvent event, EntityRef entityRef) {
        processResourceDraw(event, entityRef);
    }
    @ReceiveEvent
    public void passResourceStore(ResourceStoreEvent event, EntityRef entityRef) {
        processResourceStore(event, entityRef);
    }

    @ReceiveEvent
    public void conditionedProduction(ConditionedProductionEvent event, EntityRef entityRef) {
        if ((event.getConsumptionResourcePackages() == null || checkResourcesFullyAvailable(event.getConsumptionResourcePackages(), event.getConsumptionStorage()))
                && (event.getConsumptionResourcePackages() == null || checkCapacityFullyAvailable(event.getProductionResourcePackages(), event.getProductionStorage()))) {
            if (event.getConsumptionResourcePackages() != null) {
                for (Map.Entry<String, Integer> resource : event.getConsumptionResourcePackages().entrySet()) {
                    event.getConsumptionStorage().send(new ResourceDestructionEvent(resource.getKey(), resource.getValue()));
                }
            }
            if (event.getProductionResourcePackages() != null) {
                for (Map.Entry<String, Integer> resource : event.getProductionResourcePackages().entrySet()) {
                    event.getProductionStorage().send(new ResourceCreationEvent(resource.getKey(), resource.getValue()));
                }
            }
        }

    }

    @ReceiveEvent
    public void createResource(ResourceCreationEvent event, EntityRef entityRef) {
        processCreateResource(event, entityRef);
    }

    @ReceiveEvent
    public void destroyResource(ResourceDestructionEvent event, EntityRef entityRef) {
        processDestroyResource(event, entityRef);
    }

    @SuppressWarnings("unchecked")
    @ReceiveEvent
    public void onResourceInfoRequest(ResourceInfoRequestEvent event, EntityRef entityRef) {
        event.resources = new HashMap<>();
        Map<Component, StorageComponentHandler> storageComponentHandlers = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);
        for (Map.Entry<Component, StorageComponentHandler> entry : storageComponentHandlers.entrySet()) {
            Set<String> componentResourceTypes = entry.getValue().availableResourceTypes(entry.getKey());
            for (String resource : componentResourceTypes) {
                int amount = entry.getValue().availableResourceAmount(entry.getKey(), resource);
                if (event.resources.containsKey(resource)) {
                    event.resources.replace(resource, event.resources.get(resource) + amount);
                } else {
                    event.resources.put(resource, amount);
                }
            }
        }
        event.isHandled = true;
    }

    @ReceiveEvent
    public void onMarketInfoClientRequest(MarketInfoClientRequestEvent event, EntityRef player) {
        EntityRef market = entityManager.getEntity(event.marketId);
        ResourceInfoRequestEvent requestEvent = new ResourceInfoRequestEvent();
        market.send(requestEvent);

        if (requestEvent.isHandled) {
            player.send(new MarketInfoClientResponseEvent(requestEvent.resources));
        }
    }

    @SuppressWarnings("unchecked")
    public int processResourceDraw(ResourceDrawEvent event, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> targetStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(event.getTarget());
        Map<Component, StorageComponentHandler> originStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);

        if(targetStorageComponents.isEmpty()) {
            logger.warn("Attempted to draw out resources from a target with no valid storage. Entity: " + event.getTarget().toString());
            return -1;
        }
        if(originStorageComponents.isEmpty()) {
            logger.warn("Attempted to store resources in an origin with no valid storage. Entity: " + entityRef.toString());
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
    public int processResourceStore(ResourceStoreEvent event, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> targetStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(event.getTarget());
        Map<Component, StorageComponentHandler> originStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);

        if(targetStorageComponents.isEmpty()) {
            logger.warn("Attempted to store resources in a target with no valid storage. Entity: " + event.getTarget().toString());
            return -1;
        }
        if(originStorageComponents.isEmpty()) {
            logger.warn("Attempted to draw out resources from an origin with no valid storage. Entity: " + entityRef.toString());
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
            event.getTarget().saveComponent(component);
            if (storageAmount == 0) {
                break;
            }
        }
        return storageAmount + amountLeft;
    }

    private boolean checkResourcesFullyAvailable(Map<String, Integer> resources, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> targetStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);

        if (targetStorageComponents.isEmpty()) {
            logger.warn("Attempted to check resource availability in a target with no valid storage. Entity: " + entityRef.toString());
            return false;
        }
        for (Map.Entry<String, Integer> resource : resources.entrySet()) {
            int capacityLeft = resource.getValue();
            for (Component component : targetStorageComponents.keySet()) {
                capacityLeft -= targetStorageComponents.get(component).availableResourceAmount(component, resource.getKey());
            }
            if (capacityLeft > 0) {
                return false;
            }
        }
        return true;
    }
    private boolean checkCapacityFullyAvailable(Map<String, Integer> resources, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> targetStorageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);

        if (targetStorageComponents.isEmpty()) {
            logger.warn("Attempted to check resource capacity in a target with no valid storage. Entity: " + entityRef.toString());
            return false;
        }
        for (Map.Entry<String, Integer> resource : resources.entrySet()) {
            int capacityLeft = resource.getValue();
            for (Component component : targetStorageComponents.keySet()) {
                capacityLeft -= targetStorageComponents.get(component).availableResourceCapacity(component, resource.getKey());
            }
            if (capacityLeft > 0) {
                return false;
            }
        }
        return true;
    }

    private int processCreateResource(ResourceCreationEvent event, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> storageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);
        int amountLeft = event.getAmount();

        if (storageComponents.isEmpty()) {
            logger.warn("Attempted to create resources in a target with no valid storage. Entity: " + entityRef.toString());
            return -1;
        }

        for (Component component : storageComponents.keySet()) {
            amountLeft = storageComponents.get(component).store(component, event.getResource(), amountLeft);
            entityRef.saveComponent(component);
            if (amountLeft == 0) {
                break;
            }
        }
        return amountLeft;
    }

    private int processDestroyResource(ResourceDestructionEvent event, EntityRef entityRef) {
        Map<Component, StorageComponentHandler> storageComponents = storageHandlerLibrary.getHandlerComponentMapForEntity(entityRef);
        int amountLeft = event.getAmount();

        if (storageComponents.isEmpty()) {
            logger.warn("Attempted to destroy resources in a target with no valid storage. Entity: " + entityRef.toString());
            return -1;
        }

        for (Component component : storageComponents.keySet()) {
            amountLeft = storageComponents.get(component).draw(component, event.getResource(), amountLeft);
            entityRef.saveComponent(component);
            if (amountLeft == 0) {
                break;
            }
        }
        return amountLeft;
    }


}
