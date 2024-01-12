package net.vidalibarraquer.daishu.objects;

public class Question {

    private String file, title, exercise, exercise_image, options, answer_image;
    private int answer, answer_selected;

    public Question(String file, String title, String exercise, String exercise_image, String options, int answer, String answer_image) {
        this.file = file;
        this.title = title;
        this.exercise = exercise;
        this.exercise_image = exercise_image;
        this.options = options;
        this.answer = answer;
        this.answer_image = answer_image;
        this.answer_selected = 0;
    }

    public String getTitle() {
        return title;
    }

    public String getExercise() {
        return exercise;
    }

    public String getExercise_image() {
        return exercise_image;
    }

    public String getOptions() {
        return options;
    }

    public String getAnswer_image() {
        return answer_image;
    }

    public int getAnswer() {
        return answer;
    }

    public String getFile() {
        return file;
    }

    public int getAnswer_selected() {
        return answer_selected;
    }

    public void setAnswer_selected(int answer_selected) {
        this.answer_selected = answer_selected;
    }
}
