package net.chromarenderer.math;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bensteinert
 */
public class Constants {

    private static float calculateMachineEpsilonFloat() {
        float machEps = 1.0f;

        do
            machEps /= 2.0f;
        while ((float) (1.0 + (machEps / 2.0)) != 1.0);

        return machEps;
    }

    public static final float FLT_EPSILON =  calculateMachineEpsilonFloat();
    public static final double DBL_EPSILON = 0.000001;
    public static final float SPHERE_NAN_LIMIT = 1000000.0f;
    public static final float PI_f = 3.141593f;
    public static final float TWO_PI_f = 6.2831854f;
    public static final float RR_LIMIT = 0.80f;

    private static final int[] SOME_NICE_PRIMES = {
            10009, 10037, 10039, 10061, 10067, 10069, 10079, 10091, 10093, 10099,
            10103, 10111, 10133, 10139, 10141, 10151, 10159, 10163, 10169, 10177,
            10181, 10193, 10211, 10223, 10243, 10247, 10253, 10259, 10267, 10271,
            10273, 10289, 10301, 10303, 10313, 10321, 10331, 10333, 10337, 10343,
            10357, 10369, 10391, 10399, 10427, 10429, 10433, 10453, 10457, 10459,
            10463, 10477, 10487, 10499, 10501, 10513, 10529, 10531, 10559, 10567,
            10589, 10597, 10601, 10607, 10613, 10627, 10631, 10639, 10651, 10657,
            10663, 10667, 10687, 10691, 10709, 10711, 10723, 10729, 10733, 10739,
            10753, 10771, 10781, 10789, 10799, 10831, 10837, 10847, 10853, 10859,
    };

    private static final AtomicInteger primeIdx = new AtomicInteger(0);

    /*
     * well let's see when I get an ArrayIndexOutOfBounds, please increment here: [0]
     */
    public static final int getNextPrime(){
        return SOME_NICE_PRIMES[primeIdx.getAndIncrement()];
    }


}
