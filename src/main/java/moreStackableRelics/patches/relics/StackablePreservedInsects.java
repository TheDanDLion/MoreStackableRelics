package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PreservedInsect;

import javassist.CtBehavior;
import moreStackableRelics.ModInitializer;

public class StackablePreservedInsects {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("PreservedInsect"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numPreservedInsects = 0;

    public static void countPreservedInsects() {
        numPreservedInsects = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(PreservedInsect.ID)) {
                numPreservedInsects++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PreservedInsect.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incPreservedInsects(AbstractRelic r) {
        numPreservedInsects++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PreservedInsect.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    private static float getModifier() {
        float multiplier = 1.0F;
        for (int i = 0; i < numPreservedInsects; i++)
            multiplier *= 0.75F;
        return multiplier;
    }

    @SpirePatch2(
        clz = PreservedInsect.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || numPreservedInsects == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + Math.round((1.0F - getModifier()) * 100.0F) + DESCRIPTIONS[1];
        }
    }

    @SpirePatch2(
        clz = PreservedInsect.class,
        method = "atBattleStart"
    )
    public static class ShrinkHpFurtherPatch {
        @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"m"}
        )
        public static void Insert(AbstractMonster m) {
            m.currentHealth = (int) (m.maxHealth * getModifier());
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "healthBarUpdatedEvent");
                return LineFinder.findInOrder(ctBehavior, methodCallMatcher);
            }
        }
    }

    @SpirePatch2(
        clz = AbstractCreature.class,
        method = "loadAnimation"
    )
    public static class AnimationPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(AbstractCreature __instance, @ByRef float[] scale) {
            if (!ModInitializer.enablePreservedInsectStacking)
                return;
            int count = -1; // scale is already accounted for first Preserved Insect so -1 will ignore first
            if (!__instance.isPlayer && (AbstractDungeon.getCurrRoom()).eliteTrigger) {
                for (AbstractRelic relic : AbstractDungeon.player.relics)
                    if (relic.relicId.equals(PreservedInsect.ID))
                        count++;
                for (int i = 0; i < count; i++)
                    scale[0] += 0.3F;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(ModHelper.class, "isModEnabled"));
            }
        }
    }
}
