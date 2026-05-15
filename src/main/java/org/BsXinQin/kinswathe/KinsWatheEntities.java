package org.BsXinQin.kinswathe;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.entities.CaptureDeviceEntity;
import org.jetbrains.annotations.NotNull;

public class KinsWatheEntities {

    public static final @NotNull EntityType<@NotNull CaptureDeviceEntity> CAPTURE_DEVICE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(KinsWathe.MOD_ID, "capture_device"),
            EntityType.Builder.create(CaptureDeviceEntity::new, SpawnGroup.MISC).dimensions(0.75F, 0.75F).build("capture_device")
    );

    public static void init() {}
}