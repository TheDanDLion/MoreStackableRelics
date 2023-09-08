package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;

import moreStackableRelics.ModInitializer;

public class StackableBoots {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("Boot"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numBoots = 0;

    public static void countBoots() {
        numBoots = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(Boot.ID)) {
                numBoots++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Boot.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incBoots(AbstractRelic r) {
        numBoots++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Boot.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    @SpirePatch2(
        clz = Boot.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableBootStacking || numBoots == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + (numBoots * 5);
        }
    }

    @SpirePatch2(
        clz = Boot.class,
        method = "onAttackToChangeDamage"
    )
    public static class BootDamagePatch {
        public static int Postfix(int __result, Boot __instance, DamageInfo info, int damageAmount) {
            if (!ModInitializer.enableBootStacking)
                return __result;
            if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && damageAmount < 5 * numBoots) {
                __result = (5 * numBoots);
            }
            return __result;
        }
    }

}
