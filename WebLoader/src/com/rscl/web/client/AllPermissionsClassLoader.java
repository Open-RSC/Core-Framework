package com.rscl.web.client;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;

public class AllPermissionsClassLoader
extends URLClassLoader {
	public AllPermissionsClassLoader(URL[] paramArrayOfURL) {
		super(paramArrayOfURL);
	}

	public AllPermissionsClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader) {
		super(paramArrayOfURL, paramClassLoader);
	}

	public AllPermissionsClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory) {
		super(paramArrayOfURL, paramClassLoader, paramURLStreamHandlerFactory);
	}

	@Override
	protected PermissionCollection getPermissions(CodeSource paramCodeSource) {
		Permissions localPermissions = new Permissions();
		localPermissions.add(new AllPermission());
		return localPermissions;
	}
}

