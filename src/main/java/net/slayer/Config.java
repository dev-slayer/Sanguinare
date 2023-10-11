package net.slayer;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry public static boolean debug = false;
    @Entry public static int sunDamageRate = 4;
    @Entry public static int regenRate = 5;
    @Entry public static float bloodModifierTurnoverPoint = 10;
    @Entry public static float bloodSpeedModifier = 200;
    @Entry public static float bloodStrengthModifier = 2.5f;
}
