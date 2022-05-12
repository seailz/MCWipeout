package live.wipeout.wipeout.game.team;

import live.wipeout.wipeout.Main;
import live.wipeout.wipeout.game.Checkpoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class
TeamHandler implements CommandExecutor, TabCompleter, Listener {

    List<CustomTeam> teams = new ArrayList<>();

    Checkpoint cp1 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -554, 97, -19),
            new Integer[] {10, 5, 3},
            1);

    Checkpoint cp2 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -620, 92, -30),
            new Integer[] {15, 7, 4},
            1);

    Checkpoint cp3 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -581, 89, 16),
            new Integer[] {12, 6, 3},
            1);

    Checkpoint cp4 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -482, 96, 30),
            new Integer[] {20, 10, 5},
            3);

    Checkpoint cp5 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -473, 96, 101),
            new Integer[] {5, 3, 2},
            1);

    Checkpoint cp6 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -558, 89, 71),
            new Integer[] {10, 5, 3},
            1);

    Checkpoint cp7 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -616, 89, 54),
            new Integer[] {20, 15, 7},
            3);

    Checkpoint cp8 = new Checkpoint(
            new Location(Bukkit.getWorld("world"), -540, 91, 145),
            new Integer[] {20, 15, 7},
            3);

    public TeamHandler() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Team all = board.registerNewTeam("all");
        all.setCanSeeFriendlyInvisibles(true);
        Bukkit.getOnlinePlayers().forEach(all::addPlayer);
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        teams.forEach(team -> {
            if (team.players.contains(p)) {
                cp1.register(p.getLocation(), team);
                cp2.register(p.getLocation(), team);
                cp3.register(p.getLocation(), team);
                cp4.register(p.getLocation(), team);
                cp5.register(p.getLocation(), team);
                cp6.register(p.getLocation(), team);
                cp7.register(p.getLocation(), team);
                cp8.register(p.getLocation(), team);
            }
        });
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        // fix trying to add a player to a non-existent team.
        if (teams.isEmpty()) return;
        teams.get(0).players.add(e.getPlayer());
    }

    public boolean sendTeamMessage(Player player, String message) {
        for (CustomTeam team : teams) {
            if (team.sendTeamMessage(player, message)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("mcw_teams")) {
            if (args[0].equalsIgnoreCase("create")) {
                createTeam(args, sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                addPlayersToTeam(args, sender);
                return true;
            }

            // /mcw_teams empty <teamName>
            if (args[0].equalsIgnoreCase("empty")) {
                if (args.length <= 1) return true;
                String teamToEmpty = args[1];
                commandEmptyTeam(teamToEmpty, sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {
                Bukkit.getOnlinePlayers().forEach(p ->
                        p.teleport(new Location(Bukkit.getWorld("world"), -465, 98, -19)));
                return true;
            }
        }

        return true;
    }

    private void createTeam(String[] args, CommandSender sender) {
        String color = args[1];
        String name = args[2];

        teams.add(new CustomTeam(CommandColors.getByName(color), name));

        sender.sendMessage("Succesfully created team " + CommandColors.getByName(color) + name);
    }

    private void addPlayersToTeam(String[] args, CommandSender sender) {
        String name = args[1];
        ArrayList<Player> success = new ArrayList<>();
        ArrayList<String> failed = new ArrayList<>();

        for (int i = 2; i < args.length; i++) {
            Player p = Bukkit.getPlayer(args[i]);
            if (p == null) {
                failed.add(args[i]);
                continue;
            }

            success.add(p);
        }

        CustomTeam sel = teams.get(0);

        for (CustomTeam team : teams) {
            if (team.name.equalsIgnoreCase(name)) {
                team.players.addAll(success);
                sel = team;
                break;
            }
        }

        String msg = "Succesfully added " + ChatColor.GREEN;
        String del = ChatColor.WHITE + ", " + ChatColor.GREEN;
        ArrayList<String> conv = new ArrayList<>();
        success.forEach(p -> conv.add(p.getName()));

        msg += String.join(del, conv);

        msg += ChatColor.WHITE + " to the team " + sel.color + sel.name;

        sender.sendMessage(msg);

        teams.forEach(CustomTeam::hideOtherPlayers);
    }

    private void deleteTeam(String[] args) {
        String name = args[1];

        teams.removeIf(team -> team.name.equalsIgnoreCase(name));
    }

    @EventHandler
    public void messageSent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        for (CustomTeam team : teams) {
            if (team.sendTeamMessage(player, message)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        ArrayList<String> values = new ArrayList<>();

        if (args.length == 1) {
            values.add("create");
            values.add("add");
            values.add("start");
            values.add("empty");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                teams.forEach(team -> values.add(team.name));
            }
            else if (args[0].equalsIgnoreCase("create")) {
                for (CommandColors value : CommandColors.values()) {
                    values.add(value.colorName);
                }
            }
            else if (args[0].equalsIgnoreCase("empty")) {
                // /mcw_teams empty <TeamName>
                teams.forEach(team -> values.add(team.name));
            }
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("add"))
            Bukkit.getOnlinePlayers().forEach(p -> values.add(p.getName()));

        return values;
    }

    /**
     * Empties the inputed team
     */
    public void commandEmptyTeam(String teamToEmpty, CommandSender sender)
    {
        if (!doesTeamExist(teamToEmpty))
            sender.sendMessage(ChatColor.RED + "The inputed team doesn't exist.");

        // Empty the team
        CustomTeam customTeamToEmpty = getTeamByName(teamToEmpty);
        customTeamToEmpty.players.clear();

        // Player feedback message
        String msg = "Successfully removed every player from team " + customTeamToEmpty.name;
        sender.sendMessage(ChatColor.GREEN + msg);
    }

    /**
     *
     * @param teamName
     * @return true if the inputed teamName is the name of a currently existing team.
     */
    public boolean doesTeamExist(String teamName)
    {
        for (CustomTeam team : teams)
        {
            if (team.name.equalsIgnoreCase(teamName))
                return true;
        }
        return false;
    }

    /**
     *
     * @param teamName
     * @return the {@code CustomTeam} with the inputed name. Returns null if it doesn't exist.
     */
    public CustomTeam getTeamByName(String teamName)
    {
        for (CustomTeam team : teams)
        {
            if (team.name.equalsIgnoreCase(teamName))
                return team;
        }

        return null;
    }

    public static class CustomTeam {

        ChatColor color;
        String name;
        ArrayList<Player> players;

        public int points = 0;

        public CustomTeam(ChatColor color, String name) {
            this.color = color;
            this.name = name;

            players = new ArrayList<>();
        }

        public void hideOtherPlayers() {
            players.forEach(p -> p.setCollidable(false));
            Bukkit.getOnlinePlayers().forEach(p -> {
                players.forEach(pl -> p.showPlayer(Main.getPlugin(Main.class), pl));
                if (!players.contains(p)) {
                    players.forEach(pl -> p.hidePlayer(Main.getPlugin(Main.class), pl));
                }
            });
        }

        public boolean sendTeamMessage(Player player, String message) {
            if (!players.contains(player)) return false;

            Bukkit.broadcastMessage(
                    (ChatColor.GRAY + "[#t] " + ChatColor.WHITE + "<#p>: #m")
                            .replace("#t", color + name + ChatColor.GRAY)
                            .replace("#p", player.getDisplayName())
                            .replace("#m", message)
            );

            return true;
        }
    }

    public enum CommandColors {

        BLACK("Black", ChatColor.BLACK),
        DARK_BLUE("DarkBlue", ChatColor.DARK_BLUE),
        DARK_GREEN("DarkGreen", ChatColor.DARK_GREEN),
        DARK_AQUA("DarkAqua", ChatColor.DARK_AQUA),
        DARK_RED("DarkRed", ChatColor.DARK_RED),
        DARK_PURPLE("DarkPurple", ChatColor.DARK_PURPLE),
        GOLD("Gold", ChatColor.GOLD),
        GRAY("Gray", ChatColor.GRAY),
        GREY("Grey", ChatColor.GRAY),
        DARK_GRAY("DarkGray", ChatColor.DARK_GRAY),
        DARK_GREY("DarkGrey", ChatColor.DARK_GRAY),
        BLUE("Blue", ChatColor.BLUE),
        GREEN("Green", ChatColor.GREEN),
        AQUA("Aqua", ChatColor.AQUA),
        RED("Red", ChatColor.RED),
        LIGHT_PURPLE("LightPurple", ChatColor.LIGHT_PURPLE),
        YELLOW("Yellow", ChatColor.YELLOW),
        WHITE("White", ChatColor.WHITE);

        final String colorName;
        final ChatColor color;

        CommandColors(String colorName, ChatColor color) {
            this.colorName = colorName;
            this.color = color;
        }

        public static ChatColor getByName(String name) {
            for (CommandColors value : values()) {
                if (value.colorName.equalsIgnoreCase(name)) {
                    return value.color;
                }
            }

            return BLACK.color;
        }
    }
}
