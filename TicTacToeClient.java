
/** 
 * TicTacToeClient.java 
 * 
 * Version: TicTacToeClient.java v 1.0	2015/05/02   3:34 PM
 * 
 * Revisions: Initial Revision 
 * 
 * @author  Karan Chauhan
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.*;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import javax.swing.*;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * This class implements the client side of the game
 */

public class TicTacToeClient extends JFrame implements Runnable 
{ 
	
	private Square grid[][];                                        // 3x3 grid to store the markings 'X' and 'O'
	private Square currentSquare;
	private Socket s;                                               // a new socket
	private Scanner sc;                                      
	private Formatter fm;
	private String tt;                                          
	private String temp; 
	private boolean turn_test;                                       // variable to test whose turn it is
	
	private final String player1 = "X";                              // Player using X
	private final String player2 = "O";                              // Player using Y

	private JTextField id;                                          // identifies the text field
	private JTextArea area;                                         // identifies the text area
	private JPanel panel1;                                          // new panel
	private JPanel panel2; 

	public TicTacToeClient( String string )
	{

		tt = string; 
		area = new JTextArea( 4, 30 );                                    // specify the text area
		area.setEditable( false );
		add( new JScrollPane( area ), BorderLayout.SOUTH );               // set the border
		panel1 = new JPanel();
		panel1.setLayout( new GridLayout( 3, 3, 0, 0 ) );                 // form the grid pattern

		grid = new Square[3][3];                                         // square of size 3x3


		for ( int i = 0; i < grid.length; i++ )                          // for each row
		{
			for ( int j = 0; j < grid[ i ].length; j++ )                 // for each column
			{

				grid[ i ][ j ] = new Square(  " ",i * 3 + j);           // pass parameters to the grid
				panel1.add( grid[ i ][ j ] );                           // add a square in the grid
			} 
		} 

		id = new JTextField();                                         // create a new text field
		id.setEditable( false );  
		add( id, BorderLayout.NORTH );                                  // add border to the field

		panel2 = new JPanel();                                          // create a new panel
		panel2.add( panel1, BorderLayout.CENTER );                      // set border to panel                       
		add( panel2, BorderLayout.CENTER );                                  

		setSize( 300, 225); 
		setVisible( true );  

		initiateClient(); 
	} 

	/**
 	 * This method starts the client window
 	 * 
 	 */
	public void initiateClient() 
	{ 
		try  
		{ 
			
			s = new Socket(                           
					InetAddress.getLocalHost( ),12345);             // make connection to server 

			 
			sc = new Scanner( s.getInputStream() );                // get streams for input 
			fm = new Formatter( s.getOutputStream() );             // get streams for output
		}  
		catch ( IOException ioException ) 
		{ 
			ioException.printStackTrace();                          // handle exception
		}  


		ExecutorService es = Executors.newFixedThreadPool( 1 ); 
		es.execute( this ); 
	}  


	public void run() 
	{ 
		temp = sc.nextLine();                                      // take input

		SwingUtilities.invokeLater( 
				new Runnable() 
				{ 
					public void run()                            // implement run method within run
					{

						id.setText( "This is :" + temp + "\"" );    
					} 
				}  
				); 

		turn_test = ( temp.equals( player1 ) );                // if its player 1's chance
		while ( true ) 
		{
			if ( sc.hasNextLine() )
				dispMessage( sc.nextLine() );                  // print the message
		} 
	} 

	/**
 	 * This method prints the respective messages to be displayed
 	 * 
 	 * @param   msg      message to be displayed
 	 * 
 	 */
	private void dispMessage( String msg )
	{

		if(msg.equals("Game over %s won"))                         // if the game is won by any player display it
		{
			print(msg+"\n" );
		}    

		else if ( msg.equals( "Your move is done" ) )
		{
			print( msg+"\n" );

			int i=sc.nextInt();
			sc.nextLine();

			arrange( grid[i/3][i%3], temp );                       // set mark in square
		} 
		else if(msg.equals("Valid move in "))
			print(msg);

		else if ( msg.equals( "Invalid move, try again" ) )
		{
			print( msg + "\n" );                                    // display invalid move
			turn_test = true;                                         // still this client's turn
		} 
		else if ( msg.equals( "Opponent moved" ) )
		{
			int location = sc.nextInt();                          // get location of the move of the player
			sc.nextLine();                                        // skip the newline 
			int i = location / 3;                                 // calculate row by dividing by three
			int j = location % 3;                                 // calculate column
			arrange( grid[ i ][ j ],
					( temp.equals( player1 ) ? player2 : player1 ) );    // check the condition for chance of players
			print( "Opponent moved. Your turn.\n" );
			turn_test = true; 
		} 
		else
			print( msg + "\n" );
	} 

	/**
 	 * This method printd the value of the string
 	 * 
 	 * @param   disp    value to be displayed
 	 * 
 	 */

	private void print( final String disp )
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						area.append( disp );                   // join the string to be displayed 
					}                                            // end method run
				} 
				); 
	} 

	
	/**
 	 * This method sorts the arrangement between all the squares and the markings 'X' and 'O'
 	 * 
 	 * @param   start     the starting vertex
 	 * 
 	 */
	
	private void arrange( final Square sq, final String mark )
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						sq.setMark( mark );                         // sort the markings 
					} 
				} 
				);
	} 

	
	/**
 	 * This method returns the square to the second player
 	 * 
 	 * @param   place    location to be sent
 	 * 
 	 */

	public void sendSquare( int place ) 
	{ 

		if ( turn_test ) 
		{ 
			fm.format( "%d\n", place );                   // print the loaction of the current square 
			fm.flush();                                              
			turn_test = false; 
		}  
	} 


	public void sendSquare1( Square square ) 
	{ 
		currentSquare = square;                         // send the square to other player
	}  

	/**
	 * This class is responsible for displaying 
	 */
	
	private class Square extends JPanel 
	{ 
		private String mark; 
		private int location; 

		public Square( String squareMark, int squareLocation ) 
		{ 
			mark = squareMark;  
			location = squareLocation;  

			addMouseListener( 
					new MouseAdapter() 
					{ 
						public void mouseReleased( MouseEvent e ) 
						{
							currentSquare=Square.this; 

							sendSquare( getSquareLocation() ); 
						} 
					}  
					);  
		} 


		public Dimension getPreferredSize() 
		{ 
			return new Dimension( 30 , 30 );                        // dimensions of the grid
		} 


		public Dimension getMinimumSize() 
		{ 
			return getPreferredSize(); 
		}  

		public void setMark( String newMark ) 
		{ 
			mark = newMark;
			repaint(); 
		}


		public int getSquareLocation() 
		{ 
			return location; 
		}

		public void paintComponent( Graphics g ) 
		{ 
			super.paintComponent( g ); 

			g.drawRect( 0, 0, 29, 29 ); 
			g.drawString( mark, 11, 20 );  
		} 
	} 
} 
