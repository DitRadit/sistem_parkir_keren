<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Proteksi Keamanan Sesi Operator
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp"); 
        return;
    }

    // Menangkap data rincian dari Servlet pembawa rute
    String idTiket = (String) request.getAttribute("idTiket");
    String platNomor = (String) request.getAttribute("platNomor");
    String jenis = (String) request.getAttribute("jenis");
    String waktuMasuk = (String) request.getAttribute("waktuMasuk");
    String waktuKeluar = (String) request.getAttribute("waktuKeluar");
    
    Object durasiObj = request.getAttribute("durasi");
    Object totalBiayaObj = request.getAttribute("totalBiaya");
    
    long durasi = (durasiObj != null) ? (long) Double.parseDouble(durasiObj.toString()) : 1;
    int totalBiaya = (totalBiayaObj != null) ? (int) Double.parseDouble(totalBiayaObj.toString()) : 0;

    if(idTiket == null) {
        response.sendRedirect("dashboard");
        return;
    }
%>

<%@ include file="includes/header.jsp" %>

<style>
    /* CSS COMPACT MODE: Memaksa semua komponen lebih ramping */
    .invoice-card {
        background: #ffffff;
        border-radius: 1rem;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
        border: 1px solid rgba(0,0,0,0.05);
        overflow: hidden;
        max-width: 400px; /* Di-press agar lebih proporsional */
        margin: 0 auto;
    }
    .invoice-header {
        background: linear-gradient(135deg, #00897b, #00bfa5);
        padding: 1rem 1.5rem; /* Mengecilkan tinggi header hijau */
        color: white;
    }
    .bill-item {
        display: flex;
        justify-content: space-between;
        padding: 0.45rem 0; /* Jarak baris data dipotong setengahnya */
        border-bottom: 1px dashed #e2e8f0;
    }
    .bill-item:last-child {
        border-bottom: none;
    }
    .form-control-sm, .btn-sm-custom {
        padding: 0.5rem 1rem;
    }
</style>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-3">
            
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <p class="text-muted-custom small mb-0">POS KASIR GERBANG KELUAR</p>
                    <h4 class="fw-bold mb-0 text-dark"><i class="fas fa-file-invoice-dollar text-success me-2"></i>Kalkulasi Struk Biaya</h4>
                </div>
                <div>
                    <a href="dashboard" class="btn btn-sm btn-light rounded-pill px-3 fw-bold border shadow-sm">
                        <i class="fas fa-times me-1"></i> Batal
                    </a>
                </div>
            </div>

            <div class="row justify-content-center">
                <div class="col-md-6">
                    
                    <div class="invoice-card">
                        
                        <div class="invoice-header text-center">
                            <span class="text-white-50 text-uppercase tracking-wider font-monospace" style="font-size: 0.6rem; d-block">STRUK PENAGIHAN AKTIF</span>
                            <h5 class="fw-bold mb-1 mt-0">MALL PARKING SYSTEM</h5>
                            <span class="badge bg-white bg-opacity-20 rounded-pill px-2.5 font-monospace" style="font-size: 0.75rem;">ID: <%= idTiket %></span>
                        </div>

                        <div class="p-3 px-md-4 py-md-3 bg-white bg-opacity-75">
                            
                            <div class="text-center mb-2">
                                <h2 class="fw-extrabold text-dark tracking-wide text-uppercase mb-1" style="font-size: 2rem;"><%= platNomor %></h2>
                                <span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 px-2.5 py-1 small fw-bold text-uppercase" style="font-size: 0.7rem;">
                                    <i class="<%= jenis.equalsIgnoreCase("Mobil") ? "fas fa-car" : "fas fa-motorcycle" %> me-1"></i> <%= jenis %>
                                </span>
                            </div>

                            <hr class="text-muted opacity-25 my-2">

                            <div class="bill-item">
                                <span class="text-muted small"><i class="far fa-clock me-1"></i> Waktu Masuk</span>
                                <span class="fw-semibold text-dark small"><%= waktuMasuk != null ? waktuMasuk : "-" %></span>
                            </div>
                            <div class="bill-item">
                                <span class="text-muted small"><i class="far fa-clock me-1"></i> Waktu Keluar</span>
                                <span class="fw-semibold text-dark small"><%= waktuKeluar != null ? waktuKeluar : "Sesi Sekarang" %></span>
                            </div>
                            <div class="bill-item">
                                <span class="text-muted small"><i class="fas fa-hourglass-half me-1"></i> Durasi Parkir</span>
                                <span class="fw-bold text-dark small"><%= durasi %> Jam</span>
                            </div>

                            <div class="p-3 rounded-3 mt-3" style="background: linear-gradient(135deg, #e0f2f1 0%, #b2dfdb 100%);">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="fw-bold text-success text-uppercase small" style="letter-spacing: 0.5px; font-size: 0.75rem;">Total Tagihan</span>
                                    <h3 class="fw-extrabold text-success mb-0" style="font-size: 1.75rem;">Rp <%= String.format("%,d", totalBiaya).replace(',', '.') %></h3>
                                </div>
                            </div>

                        </div>

                        <div class="p-3 bg-light bg-opacity-50 border-top text-center">
                            <form action="ValidasiTiketServlet" method="POST" class="d-grid gap-2">
                                <input type="hidden" name="idTiket" value="<%= idTiket %>">
                                <input type="hidden" name="totalBiaya" value="<%= totalBiaya %>">
                                
                                <button type="submit" class="btn btn-success rounded-pill py-2 fw-bold shadow-sm"
                                        style="background: linear-gradient(135deg, #00897b, #00bfa5); border: none; font-size: 0.95rem;">
                                    <i class="fas fa-check-circle me-2"></i> BAYAR TUNAI & BUKA PALANG
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