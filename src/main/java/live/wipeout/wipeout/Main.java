package live.wipeout.wipeout;

import live.wipeout.wipeout.game.Game;
import live.wipeout.wipeout.game.team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    Game game;

    TeamHandler handler;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("wipeout").setExecutor(this);

        handler = new TeamHandler();

        getServer().getPluginManager().registerEvents(handler, this);
        getCommand("mcw_teams").setExecutor(handler);
        getCommand("mcw_teams").setTabCompleter(handler);

        getServer().getPluginManager().registerEvents(new TeamHandler(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("wipeout.start")) {
            if (!sender.isOp()) return true;
        }

        if (label.equalsIgnoreCase("wipeout")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "No player provided");
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid player");
                return true;
            }

            if (game != null) {
                HandlerList.unregisterAll(game);
                game.getGameTimer().cancel();
            }

            game = null;

            game = new Game();
            // TODO: game.start(player, getConfig());

            getServer().getPluginManager().registerEvents(game, this);
        }

        return true;
    }

    public void cancelGame() {
        if (game != null) {
            HandlerList.unregisterAll(game);
            game.getGameTimer().cancel();
        }

        game = null;
    }
}
