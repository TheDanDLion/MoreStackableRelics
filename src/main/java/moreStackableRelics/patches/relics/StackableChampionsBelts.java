package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.ChampionsBelt;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

@SpirePatch2(
    clz = ApplyPowerAction.class,
    method = "update"
)
public class StackableChampionsBelts {
    @SpireInsertPatch(
        locator = Locator.class
    )
    public static void Insert(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
        MoreStackableRelicsInitializer.logger.info("HERE");
        MoreStackableRelicsInitializer.logger.info(MoreStackableRelicsInitializer.enableChampBeltStacking);
        MoreStackableRelicsInitializer.logger.info(__instance == null);
        MoreStackableRelicsInitializer.logger.info(___powerToApply == null);
        MoreStackableRelicsInitializer.logger.info(__instance.target != null);
        MoreStackableRelicsInitializer.logger.info(__instance.source != null);
        MoreStackableRelicsInitializer.logger.info(__instance.source.isPlayer);
        if (!MoreStackableRelicsInitializer.enableChampBeltStacking || __instance == null || ___powerToApply == null || __instance.target == null)
            return;
        if (__instance.source != null && __instance.source.isPlayer && __instance.target != __instance.source
            && ___powerToApply.ID.equals(VulnerablePower.POWER_ID) && !__instance.target.hasPower(ArtifactPower.POWER_ID)) {
                boolean firstOne = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics)
                    if (relic.relicId.equals(ChampionsBelt.ID)) {
                        if (firstOne)
                            firstOne = false;
                        else
                            relic.onTrigger(__instance.target);
                    }
            }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractRelic.class, "onTrigger"));
        }
    }
}
