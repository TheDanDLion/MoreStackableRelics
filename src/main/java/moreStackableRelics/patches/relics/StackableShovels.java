package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Shovel;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.campfire.CampfireDigEffect;

import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.ModInitializer;

public class StackableShovels {

    private static final RelicStrings RELIC_STIRNGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("Shovel"));
    private static final String[] DESCRIPTIONS = RELIC_STIRNGS.DESCRIPTIONS;

    public static boolean buttonAdded = false;

    public static boolean isButtonAdded() {
        boolean val = buttonAdded;
        if (ModInitializer.enableShovelStacking)
            buttonAdded = true;
        return val;
    }

    @SpirePatch2(
        clz = Shovel.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableShovelStacking)
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
        clz = Shovel.class,
        method = "addCampfireOption"
    )
    public static class OnlyOneButtonPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    if (m.getMethodName().equals("add")) {
                        try {
                            m.replace("if (!moreStackableRelics.patches.relics.StackableShovels.isButtonAdded()) { $_ = $proceed($$); }");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }

    @SpirePatch2(
        clz = CampfireDigEffect.class,
        method = "update"
    )
    public static class TriggerAllShovelsPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (!ModInitializer.enableShovelStacking)
                return;
            boolean first = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(Shovel.ID)) {
                    if (first)
                        first = false;
                    else
                        (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(
                            AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier())));
                }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractDungeon.class, "returnRandomRelic"));
            }
        }
    }
}
