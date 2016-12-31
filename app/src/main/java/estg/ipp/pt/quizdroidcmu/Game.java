package estg.ipp.pt.quizdroidcmu;

import java.util.ArrayList;

public class Game {
    private int id;
    private Highscore highScore;
    private ArrayList<Integer> questionsId;
    private boolean helpFiftyFifty, helpPhone, helpPublic, helpChange;

    public Game(int id, Highscore score) {
        this.id = id;
        this.highScore = score;
        questionsId = new ArrayList<>();
        helpFiftyFifty = helpPhone = helpPublic = helpChange = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public Highscore getHighScore() {
        return highScore;
    }

    public void setHighScore(Highscore score) {
        this.highScore = score;
    }

    public ArrayList<Integer> getQuestionsId(){ return this.questionsId; }

    public void addQuestionsId(int questionId){ this.questionsId.add(questionId); }

    public void removeQuestionsId(int questionId){this.questionsId.remove(questionId); }

    public boolean isHelpFiftyFifty() {
        return helpFiftyFifty;
    }

    public void setHelpFiftyFifty() {
        this.helpFiftyFifty = true;
    }

    public boolean isHelpPhone() {
        return helpPhone;
    }

    public void setHelpPhone() {
        this.helpPhone = true;
    }

    public boolean isHelpPublic() {
        return helpPublic;
    }

    public void setHelpPublic() {
        this.helpPublic = true;
    }

    public boolean isHelpChange() {
        return helpChange;
    }

    public void setHelpChange() {
        this.helpChange = true;
    }
}
