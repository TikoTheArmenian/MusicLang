package SynthStuff;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;

public class AudioSoundsSynth {
    final private double sampleRate = 44100.0;

    private double frequency;
    private double frequency2;
    private double amplitude;
    private double seconds;
    private double twoPiF;
    private double piF;
    private String name;

    AudioSoundsSynth(double frequency, double frequency2, double amplitude, double twoPiF, double piF, double seconds, String name)
    {
        this.frequency = frequency;
        this.frequency2 = frequency2;
        this.amplitude = amplitude;
        this.seconds = seconds;
        this.twoPiF = twoPiF;
        this.piF = piF;
        this.name = name;




    }

    public AudioSoundsSynth(double frequency, double frequency2, double amplitude, double seconds, String name)
    {
        AudioSoundsSynth synth  = new AudioSoundsSynth(frequency, frequency2, amplitude, 2 * Math.PI * frequency, Math.PI * frequency2, seconds,name);

    }

    public static void makeSound(double frequency, double frequency2, double amplitude, double seconds, String name)
    {

        double twoPiF = 2 * Math.PI * frequency;
        double piF = Math.PI * frequency2;
        double sampleRate = 44100.0;


            float[] buffer = new float[(int)(seconds * sampleRate)];

            for (int sample = 0; sample < buffer.length; sample++) {
                double time = sample/sampleRate;
                buffer[sample] = (float)(amplitude * Math.cos(piF * time) * Math.sin(twoPiF * time));
            }

            final byte[] byteBuffer = new byte[buffer.length * 2];

            int bufferIndex = 0;
            for (int i = 0; i < byteBuffer.length; i++) {
                final int x = (int)(buffer[bufferIndex++] * 32767.0);

                byteBuffer[i++] = (byte)x;
                byteBuffer[i] = (byte)(x >>> 8);
            }

            File out = new File("src/"+name+".wav");

            final boolean bigEndian = false;
            final boolean signed = true;

            final int bits = 16;
            final int channels = 1;

            AudioFormat format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length);
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
                audioInputStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

    }

    public void setName(String s)
    {
        name = s;
    }

    public void play(double seconds)
    {
        float[] buffer = new float[(int)(seconds * sampleRate)];

        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float)(amplitude * Math.cos(piF * time) * Math.sin(twoPiF * time));
        }

        final byte[] byteBuffer = new byte[buffer.length * 2];

        int bufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int)(buffer[bufferIndex++] * 32767.0);

            byteBuffer[i++] = (byte)x;
            byteBuffer[i] = (byte)(x >>> 8);
        }

        File out = new File("src/"+name+".wav");

        final boolean bigEndian = false;
        final boolean signed = true;

        final int bits = 16;
        final int channels = 1;

        AudioFormat format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length);
        try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
            audioInputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
