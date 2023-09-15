package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.StrangeSpoon;

import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.MoreStackableRelicsInitializer;

public class StackableStrangeSpoons {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(MoreStackableRelicsInitializer.makeID("StrangeSpoon"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numStrangeSpoons = 0;

    public static void countSpoons() {
        numStrangeSpoons = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(StrangeSpoon.ID)) {
                numStrangeSpoons++;
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(StrangeSpoon.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incSpoons(AbstractRelic r) {
        numStrangeSpoons++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(StrangeSpoon.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static boolean getSpoonProc() {
        boolean proc = false;
        if (MoreStackableRelicsInitializer.enableStrangeSpoonStacking) {
            for (AbstractRelic relic : AbstractDungeon.player.relics)
                if (relic.relicId.equals(StrangeSpoon.ID)) {
                    proc = AbstractDungeon.cardRandomRng.randomBoolean();
                    if (proc)
                        break;
                }
        } else {
            proc = AbstractDungeon.cardRandomRng.randomBoolean();
        }
        return proc;
    }

    private static int getSpoonProcChance() {
        float chance = 1.0F;
        for (int i = 0; i < numStrangeSpoons; i++)
            chance *= 0.5F;
        return (int)(100.0F * (1.0F - chance));
    }

    @SpirePatch2(
        clz = StrangeSpoon.class,
        method = "getUpdatedDescription"
    )
    public static class AmendDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !MoreStackableRelicsInitializer.enableStrangeSpoonStacking || numStrangeSpoons == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + getSpoonProcChance() + DESCRIPTIONS[1];
        }
    }

    @SpirePatch2(
        clz = UseCardAction.class,
        method = "update"
    )
    public static class CheckAllSpoonsPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    if(m.getMethodName().equals("randomBoolean")) {
                        try {
                            m.replace("{ $_ = moreStackableRelics.patches.relics.StackableStrangeSpoons.getSpoonProc(); }");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }

        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            if (MoreStackableRelicsInitializer.enableStrangeSpoonStacking)
                for (AbstractRelic relic : AbstractDungeon.player.relics)
                    if (relic.relicId.equals(StrangeSpoon.ID))
                        relic.flash();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic"));
            }
        }
    }

}
