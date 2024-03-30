package works.nuty.calcite.parser;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.StringReader;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import works.nuty.calcite.parser.common.*;
import works.nuty.calcite.parser.primitive.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityParser extends CompoundParser<Entity> {
    private final static Map<String, Class<? extends Entity>> ENTITY_REGISTRY_ID_2_CLASS = ImmutableMap.<String, Class<? extends Entity>>builder().put("minecraft:allay", AllayEntity.class).put("minecraft:area_effect_cloud", AreaEffectCloudEntity.class).put("minecraft:armadillo", ArmadilloEntity.class).put("minecraft:armor_stand", ArmorStandEntity.class).put("minecraft:arrow", ArrowEntity.class).put("minecraft:axolotl", AxolotlEntity.class).put("minecraft:bat", BatEntity.class).put("minecraft:bee", BeeEntity.class).put("minecraft:blaze", BlazeEntity.class).put("minecraft:block_display", DisplayEntity.BlockDisplayEntity.class).put("minecraft:boat", BoatEntity.class).put("minecraft:bogged", BoggedEntity.class).put("minecraft:breeze", BreezeEntity.class).put("minecraft:breeze_wind_charge", BreezeWindChargeEntity.class).put("minecraft:camel", CamelEntity.class).put("minecraft:cat", CatEntity.class).put("minecraft:cave_spider", CaveSpiderEntity.class).put("minecraft:chest_boat", ChestBoatEntity.class).put("minecraft:chest_minecart", ChestMinecartEntity.class).put("minecraft:chicken", ChickenEntity.class).put("minecraft:cod", CodEntity.class).put("minecraft:command_block_minecart", CommandBlockMinecartEntity.class).put("minecraft:cow", CowEntity.class).put("minecraft:creeper", CreeperEntity.class).put("minecraft:dolphin", DolphinEntity.class).put("minecraft:donkey", DonkeyEntity.class).put("minecraft:dragon_fireball", DragonFireballEntity.class).put("minecraft:drowned", DrownedEntity.class).put("minecraft:egg", EggEntity.class).put("minecraft:elder_guardian", ElderGuardianEntity.class).put("minecraft:end_crystal", EndCrystalEntity.class).put("minecraft:ender_dragon", EnderDragonEntity.class).put("minecraft:ender_pearl", EnderPearlEntity.class).put("minecraft:enderman", EndermanEntity.class).put("minecraft:endermite", EndermiteEntity.class).put("minecraft:evoker", EvokerEntity.class).put("minecraft:evoker_fangs", EvokerFangsEntity.class).put("minecraft:experience_bottle", ExperienceBottleEntity.class).put("minecraft:experience_orb", ExperienceOrbEntity.class).put("minecraft:eye_of_ender", EyeOfEnderEntity.class).put("minecraft:falling_block", FallingBlockEntity.class).put("minecraft:firework_rocket", FireworkRocketEntity.class).put("minecraft:fox", FoxEntity.class).put("minecraft:frog", FrogEntity.class).put("minecraft:furnace_minecart", FurnaceMinecartEntity.class).put("minecraft:ghast", GhastEntity.class).put("minecraft:giant", GiantEntity.class).put("minecraft:glow_item_frame", GlowItemFrameEntity.class).put("minecraft:glow_squid", GlowSquidEntity.class).put("minecraft:goat", GoatEntity.class).put("minecraft:guardian", GuardianEntity.class).put("minecraft:hoglin", HoglinEntity.class).put("minecraft:hopper_minecart", HopperMinecartEntity.class).put("minecraft:horse", HorseEntity.class).put("minecraft:husk", HuskEntity.class).put("minecraft:illusioner", IllusionerEntity.class).put("minecraft:interaction", InteractionEntity.class).put("minecraft:iron_golem", IronGolemEntity.class).put("minecraft:item", ItemEntity.class).put("minecraft:item_display", DisplayEntity.ItemDisplayEntity.class).put("minecraft:item_frame", ItemFrameEntity.class).put("minecraft:fireball", FireballEntity.class).put("minecraft:leash_knot", LeashKnotEntity.class).put("minecraft:lightning_bolt", LightningEntity.class).put("minecraft:llama", LlamaEntity.class).put("minecraft:llama_spit", LlamaSpitEntity.class).put("minecraft:magma_cube", MagmaCubeEntity.class).put("minecraft:marker", MarkerEntity.class).put("minecraft:minecart", MinecartEntity.class).put("minecraft:mooshroom", MooshroomEntity.class).put("minecraft:mule", MuleEntity.class).put("minecraft:ocelot", OcelotEntity.class).put("minecraft:painting", PaintingEntity.class).put("minecraft:panda", PandaEntity.class).put("minecraft:parrot", ParrotEntity.class).put("minecraft:phantom", PhantomEntity.class).put("minecraft:pig", PigEntity.class).put("minecraft:piglin", PiglinEntity.class).put("minecraft:piglin_brute", PiglinBruteEntity.class).put("minecraft:pillager", PillagerEntity.class).put("minecraft:polar_bear", PolarBearEntity.class).put("minecraft:potion", PotionEntity.class).put("minecraft:pufferfish", PufferfishEntity.class).put("minecraft:rabbit", RabbitEntity.class).put("minecraft:ravager", RavagerEntity.class).put("minecraft:salmon", SalmonEntity.class).put("minecraft:sheep", SheepEntity.class).put("minecraft:shulker", ShulkerEntity.class).put("minecraft:shulker_bullet", ShulkerBulletEntity.class).put("minecraft:silverfish", SilverfishEntity.class).put("minecraft:skeleton", SkeletonEntity.class).put("minecraft:skeleton_horse", SkeletonHorseEntity.class).put("minecraft:slime", SlimeEntity.class).put("minecraft:small_fireball", SmallFireballEntity.class).put("minecraft:sniffer", SnifferEntity.class).put("minecraft:snow_golem", SnowGolemEntity.class).put("minecraft:snowball", SnowballEntity.class).put("minecraft:spawner_minecart", SpawnerMinecartEntity.class).put("minecraft:spectral_arrow", SpectralArrowEntity.class).put("minecraft:spider", SpiderEntity.class).put("minecraft:squid", SquidEntity.class).put("minecraft:stray", StrayEntity.class).put("minecraft:strider", StriderEntity.class).put("minecraft:tadpole", TadpoleEntity.class).put("minecraft:text_display", DisplayEntity.TextDisplayEntity.class).put("minecraft:tnt", TntEntity.class).put("minecraft:tnt_minecart", TntMinecartEntity.class).put("minecraft:trader_llama", TraderLlamaEntity.class).put("minecraft:trident", TridentEntity.class).put("minecraft:tropical_fish", TropicalFishEntity.class).put("minecraft:turtle", TurtleEntity.class).put("minecraft:vex", VexEntity.class).put("minecraft:villager", VillagerEntity.class).put("minecraft:vindicator", VindicatorEntity.class).put("minecraft:wandering_trader", WanderingTraderEntity.class).put("minecraft:warden", WardenEntity.class).put("minecraft:wind_charge", WindChargeEntity.class).put("minecraft:witch", WitchEntity.class).put("minecraft:wither", WitherEntity.class).put("minecraft:wither_skeleton", WitherSkeletonEntity.class).put("minecraft:wither_skull", WitherSkullEntity.class).put("minecraft:wolf", WolfEntity.class).put("minecraft:zoglin", ZoglinEntity.class).put("minecraft:zombie", ZombieEntity.class).put("minecraft:zombie_horse", ZombieHorseEntity.class).put("minecraft:zombie_villager", ZombieVillagerEntity.class).put("minecraft:zombified_piglin", ZombifiedPiglinEntity.class).put("minecraft:player", PlayerEntity.class).put("minecraft:fishing_bobber", FishingBobberEntity.class).build();
    private Class<? extends Entity> entityClass;

    public EntityParser(DefaultParser parser) {
        super(parser);
        this.entityClass = Entity.class;
    }

    public EntityParser(StringReader reader, String entityRegistryId) {
        super(reader);
        this.entityClass = getEntityClassById(entityRegistryId);
    }

    @Override
    public void registerOptions() {
        register("id", new EntityOption(Entity.class, parser -> {
            int cursor1 = parser.reader().getCursor();
            new RegistryParser<>(parser, Registries.ENTITY_TYPE, EntityType::isSummonable).parse();
            int cursor2 = parser.reader().getCursor();

            parser.reader().setCursor(cursor1);
            String entityId = parser.reader().readString();
            if (parser instanceof EntityParser entityParser) {
                entityParser.setEntityType(entityId);
            }
            parser.reader().setCursor(cursor2);
        }, parser -> parser.getEntityClass().equals(Entity.class)));

        register("Pos", new EntityOption(Entity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()));
        register("Motion", new EntityOption(Entity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()));
        register("Rotation", new EntityOption(Entity.class, parser -> new ListParser(parser, new FloatParser(parser), NumberRange.IntRange.exactly(2)).parse()));
        register("FallDistance", new EntityOption(Entity.class, ValueHandler.FLOAT));
        register("Fire", new EntityOption(Entity.class, ValueHandler.SHORT));
        register("Air", new EntityOption(Entity.class, ValueHandler.SHORT));
        register("OnGround", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("Invulnerable", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("PortalCooldown", new EntityOption(Entity.class, ValueHandler.INT));
        register("UUID", new EntityOption(Entity.class, ValueHandler.UUID));
        register("CustomName", new EntityOption(Entity.class, ValueHandler.STRING));
        register("CustomNameVisible", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("Silent", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("NoGravity", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("Glowing", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("TicksFrozen", new EntityOption(Entity.class, ValueHandler.INT));
        register("HasVisualFire", new EntityOption(Entity.class, ValueHandler.BOOLEAN));
        register("Tags", new EntityOption(Entity.class, parser -> new ListParser(parser, new StringParser(parser), NumberRange.IntRange.atMost(1024)).parse()));
        register("Passengers", new EntityOption(Entity.class, parser -> new ListParser(parser, new EntityParser(parser), NumberRange.IntRange.ANY).parse()));

        register("TileX", new EntityOption(AbstractDecorationEntity.class, ValueHandler.INT, parser -> !parser.getEntityClass().equals(LeashKnotEntity.class)));
        register("TileY", new EntityOption(AbstractDecorationEntity.class, ValueHandler.INT, parser -> !parser.getEntityClass().equals(LeashKnotEntity.class)));
        register("TileZ", new EntityOption(AbstractDecorationEntity.class, ValueHandler.INT, parser -> !parser.getEntityClass().equals(LeashKnotEntity.class)));

        register("ChestedHorse", new EntityOption(AbstractDonkeyEntity.class, ValueHandler.BOOLEAN));
//        register("Items", new EntityOption(AbstractDonkeyEntity.class, parser -> new AnyParser(parser).parse()));

//        register("Item", new EntityOption(AbstractFireballEntity.class, parser -> new AnyParser(parser).parse()));

        register("EatingHayStack", new EntityOption(AbstractHorseEntity.class, ValueHandler.BOOLEAN));
        register("Bred", new EntityOption(AbstractHorseEntity.class, ValueHandler.BOOLEAN));
        register("Temper", new EntityOption(AbstractHorseEntity.class, ValueHandler.INT));
        register("Tame", new EntityOption(AbstractHorseEntity.class, ValueHandler.BOOLEAN));
        register("Owner", new EntityOption(AbstractHorseEntity.class, ValueHandler.UUID_OR_PLAYER_NAME));
//        register("SaddleItem", new EntityOption(AbstractHorseEntity.class, parser -> new AnyParser(parser).parse()));

        register("CustomDisplayTile", new EntityOption(AbstractMinecartEntity.class, ValueHandler.BOOLEAN));
//        register("DisplayState", new EntityOption(AbstractMinecartEntity.class, parser -> new AnyParser(parser).parse()));
        register("DisplayOffset", new EntityOption(AbstractMinecartEntity.class, ValueHandler.INT));

        register("IsImmuneToZombification", new EntityOption(AbstractPiglinEntity.class, ValueHandler.BOOLEAN));
        register("TimeInOverworld", new EntityOption(AbstractPiglinEntity.class, ValueHandler.INT));

//        register("listener", new EntityOption(AllayEntity.class));
        register("DuplicationCooldown", new EntityOption(AllayEntity.class, ValueHandler.LONG));
        register("CanDuplicate", new EntityOption(AllayEntity.class, ValueHandler.BOOLEAN));

        register("AngerTime", new EntityOption(Angerable.class, ValueHandler.INT));
        register("AngryAt", new EntityOption(Angerable.class, ValueHandler.UUID));

        register("InLove", new EntityOption(AnimalEntity.class, ValueHandler.INT));
        register("LoveCause", new EntityOption(AnimalEntity.class, ValueHandler.UUID));

        register("Age", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.INT));
        register("Duration", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.INT));
        register("WaitTime", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.INT));
        register("ReapplicationDelay", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.INT));
        register("DurationOnUse", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.INT));
        register("RadiusOnUse", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.FLOAT));
        register("RadiusPerTick", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.FLOAT));
        register("Radius", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.FLOAT));
        register("Owner", new EntityOption(AreaEffectCloudEntity.class, ValueHandler.UUID));
        register("Particle", new EntityOption(AreaEffectCloudEntity.class, parser -> new RegistryParser<>(parser, Registries.PARTICLE_TYPE).parse()));
