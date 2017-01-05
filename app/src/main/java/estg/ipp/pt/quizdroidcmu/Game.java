package estg.ipp.pt.quizdroidcmu;

import java.util.ArrayList;

public class Game {
    private int id;
    private Highscore highScore;
    private ArrayList<Integer> questionsId;
    private boolean helpFiftyFifty, helpPhone, helpPublic, helpChange;
    private boolean helpsDisabled, unlimited;

    public Game() { }

    public Game(int id, Highscore highScore, boolean unlimited, boolean helpsDisabled) {
        this.id = id;
        this.highScore = highScore;
        questionsId = new ArrayList<>();
        this.unlimited = unlimited;
        this.helpsDisabled = helpsDisabled;
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

    public void removeQuestionsId(int pos){this.questionsId.remove(pos); }

    public boolean isHelpsDisabled() {
        return helpsDisabled;
    }

    public void setHelpsDisabled(boolean helpsDisabled) {
        this.helpsDisabled = helpsDisabled;
    }

    public boolean isHelpFiftyFifty() {
        return helpFiftyFifty;
    }

    public void setHelpFiftyFiftyUsed() {
        this.helpFiftyFifty = true;
    }

    public boolean isHelpPhone() {
        return helpPhone;
    }

    public void setHelpPhoneUsed() {
        this.helpPhone = true;
    }

    public boolean isHelpPublic() {
        return helpPublic;
    }

    public void setHelpPublicUsed() {
        this.helpPublic = true;
    }

    public boolean isHelpChange() {
        return helpChange;
    }

    public void setHelpChangeUsed() {
        this.helpChange = true;
    }

    public boolean isUnlimited() { return unlimited; }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }
}
