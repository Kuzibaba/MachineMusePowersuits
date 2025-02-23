package lehjr.powersuits.common.base;

import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.constants.MPSRegistryNames;
import lehjr.powersuits.common.item.armor.PowerArmorBoots;
import lehjr.powersuits.common.item.armor.PowerArmorChestplate;
import lehjr.powersuits.common.item.armor.PowerArmorHelmet;
import lehjr.powersuits.common.item.armor.PowerArmorLeggings;
import lehjr.powersuits.common.item.block.TinkerTableItem;
import lehjr.powersuits.common.item.module.armor.DiamondPlatingModule;
import lehjr.powersuits.common.item.module.armor.EnergyShieldModule;
import lehjr.powersuits.common.item.module.armor.IronPlatingModule;
import lehjr.powersuits.common.item.module.armor.NetheritePlatingModule;
import lehjr.powersuits.common.item.module.cosmetic.TransparentArmorModule;
import lehjr.powersuits.common.item.module.energy_generation.AdvancedSolarGeneratorModule;
import lehjr.powersuits.common.item.module.energy_generation.BasicSolarGeneratorModule;
import lehjr.powersuits.common.item.module.energy_generation.KineticGeneratorModule;
import lehjr.powersuits.common.item.module.energy_generation.ThermalGeneratorModule;
import lehjr.powersuits.common.item.module.environmental.*;
import lehjr.powersuits.common.item.module.miningenchantment.AquaAffinityModule;
import lehjr.powersuits.common.item.module.miningenchantment.FortuneModule;
import lehjr.powersuits.common.item.module.miningenchantment.SilkTouchModule;
import lehjr.powersuits.common.item.module.miningenhancement.AdvancedVeinMiner;
import lehjr.powersuits.common.item.module.miningenhancement.TunnelBoreModule;
import lehjr.powersuits.common.item.module.miningenhancement.VeinMinerModule;
import lehjr.powersuits.common.item.module.movement.*;
import lehjr.powersuits.common.item.module.special.InvisibilityModule;
import lehjr.powersuits.common.item.module.special.MagnetModule;
import lehjr.powersuits.common.item.module.special.PiglinPacificationModule;
import lehjr.powersuits.common.item.module.tool.*;
import lehjr.powersuits.common.item.module.vision.BinocularsModule;
import lehjr.powersuits.common.item.module.vision.NightVisionModule;
import lehjr.powersuits.common.item.module.weapon.*;
import lehjr.powersuits.common.item.tool.PowerFist;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = MPSConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MPSItems {
    public static MPSCreativeTab creativeTab = new MPSCreativeTab();
    /**
     * Items -------------------------------------------------------------------------------------
     */
    public static final DeferredRegister<Item> MPS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MPSConstants.MOD_ID);

    /* BlockItems --------------------------------------------------------------------------------- */
    // use directly as a module
    public static final RegistryObject<Item> TINKER_TABLE_ITEM = MPS_ITEMS.register(MPSRegistryNames.TINKER_TABLE.getPath(),
            () -> new TinkerTableItem(MPSBlocks.TINKER_TABLE_BLOCK.get()));

    public static final RegistryObject<Item> LUX_CAPACITOR_ITEM = MPS_ITEMS.register(MPSRegistryNames.LUX_CAPACITOR.getPath(),
            () -> new BlockItem(MPSBlocks.LUX_CAPACITOR_BLOCK.get(), new Item.Properties()
                    .stacksTo(64)
                    .defaultDurability(-1)
                    .tab(creativeTab)
                    .setNoRepair()));

    /* Armor -------------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> POWER_ARMOR_HELMET = MPS_ITEMS.register(MPSRegistryNames.POWER_ARMOR_HELMET.getPath(),
            PowerArmorHelmet::new);

    public static final RegistryObject<Item> POWER_ARMOR_CHESTPLATE = MPS_ITEMS.register(MPSRegistryNames.POWER_ARMOR_CHESTPLATE.getPath(),
            PowerArmorChestplate::new);

    public static final RegistryObject<Item> POWER_ARMOR_LEGGINGS = MPS_ITEMS.register(MPSRegistryNames.POWER_ARMOR_LEGGINGS.getPath(),
            PowerArmorLeggings::new);

    public static final RegistryObject<Item> POWER_ARMOR_BOOTS = MPS_ITEMS.register(MPSRegistryNames.POWER_ARMOR_BOOTS.getPath(),
            PowerArmorBoots::new);

    /* HandHeld ----------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> POWER_FIST = MPS_ITEMS.register(MPSRegistryNames.POWER_FIST.getPath(),
            PowerFist::new);

    /* Modules ------------------------------------------------------------------------------------ */
    // Armor --------------------------------------------------------------------------------------
