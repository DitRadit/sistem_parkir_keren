/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.TiketException;
import model.Tiket;

import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * MidtransCallbackServlet
 * Webhook dari Midtrans → update status tiket LUNAS
 */
public class MidtransCallbackServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {

            JSONObject json =
                new JSONObject(sb.toString());

            String orderId =
                json.getString("order_id");

            String txStatus =
                json.getString("transaction_status");

            String fraudStatus =
                json.optString("fraud_status", "accept");

            boolean lunas =
                (txStatus.equals("settlement") ||
                 txStatus.equals("capture")) &&
                fraudStatus.equals("accept");

            if (lunas) {

                // orderId format: "SQR-<idTiket>"
                String idTiket =
                    orderId.replace("SQR-", "");

                // Update via Model Tiket
                Tiket tiket = new Tiket();
                tiket.setIdTiket(idTiket);
                tiket.setStatusBayar("LUNAS");
                tiket.updateStatusBayar("LUNAS");

                System.out.println(
                    "[Callback] Tiket " + idTiket +
                    " LUNAS."
                );
            }

            resp.setStatus(200);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"ok\"}");

        } catch (DatabaseException e) {

            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(
                "{\"error\":\"" + e.getMessage() + "\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}