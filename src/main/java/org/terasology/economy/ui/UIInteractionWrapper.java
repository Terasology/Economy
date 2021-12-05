// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.economy.ui;

import org.joml.Vector2i;
import org.terasology.engine.utilities.Assets;
import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.UIWidget;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.widgets.ActivateEventListener;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.nui.widgets.TooltipLineRenderer;
import org.terasology.nui.widgets.UIList;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper widget used in the shop screen.
 * Displays a separate widget whilst capturing user interaction with it.
 * When the icon corresponding to the item is clicked, a tooltip containing the item's name and additional information is displayed.
 * <p>
 * All size values etc are determined by the content widget.
 * <p>
 * NOTE: We can't just use the {@code ItemIcon} widget that this contains, because we cannot assign a listener to it.
 * We need to assign a listener to it, because clicking on this widget indicated that this item is selected.
 * This is visible from the line in ShopScreen#addBlocks
 * <pre>
 * wrapper.setListener(widget -> handleBlockSelected(block));
 * </pre>
 * and the line in ShopScreen#addItems
 * <pre>
 * wrapper.setListener(widget -> handlePrefabSelected(item));
 * </pre>
 * The listener indicates which shop entry was selected, as well as calling the handler for the specific type, item vs block.
 */
public class UIInteractionWrapper extends CoreWidget {
    private UIWidget content;
    private ActivateEventListener listener;
    private final UIList<TooltipLine> tooltip;

    private final InteractionListener interactionListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (listener != null) {
                listener.onActivated(UIInteractionWrapper.this);
            }
            return true;
        }
    };

    public UIInteractionWrapper() {
        tooltip = new UIList<>();
        tooltip.setInteractive(false);
        tooltip.setSelectable(false);
        final UISkin defaultSkin = Assets.getSkin("Inventory:itemTooltip").get();
        tooltip.setSkin(defaultSkin);
        tooltip.setItemRenderer(new TooltipLineRenderer(defaultSkin));
        tooltip.bindList(new DefaultBinding<>(new ArrayList<>()));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(content);
        canvas.addInteractionRegion(interactionListener);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return canvas.calculatePreferredSize(content);
    }

    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        return content.getMaxContentSize(canvas);
    }

    @Override
    public UIWidget getTooltip() {
        if (tooltip.getList().size() > 0) {
            return tooltip;
        } else {
            return null;
        }
    }

    /**
     * @param content The widget to display.
     */
    public void setContent(UIWidget content) {
        this.content = content;
    }

    /**
     * @param lines The tooltip lines to display
     */
    public void setTooltipLines(List<TooltipLine> lines) {
        tooltip.setList(lines);
    }


    /**
     * @param listener The listener to use when this widget is clicked.
     */
    public void setListener(ActivateEventListener listener) {
        this.listener = listener;
    }
}