//    public static final RegistryObject<Item> LEATHER_PLATING_MODULE = MPS_ITEMS.register(MPSRegistryNames.LEATHER_PLATING_MODULE.getPath(),
//            LeatherPlatingModule::new);
    public static final RegistryObject<Item> IRON_PLATING_MODULE = MPS_ITEMS.register(MPSRegistryNames.IRON_PLATING_MODULE.getPath(),
            IronPlatingModule::new);
    public static final RegistryObject<Item> DIAMOND_PLATING_MODULE = MPS_ITEMS.register(MPSRegistryNames.DIAMOND_PLATING_MODULE.getPath(),
            DiamondPlatingModule::new);
    public static final RegistryObject<Item> NETHERITE_PLATING_MODULE = MPS_ITEMS.register(MPSRegistryNames.NETHERITE_PLATING_MODULE.getPath(),
            NetheritePlatingModule::new);
    public static final RegistryObject<Item> ENERGY_SHIELD_MODULE = MPS_ITEMS.register(MPSRegistryNames.ENERGY_SHIELD_MODULE.getPath(),
            EnergyShieldModule::new);

    // Cosmetic -----------------------------------------------------------------------------------
    public static final RegistryObject<Item> TRANSPARENT_ARMOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.TRANSPARENT_ARMOR_MODULE.getPath(),
            TransparentArmorModule::new);

    // Energy Generation --------------------------------------------------------------------------
    public static final RegistryObject<Item> SOLAR_GENERATOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.SOLAR_GENERATOR_MODULE.getPath(),
            BasicSolarGeneratorModule::new);
    public static final RegistryObject<Item> ADVANCED_SOLAR_GENERATOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.ADVANCED_SOLAR_GENERATOR_MODULE.getPath(),
            AdvancedSolarGeneratorModule::new);
    public static final RegistryObject<Item> KINETIC_GENERATOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.KINETIC_GENERATOR_MODULE.getPath(),
            KineticGeneratorModule::new);
    public static final RegistryObject<Item> THERMAL_GENERATOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.THERMAL_GENERATOR_MODULE.getPath(),
            ThermalGeneratorModule::new);

    // Environmental ------------------------------------------------------------------------------
    public static final RegistryObject<Item> AUTO_FEEDER_MODULE = MPS_ITEMS.register(MPSRegistryNames.AUTO_FEEDER_MODULE.getPath(),
            AutoFeederModule::new);
    public static final RegistryObject<Item> COOLING_SYSTEM_MODULE = MPS_ITEMS.register(MPSRegistryNames.COOLING_SYSTEM_MODULE.getPath(),
            CoolingSystemModule::new);
    public static final RegistryObject<Item> FLUID_TANK_MODULE = MPS_ITEMS.register(MPSRegistryNames.FLUID_TANK_MODULE.getPath(),
            FluidTankModule::new);
    public static final RegistryObject<Item> MOB_REPULSOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.MOB_REPULSOR_MODULE.getPath(),
            MobRepulsorModule::new);
    public static final RegistryObject<Item> WATER_ELECTROLYZER_MODULE = MPS_ITEMS.register(MPSRegistryNames.WATER_ELECTROLYZER_MODULE.getPath(),
            WaterElectrolyzerModule::new);

    // Mining Enhancements ------------------------------------------------------------------------
