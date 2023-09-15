package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

@SpirePatch2(
    clz = AbstractPlayer.class,
    method = "damage"
)
public class StackableLizardTails {
    @SpireInsertPatch(
        locator = Locator.class
    )
    public static SpireReturn<Void> Insert(AbstractPlayer __instance) {
        if (MoreStackableRelicsInitializer.enableLizardTailStacking) {
            for (AbstractRelic relic : __instance.relics) {
                if (relic instanceof LizardTail && relic.counter != -1) {
                    __instance.currentHealth = 0;
                    relic.onTrigger();
                    break;
                }
            }
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            return LineFinder.findInOrder(ctBehavior, new Matcher.TypeCastMatcher("LizardTail"));
        }
    }
}
