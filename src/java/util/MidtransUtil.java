/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.midtrans.Config;

public class MidtransUtil {

    public static void init() {

        Config.serverKey =
                "SB-Mid-server-XXXXXXXXXXXXXXXX";

        Config.clientKey =
                "SB-Mid-client-XXXXXXXXXXXXXXXX";

        Config.isProduction = false;
    }
}