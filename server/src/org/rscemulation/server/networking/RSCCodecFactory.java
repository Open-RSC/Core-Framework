package org.rscemulation.server.networking;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.rscemulation.server.net.RSCPacket;

public final class RSCCodecFactory
	implements
		ProtocolCodecFactory
{
	public final ProtocolEncoder getEncoder()
	{
		return new ProtocolEncoder()
		{
			
			@Override public void dispose(IoSession arg0) throws Exception { }
			
			public final void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			{
				RSCPacket p = (RSCPacket)message;
				byte[] data = p.getData();
				int dataLength = data.length;
				ByteBuffer buffer;
				if (!p.isBare())
				{
					buffer = ByteBuffer.allocate(dataLength + 3);
					byte[] outlen = { (byte)(dataLength >> 8), (byte)dataLength };
					buffer.put(outlen);
					int id = p.getID();
					buffer.put((byte)id);
				}
				else
				{
					buffer = ByteBuffer.allocate(dataLength);
				}
				buffer.put(data, 0, dataLength);
				buffer.flip();
				out.write(buffer);
				return;
			}

			
		};
	}

	public final ProtocolDecoder getDecoder()
	{
		return new CumulativeProtocolDecoder()
		{
			protected final boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out)
			{
				if (in.remaining() >= 2)
				{
					int length = in.getShort();
					if (length <= in.remaining())
					{
						if (length - 1 < 0)
						{
							session.close();
							return true;
						}
						byte[] payload = new byte[length - 1];
						int id = in.get() & 0xFF;
						in.get(payload);
						RSCPacket p = new RSCPacket(id, payload);
						out.write(p);
						return true;
					}
					in.rewind();
				}
				return false;
			}
		};
	}
}
