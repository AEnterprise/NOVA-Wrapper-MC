package nova.wrapper.mc18.network.discriminator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import nova.core.gui.GuiException;
import nova.core.network.Packet;
import nova.wrapper.mc18.network.MCPacket;
import nova.wrapper.mc18.wrapper.gui.MCGui.MCContainer;

//TODO: Integrate with NOVA
public class PacketGui extends PacketAbstract {

	private MCPacket wrapped;

	public PacketGui() {

	}

	public PacketGui(Packet wrapped) {
		this.wrapped = (MCPacket) wrapped;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		wrapped.writeTo(buffer);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		ByteBuf packetBuffer = Unpooled.buffer();
		packetBuffer.writeBytes(buffer);
		wrapped = new MCPacket(packetBuffer);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		if (player.openContainer instanceof MCContainer) {
			((MCContainer) player.openContainer).getGui().onNetworkEvent(wrapped);
		} else {
			throw new GuiException("Received an invalid GUI event packet, server side not present!");
		}
	}
}
