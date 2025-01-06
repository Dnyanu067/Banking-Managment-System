package com.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Accounts {

	private Connection connection;
    private Scanner scanner;
    
    public Accounts() {
		super();
	}





	public Accounts(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;

    }
    




    public long open_account(String email){
        if(!account_exist(email)) {
            String open_account_query = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
            scanner.nextLine();
            System.out.print("Enter Full Name: ");
            String full_name = scanner.nextLine();
            System.out.print("Enter Initial Amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin: ");
            String security_pin = scanner.nextLine();
            try {
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                
                preparedStatement.setString(5, security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return account_number;
                } else {
                    throw new RuntimeException("Account Creation failed!!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exist");

    }

    public long getAccount_number(String email) {
        String query = "SELECT account_number from Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!");
    }



    private long generateAccountNumber() {
        try {
            Statement statement = connection.createStatement();
            // Correcting query for Oracle to use ROWNUM (Oracle 11g or earlier) or FETCH FIRST (Oracle 12c or later)
      //      ResultSet resultSet = statement.executeQuery("SELECT account_number FROM Accounts ORDER BY account_number DESC FETCH FIRST 1 ROWS ONLY");
            ResultSet resultSet = statement.executeQuery("SELECT account_number FROM Accounts WHERE ROWNUM = 1 ORDER BY account_number DESC");

            // OR for earlier versions of Oracle use ROWNUM:
            // ResultSet resultSet = statement.executeQuery("SELECT account_number FROM Accounts WHERE ROWNUM = 1 ORDER BY account_number DESC");
            
            if (resultSet.next()) {
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number + 1; // Return next account number
            } else {
                return 10000100; // Default account number if no records exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100; // Default fallback account number
    }


    public boolean account_exist(String email) {
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;  // Account exists with the given email
            } else {
                return false;  // No account found with the given email
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
