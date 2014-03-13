package org.jboss.forge.speech.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.speech.SpeechRecognizer;

public class VoiceOffCommand extends AbstractUICommand implements UICommand {

   @Inject
   private SpeechRecognizer speech;

   @Override
   public UICommandMetadata getMetadata(UIContext context) {
      return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Speech"))
            .name("Voice: Off")
            .description("Deactivate speech control");
   }

   @Override
   public boolean isEnabled(UIContext context) {
      return speech.isRunning();
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception {

   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception {
      speech.stop();
      return Results.success();
   }
}
