package info.tregmine.commands;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

import static org.bukkit.ChatColor.*;
import org.bukkit.Server;
import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.PlayerReport;
import info.tregmine.database.ConnectionPool;
import info.tregmine.database.DBPlayerReportDAO;

public class KickCommand extends AbstractCommand
{
    public KickCommand(Tregmine tregmine)
    {
        super(tregmine, "kick");
    }

    private String argsToMessage(String[] args)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 1; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
        if (!player.isAdmin() && !player.isGuardian()) {
            return false;
        }

        if (args.length < 2) {
            player.sendMessage(DARK_AQUA + "/kick <player> <message>");
            return true;
        }

        Server server = player.getServer();
        String pattern = args[0];
        String message = argsToMessage(args);

        List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
        if (candidates.size() != 1) {
            // TODO: error message
            return true;
        }

        TregminePlayer victim = candidates.get(0);
        server.broadcastMessage(player.getChatName() + AQUA + " kicked "
                + victim.getChatName() + AQUA + ": " + message);
        LOGGER.info(victim.getName() + " kicked by " + player.getName());
        victim.kickPlayer("kicked by " + player.getName() + ": " + message);

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            PlayerReport report = new PlayerReport();
            report.setSubjectId(victim.getId());
            report.setIssuerId(player.getId());
            report.setAction(PlayerReport.Action.KICK);
            report.setMessage(message);

            DBPlayerReportDAO reportDAO = new DBPlayerReportDAO(conn);
            reportDAO.insertReport(report);
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

    public boolean handleOther(Server server, String[] args)
    {
        if (args.length == 0) {
            return false;
        }

        String pattern = args[0];

        List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
        if (candidates.size() != 1) {
            // TODO: error message
            return true;
        }

        TregminePlayer victim = candidates.get(0);

        server.broadcastMessage("GOD kicked " + victim.getChatName() + ".");
        victim.kickPlayer("kicked by GOD.");

        return true;
    }
}
