// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.systems;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.economy.components.MarketSubscriberComponent;
import org.terasology.economy.events.ConditionedProductionEvent;
import org.terasology.economy.events.SubscriberRegistrationEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;

import java.util.Collection;

/**
 * This system keeps track of entities with a MarketSubscriberComponent.
 * It sends the relevant resource requests at the respective interval for every subscriber.
 */
@Share(value = MarketUpdaterSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class MarketUpdaterSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private Logger logger = LoggerFactory.getLogger(MarketUpdaterSystem.class);
    private int timer = 300;
    private Multimap<Integer, EntityRef> productionIntervalLedger;

    @In
    private AssetManager assetManager;

    @In
    private EntityManager entityManager;

    @Override
    public void postBegin() {
        productionIntervalLedger = MultimapBuilder.hashKeys().arrayListValues().build();
    }

    @Override
    public void update(float delta) {
        for (Integer interval : productionIntervalLedger.keySet()) {
            if (interval != 0 && timer % interval == 0) {
                Collection<EntityRef> entities = productionIntervalLedger.get(interval);
                entities.forEach(this::processSubscriber);
            }
        }
        timer++;
    }

    @ReceiveEvent(components = MarketSubscriberComponent.class)
    public void registerMarketAdapter(SubscriberRegistrationEvent event, EntityRef entityRef) {
        MarketSubscriberComponent marketSubscriberComponent = entityRef.getComponent(MarketSubscriberComponent.class);
        productionIntervalLedger.put(marketSubscriberComponent.productionInterval, entityRef);
    }

    public void processSubscriber(EntityRef entity) {
        MarketSubscriberComponent marketSubscriberComponent = entity.getComponent(MarketSubscriberComponent.class);
        if (entity == null) {
            logger.error("Entity is not available for processing as market subscriber.");
        }
        if (marketSubscriberComponent == null) {
            logger.error("Entity has no valid MarketSubscriberComponent. Entity: " + entity.toFullDescription());
        }
        entity.send(new ConditionedProductionEvent(marketSubscriberComponent.consumption, marketSubscriberComponent.production,
                marketSubscriberComponent.consumptionStorage, marketSubscriberComponent.productStorage));
    }

}
