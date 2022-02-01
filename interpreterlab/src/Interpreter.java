import SynthStuff.AudioSoundsSynth;
import SynthStuff.BasicSynth;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import org.jfugue.rhythm.Rhythm;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;


public class Interpreter
{

  private final static Map<String, String> DRUMS  = new HashMap<String, String>() {{
    put("BASS_DRUM","O");
    put("ACOUSTIC_SNARE", "S");
    put("CRASH_CYMBAL_1","i");
    put("PEDAL_HI_HAT", "^");
    put("HAND_CLAP","X");
  }};


  private static Scanner in = new Scanner(System.in);

  private static ArrayList<Triplet<String,String,String>> sounds = new ArrayList<>();




  public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
//  make it so that if u just do wait with no arguments it waits untill the previouse thing is done.
//  If you do wait with argument noone or something thatn it waits for all sound to finish


    Parser p = new Parser();

    State s = new State();

    s.setVariableValue("milliOn",0);

    p.addPrefix("print", 1);

    p.addPrefix("//", 0);

    p.addPrefix("wait", 1);

    p.addPrefix("randomRange",2);

    p.addPrefix("block",2);

    p.addPrefix("run",1);

    p.addPrefix("makeSound",5); //double frequency, double frequency2, double amplitude, double millis, String name

    p.addPrefix("play",1);

    p.addPrefix("playI",4);

    p.addPrefix("playD",3);

    p.addPrefix("list",1);

    p.addPrefix("loop",2);

    p.addPrefix("if",2);

    p.addInfix("+", 2);

    p.addInfix("-", 2);

    p.addInfix("*", 2);

    p.addInfix("/", 2);

    p.addInfix("%", 2);

    p.addInfix("==", 2);

    p.addInfix(">", 2);

    p.addInfix(">=", 2);

    p.addInfix("<=", 2);

    p.addInfix("<=", 2);

    p.addDelimiters("(", ")");

    p.addDelimiters("{", "}");

    p.addDelimiters("\"", "\"");

    p.addInfix("=",2);


    Object program = p.parse(load("src/song"));


    ArrayList<Triplet> sounds = (ArrayList<Triplet>)eval(program,s);
    int c = 0;



