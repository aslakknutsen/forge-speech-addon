package org.jboss.forge.speech;

import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

@ApplicationScoped
public class SpeechSynthesizer {

   private Synthesizer synthesizer;
   private Voice voice;
   private boolean running = false;

   private void init() throws Exception {
      if(synthesizer == null) {
         String voiceName = "kevin16";

         System.setProperty("freetts.voices",
               "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

         SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.US);
         Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

         synthesizer = Central.createSynthesizer(desc);
         SynthesizerModeDesc smd = (SynthesizerModeDesc) synthesizer
               .getEngineModeDesc();
         Voice[] voices = smd.getVoices();
         voice = null;
         for (int i = 0; i < voices.length; i++) {
            if (voices[i].getName().equals(voiceName)) {
               voice = voices[i];
               break;
            }
         }
         if(voice == null) {
            throw new RuntimeException("No voice with name " + voiceName + " found");
         }

         synthesizer.allocate();
         synthesizer.getSynthesizerProperties().setVoice(voice);
      }
   }

   public boolean start() throws Exception {
      init();
      if(!running) {
         synthesizer.resume();
         running = true;
      }
      return true;
   }

   public boolean stop() throws Exception {
      if(running) {
         synthesizer.cancel();
         //synthesizer.deallocate();
         running = false;
      }
      return true;
   }

   public void speak(String text) throws Exception {
      if(running) {
         synthesizer.speakPlainText(text, null);
         synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
      }
   }

   public boolean isRunning() {
      return running;
   }
}
