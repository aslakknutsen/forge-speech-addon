package org.jboss.forge.speech;

import javax.speech.recognition.ResultAdapter;

import org.junit.Ignore;
import org.junit.Test;

import edu.cmu.sphinx.jsgf.JSGFRuleGrammar;
import edu.cmu.sphinx.jsgf.rule.JSGFRule;

public class BaiscSpeechTestCase extends ResultAdapter {

   @Test @Ignore
   public void shouldRead() throws Exception {

      SpeechRecognizer speech = new SpeechRecognizer();
      speech.start();


      JSGFRuleGrammar rules = speech.getGrammar().getRuleGrammar();
      JSGFRule rule = rules.getRule("ForgeCommand");
      System.out.println(rule);

      Thread.sleep(100000);
   }

   @Test @Ignore
   public void shouldSpeak() throws Exception {

      SpeechSynthesizer speech = new SpeechSynthesizer();
      speech.start();
      speech.speak("Hello " + System.getProperty("user.name"));
      speech.stop();
      speech.start();
      speech.speak("Hello " + System.getProperty("user.name"));
      speech.stop();
   }
}
