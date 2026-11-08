package organisespells.karmaaa;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.CastItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * organisespells java plugin
 */
public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER = Logger.getLogger("organisespells");

  public void onEnable()
  {
    saveDefaultConfig();
    getCommand("org").setExecutor(new SpellOrderCommand(this));
    getServer().getPluginManager().registerEvents(new SpellOrderMenu.MenuListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    LOGGER.info("OrganiseSpells activated");
  }

  public void onDisable()
  {
    LOGGER.info("OrganiseSpells deactivated");
  }

  public String getText(String path, String... replacements)
  {
    String text = getConfig().getString(path);
    if (text == null)
    {
      LOGGER.warning("Missing text in config.yml: " + path);
      text = path;
    }

    for (int i = 0; i + 1 < replacements.length; i += 2)
    {
      text = text.replace("{" + replacements[i] + "}", replacements[i + 1]);
    }
    return ChatColor.translateAlternateColorCodes('&', text);
  }

  public List<String> getTextList(String path, String... replacements)
  {
    List<String> lines = getConfig().getStringList(path);
    List<String> formattedLines = new ArrayList<String>();
    for (String line : lines)
    {
      formattedLines.add(getTextValue(line, replacements));
    }
    return formattedLines;
  }

  public Material getConfiguredMaterial(String path, Material defaultMaterial)
  {
    String configuredMaterial = getConfig().getString(path);
    Material material = configuredMaterial == null ? null
        : Material.matchMaterial(configuredMaterial.trim().toUpperCase(Locale.ROOT));
    if (material == null || material.isAir())
    {
      LOGGER.log(Level.WARNING, "Invalid item material at config.yml path ''{0}''. Using {1}.",
          new Object[] { path, defaultMaterial });
      return defaultMaterial;
    }
    return material;
  }

  private String getTextValue(String text, String... replacements)
  {
    for (int i = 0; i + 1 < replacements.length; i += 2)
    {
      text = text.replace("{" + replacements[i] + "}", replacements[i + 1]);
    }
    return ChatColor.translateAlternateColorCodes('&', text);
  }

  public void saveSpellOrder(Player player, CastItem castItem, List<Spell> spells)
  {
    List<String> spellNames = new ArrayList<String>();
    for (Spell spell : spells)
    {
      spellNames.add(spell.getInternalName());
    }

    String path = getSpellOrderPath(player, castItem);
    getConfig().set(path, spellNames);
    saveConfig();
  }

  private void restoreSpellOrders(Player player)
  {
    Spellbook spellbook = MagicSpells.getSpellbook(player);
    if (spellbook == null)
    {
      return;
    }

    for (Map.Entry<CastItem, List<Spell>> entry : spellbook.getItemSpells().entrySet())
    {
      restoreSpellOrder(player, spellbook, entry.getKey());
    }
  }

  public void restoreSpellOrder(Player player, Spellbook spellbook, CastItem castItem)
  {
    List<Spell> spells = spellbook.getItemSpells().get(castItem);
    List<String> savedOrder = getConfig().getStringList(getSpellOrderPath(player, castItem));
    if (spells == null || spells.isEmpty() || savedOrder.isEmpty())
    {
      return;
    }

    Spell activeSpell = getActiveSpell(spellbook, castItem, spells);
    List<Spell> reordered = new ArrayList<Spell>();
    for (String internalName : savedOrder)
    {
      for (Spell spell : spells)
      {
        if (spell.getInternalName().equals(internalName) && !reordered.contains(spell))
        {
          reordered.add(spell);
          break;
        }
      }
    }
    for (Spell spell : spells)
    {
      if (!reordered.contains(spell))
      {
        reordered.add(spell);
      }
    }

    spells.clear();
    spells.addAll(reordered);
    if (activeSpell != null)
    {
      spellbook.getActiveSpells().put(castItem, spells.indexOf(activeSpell));
    }
  }

  private Spell getActiveSpell(Spellbook spellbook, CastItem castItem, List<Spell> spells)
  {
    Integer activeIndex = spellbook.getActiveSpells().get(castItem);
    if (activeIndex == null || activeIndex < 0 || activeIndex >= spells.size())
    {
      return null;
    }
    return spells.get(activeIndex);
  }

  private String getSpellOrderPath(Player player, CastItem castItem)
  {
    String itemKey = Base64.getUrlEncoder().withoutPadding()
        .encodeToString(castItem.toString().getBytes(StandardCharsets.UTF_8));
    return "spell-orders." + player.getUniqueId() + "." + itemKey;
  }

  private class PlayerListener implements Listener
  {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
      Bukkit.getScheduler().runTask(Plugin.this, new Runnable()
      {
        @Override
        public void run()
        {
          if (event.getPlayer().isOnline())
          {
            restoreSpellOrders(event.getPlayer());
          }
        }
      });
    }
  }
}
