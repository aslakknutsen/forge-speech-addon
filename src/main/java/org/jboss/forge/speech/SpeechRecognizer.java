package org.jboss.forge.speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammar;
import edu.cmu.sphinx.jsgf.rule.JSGFRule;
import edu.cmu.sphinx.jsgf.rule.JSGFRuleAlternatives;
import edu.cmu.sphinx.jsgf.rule.JSGFRuleSequence;
import edu.cmu.sphinx.jsgf.rule.JSGFRuleToken;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

@ApplicationScoped
public class SpeechRecognizer {

   private ConfigurationManager cm = null;
   private JSGFGrammar grammar;
   private Recognizer recognizer;
   private Microphone microphone;

   private ExecutorService service;

   private boolean running = false;

   @Inject
   private Instance<RecognizerCommand> commands;

   private Map<String, String> commandTokenMap = new HashMap<>();

   private void init() throws Exception {
      if(cm == null) {
         service = Executors.newSingleThreadExecutor();
         cm = new ConfigurationManager(getClass().getResource("/forge/forge.config.xml"));
         recognizer = (Recognizer) cm.lookup("recognizer");
         grammar = (JSGFGrammar)cm.lookup("jsgfGrammar");
         microphone = (Microphone) cm.lookup("microphone");
      }
   }

   JSGFGrammar getGrammar() {
      return grammar;
   }

   private void constructGrammar() {
      if(commands == null) {
         return;
      }
      JSGFRuleGrammar rules = grammar.getRuleGrammar();
      for(RecognizerCommand command : commands) {
         List<JSGFRule> tokens = new ArrayList<>();
         for(String token : command.respondsTo()) {
            commandTokenMap.put(token, command.getName());
            JSGFRule rule;
            if(token.contains(" ")) {
               List<JSGFRule> subTokens = new ArrayList<>();
               for(String subToken : token.split(" ")) {
                  subTokens.add(new JSGFRuleToken(subToken));
               }
               rule = new JSGFRuleSequence(subTokens);
            }
            else {
               rule = new JSGFRuleToken(token);
            }
            tokens.add(rule);
          }
          rules.setRule(command.getName(), new JSGFRuleAlternatives(tokens), true);
      }
      try {
         grammar.commitChanges();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private RecognizerCommand findCommand(String match) {
      if(commands == null) {
         return null;
      }
      String commandName = commandTokenMap.get(match);
      if(commandName == null) {
         return null;
      }
      for(RecognizerCommand command : commands) {
         if(command.getName().equals(commandName)) {
            return command;
         }
      }
      return null;
   }

   public boolean start() throws Exception {
      if(running) {
         return true;
      }
      init();

      if (!microphone.startRecording()) {
         stop();
         return false;
      } else {
         running = true;
         recognizer.allocate();
      }
      constructGrammar();

      service.submit(new Callable<Void>() {
         @Override
         public Void call() throws Exception {
            while(running) {
               Result result = recognizer.recognize();
               if(result == null) {
                  continue;
               }
               String best = result.getBestFinalResultNoFiller();
               if("".equals(best)) {
                  continue;
               }
               //System.out.println(best);
               RecognizerCommand command = findCommand(best);
               if(command != null) {
                  command.execute(best);
                  microphone.clear();
               }
            }
            return null;
         }
      });
      return true;
   }

   public boolean stop() {
      running = false;
      try {
         service.awaitTermination(1, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      if(recognizer != null) {
         recognizer.deallocate();
      }
      if(microphone != null) {
         microphone.clear();
         microphone.stopRecording();
      }
      return true;
   }

   public boolean isRunning() {
      return running;
   }
}
