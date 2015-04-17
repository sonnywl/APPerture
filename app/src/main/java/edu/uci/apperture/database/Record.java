package edu.uci.apperture.database;

/**
 * Chat Record that contains the question and response of the user
 * <p/>
 * Created by Sonny on 4/16/2015.
 */
public class Record {
    private final int userId;
    private final long date;
    private final int color;
    private final byte[] image;
    private final String sound;
    private final String question;
    private final String response;

    public Record(int userId, long date, int color, String sound, byte[] image, String question, String response) {
        this.userId = userId;
        this.date = date;
        this.color = color;
        this.sound = sound;
        this.image = image;
        this.question = question;
        this.response = response;
    }

    public int getUserId() {
        return userId;
    }

    public long getDate() {
        return date;
    }

    public int getColor() {
        return color;
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
        private int userId;
        private long date;
        private int color;
        private String sound;
        private byte[] image;
        private String question;
        private String response;

        public Builder setUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder setDate(long date) {
            this.date = date;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
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
            return new Record(userId, date, color, sound, image, question, response);
        }
    }
}
