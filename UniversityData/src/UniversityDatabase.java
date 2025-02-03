import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UniversityDatabase extends JFrame {
    private JTable table1;
    private JRadioButton studentrdnbtn;
    private JRadioButton academicianrdnbtn;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton deletebtn;
    private JButton updatebtn;
    private JButton insertbtn;
    private JPanel pnl1;
    private JPanel pnl2;
    private JPanel pnl3;
    private JPanel pnl4;
    private Connection connection;
    private DefaultTableModel tableModel;


    public UniversityDatabase() {
        initComponents();
        connectDatabase();
        loadTableData();
    }

    private void initComponents() {

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Surname", "Course"}, 0);
        table1.setModel(tableModel);



        ButtonGroup group = new ButtonGroup();
        group.add(studentrdnbtn);
        group.add(academicianrdnbtn);

        studentrdnbtn.setSelected(true);
        studentrdnbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableData();
            }
        });

        academicianrdnbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableData();
            }
        });

        insertbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });

        updatebtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData();
            }
        });

        deletebtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "BerkeSQL123#");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        String query;
        if (studentrdnbtn.isSelected()) {
            query = "SELECT * FROM student";
        } else {
            query = "SELECT * FROM academician";
        }

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String course = rs.getString("coursecode");
                tableModel.addRow(new Object[]{id, name, surname, course});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        String name = textField1.getText();
        String surname = textField2.getText();
        String course = textField3.getText();


        if (name.isEmpty() || surname.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String query;
        if (studentrdnbtn.isSelected()) {
            query = "INSERT INTO student (name, surname, coursecode) VALUES (?, ?, ?)";
        } else {
            query = "INSERT INTO academician (name, surname, coursecode) VALUES (?, ?, ?)";
        }
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, course);
            pstmt.executeUpdate();
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) return;

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = textField1.getText();
        String surname = textField2.getText();
        String course = textField3.getText();
        String query;

        if (name.isEmpty() || surname.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }



        if (studentrdnbtn.isSelected()) {
            query = "UPDATE student SET name = ?, surname = ?, coursecode = ? WHERE idstudent = ?";
        } else {
            query = "UPDATE academician SET name = ?, surname = ?, coursecode= ? WHERE idacademician = ?";
        }


        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, course);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteData() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) return;

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String query;

        if (studentrdnbtn.isSelected()) {
            query = "DELETE FROM student WHERE idstudent = ?";
        } else {
            query = "DELETE FROM academician WHERE idacademician = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resetFields();
    }

    private void resetFields() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("University Database");
        frame.setContentPane(new UniversityDatabase().pnl4);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}