<%-- 
    Document   : qris_karcis_hilang
    Created on : Jun 15, 2026, 5:05:06 PM
    Author     : NabilRapa
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp");
        return;
    }

    String idTiket       = (String) request.getAttribute("idTiket");
    String platNomor     = (String) request.getAttribute("platNomor");
    String jenis         = (String) request.getAttribute("jenis");
    String qrOriginalUrl = (String) request.getAttribute("qrOriginalUrl");
    String qrCodeUrl     = (String) request.getAttribute("qrCodeUrl");

    Object totalBiayaRaw  = request.getAttribute("totalBiaya");
    Object biayaParkirRaw = request.getAttribute("biayaParkir");
    Object dendaRaw       = request.getAttribute("dendaKarcis");

    long totalBiaya  = totalBiayaRaw  != null ? ((Number) totalBiayaRaw).longValue()  : 0;
    long biayaParkir = biayaParkirRaw != null ? ((Number) biayaParkirRaw).longValue() : 0;
    long denda       = dendaRaw       != null ? ((Number) dendaRaw).longValue()       : 50000;

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
        background: #fff7ed;
        padding: 1rem;
        border-bottom: 1px dashed #f97316;
    }
    .qr-frame {
        background: #ffffff;
        border: 2px solid #f97316;
        border-radius: 1.25rem;
        padding: 1rem;
        display: inline-block;
        box-shadow: 0 4px 12px rgba(249, 115, 22, 0.15);
    }
    .blink-status {
        animation: blinker 1.5s linear infinite;
    }
    @keyframes blinker {
        50\% { opacity: 0.4; }
    }
    .simulator-box {
        background: #fffbeb;
        border: 1px dashed #f59e0b;
        border-radius: 0.75rem;
        padding: 0.75rem 1rem;
        font-size: 0.78rem;
        word-break: break-all;
    }
    .denda-breakdown {
        background: #fff7ed;
        border: 1px solid #fed7aa;
        border-radius: 0.75rem;
        font-size: 0.85rem;
    }
</style>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <p class="text-muted-custom mb-0">CHECKOUT MANUAL — KARCIS HILANG</p>
                    <h3 class="fw-bold mb-0 text-dark">
                        <i class="fas fa-exclamation-triangle text-warning me-2"></i>Pembayaran + Denda
                    </h3>
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

                        <%-- Header orange khusus karcis hilang --%>
                        <div class="qris-logo-container d-flex align-items-center justify-content-center gap-2">
                            <span class="fw-extrabold fs-4 tracking-tight text-warning">
                                <i class="fas fa-exclamation-triangle me-1"></i>KARCIS HILANG
                            </span>
                            <span class="text-muted small">| Denda Berlaku</span>
                        </div>

                        <div class="p-4 p-md-5 bg-white bg-opacity-75">

                            <%-- Rincian biaya --%>
                            <div class="denda-breakdown p-3 mb-3 text-start">
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="text-muted">Biaya Parkir</span>
                                    <span class="fw-bold">Rp <%= String.format("%,d", biayaParkir).replace(',', '.') %></span>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span class="text-danger fw-bold">
                                        <i class="fas fa-gavel me-1"></i>Denda Karcis Hilang
                                    </span>
                                    <span class="fw-bold text-danger">
                                        + Rp <%= String.format("%,d", denda).replace(',', '.') %>
                                    </span>
                                </div>
                                <hr class="my-1">
                                <div class="d-flex justify-content-between">
                                    <span class="fw-extrabold text-dark">TOTAL TAGIHAN</span>
                                    <span class="fw-extrabold text-warning" style="font-size:1.05rem;">
                                        Rp <%= String.format("%,d", totalBiaya).replace(',', '.') %>
                                    </span>
                                </div>
                            </div>

                            <%-- Nominal besar --%>
                            <span class="text-muted small text-uppercase d-block mb-1 fw-bold">TOTAL YANG HARUS DIBAYAR</span>
                            <h1 class="fw-extrabold text-warning mb-4" style="font-size: 2.5rem;">
                                Rp <%= String.format("%,d", totalBiaya).replace(',', '.') %>
                            </h1>

                            <%-- QR Code frame, warna orange --%>
                            <div class="qr-frame mb-4">
                                <% if(qrCodeUrl != null && !qrCodeUrl.isEmpty()) { %>
                                    <img src="<%= qrCodeUrl %>" alt="QRIS Payment Code" class="img-fluid" style="width: 220px; height: 220px;">
                                <% } else { %>
                                    <div class="d-flex flex-column align-items-center justify-content-center" style="width: 220px; height: 220px; background: #fff7ed; border-radius: 1rem;">
                                        <i class="fas fa-spinner fa-spin fa-2x text-warning mb-2"></i>
                                        <span class="small text-muted">Memuat QRIS...</span>
                                    </div>
                                <% } %>
                            </div>

                            <%-- Info tiket --%>
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

                        <%-- Sandbox simulator --%>
                        <% if(qrOriginalUrl != null && !qrOriginalUrl.isEmpty()) { %>
                        <div class="px-4 pb-3">
                            <div class="simulator-box text-start">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-bold text-warning-emphasis small">
                                        <i class="fas fa-flask me-1"></i> Sandbox Simulator — QR Code Image URL
                                    </span>
                                    <button class="btn btn-warning btn-sm"
                                            style="font-size:0.75rem; padding:2px 10px; border-radius:20px;"
                                            onclick="navigator.clipboard.writeText('<%= qrOriginalUrl %>').then(() => { this.innerText='Copied!'; setTimeout(() => this.innerText='Copy', 1500); })">
                                        Copy
                                    </button>
                                </div>
                                <span class="text-muted" style="font-size:0.72rem;"><%= qrOriginalUrl %></span>
                            </div>
                        </div>
                        <% } %>

                        <%-- Tombol verifikasi --%>
                        <div class="p-4 bg-light bg-opacity-50 border-top">
                            <form action="ValidasiTiketServlet" method="POST">
                                <input type="hidden" name="idTiket" value="<%= idTiket %>">
                                <button type="submit" class="btn rounded-pill py-2 fw-bold w-100 shadow-sm text-white"
                                        style="background: linear-gradient(135deg, #f97316, #ea580c); border: none;">
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
