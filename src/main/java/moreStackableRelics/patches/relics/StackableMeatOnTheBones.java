package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.MeatOnTheBone;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableMeatOnTheBones {

    private static boolean showRelic = true;

    public static void triggerAllMeats() {
        showRelic = true;
        if (AbstractDungeon.player != null) {
            if (MoreStackableRelicsInitializer.enableMeatOnTheBoneStacking) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(MeatOnTheBone.ID)) {
                        relic.onTrigger();
                        showRelic = false;
                    }
                }
            } else {
                AbstractDungeon.player.getRelic(MeatOnTheBone.ID).onTrigger();
            }
        }
    }

    public static void procIfNotAlready(AbstractRelic r) {
        if (showRelic) {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, r));
        }
    }

    @SpirePatch2(
        clz = AbstractRoom.class,
        method = "endBattle"
    )
    public static class TriggerAllMeatsPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    try {
                        if (m.getMethodName().equals("getRelic")) {
                            m.replace("{ moreStackableRelics.patches.relics.StackableMeatOnTheBones.getMeatOnTheBones(); }");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }

    @SpirePatch2(
        clz = MeatOnTheBone.class,
        method = "onTrigger"
    )
    public static class PreventMultipleProcsPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    try {
                        if (m.getMethodName().equals("flash")) {
                            m.replace("{ moreStackableRelics.patches.relics.StackableMeatOnTheBones.procIfNotAlready(this); }");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }
}
