package mashclash.scamprotector.main;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

public class GuiModSettings extends GuiScreen {
    private GuiButton toggleModButton;
    private GuiTextField ratioField;
    private GuiButton saveButton;
    public static double ratio = 10.0;

    @Override
    public void initGui() {
        int centerX = width / 2;
        int centerY = height / 2;

        ratioField = new GuiTextField(0, fontRendererObj, centerX - 100, centerY - 10, 200, 20);
        ratioField.setMaxStringLength(100);
        ratioField.setText(Double.toString(GuiModSettings.ratio));
        ratioField.setFocused(true);

        saveButton = new GuiButton(1, centerX - 50, centerY + 20, 100, 20, "Save Ratio");
        buttonList.add(saveButton);

        toggleModButton = new GuiButton(2, centerX - 50, centerY + 50, 100, 20, ScamProtector.isModEnabled() ? "Mod: ON" : "Mod: OFF");
        buttonList.add(toggleModButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Mod Settings", width / 2, 10, 0xFFFFFF);
        drawString(fontRendererObj, "Ratio:", width / 2 - 100, height / 2 - 30, 0xFFFFFF);
        ratioField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override // Keys in
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (ratioField.isFocused()) {
            ratioField.textboxKeyTyped(typedChar, keyCode);
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) { // Setting ratio modifier
            try {
                double newRatio = Double.parseDouble(ratioField.getText());
                GuiModSettings.ratio = newRatio;
                ScamProtector.setRatio(newRatio);
            } catch (NumberFormatException ignored) {
            }
        } else if (button.id == 2) { // Toggle the mod's functionality
            ScamProtector.toggleModEnabled();
            button.displayString = ScamProtector.isModEnabled() ? "Mod: ON" : "Mod: OFF";
        }
        mc.displayGuiScreen(null); // Close the GUI after performing the action
    }
}
