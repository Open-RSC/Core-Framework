package com.openrsc.server.plugins.handler;

import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.entity.player.Player;

public interface IPluginHandler {
    default boolean handlePlugin(Class<?> pluginType) {
        return handlePlugin(pluginType, new Object[]{});
    }

    default boolean handlePlugin(Class<?> pluginType, Object[] data) {
        return handlePlugin(pluginType, null, data);
    }

    default boolean handlePlugin(Class<?> pluginType, Player owner, Object[] data) {
        return handlePlugin(pluginType, owner, data, null);
    }

    boolean handlePlugin(Class<?> pluginType, Player owner, Object[] data, WalkToAction walkToAction);
}
