package org.jboss.forge.speech.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.speech.RecognizerCommand;
import org.jboss.forge.speech.SpeechSynthesizer;

@ApplicationScoped
public class ForgeCommand implements RecognizerCommand, UIContextListener {

   @Inject
   private AddonRegistry registry;
   
   private CommandFactory commandFactory;
   private CommandControllerFactory commandControllerFactory;
   
   private UIContext context;
   
   @Inject
   private SpeechSynthesizer speech;
   
   @Override
   public String getName() {
      return ForgeCommand.class.getSimpleName();
   }

   @Override
   public String[] responsTo() {
      if(commandFactory == null) {
         commandFactory = registry.getServices(CommandFactory.class).get();
      }
      if(commandControllerFactory == null) {
         commandControllerFactory = registry.getServices(CommandControllerFactory.class).get();
      }
      return splitCommands(commandFactory.getCommandNames(context).toArray(new String[]{}));
      //return commandFactory.getCommandNames(context).toArray(new String[]{});
   }

   private String[] splitCommands(String[] array) {
      String[] splitted = new String[array.length];
      for(int i = 0; i < array.length; i++) {
         splitted[i] = array[i].replaceAll("-", " ");
      }
      return splitted;
   }

   public String join(String match) {
      return match.replaceAll(" ", "-");
   }
   
   @Override
   public void execute(String match) {
      String commandName = join(match);
      UICommand command = commandFactory.getCommandByName(context, commandName);
      final UIRuntime runtime = (UIRuntime)context.getProvider();
      System.out.println(commandName);

      try {
         speech.speak("executing " + commandName.replaceAll("-", " "));
         CommandController commandController = commandControllerFactory.createController(context, runtime, command);
         commandController.initialize();
         Result result = commandController.execute();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void contextDestroyed(UIContext context) {
      //this.context = null;
   }
   
   @Override
   public void contextInitialized(UIContext context) {
      this.context = context;
   }
}
