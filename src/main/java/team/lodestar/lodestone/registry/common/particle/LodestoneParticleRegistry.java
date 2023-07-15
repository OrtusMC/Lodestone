package team.lodestar.lodestone.registry.common.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.particle.type.LodestoneParticleType;

@SuppressWarnings("unused")
public class LodestoneParticleRegistry {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LodestoneLib.LODESTONE);

    public static RegistryObject<LodestoneParticleType> WISP_PARTICLE = PARTICLES.register("wisp", LodestoneParticleType::new);
    public static RegistryObject<LodestoneParticleType> SMOKE_PARTICLE = PARTICLES.register("smoke", LodestoneParticleType::new);
    public static RegistryObject<LodestoneParticleType> SPARKLE_PARTICLE = PARTICLES.register("sparkle", LodestoneParticleType::new);
    public static RegistryObject<LodestoneParticleType> TWINKLE_PARTICLE = PARTICLES.register("twinkle", LodestoneParticleType::new);
    public static RegistryObject<LodestoneParticleType> STAR_PARTICLE = PARTICLES.register("star", LodestoneParticleType::new);

    public static void registerParticleFactory(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(WISP_PARTICLE.get(), LodestoneParticleType.Factory::new);
        event.registerSpriteSet(SMOKE_PARTICLE.get(), LodestoneParticleType.Factory::new);
        event.registerSpriteSet(SPARKLE_PARTICLE.get(), LodestoneParticleType.Factory::new);
        event.registerSpriteSet(TWINKLE_PARTICLE.get(), LodestoneParticleType.Factory::new);
        event.registerSpriteSet(STAR_PARTICLE.get(), LodestoneParticleType.Factory::new);
    }
}