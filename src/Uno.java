import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Uno extends javax.swing.JFrame {

    private Deck mDeck;
    private Human h;
    private Computer1 c1;
    private Computer2 c2;
    private Card playCard;
    

    private volatile boolean cw = true;
    private boolean firstTurn = true;
    private boolean historyButtonStatus = false;

    private int nextP = 0;
    private int humanPoints = 0;
    private int c1Points = 0;
    private int c2Points = 0;
    private int rightPressed = 0;
    private int roundCounter = 0;

    public Uno() {
        initComponents();
        moveLeft.setEnabled(false);
        moveRight.setEnabled(false);
    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Uno.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void updateMainPane() {
        mainPane.setText("");
        mainPane.insertIcon(createImageIcon(playCard.getImagePath()));
    }

    private int rColour() {
        return (int) (Math.random() * 3);
    }

    private void updateHand() {
        if(h.getHand().size()<7){
            moveRight.setEnabled(false);
        }else{
            moveLeft.setEnabled(true);
        }
        if(rightPressed==0){
            moveLeft.setEnabled(false);
        }else{
            moveLeft.setEnabled(true);
        }
        pane1.setText("");
        pane2.setText("");
        pane3.setText("");
        pane4.setText("");
        pane5.setText("");
        pane6.setText("");
        pane7.setText("");

        if (h.getHand().size() > (7 * rightPressed)) {
            pane1.insertIcon(createImageIcon(h.getCard(7 * rightPressed).getImagePath()));
        }
        if (h.getHand().size() > (7 * rightPressed + 1)) {
            pane2.insertIcon(createImageIcon(h.getCard(7 * rightPressed + 1).getImagePath()));
        }
        if (h.getHand().size() > (7 * rightPressed + 2)) {
            pane3.insertIcon(createImageIcon(h.getCard(7 * rightPressed + 2).getImagePath()));
        }
        if (h.getHand().size() > (7 * rightPressed + 3)) {
            pane4.insertIcon(createImageIcon(h.getCard(7 * rightPressed + 3).getImagePath()));
        }
        if (h.getHand().size() > (7 * rightPressed + 4)) {
            pane5.insertIcon(createImageIcon(h.getCard(7 * rightPressed + 4).getImagePath()));
        }
        if (h.getHand().size() > (7 * rightPressed + 5)) {
            pane6.insertIcon(createImageIcon(h.getCard(7 * rightPressed + 5).getImagePath()));
        }
        if (h.getHand().size() > (7 * rightPressed + 6)) {
            pane7.insertIcon(createImageIcon(h.getCard(7 * rightPressed + 6).getImagePath()));
        }
        if (h.getHand().size() > 7) {
            moveRight.setEnabled(true);
        }

    }

    private void humanPlay(int a) {
        if (checkPlayable(h.getCard(7 * rightPressed + a))) {
            playCard = h.playC(7 * rightPressed + a);
            historyOutput.append("I played "+playCard+"\n");
            mainPane.setText("");
            updateMainPane();
            updateHand();
            nextP = cw == true ? 1 : 2;
            if(playCard.getCV()!=4){
                checkS();
                round();
            }else if(playCard.getCV()==4&&playCard.getNum()==14){
                drawCards(4);
                skipTurn();
            }
        }
    }

    private void round() {//the two ai play
        firstTurn = false;
        statusB(false);
        if (h.cardNum() > 0 && c1.cardNum() > 0 && c2.cardNum() > 0) {
            if (nextP != 0) {
                int delay = 2000;// wait for second
                Timer timer = new Timer(delay, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {//computer 1 plays
                        //action that you want performed 
                        if (nextP == 1) {
                            c1ActionPane.setText("");
                            if (c1.playable(playCard) == false) {//if computer doesn't have card to play
                                c1.add(mDeck.deal());
                                if (c1.playable(playCard)) {
                                    playCard = c1.play(playCard);
                                    c1ActionPane.setText("I play "+playCard);
                                    historyOutput.append("c1 played "+playCard+"\n");
                                    updateMainPane();
                                    nextP = cw == true ? 2 : 0;
                                    checkS();
                                }else{
                                    c1ActionPane.setText("I pass ");
                                    historyOutput.append("c1 passed "+"\n");
                                    nextP = cw == true ? 2 : 0;
                                }
                            } else {
                                playCard = c1.play(playCard);
                                updateMainPane();
                                historyOutput.append("c1 played "+playCard+"\n");
                                c1ActionPane.setText("I play "+playCard);
                                nextP = cw == true ? 2 : 0;
                                checkS();
                            }
                        if(nextP==0){
                            statusB(true);
                            pickCardB.setEnabled(true);
                        }                           
                        }     
                    }
                });
                timer.setRepeats(false);//the timer should only go off once
                timer.start();
                    
                int delay2 = 4000;// wait for second
                Timer timer2 = new Timer(delay2, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        //action that you want performed 
                        if (nextP == 2) {
                            c2ActionPane.setText("");
                            if (c2.playable(playCard) == false) {//if computer doesn't have card to play
                                c2.add(mDeck.deal());
                                if (c2.playable(playCard)) {
                                    playCard = c2.play(playCard);
                                    historyOutput.append("c2 played "+playCard+"\n");
                                    c2ActionPane.setText("I play "+playCard);
                                    updateMainPane();
                                    nextP = cw == true ? 0 : 1;
                                    checkS();
                                }else{
                                    historyOutput.append("c2 passed "+"\n");
                                    c2ActionPane.setText("I pass");
                                    nextP = cw == true ? 0 : 1;
                                }
                            } else {
                                playCard = c2.play(playCard);
                                historyOutput.append("c2 played "+playCard+"\n");
                                c2ActionPane.setText("I play "+playCard);
                                updateMainPane();
                                nextP = cw == true ? 0 : 1;
                                checkS();
                            }
                        if(nextP==0){
                            statusB(true);
                            pickCardB.setEnabled(true);
                        }else if(nextP==1){
                            round();
                        }
                        }
                    }
                });
                timer2.setRepeats(false);//the timer should only go off once
                timer2.start();

            }
        }
    }

    private void checkS() {//method run when special cards  are played
        if (playCard.getNum() == 10) {//skip card
            if(nextP==1){
                c1ActionPane.setText("My turn is skipped");
                historyOutput.append("c1 turn is skipped\n");
            }else if(nextP==2){
                c2ActionPane.setText("My turn is skipped");
                historyOutput.append("c2 turn is skipped\n");
            }
            skipTurn();
        } else if (playCard.getNum() == 11) {//reverse card  
            if(firstTurn==true){
                nextP=2;
                round();
            }else{
                skipTurn(); 
            }
            cw = cw != true;
            if(cw==true){
                directionLabel.setIcon(createImageIcon("images/arrowRight.png"));
            }else{
                directionLabel.setIcon(createImageIcon("images/arrowLeft.png"));
            }
        } else if (playCard.getNum() == 12) {//+2 card
            drawCards(2);
            skipTurn();
        } else if (playCard.getNum() == 13) {//wildcard
            playCard = new Card(rColour(), 13);
            if(firstTurn==true){
                
            }else if(cw==true&&nextP==2){
                c1ActionPane.append(" I choose "+playCard.getColour());
            }else if(cw==true&&nextP==0){
                c2ActionPane.append(" I choose "+playCard.getColour());
            }else if(cw!=true&&nextP==0&&firstTurn!=true){
                c1ActionPane.append(" I choose "+playCard.getColour());
            }else if(cw!=true&&nextP==1){
                c2ActionPane.append(" I choose "+playCard.getColour());
            }
        } else if (playCard.getNum() == 14) {//+4
            playCard = new Card(rColour(), 14);
            if(firstTurn==true){
                
            }else if(cw==true&&nextP==2){
                c1ActionPane.append(" I choose "+playCard.getColour());
            }else if(cw==true&&nextP==0){
                c2ActionPane.append(" I choose "+playCard.getColour());
            }else if(cw!=true&&nextP==0){
                c1ActionPane.append(" I choose "+playCard.getColour());
            }else if(cw!=true&&nextP==1){
                c2ActionPane.append(" I choose "+playCard.getColour());
            }
            drawCards(4);
            skipTurn();
        }
    }

    private boolean checkPlayable(Card a) {//this checks if human payer's card are playable
        if (a.getCV() == 4 && a.getNum() == 13) {
            colourChooser.setVisible(true);
            return true;
        } else if (a.getCV() == 4 && a.getNum() == 14) {
            colourChooser.setVisible(true);
            return true;
        }else if (a.getCV() == playCard.getCV()) {
            return true;
        }else if (a.getNum() == playCard.getNum()) {
            return true;
        }
        System.out.println("Not playable");
        return false;
    }

    private void skipTurn() {
        if (nextP == 0) {
            nextP = cw==true ? 1 : 2;
            round();
        } else if (nextP == 1) {
            nextP = cw==true ? 2 : 0;
        } else {
            nextP = cw==true ? 0 : 1;
        }
    }

    private void drawCards(int n) {
        if(nextP==1){
            c1ActionPane.setText("I draw "+n+"cards");
            historyOutput.append("c1 draws "+n+"cards \n");
        }else if(nextP==2){
            c2ActionPane.setText("I draw "+n+"cards");
            historyOutput.append("c2 draws "+n+"cards \n");
        }
        if (nextP == 0) {
            for (int i = 0; i < n; i++) {
                h.add(mDeck.deal());
            }
            updateHand();
        } else if (nextP == 1) {
            for (int i = 0; i < n; i++) {
                c1.add(mDeck.deal());
            }
            uno1.setText("");
        } else if (nextP == 2) {
            for (int i = 0; i < n; i++) {
                c2.add(mDeck.deal());
            }
            uno2.setText("");
        }
    }
    private void check0(){
        Runnable r = new Runnable() {
            public void run() {// a side thread to keep tracks of things, like changing bavkground colour, or making the bot say uno...
                while(true){
                    if(mDeck.deckSize()<=0){
                        endRound();
                    }
                    if(playCard.getCV()==0){
                        gamePanel.setBackground(new Color(247, 113, 113));
                    }else if(playCard.getCV()==1){
                        gamePanel.setBackground(new Color(255, 247, 145));
                    }else if(playCard.getCV()==2){
                        gamePanel.setBackground(new Color(113, 247, 115));
                    }else if(playCard.getCV()==3){
                        gamePanel.setBackground(new Color(113, 217, 247));
                    }

                    if(h.cardNum()==0||c1.cardNum()==0||c2.cardNum()==0){
                        endRound();
                        break;
                    }
                    if(nextP==0){
                        nextPLabel.setText("you");
                    }else if(nextP==1){
                        nextPLabel.setText("c1");
                    }else{
                        nextPLabel.setText("c2");
                    }
                    if(c1.cardNum()==1){
                        uno1.setText("uno");
                    }else{
                        uno1.setText("");
                    }
                    if(c2.cardNum()==1){
                        uno2.setText("uno");
                    }else{
                        uno2.setText("");
                    }
                }
            }
        };
        new Thread(r).start();
    }
    private void checkDirection(){//puts the proper arrow to represent proper direction
        Runnable r = new Runnable() {
            public void run() {
                while(true){
                    if(cw==true){
                        directionLabel.setIcon(createImageIcon("images/arrowRight.png"));
                    }else{
                        directionLabel.setIcon(createImageIcon("images/arrowLeft.png"));
                    }
                    if(nextP==0){
                        nextPLabel.setText("your turn");
                    }else if(nextP==1){
                        nextPLabel.setText("c1's turn");
                    }else{
                        nextPLabel.setText("c2's turn");  
                    }
                }
            }
        };
        new Thread(r).start();
    }
    private void endScreen(){
        if(humanPoints>=300||c1Points>=300||c2Points>=300){
            if(c1Points<humanPoints&&c1Points<c2Points){
                CardLayout card = (CardLayout) panelM.getLayout();
                card.show(panelM, "c1Won");
            }else if(c2Points<humanPoints&&c2Points<c1Points){
                CardLayout card = (CardLayout) panelM.getLayout();
                card.show(panelM, "c2Won");
            }else if(humanPoints<c1Points&&humanPoints<c2Points){
                CardLayout card = (CardLayout) panelM.getLayout();
                card.show(panelM, "uWon");
            }
        }
    }
    private void endRound() {
        c1roundPoints.setText(String.valueOf(c1.getTotalHandValue()));
        c2roundPoints.setText(String.valueOf(c2.getTotalHandValue()));
        hroundPoints.setText(String.valueOf(h.getTotalHandValue()));
        if(c1.getTotalHandValue()<h.getTotalHandValue()&&c1.getTotalHandValue()<c2.getTotalHandValue()){
                roundWinner.setText("c1");
        }else if(c2.getTotalHandValue()<h.getTotalHandValue()&&c2.getTotalHandValue()<c1.getTotalHandValue()){
                roundWinner.setText("c2");
        }else if(h.getTotalHandValue()<c1.getTotalHandValue()&&h.getTotalHandValue()<c2.getTotalHandValue()){
                roundWinner.setText("you");
        }
        humanPoints += h.getTotalHandValue();
        c1Points += c1.getTotalHandValue();
        c2Points += c2.getTotalHandValue();
        
        CardLayout card = (CardLayout) panelM.getLayout();
        card.show(panelM, "roundPanel");
        moveLeft.setEnabled(false);
        moveRight.setEnabled(false);
        
        endScreen();
        
        c1PL.setText(String.valueOf(c1Points));
        c2PL.setText(String.valueOf(c2Points));
        hPL.setText(String.valueOf(humanPoints));
        
        
    }
    public void statusB(boolean a){//just a method for disabling and enabling all the buttons
        b1.setEnabled(a);
        b2.setEnabled(a);
        b3.setEnabled(a);
        b4.setEnabled(a);
        b5.setEnabled(a);
        b6.setEnabled(a);
        b7.setEnabled(a);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        colourChooser = new javax.swing.JDialog();
        jLabel8 = new javax.swing.JLabel();
        redB = new javax.swing.JButton();
        blueB = new javax.swing.JButton();
        greenB = new javax.swing.JButton();
        yellowB = new javax.swing.JButton();
        historyDialog = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        historyOutput = new javax.swing.JTextArea();
        panelM = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        newGame = new javax.swing.JButton();
        loadGButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        gamePanel = new javax.swing.JPanel();
        mainPane = new javax.swing.JTextPane();
        holder1 = new javax.swing.JPanel();
        b1 = new javax.swing.JToggleButton();
        pane1 = new javax.swing.JTextPane();
        holder2 = new javax.swing.JPanel();
        b2 = new javax.swing.JToggleButton();
        pane2 = new javax.swing.JTextPane();
        holder3 = new javax.swing.JPanel();
        b3 = new javax.swing.JToggleButton();
        pane3 = new javax.swing.JTextPane();
        holder4 = new javax.swing.JPanel();
        b4 = new javax.swing.JToggleButton();
        pane4 = new javax.swing.JTextPane();
        holder5 = new javax.swing.JPanel();
        b5 = new javax.swing.JToggleButton();
        pane5 = new javax.swing.JTextPane();
        holder6 = new javax.swing.JPanel();
        b6 = new javax.swing.JToggleButton();
        pane6 = new javax.swing.JTextPane();
        holder7 = new javax.swing.JPanel();
        b7 = new javax.swing.JToggleButton();
        pane7 = new javax.swing.JTextPane();
        roundLabel = new javax.swing.JLabel();
        moveRight = new javax.swing.JButton();
        moveLeft = new javax.swing.JButton();
        pickCardB = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        c1ActionPane = new javax.swing.JTextArea();
        c2ActionPane = new javax.swing.JTextArea();
        uno1 = new javax.swing.JLabel();
        uno2 = new javax.swing.JLabel();
        directionLabel = new javax.swing.JLabel();
        nextPLabel = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        roundPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        startRoundB = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        c2PL = new javax.swing.JLabel();
        c1PL = new javax.swing.JLabel();
        hPL = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        roundWinner = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        c1roundPoints = new javax.swing.JLabel();
        c2roundPoints = new javax.swing.JLabel();
        hroundPoints = new javax.swing.JLabel();
        c1Won = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        c2Won = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        uWon = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        mainMenu = new javax.swing.JButton();

        colourChooser.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        colourChooser.setAlwaysOnTop(true);
        colourChooser.setMinimumSize(new java.awt.Dimension(410, 180));

        jLabel8.setText("Choose Colour:");

        redB.setText("Red");
        redB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redBActionPerformed(evt);
            }
        });

        blueB.setText("Bue");
        blueB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blueBActionPerformed(evt);
            }
        });

        greenB.setText("Green");
        greenB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                greenBActionPerformed(evt);
            }
        });

        yellowB.setText("Yellow");
        yellowB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yellowBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout colourChooserLayout = new javax.swing.GroupLayout(colourChooser.getContentPane());
        colourChooser.getContentPane().setLayout(colourChooserLayout);
        colourChooserLayout.setHorizontalGroup(
            colourChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colourChooserLayout.createSequentialGroup()
                .addGroup(colourChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(colourChooserLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel8))
                    .addGroup(colourChooserLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(redB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(blueB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(greenB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(yellowB)))
                .addContainerGap(124, Short.MAX_VALUE))
        );
        colourChooserLayout.setVerticalGroup(
            colourChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colourChooserLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel8)
                .addGap(48, 48, 48)
                .addGroup(colourChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(redB)
                    .addComponent(blueB)
                    .addComponent(greenB)
                    .addComponent(yellowB))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        historyDialog.setAlwaysOnTop(true);
        historyDialog.setLocation(new java.awt.Point(150, 200));
        historyDialog.setMinimumSize(new java.awt.Dimension(320, 185));

        historyOutput.setEditable(false);
        historyOutput.setColumns(20);
        historyOutput.setRows(5);
        jScrollPane1.setViewportView(historyOutput);

        javax.swing.GroupLayout historyDialogLayout = new javax.swing.GroupLayout(historyDialog.getContentPane());
        historyDialog.getContentPane().setLayout(historyDialogLayout);
        historyDialogLayout.setHorizontalGroup(
            historyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );
        historyDialogLayout.setVerticalGroup(
            historyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        panelM.setLayout(new java.awt.CardLayout());

        mainPanel.setBackground(new java.awt.Color(190, 0, 2));
        mainPanel.setMinimumSize(new java.awt.Dimension(890, 407));
        mainPanel.setLayout(null);

        newGame.setText("New Game");
        newGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameActionPerformed(evt);
            }
        });
        mainPanel.add(newGame);
        newGame.setBounds(350, 180, 150, 40);

        loadGButton.setText("Load Game");
        loadGButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadGButtonActionPerformed(evt);
            }
        });
        mainPanel.add(loadGButton);
        loadGButton.setBounds(350, 270, 150, 40);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bg.png"))); // NOI18N
        mainPanel.add(jLabel15);
        jLabel15.setBounds(60, 20, 750, 410);

        panelM.add(mainPanel, "mainPanel");

        holder1.setLayout(null);

        b1.setBorderPainted(false);
        b1.setContentAreaFilled(false);
        b1.setFocusPainted(false);
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });
        holder1.add(b1);
        b1.setBounds(0, 0, 90, 140);

        pane1.setEditable(false);
        holder1.add(pane1);
        pane1.setBounds(0, 0, 90, 140);

        holder2.setLayout(null);

        b2.setBorderPainted(false);
        b2.setContentAreaFilled(false);
        b2.setFocusPainted(false);
        b2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b2ActionPerformed(evt);
            }
        });
        holder2.add(b2);
        b2.setBounds(0, 0, 80, 140);

        pane2.setEditable(false);
        holder2.add(pane2);
        pane2.setBounds(0, 0, 90, 150);

        holder3.setLayout(null);

        b3.setBorderPainted(false);
        b3.setContentAreaFilled(false);
        b3.setFocusPainted(false);
        b3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b3ActionPerformed(evt);
            }
        });
        holder3.add(b3);
        b3.setBounds(0, 0, 90, 140);

        pane3.setEditable(false);
        holder3.add(pane3);
        pane3.setBounds(0, 0, 90, 140);

        holder4.setLayout(null);

        b4.setBorderPainted(false);
        b4.setContentAreaFilled(false);
        b4.setFocusPainted(false);
        b4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b4ActionPerformed(evt);
            }
        });
        holder4.add(b4);
        b4.setBounds(0, 0, 90, 140);

        pane4.setEditable(false);
        holder4.add(pane4);
        pane4.setBounds(0, 0, 90, 140);

        holder5.setLayout(null);

        b5.setBorderPainted(false);
        b5.setContentAreaFilled(false);
        b5.setFocusPainted(false);
        b5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b5ActionPerformed(evt);
            }
        });
        holder5.add(b5);
        b5.setBounds(0, 0, 90, 140);

        pane5.setEditable(false);
        holder5.add(pane5);
        pane5.setBounds(0, 0, 90, 140);

        holder6.setLayout(null);

        b6.setBorderPainted(false);
        b6.setContentAreaFilled(false);
        b6.setFocusPainted(false);
        b6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b6ActionPerformed(evt);
            }
        });
        holder6.add(b6);
        b6.setBounds(0, 0, 90, 140);

        pane6.setEditable(false);
        holder6.add(pane6);
        pane6.setBounds(0, 0, 90, 140);

        holder7.setLayout(null);

        b7.setBorderPainted(false);
        b7.setContentAreaFilled(false);
        b7.setFocusPainted(false);
        b7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b7ActionPerformed(evt);
            }
        });
        holder7.add(b7);
        b7.setBounds(0, 0, 90, 140);

        pane7.setEditable(false);
        holder7.add(pane7);
        pane7.setBounds(0, 0, 90, 140);

        roundLabel.setText("Round ");

        moveRight.setText(">");
        moveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRightActionPerformed(evt);
            }
        });

        moveLeft.setText("<");
        moveLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLeftActionPerformed(evt);
            }
        });

        pickCardB.setText("Pick Card");
        pickCardB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickCardBActionPerformed(evt);
            }
        });

        jButton1.setText("Pass");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/robot.png"))); // NOI18N

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/detective.png"))); // NOI18N

        c1ActionPane.setColumns(20);
        c1ActionPane.setRows(5);

        c2ActionPane.setColumns(20);
        c2ActionPane.setRows(5);

        directionLabel.setMaximumSize(new java.awt.Dimension(62, 62));
        directionLabel.setMinimumSize(new java.awt.Dimension(62, 62));
        directionLabel.setPreferredSize(new java.awt.Dimension(62, 62));

        nextPLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jButton3.setText("History");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton3MouseExited(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel16.setText("c1");

        jLabel17.setText("c2");

        javax.swing.GroupLayout gamePanelLayout = new javax.swing.GroupLayout(gamePanel);
        gamePanel.setLayout(gamePanelLayout);
        gamePanelLayout.setHorizontalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gamePanelLayout.createSequentialGroup()
                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(gamePanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(moveLeft)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(gamePanelLayout.createSequentialGroup()
                                        .addComponent(holder1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(holder2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(gamePanelLayout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel16)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(uno1)
                                    .addComponent(holder3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(roundLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(c1ActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addComponent(holder4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(holder5, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(holder6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(holder7, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(moveRight))
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mainPane, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(gamePanelLayout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(directionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(gamePanelLayout.createSequentialGroup()
                                        .addGap(128, 128, 128)
                                        .addComponent(uno2)
                                        .addGap(33, 33, 33)
                                        .addComponent(c2ActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17)
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel9))))))
                    .addGroup(gamePanelLayout.createSequentialGroup()
                        .addGap(402, 402, 402)
                        .addComponent(nextPLabel))
                    .addGroup(gamePanelLayout.createSequentialGroup()
                        .addGap(328, 328, 328)
                        .addComponent(pickCardB)
                        .addGap(10, 10, 10)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        gamePanelLayout.setVerticalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gamePanelLayout.createSequentialGroup()
                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(nextPLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(uno1)
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addComponent(uno2)
                                .addGap(8, 8, 8))
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(gamePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(gamePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addGap(57, 57, 57)))
                                .addComponent(c2ActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(gamePanelLayout.createSequentialGroup()
                                .addComponent(directionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mainPane, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(roundLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(38, 38, 38)))
                        .addComponent(c1ActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)))
                .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(gamePanelLayout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1)
                                .addComponent(pickCardB)
                                .addComponent(jButton3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(holder6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                                .addComponent(holder5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(holder4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(holder3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(holder1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(holder2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(holder7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(45, 45, 45))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gamePanelLayout.createSequentialGroup()
                            .addComponent(moveRight)
                            .addGap(116, 116, 116)))
                    .addGroup(gamePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                        .addComponent(moveLeft)
                        .addGap(119, 119, 119))))
        );

        panelM.add(gamePanel, "gamePanel");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/detective.png"))); // NOI18N
        jLabel2.setText("jLabel2");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/robot.png"))); // NOI18N
        jLabel3.setText("jLabel3");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        jLabel4.setText("jLabel4");

        startRoundB.setText("Start Round");
        startRoundB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startRoundBActionPerformed(evt);
            }
        });

        jLabel5.setText("c2");

        jLabel6.setText("c1");

        jButton2.setText("Save and Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("You");

        c2PL.setText("0 pts");

        c1PL.setText("0 pts");

        hPL.setText("0 pts");

        jLabel18.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 204, 51));
        jLabel18.setText("Winner of Round");

        roundWinner.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel19.setText("Points gained this round");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel20.setText("Total");

        c1roundPoints.setText("0 pts");

        c2roundPoints.setText("0 pts");

        hroundPoints.setText("0 pts");

        javax.swing.GroupLayout roundPanelLayout = new javax.swing.GroupLayout(roundPanel);
        roundPanel.setLayout(roundPanelLayout);
        roundPanelLayout.setHorizontalGroup(
            roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(304, 304, 304))
            .addGroup(roundPanelLayout.createSequentialGroup()
                .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanelLayout.createSequentialGroup()
                                .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(roundPanelLayout.createSequentialGroup()
                                        .addGap(35, 35, 35)
                                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(roundPanelLayout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(c1roundPoints))
                                            .addGroup(roundPanelLayout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(c2roundPoints))
                                            .addGroup(roundPanelLayout.createSequentialGroup()
                                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(hroundPoints)))
                                        .addGap(84, 84, 84)
                                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(c1PL, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(c2PL, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(hPL, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(roundPanelLayout.createSequentialGroup()
                                        .addGap(97, 97, 97)
                                        .addComponent(jLabel19)
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel20)))
                                .addGap(252, 252, 252)
                                .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(startRoundB, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roundWinner)
                                .addGap(237, 237, 237))))
                    .addGroup(roundPanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        roundPanelLayout.setVerticalGroup(
            roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanelLayout.createSequentialGroup()
                .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel18)
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanelLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(startRoundB, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(123, 123, 123)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundPanelLayout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(roundWinner)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE))
                    .addGroup(roundPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6)
                            .addComponent(c1PL)
                            .addComponent(c1roundPoints))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(c2PL)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(c2roundPoints))
                        .addGap(10, 10, 10)))
                .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanelLayout.createSequentialGroup()
                        .addGroup(roundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(hroundPoints)
                            .addComponent(hPL))
                        .addGap(69, 69, 69))))
        );

        panelM.add(roundPanel, "roundPanel");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/detective.png"))); // NOI18N
        jLabel13.setText("jLabel13");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel14.setText("That was easy!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        javax.swing.GroupLayout c1WonLayout = new javax.swing.GroupLayout(c1Won);
        c1Won.setLayout(c1WonLayout);
        c1WonLayout.setHorizontalGroup(
            c1WonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(c1WonLayout.createSequentialGroup()
                .addGroup(c1WonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(c1WonLayout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(c1WonLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jLabel14)))
                .addContainerGap(192, Short.MAX_VALUE))
        );
        c1WonLayout.setVerticalGroup(
            c1WonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(c1WonLayout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(jLabel13)
                .addGap(58, 58, 58)
                .addComponent(jLabel14)
                .addContainerGap(177, Short.MAX_VALUE))
        );

        panelM.add(c1Won, "c1Won");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/robot.png"))); // NOI18N
        jLabel11.setText("jLabel11");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel12.setText("I thank my master for my immense intelligence ");

        javax.swing.GroupLayout c2WonLayout = new javax.swing.GroupLayout(c2Won);
        c2Won.setLayout(c2WonLayout);
        c2WonLayout.setHorizontalGroup(
            c2WonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(c2WonLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );
        c2WonLayout.setVerticalGroup(
            c2WonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(c2WonLayout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addGroup(c2WonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
        );

        panelM.add(c2Won, "c2Won");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Good Job! What took you so long to beat these badly programmed AI?");

        javax.swing.GroupLayout uWonLayout = new javax.swing.GroupLayout(uWon);
        uWon.setLayout(uWonLayout);
        uWonLayout.setHorizontalGroup(
            uWonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uWonLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );
        uWonLayout.setVerticalGroup(
            uWonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uWonLayout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(278, Short.MAX_VALUE))
        );

        panelM.add(uWon, "uWon");

        mainMenu.setText("Main Menu");
        mainMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainMenuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(mainMenu)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainMenu)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(panelM, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameActionPerformed
        CardLayout card = (CardLayout) panelM.getLayout();
        card.show(panelM, "roundPanel");
        humanPoints = 0;
        c1Points = 0;
        c2Points = 0;
    }//GEN-LAST:event_newGameActionPerformed

    private void loadGButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadGButtonActionPerformed
        try {
            BufferedReader input = new BufferedReader(new FileReader("/LoadFile.txt"));
            String line = input.readLine();
            StringTokenizer t = new StringTokenizer(line, ",");
            humanPoints = Integer.parseInt(t.nextToken());
            c1Points = Integer.parseInt(t.nextToken());
            c2Points = Integer.parseInt(t.nextToken());
            roundCounter = Integer.parseInt(t.nextToken());
            
            CardLayout card = (CardLayout) panelM.getLayout();
            card.show(panelM, "roundPanel");
            c2PL.setText(String.valueOf(c2Points));
            c1PL.setText(String.valueOf(c1Points));
            hPL.setText(String.valueOf(humanPoints));            
        } catch (IOException ex) { }
        
    }//GEN-LAST:event_loadGButtonActionPerformed

    private void startRoundBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startRoundBActionPerformed
        roundCounter++;
        mDeck = new Deck();
        historyOutput.setText("");
        mDeck.shuffle(mDeck.getDeck().size());
        rightPressed = 0;
        h = new Human(mDeck);
        c1 = new Computer1(mDeck);
        c2 = new Computer2(mDeck);
        playCard = mDeck.deal();
        nextP = 0;
        cw = true;
        firstTurn = true;
        directionLabel.setIcon(createImageIcon("images/arrowRight.png"));
        updateHand();
        updateMainPane();
        c1ActionPane.setText("");
        c2ActionPane.setText("");
        historyOutput.setText("Deck plays "+playCard+"\n");
        roundLabel.setText("Round "+roundCounter);
        CardLayout card = (CardLayout) panelM.getLayout();
        card.show(panelM, "gamePanel");
        checkS();
        check0();
        statusB(true);
        
    }//GEN-LAST:event_startRoundBActionPerformed

    private void redBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redBActionPerformed
        playCard = new Card(0, playCard.getNum());
        colourChooser.setVisible(false);
        round();
    }//GEN-LAST:event_redBActionPerformed

    private void blueBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blueBActionPerformed
        playCard = new Card(3, playCard.getNum());
        colourChooser.setVisible(false);
        round();
    }//GEN-LAST:event_blueBActionPerformed

    private void greenBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greenBActionPerformed
        playCard = new Card(2, playCard.getNum());
        colourChooser.setVisible(false);
        round();
    }//GEN-LAST:event_greenBActionPerformed

    private void yellowBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yellowBActionPerformed
        playCard = new Card(1, playCard.getNum());
        colourChooser.setVisible(false);
        round();
    }//GEN-LAST:event_yellowBActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        nextP = cw == true ? 1 : 2;
        round();
        historyOutput.setText("I passed"+"\n");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void pickCardBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickCardBActionPerformed
        h.add(mDeck.deal());
        updateHand();
        pickCardB.setEnabled(false);
    }//GEN-LAST:event_pickCardBActionPerformed

    private void moveLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveLeftActionPerformed
        rightPressed--;
        updateHand();
    }//GEN-LAST:event_moveLeftActionPerformed

    private void moveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveRightActionPerformed
        rightPressed++;
        updateHand();
        moveLeft.setEnabled(true);
    }//GEN-LAST:event_moveRightActionPerformed

    private void b7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b7ActionPerformed
        humanPlay(6);
    }//GEN-LAST:event_b7ActionPerformed

    private void b6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b6ActionPerformed
        humanPlay(5);
    }//GEN-LAST:event_b6ActionPerformed

    private void b5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b5ActionPerformed
        humanPlay(4);
    }//GEN-LAST:event_b5ActionPerformed

    private void b4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b4ActionPerformed
        humanPlay(3);
    }//GEN-LAST:event_b4ActionPerformed

    private void b3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b3ActionPerformed
        humanPlay(2);
    }//GEN-LAST:event_b3ActionPerformed

    private void b2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b2ActionPerformed
        humanPlay(1);
    }//GEN-LAST:event_b2ActionPerformed

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b1ActionPerformed
        humanPlay(0);
    }//GEN-LAST:event_b1ActionPerformed

    private void mainMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainMenuActionPerformed
        CardLayout card = (CardLayout) panelM.getLayout();
        card.show(panelM, "mainPanel");        // TODO add your handling code here:
    }//GEN-LAST:event_mainMenuActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    try {
            PrintWriter fileOutput = new PrintWriter(new FileWriter("src/culminating/LoadFile.txt", false));
            fileOutput.print(humanPoints+",");
            fileOutput.print(c1Points+",");
            fileOutput.print(c2Points+",");
            fileOutput.print(roundCounter);
            fileOutput.close();
            System.exit(0);
    } catch (IOException ex) { }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseEntered
        historyDialog.setVisible(true);
    }//GEN-LAST:event_jButton3MouseEntered

    private void jButton3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseExited
        historyDialog.setVisible(false);
    }//GEN-LAST:event_jButton3MouseExited

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(historyButtonStatus==false){
            historyDialog.setVisible(true);
        }else{
            historyDialog.setVisible(true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Uno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Uno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Uno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Uno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
      

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Uno().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton b1;
    private javax.swing.JToggleButton b2;
    private javax.swing.JToggleButton b3;
    private javax.swing.JToggleButton b4;
    private javax.swing.JToggleButton b5;
    private javax.swing.JToggleButton b6;
    private javax.swing.JToggleButton b7;
    private javax.swing.JButton blueB;
    private javax.swing.JTextArea c1ActionPane;
    private javax.swing.JLabel c1PL;
    private javax.swing.JPanel c1Won;
    private javax.swing.JLabel c1roundPoints;
    private javax.swing.JTextArea c2ActionPane;
    private javax.swing.JLabel c2PL;
    private javax.swing.JPanel c2Won;
    private javax.swing.JLabel c2roundPoints;
    private javax.swing.JDialog colourChooser;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JPanel gamePanel;
    private javax.swing.JButton greenB;
    private javax.swing.JLabel hPL;
    private javax.swing.JDialog historyDialog;
    private javax.swing.JTextArea historyOutput;
    private javax.swing.JPanel holder1;
    private javax.swing.JPanel holder2;
    private javax.swing.JPanel holder3;
    private javax.swing.JPanel holder4;
    private javax.swing.JPanel holder5;
    private javax.swing.JPanel holder6;
    private javax.swing.JPanel holder7;
    private javax.swing.JLabel hroundPoints;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadGButton;
    private javax.swing.JButton mainMenu;
    private javax.swing.JTextPane mainPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton moveLeft;
    private javax.swing.JButton moveRight;
    private javax.swing.JButton newGame;
    private javax.swing.JLabel nextPLabel;
    private javax.swing.JTextPane pane1;
    private javax.swing.JTextPane pane2;
    private javax.swing.JTextPane pane3;
    private javax.swing.JTextPane pane4;
    private javax.swing.JTextPane pane5;
    private javax.swing.JTextPane pane6;
    private javax.swing.JTextPane pane7;
    private javax.swing.JPanel panelM;
    private javax.swing.JButton pickCardB;
    private javax.swing.JButton redB;
    private javax.swing.JLabel roundLabel;
    private javax.swing.JPanel roundPanel;
    private javax.swing.JLabel roundWinner;
    private javax.swing.JButton startRoundB;
    private javax.swing.JPanel uWon;
    private javax.swing.JLabel uno1;
    private javax.swing.JLabel uno2;
    private javax.swing.JButton yellowB;
    // End of variables declaration//GEN-END:variables
}
