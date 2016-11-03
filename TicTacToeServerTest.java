 
 import javax.swing.JFrame;

 public class TicTacToeServerTest
 {
    public static void main( String args[] )
    {
       TicTacToeServer tttc = new TicTacToeServer();                    // create object of Server
       tttc.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );           // close the frame
       tttc.execute();                                                  // call the execute method
  } }