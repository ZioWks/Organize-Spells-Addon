package organisespells.karmaaa;

import java.util.List;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.CastItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpellOrderCommand implements CommandExecutor
{
  private final Plugin plugin;

  public SpellOrderCommand(Plugin plugin)
  {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
    {
      if (!sender.hasPermission("organisespells.command.reload"))
      {
        sender.sendMessage(plugin.getText("messages.reload-no-permission"));
        return true;
      }

      if (args.length > 1)
      {
        sender.sendMessage(plugin.getText("messages.usage", "label", label));
        return true;
      }

      plugin.reloadConfig();
      sender.sendMessage(plugin.getText("messages.reload-success"));
      return true;
    }

    if (!(sender instanceof Player))
    {
      sender.sendMessage(plugin.getText("messages.player-only"));
      return true;
    }

    if (args.length > 0)
    {
      sender.sendMessage(plugin.getText("messages.usage", "label", label));
      return true;
    }

    Player player = (Player) sender;
    ItemStack item = player.getInventory().getItemInMainHand();
    if (item.getType() == Material.AIR)
    {
      player.sendMessage(plugin.getText("messages.no-item"));
      return true;
    }

    Spellbook spellbook = MagicSpells.getSpellbook(player);
    if (spellbook == null)
    {
      player.sendMessage(plugin.getText("messages.spellbook-error"));
      return true;
    }

    CastItem castItem = new CastItem(item);
    plugin.restoreSpellOrder(player, spellbook, castItem);
    List<Spell> spells = spellbook.getItemSpells().get(castItem);
    if (spells == null || spells.isEmpty())
    {
      player.sendMessage(plugin.getText("messages.no-spells"));
      return true;
    }

    player.openInventory(new SpellOrderMenu(player, spellbook, castItem, plugin).getInventory());
    return true;
  }
}
