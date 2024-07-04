package me.drex.spawnanywhere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record SpawnAnywhereData(Optional<Location> spawnLocation, Optional<Location> respawnLocation) {

    public static final Codec<SpawnAnywhereData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Location.CODEC.optionalFieldOf("spawn_location").forGetter(SpawnAnywhereData::spawnLocation),
            Location.CODEC.optionalFieldOf("respawn_location").forGetter(SpawnAnywhereData::respawnLocation)
        ).apply(instance, SpawnAnywhereData::new));

    public SpawnAnywhereData withSpawnLocation(Location spawnLocation) {
        return new SpawnAnywhereData(Optional.of(spawnLocation), respawnLocation());
    }

    public SpawnAnywhereData withRespawnLocation(Location respawnLocation) {
        return new SpawnAnywhereData(spawnLocation(), Optional.of(respawnLocation));
    }

}
