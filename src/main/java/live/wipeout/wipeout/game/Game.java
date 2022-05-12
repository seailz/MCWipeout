package live.wipeout.wipeout.game;

import live.wipeout.wipeout.Main;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

@Getter
public class Game implements Listener {

    private Player participant;
    public GameTimer gameTimer = new GameTimer();
    Location startLoc;
    ArrayList<Location> checkpoints = new ArrayList<>();
    Location lastCP;
    boolean running = true;
    boolean started = false;

    public void start(Player participant, FileConfiguration config) {
        participant.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);

        gameTimer.runTaskTimer(Main.getPlugin(Main.class), 1, 1);

        this.participant = participant;

        int x = config.getInt("start.x");
        int y = config.getInt("start.y");
        int z = config.getInt("start.z");
        int yaw = config.getInt("start.yaw");

        startLoc = new Location(participant.getWorld(), x, y, z, yaw, 0);

//        for (int i = 0; i < 100; i++) {
//            var path = "checkpoints." + i;
//            if (config.contains(path)) {
//                x = config.getInt(path + ".x");
//                y = config.getInt(path + ".y");
//                z = config.getInt(path + ".z");
//                yaw = config.getInt(path + ".yaw");
//                checkpoints.add(new Location(participant.getWorld(), x, y, z, yaw, 0));
//            }
//        }

//        checkpoints.forEach(System.out::println);
//        System.out.println(startLoc);

        lastCP = startLoc;
        participant.teleport(startLoc);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (e.getPlayer().equals(participant)) {
            Main.getPlugin(Main.class).cancelGame();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (participant == null) return;
        if (!e.getPlayer().equals(participant)) return;

        if (!started)

        if (participant.getWorld().getBlockAt(participant.getLocation().clone().add(0, -1, 0)).getType().equals(Material.WHITE_STAINED_GLASS))
            lastCP = participant.getLocation().clone();

        if (participant.getWorld().getBlockAt(participant.getLocation().clone().add(0, -1, 0)).getType().equals(Material.BLACK_STAINED_GLASS))
            participant.teleport(lastCP);

        if (participant.getWorld().getBlockAt(participant.getLocation().clone().add(0, -1, 0)).getType().equals(Material.YELLOW_CONCRETE) && running) {
            gameTimer.cancel();
            long ticks = gameTimer.ticks;

            Long nticks = ticks % 20;
            long seconds = (ticks - nticks) / 20;
            long lseconds = seconds % 60;
            long minutes = (seconds - lseconds) / 60;

            double ms = nticks.doubleValue() * 0.05;

            String msStr = "" + ms;
            if (msStr.length() > 3) {
                msStr = msStr.substring(0, 4);

            }

            String finalMsStr = msStr;
            Bukkit.getServer().getOnlinePlayers().forEach(p ->
                    p.sendMessage("Final time: " + ChatColor.GREEN + minutes + ":" + lseconds + "." + finalMsStr.replace("0.", "")));

            running = false;

            Bukkit.getServer().getOnlinePlayers().forEach(p ->
                    p.stopSound("mcw:mcw.sfx.songloop"));
        }
    }
}
