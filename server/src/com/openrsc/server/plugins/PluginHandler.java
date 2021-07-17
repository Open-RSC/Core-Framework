package com.openrsc.server.plugins;

import com.openrsc.server.Server;
import com.openrsc.server.event.custom.ShopRestockEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.event.rsc.PluginTickEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.NamedThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.apache.logging.log4j.util.Unbox.box;

public final class PluginHandler {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;

	private ThreadPoolExecutor executor;
	private final ThreadFactory threadFactory;

	private URLClassLoader urlClassLoader;
	private boolean reloading = true;
	private ArrayList<Class<?>> loadedClassFiles;

	private Object defaultHandler = null;
	private List<Class<?>> knownInterfaces;
	private Map<String, Set<Object>> plugins;

	public PluginHandler (final Server server) {
		this.server = server;
		this.threadFactory = new NamedThreadFactory(server.getName()+" : PluginThread", server.getConfig());
		this.knownInterfaces = new ArrayList<>();
		this.plugins = new HashMap<>();
		this.loadedClassFiles = new ArrayList<>();
	}

	public void loadJar() throws Exception {
		final String pathToJar = "./plugins.jar";
		final boolean jarExists = new File(pathToJar).isFile();
		if (jarExists) {
			final JarFile jarFile = new JarFile(pathToJar);
			final URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
			urlClassLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());

			final Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements()) {
				final JarEntry je = enumeration.nextElement();
				if (je.getName().endsWith(".class") && !je.getName().contains("$")) {
					final String className = je.getName().substring(0, je.getName().length() - 6).replace('/', '.');
					final Class<?> c = urlClassLoader.loadClass(className);
					loadedClassFiles.add(c);
				}
			}
			jarFile.close();
		}
	}

	public List<Class<?>> loadClasses(final String pckgname) throws ClassNotFoundException {
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		final ArrayList<File> directories = new ArrayList<File>();
		try {
			final ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			final Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
			while (resources.hasMoreElements()) {
				final URL res = resources.nextElement();
				if (res.getProtocol().equalsIgnoreCase("jar")) {
					final JarURLConnection conn = (JarURLConnection) res.openConnection();
					final JarFile jar = conn.getJarFile();
					for (final JarEntry e : Collections.list(jar.entries())) {
						if (e.getName().startsWith(pckgname.replace('.', '/'))
							&& e.getName().endsWith(".class")
							&& !e.getName().contains("$")) {
							final String className = e.getName()
								.replace("/", ".")
								.substring(0, e.getName().length() - 6);
							classes.add(Class.forName(className));
						}
					}
				} else {
					directories.add(new File(URLDecoder.decode(res.getPath(), "UTF-8")));
				}
			}
		} catch (final NullPointerException x) {
			throw new ClassNotFoundException(
				pckgname
					+ " does not appear to be a valid package (Null pointer exception)");
		} catch (final UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(
				pckgname
					+ " does not appear to be a valid package (Unsupported encoding)");
		} catch (final IOException ioex) {
			throw new ClassNotFoundException(
				"IOException was thrown when trying to get all resources for "
					+ pckgname);
		}

		for (final File directory : directories) {
			if (directory.exists()) {
				final String[] files = directory.list();
				for (final String file : files) {
					if (file.endsWith(".class")) {
						classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
					}
				}
			} else {
				throw new ClassNotFoundException(pckgname + " ("
					+ directory.getPath()
					+ ") does not appear to be a valid package");
			}
		}
		return classes;
	}

	public void initPlugins() throws Exception {
		int countPlugins = 0;

		HashMap<Class<?>, Object> pluginInstances = new HashMap<>();
		for (final Class<?> interfce : loadInterfaces("com.openrsc.server.plugins.triggers")) {
			knownInterfaces.add(interfce);
			for (final Class<?> plugin : loadedClassFiles) {
				if (!interfce.isAssignableFrom(plugin)) {
					continue;
				}

				if (!pluginInstances.containsKey(plugin)) {
					pluginInstances.put(plugin, plugin.getConstructor().newInstance());
				}

				final Object instance = pluginInstances.get(plugin);
				final String interfceName = interfce.getSimpleName();

				if (!plugins.containsKey(interfceName)) {
					plugins.put(interfceName, new HashSet<>());
				}

				if (!plugins.get(interfceName).contains(instance)) {

					if (instance instanceof DefaultHandler && defaultHandler == null) {
						defaultHandler = instance;
						continue;
					}

					plugins.get(interfceName).add(instance);

					if (instance instanceof AbstractShop && interfceName.equals("TalkNpcTrigger")) {
						final AbstractShop it = (AbstractShop) instance;

						for (final Shop s : it.getShops(getServer().getWorld())) {
							getServer().getWorld().getShops().add(s);
							getServer().getGameEventHandler().add(new ShopRestockEvent(getServer().getWorld(), s));
						}
					}
				}
			}
		}

		//Look for quests/minigames specifically
		final Class<?>[] interfces = {QuestInterface.class, MiniGameInterface.class};
		for (final Class<?> interfce : interfces) {
			for (final Class<?> plugin : loadedClassFiles) {
				if (!interfce.isAssignableFrom(plugin)) {
					continue;
				}

				if (!pluginInstances.containsKey(plugin)) {
					pluginInstances.put(plugin, plugin.getConstructor().newInstance());
				}

				final Object instance = pluginInstances.get(plugin);

				if (Arrays.asList(instance.getClass().getInterfaces()).contains(QuestInterface.class)) {
					final QuestInterface q = (QuestInterface) instance;
					try {
						getServer().getWorld().registerQuest(q);
					} catch (final Exception e) {
						LOGGER.catching(e);
						continue;
					}
				} else if (Arrays.asList(instance.getClass().getInterfaces()).contains(MiniGameInterface.class)) {
					final MiniGameInterface m = (MiniGameInterface) instance;
					try {
						getServer().getWorld().registerMiniGame(m);
					} catch (final Exception e) {
						LOGGER.catching(e);
						continue;
					}
				}
			}
		}
		// Call initialization for Registrars
		for (final Class<?> plugin : loadedClassFiles) {
			if (AbstractRegistrar.class.isAssignableFrom(plugin)) {
				final Method m = plugin.getMethod("init", Server.class);
				final Object instance = plugin.getConstructor().newInstance();
				m.invoke(instance, getServer());
			}
		}

		LOGGER.info("Loaded {}", box(getServer().getWorld().getQuests().size()) + " Quests.");
		LOGGER.info("Loaded {}", box(getServer().getWorld().getMiniGames().size()) + " MiniGames.");
		LOGGER.info("Loaded total of {}", pluginInstances.size() + " plugin handlers.");
	}

	private List<Class<?>> loadInterfaces(final String thePackage) throws ClassNotFoundException {
		final List<Class<?>> classList = new ArrayList<Class<?>>();
		for (final Class<?> discovered : loadClasses(thePackage)) {
			if (discovered.isInterface()) {
				classList.add(discovered);
			}
		}
		return classList;
	}

	public void load() throws Exception {
		// TODO: Separate static loading from class based loading.
		reloading = false;

		defaultHandler = null;
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(threadFactory);

		loadJar();
		initPlugins();
	}

	public void unload() throws IOException {
		reloading = true;

		urlClassLoader.close();
		getExecutor().shutdown();
		try {
			final boolean terminationResult = getExecutor().awaitTermination(1, TimeUnit.MINUTES);
			if (!terminationResult) {
				LOGGER.error("PluginHandler thread pool termination failed");
			}
		} catch (final InterruptedException e) {
			LOGGER.catching(e);
		}

		getServer().getWorld().getQuests().clear();
		getServer().getWorld().getMiniGames().clear();
		getServer().getWorld().getShops().clear();

		knownInterfaces.clear();
		plugins.clear();
		loadedClassFiles.clear();

		executor = null;
		defaultHandler = null;
	}

	public boolean handlePlugin(final World world, final String interfce, final Object[] data) {
		return handlePlugin(null, world, interfce, data, null);
	}

	public boolean handlePlugin(final Player owner, final String interfce, final Object[] data) {
		return handlePlugin(owner, owner.getWorld(), interfce, data, null);
	}

	public boolean handlePlugin(final Player owner, final String interfce, final Object[] data, final WalkToAction walkToAction) {
		return handlePlugin(owner, owner.getWorld(), interfce, data, walkToAction);
	}

	public boolean handlePlugin(final Player owner, final World world, final String interfce, final Object[] data, final WalkToAction walkToAction) {
		synchronized(plugins) {
			if (reloading) {
				for (Object o : data) {
					if (o instanceof Player) {
						((Player) o).message("Plugins are being updated, please wait.");
					}
				}
				return false;
			}
			boolean shouldBlockDefault = false;

			if (plugins.containsKey(interfce + "Trigger")) {
				for (final Object c : plugins.get(interfce + "Trigger")) {
					try {
						final Class<?>[] dataClasses = new Class<?>[data.length];
						int i = 0;
						for (final Object o : data) {
							dataClasses[i++] = o.getClass();
						}
						final Method m = c.getClass().getMethod("block" + interfce, dataClasses);
						final boolean shouldBlock = (Boolean) m.invoke(c, data);
						if (shouldBlock) {
							shouldBlockDefault = true;
							invokePluginAction(owner, world, interfce, c, data, walkToAction);
						}
					} catch (final Exception e) {
						LOGGER.catching(e);
					}
				}
			}

			try {
				if (!shouldBlockDefault) {
					invokePluginAction(owner, world, interfce, defaultHandler, data, walkToAction);
				}
			} catch (final Exception e) {
				LOGGER.catching(e);
			}

			return shouldBlockDefault;
		}
	}

	private void invokePluginAction(final Player owner, final World world, final String interfce, final Object cls, final Object[] data, final WalkToAction walkToAction) {
		if (reloading) {
			return;
		}
		if (plugins.containsKey(interfce + "Trigger")) {
			try {
				final Class<?>[] dataClasses = new Class<?>[data.length];
				int i = 0;
				for (final Object o : data) {
					dataClasses[i++] = o.getClass();
				}

				try {

					final Method m = cls.getClass().getMethod("on" + interfce, dataClasses);
					final String pluginName = cls.getClass().getSimpleName() + "." + m.getName();

					boolean shouldFire = true;
					HashMap<String, GameTickEvent> events = getServer().getGameEventHandler().getEvents();
					for (GameTickEvent e : events.values()) {
						if (e instanceof PluginTickEvent) {
							PluginTickEvent pluginTickEvent = (PluginTickEvent)e;

							if (pluginTickEvent.getPluginName().equals(pluginName) && pluginTickEvent.getOwner().equals(owner) ) {
								shouldFire = false;
							}
						}
					}

					if (!shouldFire) return;

					final PluginTask task = new PluginTask(world, owner, interfce, data) {
						@Override
						public int action() {
							try {
								LOGGER.info("Tick " + getWorld().getServer().getCurrentTick() + " : " + pluginName + " : " + Arrays.deepToString(data));
								m.invoke(cls, data);
								return 1;
							} catch (final InvocationTargetException ex) {
								if (ex.getCause() instanceof PluginInterruptedException) {
									// PluginTask.call() will do stop() after this which will correctly shut down the Plugin.
									//LOGGER.info("Plugin Interrupted: " + ex.getMessage());
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
					final PluginTickEvent e = new PluginTickEvent(world, owner, pluginName, walkToAction, task);

					getServer().getGameEventHandler().add(e);
				} catch (final NoSuchMethodException ex) {
					LOGGER.info(ex.getMessage());
					LOGGER.info(cls.getClass().getSimpleName() + ".on" + interfce + " : " + Arrays.deepToString(data));
					// getClass().getMethod() failed because there is an executive listener, but NOT a corresponding action listener, OR
					// there is no action listener defined in Default plugin
				}
			} catch (final Exception e) {
				System.err.println("Exception at plugin handling: ");
				LOGGER.catching(e);
			}
		}
	}

	public Future<Integer> submitPluginTask(final PluginTask pluginTask) {
		return getExecutor().submit(pluginTask);
	}

	public List<Class<?>> getKnownInterfaces() {
		return knownInterfaces;
	}

	public final Server getServer() {
		return server;
	}

	private final ThreadPoolExecutor getExecutor() {
		return executor;
	}
}
