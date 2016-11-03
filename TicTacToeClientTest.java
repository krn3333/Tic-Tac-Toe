
  import javax.swing.JFrame; 
 
  public class TicTacToeClientTest 
  { 
     public static void main( String args[] ) 
     { 
       TicTacToeClient tttc;                                      // declare client application 
       tttc = new TicTacToeClient( "localhost" );                 // local host 
       tttc.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
    } 
} 

