package moreStackableRelics.patches.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WhiteBeast;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableWhiteBeasts {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("WhiteBeast"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    @SpirePatch2(
        clz = WhiteBeast.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enableWhiteBeastStacking)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0];
        }
    }

    @SpirePatch2(
        clz = AbstractRoom.class,
        method = "addPotionToRewards",
        paramtypez = {}
    )
    public static class AddPotionsPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(AbstractRoom __instance) {
            if (!MoreStackableRelicsInitializer.enableWhiteBeastStacking)
                return;
            int count = -1; // set count to -1 because 1 potion is made already, so first White Beast is already accounted for
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(WhiteBeast.ID))
                    count++;
            for (int i = 0; i < count; i++)
                __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(ArrayList.class, "add"));
            }
        }
    }


}
