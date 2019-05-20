package com.szhklt.VoiceAssistant.beam.mqtt;


import android.database.Cursor;
import android.database.CursorWrapper;

class PhoneCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public PhoneCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Phone getPhone(){
        String phoneId =getString(getColumnIndex("phoneid"));
        String topic = getString(getColumnIndex("suTopic"));
        int id = getInt(getColumnIndex("id"));
        String name = getString(getColumnIndex("name"));

        Phone phone = new Phone(name,phoneId,topic);
        return phone;
    }
}
