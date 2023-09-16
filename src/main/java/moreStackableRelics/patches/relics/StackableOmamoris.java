package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.ReflectionHacks;
import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableOmamoris {

    @SpirePatch2(
        clz = FastCardObtainEffect.class,
        method = SpirePatch.CONSTRUCTOR
    )
    public static class FastCheckOtherOmamorisPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(FastCardObtainEffect __instance) {
            if (MoreStackableRelicsInitializer.enableOmamoriStacking && !__instance.isDone) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(Omamori.ID)) {
                        if (first) {
                            first = false;
                        } else if (relic.counter != 0) {
                            ((Omamori)relic).use();
                            __instance.duration = 0.0F;
                            __instance.isDone = true;
                            break;
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(CardHelper.class, "obtain"));
            }
        }
    }

    @SpirePatch2(
        clz = ShowCardAndObtainEffect.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCard.class, float.class, float.class, boolean.class}
    )
    public static class CheckOtherOmamorisPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(ShowCardAndObtainEffect __instance, boolean convergeCards) {
            if (MoreStackableRelicsInitializer.enableOmamoriStacking && !__instance.isDone) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(Omamori.ID)) {
                        if (first) {
                            first = false;
                        } else if (relic.counter != 0) {
                            ((Omamori)relic).use();
                            __instance.duration = 0.0F;
                            __instance.isDone = true;
                            ReflectionHacks.setPrivate(__instance, ShowCardAndObtainEffect.class, "converge", convergeCards);
                            break;
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(CardHelper.class, "obtain"));
            }
        }
    }
}
