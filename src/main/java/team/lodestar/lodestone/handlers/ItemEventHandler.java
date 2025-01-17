package team.lodestar.lodestone.handlers;

import com.mojang.datafixers.util.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.entity.living.*;
import team.lodestar.lodestone.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * A handler for firing {@link IEventResponderItem} events
 */
public class ItemEventHandler {

    private static final HashSet<EventResponderSource> LOOKUPS = new HashSet<>();

    public static final EventResponderSource HELD_ITEM = registerLookup(new EventResponderSource(LodestoneLib.lodestonePath("held_item"), e -> List.of(e.getMainHandItem())));

    public static final EventResponderSource ARMOR = registerLookup(new EventResponderSource(LodestoneLib.lodestonePath("armor"), e -> {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : e.getArmorSlots()) {
            stacks.add(stack);
        }
        return stacks;
    }));

    public static void triggerDeathResponses(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        var source = event.getSource();
        var target = event.getEntity();
        var attacker = source.getEntity() instanceof LivingEntity livingAttacker ? livingAttacker : target.getLastAttacker();
        getEventResponders(target).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.incomingDeathEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.outgoingDeathEvent(event, attacker, target, stack)));
        }
    }

    public static void triggerHurtResponses(LivingDamageEvent.Pre event) {
        if (event.getNewDamage() <= 0) return;
        var source = event.getSource();
        var target = event.getEntity();
        var attacker = source.getEntity() instanceof LivingEntity livingAttacker ? livingAttacker : target.getLastAttacker();
        getEventResponders(target).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.incomingDamageEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.outgoingDamageEvent(event, attacker, target, stack)));
        }
    }

    public static void addAttributeTooltips(AddAttributeTooltipsEvent event) {
        final ItemStack stack = event.getStack();
        if (stack.getItem() instanceof IEventResponderItem eventResponderItem) {
            eventResponderItem.modifyAttributeTooltipEvent(event);
        }
    }

    public static List<EventResponderLookupResult> getEventResponders(LivingEntity entity) {
        return LOOKUPS.stream().map(s -> s.getEventResponders(entity)).toList();
    }

    public static EventResponderSource registerLookup(EventResponderSource lookup) {
        LOOKUPS.add(lookup);
        return lookup;
    }


    /**
     * An interface containing various methods which are triggered alongside various forge events.
     * Implement on your item for the methods to be called.
     */
    public interface IEventResponderItem {

        default void modifyAttributeTooltipEvent(AddAttributeTooltipsEvent event) {

        }

        default void modifyAttributesEvent(ItemAttributeModifierEvent event) {
        }

        default void incomingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void incomingDeathEvent(LivingDeathEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDeathEvent(LivingDeathEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }
    }

    public record EventResponderLookupResult(EventResponderSource source, ArrayList<Pair<IEventResponderItem, ItemStack>> result) {

        public void run(BiConsumer<IEventResponderItem, ItemStack> consumer) {
            run(IEventResponderItem.class, consumer);
        }
        public <T extends IEventResponderItem> void run(Class<T> type, BiConsumer<T, ItemStack> consumer) {
            for (Pair<IEventResponderItem, ItemStack> pair : result) {
                if (type.isInstance(pair.getFirst())) {
                    consumer.accept(type.cast(pair.getFirst()), pair.getSecond());
                }
            }
        }
    }
    public static class EventResponderSource {

        public final ResourceLocation id;
        public final Function<LivingEntity, Collection<ItemStack>> stackFunction;

        public EventResponderSource(ResourceLocation id, Function<LivingEntity, Collection<ItemStack>> stackFunction) {
            this.id = id;
            this.stackFunction = stackFunction;
        }

        public final EventResponderLookupResult getEventResponders(LivingEntity entity) {
            Collection<ItemStack> sourced = stackFunction.apply(entity);
            ArrayList<Pair<IEventResponderItem, ItemStack>> result = new ArrayList<>();
            for (ItemStack stack : sourced) {
                if (stack.getItem() instanceof IEventResponderItem responderItem) {
                    result.add(Pair.of(responderItem, stack));
                }
            }
            return new EventResponderLookupResult(this, result);
        }
    }
}