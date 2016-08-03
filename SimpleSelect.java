import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.text.*;
public class SimpleSelect {
  public static void main(String[] args) {
    String output = "\n";
     try{
      Class.forName("org.sqlite.JDBC");
      
      Connection con = DriverManager.getConnection("jdbc:sqlite:sample.db");
  
      // create a statement for query
      Statement stmt = con.createStatement();
      // execute the query and get resultset
      ResultSet rs = stmt.executeQuery("Select * from Worker order by id");
      // get column information
      
      // coltype 0 String 1 int 2 float 3 currency 4 id 
      ResultSetMetaData rsmd = rs.getMetaData();
      int nCols = rsmd.getColumnCount();
      int[] colType = new int[nCols];
      for ( int i=1 ; i<=nCols ; i++ ){
        String g = rsmd.getColumnName(i).toLowerCase();
        if (rsmd.getColumnType(i)==java.sql.Types.VARCHAR)
            
            output += String.format(" %-6s", rsmd.getColumnLabel(i));
            
        
        else {
          String fmt="%8s";
          if (g.endsWith("income")){ colType[i-1]=3;
          fmt ="%10s";
          }
          else if (g.endsWith("id")){ colType[i-1]=4;
             fmt="%-4s";
          }
          else if (g.endsWith("wage"));
          else if (g.endsWith("age")){ colType[i-1]=5;
             fmt="%4s";
          }
        
          
          
          
          output += String.format(fmt,rsmd.getColumnLabel(i)) ;
        }
      }
      // get row information
      NumberFormat nf = DecimalFormat.getCurrencyInstance();
      nf.setMaximumFractionDigits(0);
      for (output+="\n" ; rs.next() ; output+='\n')
        for ( int i=1 ; i<=nCols ; i++ ){          
          System.out.println(rsmd.getColumnType(i));
          switch (rsmd.getColumnType(i)){
              
              case java.sql.Types.FLOAT:
                
                   float f = rs.getFloat(i);
                   if (colType[i-1]==3) output+=String.format("%10s",nf.format(f));
                   else if (f>1000)  output +=String.format("%8.0f",f);
                   else output+=String.format("%8.2f",f);
                   break;
              case java.sql.Types.VARCHAR:
                   output += String.format(" %-6s", rs.getString(i));
                   break;
              default:
                if (colType[i-1]==4) output += String.format("%-4s", rs.getString(i));
                else if (colType[i-1]==5) output += String.format("%4s", rs.getString(i));
                else output += String.format("%8s", rs.getString(i));
          }
        }
      rs.close();
      stmt.close();
      con.close();
      } catch (ClassNotFoundException e){
        System.err.println("driver error");
        System.exit(1);
      } catch (SQLException e){          // display error info, 
        System.err.println("sql error"); // multiple objects can be 
        for (; e!=null; e = e.getNextException()) // linked.
          System.err.println("SQL State "+e.getSQLState()+
           " Message "+e.getMessage()+" Vendor "+ e.getErrorCode());
      }
    System.out.println(output);
    JTextArea jta = new JTextArea(output);
    jta.setFont(new Font("Lucida Console", Font.BOLD, 24));
    jta.setTabSize(9);
    JOptionPane.showMessageDialog(null,jta,"Sqlite",JOptionPane.PLAIN_MESSAGE);
  }
}