//        register("potion_contents", new EntityOption(AreaEffectCloudEntity.class,

        register("state", new EntityOption(ArmadilloEntity.class, parser -> new EnumParser(parser, Arrays.stream(ArmadilloEntity.State.values()).map(ArmadilloEntity.State::asString).collect(Collectors.toSet())).parse()));
        register("scute_time", new EntityOption(ArmadilloEntity.class, ValueHandler.INT));

//        register("ArmorItems", new EntityOption(ArmorStandEntity.class));
//        register("HandItems", new EntityOption(ArmorStandEntity.class));
        register("Invisible", new EntityOption(ArmorStandEntity.class, ValueHandler.BOOLEAN));
        register("Small", new EntityOption(ArmorStandEntity.class, ValueHandler.BOOLEAN));
        register("ShowArms", new EntityOption(ArmorStandEntity.class, ValueHandler.BOOLEAN));
        register("DisabledSlots", new EntityOption(ArmorStandEntity.class, ValueHandler.INT)); // todo slot ui
        register("NoBasePlate", new EntityOption(ArmorStandEntity.class, ValueHandler.BOOLEAN));
        register("Marker", new EntityOption(ArmorStandEntity.class, ValueHandler.BOOLEAN));
//        register("Pose", new EntityOption(ArmorStandEntity.class, ));

        register("Variant", new EntityOption(AxolotlEntity.class, parser -> new EnumParser(parser, Arrays.stream(AxolotlEntity.Variant.values()).map(AxolotlEntity.Variant::asString).collect(Collectors.toSet())).parse()));
        register("FromBucket", new EntityOption(AxolotlEntity.class, ValueHandler.BOOLEAN));

        register("BatFlags", new EntityOption(BatEntity.class, ValueHandler.BYTE)); // todo ui; bit 0: roosting

        register("hive_pos", new EntityOption(BeeEntity.class, ValueHandler.BLOCK_POS));
        register("flower_pos", new EntityOption(BeeEntity.class, ValueHandler.BLOCK_POS));
        register("HasNectar", new EntityOption(BeeEntity.class, ValueHandler.BOOLEAN));
        register("HasStung", new EntityOption(BeeEntity.class, ValueHandler.BOOLEAN));
        register("TicksSincePollination", new EntityOption(BeeEntity.class, ValueHandler.INT));
        register("CannotEnterHiveTicks", new EntityOption(BeeEntity.class, ValueHandler.INT));
        register("CropsGrownSincePollination", new EntityOption(BeeEntity.class, ValueHandler.INT));

