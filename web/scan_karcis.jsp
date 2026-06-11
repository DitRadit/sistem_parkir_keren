<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(session.getAttribute("admin") == null){
        response.sendRedirect("login.jsp"); 
        return;
    }
%>
<%@ include file="includes/header.jsp" %>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">
            
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <p class="text-muted-custom mb-0">POS GERBANG KELUAR MALL</p>
                    <h3 class="fw-bold mb-0 text-dark"><i class="fas fa-arrow-alt-circle-left text-danger me-2"></i>Terminal Exit #01</h3>
                </div>
                <div class="text-end">
                    <span class="badge bg-dark bg-opacity-75 rounded-pill px-3 py-2">
                        <i class="fas fa-user-clock me-1"></i> Operator: <%= session.getAttribute("admin_nama") %>
                    </span>
                </div>
            </div>

            <% if(request.getAttribute("error") != null) { %>
                <div class="alert alert-danger glass-card py-2 mb-4" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i> <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <div class="row g-4 justify-content-center mt-2">
                <div class="col-md-6">
                    <div class="glass-card text-center p-5">
                        <div class="icon-box icon-purple mx-auto mb-4" style="width: 80px; height: 80px; font-size: 2.5rem; background: #ebd5ff; color: #a855f7;">
                            <i class="fas fa-qrcode"></i>
                        </div>
                        <h4 class="fw-bold text-dark mb-2">Scan Karcis Pelanggan</h4>
                        <p class="text-muted small mb-4">Arahkan barcode/QR karcis ke pemindai atau unggah file gambar karcis di bawah ini.</p>

                        <form action="ScanKarcisServlet" method="POST" enctype="multipart/form-data">
                            
                            <div class="mb-4 p-4 rounded-4 border border-2 border-dashed border-primary border-opacity-25" 
                                 style="background: rgba(255,255,255,0.4); border-style: dashed !important;">
                                <i class="fas fa-cloud-upload-alt text-primary opacity-50 fa-2x mb-2"></i>
                                <input class="form-control" type="file" id="qrImage" name="qrImage" accept="image/*" required>
                                <span class="text-muted d-block mt-1" style="font-size: 0.75rem;">Mendukung format gambar PNG, JPG, JPEG</span>
                            </div>

                            <button type="submit" class="btn btn-primary rounded-4 py-3 fw-bold w-100 shadow fs-5"
                                    style="background: linear-gradient(135deg, #6366f1, #a855f7); border: none;">
                                <i class="fas fa-search-dollar me-2"></i> HITUNG TOTAL TARIF
                            </button>
                        </form>

                        <div class="relative flex py-3 items-center my-3">
                            <div class="flex-grow border-top text-muted opacity-25"></div>
                            <span class="flex-shrink mx-3 text-muted small fw-bold">ATAU INPUT MANUAL</span>
                            <div class="flex-grow border-top text-muted opacity-25"></div>
                        </div>

                        <form action="CheckoutServlet" method="GET" class="d-flex gap-2">
                            <input type="text" class="form-control rounded-pill text-center font-monospace" 
                                   name="idTiket" placeholder="Masukkan ID Tiket (Contoh: TKT12345)" required autocomplete="off">
                            <button type="submit" class="btn btn-dark rounded-pill px-4 fw-bold">Proses</button>
                        </form>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>