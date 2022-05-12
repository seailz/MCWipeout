package live.wipeout.wipeout.game;

import live.wipeout.wipeout.game.team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Checkpoint {

    private Location location;
    private Integer[] points;
    private int defaultPoints;

    int index = 0;

    List<TeamHandler.CustomTeam> passed = new ArrayList<TeamHandler.CustomTeam>();

    public Checkpoint(Location location, Integer[] points, int defaultPoints) {
        this.location = location;
        this.points = points;
        this.defaultPoints = defaultPoints;
    }

    public void register(Location location, TeamHandler.CustomTeam team) {
        if (passed.contains(team)) return;
        if (location.distance(this.location) > 3) return;

        int score = 0;
        if (index < points.length) {
            score = points[index];
            index++;
        } else {
            score = defaultPoints;
        }

        team.points += score;

        Bukkit.broadcastMessage(team.color + team.name + ChatColor.WHITE + " scored " + ChatColor.YELLOW + score + ChatColor.WHITE + " points for a total of " + ChatColor.YELLOW + team.points);

        passed.add(team);
    }
}
