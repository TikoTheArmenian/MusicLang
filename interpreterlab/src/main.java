import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.ChordProgression;
import org.staccato.ReplacementMapPreprocessor;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.Port;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class main {
    private boolean shouldGenerate = false;
    private Integer wavePos = 0;


    //change the frequency of Audio format from 44100 Hz to 72000 Hz.
    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz

    public static void main(String[] args) {
        //new Player().play(new ChordProgression("I IV vi V").eachChordAs("$!i $!i Ri $!i"), new Rhythm().addLayer("..X...X...X...XO"));
        Pattern p1 = new Pattern("V0 I[Acoustic_Bass] Eq Ch. | Eq Ch. | Dq Eq Dq Cq");
        Pattern p2 = new Pattern("V1 I[Flute] Rw     | Rw     | GmajQQQ  CmajQ");
        Player player = new Player();
        player.play(p1, p2);

    }
}

