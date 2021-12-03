// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.economy.ui;

import org.terasology.economy.components.AllowShopScreenComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.input.ButtonState;
import org.terasology.module.inventory.input.InventoryButton;

@RegisterSystem(RegisterMode.CLIENT)
public class MarketUiClientSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;

    /**
     * Show an alternative to the {@link org.terasology.module.inventory.ui.InventoryScreen InventoryScreen} with an additional in-game shop
     * if the client also has the {@link AllowShopScreenComponent}.
     * <p>
     * This event handler is registered with a higher priority than
     * {@link org.terasology.module.inventory.systems.InventoryUIClientSystem#onToggleInventory(InventoryButton,
     * EntityRef)} to open a different screen in case the respective marker component is present.
     *
     * @param event the input event to open the inventory screen
     * @param entity the client entity requesting to open the inventory screen
     */
    @ReceiveEvent(components = {ClientComponent.class, AllowShopScreenComponent.class}, priority = EventPriority.PRIORITY_HIGH)
    public void onToggleInventory(InventoryButton event, EntityRef entity) {
        if (event.getState() == ButtonState.DOWN) {
            nuiManager.toggleScreen("Economy:shopScreen");
            event.consume();
        }
    }
}
