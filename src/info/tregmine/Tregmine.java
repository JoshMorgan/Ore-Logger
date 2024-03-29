package info.tregmine;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import info.tregmine.quadtree.IntersectionException;

import org.eclipse.jetty.server.Server;

import info.tregmine.api.TregminePlayer;
import info.tregmine.database.ConnectionPool;
import info.tregmine.database.DBInventoryDAO;
import info.tregmine.database.DBZonesDAO;
import info.tregmine.database.DBPlayerDAO;
import info.tregmine.database.DBLogDAO;
import info.tregmine.zones.Lot;
import info.tregmine.zones.Zone;
import info.tregmine.zones.ZoneWorld;

import info.tregmine.listeners.*;
import info.tregmine.commands.*;
import info.tregmine.web.*;

/**
 * @author Ein Andersson
 * @author Emil Hernvall
 */
public class Tregmine extends JavaPlugin
{
    
    public final static int VERSION = 0;
    public final static int AMOUNT = 0;

    public final static Logger LOGGER = Logger.getLogger("Minecraft");

    private org.bukkit.Server server;

    private Server webServer;
    private WebHandler webHandler;

    private Map<String, TregminePlayer> players;
    private Map<Integer, TregminePlayer> playersById;
    private Map<Location, Integer> blessedBlocks;

    private Map<String, ZoneWorld> worlds;
    private Map<Integer, Zone> zones;

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        
        this.server = getServer();
        
        // Set up all data structures
        players = new HashMap<String, TregminePlayer>();
        playersById = new HashMap<Integer, TregminePlayer>();

        worlds = new TreeMap<String, ZoneWorld>(new Comparator<String>() {
            @Override
            public int compare(String a, String b)
            {
                return a.compareToIgnoreCase(b);
            }
        });

        zones = new HashMap<Integer, Zone>();

        // Set up all worlds
        WorldCreator world = new WorldCreator("world");
        world.environment(Environment.NORMAL);
        world.createWorld();

        WorldCreator nether = new WorldCreator("world_nether");
        nether.environment(Environment.NETHER);
        nether.createWorld();

        // Load blessed blocks
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            DBInventoryDAO inventoryDAO = new DBInventoryDAO(conn);
            this.blessedBlocks = inventoryDAO.loadBlessedBlocks(getServer());
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

        // Register all listeners
        PluginManager pluginMgm = server.getPluginManager();
        pluginMgm.registerEvents(new BlessedBlockListener(this), this);
        pluginMgm.registerEvents(new BoxFillBlockListener(this), this);
        pluginMgm.registerEvents(new ChatListener(this), this);
        pluginMgm.registerEvents(new CompassListener(this), this);
        pluginMgm.registerEvents(new PlayerLookupListener(this), this);
        pluginMgm.registerEvents(new SignColorListener(), this);
        pluginMgm.registerEvents(new TauntListener(this), this);
        pluginMgm.registerEvents(new TregmineBlockListener(this), this);
        pluginMgm.registerEvents(new TregminePlayerListener(this), this);
        pluginMgm.registerEvents(new ZoneBlockListener(this), this);
        pluginMgm.registerEvents(new ZoneEntityListener(this), this);
        pluginMgm.registerEvents(new ZonePlayerListener(this), this);
        pluginMgm.registerEvents(new OreLogCommand(this), this);
        pluginMgm.registerEvents(new OreLogListener(this), this);

        // Declaration of all commands
        getCommand("admins").setExecutor(new NotifyCommand(this, "admins") {
            @Override
            public boolean isTarget(TregminePlayer player)
            {
                return player.isAdmin();
            }
        });

        getCommand("guardians").setExecutor(
                new NotifyCommand(this, "guardians") {
                    @Override
                    public boolean isTarget(TregminePlayer player)
                    {
                        return player.isGuardian();
                    }
                });

