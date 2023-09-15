package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TinyChest;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

@SpirePatch2(
    clz = EventHelper.class,
    method = "roll",
    paramtypez = {Random.class}
)
public class StackableTinyChests {
    @SpireInsertPatch(
        locator = Locator.class,
        localvars = {"forceChest"}
    )
    public static void Insert(@ByRef boolean[] forceChest) {
        if (MoreStackableRelicsInitializer.enableTinyChestStacking && AbstractDungeon.player != null) {
            boolean first = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(TinyChest.ID)) {
                    if (first) {
                        first = false;
                    } else {
                        relic.counter++;
                        if (relic.counter >= 4) {
                            relic.counter = 0;
                            relic.flash();
                            forceChest[0] = true;
                        }
                    }
                }
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic"));
        }
    }
}
