package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.NeowsLament;

import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableNeowsLament {

    private static boolean used = false;

    @SpirePatch2(
        clz = AbstractPlayer.class,
        method = "applyStartOfCombatLogic"
    )
    public static class ResetFlagPatch {
        public static void Prefix() {
            used = false;
        }
    }

    @SpirePatch2(
        clz = NeowsLament.class,
        method = "atBattleStart"
    )
    public static class DecrementFirstOnlyPatch {
        public static SpireReturn<Void> Prefix(NeowsLament __instance) {
            if (!MoreStackableRelicsInitializer.enableNeowsLamentStacking)
                return SpireReturn.Continue();
            if (used || __instance.counter <= 0)
                return SpireReturn.Return();
            used = true;
            return SpireReturn.Continue();
        }
    }
}