//    public static final RegistryObject<Item> AOE_PICK_UPGRADE_MODULE = MPS_ITEMS.register(MPSRegistryNames.AOE_PICK_UPGRADE_MODULE.getPath(),
//            AOEPickUpgradeModule::new);
    public static final RegistryObject<Item> AQUA_AFFINITY_MODULE = MPS_ITEMS.register(MPSRegistryNames.AQUA_AFFINITY_MODULE.getPath(),
            AquaAffinityModule::new);
    public static final RegistryObject<Item> SILK_TOUCH_MODULE = MPS_ITEMS.register(MPSRegistryNames.SILK_TOUCH_MODULE.getPath(),
            SilkTouchModule::new);
    public static final RegistryObject<Item> FORTUNE_MODULE = MPS_ITEMS.register(MPSRegistryNames.FORTUNE_MODULE.getPath(),
            FortuneModule::new);
    public static final RegistryObject<Item> VEIN_MINER_MODULE = MPS_ITEMS.register(MPSRegistryNames.VEIN_MINER_MODULE.getPath(),
            VeinMinerModule::new);

    // WIP
    public static final RegistryObject<Item> TUNNEL_BORE_MODULE = MPS_ITEMS.register(MPSRegistryNames.TUNNEL_BORE_MODULE.getPath(),
            TunnelBoreModule::new);
    public static final RegistryObject<Item> AOE_PICK_UPGRADE_MODULE2 = MPS_ITEMS.register(MPSRegistryNames.AOE_PICK_UPGRADE_MODULE2.getPath(),
        AdvancedVeinMiner::new);

    // Movement -----------------------------------------------------------------------------------
    public static final RegistryObject<Item> BLINK_DRIVE_MODULE = MPS_ITEMS.register(MPSRegistryNames.BLINK_DRIVE_MODULE.getPath(),
            BlinkDriveModule::new);
    public static final RegistryObject<Item> CLIMB_ASSIST_MODULE = MPS_ITEMS.register(MPSRegistryNames.CLIMB_ASSIST_MODULE.getPath(),
            ClimbAssistModule::new);
    public static final RegistryObject<Item> DIMENSIONAL_RIFT_MODULE = MPS_ITEMS.register(MPSRegistryNames.DIMENSIONAL_RIFT_MODULE.getPath(),
            DimensionalRiftModule::new);
    public static final RegistryObject<Item> FLIGHT_CONTROL_MODULE = MPS_ITEMS.register(MPSRegistryNames.FLIGHT_CONTROL_MODULE.getPath(),
            FlightControlModule::new);
    public static final RegistryObject<Item> GLIDER_MODULE = MPS_ITEMS.register(MPSRegistryNames.GLIDER_MODULE.getPath(),
            GliderModule::new);
    public static final RegistryObject<Item> JETBOOTS_MODULE = MPS_ITEMS.register(MPSRegistryNames.JETBOOTS_MODULE.getPath(),
            JetBootsModule::new);
    public static final RegistryObject<Item> JETPACK_MODULE = MPS_ITEMS.register(MPSRegistryNames.JETPACK_MODULE.getPath(),
            JetPackModule::new);
    public static final RegistryObject<Item> JUMP_ASSIST_MODULE = MPS_ITEMS.register(MPSRegistryNames.JUMP_ASSIST_MODULE.getPath(),
            JumpAssistModule::new);;
    public static final RegistryObject<Item> PARACHUTE_MODULE = MPS_ITEMS.register(MPSRegistryNames.PARACHUTE_MODULE.getPath(),
            ParachuteModule::new);
    public static final RegistryObject<Item> SHOCK_ABSORBER_MODULE = MPS_ITEMS.register(MPSRegistryNames.SHOCK_ABSORBER_MODULE.getPath(),
            ShockAbsorberModule::new);
    public static final RegistryObject<Item> SPRINT_ASSIST_MODULE = MPS_ITEMS.register(MPSRegistryNames.SPRINT_ASSIST_MODULE.getPath(),
            SprintAssistModule::new);
    public static final RegistryObject<Item> SWIM_BOOST_MODULE = MPS_ITEMS.register(MPSRegistryNames.SWIM_BOOST_MODULE.getPath(),
            SwimAssistModule::new);

    // Special ------------------------------------------------------------------------------------
    public static final RegistryObject<Item> ACTIVE_CAMOUFLAGE_MODULE = MPS_ITEMS.register(MPSRegistryNames.ACTIVE_CAMOUFLAGE_MODULE.getPath(),
            InvisibilityModule::new);
    public static final RegistryObject<Item> MAGNET_MODULE = MPS_ITEMS.register(MPSRegistryNames.MAGNET_MODULE.getPath(),
            MagnetModule::new);
    public static final RegistryObject<Item> PIGLIN_PACIFICATION_MODULE = MPS_ITEMS.register(MPSRegistryNames.PIGLIN_PACIFICATION_MODULE.getPath(),
            PiglinPacificationModule::new);

    // Vision -------------------------------------------------------------------------------------
    public static final RegistryObject<Item> BINOCULARS_MODULE = MPS_ITEMS.register(MPSRegistryNames.BINOCULARS_MODULE.getPath(),
            BinocularsModule::new);
    public static final RegistryObject<Item> NIGHT_VISION_MODULE = MPS_ITEMS.register(MPSRegistryNames.NIGHT_VISION_MODULE.getPath(),
            NightVisionModule::new);

    // Tools --------------------------------------------------------------------------
    public static final RegistryObject<Item> AXE_MODULE = MPS_ITEMS.register(MPSRegistryNames.AXE_MODULE.getPath(),
            AxeModule::new);
    public static final RegistryObject<Item> DIAMOND_PICK_UPGRADE_MODULE = MPS_ITEMS.register(MPSRegistryNames.DIAMOND_PICK_UPGRADE_MODULE.getPath(),
            DiamondPickUpgradeModule::new);
    public static final RegistryObject<Item> FLINT_AND_STEEL_MODULE = MPS_ITEMS.register(MPSRegistryNames.FLINT_AND_STEEL_MODULE.getPath(),
            FlintAndSteelModule::new);
    public static final RegistryObject<Item> HOE_MODULE = MPS_ITEMS.register(MPSRegistryNames.HOE_MODULE.getPath(),
            HoeModule::new);
    public static final RegistryObject<Item> LEAF_BLOWER_MODULE = MPS_ITEMS.register(MPSRegistryNames.LEAF_BLOWER_MODULE.getPath(),
            LeafBlowerModule::new);
    public static final RegistryObject<Item> LUX_CAPACITOR_MODULE = MPS_ITEMS.register(MPSRegistryNames.LUX_CAPACITOR_MODULE.getPath(),
            LuxCapacitorModule::new);
    public static final RegistryObject<Item> PICKAXE_MODULE = MPS_ITEMS.register(MPSRegistryNames.PICKAXE_MODULE.getPath(),
            PickaxeModule::new);
    public static final RegistryObject<Item> SHEARS_MODULE = MPS_ITEMS.register(MPSRegistryNames.SHEARS_MODULE.getPath(),
            ShearsModule::new);
    public static final RegistryObject<Item> SHOVEL_MODULE = MPS_ITEMS.register(MPSRegistryNames.SHOVEL_MODULE.getPath(),
            ShovelModule::new);

    // Debug --------------------------------------------------------------------------------------
    // todo

    // Weapons ------------------------------------------------------------------------------------
    public static final RegistryObject<Item> BLADE_LAUNCHER_MODULE = MPS_ITEMS.register(MPSRegistryNames.BLADE_LAUNCHER_MODULE.getPath(),
            BladeLauncherModule::new);
    public static final RegistryObject<Item> LIGHTNING_MODULE = MPS_ITEMS.register(MPSRegistryNames.LIGHTNING_MODULE.getPath(),
            LightningModule::new);
    public static final RegistryObject<Item> MELEE_ASSIST_MODULE = MPS_ITEMS.register(MPSRegistryNames.MELEE_ASSIST_MODULE.getPath(),
            MeleeAssistModule::new);
    public static final RegistryObject<Item> PLASMA_CANNON_MODULE = MPS_ITEMS.register(MPSRegistryNames.PLASMA_CANNON_MODULE.getPath(),
            PlasmaCannonModule::new);
    public static final RegistryObject<Item> RAILGUN_MODULE = MPS_ITEMS.register(MPSRegistryNames.RAILGUN_MODULE.getPath(),
            RailgunModule::new);


//    @SubscribeEvent
//    public static void addCreativeTab(CreativeModeTabEvent.Register event) {
//        creativeTab = event.registerCreativeModeTab(new ResourceLocation(MPSConstants.MOD_ID, "items"),
//                builder -> builder.icon(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(MPSRegistryNames.POWER_ARMOR_HELMET)))
//                        .title(Component.translatable(MPSConstants.MOD_ID)));
//    }
//
//    @SubscribeEvent
//    public static void onPopulateTab(CreativeModeTabEvent.BuildContents event)
//    {
//        if (event.getTab() == creativeTab)
//        {
//            MPS_ITEMS.getEntries().forEach(item->event.accept(new ItemStack(item.get())));
//        }
//    }
}
