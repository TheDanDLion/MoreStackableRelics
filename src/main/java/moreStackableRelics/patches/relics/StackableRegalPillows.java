package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.RegalPillow;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableRegalPillows { // TODO: update campfire text but not really that important

    @SpirePatch2(
        clz = CampfireSleepEffect.class,
        method = SpirePatch.CONSTRUCTOR
    )
    public static class ApplyAllRegalPillowsPatch {
        public static void Postfix(@ByRef int[] ___healAmount) {
            if (!MoreStackableRelicsInitializer.enableRegalPillowStacking || AbstractDungeon.player == null)
                return;
            boolean first = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(RegalPillow.ID)) {
                    if (first)
                        first = false;
                    else
                        ___healAmount[0] += 15;
                }
        }
    }

    @SpirePatch2(
        clz = CampfireSleepEffect.class,
        method = "update"
    )
    public static class FlashAllRegalPillowsPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (!MoreStackableRelicsInitializer.enableRegalPillowStacking || AbstractDungeon.player == null)
                return;
            boolean first = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(RegalPillow.ID)) {
                    if (first)
                        first = false;
                    else
                        relic.flash();
                }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "heal"));
            }
        }
    }
}
