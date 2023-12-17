package moreStackableRelics.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.relics.Calipers;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.DreamCatcher;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.relics.OddMushroom;
import com.megacrit.cardcrawl.relics.PaperCrane;
import com.megacrit.cardcrawl.relics.PaperFrog;
import com.megacrit.cardcrawl.relics.PeacePipe;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.relics.StrangeSpoon;
import com.megacrit.cardcrawl.relics.Torii;

public class RelicHooks {

    public static boolean losingRelic = false;

    @SpirePatch2(
        clz = AbstractPlayer.class,
        method = "loseRelic"
    )
    public static class LoseRelicPatch {
        public static void Prefix(AbstractPlayer __instance) {
            losingRelic = true;
        }

        public static void Postfix(boolean __result, String targetID) {
            if (__result) {
                if (targetID.equals(Boot.ID))
                    StackableBoots.countBoots();
                else if (targetID.equals(Torii.ID))
                    StackableToriis.countToriis();
                else if (targetID.equals(Calipers.ID))
                    StackableCalipers.countCalipers();
                else if (targetID.equals(PaperFrog.ID))
                    StackablePaperFrogs.countFrogs();
                else if (targetID.equals(PeacePipe.ID))
                    StackablePeacePipes.countPeacePipes();
                else if (targetID.equals(PaperCrane.ID))
                    StackablePaperCranes.countCranes();
                else if (targetID.equals(OddMushroom.ID))
                    StackableOddMushrooms.countMushrooms();
                else if (targetID.equals(MembershipCard.ID) || targetID.equals(Courier.ID)) {
                    StackableDiscountRelics.countMembershipCards();
                    StackableDiscountRelics.countCouriers();
                }
                else if (targetID.equals(StrangeSpoon.ID))
                    StackableStrangeSpoons.countSpoons();
                else if (targetID.equals(DreamCatcher.ID))
                    StackableDreamCatchers.countDreamCatchers();
                else if (targetID.equals(PreservedInsect.ID))
                    StackablePreservedInsects.countPreservedInsects();
                else if (targetID.equals(SmilingMask.ID))
                    StackableSmilingMasks.countMasks();
            }
            losingRelic = false;
        }
    }
}
