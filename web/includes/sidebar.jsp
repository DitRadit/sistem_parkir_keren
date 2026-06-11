<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="col-md-2 d-none d-md-block sidebar-glass px-3 py-4">
    <div class="d-flex align-items-center gap-2 mb-4 px-2">
        <div class="bg-primary bg-gradient text-white rounded-3 d-flex align-items-center justify-content-center" style="width: 35px; height: 35px;">
            <i class="fas fa-parking"></i>
        </div>
        <h5 class="fw-bold mb-0 text-dark">ParkirPro Mall</h5>
    </div>
    
    <div class="d-flex flex-column gap-1">
        <span class="text-muted small px-2 mb-2 tracking-wider" style="font-size: 0.65rem; font-weight: 700;">MENU UTAMA</span>
        
        <a href="dashboard" class="nav-link-custom">
            <i class="fas fa-columns" style="width: 20px;"></i> Dashboard
        </a>
        <a href="KendaraanMasukServlet" class="nav-link-custom">
            <i class="fas fa-arrow-right-to-bracket" style="width: 20px;"></i> Pintu Masuk (Entry)
        </a>
        <a href="ScanKarcisServlet" class="nav-link-custom">
            <i class="fas fa-arrow-right-from-bracket" style="width: 20px;"></i> Pintu Keluar (Exit)
        </a>
        
        <hr class="text-muted opacity-25 my-3">
        
        <a href="LogoutServlet" class="nav-link-custom text-danger">
            <i class="fas fa-sign-out-alt" style="width: 20px;"></i> Keluar Sistem
        </a>
    </div>
</nav>