//        register("block_state", new EntityOption(DisplayEntity.BlockDisplayEntity.class, ));

        register("Type", new EntityOption(BoatEntity.class, parser -> new EnumParser(parser, Arrays.stream(BoatEntity.Type.values()).map(BoatEntity.Type::asString).collect(Collectors.toSet())).parse()));

        register("sheared", new EntityOption(BoggedEntity.class, ValueHandler.BOOLEAN));

        register("LastPoseTick", new EntityOption(CamelEntity.class, ValueHandler.LONG));

        register("variant", new EntityOption(CatEntity.class, parser -> new RegistryParser<>(parser, Registries.CAT_VARIANT).parse()));
        register("CollarColor", new EntityOption(CatEntity.class, parser -> new EnumParser(parser, Arrays.stream(DyeColor.values()).map(DyeColor::asString).collect(Collectors.toSet())).parse()));

        register("IsChickenJockey", new EntityOption(ChickenEntity.class, ValueHandler.BOOLEAN));
        register("EggLayTime", new EntityOption(ChickenEntity.class, ValueHandler.INT));

        register("Command", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.STRING));
        register("SuccessCount", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.INT));
//        register("CustomName", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.JSON));
        register("TrackOutput", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.BOOLEAN));
//        register("LastOutput", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.JSON));
        register("UpdateLastExecution", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.BOOLEAN));
        register("LastExecution", new EntityOption(CommandBlockMinecartEntity.class, ValueHandler.LONG));

        register("powered", new EntityOption(CreeperEntity.class, ValueHandler.BOOLEAN));
        register("Fuse", new EntityOption(CreeperEntity.class, ValueHandler.SHORT));
        register("ExplosionRadius", new EntityOption(CreeperEntity.class, ValueHandler.BYTE));
        register("ignited", new EntityOption(CreeperEntity.class, ValueHandler.BOOLEAN));

