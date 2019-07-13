/*
 * Copyright 2019 MovingBlocks
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

import org.terasology.assets.management.AssetManager;
import org.terasology.economy.components.CurrencyComponent;
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.components.MarketSubscriberComponent;
import org.terasology.economy.events.SubscriberRegistrationEvent;
import org.terasology.economy.handler.CurrencyStorageHandler;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.HashSet;
import java.util.Set;

@Share(CurrencySystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class CurrencySystem extends BaseComponentSystem {

    @In
    private EntityManager entityManager;

    @In
    private AssetManager assetManager;

    private EntityRef bank;
    private Set<EntityRef> currencies = new HashSet<>();

    @Override
    public void postBegin() {
        Set<Prefab> loadedPrefabs = assetManager.getLoadedAssets(Prefab.class);
        for (Prefab prefab : loadedPrefabs) {
            if (prefab.hasComponent(CurrencyStorageComponent.class)) {
                bank = entityManager.create(prefab);
                CurrencyStorageComponent component = bank.getComponent(CurrencyStorageComponent.class);
                bank.saveComponent(component);
            }

            if (prefab.hasComponent(CurrencyComponent.class)) {
                EntityRef currency = entityManager.create(prefab);
                MarketSubscriberComponent component = currency.getComponent(MarketSubscriberComponent.class);
                component.productStorage = bank;
                component.productionInterval = 2;
                currency.saveComponent(component);

                currency.send(new SubscriberRegistrationEvent());
                currencies.add(currency);
            }
        }
    }
}
