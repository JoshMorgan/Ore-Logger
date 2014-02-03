package info.tregmine.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Location;
import info.tregmine.Tregmine;
import info.tregmine.database.ConnectionPool;
import info.tregmine.database.DBWarpDAO;
import info.tregmine.api.TregminePlayer;

public class CreateWarpCommand extends AbstractCommand
{
    public CreateWarpCommand(Tregmine tregmine)
    {
        super(tregmine, "createwarp");
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
        if (args.length != 1) {
            return false;
        }
        if (!player.isAdmin()) {
            return false;
        }

        String name = args[0];

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            DBWarpDAO warpDAO = new DBWarpDAO(conn);

            Location loc = player.getLocation();
            warpDAO.insertWarp(name, loc);

            player.sendMessage("Warp " + args[0] + " created");
            LOGGER.info("WARPCREATE: " + args[0] + " by " + player.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

        return true;
    }
}
