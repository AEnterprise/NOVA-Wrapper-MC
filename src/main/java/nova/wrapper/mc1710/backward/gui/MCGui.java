package nova.wrapper.mc1710.backward.gui;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import nova.core.game.Game;
import nova.core.gui.Gui;
import nova.core.gui.GuiComponent;
import nova.core.gui.Outline;
import nova.core.gui.nativeimpl.NativeGui;
import nova.core.gui.render.Graphics;
import nova.core.gui.render.TextRenderer;
import nova.core.network.Packet;
import nova.wrapper.mc1710.network.PacketWrapper;

import java.util.ArrayList;
import java.util.List;

public class MCGui extends GuiScreen implements NativeGui, DrawableGuiComponent {

	private Gui component;
	private List<GuiComponent<?, ?>> components = new ArrayList<>();
	private Outline outline = Outline.empty;
	private Graphics graphics;
	private TextRenderer textRenderer;
	
	public MCGui(Gui component) {
		this.component = component;
	}
	
	@Override
	public GuiComponent<?, ?> getComponent() {
		return component;
	}
	
	@Override
	public void addElement(GuiComponent<?, ?> component) {
		components.add(component);
	}

	@Override
	public void removeElement(GuiComponent<?, ?> component) {
		components.remove(component);
	}

	@Override
	public Outline getOutline() {
		return outline;
	}

	@Override
	public void setOutline(Outline outline) {
		this.outline = outline;
	}

	@Override
	public void requestRender() {
		// Not needed as it gets redrawn every frame
	}

	@Override
	public Packet createPacket() {
		return new PacketWrapper(Unpooled.buffer());
	}

	@Override
	public void dispatchNetworkEvent(Packet packet) {
		// TODO Packets
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);

		if (textRenderer == null)
			textRenderer = new MCTextRenderer(fontRendererObj);

		graphics = new Graphics(new MCCanvas(width, height, Tessellator.instance), textRenderer);

		boolean resized = width != outline.getWidth() || height != outline.getHeight();
		Outline oldOutline = outline;
		outline = new Outline(0, 0, width, height);

		if(resized) onResized(oldOutline);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partial) {
		components.forEach((component) -> ((DrawableGuiComponent) component.getNative()).draw(mouseX, mouseY, partial, graphics));
		Outline outline = getOutline();
		graphics.getCanvas().translate(outline.x1i(), outline.y1i());
		getComponent().render(mouseX, mouseY, graphics);
		graphics.getCanvas().translate(-outline.x1i(), -outline.y1i());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onGuiClosed() {
		Game.instance.guiFactory.get().unbindCurrentGui();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		drawScreen(mouseX, mouseY, partial);
	}
}
