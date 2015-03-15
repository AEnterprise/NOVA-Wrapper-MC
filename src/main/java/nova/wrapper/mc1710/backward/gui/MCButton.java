package nova.wrapper.mc1710.backward.gui;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import nova.core.gui.GuiComponent;
import nova.core.gui.GuiEvent.MouseEvent;
import nova.core.gui.GuiEvent.MouseEvent.EnumMouseState;
import nova.core.gui.Outline;
import nova.core.gui.components.Button;
import nova.core.gui.nativeimpl.NativeButton;
import nova.core.gui.render.Graphics;
import nova.core.util.transform.Vector2i;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCButton implements NativeButton, DrawableGuiComponent {

	private final Button component;
	private Outline outline = Outline.empty;

	@SideOnly(Side.CLIENT)
	private MCGuiButton button;

	public MCButton(Button component) {
		this.component = component;

		if (FMLCommonHandler.instance().getSide().isClient()) {
			button = new MCGuiButton();
		}

		component.onGuiEvent(this::onMousePressed, MouseEvent.class);
	}

	@Override
	public GuiComponent<?, ?> getComponent() {
		return component;
	}

	@Override
	public Outline getOutline() {
		return outline;
	}

	@Override
	public void setOutline(Outline outline) {
		button.width = outline.getWidth();
		button.height = outline.getHeight();
		this.outline = outline;
	}

	@Override
	public void requestRender() {

	}

	@Override
	public String getText() {
		return button.displayString;
	}

	@Override
	public Optional<Vector2i> getMinimumSize() {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		return fontRenderer != null ? Optional.of(new Vector2i(fontRenderer.getStringWidth(button.displayString) + 10, fontRenderer.FONT_HEIGHT + 10)) : Optional.empty();
	}

	@Override
	public void setText(String text) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			button.displayString = text;
		}
	}

	@Override
	public boolean isPressed() {
		// TODO
		return false;
	}

	@Override
	public void setPressed(boolean isPressed) {
		// TODO
	}

	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		Outline outline = getOutline();
		button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
		getComponent().render(mouseX, mouseY, graphics);
	}

	public void onMousePressed(MouseEvent event) {
		if (event.state == EnumMouseState.DOWN) {
			if (button.mousePressed(Minecraft.getMinecraft(), event.mouseX, event.mouseY)) {
				button.func_146113_a(Minecraft.getMinecraft().getSoundHandler());
			}
		} else if (event.state == EnumMouseState.UP) {
			button.mouseReleased(event.mouseX, event.mouseY);
		}
	}

	@SideOnly(Side.CLIENT)
	public class MCGuiButton extends GuiButtonExt {

		public MCGuiButton() {
			super(0, 0, 0, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			MCCanvas canvas = getCanvas();
			GL11.glTranslated(canvas.tx(), canvas.ty(), canvas.getZIndex());
			super.drawButton(mc, mouseX, mouseY);
			GL11.glTranslated(-canvas.tx(), -canvas.ty(), -canvas.getZIndex());
		}
	}
}
