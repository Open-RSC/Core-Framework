package com.openrsc.server.net;

import java.io.*;
import java.util.LinkedList;
import java.util.zip.GZIPOutputStream;

public class PcapLogger {

	public String fname;
	final private LinkedList<ReplayPacket> m_packets = new LinkedList<ReplayPacket>();

	private static final byte[] spoofedClientMAC = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCC, (byte)0xCC, (byte)0xCC};
	private static final byte[] spoofedServerMAC = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x55, (byte)0x55, (byte)0x55};
	private static final int VIRTUAL_OPCODE_CONNECT = 10000;
	private static final int VIRTUAL_OPCODE_NOP = 10001;
	public static final int VIRTUAL_OPCODE_SERVER_METADATA = 12345;


	public PcapLogger(String filename) {
		this.fname = filename;
	}

	public void addPacket(Packet packet, boolean incoming) {
		ReplayPacket p = new ReplayPacket();
		p.incoming = incoming;
		p.timestamp = System.currentTimeMillis();
		p.opcode = packet.getID();
		p.data = packet.getBuffer().array();
		m_packets.add(p);
	}

	private int getLengthSize(int size) {
		if (size >= 160)
			return 2;
		return 1;
	}

	private void writeLength(DataOutputStream pcap, int size) throws IOException {
		if (size >= 160) {
			byte[] length = {(byte)(size / 256 + 160), (byte)(size & 0xFF)};
			pcap.write(length);
		} else {
			pcap.writeByte((byte)size);
		}
	}

	private void writePCAPPacket(DataOutputStream pcap, ReplayPacket packet) throws IOException {
		if (packet.opcode == VIRTUAL_OPCODE_NOP)
			return;

		int opcode = packet.opcode;
		int size = 1;
		int lengthSize = 0;
		if (opcode == VIRTUAL_OPCODE_CONNECT) {
			if (!packet.incoming) {
				opcode = 0;
				if (packet.data != null)
					size += packet.data.length;
				lengthSize = getLengthSize(size);
			} else {
				opcode = packet.data[0];
			}
		} else {
			if (packet.data != null)
				size += packet.data.length;
			lengthSize = getLengthSize(size);
		}

		long timestampMS = packet.timestamp;
		int timestampSeconds = (int)((timestampMS) / 1000);
		long timestampMicro = ((long)timestampMS * 1000) % 1000000;

		pcap.writeInt(timestampSeconds); // Timestamp seconds
		pcap.writeInt((int)timestampMicro); // Timestamp microseconds
		pcap.writeInt(size + lengthSize + 19); // Saved length
		pcap.writeInt(size + lengthSize + 19); // Original length

		// Ethernet header
		if (!packet.incoming) {
			pcap.write(spoofedServerMAC);
			pcap.write(spoofedClientMAC);
		} else {
			pcap.write(spoofedClientMAC);
			pcap.write(spoofedServerMAC);
		}
		pcap.writeShort(0x0);

		// rscminus Header
		pcap.writeByte(!packet.incoming ? 1 : 0); // Client
		pcap.writeInt(packet.opcode);

		if (lengthSize > 0)
			writeLength(pcap, size);
		pcap.writeByte(opcode);
		if (size > 1)
			pcap.write(packet.data);
	}

	public void exportPCAP() {
		// Create pcap directory if it doesn't already exist
		File pcapDir = new File("logs/pcaps/");
		if (pcapDir.isFile()) pcapDir.delete();
		if (!pcapDir.exists()) pcapDir.mkdir();

		// Required files
		File pcapFile = new File( pcapDir.getAbsolutePath() + "/" + fname + ".pcap.gz");
		try {
			DataOutputStream pcap = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(pcapFile))));

			// Write global header
			pcap.writeInt(0xa1b2c3d4); // Magic number
			pcap.writeShort(2); // Version major
			pcap.writeShort(4); // Version minor
			pcap.writeInt(0); // Timezone correction (UTC)
			pcap.writeInt(0); // Timestamp accuracy
			pcap.writeInt(65535); // Packet snapshot length
			pcap.writeInt(1); // Data link type (Ethernet)

			synchronized (m_packets) {
				for (ReplayPacket packet : m_packets) {
					writePCAPPacket(pcap, packet);
				}
			}

			pcap.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ReplayPacket {
	public long timestamp;
	public int opcode;
	public byte[] data;
	public boolean incoming;
}
