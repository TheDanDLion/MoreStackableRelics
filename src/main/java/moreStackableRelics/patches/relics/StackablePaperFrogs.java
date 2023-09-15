package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PaperFrog;

import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackablePaperFrogs {

    private static final RelicStrings RELIC_STIRNGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("PaperFrog"));
    private static final String[] DESCRIPTIONS = RELIC_STIRNGS.DESCRIPTIONS;

    public static int numFrogs = 0;

    public static void countFrogs() {
        numFrogs = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(PaperFrog.ID)) {
                numFrogs++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PaperFrog.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incFrogs(AbstractRelic r) {
        numFrogs++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(PaperFrog.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    private static float getMultiplier() {
        float multiplier = 1.5F;
        for (int i = 0; i < numFrogs; i++)
            multiplier += 0.25F;
        return multiplier;
    }

    @SpirePatch2(
        clz = PaperFrog.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enablePaperFrogStacking || numFrogs == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + ((int)((getMultiplier() - 1.0F) * 100.0F)) + DESCRIPTIONS[1];
        }
    }

    @SpirePatch2(
        clz = VulnerablePower.class,
        method = "atDamageReceive"
    )
    public static class ApplyAllFrogsPatch {
        public static float Postfix(float __result, VulnerablePower __instance, DamageInfo.DamageType type) {
            if (!MoreStackableRelicsInitializer.enablePaperFrogStacking)
                return __result;
            if (type == DamageInfo.DamageType.NORMAL && __instance.owner != null && !__instance.owner.isPlayer) {
                if (AbstractDungeon.player.hasRelic(PaperFrog.ID)) {
                    return __result / 1.75F * getMultiplier();
                }
            }
            return __result;
        }
    }
}
