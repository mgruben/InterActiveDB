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
        TableView tv = new TableView();
        bp.setCenter(tv);
        
        TableColumn <String,String> col = new TableColumn("Name");
        col.setCellValueFactory(p -> new SimpleObjectProperty(p.getValue()));
        tv.getColumns().add(col);
        
        tv.getItems().add("Ram");
        
        
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
