/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseassignment;

import java.sql.ResultSet;

/**
 *
 * @author DELL
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // MyDatabaseHandler db = new MyDatabaseHandler();
         //db.connectDatabase();
        // db.insertData();
         //ResultSet rs = db.testQuery();
         //db.showResult(rs);
         Dashboard frame=new Dashboard();
         frame.setVisible(true);
    }
    
}
