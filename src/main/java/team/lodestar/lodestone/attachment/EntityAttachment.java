package team.lodestar.lodestone.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.UnknownNullability;
import team.lodestar.lodestone.capabilityold.LodestoneEntityDataCapability;
import team.lodestar.lodestone.systems.fireeffect.FireEffectInstance;

public class EntityAttachment implements INBTSerializable<CompoundTag> {

    public FireEffectInstance fireEffectInstance;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (fireEffectInstance != null) {
            fireEffectInstance.serializeNBT(tag);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        fireEffectInstance = FireEffectInstance.deserializeNBT(tag);
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getEntity().level() instanceof ServerLevel) {
            syncTracking(event.getEntity());
        }
    }
}