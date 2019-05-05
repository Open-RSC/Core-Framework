package com.openrsc.server.plugins;

import com.openrsc.server.Server;
import com.openrsc.server.event.custom.ShopRestockEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
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

	private static PluginHandler pluginHandler = null;
	private static boolean reloading;
	private Object defaultHandler = null;
	private URLClassLoader urlClassLoader;
	private Map<String, Set<Object>> actionPlugins = new HashMap<String, Set<Object>>();
	private Map<String, Set<Object>> executivePlugins = new HashMap<String, Set<Object>>();
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	private List<Class<?>> knownInterfaces = new ArrayList<Class<?>>();
	// private ExecutorService executor = Executors.newFixedThreadPool(2);
	private Map<String, Class<?>> queue = new ConcurrentHashMap<String, Class<?>>();

	public static PluginHandler getPluginHandler() {
		if (pluginHandler == null) {
			pluginHandler = new PluginHandler();
		}
		return pluginHandler;
	}

	public static List<Class<?>> loadClasses(final String pckgname)
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

	public boolean blockDefaultAction(final String interfce, final Object[] data) {
		return blockDefaultAction(interfce, data, true);
	}

	/**
	 * @param interfce
	 * @param data
	 * @param callAction
	 * @return
	 */

	public boolean blockDefaultAction(final String interfce,
									  final Object[] data, final boolean callAction) {
		if (reloading) {
			for (Object o : data) {
				if (o instanceof Player) {
					((Player) o).message("Plugins are being updated, please wait.");
				}
			}
			return false;
		}
		boolean shouldBlock = false, flagStop = false;
		queue.clear();
		if (executivePlugins.containsKey(interfce + "ExecutiveListener")) {
			for (final Object c : executivePlugins.get(interfce
				+ "ExecutiveListener")) {
				try {
					final Class<?>[] dataClasses = new Class<?>[data.length];
					int i = 0;
					for (final Object o : data) {
						dataClasses[i++] = o.getClass();
					}
					final Method m = c.getClass().getMethod("block" + interfce,
						dataClasses);
					shouldBlock = (Boolean) m.invoke(c, data);
					if (shouldBlock) {
						queue.put(interfce, c.getClass());
						flagStop = true;
					} else if (queue.size() > 1) {

					} else if (queue.isEmpty()) {
						queue.put(interfce, defaultHandler.getClass());
					}
				} catch (final Exception e) {
					LOGGER.catching(e);
				}
			}
		}

		if (callAction) {
			handleAction(interfce, data);
		}
		return flagStop; // not sure why it matters if its false or true
	}


	public Map<String, Set<Object>> getActionPlugins() {
		return actionPlugins;
	}

	public Map<String, Set<Object>> getExecutivePlugins() {
		return executivePlugins;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public List<Class<?>> getKnownInterfaces() {
		return knownInterfaces;
	}

	public void handleAction(final String interfce, final Object[] data) {
		if (reloading) {
			return;
		}
		if (actionPlugins.containsKey(interfce + "Listener")) {
			for (final Object c : actionPlugins.get(interfce + "Listener")) {
				try {
					final Class<?>[] dataClasses = new Class<?>[data.length];
					int i = 0;
					for (final Object o : data) {
						dataClasses[i++] = o.getClass();
					}

					final Method m = c.getClass().getMethod("on" + interfce,
						dataClasses);
					boolean go = false;

					if (queue.containsKey(interfce)) {
						for (final Class<?> clz : queue.values()) {
							if (clz.getName().equalsIgnoreCase(
								c.getClass().getName())) {
								go = true;
								break;
							}
						}
					} else {
						go = true;
					}

					if (go) {
						final FutureTask<Integer> task = new FutureTask<Integer>(
							new Callable<Integer>() {
								@Override
								public Integer call() throws Exception {
									try {
										LOGGER.info("Executing with : " + m.getName());
										m.invoke(c, data);
									} catch (Exception cme) {
										LOGGER.catching(cme);
									}
									return 1;
								}
							});
						getExecutor().execute(task);
					}
				} catch (final Exception e) {
					System.err.println("Exception at plugin handling: ");
					LOGGER.catching(e);
				}
			}
		}
	}

	public void initPlugins() throws Exception {

		final Map<String, Object> loadedPlugins = new HashMap<String, Object>();
		ArrayList<Class<?>> loadedClassFiles = new ArrayList<Class<?>>();

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

					for (final Shop s : it.getShops()) {
						World.getWorld().getShops().add(s);
						Server.getServer().getEventHandler().add(new ShopRestockEvent(s));
					}
				}
				if (loadedPlugins.containsKey(instance.getClass().getName())) {
					instance = loadedPlugins.get(instance.getClass().getName());
				} else {
					loadedPlugins.put(instance.getClass().getName(), instance);
					if (instance instanceof QuestInterface) {
						final QuestInterface q = (QuestInterface) instance;
						try {
							World.getWorld().registerQuest(q);
						} catch (final Exception e) {
							LOGGER.error("Error registering quest " + q.getQuestName());
							LOGGER.catching(e);
							continue;
						}
					} else if (instance instanceof MiniGameInterface) {
						final MiniGameInterface m = (MiniGameInterface) instance;
						try {
							World.getWorld().registerMiniGame(m);;
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
						.contains(QuestInterface.class)) {
						final QuestInterface q = (QuestInterface) instance;
						try {
							World.getWorld().registerQuest(
								(QuestInterface) instance);
						} catch (final Exception e) {
							LOGGER.error(
								"Error registering quest "
									+ q.getQuestName());
							LOGGER.catching(e);
							continue;
						}
					} else if (Arrays.asList(instance.getClass().getInterfaces())
							.contains(MiniGameInterface.class)) {
						final MiniGameInterface m = (MiniGameInterface) instance;
						try {
							World.getWorld().registerMiniGame(
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
		LOGGER.info("\t Loaded {}", box(World.getWorld().getQuests().size()) + " Quests.");
		LOGGER.info("\t Loaded {}", box(World.getWorld().getMiniGames().size()) + " MiniGames.");
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

	public void unload() throws IOException {
		reloading = true;
		urlClassLoader.close();
		executor.shutdown();
		World.getWorld().getQuests().clear();
		World.getWorld().getMiniGames().clear();
		World.getWorld().getShops().clear();

		queue.clear();
		actionPlugins.clear();
		executivePlugins.clear();
		knownInterfaces.clear();

		actionPlugins = new HashMap<String, Set<Object>>();
		executivePlugins = new HashMap<String, Set<Object>>();
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		knownInterfaces = new ArrayList<Class<?>>();
		queue = new ConcurrentHashMap<String, Class<?>>();
		defaultHandler = null;
	}

	public void reload() throws Exception {
		initPlugins();
		reloading = false;
	}
}
