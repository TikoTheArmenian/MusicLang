import java.io.*;

public class Tokenizer
{
  private static final String WHITESPACE = " \n\t\r";
  private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_#";
  private static final String[] TWOCHARACTERNONALPHANUM = new String[]{"==","<=",">=",};
  
  private String text;
  private int index;  //index of char to process next
  
  public Tokenizer(String text)
  {

    this.text = text;
    index = 0;
  }
  
  public String next()
  {

    skipWhitespace();

    
    if (index == text.length())
      return "";  //indicates end of program
    
    String token = "";

    while(text.startsWith("//", index)) {
      skipComment();
    }

    if(text.length()>index+2 && isTwoCharNonAlphaNumeric(text.substring(index,index+2)))
    {
      token = text.substring(index,index+2);
      index+=2;
    }
    else if (isAlphaNum(text.substring(index, index + 1)))
    {
      //consists of a-z, A-Z, 0 - 9, or _.
      //this code assumes any string of such characters is a single token.
      while (index < text.length() && isAlphaNum(text.substring(index, index + 1)))
      {
        token += text.substring(index, index + 1);
        index++;
      }
    }
    else
    {
      //not alphanumeric.  therefore, this symbol is assumed to be a one character token.
      token = text.substring(index, index + 1);
      index++;
    }
    
    skipWhitespace();
    return token;
  }
  
  private void skipWhitespace()
  {
    while (index < text.length() && isWhitespace(text.substring(index, index + 1)))
      index++;
  }
  private void skipComment()
  {
    while (index < text.length() && !isSameLine(text.substring(index, index + 1)))
      index++;
    skipWhitespace();

  }
  
  private boolean isWhitespace(String ch)
  {
    return WHITESPACE.contains(ch);
  }

  private boolean isSameLine(String ch)
  {
    return ch.equals("\n");
  }


  //true if 0-9, a-z, A-Z, or _
  private boolean isAlphaNum(String ch)
  {
    return ALPHANUM.contains(ch);
  }

  private boolean isTwoCharNonAlphaNumeric(String ch)
  {
    for(String s: TWOCHARACTERNONALPHANUM) {
        if(s.equals(ch))
          return true;
    }
    return false;
  }
}
