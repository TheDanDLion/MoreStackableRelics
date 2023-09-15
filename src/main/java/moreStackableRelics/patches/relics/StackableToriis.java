package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Torii;

import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableToriis {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("Torii"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numToriis = 0;

    public static void countToriis() {
        numToriis = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(Torii.ID)) {
                numToriis++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Torii.ID)) {
                relic.description = relic.getUpdatedDescription();
                for (PowerTip tip : relic.tips)
                    tip.body = relic.description;
            }
    }

    public static void incToriis(AbstractRelic r) {
        numToriis++;
        r.description = r.getUpdatedDescription();
        for (PowerTip tip : r.tips)
            tip.body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Torii.ID)) {
                relic.description = relic.getUpdatedDescription();
                for (PowerTip tip : relic.tips)
                    tip.body = relic.description;
            }
    }

    @SpirePatch2(
        clz = Torii.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enableToriiStacking || numToriis == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + (numToriis * 5);
        }
    }

    @SpirePatch2(
        clz = Torii.class,
        method = "onAttacked"
    )
    public static class ToriiBlockPatch {
        public static int Postfix(int __result, DamageInfo info, int damageAmount) {
            if (MoreStackableRelicsInitializer.enableToriiStacking && info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 1 && damageAmount <= 5 * numToriis
                && AbstractDungeon.player.hasRelic(Torii.ID)) {
                boolean first = __result == 5;
                for (AbstractRelic relic : AbstractDungeon.player.relics)
                    if (relic.relicId.equals(Torii.ID)) {
                        if (first)
                            first = false;
                        else {
                            relic.flash();
                            AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, relic));
                        }
                    }
                return 1;
            }
            return __result;
        }
    }
}
