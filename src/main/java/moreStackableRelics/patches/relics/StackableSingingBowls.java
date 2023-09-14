package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;

import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.ModInitializer;

@SpirePatch2(
    clz = SingingBowlButton.class,
    method = "onClick"
)
public class StackableSingingBowls {

    public static void applySingingBowls() {
        if (ModInitializer.enableSingingBowlStacking) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(SingingBowl.ID)) {
                    relic.flash();
                    AbstractDungeon.player.increaseMaxHp(2, true);
                }
            }
        } else {
            if (AbstractDungeon.player.hasRelic(SingingBowl.ID)) {
                AbstractDungeon.player.getRelic(SingingBowl.ID).flash();
                AbstractDungeon.player.increaseMaxHp(2, true);
            }
        }
    }

    @SpireInstrumentPatch
    public static ExprEditor Editor() {
        return new ExprEditor() {
            public void edit(MethodCall m) {
                if (m.getMethodName().equals("increaseMaxHp")) {
                    try {
                        m.replace("{ moreStackableRelics.patches.relics.StackableSingingBowls.applySingingBowls(); }");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
