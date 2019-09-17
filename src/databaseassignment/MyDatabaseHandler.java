/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseassignment;

import static databaseassignment.Dashboard.allTables;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DELL
 */
public class MyDatabaseHandler extends Dashboard {
    Connection connect = null;
    String table;
    JTable tab;
    int columnCount = 0;
    public void connectDatabase(String database,String user,String password){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/"+database+"?autoReconnect=true&useSSL=false"
                    ,user,password);
            System.out.println("Successfully Connected to Mysql");

        }catch(Exception e){
            System.out.println("Not Connected..");
            e.printStackTrace();
        }
    }
    
    public void close(){
        if(connect != null){
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
   
    
    public void showResultTable(){
        try{
            String query = "SELECT * FROM "+allTables.getSelectedItem();
            PreparedStatement pStatement = connect.prepareStatement(query);
            ResultSet rs=pStatement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
        
            int cols = rsmd.getColumnCount();
            rs.last(); 
            int rows= rs.getRow();
            JTable.setModel(new DefaultTableModel(rows, cols));
            rs.beforeFirst();
            int p=0;
            for(int i=1;i<=cols;i++){
                JTable.getColumnModel().getColumn(i-1).setHeaderValue(rsmd.getColumnName(i));
            }    
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                        String columnValue = rs.getString(i);
                        JTable.setValueAt(columnValue, p, i-1);
                }
                p++;
            }
            
            
        }catch(Exception e){
            System.out.println("Error in showing table data");
            e.printStackTrace();
        }
       
        
    }
    
    public void fillCombo(){
        try{
            DatabaseMetaData md = connect.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                allTables.addItem(rs.getString(3));
            }
        }catch(Exception e){
            System.out.println("Error in populating combo");
            e.printStackTrace();
        }
    }
    
    public void showInsertForm(){
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    JButton btn = new JButton("submit");
    
    Statement statement = null;
    ResultSet rs = null;
    ResultSetMetaData rsMetaData = null;
    
    JTextField[] texts;
        try {
            String query = "SELECT * FROM "+allTables.getSelectedItem();        
            statement = connect.createStatement();
            rs = statement.executeQuery(query);
            rsMetaData = rs.getMetaData();
            columnCount = rsMetaData.getColumnCount();
            table = (String) allTables.getSelectedItem();
            tab = JTable;
            
            texts = new JTextField[columnCount+1];
            
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                String colName = rsMetaData.getColumnLabel(columnIndex);
                System.out.println(colName+" "+columnIndex);
                JLabel label = new JLabel(colName);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(label);
                texts[columnIndex] = new JTextField();
                texts[columnIndex].setPreferredSize(new Dimension(200,30));
                JTextField text = new JTextField();
                text.setPreferredSize(new Dimension(200, 30));
                panel.add(texts[columnIndex]);
            }
            
            //frame.setSize(700, 500);
            panel.setLayout(new GridLayout(columnCount,2,10,30));
            panel.setBorder(new EmptyBorder(20, 10, 20, 10));
            frame.add(panel,BorderLayout.CENTER);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    
                    try{

                        String query = "INSERT INTO "+allTables.getSelectedItem()+" VALUES(";
                        for (int columnIndex = 1; columnIndex < columnCount; columnIndex++){
                            query += "?,";
                        }
                        query += "?)";
                        System.out.println(query);
                        PreparedStatement pStatement = connect.prepareStatement(query);;
                        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                            pStatement.setString(columnIndex, texts[columnIndex].getText());
                            System.out.println("pStatement.setString("+columnIndex+","+texts[columnIndex].getText()+");");
                        }
                        pStatement.executeUpdate();
                        System.out.println("Inserted successfully");

                    }catch(Exception e){
                        System.out.println("Error in inserting data");
                        e.printStackTrace();
                    }
                    finally{
                        
                        loadTab();
                        columnCount = 0;
                        frame.dispose();
                    }
                }
            });
            frame.add(btn,BorderLayout.SOUTH);
            frame.setTitle((String) allTables.getSelectedItem());
            //frame.add(new JButton("Submit"));
            frame.pack();
            //frame.setSize(800, 1000);
            frame.setVisible(true);
            frame.setLayout(new BorderLayout());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            System.out.println("Insert Form");
        } catch (Exception e) {
            e.printStackTrace();
        }   
        
    }
    
    private void loadTab() {
        try {
            String query = "SELECT * FROM "+allTables.getSelectedItem();
            Statement statement = connect.createStatement();
            ResultSet result = statement.executeQuery(query);
            resultSetToTableModel(result, tab);
        } catch (Exception e) {
        }
    }
    
    public void resultSetToTableModel(ResultSet rs, JTable table) throws SQLException {
        //Create new table model
        DefaultTableModel tableModel = new DefaultTableModel();

        //Retrieve meta data from ResultSet
        ResultSetMetaData metaData = rs.getMetaData();

        //Get number of columns from meta data
        int columnCount = metaData.getColumnCount();

        //Get all column names from meta data and add columns to table model
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
            tableModel.addColumn(metaData.getColumnLabel(columnIndex));
        }

        //Create array of Objects with size of column count from meta data
        Object[] row = new Object[columnCount];

        //Scroll through result set
        while (rs.next()){
            //Get object from column with specific index of result set to array of objects
            for (int i = 0; i < columnCount; i++){
                row[i] = rs.getObject(i+1);
            }
            //Now add row to table model with that array of objects as an argument
            tableModel.addRow(row);
        }

        //Now add that table model to your table and you are done :D
        table.setModel(tableModel);
    }
    
    
    
    
    public void deleteData(){
        try{
            DefaultTableModel model = (DefaultTableModel)JTable.getModel();
            int row = JTable.getSelectedRow();
            int modelRow = JTable.convertRowIndexToModel( row );
            String tb_name = (String) allTables.getSelectedItem();
            String condition = (String) JTable.getColumnModel().getColumn(0).getHeaderValue();
            String val = (String) JTable.getValueAt(row, 0);
            String sql = "DELETE FROM "+tb_name+" WHERE "+condition+"="+val;
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.executeUpdate();
            model.removeRow( modelRow );
        }catch(Exception e){
            System.out.println("Error in deleting data");
            e.printStackTrace();
        }
        
    }
    
}
