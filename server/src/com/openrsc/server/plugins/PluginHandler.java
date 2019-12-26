package com.openrsc.server.plugins;

import com.openrsc.server.Server;
import com.openrsc.server.event.custom.ShopRestockEvent;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.event.rsc.PluginTickEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.entity.Mob;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
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

	private final ThreadFactory threadFactory;

	private URLClassLoader urlClassLoader;
	private boolean reloading = true;
	private ArrayList<Class<?>> loadedClassFiles = new ArrayList<Class<?>>();

	private Object defaultHandler = null;
	private Map<String, Set<Object>> actionPlugins;
	private Map<String, Set<Object>> executivePlugins;
	private List<Class<?>> knownInterfaces;
	private Map<String, Class<?>> queue;
	private Map<String, Object> loadedPlugins;

	public PluginHandler (Server server) {
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
					if (instance instanceof QuestInterface) {
						final QuestInterface q = (QuestInterface) instance;
						try {
							getServer().getWorld().registerQuest(q);
						} catch (final Exception e) {
							LOGGER.error("Error registering quest " + q.getQuestName());
							LOGGER.catching(e);
							continue;
						}
					} else if (instance instanceof MiniGameInterface) {
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
					} else if (Arrays.asList(instance.getClass().getInterfaces())
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
		queue = new ConcurrentHashMap<String, Class<?>>();
		loadedPlugins = new HashMap<String, Object>();
		defaultHandler = null;

		loadJar();
		initPlugins();
	}

	public void unload() throws IOException {
		reloading = true;

		urlClassLoader.close();

		getServer().getWorld().getQuests().clear();
		getServer().getWorld().getMiniGames().clear();
		getServer().getWorld().getShops().clear();

		queue.clear();
		actionPlugins.clear();
		executivePlugins.clear();
		knownInterfaces.clear();
		loadedPlugins.clear();
		loadedClassFiles.clear();

		actionPlugins = null;
		executivePlugins = null;
		knownInterfaces = null;
		queue = null;
		loadedClassFiles = null;

		defaultHandler = null;
	}

	public boolean blockDefaultAction(final Mob owner, final String interfce, final Object[] data) {
		return blockDefaultAction(owner, interfce, data, true);
	}

	/**
	 * @param interfce
	 * @param data
	 * @param callAction
	 * @return
	 */

	public boolean blockDefaultAction(final Mob owner, final String interfce,
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
			handleAction(owner, interfce, data);
		}
		return flagStop; // not sure why it matters if its false or true
	}

	public void handleAction(final Mob owner, final String interfce, final Object[] data) {
		handleAction(owner, owner.getWorld(), interfce, data);
	}

	public void handleAction(final World world, final String interfce, final Object[] data) {
		handleAction(null, world, interfce, data);
	}

	public void handleAction(final Mob owner, final World world, final String interfce, final Object[] data) {
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
						final String pluginName = c.getClass().getSimpleName() + "." + m.getName();
						PluginTickEvent e = new PluginTickEvent(world, owner, pluginName, new PluginTask(getServer()) {
							@Override
							public int action() {
								try {
									LOGGER.info("Executing Plugin : " + pluginName + " : " + Arrays.deepToString(data));
									m.invoke(c,data);
									return 1;
								} catch (final Exception ex) {
									LOGGER.catching(ex);
									return 0;
								}
							}
						});

						getServer().getGameEventHandler().add(e);
					}
				} catch (final Exception e) {
					System.err.println("Exception at plugin handling: ");
					LOGGER.catching(e);
				}
			}
		}
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

	public Server getServer() {
		return server;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}
}
