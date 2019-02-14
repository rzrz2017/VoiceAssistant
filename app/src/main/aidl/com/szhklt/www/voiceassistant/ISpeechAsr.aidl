// ISpeechAsr.aidl
package com.szhklt.www.voiceassistant;

// Declare any non-default types here with import statements
import com.szhklt.www.voiceassistant.ISpeechAsrListener;

interface ISpeechAsr {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int startListening(ISpeechAsrListener listener);
    void startSpeaking(java.lang.String text);
}