//        register("transformation", new EntityOption(DisplayEntity.class, ));
        register("interpolation_duration", new EntityOption(DisplayEntity.class, ValueHandler.INT));
        register("start_interpolation", new EntityOption(DisplayEntity.class, ValueHandler.INT));
        register("teleport_duration", new EntityOption(DisplayEntity.class, ValueHandler.INT));
        register("billboard", new EntityOption(DisplayEntity.class, parser -> new EnumParser(parser, Arrays.stream(DisplayEntity.BillboardMode.values()).map(DisplayEntity.BillboardMode::asString).collect(Collectors.toSet())).parse()));
        register("view_range", new EntityOption(DisplayEntity.class, ValueHandler.FLOAT));
        register("shadow_radius", new EntityOption(DisplayEntity.class, ValueHandler.FLOAT));
        register("shadow_strength", new EntityOption(DisplayEntity.class, ValueHandler.FLOAT));
        register("width", new EntityOption(DisplayEntity.class, ValueHandler.FLOAT));
        register("height", new EntityOption(DisplayEntity.class, ValueHandler.FLOAT));
        register("glow_color_override", new EntityOption(DisplayEntity.class, ValueHandler.INT)); // todo ui; color
//        register("brightness", new EntityOption(DisplayEntity.class, ));

        register("TreasurePosX", new EntityOption(DolphinEntity.class, ValueHandler.INT));
        register("TreasurePosY", new EntityOption(DolphinEntity.class, ValueHandler.INT));
        register("TreasurePosZ", new EntityOption(DolphinEntity.class, ValueHandler.INT));
        register("GotFish", new EntityOption(DolphinEntity.class, ValueHandler.BOOLEAN));
        register("Moistness", new EntityOption(DolphinEntity.class, ValueHandler.INT));

        register("beam_target", new EntityOption(EndCrystalEntity.class, ValueHandler.BLOCK_POS));
        register("ShowBottom", new EntityOption(EndCrystalEntity.class, ValueHandler.BOOLEAN));

        register("DragonPhase", new EntityOption(EnderDragonEntity.class, ValueHandler.INT));
        register("DragonDeathTime", new EntityOption(EnderDragonEntity.class, ValueHandler.INT));

