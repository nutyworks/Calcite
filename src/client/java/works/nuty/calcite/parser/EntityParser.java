package works.nuty.calcite.parser;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
import net.minecraft.text.Text;
import works.nuty.calcite.parser.options.EntityOptions;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EntityParser extends DefaultParser {
    public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.value"));
    private final static Map<String, Class<?>> ENTITY_REGISTRY_ID_2_CLASS = ImmutableMap.<String, Class<?>>builder()
        .put("minecraft:allay", AllayEntity.class)
        .put("minecraft:area_effect_cloud", AreaEffectCloudEntity.class)
        .put("minecraft:armadillo", ArmadilloEntity.class)
        .put("minecraft:armor_stand", ArmorStandEntity.class)
        .put("minecraft:arrow", ArrowEntity.class)
        .put("minecraft:axolotl", AxolotlEntity.class)
        .put("minecraft:bat", BatEntity.class)
        .put("minecraft:bee", BeeEntity.class)
        .put("minecraft:blaze", BlazeEntity.class)
        .put("minecraft:block_display", DisplayEntity.BlockDisplayEntity.class)
        .put("minecraft:boat", BoatEntity.class)
        .put("minecraft:bogged", BoggedEntity.class)
        .put("minecraft:breeze", BreezeEntity.class)
        .put("minecraft:breeze_wind_charge", BreezeWindChargeEntity.class)
        .put("minecraft:camel", CamelEntity.class)
        .put("minecraft:cat", CatEntity.class)
        .put("minecraft:cave_spider", CaveSpiderEntity.class)
        .put("minecraft:chest_boat", ChestBoatEntity.class)
        .put("minecraft:chest_minecart", ChestMinecartEntity.class)
        .put("minecraft:chicken", ChickenEntity.class)
        .put("minecraft:cod", CodEntity.class)
        .put("minecraft:command_block_minecart", CommandBlockMinecartEntity.class)
        .put("minecraft:cow", CowEntity.class)
        .put("minecraft:creeper", CreeperEntity.class)
        .put("minecraft:dolphin", DolphinEntity.class)
        .put("minecraft:donkey", DonkeyEntity.class)
        .put("minecraft:dragon_fireball", DragonFireballEntity.class)
        .put("minecraft:drowned", DrownedEntity.class)
        .put("minecraft:egg", EggEntity.class)
        .put("minecraft:elder_guardian", ElderGuardianEntity.class)
        .put("minecraft:end_crystal", EndCrystalEntity.class)
        .put("minecraft:ender_dragon", EnderDragonEntity.class)
        .put("minecraft:ender_pearl", EnderPearlEntity.class)
        .put("minecraft:enderman", EndermanEntity.class)
        .put("minecraft:endermite", EndermiteEntity.class)
        .put("minecraft:evoker", EvokerEntity.class)
        .put("minecraft:evoker_fangs", EvokerFangsEntity.class)
        .put("minecraft:experience_bottle", ExperienceBottleEntity.class)
        .put("minecraft:experience_orb", ExperienceOrbEntity.class)
        .put("minecraft:eye_of_ender", EyeOfEnderEntity.class)
        .put("minecraft:falling_block", FallingBlockEntity.class)
        .put("minecraft:firework_rocket", FireworkRocketEntity.class)
        .put("minecraft:fox", FoxEntity.class)
        .put("minecraft:frog", FrogEntity.class)
        .put("minecraft:furnace_minecart", FurnaceMinecartEntity.class)
        .put("minecraft:ghast", GhastEntity.class)
        .put("minecraft:giant", GiantEntity.class)
        .put("minecraft:glow_item_frame", GlowItemFrameEntity.class)
        .put("minecraft:glow_squid", GlowSquidEntity.class)
        .put("minecraft:goat", GoatEntity.class)
        .put("minecraft:guardian", GuardianEntity.class)
        .put("minecraft:hoglin", HoglinEntity.class)
        .put("minecraft:hopper_minecart", HopperMinecartEntity.class)
        .put("minecraft:horse", HorseEntity.class)
        .put("minecraft:husk", HuskEntity.class)
        .put("minecraft:illusioner", IllusionerEntity.class)
        .put("minecraft:interaction", InteractionEntity.class)
        .put("minecraft:iron_golem", IronGolemEntity.class)
        .put("minecraft:item", ItemEntity.class)
        .put("minecraft:item_display", DisplayEntity.ItemDisplayEntity.class)
        .put("minecraft:item_frame", ItemFrameEntity.class)
        .put("minecraft:fireball", FireballEntity.class)
        .put("minecraft:leash_knot", LeashKnotEntity.class)
        .put("minecraft:lightning_bolt", LightningEntity.class)
        .put("minecraft:llama", LlamaEntity.class)
        .put("minecraft:llama_spit", LlamaSpitEntity.class)
        .put("minecraft:magma_cube", MagmaCubeEntity.class)
        .put("minecraft:marker", MarkerEntity.class)
        .put("minecraft:minecart", MinecartEntity.class)
        .put("minecraft:mooshroom", MooshroomEntity.class)
        .put("minecraft:mule", MuleEntity.class)
        .put("minecraft:ocelot", OcelotEntity.class)
        .put("minecraft:painting", PaintingEntity.class)
        .put("minecraft:panda", PandaEntity.class)
        .put("minecraft:parrot", ParrotEntity.class)
        .put("minecraft:phantom", PhantomEntity.class)
        .put("minecraft:pig", PigEntity.class)
        .put("minecraft:piglin", PiglinEntity.class)
        .put("minecraft:piglin_brute", PiglinBruteEntity.class)
        .put("minecraft:pillager", PillagerEntity.class)
        .put("minecraft:polar_bear", PolarBearEntity.class)
        .put("minecraft:potion", PotionEntity.class)
        .put("minecraft:pufferfish", PufferfishEntity.class)
        .put("minecraft:rabbit", RabbitEntity.class)
        .put("minecraft:ravager", RavagerEntity.class)
        .put("minecraft:salmon", SalmonEntity.class)
        .put("minecraft:sheep", SheepEntity.class)
        .put("minecraft:shulker", ShulkerEntity.class)
        .put("minecraft:shulker_bullet", ShulkerBulletEntity.class)
        .put("minecraft:silverfish", SilverfishEntity.class)
        .put("minecraft:skeleton", SkeletonEntity.class)
        .put("minecraft:skeleton_horse", SkeletonHorseEntity.class)
        .put("minecraft:slime", SlimeEntity.class)
        .put("minecraft:small_fireball", SmallFireballEntity.class)
        .put("minecraft:sniffer", SnifferEntity.class)
        .put("minecraft:snow_golem", SnowGolemEntity.class)
        .put("minecraft:snowball", SnowballEntity.class)
        .put("minecraft:spawner_minecart", SpawnerMinecartEntity.class)
        .put("minecraft:spectral_arrow", SpectralArrowEntity.class)
        .put("minecraft:spider", SpiderEntity.class)
        .put("minecraft:squid", SquidEntity.class)
        .put("minecraft:stray", StrayEntity.class)
        .put("minecraft:strider", StriderEntity.class)
        .put("minecraft:tadpole", TadpoleEntity.class)
        .put("minecraft:text_display", DisplayEntity.TextDisplayEntity.class)
        .put("minecraft:tnt", TntEntity.class)
        .put("minecraft:tnt_minecart", TntMinecartEntity.class)
        .put("minecraft:trader_llama", TraderLlamaEntity.class)
        .put("minecraft:trident", TridentEntity.class)
        .put("minecraft:tropical_fish", TropicalFishEntity.class)
        .put("minecraft:turtle", TurtleEntity.class)
        .put("minecraft:vex", VexEntity.class)
        .put("minecraft:villager", VillagerEntity.class)
        .put("minecraft:vindicator", VindicatorEntity.class)
        .put("minecraft:wandering_trader", WanderingTraderEntity.class)
        .put("minecraft:warden", WardenEntity.class)
        .put("minecraft:wind_charge", WindChargeEntity.class)
        .put("minecraft:witch", WitchEntity.class)
        .put("minecraft:wither", WitherEntity.class)
        .put("minecraft:wither_skeleton", WitherSkeletonEntity.class)
        .put("minecraft:wither_skull", WitherSkullEntity.class)
        .put("minecraft:wolf", WolfEntity.class)
        .put("minecraft:zoglin", ZoglinEntity.class)
        .put("minecraft:zombie", ZombieEntity.class)
        .put("minecraft:zombie_horse", ZombieHorseEntity.class)
        .put("minecraft:zombie_villager", ZombieVillagerEntity.class)
        .put("minecraft:zombified_piglin", ZombifiedPiglinEntity.class)
        .put("minecraft:player", PlayerEntity.class)
        .put("minecraft:fishing_bobber", FishingBobberEntity.class)
        .build();
    private Class<?> entityClass;

    public EntityParser(StringReader reader) {
        this(reader, "");
        this.entityClass = Entity.class;
    }

    public EntityParser(StringReader reader, String entityRegistryId) {
        super(reader);
        this.entityClass = getEntityClass(entityRegistryId);
    }

    Class<?> getEntityClass(String registryId) {
        String prefixed = registryId.startsWith("minecraft:") ? registryId : ("minecraft:" + registryId);
        return ENTITY_REGISTRY_ID_2_CLASS.getOrDefault(prefixed, Entity.class);
    }

    public void parse() throws CommandSyntaxException {
        this.suggest(this::suggestOpenCompound);
        this.reader().expect('{');
        this.suggest(this::suggestOptionKey);
        this.reader().skipWhitespace();
        while (this.reader().canRead() && this.reader().peek() != '}') {
            int cursor = this.reader().getCursor();
            String key = this.reader().readString();
            EntityOptions.ValueHandler handler = EntityOptions.getValueHandler(this, key, this.reader().getCursor());

            if (key.isEmpty() || this.hasPotentialOptionKey(key)) {
                this.reader().setCursor(cursor);
                throw EXPECTED_KEY.createWithContext(this.reader());
            }
            this.reader().skipWhitespace();
            cursor = this.reader().getCursor();

            if (!this.reader().canRead() || this.reader().peek() != ':') {
                this.reader().setCursor(cursor);
                this.suggest(this::suggestColon);
                throw EXPECTED_VALUE.createWithContext(this.reader());
            }
            this.reader().skip();
            this.reader().skipWhitespace();

            this.suggestNothing();
            // todo handler should be non-null
            if (handler != null) handler.handle(this);
            this.reader().skipWhitespace();
            cursor = this.reader().getCursor();

            this.suggest(this::suggestOptionsNextOrClose);
            if (!this.reader().canRead()) continue;
            if (this.reader().peek() == ',') {
                this.reader().skip();
                this.reader().skipWhitespace();
                this.suggest(this::suggestOptionKey);
                continue;
            }
            if (this.reader().peek() == ']') break;
            this.reader().setCursor(cursor);
            throw EXPECTED_KEY.createWithContext(this.reader());
        }
        this.reader().expect('}');
        this.suggestNothing();
    }

    private CompletableFuture<Suggestions> suggestOpenCompound(SuggestionsBuilder builder) {
        builder.suggest("{");
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder builder) {
        builder.suggest(",");
        builder.suggest("}");
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionKey(SuggestionsBuilder builder) {
        EntityOptions.suggestKeys(this, builder);
        return builder.buildFuture();
    }

    private boolean hasPotentialOptionKey(String key) {
        return EntityOptions.isPotentialKey(this, key);
    }

    private CompletableFuture<Suggestions> suggestColon(SuggestionsBuilder builder) {
        builder.suggest(":");
        return builder.buildFuture();
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        return super.getSuggestions().apply(builder.createOffset(this.reader().getCursor()));
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
    }
}
