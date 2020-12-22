package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Stats {
    private int NP;
    private int GP;
    private int LP;
    private int NSEEDS;
    private double FPR;
    private long NFE;
    private double P;

    @Override
    public String toString() {
        return "Stats{" +
                "NP=" + NP +
                ", GP=" + GP +
                ", LP=" + LP +
                ", NSEEDS=" + NSEEDS +
                ", FPR=" + FPR +
                ", NFE=" + NFE +
                '}';
    }
}
