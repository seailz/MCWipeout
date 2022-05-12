package live.wipeout.wipeout.game;

import lombok.Getter;
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
            Bukkit.getServer().getOnlinePlayers().forEach(p -> {
//                p.sendMessage("glkuishfuhdlgkhsdf ");
                p.stopAllSounds();
//                p.setOp(true);
//                Bukkit.dispatchCommand(p, "playsound mcw:mcw.sfx.songloop ambient @s");
//                p.setOp(false);
                p.playSound(p.getLocation(), "mcw:mcw.sfx.songloop", 1, 1);
            });
        }

//        if (ticks % 3 != 0) return;

        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "title @a actionbar \"" + ChatColor.GREEN + getTime() + '"');
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
