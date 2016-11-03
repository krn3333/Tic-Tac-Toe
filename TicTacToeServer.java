/** 
 * TicTacToeClient.java 
 * 
 * Version: TicTacToeClient.java v 1.0	2015/05/11   3:34 PM
 * 
 * Revisions: Initial Revision 
 * 
 * @author  Karan Chauhan
 */

import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.*;

/**
 * This class implements the server side of the game
 */


 public class TicTacToeServer extends JFrame
 {
	 private  int x,y,z;
	 
		private String grid[] = new String[ 9 ];                                 // Tic Tac Toe board having 9 boxes
		private JTextArea o_area;                                                // defines the text area
		private Clients[] pl;                                                     
		private ServerSocket ss;                                               // declare the serversocket
		
		private int curr; 
	    private Lock lock;                                                        // declare the lock
		private Condition next_chance;                                           // to wait for other player
		private Condition next_connected;                                       // to wait for other player's turn
		
		
		private final static int first_player = 0;                              // initialize player 1
		private final static int second_player = 1;                            // initialize player 2
		private final static String[] sign = { "X", "O" };
		private ExecutorService run; 
   
		public TicTacToeServer()
		{
			run = Executors.newFixedThreadPool( 2 );
			lock = new ReentrantLock(); 

			next_chance = lock.newCondition();
			next_connected = lock.newCondition();

			for ( int i = 0; i < 9; i++ )
				grid[ i ] = new String( " " );                               // enter string into grid
			pl = new Clients[ 2 ];                                       // decide the number of players
			curr = first_player;                                          // determine the current player
			try
			{
				ss = new ServerSocket( 12345, 2 );                        // establish connection
			} 
			catch ( IOException ioException )
			{
				ioException.printStackTrace();
				System.exit( 1 );
			} 

			grid[0]="a";                                                  // set values to all the boxes of grid
			grid[1]="b";
			grid[2]="c";
			grid[3]="d";
			grid[4]="e";
			grid[5]="f";
			grid[6]="g";
			grid[7]="h";
			grid[8]="i";    
			o_area = new JTextArea();                                   // new Text area
			add( o_area, BorderLayout.CENTER );
			o_area.setText( "Waiting for connections....\n" );
			setSize( 300, 300 );                                        // set size of the grid
			setVisible( true ); 
		}  

          public void msgbox(String s){
       	   JOptionPane.showMessageDialog(null, s);
       	}
          
          public void execute()
      	{
      		for ( int i = 0; i < pl.length; i++ )              
      		{
      			try 
      			{
      				pl[ i ] = new Clients( ss.accept(), i );             // accept the connection        
      				run.execute( pl[ i ] );
      			} 
      			catch ( IOException ioException )
      			{
      				ioException.printStackTrace();
      				System.exit( 1 );
      			} 
      		} 
      		lock.lock();
      		try
      		{
      			pl[ first_player ].suspend( false );       
      			next_chance.signal(); 
      		} 
      		finally
      		{
      			lock.unlock();                                      // unlock game after notifying player 1
      		} 
      	} 
          

          /**
      	 * This method prints the respective messages to be displayed
      	 * 
      	 * @param   msg      message to be displayed
      	 * 
      	 */
      	private void displayMessage( final String msg )
      	{
      		SwingUtilities.invokeLater(
      				new Runnable()
      				{
      					public void run()             {
      						o_area.append( msg ); 
      					} 
      				} 
      				); 
      	} 

    
      	/**
    	 * This method determines if the move is valid
    	 * 
    	 * @param   place      location  to be notified
    	 * @param   pl1        parameter to compare
    	 * 
    	 */
    	public boolean verify( int place, int pl1 )                        // determine if move is valid
    	{

    		while ( pl1 != curr )                                             // while not current player, must wait for turn
    		{
    			lock.lock();                                             // lock game to wait for other player to go

    			try
    			{
    				next_connected.await();                                 // wait for other player's turn
    			} 
    			catch ( InterruptedException exception )
    			{
    				exception.printStackTrace();
    			} 
    			finally
    			{
    				lock.unlock();                                         // unlock game after waiting
    			} 
    		}  


    		if ( !isFull( place ) )
    		{
    			if(winCondition())
    			{
    				return false;
    			} 
    			grid[ place ] = sign[ curr ]; 

    			pl[curr].move(place);

    			curr = ( curr + 1 ) % 2;                                      // change player after each move


    			pl[ curr ].playerMovement( place );                     // let new current player know that move occurred

    			lock.lock();                                                // lock game and notify other player to go

    			try 
    			{
    				next_connected.signal();                             // signal other player to continue
    			}
    			finally 
    			{
    				if(!winCondition())
    					lock.unlock();      
    				else
 
            { 	msgbox("GAME OVER! "+ grid[x]+ " won");
                 pl[curr].fm.format("GAME OVER! %s won\n",grid[x]);
                 pl[curr].fm.flush();
                lock.lock();

                        } 
         
} 
             return true; 
      } 
      else
          return false;
   }

    
    boolean isDraw(String[] board){
    	boolean check=true;
 	   for (int i = 0; i < board.length; i++) {
			if(board[i]=="X" || board[i]=="O"){
				check=false;
			}else{
				return true;
			}
		}
	return check;
 	   
    }
    
    
	/**
	 * This method determines whether location is occupied
	 * 
	 * @param    location     position in the grid
	 * 
	 */

	public boolean isFull( int location )
	{
		if ( grid[ location ].equals( sign[ first_player ] ) ||
				grid [ location ].equals( sign[ second_player ] ) ){
			pl[curr].fm.format("Game over : Draw\n",grid[x]);
			return true;}                                                     // location is occupied
		else
			return false;
	} 

    
    public boolean winCondition()
	{

		int x=0;
		int y=1;
		int z=2;

		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))                 // check if the win condition is satisfied for all possible cases
			return true;
		x=0;y=3;z=6;                                                          // keep on changing the value of x,y and z
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;
		x=0;y=4;z=8;
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;
		x=1;y=4;z=7;
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;
		x=2;y=5;z=8;
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;
		x=3;y=4;z=5;
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;
		x=6;y=7;z=8;
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;
		x=2;y=4;z=6;
		if(grid[x].equals(grid[y])&&grid[x].equals(grid[z]))
			return true;                                                         

		return false;                                                      // return false if the game is not won
	} 

    /**
	 * This is private inner class Player manages each Player as a runnable
	 */

	private class Clients implements Runnable
	{
		private Socket ss1; 
		private Scanner input; 
		private Formatter fm;
		private int n; 
		private String set; 
		private boolean end = true;

		public Clients( Socket so, int nm )
		{
			n = nm;
			set = sign[ n ]; 
			ss1 = so; 
			try 
			{
				input = new Scanner( ss1.getInputStream() );    
				fm = new Formatter( ss1.getOutputStream() );
			} 
			catch ( IOException ioException )
			{ 
				ioException.printStackTrace();
				System.exit( 1 );
			} 
		}

		/**
		 * This method checks if the move is complete or not
		 * 
		 * @param loc   position of the move
		 * 
		 */

		 public void move(int loc)
		{
			 fm.format("Your move is done"+"\n");
			 fm.format("%d\n",loc);
			 fm.flush();
		}
		 
		 /**
			 * This method sends message that other player moved
			 * 
			 * @param location   position of the movement
			 * 
			 */
		 
		 public void playerMovement( int location )
		 {
			 fm.format( "Opponent moved\n" );                       
			 fm.format( "%d\n", location );                                        // send location of move
			 fm.flush();                                                          // flush output                            
		 } 

      public void run()
      {
        
       try
       {
           displayMessage( "Player " + set + " connected\n" );   // send client its mark (X or O), process messages from client
           fm.format( "%s\n", set );                                  // send player's mark
           fm.flush();                                                  // flush output                     

         if ( n == first_player )
           {
             fm.format( "%s\n%s", "Player X connected",
                "Waiting for other player\n" );             
             fm.flush();                                                      // flush output               

           lock.lock();                                                        // lock game to wait for second player

            try
            {
             while( end )
              {
                next_chance.await();                                          // wait for player O
              }
           } 
            catch ( InterruptedException exception )
           {
             exception.printStackTrace();
            }
            finally
           {
              lock.unlock();                                         // unlock game after second player
            }

                                                                     
              fm.format( "second player connected...\n" );   // send message that other player connected
              fm.flush();                                             // flush output                         
            }                                                           // end if
            else
            {
               fm.format( "Player O connected, please wait\n" );
               fm.flush();                       
            } 

                                  
                while ( !winCondition () )                           // while game not over
                {
                  int location = 0;

                       if ( input.hasNext() )
                    location = input.nextInt();                          // get move location
                                                                          // check for valid move
                  if ( verify( location, n ) )
                  {
                    displayMessage( "\nlocation: " +location );
               fm.format( "Valid move in %d\n",location);
                    fm.flush();                     
                  }  
                  else 
                  {
                    fm.format( "Invalid move" );
                    fm.flush ();            
                  }  
                       if(winCondition()||!(isDraw(grid)))
               { 
                    	  if (winCondition()) {
                    		  msgbox("Game over "+ grid[x]+ " won");
                    		  fm.format("Game over  %s won\n",grid[x]);
 			                 fm.flush();
						}else  if (!isDraw(grid)) {
                 		   msgbox("Game Drawn");
						}
                 
                         } 
            }
}             finally
             {
                try
                {
                  ss1.close();                                                  // close connection to client
               } 
                catch ( IOException ioException )
                {
                  ioException.printStackTrace();
                  System.exit( 1 );
                }             }
          }
          
     
      public void suspend( boolean b )
		 {
			 end = b; 
		 } 
    } 
}