package org.jboss.forge.speech;

public interface RecognizerCommand {

   String getName();

   String[] respondsTo();

   public void execute(String match);
}
