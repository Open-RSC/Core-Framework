/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package org.rscemulation.installer.swing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import org.rscemulation.installer.Config;
import org.rscemulation.installer.Installer;
import org.rscemulation.installer.Logger;
import org.rscemulation.installer.internationalization.LocaleProvider;
import org.rscemulation.installer.swing.callbacks.DownloadProgressUpdate;
import org.rscemulation.installer.swing.callbacks.ResetDownloadProgress;

/**
 * An asynchronous background service that downloads and replaces any files 
 * that are out of date.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 * @see SwingWorker
 *
 */
public class BackgroundWorker
	extends
		SwingWorker<Object, Runnable>
{
	
	/// The {@link Installer} that should be notified of status changes
	private final InstallerFrame gui;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Object doInBackground()
		throws
			Exception
	{
		try
		{
			Files.createDirectories(Paths.get(Config.INSTALL_DIR));
			for(URL url : getFileIndex(Config.PROTOCOL + "://" + Config.UPDATE_HOST + Config.FILE_INDEX_PAGE))
			{
				super.publish(new ResetDownloadProgress(gui));
				try
				{
					ensureLocalCopyIsLatest(url);
				}
				// Occurs if one of the indexed files does not exist
				catch(FileNotFoundException fnfe)
				{
					Logger.error(String.format(LocaleProvider.getString("Logger.RemoteFileNotFoundErrorMessage"), url.toString()), fnfe);
					break;
				}
			}
		}
		// Occurs if the file index does not exist
		catch(FileNotFoundException fnfe)
		{
			Logger.error(String.format(LocaleProvider.getString("Logger.RemoteFileNotFoundErrorMessage"), Config.PROTOCOL + "://" + Config.UPDATE_HOST + Config.FILE_INDEX_PAGE), fnfe);
		}
		// Occurs if one of the files could not be downloaded (network error)
		catch(IOException ioe)
		{
			Logger.error(String.format(LocaleProvider.getString("Logger.DownloadErrorMessage"), Config.PROTOCOL + "://" + Config.UPDATE_HOST + Config.FILE_INDEX_PAGE), ioe);
		}
		// Unknown Exception! 
		catch(Throwable t)
		{
			Logger.error(t);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void process(List<Runnable> callbacks)
	{
		for (Runnable callback : callbacks)
		{
			callback.run();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void done()
	{
		gui.onStatusChange(LocaleProvider.getString("Installer.UpdateSuccessMessage"));
		gui.onUpdateFinished();
	}
	
	/**
	 * Constructs a <code>BackgroundUpdater</code> with the provided 
	 * {@link Installer} to report events to
	 * 
	 * @param gui the {@link Installer} to report events to
	 * 
	 */
	public BackgroundWorker(InstallerFrame gui)
	{
		this.gui = gui;
	}
	
	/**
	 * Ensures that the local copy of the provided remote resource is up to 
	 * date as determined by the last-modified timestamp of the local file 
	 * versus the last-modified timestamp of the remote file.  If the remote 
	 * file has been updated more recently than the local file, then an 
	 * attempt is made to replace the local file with the remote file.
	 * <br><br>
	 * <strong>Each remote resource is matched against a local file located 
	 * at {@link Config#INSTALL_DIR} + remote_file_name}, so a remote 
	 * file existing at "http://www.arbitraryurl.com/SomeFile.txt" is compared 
	 * to local file {@link Config#INSTALL_DIR} + "/SomeFile.txt"
	 * </strong>
	 * 
	 * @param resource the resource to run an update check for
	 * 
	 * @throws IOException if any I/O error occurs (this should be considered 
	 * a fatal error - either retry or abort)
	 * 
	 */
	private void ensureLocalCopyIsLatest(URL resource)
		throws
			IOException
	{
		Path path = Paths.get(Config.INSTALL_DIR + resource.getFile());
		URLConnection c = resource.openConnection();
		try
		{
			if(Files.exists(path))
			{
			    BasicFileAttributes view
			       = Files.getFileAttributeView(path, BasicFileAttributeView.class)
			              .readAttributes();
			    if(c.getLastModified() > view.lastModifiedTime().toMillis() 
			    		|| c.getContentLengthLong() != Files.size(path))
			    {
			    	updateFile(c);
			    }
			}
			else
			{
				updateFile(c);
			}
		}
		finally
		{
			// Should trigger a disconnection in most cases...
			c.getInputStream().close();
		}
	}
	
	/**
	 * Attempts to download the latest resource at the provided remote address 
	 * and either create, or replace a local copy of said resource.  The 
	 * last-modified setting of the resulting file shall be set to the same 
	 * as the remote file (meaning that this could mark the file as created in 
	 * the future, which might trip some malware scanners...we'll have to 
	 * wait and see.)
	 * 
	 * @param c the {@link URLConnection} to the remote file
	 * 
	 * @throws IOException if any I/O error occurs (this should be considered 
	 * a fatal error - either retry or abort)
	 * 
	 */
	private void updateFile(URLConnection c)
			throws
			IOException
	{
		String resourceName = c.getURL().getFile();
		long remoteFileLength = c.getContentLengthLong();
		ReadableByteChannel remoteFile = 
				Channels.newChannel(c.getInputStream());
		Files.createDirectories(Paths.get(Config.INSTALL_DIR + 
				resourceName.substring(0, resourceName.lastIndexOf('/'))));
		File localFile = new File(Config.INSTALL_DIR + resourceName);
		// Could use NIO's channel copying directly, but wouldn't be able 
		// to report download progress to the event dispatch thread, so we'll
		// use an old fashioned buffered approach.
		OutputStream localStream = null;
		try
		{
			localStream = new FileOutputStream(localFile);
			ByteBuffer tempBuffer = 
					ByteBuffer.allocate(Config.DOWNLOAD_BUFFER_SIZE);
			long read = 0, downloaded = 0, then = System.nanoTime();
			while((read = remoteFile.read(tempBuffer)) != -1)
			{
				downloaded += read;
				float delta = System.nanoTime() - then;
				tempBuffer.flip();
				localStream.write(tempBuffer.array(), 0, (int)read);
				tempBuffer.clear();
				final float percentFinished = (((float)downloaded / 
									(float)remoteFileLength) * 100.0f);
				final float kbps = ((float)downloaded / 
									((float)delta * 0.000000001f)) / 1024.0f;
				// Report to the GUI that this chunk has been downloaded
				super.publish(new DownloadProgressUpdate(gui, resourceName, percentFinished, kbps));
			}
			localStream.close();
			localFile.setLastModified(c.getLastModified());
			
			// Temporary so that RSCE's current client will still work
			if(resourceName.endsWith(".zip"))
			{
				__temp_unzip(new ZipFile(localFile));
			}
		}
		finally
		{
			if(localStream != null)
			{
				try
				{
					localStream.close();
				}
				catch(IOException ioe)
				{
					/** Ignore */
				}
			}
		}
	}
	
	/**
	 * Retrieves the list of links to the remote files that make up the 
	 * overall installation package.  The page provided to this method should 
	 * contain newline-delimited links to each asset that should be subject to 
	 * installation.
	 * 
	 * @param url the page that contains the installation file index
	 * 
	 * @return a list of the various links to resources that should be 
	 * subject to installation.
	 * 
	 * @throws IOException if any I/O error occurs (this should be considered 
	 * a fatal error - either retry or abort)
	 * 
	 */
	private List<URL> getFileIndex(String url)
		throws
			IOException
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(
					new InputStreamReader(
						new URL(url).openConnection().getInputStream()));		
			List<URL> lines = 
					new ArrayList<URL>(Config.EXPECTED_FILE_COUNT);
			String line = null;
			while ((line = in.readLine()) != null)
			{
				lines.add(new URL(line));
			}
			return lines;
		}
		finally
		{
			if(in != null)
			{
				try
				{
					in.close();
				}
				catch(IOException ioe)
				{
					/** Ignore */
				}
			}
		}
	}
	
	public void __temp_copy_input_stream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}
	
	private void __temp_unzip(ZipFile zip)
		throws
			IOException
	{
		Enumeration<?> entries = zip.entries();
		while(entries.hasMoreElements())
		{
			final ZipEntry entry = (ZipEntry) entries.nextElement();
			if(entry.isDirectory())
			{
				Files.createDirectories(Paths.get(Config.INSTALL_DIR + entry));
				continue;
			}
			try
			{
				__temp_copy_input_stream(zip.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(Config.INSTALL_DIR
								+ entry.getName())));
			}
			catch(IOException ioe)
			{
				Logger.error(String.format(LocaleProvider.getString("Logger.FileAlreadyInUseErrorMessage"), entry.getName()), ioe);
			}
		}
		zip.close();
	}
}
