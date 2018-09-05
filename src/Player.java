import java.util.ArrayList;

public class Player {
    private ArrayList<Card> hand = new ArrayList();
    private volatile int aq = 0;
    
    public Player(Deck a){
        for(int i=0;i<7;i++){
            add(a.deal());
        }
    }
    public void add(Card a){
        hand.add(a);
    }
    public ArrayList getHand(){
        return hand;
    }
    public Card getCard(int i){
        return hand.get(i);
    }
    public int getTotalHandValue(){
        int p = 0;
        for(int i=0;i<hand.size();i++){
            p+=hand.get(i).getValue();
        }
        return p;
    }
    public Card playC(int a){
        Card b = hand.get(a);
        hand.remove(a);
        return b;
    }
    public int cardNum(){
        aq = hand.size();
        return  aq;
    }
    public boolean playable(Card a){
        for(int i=0;i<hand.size();i++){
            if(a.getCV()==hand.get(i).getCV()){
                return true;
            }else if (a.getNum()==hand.get(i).getNum()){
                return true;
            }
        }
        for(int i=0;i<hand.size();i++){
            if (hand.get(i).getCV()==4){
                return true;
            }
        }    
        return false;
    }
}
