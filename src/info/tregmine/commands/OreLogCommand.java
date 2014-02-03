package info.tregmine.commands;

import static org.bukkit.ChatColor.*;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class OreLogCommand extends AbstractCommand implements Listener
{
    public OreLogCommand(Tregmine tregmine)
    {
        super(tregmine, "orelog");
        this.plugin = tregmine;
    }


    Tregmine plugin;

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

        TregminePlayer tplayer = candidates.get(0);

        if (tplayer != null) {

            FileConfiguration config = plugin.getConfig();

            int coalcount = config.getInt(tplayer.getName() + ".coal");
            int diamcount = config.getInt(tplayer.getName() + ".diamond");
            int emercount = config.getInt(tplayer.getName() + ".emerald");
            int goldcount = config.getInt(tplayer.getName() + ".gold");
            int ironcount = config.getInt(tplayer.getName() + ".iron");
            int lapicount = config.getInt(tplayer.getName() + ".lapis");
            int quarcount = config.getInt(tplayer.getName() + ".quartz");
            int redscount = config.getInt(tplayer.getName() + ".redstone");
            int nethcount = config.getInt(tplayer.getName() + ".netherrack");
            int stoncount = config.getInt(tplayer.getName() + ".stone");

            int a = coalcount;
            int b = diamcount;
            int c = emercount;
            int d = goldcount;
            int e = ironcount;
            int f = lapicount;
            int g = quarcount;
            int h = redscount;
            int i = nethcount;
            int j = stoncount;

            int total = a+b+c+d+e+f+g+h+i+j;

            long coalperc = Math.round(a * 100.00/total);
            long diamperc = Math.round(b * 100.00/total);
            long emerperc = Math.round(c * 100.00/total);
            long goldperc = Math.round(d * 100.00/total);
            long ironperc = Math.round(e * 100.00/total);
            long lapiperc = Math.round(f * 100.00/total);
            long quarperc = Math.round(g * 100.00/total);
            long redsperc = Math.round(h * 100.00/total);
            long nethperc = Math.round(i * 100.00/total);
            long stonperc = Math.round(j * 100.00/total);

            ChatColor coallevel = ChatColor.DARK_GREEN;
            ChatColor diamlevel = ChatColor.DARK_GREEN;
            ChatColor emerlevel = ChatColor.DARK_GREEN;
            ChatColor goldlevel = ChatColor.DARK_GREEN;
            ChatColor ironlevel = ChatColor.DARK_GREEN;
            ChatColor lapilevel = ChatColor.DARK_GREEN;
            ChatColor quarlevel = ChatColor.DARK_GREEN;
            ChatColor redslevel = ChatColor.DARK_GREEN;
            ChatColor nethlevel = ChatColor.DARK_GREEN;
            ChatColor stonlevel = ChatColor.DARK_GREEN;

            player.sendMessage(DARK_GRAY + "********** " + GOLD + "Ore Log for: " + tplayer.getChatName() + DARK_GRAY + " *********");

            if (coalcount > 0) { 
                if (coalperc > 5) { coallevel = ChatColor.RED; } 
                else if (coalperc > 3) { coallevel = ChatColor.YELLOW; } else
                { coallevel = ChatColor.GREEN; }
                player.sendMessage(coallevel + "Coal: " + coalperc + "%" + " (" + coalcount + ")");
            }else{
                player.sendMessage(GREEN + "Coal: none");
            }

            if (diamcount > 0) { 
                if (diamperc > 3) { diamlevel = ChatColor.RED; } 
                else if (diamperc > 0.5) { diamlevel = ChatColor.YELLOW; } else
                { diamlevel = ChatColor.GREEN; }
                player.sendMessage(diamlevel + "Diamond: " + diamperc + "%" + " (" + diamcount + ")");
            }else{
                player.sendMessage(GREEN + "Diamond: none");
            }

            if (emercount > 0) { 
                if (emerperc > 3) { emerlevel = ChatColor.RED; } 
                else if (emerperc > 0.5) { emerlevel = ChatColor.YELLOW; } else
                { emerlevel = ChatColor.GREEN; }
                player.sendMessage(emerlevel + "Emerald: " + emerperc + "%" + " (" + emercount + ")");
            }else{
                player.sendMessage(GREEN + "Emerald: none");
            }

            if (goldcount > 0) { 
                if (goldperc > 5) { goldlevel = ChatColor.RED; } 
                else if (goldperc > 3) { goldlevel = ChatColor.YELLOW; } else
                { goldlevel = ChatColor.GREEN; }
                player.sendMessage(goldlevel + "Gold: " + goldperc + "%" + " (" + goldcount + ")");
            }else{
                player.sendMessage(GREEN + "Gold: none");
            }

            if (ironcount > 0) { 
                if (ironperc > 7) { ironlevel = ChatColor.RED; } 
                else if (ironperc > 3) { ironlevel = ChatColor.YELLOW; } else
                { ironlevel = ChatColor.GREEN; }
                player.sendMessage(ironlevel + "Iron: " + ironperc + "%" + " (" + ironcount + ")");
            }else{
                player.sendMessage(GREEN + "Iron: none");
            }

            if (lapicount > 0) { 
                if (lapiperc > 5) { lapilevel = ChatColor.RED; } 
                else if (lapiperc > 3) { lapilevel = ChatColor.YELLOW; } else
                { lapilevel = ChatColor.GREEN; }
                player.sendMessage(lapilevel + "Lapis: " + lapiperc + "%" + " (" + lapicount + ")");
            }else{
                player.sendMessage(GREEN + "Lapis: none");
            }

            if (quarcount > 0) { 
                if (quarperc > 10) { quarlevel = ChatColor.RED; } 
                else if (quarperc > 5) { quarlevel = ChatColor.YELLOW; } else
                { quarlevel = ChatColor.GREEN; }
                player.sendMessage(quarlevel + "Quartz: " + quarperc + "%" + " (" + quarcount + ")");
            }else{
                player.sendMessage(GREEN + "Quartz: none");
            }

            if (redscount > 0) { 
                if (redsperc > 5) { redslevel = ChatColor.RED; } 
                else if (redsperc > 3) { redslevel = ChatColor.YELLOW; } else
                { redslevel = ChatColor.GREEN; }
                player.sendMessage(redslevel + "Redstone: " + redsperc + "%" + " (" + redscount + ")");
            }else{
                player.sendMessage(GREEN + "Redstone: none");
            }

            if (nethcount > 0) { 
                if (nethperc > 101) { nethlevel = ChatColor.RED; } 
                else if (nethperc > 100) { nethlevel = ChatColor.YELLOW; } else
                { nethlevel = ChatColor.GREEN; }
                player.sendMessage(nethlevel + "Netherrack: " + nethperc + "%" + " (" + nethcount + ")");
            }else{
                player.sendMessage(GREEN + "Netherrack: none");
            }

            if (stoncount > 0) { 
                if (stonperc > 101) { stonlevel = ChatColor.RED; } 
                else if (stonperc > 100) { stonlevel = ChatColor.YELLOW; } else
                { stonlevel = ChatColor.GREEN; }
                player.sendMessage(stonlevel + "Stone: " + stonperc + "%" + " (" + stoncount + ")");
            }else{
                player.sendMessage(GREEN + "Stone: none");
            }

            player.sendMessage(DARK_GRAY + "********** " + GOLD + "End of " + tplayer.getChatName() + "'s" + GOLD + " log" + DARK_GRAY + " **********");

        }else{
            player.sendMessage(RED + "/orelog <player>");
        }

        LOGGER.info(player.getName() + " used /who on player " + tplayer.getName());

        return true;
    }
}

/**
 * @author Josh Morgan
 * last edit: 15/07/2013 9:06PM
 */
