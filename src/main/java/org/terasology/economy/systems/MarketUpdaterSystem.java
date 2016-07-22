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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.economy.components.MarketSubscriberComponent;
import org.terasology.economy.events.RequestConditionedProduction;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.Collection;

/**
 * This system keeps track of entities with a MarketSubscriberComponent.
 * It sends the relevant resource requests at the respective interval for every subscriber.
 * It will also suppress resource requests if either
 *              A. the prior produced resources couldn't be stored (there are items in the internal buffer)
 *              B. the resources it consumes were not available
 */
@Share(value = MarketUpdaterSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class MarketUpdaterSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private Logger logger = LoggerFactory.getLogger(MarketUpdaterSystem.class);
    private int timer = 0;
    private Multimap<Integer, EntityRef> productionIntervalLedger;
    private EntityRef subscriberLedgerEntity;

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
            if (timer % interval == 0) {
                Collection<EntityRef> entities = productionIntervalLedger.get(interval);
                entities.forEach(this::processSubscriber);
            }
        }
    }

    @ReceiveEvent(components = {MarketSubscriberComponent.class})
    public void registerMarketAdapter(Event event, EntityRef entityRef) {
        MarketSubscriberComponent marketSubscriberComponent = entityRef.getComponent(MarketSubscriberComponent.class);
        productionIntervalLedger.put(marketSubscriberComponent.productionInterval, entityRef);
    }

    public void processSubscriber(EntityRef entity) {
        MarketSubscriberComponent marketSubscriberComponent = entity.getComponent(MarketSubscriberComponent.class);
        entity.send(new RequestConditionedProduction(marketSubscriberComponent.consumption, marketSubscriberComponent.production,
                marketSubscriberComponent.consumptionStorage, marketSubscriberComponent.productStorage));
    }

}
