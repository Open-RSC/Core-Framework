package org.openrsc.server.networking;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.openrsc.server.net.WebPacket;

public final class WebCodecFactory
	implements
		ProtocolCodecFactory
{

	public final ProtocolEncoder getEncoder()
	{
		return new ProtocolEncoder()
		{
			@Override
			public final void dispose(IoSession arg0) throws Exception { }

			public final void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			{
				if (message instanceof Integer)
				{
					Integer i = (Integer)message;
					byte[] bytes = i.toString().getBytes();
					ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
					buffer.put(bytes);
					buffer.flip();
					out.write(buffer);
				}
			}
		};
	}

	public final ProtocolDecoder getDecoder()
	{
		return new CumulativeProtocolDecoder()
		{

			@Override
			protected final boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out)
				throws
					Exception
			{
				if (in.remaining() > 1)
				{
					int messageLength = in.getUnsignedShort();
					if (in.remaining() < messageLength)
					{
						in.rewind();
					}
					else
					{
						int id = in.get() & 0xFF;
						byte[] data = new byte[messageLength - 1];
						in.get(data);
						out.write(new WebPacket(id, data));
						return true;
					}
				}
				return false;
			}
		};
	}
}
