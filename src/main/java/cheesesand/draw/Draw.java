package cheesesand.draw;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.darksoldier1404.dppc.utils.ConfigUtils;

import javax.annotation.Nullable;


public final class Draw extends JavaPlugin implements Listener {

    public static Draw plugin;
    public static YamlConfiguration config;


    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        config = ConfigUtils.loadDefaultPluginConfig(plugin);

        ItemStack ApLock = new ItemStack(Material.PAPER);
        ItemMeta ApLockMeta = ApLock.getItemMeta();
        ApLockMeta.setDisplayName("§f§l랜덤 아이템 소멸권");
        ApLockMeta.setLore(List.of("§7인벤토리에 있는 아무 아이템이나 소멸합니다"));
        ApLock.setItemMeta(ApLockMeta);

        ItemStack RpLock = new ItemStack(Material.PAPER);
        ItemMeta RpLockMeta = RpLock.getItemMeta();
        RpLockMeta.setDisplayName("§f§l확정 아이템 소멸권");
        RpLockMeta.setLore(List.of("§7인벤토리에 있는 배리어가 아무거나 소멸합니다"));
        RpLock.setItemMeta(RpLockMeta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "ApLock"), ApLock);
        recipe.shape("E", "H", "S");

        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('H', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);

        ShapedRecipe recipe2 = new ShapedRecipe(new NamespacedKey(plugin, "RpLock"), RpLock);

        recipe2.shape("A", "B", "C");

        recipe2.setIngredient('A', Material.PAPER);
        recipe2.setIngredient('B', Material.IRON_INGOT);
        recipe2.setIngredient('C', Material.STICK);

        Bukkit.addRecipe(recipe2);

    }


    @EventHandler
    public void onBlockBreak(PlayerInteractEvent event) {
        Player p = event.getPlayer();


        int a = ThreadLocalRandom.current().nextInt(40);
        int b = ThreadLocalRandom.current().nextInt(100);
        int num = 0;
        int per = config.getInt("Settings.per") + 1;


        HashMap<Integer, Integer> nm = new HashMap<>();
        String pn = p.getName();
        if (p.getInventory().getItemInOffHand().getType() != Material.AIR || p.getInventory().getItemInMainHand().getType() != Material.AIR) {

            String bn = event.getClickedBlock().getBlockData().getMaterial().translationKey();

            if (b < per + 1) {
                p.getInventory().setItem(a, new ItemStack(Material.BARRIER));
                Bukkit.broadcastMessage(pn + "님이 " + bn + "과 상호작용 하시다가" + a + "번 인벤토리 슬롯이 잠겼습니다!");
                nm.put(num, a);
                num++;
            }


        }
        if (event.getMaterial() == Material.BARRIER) {
            event.setCancelled(true);
        }
        if (event.getAction().isRightClick()) {

            if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§f§l랜덤 아이템 소멸권")) {
                    p.getInventory().setItem(a, new ItemStack(Material.AIR));
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    Bukkit.broadcastMessage(pn + "님이 랜덤 아이템 소멸권을 사용하여" + a + "번 인벤토리 슬롯이 소멸했습니다!");
                }
                if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§f§l확정 아이템 소멸권")) {
                    int LocalMapRandom = ThreadLocalRandom.current().nextInt(nm.size());
                    int LocalMap = nm.get(LocalMapRandom);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    p.getInventory().setItem(LocalMap, new ItemStack(Material.AIR));
                    Bukkit.broadcastMessage(pn + "님이 확정 아이템 소멸권을 사용하여" + LocalMap + "번 인벤토리 슬롯이 해금되었습니다!");
                }

            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (Objects.requireNonNull(e.getCurrentItem()).getType() == Material.BARRIER) {
            e.setCancelled(true);
        }

        if (Objects.requireNonNull(e.getCursor()).getType() == Material.BARRIER) {
            e.setCancelled(true);
        }
        Player p = (Player) e.getWhoClicked();
        if (e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            if (e.getHotbarButton() > -1 && Objects.requireNonNull(p.getInventory().getItem(e.getHotbarButton())).getType() == Material.BARRIER) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.BARRIER) {
            e.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
