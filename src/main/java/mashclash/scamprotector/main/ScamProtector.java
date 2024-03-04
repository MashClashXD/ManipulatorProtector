package mashclash.scamprotector.main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@Mod(modid = "maniproc1", name = "Manipulation Protector", version = "1.0")
public class ScamProtector {
    private static boolean modEnabled = true;
    private static double ratio = 3.0;
    private static final KeyBinding openMenuKeybind = new KeyBinding("Open Ratio Menu", Keyboard.KEY_RMENU, "Scam Protector");

    public static double getRatio() {
        return ratio;
    }

    public static void setRatio(double newRatio) {
        ratio = newRatio;
    }

    public static void toggleModEnabled() {
        modEnabled = !modEnabled;
    }

    public static boolean isModEnabled() {
        return modEnabled;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(openMenuKeybind);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register the GUI
        MinecraftForge.EVENT_BUS.register(new GuiModSettings());
        MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openMenuKeybind.isPressed()) {
            // Open the GUI when the key is pressed
            Minecraft.getMinecraft().displayGuiScreen(new GuiModSettings());
        }
    }
}
