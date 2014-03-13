package org.jboss.forge.speech.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.speech.SpeechSynthesizer;

public class SpeakCommand extends AbstractUICommand implements UICommand {

   @Inject
   private SpeechSynthesizer speech;

   @Inject
   @WithAttributes(required = true, label = "What to speak")
   private UIInputMany<String> arguments;

   @Override
   public UICommandMetadata getMetadata(UIContext context) {
      return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Speech"))
            .name("Speech: Speak")
            .description("Allow the computer to speak to output if speech is on");
   }

   @Override
   public boolean isEnabled(UIContext context) {
      return speech.isRunning();
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception {
      builder.add(arguments);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception {
      StringBuilder sb = new StringBuilder();
      for(String word : arguments.getValue()) {
         sb.append(word).append(" ");
      }
      speech.speak(sb.toString());
      return Results.success();
   }
}
