package org.jboss.forge.speech;

public interface RecognizerCommand {

   String getName();

   String[] responsTo();

   public void execute(String match);
}
