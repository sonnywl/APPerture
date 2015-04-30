package edu.uci.apperture.database;

/**
 * Chat Record that contains the question and response of the user
 * <p/>
 * Created by Sonny on 4/16/2015.
 */
public class Record {
    private final long date;
    private final byte[] image;
    private final String sound;
    private final String question;
    private final String response;

    public Record(long date, String sound, byte[] image, String question, String response) {
        this.date = date;
        this.sound = sound;
        this.image = image;
        this.question = question;
        this.response = response;
    }

    public long getDate() {
        return date;
    }


    public String getSound() {
        return sound;
    }

    public byte[] getImage() {
        return image;
    }

    public String getQuestion() {
        return question;
    }

    public String getResponse() {
        return response;
    }


    public static class Builder {
        private long date;
        private String sound;
        private byte[] image;
        private String question;
        private String response;

        public Builder setDate(long date) {
            this.date = date;
            return this;
        }

        public Builder setSound(String sound) {
            this.sound = sound;
            return this;
        }

        public Builder setImage(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder setQuestion(String question) {
            this.question = question;
            return this;
        }

        public Builder setResponse(String response) {
            this.response = response;
            return this;
        }

        public Record createRecord() {
            return new Record(date, sound, image, question, response);
        }
    }
}
