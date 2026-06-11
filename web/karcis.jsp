<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Proteksi Sesi Keamanan
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp"); 
        return;
    }

    // Menangkap data kiriman sukses dari KendaraanMasukServlet
    String idTiket = (String) request.getAttribute("idTiket");
    String platNomor = (String) request.getAttribute("platNomor");
    String jenis = (String) request.getAttribute("jenis");
    String qrImage = (String) request.getAttribute("qrImage");

    if(idTiket == null) {
        response.sendRedirect("KendaraanMasukServlet");
        return;
    }
%>

<%@ include file="includes/header.jsp" %>

<style>
    .ticket-card {
        background: #ffffff;
        color: #2d3748;
        border-radius: 1rem;
        border: 2px dashed #cbd5e1;
        max-width: 380px;
        margin: 0 auto;
        box-shadow: 0 10px 25px rgba(0,0,0,0.05);
    }
    .ticket-header {
        border-bottom: 2px dashed #e2e8f0;
        padding-bottom: 1.5rem;
    }
    
    @media print {
        body {
            background: #ffffff !important;
        }
        .col-md-2, main > div:not(.printable-zone), .no-print, header, nav {
            display: none !important;
        }
        .col-md-10 {
            width: 100% !important;
            margin: 0 !important;
            padding: 0 !important;
        }
        .printable-zone {
            display: block !important;
            margin: 0 !important;
            padding: 0 !important;
        }
        .ticket-card {
            border: none !important;
            box-shadow: none !important;
            max-width: 100% !important;
            padding: 10px !important;
        }
    }
</style>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">
            
            <div class="d-flex justify-content-between align-items-center mb-4 no-print">
                <div>
                    <p class="text-muted-custom mb-0">SISTEM GENERATOR KARCIS</p>
                    <h3 class="fw-bold mb-0 text-success"><i class="fas fa-check-circle me-2"></i>Tiket Berhasil Dibuat</h3>
                </div>
                <div>
                    <a href="dashboard" class="btn btn-sm btn-light rounded-pill px-3 fw-bold border shadow-sm">
                        <i class="fas fa-arrow-left me-1"></i> Kembali ke Dashboard
                    </a>
                </div>
            </div>

            <div class="row justify-content-center mt-3">
                <div class="col-md-6 printable-zone">
                    
                    <div class="ticket-card p-4 text-center">
                        <div class="ticket-header">
                            <h4 class="fw-bold mb-1 tracking-tight text-dark"><i class="fas fa-parking text-primary me-2"></i>PARKIRPRO MALL</h4>
                            <span class="text-muted small d-block mb-2">Gedung Parking Sentral Mall Utama</span>
                            <span class="badge bg-dark rounded-pill px-3 py-1 font-monospace"><%= idTiket %></span>
                        </div>

                        <div class="py-4">
                            <span class="text-muted small text-uppercase d-block mb-1 fw-bold tracking-wider">NOMOR PLAT KENDARAAN</span>
                            <h2 class="fw-extrabold text-dark tracking-wide mb-3 text-uppercase"><%= platNomor %></h2>
                            
                            <div class="row g-2 justify-content-center mb-4">
                                <div class="col-auto">
                                    <span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 px-3 py-2 fw-bold text-uppercase">
                                        <i class="<%= jenis.equalsIgnoreCase("Mobil") ? "fas fa-car" : "fas fa-motorcycle" %> me-1"></i> <%= jenis %>
                                    </span>
                                </div>
                            </div>

                            <div class="bg-light p-3 d-inline-block rounded-4 border mb-3 shadow-sm">
                                <img src="<%= qrImage %>" alt="QR Karcis Parkir" class="img-fluid" style="width: 180px; height: 180px;">
                            </div>
                            
                            <p class="text-muted font-monospace mb-0" style="font-size: 0.75rem;">
                                Tempelkan kode QR ini pada mesin scanner <br> saat hendak keluar gerbang mall.
                            </p>
                        </div>

                        <div class="pt-3 border-top border-light">
                            <span class="text-muted d-block small mb-1" style="font-size: 0.7rem;">Dicetak secara otomatis oleh sistem pos parkir.</span>
                            <span class="text-dark small fw-bold font-monospace" style="font-size: 0.75rem;">SOP #01-GATE-ENTRY</span>
                        </div>
                    </div>

                    <div class="text-center mt-4 no-print" style="max-width: 380px; margin-left: auto; margin-right: auto;">
                        <div class="d-grid gap-2">
                            <button onclick="jalankanCetak();" class="btn btn-success btn-lg rounded-4 fw-bold shadow-sm py-2" 
                                    style="background: linear-gradient(135deg, #00897b, #00bfa5); border: none;">
                                <i class="fas fa-print me-2"></i> CETAK & KEMBALI
                            </button>
                            
                            <a href="dashboard" class="btn btn-outline-secondary rounded-4 fw-bold py-2">
                                <i class="fas fa-home me-1"></i> Buka Dashboard Manajerial
                            </a>
                        </div>
                    </div>

                </div>
            </div>

        </main>
    </div>
</div>

<script>
    function jalankanCetak() {
        // Memicu jendela cetak/unduh bawaan browser
        window.print();
    }

    // Listener otomatis: Berjalan tepat ketika jendela cetak ditutup oleh operator
    window.onafterprint = function() {
        // Otomatis tendang kembali ke rute dashboard komparatif kamu
        window.location.href = 'dashboard';
    };
</script>

<%@ include file="includes/footer.jsp" %>