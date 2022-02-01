import SynthStuff.AudioSoundsSynth;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;

public class main2 {

    public static void main(String[] args) {
        try
        {
            //source file
            final File file1 = new File("src/dong.wav");
            //destination file
            final File file2 = new File("src/dongTemp.wav");
            //audio stream of file1
            final AudioInputStream in1 = AudioSystem.getAudioInputStream(file1);

            //get audio format for targetted sound
            final AudioFormat inFormat = getOutFormat(in1.getFormat(),5000);
            //get the target file audio stream using file format
            final AudioInputStream in2 = AudioSystem.getAudioInputStream(inFormat, in1);
            //write the audio file in targeted pitch file
            AudioSystem.write(in2, AudioFileFormat.Type.WAVE, file2);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }



    //change the frequency of Audio format
    private static AudioFormat getOutFormat (AudioFormat inFormat,int sampleRate){
        int ch = inFormat.getChannels();
        float rate = inFormat.getSampleRate();
        return new AudioFormat(inFormat.getEncoding(), sampleRate, 16, ch, ch * 2, rate,
                inFormat.isBigEndian());
    }
}