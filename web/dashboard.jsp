<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Tiket" %>
<%
    // Validasi Keamanan: Jika session kosong, tendang kembali ke halaman login
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp"); 
        return;
    }

    // FIX 1: Menangkap data sesuai dengan key yang dikirim oleh DashboardServlet.java
    List<Tiket> dataParkir = (List<Tiket>) request.getAttribute("daftarTiket");
    
    // Mengambil data angka kapasitas untuk kalkulasi widget persentase secara dinamis
    int jumlahAktif = (request.getAttribute("jumlahAktif") != null) ? (int) request.getAttribute("jumlahAktif") : 0;
    int kapasitasMaks = (request.getAttribute("kapasitasMaks") != null) ? (int) request.getAttribute("kapasitasMaks") : 500;
    int slotTersedia = (request.getAttribute("slotTersedia") != null) ? (int) request.getAttribute("slotTersedia") : 500;
    // Tambahkan baris ini di bagian blok deklarasi atas JSP kamu
    int omsetHariIni = (request.getAttribute("omsetHariIni") != null) ? (int) request.getAttribute("omsetHariIni") : 0;
    // Hitung persentase okupansi parkir mall secara real-time
    int persenOkupansi = (kapasitasMaks > 0) ? (jumlahAktif * 100) / kapasitasMaks : 0;
%>

