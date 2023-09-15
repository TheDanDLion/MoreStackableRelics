package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldenEye;

import moreStackableRelics.MoreStackableRelicsInitializer;

@SpirePatch2(
    clz = ScryAction.class,
    method = SpirePatch.CONSTRUCTOR
)
public class StackableGoldenEyes {
    public static void Postfix(ScryAction __instance) {
        if (!MoreStackableRelicsInitializer.enableGoldenEyeStacking || __instance == null)
            return;
        boolean first = true; // first one is proc'd already
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(GoldenEye.ID)) {
                if (first) {
                    relic.flash();
                    first = false;
                }
                else {
                    __instance.amount += 2;
                    relic.flash();
                }
            }
    }
}
