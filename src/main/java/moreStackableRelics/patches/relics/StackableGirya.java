package moreStackableRelics.patches.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Girya;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.LiftOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireLiftEffect;

import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.ModInitializer;

public class StackableGirya {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("Girya"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static boolean buttonAdded = false;

    public static boolean isButtonAdded(Girya girya) {
        boolean val = buttonAdded;
        if (ModInitializer.enableGiryaStacking) {
            if (girya.counter > 2)
                return true;
            buttonAdded = true;
        }
        return val;
    }

    public static void incrementAllGirya() {
        if (!ModInitializer.enableGiryaStacking)
            return;
        boolean first = true;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Girya.ID)) {
                relic.flash();
                if (first) {
                    first = false;
                } else if (relic.counter < 3) {
                    relic.counter++;
                }
                if (relic.counter > 3)
                    relic.counter = 3;
            }
    }

    public static boolean getGiryaStacking() {
        return ModInitializer.enableGiryaStacking;
    }

    @SpirePatch2(
        clz = Girya.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableGiryaStacking)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0];
        }
    }

    @SpirePatch2(
        clz = RestRoom.class,
        method = "onPlayerEntry"
    )
    public static class ResetOptionPatch {
        public static void Prefix() {
            buttonAdded = false;
        }
    }

    @SpirePatch2(
        clz = Girya.class,
        method = "addCampfireOption"
    )
    public static class OnlyOneButtonPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    if (m.getMethodName().equals("add")) {
                        try {
                            m.replace("if (!moreStackableRelics.patches.relics.StackableGirya.isButtonAdded(this)) { $_ = $proceed($$); }");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }

    @SpirePatch2(
        clz = CampfireUI.class,
        method = "initializeButtons"
    )
    public static class AddDisabledButtonPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(ArrayList<AbstractCampfireOption> ___buttons) {
            if (ModInitializer.enableGiryaStacking) {
                if (AbstractDungeon.player.hasRelic(Girya.ID)) {
                    for (AbstractCampfireOption option : ___buttons)
                        if (option instanceof LiftOption)
                            return;
                    ___buttons.add(new LiftOption(false));
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CampfireUI.class, "buttons");
                return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[3] };
            }
        }
    }

    @SpirePatch2(
        clz = CampfireLiftEffect.class,
        method = "update"
    )
    public static class TriggerAllGiryaPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    if (m.getMethodName().equals("flash")) {
                        try {
                            m.replace("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }

        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (!ModInitializer.enableGiryaStacking) {
                AbstractDungeon.player.getRelic(Girya.ID).flash();
                (AbstractDungeon.player.getRelic(Girya.ID)).counter++;
                return;
            }
            incrementAllGirya();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SoundMaster.class, "play");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
