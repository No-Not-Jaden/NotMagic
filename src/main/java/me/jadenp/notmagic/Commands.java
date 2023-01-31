package me.jadenp.notmagic;


import me.jadenp.notmagic.RevisedClasses.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.util.EnumUtils;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class Commands implements CommandExecutor, TabCompleter {

    private Plugin plugin;
    private NotMagic notMagic;
    private HashMap<String, Integer> levels = new HashMap<>();
    public List<String> language = new ArrayList<>();

    private RevisedEvents eventClass;

    public void setEventClass(RevisedEvents eventClass){
        this.eventClass = eventClass;
    }

    public Commands(NotMagic plugin){
        this.plugin = plugin;
        this.notMagic = plugin;

    }


    /* public String color(String str){
            str = ChatColor.translateAlternateColorCodes('&', str);
            while (str.contains("{#") && str.substring(str.indexOf("{#")).contains("}")){
                str = str.substring(0,str.indexOf("{#")) + ChatColor.of(str.substring(str.indexOf("{#") + 2, str.substring(str.indexOf("{#")).indexOf("}") + str.substring(0,str.indexOf("{#")).length())) + str.substring(str.substring(str.indexOf("{#") + 1).indexOf("}") + 1);

            }
            return str;
        }*/
    public String color(String str){
        str = ChatColor.translateAlternateColorCodes('&', str);
        return translateHexColorCodes("&#","", str);
    }
    public String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }


    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        java.util.List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (int i = list.size() - 1; i > -1; i--) {
            temp.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return temp;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nm")) {
            String prefix = Language.prefix();

            if (args.length == 0) {
                if (sender.hasPermission("notmagic.basic")) {
                    sendGradientTop(sender);
                    sender.sendMessage(ChatColor.GREEN + "NotMagic is a magic plugin that has a unique way");
                    sender.sendMessage(ChatColor.GREEN + "of casting spells. By drawing patterns in the air");
                    sender.sendMessage(ChatColor.GREEN + "with 9 lines, you can cast every spell we've created.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.DARK_GREEN + "Plugin by: Not_Jaden");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.BLUE + "For help, do /nm help");
                    sender.sendMessage(ChatColor.RED + "For Admin help, do /nm admin");
                    sendGradientBottom(sender);
                    return true;
                } else {
                    sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                }
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("notmagic.basic")) {
                    sendGradientTop(sender);
                    if (args.length == 1) {
                        TextComponent basicWandCraft = new TextComponent(ChatColor.LIGHT_PURPLE + "Basic Wand");
                        basicWandCraft.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm recipe basicWand"));
                        basicWandCraft.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.LIGHT_PURPLE + "Basic Wand Recipe")));
                        TextComponent textComponent = new TextComponent(ChatColor.BLUE + "appropriately named ");
                        textComponent.addExtra(basicWandCraft);
                        sender.sendMessage(ChatColor.BLUE + "First, if you are new to magic, this may seem");
                        sender.sendMessage(ChatColor.BLUE + "confusing, but trust me, its all shapes and patterns");
                        sender.sendMessage(ChatColor.BLUE + "after you get started. Before we get into anything, ");
                        sender.sendMessage(ChatColor.BLUE + "you are going to need a wand. The most basic wand can");
                        sender.sendMessage(ChatColor.BLUE + "crafted with 4 " + ChatColor.LIGHT_PURPLE + "Magic Dust" + ChatColor.BLUE + "and 1 stick,");
                        sender.spigot().sendMessage(textComponent);
                        TextComponent rightArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "--→");
                        rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 2"));
                        rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Next Page")));
                        TextComponent center = new TextComponent(ChatColor.GRAY + "              ███ ");
                        center.addExtra(rightArrow);
                        sender.spigot().sendMessage(center);

                    /* >>> ⇦ ⇨ <<<→←⇐⇒
                    TextComponent msg = new TextComponent(ChatColor.GREEN + "Wiki: " + ChatColor.DARK_GREEN + "Click here");
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://nrg-mc.com/wiki/"));
                    sender.spigot().sendMessage(msg);
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.BLUE + "For more help, suggest a new guide on the NRG Discord ");
                    sender.sendMessage(ChatColor.BLUE + "or ask Not_Jaden in-game on Jaden#8777 on discord");
                     */
                    } else if (args[1].equalsIgnoreCase("1")){
                        sendGradientTop(sender);
                        TextComponent basicWandCraft = new TextComponent(ChatColor.LIGHT_PURPLE + "Basic Wand");
                        basicWandCraft.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm recipe basicWand"));
                        basicWandCraft.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.LIGHT_PURPLE + "Basic Wand Recipe")));
                        TextComponent textComponent = new TextComponent(ChatColor.BLUE + "appropriately named ");
                        textComponent.addExtra(basicWandCraft);
                        sender.sendMessage(ChatColor.BLUE + "First, if you are new to magic, this may seem");
                        sender.sendMessage(ChatColor.BLUE + "confusing, but trust me, its all shapes and patterns");
                        sender.sendMessage(ChatColor.BLUE + "after you get started. Before we get into anything, ");
                        sender.sendMessage(ChatColor.BLUE + "you are going to need a wand. The most basic wand can");
                        sender.sendMessage(ChatColor.BLUE + "crafted with 4 " + ChatColor.LIGHT_PURPLE + "Magic Dust" + ChatColor.BLUE + "and 1 stick,");
                        sender.spigot().sendMessage(textComponent);
                        TextComponent rightArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "--→");
                        rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 2"));
                        rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Next Page")));
                        TextComponent center = new TextComponent(ChatColor.GRAY + "              ███ ");
                        center.addExtra(rightArrow);
                        sender.spigot().sendMessage(center);
                    } else if (args[1].equalsIgnoreCase("2")){
                        sender.sendMessage(ChatColor.BLUE + "Now that you have a wand, you may have noticed your MP");
                        sender.sendMessage(ChatColor.BLUE + "indicator either above your action bar while holding the");
                        sender.sendMessage(ChatColor.BLUE + "wand or somewhere else on your screen. MP stands for Mana");
                        sender.sendMessage(ChatColor.BLUE + "Points. Casting spells or doing other magic activities will");
                        sender.sendMessage(ChatColor.BLUE + "use up your MP. Overtime your MP will regenerate. What your");
                        sender.sendMessage(ChatColor.BLUE + "MP will be most useful for you is casting spells. By default");
                        sender.sendMessage(ChatColor.BLUE + "you can cast 2 spells, one main spell and one secondary spell.");
                        TextComponent rightArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "--→");
                        rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 3"));
                        rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Next Page")));
                        TextComponent leftArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "←--");
                        leftArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 1"));
                        leftArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Last Page")));
                        TextComponent center = new TextComponent(ChatColor.GRAY + " ███ ");
                        TextComponent space = new TextComponent("            ");
                        space.addExtra(leftArrow);
                        space.addExtra(center);
                        space.addExtra(rightArrow);
                        sender.spigot().sendMessage(space);
                    } else if (args[1].equalsIgnoreCase("3")){
                        sender.sendMessage(ChatColor.BLUE + "Main spells can be activated by left clicking with your wand.");
                        sender.sendMessage(ChatColor.BLUE + "The default main spell is " + ChatColor.GREEN + "Burn" + ChatColor.BLUE + ". It shoots a fire stream");
                        sender.sendMessage(ChatColor.BLUE + "that will burn your opponent. It uses 3 MP. After you've");
                        sender.sendMessage(ChatColor.BLUE + "learned more than 1 main spell, you can switch main spells");
                        sender.sendMessage(ChatColor.BLUE + "by casting a secondary spell. Secondary spells require you");
                        sender.sendMessage(ChatColor.BLUE + "to cast a pattern in the air by holding right click and");
                        sender.sendMessage(ChatColor.BLUE + "moving your mouse. You have 9 lines that you can draw. Each");
                        sender.sendMessage(ChatColor.BLUE + "line will be a different color. ");
                        TextComponent rightArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "--→");
                        rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 4"));
                        rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Next Page")));
                        TextComponent leftArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "←--");
                        leftArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 2"));
                        leftArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Last Page")));
                        TextComponent center = new TextComponent(ChatColor.GRAY + " ███ ");
                        TextComponent space = new TextComponent("            ");
                        space.addExtra(leftArrow);
                        space.addExtra(center);
                        space.addExtra(rightArrow);
                        sender.spigot().sendMessage(space);
                    } else if (args[1].equalsIgnoreCase("4")){
                        sender.sendMessage(ChatColor.BLUE + "To cast the spell, left click after drawing a pattern.");
                        sender.sendMessage(ChatColor.BLUE + "There are a few more rules when casting a spell. First,");
                        sender.sendMessage(ChatColor.BLUE + "a spell with lines too spread out will break the spell");
                        sender.sendMessage(ChatColor.BLUE + "Second, You can cast a spell away from where you drew it");
                        sender.sendMessage(ChatColor.BLUE + "as long as you cast it within 30 seconds of drawing the ");
                        sender.sendMessage(ChatColor.BLUE + "last line. Some spells will be cast relative to the player,");
                        sender.sendMessage(ChatColor.BLUE + "and others will be cast relative to the spell.");
                        TextComponent rightArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "--→");
                        rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 5"));
                        rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Next Page")));
                        TextComponent leftArrow = new TextComponent(ChatColor.of(new Color(232, 26, 225)) + "←--");
                        leftArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nm help 3"));
                        leftArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.of(new Color(232, 26, 225)) + "Last Page")));
                        TextComponent center = new TextComponent(ChatColor.GRAY + " ███ ");
                        TextComponent space = new TextComponent("            ");
                        space.addExtra(leftArrow);
                        space.addExtra(center);
                        space.addExtra(rightArrow);
                        sender.spigot().sendMessage(space);
                    } else if (args[1].equalsIgnoreCase("5")){
                        sender.sendMessage(ChatColor.BLUE + "Being a good magician is all about memory and cast speed.");
                        sender.sendMessage(ChatColor.BLUE + "But what are you to memorise if you don't even know the spell?");
                        sender.sendMessage(ChatColor.BLUE + "Thankfully there is a way to learn to cast every spell in game.");
                        sender.sendMessage(ChatColor.BLUE + "If enabled, you can run the command /nm spell (spellName).");
                        sender.sendMessage(ChatColor.BLUE + "Otherwise, you may have to travel to an in-game location indicated");
                        sender.sendMessage(ChatColor.BLUE + "by one of your moderators.");
                    }
                    sendGradientBottom(sender);
                } else {
                    sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                }
                } else if (args[0].equalsIgnoreCase("admin")) {
                    if (sender.hasPermission("notmagic.admin")){
                        sendGradientTop(sender);
                        sender.sendMessage(ChatColor.GREEN + "/nm spellcolor (player) (R) (G) (B)" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Changes a player's spell color");
                        sender.sendMessage(ChatColor.GREEN + "/nm give [player] (NM Item)" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Gives yourself or a player a NM Item");
                        sender.sendMessage(ChatColor.GREEN + "/nm mine (add/remove) (#)" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Adds or removes blocks to the mana mines list");
                        sender.sendMessage(ChatColor.GREEN + "/nm setlevel (player) (#)" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Changes a player's magic level");
                        sender.sendMessage(ChatColor.GREEN + "/nm reload" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "reloads the plugin's configuration");
                        sendGradientBottom(sender);
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                    }
                } else if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("notmagic.admin")) {
                        if (args[1] != null) {
                            if (Bukkit.getPlayer(args[1]) != null && args.length > 2) {
                                if (Items.data(args[2]) != null) {
                                    if (args.length > 3) {
                                        for (int i = 0; i < Integer.parseInt(args[3]); i++) {
                                            givePlayer(Objects.requireNonNull(Bukkit.getPlayer(args[1])), Items.data(args[2]));
                                        }
                                        sender.sendMessage(prefix + ChatColor.GREEN + "Gave " + args[3] + " " + args[2] + " to " + args[1] + ".");
                                    } else {
                                        givePlayer(Objects.requireNonNull(Bukkit.getPlayer(args[1])), Items.data(args[2]));
                                        sender.sendMessage(prefix + ChatColor.GREEN + "Gave 1 " + args[2] + " to " + args[1] + ".");
                                    }

                                } else {
                                    sender.sendMessage(prefix+ ChatColor.RED + "Invalid Item!");
                                }
                            } else {
                                if (Items.data(args[1]) != null) {
                                    if (args.length > 2) {
                                        for (int i = 0; i < Integer.parseInt(args[3]); i++) {
                                            if (sender instanceof  Player) {
                                                Player p = (Player) sender;
                                                givePlayer(p, Items.data(args[1]));
                                            } else {
                                                sender.sendMessage(prefix + "You are not a player!");
                                            }
                                        }
                                        if (sender instanceof  Player) {
                                            Player p = (Player) sender;
                                            sender.sendMessage(prefix + ChatColor.GREEN + "Gave " + args[2] + " " + args[1] + " to " + p.getName() + ".");
                                        } else {
                                            sender.sendMessage(prefix + "You are not a player!");
                                        }
                                    } else {
                                        if (sender instanceof  Player) {
                                            Player p = (Player) sender;
                                            givePlayer(p, Items.data(args[1]));
                                            sender.sendMessage(prefix + ChatColor.GREEN + "Gave 1 " + args[1] + " to " + p.getName() + ".");
                                        } else {
                                            sender.sendMessage(prefix + "You are not a player!");
                                        }
                                    }
                                } else {
                                    sender.sendMessage(prefix+ ChatColor.RED + "Invalid Item!");
                                }
                            }
                        } else {
                            sender.sendMessage(prefix+ ChatColor.YELLOW + "Usage:");
                            sender.sendMessage(ChatColor.GREEN + "/nm give [player] (NM Item)" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Gives yourself or a player a NM Item");
                        }

                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                    }
                }else if (args[0].equalsIgnoreCase("mine")){
                    if (sender.hasPermission("notmagic.admin")) {
                        if (args.length == 3){
                            if (args[1].equalsIgnoreCase("add")){
                                YamlConfiguration c = YamlConfiguration.loadConfiguration(notMagic.manaMines);
                                c.set(args[2], ((Player) sender).getTargetBlock(null, 10).getLocation());
                                try {
                                    c.save(notMagic.manaMines);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sender.sendMessage(prefix + ChatColor.GREEN + "Added mine.");
                            } else if (args[1].equalsIgnoreCase("remove")){
                                YamlConfiguration c = YamlConfiguration.loadConfiguration(notMagic.manaMines);
                                c.set(args[2], null);
                                try {
                                    c.save(notMagic.manaMines);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sender.sendMessage(prefix + ChatColor.RED + "Removed mine.");
                            }

                        } else if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                            YamlConfiguration c = YamlConfiguration.loadConfiguration(notMagic.manaMines);
                            int i = 1;
                            while (true){
                                if (c.getLocation(i + "") != null){
                                    Location l = Objects.requireNonNull(c.getLocation(i + ""));
                                    sender.sendMessage(prefix + ChatColor.BLUE + i + ": " + ChatColor.DARK_AQUA + "world: " + ChatColor.AQUA + l.getWorld().getName() + ChatColor.DARK_AQUA + "x: " + ChatColor.AQUA + l.getX() + ChatColor.DARK_AQUA + "y: " + ChatColor.AQUA + l.getY() + ChatColor.DARK_AQUA + "z: " + ChatColor.AQUA + l.getZ());
                                } else {
                                    break;
                                }
                                i++;
                            }
                        }
                        else {
                            sender.sendMessage(prefix + ChatColor.YELLOW + "Usage:");
                            sender.sendMessage(ChatColor.GREEN + "/nm mine (add/remove) (#)" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Adds or removes blocks to the mana mines list");
                        }
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                    }

            } else if (args[0].equalsIgnoreCase("setlevel")) {
                if (sender.hasPermission("notmagic.admin")) {
                    if (Bukkit.getPlayer(args[1]) != null && args[2] != null) {
                        ChangeStatsEvent event = new ChangeStatsEvent("level", Integer.parseInt(args[2]), Bukkit.getPlayer(args[1]));
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    }
                } else {
                    sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                }
            } else if (args[0].equalsIgnoreCase("spell")) {
                if (args.length == 2){
                    if (sender instanceof Player){

                    Spell spell = eventClass.magicClass.spellIndex.querySpell(args[1]);
                    if (spell != null) {
                        PlayerData data = findPlayer(((Player) sender).getUniqueId());
                        if (!data.isDisplayingSpell()) {
                            if (data.getSpellsUnlocked().contains(spell.getName()) || sender.hasPermission("notmagic.admin")) {
                                sender.sendMessage(prefix + Language.demonstrateSpell().replace("{spell}", spell.getName()));
                                data.setDisplayingSpell(true);
                                spell.displayRealSpell((Player) sender);
                            } else {
                                sender.sendMessage(prefix + Language.unrecognizedSpell());
                            }
                        } else {
                            sender.sendMessage(prefix + Language.displayWait());
                        }

                    } else {
                        sender.sendMessage(prefix + Language.unrecognizedSpell());
                    }
                    } else {
                        sender.sendMessage(prefix + "You are not a player!");
                    }
                } else if (args.length == 3){

                } else {

                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("notmagic.admin")){
                    notMagic.loadConfig();
                } else {
                    sender.sendMessage(prefix + ChatColor.RED + "You do not have the permission to use this command!");
                }
            } else if (args[0].equalsIgnoreCase("spawn")) {
                if (sender.hasPermission("notmagic.admin")){
                    if (args.length > 1){
                        try {
                            Entity entity = ((Player) sender).getWorld().spawn(((Player) sender).getLocation(), EnumUtils.findEnumInsensitiveCase(EntityType.class, args[1]).getEntityClass());
                            notMagic.eventClass.addMagicEntity(entity, 1);
                            sender.sendMessage(prefix + ChatColor.GREEN + "Successfully spawned in a magic entity.");
                        } catch (IllegalArgumentException ignored){
                            sender.sendMessage(prefix + ChatColor.RED + "Unknown Entity!");
                        }
                    }
                }
            }
            else {
                    sender.sendMessage(prefix + ChatColor.RED + "Unknown NM Command!");
                }

            } else if (command.getName().equalsIgnoreCase("nmtop")){
            if (sender.hasPermission("notmagic.basic")){
                sendGradientTop(sender);
                int i = 0;
                for (Map.Entry<String, Integer> en : levels.entrySet()) {
                    if (i < 10) {
                        sender.sendMessage(ChatColor.of(new Color(26, 194, 232)) + en.getKey() + " " + ChatColor.of(new Color(232, 26, 225)) + en.getValue());
                        i++;
                    } else {
                        break;
                    }
                }
                sendGradientBottom(sender);
            }
        }



    return true;

    }
    public PlayerData findPlayer(UUID uuid){
        for (PlayerData data : eventClass.getPlayerData()){
            if (data.getUuid().equals(uuid)){
                return data;
            }
        }
        return null;
    }
    public void sendGradientTop(CommandSender sender){
        StringBuilder end = new StringBuilder();
        for (int i = 1; i < 31; i++) {
            String a;
            if (i < 11) {
                a = ChatColor.of(new Color(0, 100 - (i * 10), 252)) + "" + ChatColor.BOLD + "-";
            } else {
                a = ChatColor.of(new Color((i * 10) - 100, 0, 252)) + "" + ChatColor.BOLD + "-";
            }

            end.append(a);
            if (i == 10) {
                end.append(ChatColor.of(new Color(3, 144, 252))).append(ChatColor.BOLD).append("Not Magic");
                i += 8;
            }
        }
        sender.sendMessage(String.valueOf(end));
    }
    public void sendGradientBottom(CommandSender sender){
        StringBuilder end2 = new StringBuilder();
        for (int i = 1; i < 31; i++) {
            String a;
            if (i < 11) {
                a = ChatColor.of(new Color(0, 100 - (i * 10), 252)) + "" + ChatColor.BOLD + "-";
            } else {
                a = ChatColor.of(new Color((i * 10) - 100, 0, 252)) + "" + ChatColor.BOLD + "-";
            }
            end2.append(a);
        }
        sender.sendMessage(String.valueOf(end2));
    }
    public void givePlayer(Player p, ItemStack itemStack){
        HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>((p.getInventory().addItem(itemStack)));
        if (!leftOver.isEmpty()) {
            Location loc = p.getLocation();
            p.getWorld().dropItem(loc, leftOver.get(0));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("nm")){
            if (args.length == 1) {
                if (sender.hasPermission("notmagic.basic")) {
                    list.add("help");
                }
                if (sender.hasPermission("notmagic.admin")) {
                    list.add("admin");
                    list.add("give");
                    list.add("setLevel");
                    list.add("spellColor");
                    list.add("mine");
                    list.add("debug");
                    list.add("spell");
                }
            } else if (args.length == 2){
                if (args[0].equalsIgnoreCase("help")){
                    if (sender.hasPermission("notmagic.basic")) {
                        list.add("1");
                        list.add("2");
                        list.add("3");
                        list.add("4");
                        list.add("5");
                    }
                } else if (args[0].equalsIgnoreCase("give")){
                    if (sender.hasPermission("notmagic.admin")) {
                        for (Player p : Bukkit.getOnlinePlayers()){
                            list.add(p.getName());
                        }
                        list.add("basicWand");
                        //list.addAll(eventClass.getMagicClass().getSpellIndex().getSpellBookNames());

                    }
                } else if (args[0].equalsIgnoreCase("mines")){
                    if (sender.hasPermission("notmagic.admin")) {
                        list.add("add");
                        list.add("remove");
                    }
                }
            }
        }
        return list;
    }
}
