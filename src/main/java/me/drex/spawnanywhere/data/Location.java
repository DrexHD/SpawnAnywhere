package me.drex.spawnanywhere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public record Location(ResourceKey<Level> dimension, BlockPos pos, float yRot, float xRot) {

    public static final Codec<Location> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            (Level.RESOURCE_KEY_CODEC.fieldOf("dimension")).forGetter(Location::dimension),
            BlockPos.CODEC.fieldOf("pos").forGetter(Location::pos),
            Codec.FLOAT.fieldOf("yRot").forGetter(Location::yRot),
            Codec.FLOAT.fieldOf("xRot").forGetter(Location::xRot)
        ).apply(instance, Location::new));

    public Location(Entity entity) {
        this(entity.level().dimension(), entity.blockPosition(), entity.getYRot(), entity.getXRot());
    }

    public DimensionTransition dimensionTransition(MinecraftServer server, DimensionTransition.PostDimensionTransition postDimensionTransition) {
        ServerLevel level = server.getLevel(dimension());
        return new DimensionTransition(level, pos().getCenter(), Vec3.ZERO, yRot(), xRot(), postDimensionTransition);
    }
}
