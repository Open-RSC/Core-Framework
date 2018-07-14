package org.openrsc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StreamClass extends PacketConstruction implements Runnable {

	public StreamClass(Socket socket, openrsc<?> gameWindow) throws IOException {
		closingStream = false;
		closedStream = true;
		streamSocket = socket;
		socket.setReceiveBufferSize(25000);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		closedStream = false;
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void closeStream() {
		
		super.closeStream();
		closingStream = true;
		try {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			if (streamSocket != null)
				streamSocket.close();
		} catch (IOException _ex) { }
		closedStream = true;
		synchronized (this) {
			notify();
		}
		outputBuffer = null;
	}

	public int readInputStream() throws IOException {
		if (closingStream)
			return 0;
		else
			return inputStream.read();
	}

	public int inputStreamAvailable() throws IOException {
		if (closingStream)
			return 0;
		else
			return inputStream.available();
	}


    public void readInputStream(int length, int offset, byte abyte0[])
            throws IOException {
        if (closingStream)
            return;
        int k = 0;
        int l;
        for (; k < length; k += l)
            if ((l = inputStream.read(abyte0, k + offset, length - k)) <= 0)
                throw new IOException("EOF");

    }

	public void writeToOutputBuffer(byte abyte0[], int i, int j)
			throws IOException {
		if (closingStream)
			return;
		if (outputBuffer == null)
			outputBuffer = new byte[20000];
		synchronized (this) {
			for (int k = 0; k < j; k++) {
				outputBuffer[bufferSize] = abyte0[k + i];
				bufferSize = (bufferSize + 1) % 5000;
				if (bufferSize == (dataWritten + 4900) % 5000)
					throw new IOException("buffer overflow");
			}
			notify();
		}
	}

	public void run() {
		while (!closedStream) {
			int i;
			int j;
			synchronized (this) {
				if (bufferSize == dataWritten)
					try {
						wait();
					} catch (InterruptedException _ex) {
					}
				if (closedStream)
					return;
				j = dataWritten;
				if (bufferSize >= dataWritten)
					i = bufferSize - dataWritten;
				else
					i = 5000 - dataWritten;
			}
			if (i > 0) {
				try {
					outputStream.write(outputBuffer, j, i);
				}
				// catch(
				catch (IOException ioexception) {
					super.error = true;
					super.errorText = "Twriter:" + ioexception;
				}
				dataWritten = (dataWritten + i) % 5000;
				try {
					if (bufferSize == dataWritten)
						outputStream.flush();
				} catch (IOException ioexception1) {
					super.error = true;
					super.errorText = "Twriter:" + ioexception1;
				}
			}
		}
	}

	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket streamSocket;
	private boolean closingStream;
	private byte outputBuffer[];
	private int dataWritten;
	private int bufferSize;
	private boolean closedStream;
}
