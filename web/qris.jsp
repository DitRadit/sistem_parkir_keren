<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp");
        return;
    }

    String idTiket   = (String) request.getAttribute("idTiket");
    String platNomor = (String) request.getAttribute("platNomor");
    String jenis     = (String) request.getAttribute("jenis");

    // FIX: pakai Number supaya aman baik Double maupun Integer
    Object totalBiayaRaw = request.getAttribute("totalBiaya");
    long totalBiaya = totalBiayaRaw != null ? ((Number) totalBiayaRaw).longValue() : 0;

    // FIX: nama attribute sekarang "qrImage" sesuai dengan yang dikirim GenerateQRISServlet
    String qrCodeUrl = (String) request.getAttribute("qrImage");

    if(idTiket == null) {
        response.sendRedirect("dashboard");
        return;
    }
%>

<%@ include file="includes/header.jsp" %>

<style>
    .qris-card {
        background: #ffffff;
        border-radius: 1.5rem;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
        border: 1px solid rgba(0,0,0,0.05);
        overflow: hidden;
    }
    .qris-logo-container {
        background: #f8fafc;
        padding: 1rem;
        border-bottom: 1px dashed #e2e8f0;
    }
    .qr-frame {
        background: #ffffff;
        border: 2px solid #00897b;
        border-radius: 1.25rem;
        padding: 1rem;
        display: inline-block;
        box-shadow: 0 4px 12px rgba(0, 137, 123, 0.1);
    }
    .blink-status {
        animation: blinker 1.5s linear infinite;
    }
    @keyframes blinker {
        50% { opacity: 0.4; }
    }
</style>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <p class="text-muted-custom mb-0">TERMINAL PEMBAYARAN DIGITAL</p>
                    <h3 class="fw-bold mb-0 text-dark"><i class="fas fa-wallet text-primary me-2"></i>Gateway QRIS Cashless</h3>
                </div>
                <div>
                    <a href="dashboard" class="btn btn-sm btn-light rounded-pill px-3 fw-bold border shadow-sm">
                        <i class="fas fa-times me-1"></i> Batalkan Transaksi
                    </a>
                </div>
            </div>

            <div class="row g-4 justify-content-center">
                <div class="col-md-6 col-lg-5">

                    <div class="qris-card text-center">

                        <div class="qris-logo-container d-flex align-items-center justify-content-center gap-2">
                            <span class="fw-extrabold fs-4 tracking-tight text-primary"><i class="fas fa-qrcode me-1"></i>QRIS</span>
                            <span class="text-muted small">| GPN Bersama</span>
                        </div>

                        <div class="p-4 p-md-5 bg-white bg-opacity-75">

                            <span class="text-muted small text-uppercase d-block mb-1 fw-bold tracking-wider">TOTAL TARIF PARKIR</span>
                            <h1 class="fw-extrabold text-success mb-4" style="font-size: 2.5rem;">
                                Rp <%= String.format("%,d", totalBiaya).replace(',', '.') %>
                            </h1>

                            <div class="qr-frame mb-4">
                                <% if(qrCodeUrl != null && !qrCodeUrl.isEmpty()) { %>
                                    <img src="<%= qrCodeUrl %>" alt="QRIS Payment Code" class="img-fluid" style="width: 220px; height: 220px;">
                                <% } else { %>
                                    <div class="d-flex flex-column align-items-center justify-content-center" style="width: 220px; height: 220px; background: #f1f5f9; border-radius: 1rem;">
                                        <i class="fas fa-spinner fa-spin fa-2x text-primary mb-2"></i>
                                        <span class="small text-muted">Memuat QRIS...</span>
                                    </div>
                                <% } %>
                            </div>

                            <div class="p-3 rounded-4 bg-light border border-opacity-10 mb-4 text-start font-monospace small">
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="text-muted">ID TIKET:</span>
                                    <span class="fw-bold text-dark"><%= idTiket %></span>
                                </div>
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="text-muted">PLAT NOMOR:</span>
                                    <span class="fw-bold text-dark text-uppercase"><%= platNomor %></span>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <span class="text-muted">JENIS:</span>
                                    <span class="fw-bold text-dark"><%= jenis %></span>
                                </div>
                            </div>

                            <div class="alert alert-warning border border-warning border-opacity-25 rounded-4 py-2 small d-flex align-items-center justify-content-center gap-2 mb-0">
                                <i class="fas fa-circle-notch fa-spin text-warning blink-status"></i>
                                <span class="fw-bold text-warning-emphasis">Menunggu pembayaran dari pelanggan...</span>
                            </div>

                        </div>

                        <div class="p-4 bg-light bg-opacity-50 border-top">
                            <form action="ValidasiTiketServlet" method="POST">
                                <input type="hidden" name="idTiket" value="<%= idTiket %>">
                                <button type="submit" class="btn btn-primary rounded-pill py-2 fw-bold w-100 shadow-sm"
                                        style="background: linear-gradient(135deg, #00897b, #00bfa5); border: none;">
                                    <i class="fas fa-sync-alt me-2"></i> VERIFIKASI STATUS BAYAR
                                </button>
                            </form>
                        </div>

                    </div>

                </div>
            </div>

        </main>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>