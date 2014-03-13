package org.jboss.forge.speech.command;

import javax.inject.Inject;

import org.jboss.forge.speech.RecognizerCommand;
import org.jboss.forge.speech.SpeechSynthesizer;

public class GreetingCommand implements RecognizerCommand {

   @Inject
   private SpeechSynthesizer speech;

   @Override
   public String getName() {
      return GreetingCommand.class.getSimpleName();
   }

   @Override
   public String[] responsTo() {
      return new String[] {"hello", "good day", "good evening", "greetings"};
   }

   @Override
   public void execute(String match) {
      try
      {
         speech.speak(match + " " + System.getProperty("user.name"));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
