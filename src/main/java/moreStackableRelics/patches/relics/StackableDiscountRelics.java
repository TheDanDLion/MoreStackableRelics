package moreStackableRelics.patches.relics;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

import basemod.ReflectionHacks;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import moreStackableRelics.ModInitializer;

public class StackableDiscountRelics {

    private static final RelicStrings RELIC_STRINGS = CardCrawlGame.languagePack.getRelicStrings(ModInitializer.makeID("DiscountRelics"));
    private static final String[] DESCRIPTIONS = RELIC_STRINGS.DESCRIPTIONS;

    public static int numCourier = 0;
    public static int numMembershipCard = 0;

    public static void countCouriers() {
        numCourier = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Courier.ID))
                numCourier++;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Courier.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void countMembershipCards() {
        numMembershipCard = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(MembershipCard.ID))
                numMembershipCard++;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(MembershipCard.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incCouriers(AbstractRelic r) {
        numCourier++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(Courier.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    public static void incMembershipCards(AbstractRelic r) {
        numMembershipCard++;
        r.description = r.getUpdatedDescription();
        r.tips.get(0).body = r.description;
        for (AbstractRelic relic : AbstractDungeon.player.relics)
            if (relic.relicId.equals(MembershipCard.ID)) {
                relic.description = relic.getUpdatedDescription();
                relic.tips.get(0).body = relic.description;
            }
    }

    private static int getTotalDiscount() {
        float discount = 1.0F;
        for (int i = 0; i < numMembershipCard; i++)
                discount *= 0.5F;
        for (int i = 0; i <numCourier; i++)
            discount *= 0.8F;
        return (int)((1.0F - discount) * 100.0F);
    }

    public static int getPurgeCost(int purgeCost) {
        if (ModInitializer.enableMemCardStacking) {
            float multiplier = 1.0f;
            for (int i = 0; i < numMembershipCard; i++)
                multiplier *= 0.5F;
            for (int i = 0; i <numCourier; i++)
                multiplier *= 0.8F;
            return MathUtils.round(purgeCost * multiplier);
        }
        return purgeCost;
    }

    @SpirePatch2(
        clz = Courier.class,
        method = "getUpdatedDescription"
    )
    public static class AmendCourierDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableCourierStacking || numCourier == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[0] + getTotalDiscount() + DESCRIPTIONS[2];
        }
    }

    @SpirePatch2(
        clz = MembershipCard.class,
        method = "getUpdatedDescription"
    )
    public static class AmendMembershipCardDescriptionPatch {
        public static String Postfix(String __result) {
            if (AbstractDungeon.player == null || DESCRIPTIONS == null || !ModInitializer.enableMemCardStacking || numMembershipCard == 1)
                return __result;
            return __result + " NL NL " + DESCRIPTIONS[1] + getTotalDiscount() + DESCRIPTIONS[2];
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "init"
    )
    public static class InitPatch {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(ShopScreen __instance) {

            int courierCounter = -1; // set to negative one since first one is always being proc'd
            int membershipCardCounter = -1;

            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(MembershipCard.ID) && ModInitializer.enableMemCardStacking)
                    membershipCardCounter++;
                else if (relic.relicId.equals(Courier.ID) && ModInitializer.enableCourierStacking)
                    courierCounter++;
            }

            ModInitializer.logger.info("Courier counter: " + courierCounter);
            ModInitializer.logger.info("Membership card counter: " + membershipCardCounter);

            if (ModInitializer.enableCourierStacking)
                for (int i = 0; i < courierCounter; i++)
                    __instance.applyDiscount(0.8F, true);
            if (ModInitializer.enableMemCardStacking)
                for (int i = 0; i < membershipCardCounter; i++)
                    __instance.applyDiscount(0.5F, true);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic"));
            }
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "purgeCard"
    )
    public static class PurgeCardPatch {
        @SpireInstrumentPatch
        public static ExprEditor Editor() {
            return new ExprEditor() {
                public void edit(MethodCall m) {
                    if (m.getMethodName().equals("round")) {
                        try {
                            m.replace("{ $_ = moreStackableRelics.patches.relics.StackableDiscountRelics.getPurgeCost(purgeCost); }");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }

        // @SpireInsertPatch(
        //     locator = Locator.class
        // )
        // public static void Postfix() {
        //     if (AbstractDungeon.player.hasRelic(SmilingMask.ID)) {
        //         ShopScreen.actualPurgeCost = 50;
        //     }

        //     float costMultiplier = 1.0F;
        //     for (AbstractRelic relic : AbstractDungeon.player.relics) {
        //         if (relic.relicId.equals(MembershipCard.ID) && ModInitializer.enableMemCardStacking)
        //             costMultiplier *= 0.5F;
        //         else if (relic.relicId.equals(Courier.ID) && ModInitializer.enableCourierStacking)
        //             costMultiplier *= 0.8F;
        //     }
        //     ModInitializer.logger.info("Cost multiplier: " + costMultiplier);

        //     ShopScreen.actualPurgeCost = MathUtils.round(ShopScreen.purgeCost * costMultiplier);
        // }

        // private static class Locator extends SpireInsertLocator {
        //     @Override
        //     public int[] Locate(CtBehavior ctBehavior) throws Exception {
        //         return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic"));
        //     }
        // }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "getNewPrice",
        paramtypez = {StorePotion.class}
    )
    public static class GetNewRelicPatch {
        public static void Postfix(StorePotion r) {
            int retVal = r.price;
            int courierCounter = -1; // set to negative one since first one is always being proc'd
            int membershipCardCounter = -1;

            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(MembershipCard.ID) && ModInitializer.enableMemCardStacking)
                    membershipCardCounter++;
                else if (relic.relicId.equals(Courier.ID) && ModInitializer.enableCourierStacking)
                    courierCounter++;
            }

            ModInitializer.logger.info("Courier counter: " + courierCounter);
            ModInitializer.logger.info("Membership card counter: " + membershipCardCounter);

            if (ModInitializer.enableMemCardStacking)
                for (int i = 0; i < courierCounter; i++)
                    retVal = ReflectionHacks.privateMethod(ShopScreen.class, "applyDiscountToRelic", Integer.class, Float.class).invoke(r, retVal, 0.8F);
            if (ModInitializer.enableCourierStacking)
                for (int i = 0; i < membershipCardCounter; i++)
                    retVal = ReflectionHacks.privateMethod(ShopScreen.class, "applyDiscountToRelic", Integer.class, Float.class).invoke(r, retVal, 0.5F);

            r.price = retVal;
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "getNewPrice",
        paramtypez = {StoreRelic.class}
    )
    public static class GetNewPotionPatch {
        public static void Postfix(StoreRelic r) {
            int retVal = r.price;
            int courierCounter = -1; // set to negative one since first one is always being proc'd
            int membershipCardCounter = -1;

            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(MembershipCard.ID) && ModInitializer.enableMemCardStacking)
                    membershipCardCounter++;
                else if (relic.relicId.equals(Courier.ID) && ModInitializer.enableCourierStacking)
                    courierCounter++;
            }

            ModInitializer.logger.info("Courier counter: " + courierCounter);
            ModInitializer.logger.info("Membership card counter: " + membershipCardCounter);

            if (ModInitializer.enableCourierStacking)
                for (int i = 0; i < courierCounter; i++)
                    retVal = ReflectionHacks.privateMethod(ShopScreen.class, "applyDiscountToRelic", Integer.class, Float.class).invoke(r, retVal, 0.8F);
            if (ModInitializer.enableMemCardStacking)
                for (int i = 0; i < membershipCardCounter; i++)
                    retVal = ReflectionHacks.privateMethod(ShopScreen.class, "applyDiscountToRelic", Integer.class, Float.class).invoke(r, retVal, 0.5F);

            r.price = retVal;
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "setPrice"
    )
    public static class SetPricePatch {
        public static void Postfix(AbstractCard card) {
            int tmpPrice = card.price;
            int courierCounter = -1; // set to negative one since first one is always being proc'd
            int membershipCardCounter = -1;

            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(MembershipCard.ID) && ModInitializer.enableMemCardStacking)
                    membershipCardCounter++;
                else if (relic.relicId.equals(Courier.ID) && ModInitializer.enableCourierStacking)
                    courierCounter++;
            }

            ModInitializer.logger.info("Courier counter: " + courierCounter);
            ModInitializer.logger.info("Membership card counter: " + membershipCardCounter);

            if (ModInitializer.enableCourierStacking)
                for (int i = 0; i < courierCounter; i++)
                    tmpPrice *= 0.8F;
            if (ModInitializer.enableMemCardStacking)
                for (int i = 0; i < membershipCardCounter; i++)
                    tmpPrice *= 0.5F;

            card.price = tmpPrice;
        }
    }
}
