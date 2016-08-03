// Copyright (c) 2005-2006 Ramiro Villarreal  All rights reserved.
import javax.swing.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.border.*;

public class InterActiveSQL extends JFrame {
  Connection con;
  JTable tbl;
  JPanel ConnectPanel;
  static String op[]= {"Ok","Cancel"};  
  String query;
  JTextField jtf;
  public InterActiveSQL(String url, String driver) {
    super("InteractiveSQL");
    query = "Select * from Worker";    
    try{
       Class.forName( driver );
       con = DriverManager.getConnection( url );
    } catch(ClassNotFoundException e){
      System.out.println("Driver not found");
      System.exit(1);
    } catch(SQLException e){
      System.err.println("SQL error");
      System.exit(1);
    }    
       
    // Connection Panel for Dialog
    ConnectPanel = new JPanel();
    // null for layout means we do the layout manually
    // only do this if you know exactly how u want it to look
    ConnectPanel.setLayout(null);
    JLabel jl ;
    JTextField jtf2;
    ConnectPanel.add( jl = new JLabel("Data Source"));
    jl.setBounds( 0, 0, 76, 24);



    ConnectPanel.add( jtf2 = new JTextField(url));
    jtf2.setBounds( 84, 0, 200, 24);
    ConnectPanel.add( jl = new JLabel("Driver"));
    jl.setBounds(0, 24, 80, 24);
    ConnectPanel.add( jtf2 = new JTextField(driver ,25));
    jtf2.setBounds( 84, 24, 200, 24);
    // even though JComponent has a setSize method that is 
    // not what is used to set the size;
    ConnectPanel.setPreferredSize( new Dimension( 300, 72 ));
    
    // Top Panel - a panel at the top to hold the buttons
    JPanel jp = new JPanel();
    jp.setBorder(BorderFactory.createLineBorder(Color.black));    
    // gridbagLayout is the toughest, usually avoided, but we 
    // need at least one example
    
    GridBagLayout gb = new GridBagLayout();
    jp.setLayout(gb);
    GridBagConstraints c = new GridBagConstraints();   
    // Connect Button
    // add the component as usual, but then set the constraints
    // so the layout knows what to do with it
    JButton jb =new JButton("Connect");
    c.weightx=0.0;
    gb.setConstraints(jb,c);
    jp.add(jb);
    jb.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
          JOptionPane.showOptionDialog(null,
               ConnectPanel,"Set connection",
               JOptionPane.DEFAULT_OPTION,  
               JOptionPane.PLAIN_MESSAGE,
               null,op,op[0]);
       }      
      });
    // textfield
    c.weightx=1.0;
    c.fill=GridBagConstraints.BOTH;
    jtf = new JTextField(query,28);
    jtf.setFont( jtf.getFont().deriveFont( Font.BOLD, 12));
    gb.setConstraints( jtf, c);
    jp.add( jtf);
    jtf.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        setTable(jtf.getText().trim());
      }
    });
    addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        try{ con.close(); } catch (SQLException sqle){
             System.err.println("sql error in shutdown");
           }
        System.exit(0);
      }
    });
    
    //getContentPane().add(jp);
    //  the WindowListener is needed to make sure we
    //  shut down the connection
    add(jp,BorderLayout.NORTH);
    setBounds(100,100,500,300);
    setVisible(true);
  }
  
  
  public void setTable(String q){
    //query = q;
    // remove the old table. For a lot of records,
    // we would not do this, but i'm keeping it simple
    if (tbl != null) getContentPane().remove(1);
    Vector <Vector>rows = new Vector<Vector>();
    Vector <String>cols = new Vector<String>();
    try{
      Statement stmt = con.createStatement();
      // submit query - not all queries return resultsets
      // but select does
      if( ! q.toLowerCase().startsWith("select")){
         stmt.executeUpdate(q);
      }
      else query = q;

      ResultSet rs = stmt.executeQuery(query);

      // metaData gives info about database fields
      ResultSetMetaData rsmd = rs.getMetaData();
      // get Column info
      int nCols = rsmd.getColumnCount();
      int colType[]= new int[nCols];
      //0 string 1 int 2 float 3 currency 4 id 
      for (int i=1; i<=nCols ; i++ ){
        
          String g= rsmd.getColumnName(i).toLowerCase();
          cols.add(rsmd.getColumnLabel(i));
          if (g.endsWith("income")) colType[i-1]=4;
          else if (g.endsWith("wage"));
          else if (g.endsWith("age")) colType[i-1]=6;
      }
      NumberFormat nf = DecimalFormat.getCurrencyInstance();
      nf.setMaximumFractionDigits(0);
      // get Row info - table info
      while (rs.next()){
        Vector <String> row = new Vector<String>();
        for (int i=1; i<=nCols; i++)
          if (colType[i-1]==4)
                 row.addElement(String.format("%12s", nf.format(rs.getFloat(i))));
          else if (colType[i-1]==6)
                      row.addElement(String.format("%8d", rs.getInt(i)));
          else if (rsmd.getColumnType(i)==java.sql.Types.FLOAT)
              row.addElement( String.format("%8.2f", rs.getFloat(i)));
          
          else    row.addElement( String.format("%-8s",rs.getString(i)));
        rows.addElement(row);
      }
      // close resultset and statement
      rs.close();
      stmt.close();      
    } catch (SQLException sqle){ 
      // display error info, multiple objjects can be linked together.
      for(; sqle!=null; sqle=sqle.getNextException())
        System.err.println("SQL State:"+sqle.getSQLState()+
                " Vendor:"+ sqle.getErrorCode()
                +"\nMessage:"+sqle.getMessage() );
    } catch (Exception e){ e.printStackTrace();  }
    // create the table and add to scroll pane
    tbl = new JTable( rows, cols);
    
    tbl.setFont(tbl.getFont().deriveFont( Font.BOLD, 16));
    tbl.setRowHeight(20);
    getContentPane().add( new JScrollPane( tbl ));
    validate();
  }
  
  
  public static void main(String[] args) {
//    String driver = new String("sun.jdbc.odbc.JdbcOdbcDriver");
  // new InterActiveSQL("jdbc:odbc:Worker",driver);
    String driver = new String("org.sqlite.JDBC");
    new InterActiveSQL("jdbc:sqlite:sample.db",driver);
    //new MetalworksHelp();
  }
}
