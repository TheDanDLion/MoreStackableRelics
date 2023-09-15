package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WingBoots;

import javassist.CtBehavior;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableWingBoots {

    @SpirePatch2(
        clz = MapRoomNode.class,
        method = "wingedIsConnectedTo"
    )
    public static class CheckOtherWingBootsPatch {
        public static boolean Postfix(boolean __result, MapRoomNode __instance, MapRoomNode node) {
            if (__result || !MoreStackableRelicsInitializer.enableWingBootStacking)
                return __result;

            boolean first = true;
            for (MapEdge edge : __instance.getEdges()) {
                if (node.y == edge.dstY) {
                    for (AbstractRelic relic : AbstractDungeon.player.relics) {
                        if (relic.relicId.equals(WingBoots.ID)) {
                            if (first) {
                                first = false;
                            } else {
                                __result = true;
                                break;
                            }
                        }
                    }
                }
            }
            return __result;
        }
    }

    @SpirePatch2(
        clz = MapRoomNode.class,
        method = "update"
    )
    public static class SetOtherWingBootsCounterPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (MoreStackableRelicsInitializer.enableWingBootStacking) {
                boolean first = true;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(WingBoots.ID)) {
                        if (first) {
                            first = false;
                        } else if (relic.counter > 0) {
                            relic.counter--;
                            if (relic.counter <= 0)
                                relic.setCounter(-2);
                            break;
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic"));
            }
        }
    }
}
