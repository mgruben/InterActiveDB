/*
 * Copyright (C) 2016 Michael <GrubenM@GMail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Michael <GrubenM@GMail.com>
 */
public class InterActiveDBfx extends Application {
    
    class Table extends TableView {
        public void load(String colNames[]) {
            for (int i = 0; i < colNames.length; i++) {
                TableColumn <DataRow,String> col = new TableColumn<>(colNames[i]);
                final int g = i;
                col.setCellValueFactory(p -> p.getValue().getProperty(g));
                this.getColumns().add(col);
            }
        }
    }

    class DataRow {
        SimpleObjectProperty data[];
            public DataRow(String strData[]) {
            data = new SimpleObjectProperty[strData.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = new SimpleObjectProperty(strData[i]);
            }
        }

        public DataRow(SimpleObjectProperty xData[]) {
            data = xData;
        }
        public SimpleObjectProperty getProperty(int i) {
            return data[i];
        }
    }

    @Override
    public void start(Stage stage) {
        BorderPane bp = new BorderPane();
        
        TextField tf = new TextField("Select * from Worker");
        bp.setTop(tf);
        Table table = new Table();
        bp.setCenter(table);
        
        try {
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
            String colNames[] = new String[nCols];
            for ( int i=1 ; i<=nCols ; i++ ) {
              colNames[i-1] = rsmd.getColumnName(i);
            }
            table.load(colNames);

            // get row information
            NumberFormat nf = DecimalFormat.getCurrencyInstance();
            nf.setMaximumFractionDigits(0);
            for ( ; rs.next() ; ) {
                for (int i=1; i<=nCols; i++) {
                    colNames[i-1] = rs.getString(i);
                }
            table.getItems().add(new DataRow(colNames));
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (ClassNotFoundException e){
            System.err.println("driver error");
            System.exit(1);
        } catch (SQLException e) {          // display error info, 
            System.err.println("sql error"); // multiple objects can be 
            for (; e!=null; e = e.getNextException()) // linked.
                System.err.println("SQL State "+e.getSQLState()+
                " Message "+e.getMessage()+" Vendor "+ e.getErrorCode());
        }
        
        Scene sc = new Scene(bp,400,200);
        stage.setScene(sc);
        
        stage.setTitle("Hello World!");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