<%@ include file="includes/header.jsp" %>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">
            
            <div class="d-flex justify-content-between align-items-end mb-4">
                <div>
                    <p class="text-muted-custom mb-1">Selamat datang kembali, <strong class="text-dark"><%= session.getAttribute("admin") %></strong> 👋</p>
                    <h2 class="fw-bold mb-0 text-dark" style="letter-spacing: -0.5px;">Dashboard Utama</h2>
                </div>
                <div class="d-flex align-items-center gap-3">
                    <span class="badge bg-white text-primary border border-primary border-opacity-25 rounded-pill px-3 py-2 fw-bold shadow-sm">
                        <i class="fas fa-shield-alt me-1"></i> <%= session.getAttribute("role") %>
                    </span>
                    <img src="https://ui-avatars.com/api/?name=<%= session.getAttribute("admin") %>&background=00897b&color=fff" alt="Profile" class="rounded-circle shadow-sm" width="40">
                </div>
            </div>

            <div class="row g-4">
                
                <div class="col-lg-8">
                    
                    <div class="glass-card mb-4">
                        <span class="text-muted-custom fw-bold d-block mb-3 text-uppercase tracking-wider" style="font-size: 0.75rem;">Akses Terminal Pintu Gerbang</span>
                        <div class="row g-3">
                            <div class="col-6">
                                <a href="KendaraanMasukServlet" class="btn btn-light w-100 p-3 rounded-4 border border-white text-start d-flex align-items-center justify-content-between shadow-sm transition-hover">
                                    <div>
                                        <h6 class="fw-bold mb-1 text-dark">Pintu MASUK (Entry)</h6>
                                        <span class="text-muted small">Cetak Tiket & Slot Baru</span>
                                    </div>
                                    <div class="icon-box icon-purple m-0"><i class="fas fa-arrow-right-to-bracket"></i></div>
                                </a>
                            </div>
                            <div class="col-6">
                                <a href="ScanKarcisServlet" class="btn btn-light w-100 p-3 rounded-4 border border-white text-start d-flex align-items-center justify-content-between shadow-sm transition-hover">
                                    <div>
                                        <h6 class="fw-bold mb-1 text-dark">Pintu KELUAR (Exit)</h6>
                                        <span class="text-muted small">Scan QR & Hitung Tarif</span>
                                    </div>
                                    <div class="icon-box icon-blue m-0"><i class="fas fa-arrow-right-from-bracket"></i></div>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="glass-card">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <span class="text-muted-custom fw-bold text-uppercase tracking-wider" style="font-size: 0.75rem;">Live Monitor Kendaraan Terparkir</span>
                            <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3 py-1 small">Live Update</span>
                        </div>
                        
                        <div class="table-responsive">
                            <table class="table table-custom table-borderless mb-0 align-middle">
                                <thead>
                                    <tr>
                                        <th>ID TIKET</th>
                                        <th>NOMOR PLAT</th>
                                        <th>JENIS</th>
                                        <th>WAKTU MASUK</th>
                                        <th class="text-center">AKSI</th>
                                        <th class="text-center">KARCIS HILANG</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (dataParkir != null && !dataParkir.isEmpty()) {
                                            for (Tiket t : dataParkir) {
                                    %>
                                            <tr>
                                                <td><span class="badge bg-secondary bg-opacity-10 text-dark font-monospace px-2 py-1"><%= t.getIdTiket() %></span></td>
                                                <td class="fw-bold text-uppercase tracking-wide text-dark"><%= t.getPlatNomor() %></td>
                                                <td>
                                                    <span class="small fw-semibold text-secondary">
                                                        <i class="<%= t.getJenis().equalsIgnoreCase("Mobil") ? "fas fa-car" : "fas fa-motorcycle" %> me-1"></i>
                                                        <%= t.getJenis() %>
                                                    </span>
                                                </td>
                                                <td class="text-muted small"><%= t.getWaktuMasuk() %></td>
                                                <td class="text-center">
                                                    <a href="CheckoutServlet?idTiket=<%= t.getIdTiket() %>" class="btn btn-sm btn-outline-danger rounded-pill px-3 fw-bold shadow-sm">
                                                        <i class="fas fa-cash-register me-1"></i> Out / Bayar
                                                    </a>
                                                </td>
                                                <td>
                                                    <a href="KarcisHilangServlet?idTiket=<%= t.getIdTiket()%>" class="btn btn-warning btn-sm rounded-pill px-3 fw-bold shadow-sm">
                                                        <i class="fas fa-exclamation-triangle me-1"></i> Karcis Hilang
                                                    </a>
                                                </td>
                                            </tr>
                                    <%
                                            }
                                        } else {
                                    %>
                                            <tr>
                                                <td colspan="5" class="text-center py-5 text-muted">
                                                    <i class="fas fa-parking fa-2x mb-2 d-block text-opacity-25"></i>
                                                    Belum ada kendaraan aktif di dalam area parkir mall.
                                                </td>
                                            </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    
                    <div class="glass-card mb-4">
                        <span class="text-muted-custom fw-bold d-block mb-3 text-uppercase tracking-wider" style="font-size: 0.75rem;">Utilitas Ruang Parkir</span>
                        
                        <div class="progress mb-3 shadow-sm" style="height: 10px; background-color: rgba(0,0,0,0.05);">
                            <div class="progress-bar bg-success progress-bar-striped progress-bar-animated" role="progressbar" style="width: <%= persenOkupansi %>%"></div>
                        </div>
                        
                        <div class="d-flex justify-content-between text-center mt-2">
                            <div>
                                <span class="d-block text-muted-custom small" style="font-size: 0.7rem;">KAPASITAS TERISI</span>
                                <span class="fw-bold text-dark fs-5"><%= jumlahAktif %></span>
                            </div>
                            <div class="border-end border-light-subtle"></div>
                            <div>
                                <span class="d-block text-muted-custom small" style="font-size: 0.7rem;">TERSISA (SLOT)</span>
                                <span class="fw-bold text-success fs-5"><%= slotTersedia %></span>
                            </div>
                        </div>
                    </div>

                    <div class="row g-3">
                        <div class="col-6">
                            <div class="glass-card mb-0 p-3 h-100">
                                <div class="icon-box icon-purple mb-2"><i class="fas fa-chart-pie"></i></div>
                                <span class="text-muted-custom small text-uppercase d-block" style="font-size: 0.65rem; font-weight: 700;">Okupansi Lot</span>
                                <h4 class="fw-bold mb-0 text-dark"><%= persenOkupansi %>%</h4>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="glass-card mb-0 p-3 h-100">
                                <div class="icon-box icon-pink mb-2"><i class="fas fa-coins"></i></div>
                                <span class="text-muted-custom small text-uppercase d-block" style="font-size: 0.65rem; font-weight: 700;">Omset Hari Ini</span>
                                <h4 class="fw-bold mb-0 text-dark">Rp <%= String.format("%,d", omsetHariIni).replace(',', '.') %></h4>
                            </div>
                        </div>
                    </div>

                    <div class="glass-card banner-card d-flex align-items-center justify-content-between p-4 mt-4 text-white">
                        <div style="z-index: 2;">
                            <span class="small text-white-50 text-uppercase d-block mb-1 font-monospace" style="font-size: 0.65rem; letter-spacing: 1px;">INFORMASI POS</span>
                            <h6 class="fw-bold mb-2">Integrasi Pembayaran QRIS Cashless Aktif</h6>
                            <span class="badge bg-white text-success rounded-pill px-3 py-1 small fw-bold">ONLINE</span>
                        </div>
                        <i class="fas fa-network-wired fa-3x text-white opacity-25 position-absolute end-0 bottom-0 m-3" style="z-index: 1;"></i>
                    </div>

                </div>
            </div>

        </main>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>