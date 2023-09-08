package moreStackableRelics.patches.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;

import javassist.CtBehavior;
import moreStackableRelics.ModInitializer;

public class StackableDreamCatchers {

    public static int numDreamCatchers = 0;
    private static boolean inChain = false;
    private static boolean choosing = false;
    private static int counter = 0;

    public static void countDreamCatchers() {
        numDreamCatchers = 0;
        for (int i = 0; i < AbstractDungeon.player.relics.size(); i++) {
            if (AbstractDungeon.player.relics.get(i).relicId.equals("Dream Catcher")) {
                numDreamCatchers++;
            }
        }
    }

    @SpirePatch2(
        clz = AbstractDungeon.class,
        method = "closeCurrentScreen"
    )
    public static class SetChoosingToFalsePatch {
        public static void Postfix() {
            choosing = false;
        }
    }

    @SpirePatch2(
        clz = AbstractRoom.class,
        method = "update"
    )
    public static class ContinueDreamCatcherChainPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(AbstractRoom __instance) {
            if (!ModInitializer.enableDreamCatcherStacking || counter == 0 || !inChain || choosing || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD || !(__instance instanceof RestRoom))
                return;
            ModInitializer.logger.info("Counter: " + counter);
            counter--;
            AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
            ArrayList<AbstractCard> rewardCards = AbstractDungeon.getRewardCards();
            if (rewardCards != null && !rewardCards.isEmpty()) {
                AbstractDungeon.cardRewardScreen.open(rewardCards, null, CampfireSleepEffect.TEXT[0]);
                choosing = true;
            }
            if (counter <= 0) {
                inChain = false;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findAllInOrder(ctBehavior, new Matcher.MethodCallMatcher(ProceedButton.class, "show"));
            }
        }
    }

    @SpirePatch2(
        clz = CampfireSleepEffect.class,
        method = "update"
    )
    public static class StartDreamCatcherChainPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (!ModInitializer.enableDreamCatcherStacking)
                return;
            inChain = true;
            counter = numDreamCatchers - 1;
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(CardRewardScreen.class, "open"));
            }
        }
    }
}
