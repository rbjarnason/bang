// copyright (c) 1998 Róbert Viðar Bjarnason
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to
// the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

package org.bang.media;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;

import org.bang.Bang;

public class BangVoice {

    static RuleGrammar ruleGrammar;
    static DictationGrammar dictationGrammar;
    static Recognizer recognizer;
    static Synthesizer synthesizer;
    static ResourceBundle resources;
    static Bang thisBang;
    //
    // This is the listener for rule grammar results.  The
    // resultAccepted method is called when the user issues a command.
    // We then request the tags that we associated with the grammar in
    // hello.gram, and take an action based on the tag.  Using tags
    // rather than looking directly at what the user said means we can
    // change the grammar without having to change our code.
    //
    static ResultListener ruleListener = new ResultAdapter() {

        // accepted result
        public void resultAccepted(ResultEvent e) {
            try {

                // get the result
                FinalRuleResult result = (FinalRuleResult) e.getSource();
                String tags[] = result.getTags();
                
                if (tags[0].equals("lookright")) {
		       thisBang.viewer.turnRight();
                
                } else if (tags[0].equals("begin")) {
                    speak("listening");
               	    thisBang.textInput.setText("\"");        
                    ruleGrammar.setEnabled(false);
                    ruleGrammar.setEnabled("<stop>", true);
                    dictationGrammar.setEnabled(true);
                    recognizer.commitChanges();

                } else if (tags[0].equals("emote")) {
                    speak("listening closely");
               	    thisBang.textInput.setText("emote ");        
                    ruleGrammar.setEnabled(false);
                    ruleGrammar.setEnabled("<stop>", true);
                    dictationGrammar.setEnabled(true);
                    recognizer.commitChanges();

                    
                // the user has said "that's all"
                } else if (tags[0].equals("stop")) {
                    dictationGrammar.setEnabled(false);
                    ruleGrammar.setEnabled(true);
                    recognizer.commitChanges();                             
             	    String theText = (thisBang.textInput.getText());
               	    thisBang.theMooConnection.writeToSocket(theText+"\n");
                    thisBang.textInput.setText("");              
               
                } else if (tags[0].equals("lookleft")) {
                    thisBang.viewer.turnLeft();
                
                } else if (tags[0].equals("left")) {
		   if (thisBang.viewer.goTurnRunnable == null) 
			{
   	                 thisBang.viewer.goTurn(1);
   	                }
   	           else { thisBang.viewer.turnDirection = 1; }

                } else if (tags[0].equals("right")) {
		   if (thisBang.viewer.goTurnRunnable == null) 
			{
   	                 thisBang.viewer.goTurn(-1);
   	                }
   	           else { thisBang.viewer.turnDirection = -1; }
              
                
                } else if (tags[0].equals("run")) {
		   if (thisBang.viewer.goForwardRunnable == null) 
			{
   	                 thisBang.viewer.goForward(50,-1);
   	                }
   	           else { thisBang.viewer.runSpeed = 50; }
                
                } else if (tags[0].equals("forward")) {
		   if (thisBang.viewer.goForwardRunnable == null) 
			{
   	                 thisBang.viewer.goForward(550,-1);
   	                 }
                
                } else if (tags[0].equals("back")) {
		   if (thisBang.viewer.goForwardRunnable == null) 
			{
   	                 thisBang.viewer.goForward(550,1);
   	                 }
		   else { thisBang.viewer.runDirection=1; }
                
                } else if (tags[0].equals("halt")) {
		   if (thisBang.viewer.goForwardRunnable != null) { 
                    thisBang.viewer.goForward = false;
                    thisBang.viewer.goForwardRunnable.stop();
                    thisBang.viewer.goForwardRunnable = null; }
		   if (thisBang.viewer.goTurnRunnable != null) { 
                    thisBang.viewer.goTurn = false;
                    thisBang.viewer.goTurnRunnable.stop();
                    thisBang.viewer.goTurnRunnable = null;
                     }
                
                } else if (tags[0].equals("bye")) {
                    speak(resources.getString("bye"));
                //    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
                //    System.exit(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // rejected result - say "eh?" etc.
        int i = 0;
        String eh[] = null;
        public void resultRejected(ResultEvent e) {
         /*   if (eh==null) {
                String s = resources.getString("eh");
                StringTokenizer t = new StringTokenizer(s);
                int n = t.countTokens();
                eh = new String[n];
                for (int i=0; i<n; i++)
                    eh[i] = t.nextToken();
            }*/
//            speak(eh[(i++)%eh.length]);
        }
    };


    //
    // This is the listener for dictation results.  The resultUpdated
    // method is called for every recognized token.  The
    // resultAccepted method is called when the dictation result
    // completes, which in this application occurs when the user says
    // "that's all".
    //
    static ResultListener dictationListener = new ResultAdapter() {

	int n = 0;

        public synchronized void resultUpdated(ResultEvent e) {
            Result result = (Result) e.getSource();
            for (int i=n; i<result.numTokens(); i++) {
                  thisBang.textInput.setText(thisBang.textInput.getText()+" "+result.getBestToken(i).getSpokenText());
	    }
            n = result.numTokens();
        }

        public void resultAccepted(ResultEvent e) {
            speak("Thank you");
        }
    };


    //
    // Audio listener prints out audio levels to help diagnose problems.
    //
    static RecognizerAudioListener audioListener =new RecognizerAudioAdapter(){
        public void audioLevel(RecognizerAudioEvent e) {
        }
    };

    //
    // Here's a method to say something. If the synthesizer isn't
    // available, we just print the message.
    //
    static void speak(String s) {
        if (synthesizer!=null) {
            try {
                synthesizer.speak(s, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            System.out.println(s);
    }

    public void VoiceBang() {}

    public synchronized void stop() { 
    	try { synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY); }
    	catch (Exception e) {}
		}

    public void doVoice(Bang theBang) {

	this.thisBang = theBang;

        try {

	    System.out.println("Starting JavaSpeech");
            System.out.println("locale is " + Locale.getDefault());
            resources = ResourceBundle.getBundle("res");

            // create a recognizer matching default locale, add audio listener
            recognizer = Central.createRecognizer(null);
            recognizer.allocate();
            recognizer.getAudioManager().addAudioListener(audioListener);

            // create dictation grammar
            dictationGrammar = recognizer.getDictationGrammar(null);
            dictationGrammar.addResultListener(dictationListener);
            
            // create a rule grammar, activate it
            String grammarName = resources.getString("grammar");
            Reader reader = new FileReader(grammarName);
            ruleGrammar = recognizer.loadJSGF(reader);
            ruleGrammar.addResultListener(ruleListener);
            ruleGrammar.setEnabled(true);
        
            // commit new grammars, start recognizer
            recognizer.commitChanges();
            recognizer.requestFocus();
            recognizer.resume();
	
	 SynthesizerModeDesc required = new SynthesizerModeDesc();
                  Voice voice = new Voice(null, Voice.GENDER_FEMALE, Voice.AGE_TEENAGER, null);
                  required.addVoice(voice);

            // create a synthesizer, speak a greeting
            synthesizer = Central.createSynthesizer(required);
            
            if (synthesizer!=null) synthesizer.allocate();
            speak(resources.getString("greeting"));
        } catch (Exception e) {

            e.printStackTrace();
            System.exit(-1);

        }
    }

}

