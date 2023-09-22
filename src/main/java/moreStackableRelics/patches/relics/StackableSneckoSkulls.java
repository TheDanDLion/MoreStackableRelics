package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SneckoSkull;

import moreStackableRelics.MoreStackableRelicsInitializer;

@SpirePatch2(
    clz = ApplyPowerAction.class,
    method = SpirePatch.CONSTRUCTOR,
    paramtypez = {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class}
)
public class StackableSneckoSkulls {
    public static void Postfix(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source, AbstractPower ___powerToApply) {
        if (MoreStackableRelicsInitializer.enableSneckoSkullStacking
            && AbstractDungeon.player.hasRelic(SneckoSkull.ID)
            && source != null && source.isPlayer && target != source
            && ___powerToApply.ID.equals(PoisonPower.POWER_ID)) {
                __instance.amount--;
                ___powerToApply.amount--;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(SneckoSkull.ID)) {
                        __instance.amount++;
                        ___powerToApply.amount++;
                        relic.flash();
                    }
                }
        }
    }
}