    for(int i = 0; i< sounds.size(); i++)
    {
      if(sounds.get(i) == null)
        c++;
      else
      {
        if(c!=0) {
          c=0;
        }
        //System.out.println(sounds.get(i).first + ", " +sounds.get(i).second +", " + sounds.get(i).third);
      }
    }
    int waited = 0;
    for(Triplet sound: sounds)
    {
      if(sound==null)
      {

        waited++;
        try {
          Thread.sleep(1);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      else {

        Player player = new Player();
        Pattern first = new Pattern((String)sound.first);

        Rhythm second = new Rhythm();
        if (sound.second != null) {
          String[] drum = ((String)sound.second).split(" ");

          for(String d: drum)
            second.addLayer(d);
        }
        int tempo = 500;
        try {
          player.play(first.setTempo(tempo), second.getPattern().setTempo(tempo));
        }
        catch (NullPointerException n)
        {
          System.out.println(first);
          System.out.println(second.getPattern());
        }


        if (sound.third != null) {
          //Clip clip = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
          Clip clip = AudioSystem.getClip();
          clip.open(AudioSystem.getAudioInputStream(new File((String) sound.third)));
          clip.setFramePosition(0);
          FloatControl gainControl =
                  (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
          gainControl.setValue(-10.0f); // Reduce volume by 10 decibels.
          clip.start();
//        try
//        {
//          Thread.sleep(1);
//        }
//        catch (Exception e)
//        {
//          e.printStackTrace();
//        }
        }



      }


    }


    try
    {
      Thread.sleep(10000);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

  }
  
  public static Object eval(Object exp, State state) throws LineUnavailableException, UnsupportedAudioFileException, IOException {

    int milliOn = (int)state.getVariableValue("milliOn");

    if (exp instanceof Integer)
    {

      //the value of an integer is itself
      return exp;
    }
    else if (exp instanceof String)
    {
      String varName = (String)exp;
      if(state.getVariableValue(varName)!=null)
        return state.getVariableValue(varName);
      throw new RuntimeException("cannot find variable:  " + varName);
    }
    else
    {


      //must be a List
      List list = (List)exp;



      if (list.get(0).equals("print"))  // print EXP
      {
        Object argument = list.get(1);
        System.out.println(eval(argument,state));
        return sounds;
      }
      else if (list.get(0).equals("//"))
      {
        return sounds;
      }
      else if (list.get(0).equals("basicSynth"))
      {
        new BasicSynth((Integer)eval(list.get(1),state),(Integer)eval(list.get(2),state));
        return sounds;
      }
      else if (list.get(0).equals("makeSound"))
      {
        String name = (String)eval(list.get(5),state);
        double freq1 = ((Integer)eval(list.get(1),state)).doubleValue();
        double freq2 = ((Integer)eval(list.get(2),state)).doubleValue();
        double amplitude = ((Integer)eval(list.get(3),state)).doubleValue()/1000;
        double millis = ((Integer)eval(list.get(4),state)).doubleValue()/1000;
        AudioSoundsSynth.makeSound(freq1,freq2, amplitude,millis,name);

        return sounds;
      }
      else if (list.get(0).equals("play"))
      {
          String name = (String) eval(list.get(1), state);
          int numTimes = 1;
          int pause = 0;
          addToSounds(milliOn, numTimes, pause, name);
          state.setVariableValue("milliOn", milliOn);
        return sounds;
      }
      else if (list.get(0).equals("playI"))
      {
        String instrument = (list.get(1)).toString();
        String note = (list.get(2)).toString();
        int numTimes = (int) eval(list.get(3),state);
        int pause = (int) eval(list.get(4), state);
        addToSounds(milliOn,instrument,note,numTimes,pause);
      }
      else if (list.get(0).equals("playD"))
      {
        String drum = (list.get(1)).toString();
        int numTimes = (int) eval(list.get(2),state);
        int pause = (int) eval(list.get(3), state);
        addToSounds(milliOn,drum,numTimes,pause);
      }
      else if (list.get(0).equals("changeSpeed"))
      {
        String argument1 = (String)eval(list.get(1),state);
        String argument2 = (String)eval(list.get(2),state);
        int argument3 = (Integer) eval(list.get(3),state)/1000; //factor of


        try
        {
          //source file
          final File file1 = new File("src/"+argument1+".wav");
          //destination file
          final File file2 = new File("src/"+argument2+".wav");
          //audio stream of file1
          final AudioInputStream in1 = AudioSystem.getAudioInputStream(file1);

          //get audio format for targetted sound
          final AudioFormat inFormat = getOutFormat(in1.getFormat(),argument3);
          //get the target file audio stream using file format
          final AudioInputStream in2 = AudioSystem.getAudioInputStream(inFormat, in1);
          //write the audio file in targeted pitch file
          AudioSystem.write(in2, AudioFileFormat.Type.WAVE, file2);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }

        return sounds;
      }
      else if (list.get(0).equals("loop"))
      {
        for (int i = 0; i< (int)eval(list.get(1),state); i++)
          eval(list.get(2),state);
        return sounds;
      }
      else if (list.get(0).equals("list"))
      {
        //state.setVariableValue(list.get(1),); TODO
      }
      else if (list.get(0).equals("wait"))
      {
        state.setVariableValue("milliOn",(int)state.getVariableValue("milliOn")+((int)eval(list.get(1),state)));
        return sounds;

      }
      else if (list.get(0).equals("block"))
      {
        state.setVariableValue((String)eval(list.get(1),state),list.get(2));
      }
      else if (list.get(0).equals("run"))
      {

        eval(state.getVariableValue((String)eval(list.get(1),state)),state);
      }
      else if (list.get(0).equals("randomRange"))
      {
        double d = RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
        return (int)(d*(((int)eval(list.get(2),state)-(int)eval(list.get(1),state))+1) + (int)eval(list.get(1),state));
      }
      else if (list.get(0).equals("if"))
      {
        if((boolean)eval(list.get(1),state))
        {
          eval(list.get(2),state);
        }
        return sounds;
      }
      else if (list.get(0).equals("(")) {
          //TODO
        return eval(list.get(1), state);
      }
      else if (list.get(0).equals("{"))
      {
        int n = 1;
        while(!list.get(n).equals("}")) {
          eval(list.get(n),state);
          n++;
        }
        return sounds;
      }
      else if (list.get(0).equals("\"")) {
        String s = "";
        int n = 1;
        while(!list.get(n).equals("\"")) {
          s+=(String)list.get(n);
          n++;
        }
        return s;
      }


      if (list.get(1).equals("+"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        if(argument1 instanceof String) {
          if (argument2 instanceof Integer)
            return argument1 + Integer.toString((Integer)argument2);
          return argument1 + (String)argument2;
        }
        return (Integer)argument1 + (Integer)argument2;
      }
      else if (list.get(1).equals("-"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (Integer)eval(argument1,state) - (Integer)eval(argument2,state);
      }
      else if (list.get(1).equals("*"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (Integer)eval(argument1,state) * (Integer)eval(argument2,state);
      }
      else if (list.get(1).equals("/"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (Integer)eval(argument1,state) / (Integer)eval(argument2,state);
      }
      else if (list.get(1).equals("%"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (Integer)eval(argument1,state) % (Integer)eval(argument2,state);
      }
      else if (list.get(1).equals("=="))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return eval(argument1,state) == eval(argument2,state);
      }
      else if (list.get(1).equals("<"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (int)eval(argument1,state) < (int)eval(argument2,state);
      }
      else if (list.get(1).equals("<="))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (int)eval(argument1,state) <= (int)eval(argument2,state);
      }
      else if (list.get(1).equals(">"))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (int)eval(argument1,state) > (int)eval(argument2,state);
      }
      else if (list.get(1).equals(">="))  // EXP + EXP
      {
        Object argument1 = eval(list.get(0),state);
        Object argument2 = eval(list.get(2),state);
        return (int)eval(argument1,state) >= (int)eval(argument2,state);
      }
      else if(list.get(1).equals("="))
      {
        state.setVariableValue((String)list.get(0),eval(list.get(2),state));
        return sounds;
      }

      return sounds;
      //throw new RuntimeException("unable to evaluate:  " + exp);
    }
  }

  private static void addToSounds(int milliOn, String instrument, String note, int numTimes, int pause) {
    int tempMilliOn = milliOn;
    if(pause==0)
      pause = 1;
    while(sounds.size()<tempMilliOn+pause*numTimes) {
      sounds.add(null);
    }
    for(int i = 0; i< numTimes;i++) {

      String second;
      String third;
      if(sounds.get(tempMilliOn)==null)
      {
        second=null;
        third=null;
      }
      else {
        second = sounds.get(tempMilliOn).second;
        third = sounds.get(tempMilliOn).third;
      }


      if(sounds.get(tempMilliOn)!=null && sounds.get(tempMilliOn).first!=null && sounds.get(tempMilliOn).first.startsWith("V")){
        String s = sounds.get(tempMilliOn).first;
        int addV = (int)(Math.random()*16);
        for(int j = s.length()-2; j>=0; j--)
        {
          if(s.charAt(j) == 'V' && (j==0 || s.charAt(j-1) != '['))
            addV = Integer.parseInt(s.substring(j + 1, j + 2));

        }
        sounds.set(tempMilliOn,
                new Triplet<>(sounds.get(tempMilliOn).first + " V" + addV + " I[" + instrument + "] " + note
                        , second, third));

      }
      else
        sounds.set(tempMilliOn,
                new Triplet<>("V" + "0" + " I[" + instrument + "] " + note
                        , second, third));
        tempMilliOn+=pause;
    }
  }

  private static void addToSounds(int milliOn, String drum ,int numTimes, int pause) {

    int tempMilliOn = milliOn;
    if(pause==0)
      pause = 1;
    while(sounds.size()<tempMilliOn+pause*numTimes)
      sounds.add(null);
    sounds.add(null);
    for(int i = 0; i< numTimes;i++) {
      while(sounds.get(tempMilliOn)!=null && sounds.get(tempMilliOn).third!=null)
      {
        tempMilliOn++;
      }
      String first;
      String third;
      if(sounds.get(tempMilliOn)==null)
      {
        first=null;
        third=null;
      }
      else {
        first = sounds.get(tempMilliOn).first;
        third = sounds.get(tempMilliOn).third;
      }
      if(sounds.get(tempMilliOn)!=null && sounds.get(tempMilliOn).third!=null)
      {
        sounds.set(tempMilliOn,new Triplet<>(first,sounds.get(tempMilliOn).second+" " + DRUMS.get(drum),third));
      }
      else {
        if(DRUMS.get(drum)==null)
          throw new RuntimeException(drum+ " not a real drum");
        sounds.set(tempMilliOn, new Triplet<>(first, DRUMS.get(drum) + " ", third));
      }


        tempMilliOn+=pause;
    }

  }

  private static void addToSounds(int milliOn, int numTimes, int pause, String soundName) {
    int tempMilliOn = milliOn;
    if(pause==0)
      pause = 1;
    while(sounds.size()<tempMilliOn+pause*numTimes)
      sounds.add(null);
    for(int i = 0; i< numTimes;i++) {
      while(sounds.get(tempMilliOn)!=null && sounds.get(tempMilliOn).third!=null)
      {
        tempMilliOn++;
      }
      String first;
      String second;
      if(sounds.get(tempMilliOn)==null)
      {
        first=null;
        second=null;
      }
      else {
        first = sounds.get(tempMilliOn).first;
        second = sounds.get(tempMilliOn).second;
      }
      sounds.set(tempMilliOn,
              new Triplet<>(first,second, "src/" + soundName + ".wav"));
      tempMilliOn+=pause;
    }
  }

  public static String input()
  {
    return in.nextLine();
  }
  
  public static String load(String fileName)
  {
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null)
      {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
      return sb.toString();
    }
    catch(IOException e)
    {
      throw new RuntimeException(e);
    }

  }

  private static final class RandomNumberGeneratorHolder {
    static final Random randomNumberGenerator = new Random();
  }

  private static AudioFormat getOutFormat (AudioFormat inFormat,int factor){
    int ch = inFormat.getChannels();
    float rate = inFormat.getSampleRate();
    return new AudioFormat(inFormat.getEncoding(), inFormat.getSampleRate()*factor, 16, ch, ch * 2, rate,
            inFormat.isBigEndian());
  }



}