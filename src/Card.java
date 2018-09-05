public class Card {
    private int colourValue;
    private int num;//this is the value 1-13
    private int value;//point value
    private String imagePath = "";
    
    public Card(int a, int b){
        this.num = b;
        this.colourValue=a;
        this.imagePath = "images/"+a+""+b+".png";
        
        if(b==13||b==14){
            value= 50;
        }else if(b>9){
            value=20;
        }else{
            value=b;
        }
    }
    public String getColour(){
        if(colourValue==0){
            return "Red ";
        }else if(colourValue==1){
            return "Yellow ";
        }else if(colourValue==2){
            return "Green ";
        }
            return "Blue ";
    }
    public String getImagePath(){
        return imagePath;
    }
    public int getCV(){
        return colourValue;//method that returns the suit as a string
    }
    public int getNum(){
        return num;//method that returns the card value
    }
    public int getValue(){
        return value;//method that returns the card value
    }
    @Override
    public String toString(){
        //this wil return the card by its value where 1, 11, 12, 13 it's going to b A,J,K,Q and its suit
        String re = "";
        if(colourValue==0){
            re="Red ";
        }else if(colourValue==1){
            re="Yellow ";
        }else if(colourValue==2){
            re="Green ";
        }else if(colourValue==3){
            re="Blue ";
        }else if(colourValue==4&&num==13){
            return "Wildcard";
        }else if(colourValue==4&&num==14){
            return "Draw 4";
        }
        if(num==10){
            return  re + " skip";
        }else if(num==11){
            return re + " reverse";
        }else if(num==12){
            return re+ " +2";
        }
        return re+" of "+num;
    }
}
