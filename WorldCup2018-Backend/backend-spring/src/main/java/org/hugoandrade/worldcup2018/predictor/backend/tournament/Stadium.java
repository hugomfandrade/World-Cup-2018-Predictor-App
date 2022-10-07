package org.hugoandrade.worldcup2018.predictor.backend.tournament;

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