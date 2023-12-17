package moreStackableRelics.patches.relics;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.rewards.RewardItem;

import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.MoreStackableRelicsInitializer;

@SpirePatch2(
    clz = RewardItem.class,
    method = "applyGoldBonus"
)
public class StackableGoldenIdols {

    public static int getGoldIdolBonusGold(int tmp) {
        int gold = 0;
        float mult = 0.0F;
        if (MoreStackableRelicsInitializer.enableGoldenIdolStacking) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(GoldenIdol.ID)) {
                    mult += 0.25F;
                }
            }
            gold += MathUtils.round(tmp * mult);
        } else {
            gold = MathUtils.round(tmp * 0.25F);
        }
        return gold;
    }

    @SpireInstrumentPatch
    public static ExprEditor Editor() {
        return new ExprEditor() {
            public void edit(MethodCall m) {
                if (m.getMethodName().equals("round") && m.getLineNumber() < 127) {
                    try {
                        m.replace("{ $_ = moreStackableRelics.patches.relics.StackableGoldenIdols.getGoldIdolBonusGold(tmp); }");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
