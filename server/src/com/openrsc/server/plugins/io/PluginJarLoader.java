package com.openrsc.server.plugins.io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginJarLoader {
    private final ArrayList<Class<?>> loadedClasses;
    private URLClassLoader urlClassLoader;

    public PluginJarLoader() {
        this.loadedClasses = new ArrayList<>();
    }

    public ArrayList<Class<?>> getLoadedClasses() {
        return loadedClasses;
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
                    loadedClasses.add(c);
                }
            }
            jarFile.close();
        }
    }

    public List<Class<?>> loadClasses(final String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        final ArrayList<File> directories = new ArrayList<>();
        try {
            final ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            final Enumeration<URL> resources = cld.getResources(packageName.replace('.', '/'));
            while (resources.hasMoreElements()) {
                final URL res = resources.nextElement();
                if (res.getProtocol().equalsIgnoreCase("jar")) {
                    final JarURLConnection conn = (JarURLConnection) res.openConnection();
                    final JarFile jar = conn.getJarFile();
                    for (final JarEntry e : Collections.list(jar.entries())) {
                        if (e.getName().startsWith(packageName.replace('.', '/'))
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
                    packageName
                            + " does not appear to be a valid package (Null pointer exception)");
        } catch (final UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(
                    packageName
                            + " does not appear to be a valid package (Unsupported encoding)");
        } catch (final IOException ioex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + packageName);
        }

        for (final File directory : directories) {
            if (directory.exists()) {
                final String[] files = directory.list();
                if(files != null) {
                    for (final String file : files) {
                        if (file.endsWith(".class")) {
                            classes.add(Class.forName(packageName + '.' + file.substring(0, file.length() - 6)));
                        }
                    }
                }
            } else {
                throw new ClassNotFoundException(packageName + " ("
                        + directory.getPath()
                        + ") does not appear to be a valid package");
            }
        }
        return classes;
    }

    public Set<Class<?>> loadTriggers(final String packageName) throws ClassNotFoundException {
        final Set<Class<?>> classList = new LinkedHashSet<>();
        for (final Class<?> discovered : loadClasses(packageName)) {
            if (discovered.isInterface()) {
                classList.add(discovered);
            }
        }
        return classList;
    }

    public void clear() {
        loadedClasses.clear();
        try {
            urlClassLoader.close();
        } catch(Exception ignored) {}
    }
}
