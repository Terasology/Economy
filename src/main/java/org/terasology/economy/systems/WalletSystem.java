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
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.events.UpdateWalletEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;

@Share(WalletSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class WalletSystem extends BaseComponentSystem {

    @In
    private EntityManager entityManager;

    @In
    private AssetManager assetManager;

    @In
    private NUIManager nuiManager;

    @In
    private LocalPlayer localPlayer;

    public EntityRef wallet;

    @Override
    public void postBegin() {
        CurrencyStorageComponent component = new CurrencyStorageComponent(100);
        wallet = entityManager.create();
        wallet.addComponent(component);

        nuiManager.getHUD().addHUDElement("walletHud");
    }

    @ReceiveEvent(components = {CurrencyStorageComponent.class})
    public void onUpdateWallet(UpdateWalletEvent event, EntityRef entity) {

        // TODO: Probably use the resource draw/delete/create events to handle currency?

        CurrencyStorageComponent component = entity.getComponent(CurrencyStorageComponent.class);
        component.amount += event.getDelta();
        entity.saveComponent(component);
    }

    public boolean isValidTransaction(int delta) {
        int balance = wallet.getComponent(CurrencyStorageComponent.class).amount;
        return (balance + delta >= 0);
    }
}
