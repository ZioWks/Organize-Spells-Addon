package organisespells.karmaaa;

import java.util.Collections;
import java.util.List;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.CastItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpellOrderMenu implements InventoryHolder
{
  private static final int PAGE_SIZE = 45;
  private static final int PREVIOUS_SLOT = 45;
  private static final int PAGE_INFO_SLOT = 49;
  private static final int NEXT_SLOT = 53;

  private final Player player;
  private final Spellbook spellbook;
  private final CastItem castItem;
  private final Plugin plugin;
  private final Inventory inventory;
  private int page;
  private int selectedIndex = -1;

  public SpellOrderMenu(Player player, Spellbook spellbook, CastItem castItem, Plugin plugin)
  {
    this.player = player;
    this.spellbook = spellbook;
    this.castItem = castItem;
    this.plugin = plugin;
    List<Spell> spells = spellbook.getItemSpells().get(castItem);
    int rows = Math.max(1, Math.min(6, (spells.size() + 8) / 9));
    if (spells.size() > 54)
    {
      rows = 6;
    }
    this.inventory = Bukkit.createInventory(this, rows * 9, plugin.getText("gui.title"));
    render();
  }

  @Override
  public Inventory getInventory()
  {
    return inventory;
  }

  private void onInventoryClick(InventoryClickEvent event)
  {
    if (event.getView().getTopInventory().getHolder() != this)
    {
      return;
    }

    event.setCancelled(true);
    if (event.getClickedInventory() != inventory)
    {
      return;
    }

    List<Spell> spells = getBoundSpells();
    if (spells == null || spells.isEmpty())
    {
      player.sendMessage(plugin.getText("messages.spells-unavailable"));
      player.closeInventory();
      return;
    }

    int pageCount = getPageCount(spells.size());
    int slot = event.getSlot();
    if (pageCount > 1 && slot == PREVIOUS_SLOT)
    {
      if (page > 0)
      {
        page--;
        render();
      }
      return;
    }
    if (pageCount > 1 && slot == NEXT_SLOT)
    {
      if (page + 1 < pageCount)
      {
        page++;
        render();
      }
      return;
    }
    if (slot < 0 || slot >= getSpellSlotCount(spells.size()))
    {
      return;
    }

    int clickedIndex = page * getSpellSlotCount(spells.size()) + slot;
    if (clickedIndex >= spells.size())
    {
      return;
    }

    if (selectedIndex < 0)
    {
      selectedIndex = clickedIndex;
      player.sendMessage(plugin.getText("messages.spell-selected",
          "spell", spells.get(clickedIndex).getName()));
      render();
      return;
    }

    if (selectedIndex == clickedIndex)
    {
      selectedIndex = -1;
      render();
      return;
    }

    Spell activeSpell = getActiveSpell(spells);
    Spell firstSpell = spells.get(selectedIndex);
    Spell secondSpell = spells.get(clickedIndex);
    Collections.swap(spells, selectedIndex, clickedIndex);
    updateActiveSpellIndex(activeSpell, spells);
    plugin.saveSpellOrder(player, castItem, spells);
    spellbook.save();

    selectedIndex = -1;
    player.sendMessage(plugin.getText("messages.spells-swapped",
        "first", firstSpell.getName(), "second", secondSpell.getName()));
    render();
  }

  private void onInventoryDrag(InventoryDragEvent event)
  {
    if (event.getView().getTopInventory().getHolder() == this)
    {
      event.setCancelled(true);
    }
  }

  private void render()
  {
    List<Spell> spells = getBoundSpells();
    if (spells == null)
    {
      return;
    }

    int pageCount = getPageCount(spells.size());
    int pageSize = getSpellSlotCount(spells.size());
    page = Math.min(page, pageCount - 1);
    inventory.clear();

    int start = page * pageSize;
    int end = Math.min(start + pageSize, spells.size());
    for (int index = start; index < end; index++)
    {
      inventory.setItem(index - start, createSpellItem(spells.get(index), index));
    }

    if (pageCount > 1)
    {
      if (page > 0)
      {
        inventory.setItem(PREVIOUS_SLOT, createButton(
            "gui.items.previous", Material.ARROW, "gui.items.previous-page"));
      }
      inventory.setItem(PAGE_INFO_SLOT, createButton(
          "gui.items.page", Material.BOOK,
          "gui.items.page.name", "page", String.valueOf(page + 1),
          "pages", String.valueOf(pageCount)));
      if (page + 1 < pageCount)
      {
        inventory.setItem(NEXT_SLOT, createButton(
            "gui.items.next", Material.ARROW, "gui.items.next-page"));
      }
    }
  }

  private ItemStack createSpellItem(Spell spell, int index)
  {
    ItemStack item = new ItemStack(plugin.getConfiguredMaterial(
        "gui.items.spell.material", Material.ENCHANTED_BOOK));
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(plugin.getText("gui.items.spell.name",
        "spell", spell.getName(), "position", String.valueOf(index + 1)));
    String status = plugin.getText(index == selectedIndex
            ? "gui.items.spell.selected-status"
            : "gui.items.spell.unselected-status",
        "position", String.valueOf(index + 1));
    meta.setLore(plugin.getTextList("gui.items.spell.lore",
        "spell", spell.getName(),
        "position", String.valueOf(index + 1),
        "status", status));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack createButton(String configPath, Material defaultMaterial, String textPath,
      String... replacements)
  {
    ItemStack item = new ItemStack(plugin.getConfiguredMaterial(
        configPath + ".material", defaultMaterial));
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(plugin.getText(textPath + ".name", replacements));
    meta.setLore(plugin.getTextList(textPath + ".lore", replacements));
    item.setItemMeta(meta);
    return item;
  }

  private List<Spell> getBoundSpells()
  {
    return spellbook.getItemSpells().get(castItem);
  }

  private Spell getActiveSpell(List<Spell> spells)
  {
    Integer activeIndex = spellbook.getActiveSpells().get(castItem);
    if (activeIndex == null || activeIndex < 0 || activeIndex >= spells.size())
    {
      return null;
    }
    return spells.get(activeIndex);
  }

  private void updateActiveSpellIndex(Spell activeSpell, List<Spell> spells)
  {
    if (activeSpell != null)
    {
      spellbook.getActiveSpells().put(castItem, spells.indexOf(activeSpell));
    }
  }

  private int getPageCount(int spellCount)
  {
    int pageSize = getSpellSlotCount(spellCount);
    return Math.max(1, (spellCount + pageSize - 1) / pageSize);
  }

  private int getSpellSlotCount(int spellCount)
  {
    return spellCount > 54 ? PAGE_SIZE : inventory.getSize();
  }

  public static class MenuListener implements org.bukkit.event.Listener
  {
    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
      if (event.getView().getTopInventory().getHolder() instanceof SpellOrderMenu)
      {
        ((SpellOrderMenu) event.getView().getTopInventory().getHolder()).onInventoryClick(event);
      }
    }

    @org.bukkit.event.EventHandler
    public void onInventoryDrag(InventoryDragEvent event)
    {
      if (event.getView().getTopInventory().getHolder() instanceof SpellOrderMenu)
      {
        ((SpellOrderMenu) event.getView().getTopInventory().getHolder()).onInventoryDrag(event);
      }
    }
  }
}
