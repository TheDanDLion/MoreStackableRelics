package moreStackableRelics.patches.relics;

import java.util.ArrayList;
import java.util.UUID;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch2(
    clz = AbstractRelic.class,
    method = SpirePatch.CLASS
)
public class RelicId {
    private static String DEFAULT_ID = "StackableId";

    public static ArrayList<String> relicIds = new ArrayList<>();

    public static SpireField<String> stackId = new SpireField<>(() -> DEFAULT_ID);

    public static void register(AbstractRelic relic) {
        String id = stackId.get(relic);
        if (id.equals(DEFAULT_ID)) {
            id = UUID.randomUUID().toString();
            stackId.set(relic, id);
            relicIds.add(id);
        }
    }

    public static boolean isRegistered(AbstractRelic relic) {
        return relicIds.contains(stackId.get(relic));
    }
}
