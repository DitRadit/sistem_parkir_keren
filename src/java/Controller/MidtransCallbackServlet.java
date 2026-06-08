/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javax.servlet.http.*;

import org.json.JSONObject;

import util.JDBC;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MidtransCallbackServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            StringBuilder json =
                new StringBuilder();

            BufferedReader reader =
                req.getReader();

            String line;

            while ((line = reader.readLine())
                    != null) {

                json.append(line);
            }

            JSONObject object =
                new JSONObject(
                    json.toString()
                );

            String orderId =
                object.getString(
                    "order_id"
                );

            String status =
                object.getString(
                    "transaction_status"
                );

            if (status.equals("settlement")) {

                Connection con =
                    JDBC.getConnection();

                String sql =
                    "UPDATE pembayaran " +
                    "SET status_bayar='LUNAS' " +
                    "WHERE order_id=?";

                PreparedStatement ps =
                    con.prepareStatement(sql);

                ps.setString(1, orderId);

                ps.executeUpdate();

                ps.close();
                con.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}