        getCommand("action").setExecutor(new ActionCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("bless").setExecutor(new BlessCommand(this));
        getCommand("blockhere").setExecutor(new BlockHereCommand(this));
        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("clean").setExecutor(new CleanInventoryCommand(this));
        getCommand("cname").setExecutor(new ChangeNameCommand(this));
        getCommand("createmob").setExecutor(new CreateMobCommand(this));
        getCommand("createwarp").setExecutor(new CreateWarpCommand(this));
        getCommand("creative").setExecutor(new GameModeCommand(this, "creative", GameMode.CREATIVE));
        getCommand("fill").setExecutor(new FillCommand(this, "fill"));
        getCommand("force").setExecutor(new ForceCommand(this));
        getCommand("give").setExecutor(new GiveCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("inv").setExecutor(new InventoryCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("keyword").setExecutor(new KeywordCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("lot").setExecutor(new LotCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("newspawn").setExecutor(new NewSpawnCommand(this));
        getCommand("normal").setExecutor(new NormalCommand(this));
        getCommand("nuke").setExecutor(new NukeCommand(this));
        getCommand("orelog").setExecutor(new OreLogCommand(this));
        getCommand("password").setExecutor(new PasswordCommand(this));
        getCommand("pos").setExecutor(new PositionCommand(this));
        getCommand("regeneratechunk").setExecutor(new RegenerateChunkCommand(this));
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("say").setExecutor(new SayCommand(this));
        getCommand("sendto").setExecutor(new SendToCommand(this));
        getCommand("setspawner").setExecutor(new SetSpawnerCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("summon").setExecutor(new SummonCommand(this));
        getCommand("survival").setExecutor(new GameModeCommand(this, "survival", GameMode.SURVIVAL));
        getCommand("testfill").setExecutor(new FillCommand(this, "testfill"));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("town").setExecutor(new ZoneCommand(this, "town"));
        getCommand("tp").setExecutor(new TeleportCommand(this));
        getCommand("tpshield").setExecutor(new TeleportShieldCommand(this));
        getCommand("tpto").setExecutor(new TeleportToCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(this));
        getCommand("user").setExecutor(new UserCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("wallet").setExecutor(new WalletCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("who").setExecutor(new WhoCommand(this));
        getCommand("zone").setExecutor(new ZoneCommand(this, "zone"));

        try {
            webHandler = new WebHandler(this);
            webHandler.addAction(new VersionAction.Factory());
            webHandler.addAction(new OperatorListAction.Factory());

            webServer = new Server(9192);
            webServer.setHandler(webHandler);
            webServer.start();
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start web server!", e);
        }

        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, webHandler, 0, 20);
    }

    // run when plugin is disabled
    @Override
    public void onDisable()
    {
        saveConfig();
        
        server.getScheduler().cancelTasks(this);

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            // Add a record of logout to db for all players
            DBLogDAO logDAO = new DBLogDAO(conn);
            for (TregminePlayer player : getOnlinePlayers()) {
                player.sendMessage(ChatColor.AQUA
                        + "Tregmine successfully unloaded " + "build: "
                        + getDescription().getVersion());

                logDAO.insertLogin(player, true);

                removePlayer(player);
            }
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

    }

    @Override
    public void onLoad()
    {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            DBPlayerDAO playerDAO = new DBPlayerDAO(conn);
            DBLogDAO logDAO = new DBLogDAO(conn);

            Player[] players = getServer().getOnlinePlayers();
            for (Player player : players) {
                TregminePlayer tregPlayer = playerDAO.getPlayer(player);
                addPlayer(tregPlayer);

                player.sendMessage(ChatColor.GRAY
                        + "Tregmine successfully loaded " + "to build: "
                        + this.getDescription().getVersion());

                logDAO.insertLogin(tregPlayer, false);
            }
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
    }

    public void reloadPlayer(TregminePlayer player)
    {
        Connection conn = null;
        try {
            DBPlayerDAO playerDAO = new DBPlayerDAO(conn);

            TregminePlayer tregPlayer =
                    playerDAO.getPlayer(player.getDelegate());
            addPlayer(tregPlayer);
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
    }

    public ZoneWorld getWorld(World world)
    {
        ZoneWorld zoneWorld = worlds.get(world.getName());

        // lazy load zone worlds as required
        if (zoneWorld == null) {
            Connection conn = null;
            try {
                conn = ConnectionPool.getConnection();
                DBZonesDAO dao = new DBZonesDAO(conn);

                zoneWorld = new ZoneWorld(world);
                List<Zone> zones = dao.getZones(world.getName());
                for (Zone zone : zones) {
                    try {
                        zoneWorld.addZone(zone);
                        this.zones.put(zone.getId(), zone);
                    } catch (IntersectionException e) {
                        LOGGER.warning("Failed to load zone " + zone.getName()
                                + " with id " + zone.getId() + ".");
                    }
                }

                List<Lot> lots = dao.getLots(world.getName());
                for (Lot lot : lots) {
                    try {
                        zoneWorld.addLot(lot);
                    } catch (IntersectionException e) {
                        LOGGER.warning("Failed to load lot " + lot.getName()
                                + " with id " + lot.getId() + ".");
                    }
                }

                worlds.put(world.getName(), zoneWorld);
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
        }

        return zoneWorld;
    }

    public Zone getZone(int zoneId)
    {
        return zones.get(zoneId);
    }

    public List<TregminePlayer> getOnlinePlayers()
    {
        List<TregminePlayer> players = new ArrayList<TregminePlayer>();
        for (Player player : server.getOnlinePlayers()) {
            players.add(getPlayer(player));
        }

        return players;
    }

    public void addPlayer(TregminePlayer player)
    {
        players.put(player.getName(), player);
        playersById.put(player.getId(), player);
    }

    public void removePlayer(Player p)
    {
        TregminePlayer player = players.get(p.getName());
        if (player == null) {
            return;
        }

        players.remove(player.getName());
        playersById.remove(player.getId());
    }

    public TregminePlayer getPlayer(String name)
    {
        return players.get(name);
    }

    public TregminePlayer getPlayer(Player player)
    {
        return players.get(player.getName());
    }

    public TregminePlayer getPlayerOffline(String name)
    {
        if (players.containsKey(name)) {
            return players.get(name);
        }

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            DBPlayerDAO playerDAO = new DBPlayerDAO(conn);
            return playerDAO.getPlayer(name);
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
    }

    public TregminePlayer getPlayerOffline(int id)
    {
        if (playersById.containsKey(id)) {
            return playersById.get(id);
        }

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            DBPlayerDAO playerDAO = new DBPlayerDAO(conn);
            return playerDAO.getPlayer(id);
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
    }

    public List<TregminePlayer> matchPlayer(String pattern)
    {
        List<Player> matches = server.matchPlayer(pattern);
        if (matches.size() == 0) {
            return new ArrayList<TregminePlayer>();
        }

        List<TregminePlayer> decoratedMatches = new ArrayList<TregminePlayer>();
        for (Player match : matches) {
            TregminePlayer decoratedMatch = getPlayer(match);
            if (decoratedMatch == null) {
                continue;
            }
            decoratedMatches.add(decoratedMatch);
        }

        return decoratedMatches;
    }

    public Map<Location, Integer> getBlessedBlocks()
    {
        return blessedBlocks;
    }
}
