package com.openrsc.server.plugins.handler;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.openrsc.server.Server;
import com.openrsc.server.event.custom.ShopRestockEvent;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.event.rsc.PluginTickEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.*;
import com.openrsc.server.plugins.io.PluginJarLoader;
import com.openrsc.server.util.NamedThreadFactory;
import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.openrsc.server.plugins.Functions.delay;
import static org.apache.logging.log4j.util.Unbox.box;

public final class PluginHandler implements IPluginHandler {
    /**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private final Server server;
    private final ThreadFactory threadFactory;
    private final Multimap<Class<?>, Object> triggerTypeToInstance = HashMultimap.create();
    private final Set<Class<?>> triggerTypes = new HashSet<>();
    private final ClassToInstanceMap<Object> pluginInstances = MutableClassToInstanceMap.create();
    private final PluginJarLoader loader = new PluginJarLoader();
    private final Injector injector;
    private ThreadPoolExecutor executor;
    private boolean reloading = true;
    private Object defaultHandler = null;

    public PluginHandler(final Server server) {
        this.server = server;
        this.threadFactory = new NamedThreadFactory(server.getName() + " : PluginThread", server.getConfig());
        try {
            triggerTypes.addAll(loader.loadTriggers("com.openrsc.server.plugins.triggers"));
        } catch (Exception ex) {
            LOGGER.error("Unable to load triggers: ", ex);
        }

        this.injector = Guice.createInjector(binder -> {
            binder.bind(Server.class).toInstance(server);
            binder.bind(World.class).toProvider(server::getWorld).in(Scopes.SINGLETON);
        });
    }

    public void initPlugins() throws Exception {
        // Iterate over the classes picked up by the class path scan
        for (final Class<?> pluginType : loader.getLoadedClasses()) {
            if (DefaultHandler.class.isAssignableFrom(pluginType) && defaultHandler == null) {
                defaultHandler = getPluginInstance(pluginType);
                continue;
            }

            if (MiniGameInterface.class.isAssignableFrom(pluginType)) {
                server.getWorld().registerMiniGame((MiniGameInterface) getPluginInstance(pluginType));
            }

            if (QuestInterface.class.isAssignableFrom(pluginType)) {
                server.getWorld().registerQuest((QuestInterface) getPluginInstance(pluginType));
            }

            if (AbstractRegistrar.class.isAssignableFrom(pluginType)) {
                final Method m = pluginType.getMethod("init", Server.class);
                final Object instance = getPluginInstance(pluginType);
                m.invoke(instance, server);
            }

            // Get a list of the triggers implemented by the plugin type
            Set<Class<?>> implementedTriggers =
                    StreamSupport.stream(
                            ClassUtils.hierarchy(pluginType, ClassUtils.Interfaces.INCLUDE).spliterator(),
                            true
                    )
                    .filter(triggerTypes::contains)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // If there are no triggers implemented by this, we should do nothing
            if (implementedTriggers.isEmpty()) {
                continue;
            }

            // Triggers found, create an instance
            Object pluginInstance = getPluginInstance(pluginType);

            if (pluginInstance instanceof AbstractShop) {
                final AbstractShop shopPlugin = (AbstractShop) pluginInstance;

                for (final Shop shop : shopPlugin.getShops(server.getWorld())) {
                    server.getWorld().getShops().add(shop);
                    server.getGameEventHandler().add(new ShopRestockEvent(server.getWorld(), shop));
                }
            }

            // Register this plugin instance with all the associated triggers
            for (Class<?> triggerType : implementedTriggers) {
                triggerTypeToInstance.put(triggerType, pluginInstance);
            }
        }

        LOGGER.info("Loaded {}", box(server.getWorld().getQuests().size()) + " Quests.");
        LOGGER.info("Loaded {}", box(server.getWorld().getMiniGames().size()) + " MiniGames.");
        LOGGER.info("Loaded total of {}", pluginInstances.size() + " plugin handlers.");
    }

    public <T> T getPluginInstance(Class<T> type) {
        if (!pluginInstances.containsKey(type)) {
            final T instance = injector.getInstance(type);
            pluginInstances.putInstance(type, instance);
            return instance;
        }
        return (T) pluginInstances.getInstance(type);
    }

    public Collection<Class<?>> explodeClassTree(Class<?> type) {
        if (type == null) {
            return Collections.emptySet();
        }

        if (type.getInterfaces().length == 0 && type.getSuperclass() == Object.class) {
            return Collections.singleton(type);
        }
        final Set<Class<?>> interfaces = Arrays.stream(type.getInterfaces()).collect(Collectors.toSet());
        interfaces.forEach(supertype -> interfaces.addAll(explodeClassTree(supertype)));
        return interfaces;
    }

    public void load() throws Exception {
        // TODO: Separate static loading from class based loading.
        reloading = false;

        defaultHandler = null;
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(threadFactory);

        loader.loadJar();
        initPlugins();
    }

    public void unload() throws IOException {
        reloading = true;

        getExecutor().shutdown();
        try {
            final boolean terminationResult = getExecutor().awaitTermination(1, TimeUnit.MINUTES);
            if (!terminationResult) {
                LOGGER.error("PluginHandler thread pool termination failed");
            }
        } catch (final InterruptedException e) {
            LOGGER.catching(e);
        }

        server.getWorld().getQuests().clear();
        server.getWorld().getMiniGames().clear();
        server.getWorld().getShops().clear();

        triggerTypeToInstance.clear();
        pluginInstances.clear();
        loader.clear();

        executor = null;
        defaultHandler = null;
    }

    public boolean handlePlugin(Class<?> triggerType, Player owner, Object[] data, WalkToAction walkToAction) {
        final String simpleName = triggerType.getSimpleName();
        String triggerName = simpleName.substring(0, simpleName.indexOf("Trigger"));
        synchronized (triggerTypeToInstance) {
            if (reloading) {
                Arrays.stream(data)
                        .filter(obj -> obj instanceof Player)
                        .findAny()
                        .map(Player.class::cast)
                        .ifPresent(player -> player.message("Plugins are being updated, please wait."));
                return false;
            }
            boolean shouldBlockDefault = false;

            Collection<Object> triggerInstances = triggerTypeToInstance.get(triggerType);
            if (triggerInstances.isEmpty()) {
                LOGGER.warn("Unable to handle unknown plugin: {}", simpleName);
            } else {
                for (Object trigger : triggerInstances) {
                    try {
                        final Class<?>[] dataClasses = Arrays.stream(data)
                                .map(Object::getClass)
                                .toArray(Class<?>[]::new);
                        final Method method = triggerType.getMethod("block" + triggerName, dataClasses);
                        final boolean shouldBlock = (Boolean) method.invoke(trigger, data);
                        if (shouldBlock) {
                            shouldBlockDefault = true;
                            invokePluginAction(triggerType, owner, trigger, data, walkToAction);
                        }
                    } catch (final Exception e) {
                        LOGGER.catching(e);
                    }
                }
            }

            try {
                if (!shouldBlockDefault) {
                    invokePluginAction(triggerType, owner, defaultHandler, data, walkToAction);
                }
            } catch (final Exception e) {
                LOGGER.catching(e);
            }

            return shouldBlockDefault;
        }
    }

    private void invokePluginAction(
            Class<?> triggerType,
            Player player,
            Object triggerInstance,
            Object[] data,
            WalkToAction walkToAction
    ) {
        if (reloading) {
            return;
        }

        try {
            final Class<?>[] dataClasses = Arrays.stream(data)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);

            final String simpleName = triggerType.getSimpleName();
            String triggerName = simpleName.substring(0, simpleName.indexOf("Trigger"));
            try {
                final Method method = triggerType.getMethod("on" + triggerName, dataClasses);
                final String pluginName = triggerInstance.getClass().getSimpleName() + "." + method.getName();

                final PluginTask task = new PluginTask(server.getWorld(), player, triggerName, data) {
                    @Override
                    public int action() {
                        try {
                            LOGGER.info("Tick " + getWorld().getServer().getCurrentTick() + " : " + pluginName + " : " + Arrays.deepToString(data));
                            method.invoke(triggerInstance, data);
                            return 1;
                        } catch (final InvocationTargetException ex) {
                            if (ex.getCause() instanceof PluginInterruptedException) {
                                // PluginTask.call() will do stop() after this which will correctly shut down the Plugin.
                                return 1;
                            } else {
                                LOGGER.catching(ex);
                                return 0;
                            }
                        } catch (final Exception ex) {
                            LOGGER.catching(ex);
                            return 0;
                        }
                    }
                };

                final PluginTickEvent e = new PluginTickEvent(server.getWorld(), player, pluginName, walkToAction, task);

                boolean hasEvent = server.getGameEventHandler().has(e);
                // On Jagex Original Clients there was no immediate menu cancels when clicking out of menu
				// Addendum circa 14th March 2023: There wasn't an extra tick delay like in the codebase prior.
				// We just need to stop the plugin now and replace it with a new one.
                if (player != null && player.canceledMenuHandler && hasEvent) {
					player.canceledMenuHandler = false;
					for (PluginTask plugin : player.getOwnedPlugins()) {
						if (plugin.getScriptContext().getInteractingNpc() != null) {
							plugin.getPluginTickEvent().stop();
							server.getGameEventHandler().remove(plugin.getPluginTickEvent());
							break;
						}
					}
					server.getGameEventHandler().add(e);
				} else {
					server.getGameEventHandler().add(e);
				}
            } catch (final NoSuchMethodException ex) {
                LOGGER.info(ex.getMessage());
                LOGGER.info(simpleName + ".on" + triggerName + " : " + Arrays.deepToString(data));
                // getClass().getMethod() failed because there is an executive listener, but NOT a corresponding action listener, OR
                // there is no action listener defined in Default plugin
            }
        } catch (final Exception e) {
            System.err.println("Exception at plugin handling: ");
            LOGGER.catching(e);
        }
    }

    public Future<Integer> submitPluginTask(final PluginTask pluginTask) {
        return getExecutor().submit(pluginTask);
    }

    private ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
