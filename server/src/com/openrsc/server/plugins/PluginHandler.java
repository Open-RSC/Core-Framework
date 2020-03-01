package com.openrsc.server.plugins;

import com.openrsc.server.Server;
import com.openrsc.server.event.custom.ShopRestockEvent;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.event.rsc.PluginTickEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.NamedThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

/**
 * Initiates plug-ins that implements some listeners
 *
 * @author Peeter, design idea xEnt
 * @author n0m - changes made:
 * Plugins are now loaded from external jar
 * Plugins are now reloadable.
 * No longer need to add paths to plugins.
 */
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
	private ArrayList<Class<?>> loadedClassFiles = new ArrayList<Class<?>>();

	private Object defaultHandler = null;
	private Map<String, Set<Object>> actionPlugins;
	private Map<String, Set<Object>> executivePlugins;
	private List<Class<?>> knownInterfaces;
	private Map<String, Object> loadedPlugins;

	public PluginHandler (final Server server) {
		this.server = server;
		threadFactory = new NamedThreadFactory(getServer().getName()+" : PluginThread");
	}

	public void loadJar() throws Exception {
		String pathToJar = "./plugins.jar";
		boolean jarExists = new File(pathToJar).isFile();
		if (jarExists) {
			JarFile jarFile = new JarFile(pathToJar);
			URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
			urlClassLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());

			Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements()) {
				JarEntry je = enumeration.nextElement();
				if (je.getName().endsWith(".class") && !je.getName().contains("$")) {
					String className = je.getName().substring(0,
						je.getName().length() - 6).replace('/', '.');
					Class<?> c = urlClassLoader.loadClass(className);
					loadedClassFiles.add(c);
				}
			}
			jarFile.close();
		}
	}

	public List<Class<?>> loadClasses(final String pckgname)
		throws ClassNotFoundException {
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		final ArrayList<File> directories = new ArrayList<File>();
		try {
			final ClassLoader cld = Thread.currentThread()
				.getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			final Enumeration<URL> resources = cld.getResources(pckgname
				.replace('.', '/'));
			while (resources.hasMoreElements()) {
				final URL res = resources.nextElement();
				if (res.getProtocol().equalsIgnoreCase("jar")) {
					final JarURLConnection conn = (JarURLConnection) res
						.openConnection();
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
					directories.add(new File(URLDecoder.decode(res.getPath(),
						"UTF-8")));
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
						classes.add(Class.forName(pckgname + '.'
							+ file.substring(0, file.length() - 6)));
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
		for (final Class<?> interfce : loadInterfaces("com.openrsc.server.plugins.listeners.action")) {
			final String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
			knownInterfaces.add(interfce);
			for (final Class<?> plugin : loadedClassFiles) {
				if (!interfce.isAssignableFrom(plugin)) {
					continue;
				}

				Object instance = plugin.getConstructor().newInstance();
				if (instance instanceof DefaultHandler && defaultHandler == null) {
					defaultHandler = instance;
					continue;
				}
				if (instance instanceof ShopInterface) {
					final ShopInterface it = (ShopInterface) instance;

					for (final Shop s : it.getShops(getServer().getWorld())) {
						getServer().getWorld().getShops().add(s);
						getServer().getGameEventHandler().add(new ShopRestockEvent(getServer().getWorld(), s));
					}
				}
				if (loadedPlugins.containsKey(instance.getClass().getName())) {
					instance = loadedPlugins.get(instance.getClass().getName());
				} else {
					loadedPlugins.put(instance.getClass().getName(), instance);
					if (instance instanceof MiniGameInterface) {
						final MiniGameInterface m = (MiniGameInterface) instance;
						try {
							getServer().getWorld().registerMiniGame(m);
						} catch (final Exception e) {
							LOGGER.error("Error registering minigame " + m.getMiniGameName());
							LOGGER.catching(e);
							continue;
						}
					}
				}
				if (actionPlugins.containsKey(interfceName)) {
					final Set<Object> data = actionPlugins.get(interfceName);
					data.add(instance);
					actionPlugins.put(interfceName, data);
				} else {
					final Set<Object> data = new HashSet<Object>();
					data.add(instance);
					actionPlugins.put(interfceName, data);
				}
			}
		}
		for (final Class<?> interfce : loadInterfaces("com.openrsc.server.plugins.listeners.executive")) {
			final String interfceName = interfce.getName().substring(
				interfce.getName().lastIndexOf(".") + 1);
			knownInterfaces.add(interfce);
			for (final Class<?> plugin : loadedClassFiles) {
				if (!interfce.isAssignableFrom(plugin)) {
					continue;
				}
				Object instance = plugin.getConstructor().newInstance();
				if (loadedPlugins.containsKey(instance.getClass().getName())) {
					instance = loadedPlugins.get(instance.getClass().getName());
				} else {
					loadedPlugins.put(instance.getClass().getName(), instance);

					if (Arrays.asList(instance.getClass().getInterfaces())
							.contains(MiniGameInterface.class)) {
						final MiniGameInterface m = (MiniGameInterface) instance;
						try {
							getServer().getWorld().registerMiniGame(
								(MiniGameInterface) instance);
						} catch (final Exception e) {
							LOGGER.error(
								"Error registering minigame "
									+ m.getMiniGameName());
							LOGGER.catching(e);
							continue;
						}
					}
				}

				if (executivePlugins.containsKey(interfceName)) {
					final Set<Object> data = executivePlugins.get(interfceName);
					data.add(instance);
					executivePlugins.put(interfceName, data);
				} else {
					final Set<Object> data = new HashSet<Object>();
					data.add(instance);
					executivePlugins.put(interfceName, data);
				}
			}
		}

		//Look for quests specifically
		Class<?> interfce = QuestInterface.class;
		for (final Class<?> plugin : loadedClassFiles) {
			if (!interfce.isAssignableFrom(plugin)) {
				continue;
			}
			Object instance = plugin.getConstructor().newInstance();

			if (Arrays.asList(instance.getClass().getInterfaces())
			.contains(QuestInterface.class)) {
				final QuestInterface q = (QuestInterface) instance;
				try {
					getServer().getWorld().registerQuest(
						(QuestInterface) instance);
				} catch (final Exception e) {
					LOGGER.error(
						"Error registering quest "
							+ q.getQuestName());
					LOGGER.catching(e);
					continue;
				}
			}
		}

		LOGGER.info("\t Loaded {}", box(getServer().getWorld().getQuests().size()) + " Quests.");
		LOGGER.info("\t Loaded {}", box(getServer().getWorld().getMiniGames().size()) + " MiniGames.");
		LOGGER.info("\t Loaded total of {}", box(loadedPlugins.size()) + " plugin handlers.");
	}

	private List<Class<?>> loadInterfaces(final String thePackage)
		throws ClassNotFoundException {
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

		actionPlugins = new HashMap<String, Set<Object>>();
		executivePlugins = new HashMap<String, Set<Object>>();
		knownInterfaces = new ArrayList<Class<?>>();
		loadedPlugins = new HashMap<String, Object>();
		defaultHandler = null;
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(threadFactory);

		loadJar();
		initPlugins();
	}

	public void unload() throws IOException {
		reloading = true;

		urlClassLoader.close();
		getExecutor().shutdown();

		getServer().getWorld().getQuests().clear();
		getServer().getWorld().getMiniGames().clear();
		getServer().getWorld().getShops().clear();

		actionPlugins.clear();
		executivePlugins.clear();
		knownInterfaces.clear();
		loadedPlugins.clear();
		loadedClassFiles.clear();

		actionPlugins = null;
		executivePlugins = null;
		knownInterfaces = null;
		executor = null;
		loadedClassFiles = null;

		defaultHandler = null;
	}

	public boolean handlePlugin(final World world, final String interfce, final Object[] data) {
		return handlePlugin(null, world, interfce, data, null);
	}

	public boolean handlePlugin(final Mob owner, final String interfce, final Object[] data) {
		return handlePlugin(owner, owner.getWorld(), interfce, data, null);
	}

	public boolean handlePlugin(final Mob owner, final String interfce, final Object[] data, final WalkToAction walkToAction) {
		return handlePlugin(owner, owner.getWorld(), interfce, data, walkToAction);
	}

	public boolean handlePlugin(final Mob owner, final World world, final String interfce, final Object[] data, final WalkToAction walkToAction) {
		synchronized(actionPlugins) {
			if (reloading) {
				for (Object o : data) {
					if (o instanceof Player) {
						((Player) o).message("Plugins are being updated, please wait.");
					}
				}
				return false;
			}
			boolean shouldBlockDefault = false;
			if (executivePlugins.containsKey(interfce + "ExecutiveListener")) {
				for (final Object c : executivePlugins.get(interfce + "ExecutiveListener")) {
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

	private void invokePluginAction(final Mob owner, final World world, final String interfce, final Object cls, final Object[] data, final WalkToAction walkToAction) {
		if (reloading) {
			return;
		}
		if (actionPlugins.containsKey(interfce + "Listener")) {
			try {
				final Class<?>[] dataClasses = new Class<?>[data.length];
				int i = 0;
				for (final Object o : data) {
					dataClasses[i++] = o.getClass();
				}

				try {
					final Method m = cls.getClass().getMethod("on" + interfce, dataClasses);
					final String pluginName = cls.getClass().getSimpleName() + "." + m.getName();
					final PluginTickEvent e = new PluginTickEvent(world, owner, pluginName, walkToAction, new PluginTask(world) {
						@Override
						public int action() {
							try {
								LOGGER.info("Executing Plugin : Tick " + getWorld().getServer().getCurrentTick() + " : " + pluginName + " : " + Arrays.deepToString(data));
								Npc interactingNpc = null;
								if (owner != null && owner.isPlayer())
									interactingNpc = ((Player)owner).getInteractingNpc();
								m.invoke(cls, data);
								if (interfce.equalsIgnoreCase("TalkToNpc")) {
									((Player)owner).setBusy(false);
									if (interactingNpc != null)
										interactingNpc.setBusy(false);
									if (((Player)owner).getInteractingNpc() != null)
										((Player)owner).getInteractingNpc().setBusy(false);
								}
								return 1;
							} catch (final Exception ex) {
								LOGGER.catching(ex);
								return 0;
							}
						}
					});

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

	public Map<String, Set<Object>> getActionPlugins() {
		return actionPlugins;
	}

	public Map<String, Set<Object>> getExecutivePlugins() {
		return executivePlugins;
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
