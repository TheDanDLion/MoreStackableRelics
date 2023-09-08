package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Calipers;

import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.ModInitializer;

public class StackableCalipers {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("Calipers"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numCalipers = 0;

    public static void countCalipers() {
        numCalipers = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Calipers.ID))
                numCalipers++;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Calipers.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incCalipers(AbstractRelic r) {
        numCalipers++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Calipers.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static int getCalipersBlockLoss() {
        int blockLoss = 15;
        if (numCalipers > 1)
            blockLoss -= (numCalipers - 1);
        return (ModInitializer.enableCaliperStacking ? blockLoss : 15);
    }

    @SpirePatch2(
        clz = Calipers.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableCaliperStacking || numCalipers == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + getCalipersBlockLoss();
        }
    }

    @SpirePatch2(
        clz = GameActionManager.class,
        method = "getNextAction"
    )
    public static class GetNextActionPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    try {
                        if (m.getMethodName().equals("loseBlock") && m.getSignature().equals("(I)V")) {
                            m.replace("{ com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.loseBlock(moreStackableRelics.patches.relics.StackableCalipers.getCalipersBlockLoss()); }");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }


}
