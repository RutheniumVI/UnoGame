public class Computer1 extends Player{
    public Computer1(Deck a){
        super(a);
    }
    public Card play(Card a){
       
        for(int i=0;i<super.getHand().size();i++){
            if(a.getCV()==super.getCard(i).getCV()){
                return super.playC(i);
            }
        }

        for(int i=0;i<super.getHand().size();i++){
            if(a.getNum()==super.getCard(i).getNum()){
                return super.playC(i);
            }
        }
        for(int i=0;i<super.getHand().size();i++){
            if(super.getCard(i).getCV()==4){
                return super.playC(i);
            }
        }
        return new Card(9, 9);
    }
}
