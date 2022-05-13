package live.wipeout.wipeout.game.team;

import live.wipeout.wipeout.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CustomTeam {

    public ChatColor color;
    public String name;
    public ArrayList<Player> players;

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
