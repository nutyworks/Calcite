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
import works.nuty.calcite.parser.primitive.*;

import java.util.Map;
import java.util.function.Predicate;

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
        register("id", new EntityOption(Entity.class, parser -> new EntityTypeParser(parser).parse(), parser -> parser.getEntityClass().equals(Entity.class)));

        register("Pos", new EntityOption(Entity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()));
        register("Motion", new EntityOption(Entity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()));
        register("Rotation", new EntityOption(Entity.class, parser -> new ListParser(parser, new FloatParser(parser), NumberRange.IntRange.exactly(2)).parse()));
        register("FallDistance", new EntityOption(Entity.class, parser -> new FloatParser(parser).parse()));
        register("Fire", new EntityOption(Entity.class, parser -> new ShortParser(parser).parse()));
        register("Air", new EntityOption(Entity.class, parser -> new ShortParser(parser).parse()));
        register("OnGround", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("Invulnerable", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("PortalCooldown", new EntityOption(Entity.class, parser -> new IntParser(parser).parse()));
        register("UUID", new EntityOption(Entity.class, parser -> new UUIDParser(parser).parse()));
        register("CustomName", new EntityOption(Entity.class, parser -> new StringParser(parser).parse()));
        register("CustomNameVisible", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("Silent", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("NoGravity", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("Glowing", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("TicksFrozen", new EntityOption(Entity.class, parser -> new IntParser(parser).parse()));
        register("HasVisualFire", new EntityOption(Entity.class, parser -> new BooleanParser(parser).parse()));
        register("Tags", new EntityOption(Entity.class, parser -> new ListParser(parser, new StringParser(parser), NumberRange.IntRange.atMost(1024)).parse()));
        register("Passengers", new EntityOption(Entity.class, parser -> new ListParser(parser, new EntityParser(parser), NumberRange.IntRange.ANY).parse()));

        register("TileX", new EntityOption(AbstractDecorationEntity.class, parser -> new IntParser(parser).parse(), parser -> !parser.getEntityClass().equals(LeashKnotEntity.class)));
        register("TileY", new EntityOption(AbstractDecorationEntity.class, parser -> new IntParser(parser).parse(), parser -> !parser.getEntityClass().equals(LeashKnotEntity.class)));
        register("TileZ", new EntityOption(AbstractDecorationEntity.class, parser -> new IntParser(parser).parse(), parser -> !parser.getEntityClass().equals(LeashKnotEntity.class)));

        register("Item", new EntityOption(ItemFrameEntity.class, parser -> new AnyParser(parser).parse())); // todo item parser
        register("ItemRotation", new EntityOption(ItemFrameEntity.class, parser -> new ByteParser(parser).parse()));
        register("ItemDropChance", new EntityOption(ItemFrameEntity.class, parser -> new FloatParser(parser).parse()));
        register("Facing", new EntityOption(ItemFrameEntity.class, parser -> new ByteParser(parser).parse()));
        register("Invisible", new EntityOption(ItemFrameEntity.class, parser -> new BooleanParser(parser).parse()));
        register("Fixed", new EntityOption(ItemFrameEntity.class, parser -> new BooleanParser(parser).parse()));

        register("facing", new EntityOption(PaintingEntity.class, parser -> new ByteParser(parser).parse()));
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

    final class EntityOption implements Option {
        final Class<?> requiresClass;
        final ValueHandler valueHandler;
        final Predicate<EntityParser> predicate;

        EntityOption(Class<? extends Entity> requiresClass, ValueHandler valueHandler) {
            this(requiresClass, valueHandler, ignored -> true);
        }

        EntityOption(Class<?> requiresClass, ValueHandler valueHandler, Predicate<EntityParser> predicate) {
            this.requiresClass = requiresClass;
            this.valueHandler = valueHandler;
            this.predicate = predicate;
        }

        public boolean shouldSuggest() {
            if (!predicate.test(EntityParser.this)) return false;
            Class<?> entityClass = EntityParser.this.getEntityClass();
            while (!entityClass.equals(Object.class)) {
                if (entityClass.equals(requiresClass)) {
                    return true;
                }
                entityClass = entityClass.getSuperclass();
            }
            return false;
        }

        @Override
        public ValueHandler getValueHandler() {
            return valueHandler;
        }
    }
}
