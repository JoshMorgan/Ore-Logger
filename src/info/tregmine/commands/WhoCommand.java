package info.tregmine.commands;

import static org.bukkit.ChatColor.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.ConnectionPool;
import info.tregmine.database.DBWalletDAO;

public class WhoCommand extends AbstractCommand
{
    public WhoCommand(Tregmine tregmine)
    {
        super(tregmine, "who");
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
        if (args.length != 1) {
            return false;
        }

        String pattern = args[0];

        List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
        if (candidates.size() != 1) {
            return true;
        }

        TregminePlayer whoPlayer = candidates.get(0);

        if (whoPlayer == null) {
            return true;
        }

        double X = whoPlayer.getLocation().getX();
        double Y = whoPlayer.getLocation().getY();
        double Z = whoPlayer.getLocation().getZ();

        float X2 = (float)Math.round(X);
        float Y2 = (float)Math.round(Y);
        float Z2 = (float)Math.round(Z);

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            DBWalletDAO walletDAO = new DBWalletDAO(conn);

            long balance = walletDAO.balance(whoPlayer);

            player.sendMessage(DARK_GRAY + "******************** " + DARK_PURPLE +
                               "PLAYER INFO" + DARK_GRAY + " ********************");
            player.sendMessage(GOLD + "Player: " + GRAY + whoPlayer.getChatName());
            player.sendMessage(GOLD + "World: " + GRAY + whoPlayer.getWorld().getName());
            player.sendMessage(GOLD + "Coords: " + GRAY + X2 + ", " + Y2 + ", " + Z2);
            player.sendMessage(GOLD + "Channel: " + GRAY + whoPlayer.getChatChannel());
            player.sendMessage(GOLD + "Wallet: " + GRAY + balance + " Tregs.");
            player.sendMessage(GOLD + "Health: " + GRAY + whoPlayer.getHealth());
            player.sendMessage(GOLD + "Country: " + GRAY + whoPlayer.getCountryName());
            player.sendMessage(GOLD + "IP Address: " + GRAY + whoPlayer.getIp());
            player.sendMessage(DARK_GRAY + "******************************************************");

            LOGGER.info(player.getName() + " used /who on player " + whoPlayer.getName());

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
