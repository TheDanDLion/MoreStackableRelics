package moreStackableRelics.patches.relics;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PaperCrane;

import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackablePaperCranes {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("PaperCrane"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numCranes = 0;

    public static void countCranes() {
        numCranes = 0;
        if (AbstractDungeon.player == null)
            return;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(PaperCrane.ID)) {
                numCranes++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PaperCrane.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incCranes(AbstractRelic r) {
        if (AbstractDungeon.player == null || r == null)
            return;
        numCranes++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PaperCrane.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    private static int getCurrentReduction() {
        float reduction = 0.75F;
        for (int i = 0; i < numCranes; i++)
            reduction *= 0.8F;
        return MathUtils.round((100.0F * (1.0F - reduction)));
    }

    @SpirePatch2(
        clz = PaperCrane.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enablePaperCraneStacking || numCranes == 1 || __result == null)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + getCurrentReduction() + DESCRIPTIONS[1];
        }
    }

    @SpirePatch2(
        clz = WeakPower.class,
        method = "atDamageGive"
    )
    public static class AtDamageGivePatch {
        public static float Postfix(WeakPower __instance, float __result, DamageInfo.DamageType type) {
            if (!MoreStackableRelicsInitializer.enablePaperCraneStacking || type == null || __instance.owner.isPlayer)
                return __result;
            if (type == DamageType.NORMAL) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics)
                    if (relic.relicId.equals(PaperCrane.ID)) {
                        if (first)
                            first = false;
                        else
                            __result *= 0.8F;
                    }
            }
            return __result;
        }
    }


}
