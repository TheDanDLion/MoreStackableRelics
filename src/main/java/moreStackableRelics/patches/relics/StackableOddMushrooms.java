package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.OddMushroom;

import moreStackableRelics.ModInitializer;

public class StackableOddMushrooms {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("OddMushroom"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numMushrooms = 0;

    public static void countMushrooms() {
        numMushrooms = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(OddMushroom.ID)) {
                numMushrooms++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(OddMushroom.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incMushrooms(AbstractRelic r) {
        numMushrooms++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(OddMushroom.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    private static float getMultiplier() {
        float multiplier = 0.5F;
        for (int i = 0; i < numMushrooms; i++)
            multiplier /= 2.0F;
        return 1.0F + multiplier;
    }

    @SpirePatch2(
        clz = OddMushroom.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableOddMushroomStacking || numMushrooms == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + ((int)(100.0F * (getMultiplier() - 1.0F))) + DESCRIPTIONS[1];
        }
    }

    @SpirePatch2(
        clz = VulnerablePower.class,
        method = "atDamageReceive"
    )
    public static class ApplyAllMushroomsPatch {
        public static float Postfix(float __result, VulnerablePower __instance, DamageInfo.DamageType type) {
            if (!ModInitializer.enableOddMushroomStacking)
                return __result;
            if (type == DamageInfo.DamageType.NORMAL && AbstractDungeon.player.hasRelic("Odd Mushroom")) {
                return __result / 1.25F * getMultiplier();
            }
            return __result;
        }
    }
}
