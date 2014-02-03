package info.tregmine.commands;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.Location;
import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SendToCommand extends AbstractCommand
{
    public SendToCommand(Tregmine tregmine)
    {
        super(tregmine, "sendto");
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
        if (!player.isAdmin()) {
            return false;
        }

        if (args.length != 2) {
            return false;
        }

        List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
        if (candidates.size() != 1) {
            // TODO: List users
            return true;
        }

        TregminePlayer victim = candidates.get(0);

        Server server = tregmine.getServer();
        World world = server.getWorld(args[1]);
        if (world == null) {
            // TODO: error message
            return false;
        }

        Location cpspawn = world.getSpawnLocation();
        victim.teleport(cpspawn);

        return true;
    }
}
