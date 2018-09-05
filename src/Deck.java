import java.util.ArrayList;


public class Deck {
    private ArrayList<Card> deck = new ArrayList();

    public Deck(){
        for(int i=0;i<2;i++){
            for(int j=0;j<4;j++){
                for(int k=0;k<13;k++){
                    deck.add(new Card(j, k));
                }
            }
            deck.add(new Card(4, 13));
            deck.add(new Card(4, 14));
            deck.add(new Card(4, 13));
            deck.add(new Card(4, 14));
        }
    }
    
    public void shuffle(int a){//fisher-yates shuffle
        if(a<=1){
            return;  
        }
        int rand = (int) ((a-2)*Math.random());
        Card t = deck.get(a-1);
        deck.set(a-1, deck.get(rand));
        deck.set(rand, t);
	shuffle(a-1);
    }
    public Card deal(){
        //method  return the card from top of deck and remove it from the arrayList
        Card t = deck.get(0);
        deck.remove(0);
        return t;
    }
    public void deckAdd(Card a){
        deck.add(a);
    }
    public ArrayList<Card> getDeck(){
        //return the deck as an arraylist, this can be used for printing out the deck
        return deck; 
    }
    public int deckSize(){
        return deck.size();
    }
}

