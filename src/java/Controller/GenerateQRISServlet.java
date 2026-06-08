/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.JSONObject;

import util.JDBC;
import util.MidtransUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class GenerateQRISServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            MidtransUtil.init();

            String idTiket =
                req.getParameter("idTiket");

            double total =
                Double.parseDouble(
                    req.getParameter("total")
                );

            String orderId =
                UUID.randomUUID().toString();

            JSONObject detail =
                new JSONObject();

            detail.put(
                "order_id",
                orderId
            );

            detail.put(
                "gross_amount",
                total
            );

            JSONObject transaction =
                new JSONObject();

            transaction.put(
                "transaction_details",
                detail
            );

            String responseMidtrans =
                MidtransSnapApi
                    .createTransaction(
                        transaction
                    );

            JSONObject json =
                new JSONObject(
                    responseMidtrans
                );

            String qrUrl =
                json.getJSONArray("actions")
                    .getJSONObject(0)
                    .getString("url");

            Connection con =
                JDBC.getConnection();

            String sql =
                "INSERT INTO pembayaran " +
                "(order_id, id_tiket, total, status_bayar) " +
                "VALUES (?, ?, ?, ?)";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ps.setString(1, orderId);
            ps.setString(2, idTiket);
            ps.setDouble(3, total);
            ps.setString(4, "BELUM BAYAR");

            ps.executeUpdate();

            req.setAttribute(
                "qrUrl",
                qrUrl
            );

            req.getRequestDispatcher(
                "payment.jsp"
            ).forward(req, resp);

        } catch (
                IOException |
                MidtransError |
                java.sql.SQLException e
        ) {

            e.printStackTrace();
        }
    }
}
