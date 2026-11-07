package dansplugins.mailboxes.objects;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Minimal {@link ItemMeta} that serializes the way a server's does, for tests that need an item
 * with metadata. Must be a concrete class: Bukkit looks serializable classes up by exact type.
 */
@SerializableAs("StubItemMeta")
public class StubItemMeta implements ItemMeta, ConfigurationSerializable {
    private String displayName;

    public StubItemMeta(String displayName) {
        this.displayName = displayName;
    }

    public static StubItemMeta deserialize(Map<String, Object> args) {
        return new StubItemMeta((String) args.get("displayName"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("displayName", displayName);
        return result;
    }

    @Override
    public boolean hasDisplayName() {
        return displayName != null;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public StubItemMeta clone() {
        return new StubItemMeta(displayName);
    }

    @Override
    public boolean hasLocalizedName() {
        return false;
    }

    @Override
    public String getLocalizedName() {
        return null;
    }

    @Override
    public void setLocalizedName(String name) {
    }

    @Override
    public boolean hasLore() {
        return false;
    }

    @Override
    public List<String> getLore() {
        return null;
    }

    @Override
    public void setLore(List<String> lore) {
    }

    @Override
    public boolean hasEnchants() {
        return false;
    }

    @Override
    public boolean hasEnchant(Enchantment ench) {
        return false;
    }

    @Override
    public int getEnchantLevel(Enchantment ench) {
        return 0;
    }

    @Override
    public Map<Enchantment, Integer> getEnchants() {
        return Collections.emptyMap();
    }

    @Override
    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        return false;
    }

    @Override
    public boolean removeEnchant(Enchantment ench) {
        return false;
    }

    @Override
    public boolean hasConflictingEnchant(Enchantment ench) {
        return false;
    }

    @Override
    public void addItemFlags(ItemFlag... itemFlags) {
    }

    @Override
    public void removeItemFlags(ItemFlag... itemFlags) {
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return Collections.emptySet();
    }

    @Override
    public boolean hasItemFlag(ItemFlag flag) {
        return false;
    }

    @Override
    public boolean isUnbreakable() {
        return false;
    }

    @Override
    public void setUnbreakable(boolean unbreakable) {
    }

    @Override
    public Spigot spigot() {
        return new Spigot();
    }
}
