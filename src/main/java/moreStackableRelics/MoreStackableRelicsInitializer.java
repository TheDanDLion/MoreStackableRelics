package moreStackableRelics;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostCreateStartingRelicsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.RelicGetSubscriber;
import moreStackableRelics.patches.relics.RelicHooks;
import moreStackableRelics.patches.relics.StackableBoots;
import moreStackableRelics.patches.relics.StackableCalipers;
import moreStackableRelics.patches.relics.StackableDiscountRelics;
import moreStackableRelics.patches.relics.StackableDreamCatchers;
import moreStackableRelics.patches.relics.StackableOddMushrooms;
import moreStackableRelics.patches.relics.StackablePaperCranes;
import moreStackableRelics.patches.relics.StackablePaperFrogs;
import moreStackableRelics.patches.relics.StackablePeacePipes;
import moreStackableRelics.patches.relics.StackablePreservedInsects;
import moreStackableRelics.patches.relics.StackableShovels;
import moreStackableRelics.patches.relics.StackableSmilingMasks;
import moreStackableRelics.patches.relics.StackableStrangeSpoons;
import moreStackableRelics.patches.relics.StackableToriis;
import moreStackableRelics.util.IDCheckDontTouchPls;
import moreStackableRelics.util.TextureLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.relics.Calipers;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.DreamCatcher;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.relics.OddMushroom;
import com.megacrit.cardcrawl.relics.PaperCrane;
import com.megacrit.cardcrawl.relics.PaperFrog;
import com.megacrit.cardcrawl.relics.PeacePipe;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.relics.StrangeSpoon;
import com.megacrit.cardcrawl.relics.Torii;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class MoreStackableRelicsInitializer implements
    EditStringsSubscriber,
    PostCreateStartingRelicsSubscriber,
    PostInitializeSubscriber,
    RelicGetSubscriber {

    public static final Logger logger = LogManager.getLogger(MoreStackableRelicsInitializer.class.getName());
    private static String modID;

    // Mod-settings settings. This is if you want an on/off savable button
    public static Properties moreStackableRelicsProperties = new Properties();
    public static final String ENABLE_GIGA_BOOT = "gigaBoot";
    public static final String ENABLE_BOOT_STACKING = "boot";
    public static final String ENABLE_CABLE_STACKING = "cable";
    public static final String ENABLE_CALIPER_STACKING = "caliper";
    public static final String ENABLE_CHAMP_BELT_STACKING = "belt";
    public static final String ENABLE_MEMBERSHIP_CARD_STACKING = "membershipCard";
    public static final String ENABLE_COURIER_STACKING = "courier";
    public static final String ENABLE_DREAM_CATCHER_STACKING = "dreamCatcher";
    public static final String ENABLE_GIRYA_STACKING = "girya";
    public static final String ENABLE_GOLDEN_EYE_STACKING = "goldenEye";
    public static final String ENABLE_GOLDEN_IDOL_STACKING = "goldenIdol";
    public static final String ENABLE_LIZARD_TAIL_STACKING = "lizardTailStacking";
    public static final String ENABLE_MEAT_ON_THE_BONE_STACKING = "meatOnTheBone";
    public static final String ENABLE_ODD_MUSHROOM_STACKING = "oddMushroom";
    public static final String ENABLE_OMAMORI_STACKING = "omamori";
    public static final String ENABLE_PAPER_CRANE_STACKING = "paperCrane";
    public static final String ENABLE_PAPER_FROG_STACKING = "paperFrog";
    public static final String ENABLE_PEACE_PIPE_STACKING = "peacePipe";
    public static final String ENABLE_PRESERVED_INSECT_STACKING = "preservedInsect";
    public static final String ENABLE_REGAL_PILLOW_STACKING = "regalPillow";
    public static final String ENABLE_SHOVEL_STACKING = "shovel";
    public static final String ENABLE_SMILING_MASK_STACKING = "smilingMask";
    public static final String ENABLE_SNECKO_EYE_STACKING = "sneckoEye";
    public static final String ENABLE_SNECKO_SKULL_STACKING = "sneckoSkull";
    public static final String ENABLE_SINGING_BOWL_STACKING = "singingBowl";
    public static final String ENABLE_STRANGE_SPOON_STACKING = "strangeSpoon";
    public static final String ENABLE_TINY_CHEST_STACKING = "tinyChest";
    public static final String ENABLE_TORII_STACKING = "torii";
    public static final String ENABLE_WHITE_BEAST_STACKING = "whiteBeast";
    public static final String ENABLE_WING_BOOT_STACKING = "wingBoot";
    public static boolean enableGigaBoot = false;
    public static boolean enableBootStacking = true;
    public static boolean enableCableStacking = true;
    public static boolean enableCaliperStacking = true;
    public static boolean enableChampBeltStacking = true;
    public static boolean enableMemCardStacking = true;
    public static boolean enableCourierStacking = true;
    public static boolean enableDreamCatcherStacking = true;
    public static boolean enableGiryaStacking = true;
    public static boolean enableGoldenEyeStacking = true;
    public static boolean enableGoldenIdolStacking = true;
    public static boolean enableLizardTailStacking = true;
    public static boolean enableMeatOnTheBoneStacking = true;
    public static boolean enableOddMushroomStacking = true;
    public static boolean enableOmamoriStacking = true;
    public static boolean enablePaperCraneStacking = true;
    public static boolean enablePaperFrogStacking = true;
    public static boolean enablePeacePipeStacking = true;
    public static boolean enablePreservedInsectStacking = true;
    public static boolean enableRegalPillowStacking = true;
    public static boolean enableShovelStacking = true;
    public static boolean enableSmilingMaskStacking = true;
    public static boolean enableSneckoEyeStacking = true;
    public static boolean enableSneckoSkullStacking = true;
    public static boolean enableSingingBowlStacking = true;
    public static boolean enableStrangeSpoonStacking = true;
    public static boolean enableTinyChestStacking = true;
    public static boolean enableToriiStacking = true;
    public static boolean enableWhiteBeastStacking = true;
    public static boolean enableWingBootStacking = true;

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "More Stackable Relics";
    private static final String AUTHOR = "dandylion1740";
    private static final String DESCRIPTION = "Makes more relics in the game stackable.";

    // =============== INPUT TEXTURE LOCATION =================

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "moreStackableRelicsResources/images/Badge.png";

    // =============== SUBSCRIBE, INITIALIZE =================

    public MoreStackableRelicsInitializer(){
        logger.info("Subscribe to More Stackable Relics hooks");

        BaseMod.subscribe(this);
        setModID("moreStackableRelics");

        logger.info("Done subscribing");

        logger.info("Adding mod settings");
        moreStackableRelicsProperties.setProperty(ENABLE_GIGA_BOOT, "FALSE");
        moreStackableRelicsProperties.setProperty(ENABLE_BOOT_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_CABLE_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_CALIPER_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_CHAMP_BELT_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_MEMBERSHIP_CARD_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_COURIER_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_DREAM_CATCHER_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_GIRYA_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_GOLDEN_EYE_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_GOLDEN_IDOL_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_LIZARD_TAIL_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_MEAT_ON_THE_BONE_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_ODD_MUSHROOM_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_OMAMORI_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_PAPER_CRANE_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_PAPER_FROG_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_PEACE_PIPE_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_PRESERVED_INSECT_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_REGAL_PILLOW_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_SHOVEL_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_SMILING_MASK_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_SNECKO_EYE_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_SNECKO_SKULL_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_SINGING_BOWL_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_STRANGE_SPOON_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_TINY_CHEST_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_TORII_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_WHITE_BEAST_STACKING, "TRUE");
        moreStackableRelicsProperties.setProperty(ENABLE_WING_BOOT_STACKING, "TRUE");

        try {
            SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties); // ...right here
            config.load();
            enableGigaBoot = config.getBool(ENABLE_GIGA_BOOT);
            enableBootStacking = config.getBool(ENABLE_BOOT_STACKING);
            enableCableStacking = config.getBool(ENABLE_CABLE_STACKING);
            enableCaliperStacking = config.getBool(ENABLE_CALIPER_STACKING);
            enableChampBeltStacking = config.getBool(ENABLE_CHAMP_BELT_STACKING);
            enableMemCardStacking = config.getBool(ENABLE_MEMBERSHIP_CARD_STACKING);
            enableCourierStacking = config.getBool(ENABLE_COURIER_STACKING);
            enableDreamCatcherStacking = config.getBool(ENABLE_DREAM_CATCHER_STACKING);
            enableGiryaStacking = config.getBool(ENABLE_GIRYA_STACKING);
            enableGoldenEyeStacking = config.getBool(ENABLE_GOLDEN_EYE_STACKING);
            enableGoldenIdolStacking = config.getBool(ENABLE_GOLDEN_IDOL_STACKING);
            enableLizardTailStacking = config.getBool(ENABLE_LIZARD_TAIL_STACKING);
            enableMeatOnTheBoneStacking = config.getBool(ENABLE_MEAT_ON_THE_BONE_STACKING);
            enableOddMushroomStacking = config.getBool(ENABLE_ODD_MUSHROOM_STACKING);
            enableOmamoriStacking = config.getBool(ENABLE_OMAMORI_STACKING);
            enablePaperCraneStacking = config.getBool(ENABLE_PAPER_CRANE_STACKING);
            enablePaperFrogStacking = config.getBool(ENABLE_PAPER_FROG_STACKING);
            enablePeacePipeStacking = config.getBool(ENABLE_PEACE_PIPE_STACKING);
            enablePreservedInsectStacking = config.getBool(ENABLE_PRESERVED_INSECT_STACKING);
            enableRegalPillowStacking = config.getBool(ENABLE_REGAL_PILLOW_STACKING);
            enableShovelStacking = config.getBool(ENABLE_SHOVEL_STACKING);
            enableSmilingMaskStacking = config.getBool(ENABLE_SMILING_MASK_STACKING);
            enableSneckoEyeStacking = config.getBool(ENABLE_SNECKO_EYE_STACKING);
            enableSneckoSkullStacking = config.getBool(ENABLE_SNECKO_SKULL_STACKING);
            enableSingingBowlStacking = config.getBool(ENABLE_SINGING_BOWL_STACKING);
            enableStrangeSpoonStacking = config.getBool(ENABLE_STRANGE_SPOON_STACKING);
            enableTinyChestStacking = config.getBool(ENABLE_TINY_CHEST_STACKING);
            enableToriiStacking = config.getBool(ENABLE_TORII_STACKING);
            enableWhiteBeastStacking = config.getBool(ENABLE_WHITE_BEAST_STACKING);
            enableWingBootStacking = config.getBool(ENABLE_WING_BOOT_STACKING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
    }

    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP

    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = MoreStackableRelicsInitializer.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO

    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH

    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = MoreStackableRelicsInitializer.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = MoreStackableRelicsInitializer.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO

    // ====== YOU CAN EDIT AGAIN ======

    //Used by @SpireInitializer
    public static void initialize(){
        logger.info("========================= Initializing More Stackable Relics Mod. =========================");
        //This creates an instance of our classes and gets our code going after BaseMod and ModTheSpire initialize.
        new MoreStackableRelicsInitializer();
        logger.info("========================= /More Stackable Relics Mod Initialized./ =========================");
    }

    // ============== /SUBSCRIBE, INITIALIZE/ =================


    // =============== POST-INITIALIZE =================

    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        // Create the on/off buttons:
        ModLabeledToggleButton enableGigaBootButton = new ModLabeledToggleButton("Enable GIGA Boot (When dealing unblocked damage less than 5 * number of boots, damage is 5 * number of boots)",
                350.0F, 750.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableGigaBoot,
                settingsPanel,
                (label) -> {},
                (button) -> {

            enableGigaBoot = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_GIGA_BOOT, enableGigaBoot);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableBootStackingButton = new ModLabeledToggleButton("Boot Stacking",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableBootStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {

            enableBootStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_BOOT_STACKING, enableBootStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableCaliperStackingButton = new ModLabeledToggleButton("Caliper Stacking",
                350.0f, 650.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableCaliperStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {

            enableCaliperStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_CALIPER_STACKING, enableCaliperStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableChampBeltStackingButton = new ModLabeledToggleButton("Champion Belt Stacking",
                350.0F, 600.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableChampBeltStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableCaliperStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_CALIPER_STACKING, enableCaliperStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableCourierStackingButton = new ModLabeledToggleButton("Courier Stacking",
                350.0F, 550.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableCourierStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableCourierStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_COURIER_STACKING, enableCourierStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableDreamCatcherStackingButton = new ModLabeledToggleButton("Dream Catcher Stacking",
                350.0F, 500.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableDreamCatcherStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableDreamCatcherStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_DREAM_CATCHER_STACKING, enableDreamCatcherStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableGiryaStackingButton = new ModLabeledToggleButton("Girya Stacking",
                350.0F, 450.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableGiryaStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableGiryaStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_GIRYA_STACKING, enableGiryaStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableCableStackingButton = new ModLabeledToggleButton("Gold Plated Cables Stacking",
                350.0f, 400.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableCableStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {

            enableCableStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_CABLE_STACKING, enableCableStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableGoldenEyeStackingButton = new ModLabeledToggleButton("Golden Eye Stacking",
                350.0F, 350.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableGoldenEyeStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableGoldenEyeStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_GOLDEN_EYE_STACKING, enableGoldenEyeStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableGoldenIdolStackingButton = new ModLabeledToggleButton("Golden Idol Stacking",
                350.0F, 300.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableGoldenIdolStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableGoldenIdolStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_GOLDEN_IDOL_STACKING, enableGoldenIdolStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableLizardTailStackingButton = new ModLabeledToggleButton("Lizard Tail Stacking",
                350.0F, 250.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableLizardTailStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableLizardTailStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_LIZARD_TAIL_STACKING, enableLizardTailStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableMeatOnTheBoneStackingButton = new ModLabeledToggleButton("Meat on the Bone Stacking",
                350.0F, 200.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableMeatOnTheBoneStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableMeatOnTheBoneStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_MEAT_ON_THE_BONE_STACKING, enableMeatOnTheBoneStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableMemCardStackingButton = new ModLabeledToggleButton("Membership Card Stacking",
                725.0F, 700.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableMemCardStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableMemCardStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig", moreStackableRelicsProperties);
                config.setBool(ENABLE_MEMBERSHIP_CARD_STACKING, enableMemCardStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableOddMushroomStackingButton = new ModLabeledToggleButton("Odd Mushroom Stacking",
                725.0F, 650.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableOddMushroomStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableOddMushroomStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_ODD_MUSHROOM_STACKING, enableOddMushroomStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableOmamoriStackingButton = new ModLabeledToggleButton("Omamori Stacking",
                725.0F, 600.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableOmamoriStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableOmamoriStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_OMAMORI_STACKING, enableOmamoriStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enablePaperCraneStackingButton = new ModLabeledToggleButton("Paper Crane Stacking",
                725.0F, 550.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enablePaperCraneStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enablePaperCraneStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_PAPER_CRANE_STACKING, enablePaperCraneStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enablePaperFrogStackingButton = new ModLabeledToggleButton("Paper Frog Stacking",
                725.0F, 500.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enablePaperFrogStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enablePaperFrogStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_PAPER_FROG_STACKING, enablePaperFrogStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enablePeacePipeStackingButton = new ModLabeledToggleButton("Peace Pipe Stacking",
                725.0F, 450.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enablePeacePipeStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enablePeacePipeStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_PEACE_PIPE_STACKING, enablePeacePipeStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enablePreservedInsectStackingButton = new ModLabeledToggleButton("Preserved Insect Stacking",
                725.0F, 400.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enablePreservedInsectStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enablePreservedInsectStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_PRESERVED_INSECT_STACKING, enablePreservedInsectStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableRegalPillowStackingButton = new ModLabeledToggleButton("Regal Pillow Stacking",
                725.0F, 350.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableRegalPillowStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableRegalPillowStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_REGAL_PILLOW_STACKING, enableRegalPillowStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableShovelStackingButton = new ModLabeledToggleButton("Shovel Stacking",
                725.0F, 300.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableShovelStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableShovelStacking = button.enabled;
            StackableShovels.buttonAdded = false;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_SHOVEL_STACKING, enableShovelStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableSingingBowlStackingButton = new ModLabeledToggleButton("Singing Bowl Stacking",
                725.0F, 250.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableSingingBowlStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableSingingBowlStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_SINGING_BOWL_STACKING, enableSingingBowlStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableSmilingMaskStackingButton = new ModLabeledToggleButton("Smiling Mask Stacking",
                725.0F, 200.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableSmilingMaskStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableSmilingMaskStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_SMILING_MASK_STACKING, enableSmilingMaskStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableSneckoEyeStackingButton = new ModLabeledToggleButton("Snecko Eye Stacking",
                1100.0F, 700.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableSneckoEyeStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableSneckoEyeStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_SNECKO_EYE_STACKING, enableSneckoEyeStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableSneckoSkullStackingButton = new ModLabeledToggleButton("Snecko Skull Stacking",
                1100.0F, 650.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableSneckoSkullStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableSneckoSkullStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_SNECKO_SKULL_STACKING, enableSneckoSkullStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableStrangeSpoonStackingButton = new ModLabeledToggleButton("Strange Spoon Stacking",
                1100.0F, 600.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableStrangeSpoonStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableStrangeSpoonStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_STRANGE_SPOON_STACKING, enableStrangeSpoonStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableTinyChestStackingButton = new ModLabeledToggleButton("Tiny Chest Stacking",
                1100.0F, 550.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableTinyChestStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableTinyChestStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_TINY_CHEST_STACKING, enableTinyChestStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableToriiStackingButton = new ModLabeledToggleButton("Torii Stacking",
                1100.0F, 500.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableToriiStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableToriiStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_TORII_STACKING, enableToriiStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableWhiteBeastStackingButton = new ModLabeledToggleButton("White Beast Stacking",
                1100.0F, 450.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableWhiteBeastStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableWhiteBeastStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                    moreStackableRelicsProperties);
                config.setBool(ENABLE_WHITE_BEAST_STACKING, enableWhiteBeastStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ModLabeledToggleButton enableWingBootStackingButton = new ModLabeledToggleButton("Winged Boots Stacking",
                1100.0F, 400.0F, Settings.CREAM_COLOR, FontHelper.charDescFont,
                enableWingBootStacking,
                settingsPanel,
                (label) -> {},
                (button) -> {
            enableWingBootStacking = button.enabled;
            try {
                SpireConfig config = new SpireConfig("moreStackableRelics", "moreStackableRelicsConfig",
                        moreStackableRelicsProperties);
                config.setBool(ENABLE_WING_BOOT_STACKING, enableWingBootStacking);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        settingsPanel.addUIElement(enableGigaBootButton);
        settingsPanel.addUIElement(enableBootStackingButton);
        settingsPanel.addUIElement(enableCableStackingButton);
        settingsPanel.addUIElement(enableCaliperStackingButton);
        settingsPanel.addUIElement(enableChampBeltStackingButton);
        settingsPanel.addUIElement(enableMemCardStackingButton);
        settingsPanel.addUIElement(enableCourierStackingButton);
        settingsPanel.addUIElement(enableDreamCatcherStackingButton);
        settingsPanel.addUIElement(enableGiryaStackingButton);
        settingsPanel.addUIElement(enableGoldenEyeStackingButton);
        settingsPanel.addUIElement(enableGoldenIdolStackingButton);
        settingsPanel.addUIElement(enableLizardTailStackingButton);
        settingsPanel.addUIElement(enableMeatOnTheBoneStackingButton);
        settingsPanel.addUIElement(enableOddMushroomStackingButton);
        settingsPanel.addUIElement(enableOmamoriStackingButton);
        settingsPanel.addUIElement(enablePaperCraneStackingButton);
        settingsPanel.addUIElement(enablePaperFrogStackingButton);
        settingsPanel.addUIElement(enablePeacePipeStackingButton);
        settingsPanel.addUIElement(enablePreservedInsectStackingButton);
        settingsPanel.addUIElement(enableRegalPillowStackingButton);
        settingsPanel.addUIElement(enableShovelStackingButton);
        settingsPanel.addUIElement(enableSingingBowlStackingButton);
        settingsPanel.addUIElement(enableSmilingMaskStackingButton);
        settingsPanel.addUIElement(enableSneckoEyeStackingButton);
        settingsPanel.addUIElement(enableSneckoSkullStackingButton);
        settingsPanel.addUIElement(enableStrangeSpoonStackingButton);
        settingsPanel.addUIElement(enableTinyChestStackingButton);
        settingsPanel.addUIElement(enableToriiStackingButton);
        settingsPanel.addUIElement(enableWhiteBeastStackingButton);
        settingsPanel.addUIElement(enableWingBootStackingButton);

        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        logger.info("Done loading badge Image and mod options");
    }

    // =============== /POST-INITIALIZE/ =================


    // =============== RELIC MGMT =================

    @Override
    public void receiveRelicGet(AbstractRelic relic) {
        if (RelicHooks.losingRelic)
            return;
        if (relic.relicId.equals(Boot.ID))
            StackableBoots.incBoots(relic);
        else if (relic.relicId.equals(Torii.ID))
            StackableToriis.incToriis(relic);
        else if (relic.relicId.equals(Calipers.ID))
            StackableCalipers.incCalipers(relic);
        else if (relic.relicId.equals(PaperFrog.ID))
            StackablePaperFrogs.incFrogs(relic);
        else if (relic.relicId.equals(PeacePipe.ID))
            StackablePeacePipes.numPeacePipes++;
        else if (relic.relicId.equals(PaperCrane.ID))
            StackablePaperCranes.incCranes(relic);
        else if (relic.relicId.equals(StrangeSpoon.ID))
            StackableStrangeSpoons.incSpoons(relic);
        else if (relic.relicId.equals(OddMushroom.ID))
            StackableOddMushrooms.incMushrooms(relic);
        else if (relic.relicId.equals(MembershipCard.ID)) {
            StackableDiscountRelics.incMembershipCards(relic);
            StackableDiscountRelics.countCouriers();
        }
        else if (relic.relicId.equals(Courier.ID)) {
            StackableDiscountRelics.incCouriers(relic);
            StackableDiscountRelics.countMembershipCards();
        }
        else if (relic.relicId.equals(DreamCatcher.ID))
            StackableDreamCatchers.numDreamCatchers++;
        else if (relic.relicId.equals(PreservedInsect.ID))
            StackablePreservedInsects.incPreservedInsects(relic);
        else if (relic.relicId.equals(SmilingMask.ID))
            StackableSmilingMasks.incMasks(relic);
    }

    @Override
    public void receivePostCreateStartingRelics(PlayerClass arg0, ArrayList<String> arg1) {
        StackableBoots.numBoots = 0;
        StackableCalipers.numCalipers = 0;
        StackableToriis.numToriis = 0;
        StackablePaperFrogs.numFrogs = 0;
        StackablePeacePipes.numPeacePipes = 0;
        StackablePaperCranes.numCranes = 0;
        StackableStrangeSpoons.numStrangeSpoons = 0;
        StackableOddMushrooms.numMushrooms = 0;
        StackableDreamCatchers.numDreamCatchers = 0;
        StackableDiscountRelics.numMembershipCard = 0;
        StackableDiscountRelics.numCourier = 0;
        StackablePreservedInsects.numPreservedInsects = 0;
        StackableSmilingMasks.numMasks = 0;
        StackableShovels.buttonAdded = false;
    }
    // =============== /RELIC MGMT/ =================


    // ================ LOAD THE TEXT ===================

    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());
        pathCheck();
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                getModID() + "Resources/localization/eng/MoreStackableRelics-Relic-Strings.json");
        logger.info("Done editing strings");
    }


    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }
}
