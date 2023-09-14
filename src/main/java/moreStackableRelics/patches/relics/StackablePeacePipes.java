package moreStackableRelics.patches.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PeacePipe;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.vfx.campfire.CampfireTokeEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.ModInitializer;

public class StackablePeacePipes {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("PeacePipe"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    private static final UIStrings UI_STRINGS = CardCrawlGame.languagePack.getUIString("HandCardSelectScreen");
    private static final String[] TEXT = UI_STRINGS.TEXT;

    public static boolean buttonAdded = false;

    public static int numPeacePipes = 0;

    public static void countPeacePipes() {
        numPeacePipes = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PeacePipe.ID))
                numPeacePipes++;
    }

    public static boolean isButtonAdded() {
        boolean val = buttonAdded;
        if (ModInitializer.enablePeacePipeStacking)
            buttonAdded = true;
        return val;
    }

    @SpirePatch2(
        clz = PeacePipe.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enablePeacePipeStacking)
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
        clz = PeacePipe.class,
        method = "addCampfireOption"
    )
    public static class OnlyOneButtonPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    if (m.getMethodName().equals("add")) {
                        try {
                            m.replace("if (!moreStackableRelics.patches.relics.StackablePeacePipes.isButtonAdded()) { $_ = $proceed($$); }");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }

    @SpirePatch2(
        clz = CampfireTokeEffect.class,
        method = "update"
    )
    public static class AddExtraTokesPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static SpireReturn<Void> Insert() {
            if (!ModInitializer.enablePeacePipeStacking)
                return SpireReturn.Continue();
            AbstractDungeon.gridSelectScreen.anyNumber = true;
            AbstractDungeon.gridSelectScreen.open(
                CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), numPeacePipes, (numPeacePipes == 1 ? CampfireTokeEffect.TEXT[0] : TEXT[2] + numPeacePipes + TEXT[3] + DESCRIPTIONS[1]), false, false, true, true);
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

        @SpireInsertPatch(
            locator = Locator2.class
        )
        public static void Insert2() {
            if (!ModInitializer.enablePeacePipeStacking)
                return;
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, Settings.WIDTH * (i + 1) / (numPeacePipes + 1), Settings.HEIGHT / 2));
                AbstractDungeon.player.masterDeck.removeCard(card);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "clear");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
