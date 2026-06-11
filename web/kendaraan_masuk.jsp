<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp"); 
        return;
    }
    // Menangkap status kapasitas dari KendaraanMasukServlet
    Boolean parkirPenuh = (Boolean) request.getAttribute("parkirPenuh");
    if(parkirPenuh == null) parkirPenuh = false;
%>
<%@ include file="includes/header.jsp" %>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">
            
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <p class="text-muted-custom mb-0">POS GERBANG MASUK MALL</p>
                    <h3 class="fw-bold mb-0 text-dark"><i class="fas fa-arrow-alt-circle-right text-success me-2"></i>Terminal Entry #01</h3>
                </div>
                <div class="text-end">
                    <span class="badge bg-dark bg-opacity-75 rounded-pill px-3 py-2">
                        <i class="fas fa-user-clock me-1"></i> Operator: <%= session.getAttribute("admin_nama") %>
                    </span>
                </div>
            </div>

            <% if(request.getAttribute("error") != null) { %>
                <div class="alert alert-danger glass-card py-2 mb-4" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i> <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="glass-card p-4 h-100">
                        <h5 class="fw-bold mb-4 text-secondary"><i class="fas fa-keyboard me-2"></i>Input Data Kendaraan</h5>
                        
                        <% if(parkirPenuh) { %>
                            <div class="text-center p-5 bg-danger bg-opacity-10 rounded-4 border border-danger border-opacity-25 my-4">
                                <i class="fas fa-ban fa-3x text-danger mb-3"></i>
                                <h4 class="fw-bold text-danger">KAPASITAS PENUH!</h4>
                                <p class="text-muted mb-0">Palang pintu otomatis terkunci hingga ada kendaraan yang keluar.</p>
                            </div>
                        <% } else { %>
                            <form action="KendaraanMasukServlet" method="POST">
                                <div class="mb-4">
                                    <label for="platNomor" class="form-label text-muted fw-bold small">NOMOR PLAT KENDARAAN</label>
                                    <input type="text" class="form-control form-control-lg text-uppercase fw-bold text-center border-2 border-primary border-opacity-25 shadow-sm" 
                                           id="platNomor" name="platNomor" placeholder="B 1234 ABC" 
                                           style="font-size: 2.5rem; letter-spacing: 5px; height: 80px;" autocomplete="off" required autofocus>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label text-muted fw-bold small">JENIS KENDARAAN</label>
                                    <div class="row g-3">
                                        <div class="col-6">
                                            <input type="radio" class="btn-check" name="jenis" id="motor" value="Motor" checked>
                                            <label class="btn btn-outline-primary w-100 py-4 rounded-4 shadow-sm fw-bold fs-5" for="motor">
                                                <i class="fas fa-motorcycle fa-2x d-block mb-2"></i> SEPEDAMOTOR
                                            </label>
                                        </div>
                                        <div class="col-6">
                                            <input type="radio" class="btn-check" name="jenis" id="mobil" value="Mobil">
                                            <label class="btn btn-outline-primary w-100 py-4 rounded-4 shadow-sm fw-bold fs-5" for="mobil">
                                                <i class="fas fa-car fa-2x d-block mb-2"></i> MOBIL / RODA 4
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-success btn-lg w-100 rounded-4 fw-bold py-3 fs-4 shadow mt-2" 
                                        style="background: linear-gradient(135deg, #00897b, #00bfa5); border: none;">
                                    <i class="fas fa-print me-2"></i> CETAK TIKET & BUKA PINTU
                                </button>
                            </form>
                        <% } %>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="glass-card p-4 bg-white bg-opacity-25 h-100 d-flex flex-column justify-content-between">
                        <div>
                            <h5 class="fw-bold mb-4 text-secondary"><i class="fas fa-parking me-2"></i>Kondisi Slot Parkir Real-Time</h5>
                            
                            <div class="p-3 rounded-4 bg-white bg-opacity-70 border mb-3 shadow-sm text-center">
                                <span class="text-muted small d-block text-uppercase fw-bold">Total Kendaraan di Dalam</span>
                                <h1 class="fw-bold text-primary display-4 mb-0"><%= request.getAttribute("jumlahAktif") != null ? request.getAttribute("jumlahAktif") : "0" %></h1>
                                <span class="text-muted small">Dari total kapasitas <%= request.getAttribute("kapasitasMaks") != null ? request.getAttribute("kapasitasMaks") : "500" %> slot</span>
                            </div>

                            <div class="mb-4 px-2">
                                <div class="progress rounded-pill" style="height: 12px;">
                                    <div class="progress-bar bg-success progress-bar-striped progress-bar-animated" role="progressbar" style="width: 65%"></div>
                                </div>
                            </div>
                        </div>

                        <div class="p-3 bg-primary bg-opacity-10 rounded-4 border border-primary border-opacity-25 text-primary small">
                            <h6 class="fw-bold mb-2"><i class="fas fa-info-circle"></i> SOP Operator Gerbang:</h6>
                            <ul class="mb-0 ps-3">
                                <li>Pastikan plat nomor yang diketik sesuai fisik kendaraan.</li>
                                <li>Jika printer karcis macet, gunakan menu cetak ulang di dashboard.</li>
                                <li>Gunakan tombol pintas keyboard untuk mempercepat transaksi.</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>