//        register("carriedBlockState", new EntityOption(EndermanEntity.class, ))

        register("Lifetime", new EntityOption(EndermiteEntity.class, ValueHandler.INT));

        register("Warmup", new EntityOption(EvokerFangsEntity.class, ValueHandler.INT));
        register("Owner", new EntityOption(EvokerFangsEntity.class, ValueHandler.UUID));

        register("Health", new EntityOption(ExperienceOrbEntity.class, ValueHandler.SHORT));
        register("Age", new EntityOption(ExperienceOrbEntity.class, ValueHandler.SHORT));
        register("Value", new EntityOption(ExperienceOrbEntity.class, ValueHandler.SHORT));
        register("Count", new EntityOption(ExperienceOrbEntity.class, ValueHandler.INT));

        register("power", new EntityOption(ExplosiveProjectileEntity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()));

//        register("Item", new EntityOption(EyeOfEnderEntity.class, ))

//        register("BlockState", new EntityOption(FallingBlockEntity.class, ))
        register("Time", new EntityOption(FallingBlockEntity.class, ValueHandler.INT));
        register("HurtEntities", new EntityOption(FallingBlockEntity.class, ValueHandler.BOOLEAN));
        register("FallHurtAmount", new EntityOption(FallingBlockEntity.class, ValueHandler.FLOAT));
        register("FallHurtMax", new EntityOption(FallingBlockEntity.class, ValueHandler.INT));
        register("DropItem", new EntityOption(FallingBlockEntity.class, ValueHandler.BOOLEAN));
//        register("TileEntityData", new EntityOption(FallingBlockEntity.class, ValueHandler.));
        register("CancelDrop", new EntityOption(FallingBlockEntity.class, ValueHandler.BOOLEAN));

        register("ExplosionPower", new EntityOption(FireballEntity.class, ValueHandler.BYTE));

        register("Life", new EntityOption(FireworkRocketEntity.class, ValueHandler.INT));
        register("LifeTime", new EntityOption(FireworkRocketEntity.class, ValueHandler.INT));
