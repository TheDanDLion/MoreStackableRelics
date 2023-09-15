package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SneckoEye;

import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableSneckoEyes {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("SneckoEye"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    @SpirePatch2(
        clz = SneckoEye.class,
        method = "getUpdatedDescription"
    )
    public static class AmendSneckoEyeDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || __result == null || !MoreStackableRelicsInitializer.enableSneckoEyeStacking)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0];
        }
    }

    @SpirePatch2(
        clz = ConfusionPower.class,
        method = "onCardDraw"
    )
    public static class RerollNonZeroCostPatch {
        public static void Postfix(ConfusionPower __instance, AbstractCard card) {
            if (__instance.owner == null || __instance == null || card == null || !MoreStackableRelicsInitializer.enableSneckoEyeStacking || card.cost <= 0)
                return;
            boolean first = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(SneckoEye.ID)) {
                    if (first) {
                        first = false;
                    } else {
                        int newCost = AbstractDungeon.cardRandomRng.random(3);
                        if (card.cost != newCost) {
                            card.cost = newCost;
                            card.costForTurn = card.cost;
                            card.isCostModified = true;
                        }
                        card.freeToPlayOnce = false;
                        if (card.cost == 0)
                            break;
                    }
                }
        }
    }
}
