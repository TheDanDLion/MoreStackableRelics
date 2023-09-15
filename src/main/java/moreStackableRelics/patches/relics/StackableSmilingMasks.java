package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;

import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableSmilingMasks {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("SmilingMask"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numMasks = 0;

    public static void countMasks() {
        numMasks = 0;
        if (AbstractDungeon.player == null)
            return;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(SmilingMask.ID)) {
                numMasks++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(SmilingMask.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incMasks(AbstractRelic r) {
        if (AbstractDungeon.player == null || r == null)
            return;
        numMasks++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(SmilingMask.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    @SpirePatch2(
        clz = SmilingMask.class,
        method = "getUpdatedDescription"
    )
    public static class AmendSmilingMaskDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enableSmilingMaskStacking || numMasks == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + getCurrentPurgeCost() + DESCRIPTIONS[1];
        }
    }

    public static int getCurrentPurgeCost() {
        return 50 - (5 * (numMasks - 1));
    }

    @SpirePatch2(
        clz = StoreRelic.class,
        method = "purchaseRelic"
    )
    public static class RecalculatePurgeCostOnRelicPurchasePatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) {
                    try {
                        if (f.getFieldName().equals("actualPurgeCost")) {
                            f.replace("{ $_ = moreStackableRelics.patches.relics.StackableSmilingMasks.getCurrentPurgeCost(); }");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "init"
    )
    public static class RecalcluatePurgeCostOnInitPatch {
        public static void Postfix() {
            if (MoreStackableRelicsInitializer.enableSmilingMaskStacking && AbstractDungeon.player.hasRelic(SmilingMask.ID))
                ShopScreen.actualPurgeCost = getCurrentPurgeCost();
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "purgeCard"
    )
    public static class RecalculatePurgeCostOnPurgeCardPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (MoreStackableRelicsInitializer.enableSmilingMaskStacking && AbstractDungeon.player.hasRelic(SmilingMask.ID))
                ShopScreen.actualPurgeCost = getCurrentPurgeCost();
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(SmilingMask.ID))
                    relic.stopPulse();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "applyDiscount"
    )
    public static class RecalculatePurgeCostOnApplyDiscountPatch {
        public static void Postfix() {
            if (AbstractDungeon.player.hasRelic(SmilingMask.ID) && MoreStackableRelicsInitializer.enableSmilingMaskStacking)
                ShopScreen.actualPurgeCost = getCurrentPurgeCost();
        }
    }
}
