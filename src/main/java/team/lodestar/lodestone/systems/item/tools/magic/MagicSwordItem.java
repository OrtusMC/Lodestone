package team.lodestar.lodestone.systems.item.tools.magic;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;
import team.lodestar.lodestone.systems.item.tools.LodestoneSwordItem;

import java.util.ArrayList;
import java.util.List;

public class MagicSwordItem extends LodestoneSwordItem {
    public final float magicDamage;

    public MagicSwordItem(Tier material, int attackDamage, float attackSpeed, float magicDamage, Item.Properties properties) {
        super(material, attackDamage, attackSpeed, properties.durability(material.getUses()));
        this.magicDamage = magicDamage;
    }


    @Override
    public List<ItemAttributeModifiers.Entry> createExtraAttributes() {
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();
        var magic = new ItemAttributeModifiers.Entry(LodestoneAttributes.MAGIC_DAMAGE, new AttributeModifier(LodestoneLib.lodestonePath("magic_damage"), magicDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        entries.add(magic);
        return entries;
    }
}