//        register("FireworksItem", new EntityOption(FireworkRocketEntity.class, ));
        register("ShotAtAngle", new EntityOption(FireworkRocketEntity.class, ValueHandler.BOOLEAN));

        register("FromBucket", new EntityOption(FishEntity.class, ValueHandler.BOOLEAN));

        register("Trusted", new EntityOption(FoxEntity.class, parser -> new ListParser(parser, new UUIDParser(parser), NumberRange.IntRange.ANY).parse()));
        register("Sleeping", new EntityOption(FoxEntity.class, ValueHandler.BOOLEAN));
        register("Type", new EntityOption(FoxEntity.class, parser -> new EnumParser(parser, Arrays.stream(FoxEntity.Type.values()).map(FoxEntity.Type::asString).collect(Collectors.toSet())).parse()));
        register("Sitting", new EntityOption(FoxEntity.class, ValueHandler.BOOLEAN));
        register("Crouching", new EntityOption(FoxEntity.class, ValueHandler.BOOLEAN));

        register("variant", new EntityOption(FrogEntity.class, parser -> new RegistryParser<>(parser, Registries.FROG_VARIANT).parse()));

        register("PushX", new EntityOption(FurnaceMinecartEntity.class, ValueHandler.DOUBLE));
        register("PushZ", new EntityOption(FurnaceMinecartEntity.class, ValueHandler.DOUBLE));
        register("Fuel", new EntityOption(FurnaceMinecartEntity.class, ValueHandler.SHORT));

        register("ExplosionPower", new EntityOption(GhastEntity.class, ValueHandler.BYTE));

        register("DarkTicksRemaining", new EntityOption(GlowSquidEntity.class, ValueHandler.INT));

        register("IsScreamingGoat", new EntityOption(GoatEntity.class, ValueHandler.BOOLEAN));
        register("HasLeftHorn", new EntityOption(GoatEntity.class, ValueHandler.BOOLEAN));
        register("HasRightHorn", new EntityOption(GoatEntity.class, ValueHandler.BOOLEAN));

//        register("Item", new EntityOption(ItemFrameEntity.class, parser -> new AnyParser(parser).parse()));
        register("ItemRotation", new EntityOption(ItemFrameEntity.class, ValueHandler.BYTE));
        register("ItemDropChance", new EntityOption(ItemFrameEntity.class, ValueHandler.FLOAT));
        register("Facing", new EntityOption(ItemFrameEntity.class, ValueHandler.BYTE));
        register("Invisible", new EntityOption(ItemFrameEntity.class, ValueHandler.BOOLEAN));
        register("Fixed", new EntityOption(ItemFrameEntity.class, ValueHandler.BOOLEAN));

        register("facing", new EntityOption(PaintingEntity.class, ValueHandler.BYTE));
        register("variant", new EntityOption(PaintingEntity.class, parser -> new RegistryParser<>(parser, Registries.PAINTING_VARIANT).parse()));

//        register("Inventory", new EntityOption(InventoryOwner.class, parser -> new AnyParser(parser).parse()));
    }

    Class<? extends Entity> getEntityClassById(String registryId) {
        String prefixed = registryId.contains(":") ? registryId : ("minecraft:" + registryId);
        return ENTITY_REGISTRY_ID_2_CLASS.getOrDefault(prefixed, Entity.class);
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public void setEntityType(String registryId) {
        this.entityClass = getEntityClassById(registryId);
    }

    final class EntityOption extends Option {
        final Class<?> requiresClass;
        final Predicate<EntityParser> predicate;

        EntityOption(Class<?> requiresClass, ValueHandler valueHandler) {
            this(requiresClass, valueHandler, ignored -> true);
        }

        EntityOption(Class<?> requiresClass, ValueHandler valueHandler, Predicate<EntityParser> predicate) {
            super(valueHandler);
            this.requiresClass = requiresClass;
            this.predicate = predicate;
        }

        @Override
        public boolean shouldSuggest() {
            if (!predicate.test(EntityParser.this)) return false;
            Class<?> entityClass = EntityParser.this.getEntityClass();
            while (!entityClass.equals(Object.class)) {
                if (entityClass.equals(requiresClass)) {
                    return true;
                }
                entityClass = entityClass.getSuperclass();
            }
            for (var iface : entityClass.getInterfaces()) {
                if (requiresClass.equals(iface)) {
                    return true;
                }
            }
            return false;
        }
    }
}
