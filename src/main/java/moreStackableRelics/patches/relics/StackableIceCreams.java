package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.IceCream;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableIceCreams {
    
    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("IceCream"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    @SpirePatch2(
        clz = IceCream.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enableIceCreamStacking || __result == null) {
                return __result;
            }
            return __result + " NL NL " + DESCRIPTIONS[0];
        }
    }

    @SpirePatch2(
        clz = EnergyManager.class,
        method = "recharge"
    )
    public static class GainStackedEnergyPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (MoreStackableRelicsInitializer.enableIceCreamStacking) {
                boolean first = true;
                int energyGain = 0;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(IceCream.ID)) {
                        if (first) {
                            first = false;
                        } else {
                            relic.flash();
                            EnergyPanel.addEnergy(AbstractDungeon.player.energy.energyMaster);
                            energyGain += AbstractDungeon.player.energy.energyMaster;
                        }
                    }
                }
                AbstractDungeon.actionManager.updateEnergyGain(energyGain);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.NewExprMatcher(RelicAboveCreatureAction.class);
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
