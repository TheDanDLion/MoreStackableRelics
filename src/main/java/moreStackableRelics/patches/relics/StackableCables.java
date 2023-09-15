package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.DarkImpulseAction;
import com.megacrit.cardcrawl.actions.defect.ImpulseAction;
import com.megacrit.cardcrawl.actions.defect.TriggerEndOfTurnOrbsAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldPlatedCables;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableCables {

    public static void triggerOnEndOfTurn() {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(GoldPlatedCables.ID)) {
                ((AbstractOrb)AbstractDungeon.player.orbs.get(0)).onEndOfTurn();
            }
        }
    }

    @SpirePatch2(
        clz = TriggerEndOfTurnOrbsAction.class,
        method = "update"
    )
    public static class TriggerAllGoldPlatedCablesOnEndTurn {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(TriggerEndOfTurnOrbsAction __instance) {
            if (!MoreStackableRelicsInitializer.enableCableStacking)
                return;
            if (!(AbstractDungeon.player.orbs.get(0) instanceof EmptyOrbSlot)) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof GoldPlatedCables) {
                        if (first)
                            first = false;
                        else
                            ((AbstractOrb)AbstractDungeon.player.orbs.get(0)).onEndOfTurn();
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

    @SpirePatch2(
        clz = ImpulseAction.class,
        method = "update"
    )
    public static class TriggerAllGoldPlatedCablesOnImpulse {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (!MoreStackableRelicsInitializer.enableCableStacking)
                return;
            if (!(AbstractDungeon.player.orbs.get(0) instanceof EmptyOrbSlot)) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof GoldPlatedCables) {
                        if (first)
                            first = false;
                        else {
                            ((AbstractOrb)AbstractDungeon.player.orbs.get(0)).onStartOfTurn();
                            ((AbstractOrb)AbstractDungeon.player.orbs.get(0)).onEndOfTurn();
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int [] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic"));
            }
        }
    }

    @SpirePatch2(
        clz = DarkImpulseAction.class,
        method = "update"
    )
    public static class TriggerAllGoldPlatedCablesOnDarkImpulse {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (!MoreStackableRelicsInitializer.enableCableStacking)
                return;
            if (!(AbstractDungeon.player.orbs.get(0) instanceof EmptyOrbSlot)
                && (AbstractDungeon.player.orbs.get(0) instanceof Dark)) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(GoldPlatedCables.ID)) {
                        if (first)
                            first = false;
                        else {
                            ((AbstractOrb)AbstractDungeon.player.orbs.get(0)).onStartOfTurn();
                            ((AbstractOrb)AbstractDungeon.player.orbs.get(0)).onEndOfTurn();
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int [] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic"));
            }
        }
    }

    @SpirePatch2(
        clz = AbstractPlayer.class,
        method = "applyStartOfTurnOrbs"
    )
    public static class TriggerAllGoldPlatedCablesOnStartOfTurn {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(AbstractPlayer __instance) {
            if (!MoreStackableRelicsInitializer.enableCableStacking)
                return;
            if (!(__instance.orbs.get(0) instanceof EmptyOrbSlot)) {
                boolean first = true;
                for (AbstractRelic relic : __instance.relics) {
                    if (relic.relicId.equals(GoldPlatedCables.ID)) {
                        if (first)
                            first = false;
                        else
                            ((AbstractOrb)__instance.orbs.get(0)).onStartOfTurn();
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int [] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic"));
            }
        }
    }

    @SpirePatch2(
        cls = "com.evacipated.cardcrawl.mod.stslib.actions.defect.TriggerPassiveAction",
        method = "update",
        requiredModId = "stslib"
    )
    public static class IncreaseTriggersForStsLibPatch {
        public static void Prefix(AbstractGameAction __instance, float ___duration, AbstractOrb ___targetOrb) {
            if (!MoreStackableRelicsInitializer.enableCableStacking)
                return;
            if (___duration == Settings.ACTION_DUR_FAST && !AbstractDungeon.player.orbs.isEmpty() && AbstractDungeon.player.orbs.get(0) == ___targetOrb) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(GoldPlatedCables.ID)) {
                        if (first)
                            first = false;
                        else {
                            ___targetOrb.onStartOfTurn();
                            ___targetOrb.onEndOfTurn();
                        }
                    }
                }
            }
        }
    }
}
