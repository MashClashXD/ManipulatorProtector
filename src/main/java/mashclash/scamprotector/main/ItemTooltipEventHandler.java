package mashclash.scamprotector.main;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Arrays;
import java.util.List;

public class ItemTooltipEventHandler {

    private static final List<String> validItemNames = Arrays.asList(
            "\u00a7aBuy only \u00a7eone\u00a7a!",
            "\u00a7aBuy a stack\u00a7a!",
            "\u00a7aFill my inventory!",
            "\u00a7aCustom Amount"
    );

    private String previousType; // Declaring previousType as a global variable

    // Constructor to initialize previousType
    public ItemTooltipEventHandler() {
        previousType = "dmkandoanjkld"; // Initialize previousType with a default value
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!ScamProtector.isModEnabled()) {
            return; // Exit early if the mod is disabled
        }
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = event.itemStack;
        NBTTagCompound nbt = stack.getTagCompound(); // Get the NBT data of the item

        if (nbt != null) {
            NBTTagCompound displayTag = nbt.getCompoundTag("display"); // Get the 'display' NBT tag
            if (displayTag != null && displayTag.hasKey("Name")) {
                String typeOfPurchase = displayTag.getString("Name");
                if (!previousType.equals(typeOfPurchase) || previousType.equals("\u00a7aCustom Amount")){
                    if (validItemNames.contains(typeOfPurchase)) {
                        if (displayTag.hasKey("Lore")) {
                            NBTTagList loreList = displayTag.getTagList("Lore", 8);
                            if (loreList.tagCount() > 0) {
                                String itemName = HypixelAPIRequest.getItemId(removeColorCodes(loreList.getStringTagAt(0))); // Get the first line of lore
                                String price = removeColorCodes(loreList.getStringTagAt(4)); // Get the first line of lore
                                double priceDouble = 0.0;
                                try {
                                    if (typeOfPurchase.equals("\u00a76Sell Instantly")) {
                                        String amount = removeColorCodes(loreList.getStringTagAt(4));
                                        String total = removeColorCodes(loreList.getStringTagAt(5));
                                        amount = amount.replaceFirst("Amount: ", "");
                                        amount = amount.replaceFirst("x", "");
                                        total = total.replaceFirst("Total: ", "");
                                        total = total.replaceFirst(" coins", "");
                                        double amountDouble = Double.parseDouble(amount);
                                        double totalDouble = Double.parseDouble(total);
                                        priceDouble = totalDouble / amountDouble;
                                    } else {
                                        price = price.replaceFirst(" coins", "");
                                        price = price.replaceFirst("Per unit: ", "");
                                        priceDouble = Double.parseDouble(price);
                                    }
                                } catch (NumberFormatException e) {
                                    priceDouble = -1.0; // Set price to -1 if conversion fails
                                }
                                // Remove the color code if it exists
                                double sellPrice = HypixelAPIRequest.getPriceForItem(itemName, priceDouble);

                                // Check if priceDouble is x times larger than sellPrice
                                double frac = priceDouble / sellPrice;
                                if (frac >= ScamProtector.getRatio()) { // Detects a manipulation
                                    Minecraft.getMinecraft().thePlayer.closeScreen(); // Close the GUI
                                    Minecraft.getMinecraft().thePlayer.playSound("random.anvil_land", 1.0F, 1.0F);
                                    player.addChatMessage(new ChatComponentText("\u00a74MARKET MANIPULATION"));
                                    player.addChatMessage(new ChatComponentText("\u00a74Item \u00a74Name: \u00A7f" + itemName)); // Print lore to chat
                                    player.addChatMessage(new ChatComponentText("\u00a74Price: \u00A7f" + priceDouble));
                                    player.addChatMessage(new ChatComponentText("\u00a74Sell \u00a74Price: \u00A7f" + sellPrice));
                                }

                                previousType = displayTag.getString("Name");
                            }
                        }
                    }
                }
            }
        }
    }

    private String removeColorCodes(String input) {
        return input.replaceAll("\\u00a7.", "");
    }
}
