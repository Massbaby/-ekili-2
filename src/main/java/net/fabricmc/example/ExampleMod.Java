package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExampleMod implements ClientModInitializer {
    private static KeyBinding cekilisMenuTusu;
    private final List<String> katilimcilar = new ArrayList<>();
    private boolean cekilisAktif = false;
    private String katilimKelimesi = "omer";

    @Override
    public void onInitializeClient() {
        // B tuşunu (veya dilediğin bir tuşu) oyuna tanımlıyoruz
        cekilisMenuTusu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cekilis.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B, // Varsayılan tuş: B harfi
                "category.cekilis.mod"
        ));

        // Sunucudaki gelen mesajları dinleme (Oyuncular chate kelimeyi yazdığında yakalamak için)
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, instant) -> {
            String mesajText = message.getString().trim();
            
            // Eğer çekiliş aktifse ve gelen mesaj senin belirlediğin kelimeyle eşleşiyorsa
            if (cekilisAktif && mesajText.equalsIgnoreCase(katilimKelimesi)) {
                // Not: Sunucuda oyuncu adlarını tam yakalamak için sohbet paketleri gerekebilir, 
                // ancak şimdilik temel mantığı kuruyoruz.
            }
        });

        // Oyuncu her B tuşuna bastığında tetiklenecek kısayol
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (cekilisMenuTusu.wasPressed()) {
                if (client.player != null) {
                    // Şimdilik B tuşuna basınca çekilişi test amaçlı başlatıp bitirelim
                    if (!cekilisAktif) {
                        cekilisAktif = true;
                        katilimcilar.clear();
                        client.player.sendMessage(Text.literal("§a[Çekiliş Modu] Çekiliş Başlatıldı! Kelime: " + katilimKelimesi), false);
                    } else {
                        cekilisAktif = false;
                        if (!katilimcilar.isEmpty()) {
                            Random rand = new Random();
                            String kazanan = katilimcilar.get(rand.nextInt(katilimcilar.size()));
                            client.player.sendMessage(Text.literal("§e[Çekiliş Modu] ÇEKİLİŞ BİTTİ! Kazanan: " + kazanan), false);
                        } else {
                            client.player.sendMessage(Text.literal("§c[Çekiliş Modu] Çekiliş bitti ancak kimse katılamadı."), false);
                        }
                    }
                }
            }
        });
    }
}