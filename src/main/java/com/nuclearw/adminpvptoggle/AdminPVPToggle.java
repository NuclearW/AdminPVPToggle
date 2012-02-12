package com.nuclearw.adminpvptoggle;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminPVPToggle extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		// Register listeners
		getServer().getPluginManager().registerEvents(this, this);

		// Database
		initDatabase();

		// Hi ma
		getLogger().info("Finished Loading " + getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Finished Unloading "+getDescription().getFullName());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO: Commands for adding to and fro
		return true;
	}

	@Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PVPPlayer.class);
        return list;
    }

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.isCancelled()) return;
		if(!(event instanceof EntityDamageByEntityEvent)) return;
		EntityDamageByEntityEvent dEvent = (EntityDamageByEntityEvent) event;
		if(!(dEvent.getDamager() instanceof Player)) return;
		if(!(dEvent.getEntity() instanceof Player)) return;

		Player attacker = (Player) dEvent.getDamager();
		Player victim = (Player) dEvent.getEntity();

		if(!canPVP(attacker.getName()) || !canPVP(victim.getName())) {
			event.setCancelled(true);
			return;
		}
	}

	private boolean canPVP(String playerName) {
		int rowCount = getDatabase().find(PVPPlayer.class).where().ieq("name", playerName).findRowCount();
		if(rowCount > 0) return false;
		return true;
	}

	private void initDatabase() {
        try {
            getDatabase().find(PVPPlayer.class).findRowCount();
        } catch (PersistenceException ex) {
            getLogger().info("Initializing database");
            this.installDDL();
        }
	}
}
