<%-- 
    Document   : karcis_hilang
    Created on : Jun 15, 2026, 4:51:37 PM
    Author     : NabilRapa
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Karcis Hilang - SQR Parking</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <h2>&#x26A0; Checkout Manual - Karcis Hilang</h2>
    <p>Masukkan plat nomor kendaraan. Akan dikenakan <strong>denda Rp 50.000</strong> di luar biaya parkir normal.</p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="KarcisHilangServlet">
        <div class="form-group">
            <label>Plat Nomor</label>
            <input type="text" name="platNomor" class="form-control"
                   placeholder="Contoh: B 1234 ABC"
                   style="text-transform:uppercase"
                   required autofocus>
        </div>
        <button type="submit" class="btn btn-warning">
            Cari Kendaraan &amp; Generate QRIS
        </button>
        <a href="dashboard" class="btn btn-secondary">Batal</a>
    </form>
</div>
</body>
</html>
