package org.hugoandrade.worldcup2018.predictor.backend.config;


public final class StaticVariableUtils {
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = StaticVariableUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private StaticVariableUtils() {
        throw new AssertionError();
    }

    public enum SCountry {

        Russia("Russia"),
        SaudiArabia("Saudi Arabia"),
        Egypt("Egypt"),
        Uruguay("Uruguay"),

        Portugal("Portugal"),
        Spain("Spain"),
        Morocco("Morocco"),
        Iran("Iran"),

        France("France"),
        Australia("Australia"),
        Peru("Peru"),
        Denmark("Denmark"),

        Argentina("Argentina"),
        Iceland("Iceland"),
        Croatia("Croatia"),
        Nigeria("Nigeria"),

        Brazil("Brazil"),
        Switzerland("Switzerland"),
        CostaRica("Costa Rica"),
        Serbia("Serbia"),

        Germany("Germany"),
        Mexico("Mexico"),
        Sweden("Sweden"),
        SouthKorea("South Korea"),

        Belgium("Belgium"),
        Panama("Panama"),
        Tunisia("Tunisia"),
        England("England"),

        Poland("Poland"),
        Senegal("Senegal"),
        Colombia("Colombia"),
        Japan("Japan");

        public final String name;

        SCountry(String group) {
            name = group;
        }
    }

    public enum SStage {

        groupStage("Group Stage"),
        roundOf16("Round of 16"),
        quarterFinals("Quarter Finals"),
        semiFinals("Semi Finals"),
        thirdPlacePlayOff("3rd Place Playoff"),
        finals("Final"),
        all("All"),
        unknown("Unknown");

        public final String name;

        SStage(String stage) {
            name = stage;
        }

        public static SStage get(String stage) {
            for (SStage s : SStage.values()) {
                if (s.name.equalsIgnoreCase(stage)) {
                    return s;
                }
            }
            return null;
        }
    }

    public enum SGroup {

        A("A"),
        B("B"),
        C("C"),
        D("D"),
        E("E"),
        F("F"),
        G("G"),
        H("H");

        public final String name;

        SGroup(String group) {
            name = group;
        }

        public static SGroup get(String group) {
            for (SGroup s : SGroup.values()) {
                if (s.name.equalsIgnoreCase(group)) {
                    return s;
                }
            }
            return null;
        }
    }

    public enum Stadium {

        LuzhnikiStadium("Luzhniki Stadium (Moscow)"),
        OtkritieArena("Otkritie Arena (Moscow)"),
        KrestovskyStadium("Krestovsky Stadium (Saint Petersburg)"),
        FishtOlympicStadium("Fisht Olympic Stadium (Sochi)"),
        CosmosArena("Cosmos Arena (Samara)"),
        RostovArena("Rostov Arena (Rostov-on-Don)"),
        KazanArena("Kazan Arena (Kazan)"),
        VolgogradArena("Volgograd Arena (Volgograd)"),
        NizhnyNovgorodStadium("Nizhny Novgorod Stadium (Nizhny Novgorod)"),
        MordoviaArena("Mordovia Arena (Saransk)"),
        CentralStadium("Central Stadium (Yekaterinburg)"),
        KaliningradStadium("Kaliningrad Stadium (Kaliningrad)");

        public final String name;

        Stadium(String group) {
            name = group;
        }
    }
}
