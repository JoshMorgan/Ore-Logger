package info.tregmine.listeners;

import info.tregmine.Tregmine;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OreLogListener implements Listener{
    public static Tregmine plugin;
    public OreLogListener(Tregmine tregmine){
        plugin = tregmine;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Material b = event.getBlock().getType();
        Player player = event.getPlayer();
        
        if(player.getGameMode() != GameMode.CREATIVE){

        if (b==Material.DIAMOND_ORE){
            int diamonds = plugin.getConfig().getInt(player.getName() + ".diamond");
            plugin.getConfig().set(player.getName() + ".diamond", diamonds + 1);
        }
        
        if (b==Material.EMERALD_ORE){
            int emeralds = plugin.getConfig().getInt(player.getName() + ".emerald");
            plugin.getConfig().set(player.getName() + ".emerald", emeralds + 1);
        }
        
        if (b==Material.IRON_ORE){
            int irons = plugin.getConfig().getInt(player.getName() + ".iron");
            plugin.getConfig().set(player.getName() + ".iron", irons + 1);
        }
        
        if (b==Material.GOLD_ORE){
            int golds = plugin.getConfig().getInt(player.getName() + ".gold");
            plugin.getConfig().set(player.getName() + ".gold", golds + 1);
        }
        
        if (b==Material.COAL_ORE){
            int coals = plugin.getConfig().getInt(player.getName() + ".coal");
            plugin.getConfig().set(player.getName() + ".coal", coals + 1);
        }
        
        if (b==Material.REDSTONE_ORE){
            int redstones = plugin.getConfig().getInt(player.getName() + ".redstone");
            plugin.getConfig().set(player.getName() + ".redstone", redstones + 1);
        }
        
        if (b==Material.LAPIS_ORE){
            int lapiss = plugin.getConfig().getInt(player.getName() + ".lapis");
            plugin.getConfig().set(player.getName() + ".lapis", lapiss + 1);
        }
        
        if (b==Material.QUARTZ_ORE){
            int quartzs = plugin.getConfig().getInt(player.getName() + ".quartz");
            plugin.getConfig().set(player.getName() + ".quartz", quartzs + 1);
        }
        
        if (b==Material.STONE){
            int stones = plugin.getConfig().getInt(player.getName() + ".stone");
            plugin.getConfig().set(player.getName() + ".stone", stones + 1);
        }
        
        if (b==Material.NETHERRACK){
            int netherracks = plugin.getConfig().getInt(player.getName() + ".netherrack");
            plugin.getConfig().set(player.getName() + ".netherrack", netherracks + 1);
        }
        }
    }
}
