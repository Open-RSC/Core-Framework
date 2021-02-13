package com.aposbot;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public final class ScriptClassLoader extends ClassLoader {

    private final HashMap<String, Class<?>> classes = new HashMap<>();

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        final File file = new File(ScriptFrame.dir, name + ".class");
        if (!file.exists()) {
            return super.loadClass(name);
        }
        if (classes.containsKey(name)) {
            return classes.get(name);
        }
        final int len = (int) file.length();
        final byte[] b = new byte[len];
        FileInputStream in = null;
        int read = 0;
        try {
            in = new FileInputStream(file);
            do {
                final int i = in.read(b, read, len - read);
                if (i == -1)
                    throw new EOFException();
                read += i;
            } while (read < len);
        } catch (final Throwable t) {
            throw new ClassNotFoundException(t.getMessage(), t);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        }
        name = name.substring(name.lastIndexOf(File.separator) + 1);
        final Class<?> c = defineClass(name, b, 0, len);
        classes.put(name, c);
        return c;
    }
}
