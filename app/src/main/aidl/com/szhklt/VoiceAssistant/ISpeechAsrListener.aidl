// ISpeechAsrListener.aidl
package com.szhklt.VoiceAssistant;

// Declare any non-default types here with import statements

interface ISpeechAsrListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean onResult(java.lang.String result);
}
