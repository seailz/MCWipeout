package live.wipeout.wipeout.game;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class GameTimer extends BukkitRunnable {

    private long ticks = 0;
    private long songTicks = 1486;

    @Override
    public void run() {
        ticks++;

        if (ticks % songTicks == 0 || ticks == 1) {

            // Sound handling
            Bukkit.getServer().getOnlinePlayers().forEach(p -> {
//                p.sendMessage("glkuishfuhdlgkhsdf ");
                p.stopAllSounds();
//                p.setOp(true);
//                Bukkit.dispatchCommand(p, "playsound mcw:mcw.sfx.songloop ambient @s");
//                p.setOp(false);
                // Play an "ambient" sound to everyone, meaning they're gonna be able to hear it from wherever they are in the map
                p.playSound(p.getLocation(), "mcw:mcw.sfx.songloop", Integer.MAX_VALUE, 1);
            });
        }

//        if (ticks % 3 != 0) return;

        // Show the time of the participant to everyone
        Bukkit.getServer().getWorld("world").getPlayers().forEach(player -> {
            String timeActionBar = ChatColor.GREEN + getTime();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(timeActionBar));
        });

        // Old actionbar message that didn't work unfortunately
        //Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "title @a actionbar \"" + ChatColor.GREEN + getTime() + '"');
    }

    public String getTime() {
        long nticks = ticks % 20;
        long seconds = (ticks - nticks) / 20;
        long lseconds = seconds % 60;
        long minutes = (seconds - lseconds) / 60;

//        double ms = new Long(nticks).doubleValue() * 0.05;
//
//        String msStr = "" + ms;
//        if (msStr.length() > 3) {
//            msStr = msStr.substring(0, 3);
//        }
//
//        System.out.println(msStr);

        return "%m:%s"
                .replace("%m", minutes + "")
                .replace("%s", lseconds + "");
    }
}
