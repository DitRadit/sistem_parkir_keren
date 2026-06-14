<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList, model.Admin" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (!"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect("dashboard");
        return;
    }

    ArrayList<Admin> daftarUser = (ArrayList<Admin>) request.getAttribute("daftarUser");
    Admin editUser = (Admin) request.getAttribute("editUser");
    String errorPage = (String) request.getAttribute("errorPage");
    String currentAdminId = (String) session.getAttribute("adminId");
    boolean isEditMode = (editUser != null);
%>

<%@ include file="includes/header.jsp" %>

<%-- Flash Message Handler --%>
<%
    String flashSuccess = (String) session.getAttribute("flashSuccess");
    String flashError   = (String) session.getAttribute("flashError");
    if (flashSuccess != null) session.removeAttribute("flashSuccess");
    if (flashError   != null) session.removeAttribute("flashError");
%>
<% if (flashSuccess != null) { %>
<script>
document.addEventListener("DOMContentLoaded", function () {
    Swal.fire({ icon:'success', title:'Berhasil!', text:'<%= flashSuccess.replace("'","\\'") %>', toast:true, position:'top-end', timer:3000, timerProgressBar:true, showConfirmButton:false });
});
</script>
<% } %>
<% if (flashError != null) { %>
<script>
document.addEventListener("DOMContentLoaded", function () {
    Swal.fire({ icon:'error', title:'Gagal!', text:'<%= flashError.replace("'","\\'") %>', toast:true, position:'top-end', timer:4000, timerProgressBar:true, showConfirmButton:false });
});
</script>
<% } %>

<style>
    .user-card {
        background: #ffffff;
        border-radius: 1rem;
        box-shadow: 0 4px 20px rgba(0,0,0,0.05);
        border: 1px solid rgba(0,0,0,0.06);
    }
    .badge-admin {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        font-size: 0.7rem;
        padding: 4px 10px;
        border-radius: 20px;
        font-weight: 600;
    }
    .badge-operator {
        background: linear-gradient(135deg, #00897b, #00bfa5);
        color: white;
        font-size: 0.7rem;
        padding: 4px 10px;
        border-radius: 20px;
        font-weight: 600;
    }
    .form-card {
        background: #f8fafc;
        border: 1px dashed #cbd5e1;
        border-radius: 1rem;
        padding: 1.5rem;
    }
    .form-card.edit-mode {
        background: #fffbeb;
        border-color: #f59e0b;
    }
    .avatar-circle {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        font-size: 1rem;
        color: white;
    }
    .btn-action { padding: 4px 12px; font-size: 0.78rem; border-radius: 8px; }
</style>

<div class="container-fluid">
    <div class="row">
        <%@ include file="includes/sidebar.jsp" %>

        <main class="col-md-10 ms-sm-auto px-md-5 py-4">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <p class="text-muted-custom mb-0 small">PENGATURAN SISTEM</p>
                    <h3 class="fw-bold mb-0 text-dark"><i class="fas fa-users-cog text-primary me-2"></i>Manajemen Pengguna</h3>
                </div>
                <a href="dashboard" class="btn btn-sm btn-light rounded-pill px-3 border shadow-sm fw-bold">
                    <i class="fas fa-arrow-left me-1"></i> Kembali
                </a>
            </div>

            <% if (errorPage != null) { %>
            <div class="alert alert-danger rounded-3"><i class="fas fa-exclamation-triangle me-2"></i><%= errorPage %></div>
            <% } %>

            <div class="row g-4">

                <%-- ===== FORM TAMBAH / EDIT ===== --%>
                <div class="col-lg-4">
                    <div class="form-card <%= isEditMode ? "edit-mode" : "" %>">
                        <h6 class="fw-bold mb-3 text-dark">
                            <% if (isEditMode) { %>
                                <i class="fas fa-user-edit text-warning me-2"></i>Edit User: <span class="text-warning"><%= editUser.getNama() %></span>
                            <% } else { %>
                                <i class="fas fa-user-plus text-primary me-2"></i>Tambah User Baru
                            <% } %>
                        </h6>

                        <form action="UserManagementServlet" method="POST">
                            <input type="hidden" name="act" value="<%= isEditMode ? "edit" : "add" %>">
                            <% if (isEditMode) { %>
                            <input type="hidden" name="idAdmin" value="<%= editUser.getIdAdmin() %>">
                            <% } %>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Nama Lengkap</label>
                                <input type="text" name="nama" class="form-control rounded-3"
                                    placeholder="contoh: Budi Santoso"
                                    value="<%= isEditMode ? editUser.getNama() : "" %>"
                                    required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Username</label>
                                <div class="input-group">
                                    <span class="input-group-text rounded-start-3 bg-white"><i class="fas fa-at text-muted"></i></span>
                                    <input type="text" name="username" class="form-control rounded-end-3"
                                        placeholder="username unik"
                                        value="<%= isEditMode ? editUser.getUsername() : "" %>"
                                        required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">
                                    Password <% if (isEditMode) { %><span class="text-muted fw-normal">(kosongkan jika tidak ganti)</span><% } %>
                                </label>
                                <div class="input-group">
                                    <span class="input-group-text rounded-start-3 bg-white"><i class="fas fa-lock text-muted"></i></span>
                                    <input type="password" name="password" id="passwordInput" class="form-control"
                                        placeholder="<%= isEditMode ? "••••••• (opsional)" : "minimal 6 karakter" %>"
                                        <%= isEditMode ? "" : "required" %>>
                                    <button type="button" class="btn btn-outline-secondary rounded-end-3"
                                        onclick="togglePass()"><i class="fas fa-eye" id="eyeIcon"></i></button>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-semibold small text-muted">Role</label>
                                <select name="role" class="form-select rounded-3" required>
                                    <option value="">-- Pilih Role --</option>
                                    <option value="ADMIN"    <%= isEditMode && "ADMIN".equals(editUser.getRole().name())    ? "selected" : "" %>>👑 ADMIN</option>
                                    <option value="OPERATOR" <%= isEditMode && "OPERATOR".equals(editUser.getRole().name()) ? "selected" : "" %>>🎫 OPERATOR</option>
                                </select>
                                <div class="mt-2 small text-muted">
                                    <i class="fas fa-info-circle me-1"></i>
                                    ADMIN dapat mengakses manajemen user. OPERATOR hanya akses operasional parkir.
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn fw-bold rounded-3 py-2
                                    <%= isEditMode ? "btn-warning" : "btn-primary" %>"
                                    style="<%= isEditMode ? "background: linear-gradient(135deg,#f59e0b,#d97706); border:none;" : "background: linear-gradient(135deg,#00897b,#00bfa5); border:none;" %>">
                                    <i class="fas fa-<%= isEditMode ? "save" : "plus-circle" %> me-2"></i>
                                    <%= isEditMode ? "Simpan Perubahan" : "Tambah User" %>
                                </button>
                                <% if (isEditMode) { %>
                                <a href="UserManagementServlet" class="btn btn-light border rounded-3 fw-bold">
                                    <i class="fas fa-times me-1"></i> Batal Edit
                                </a>
                                <% } %>
                            </div>
                        </form>
                    </div>

                    <%-- Info RBAC --%>
                    <div class="mt-3 p-3 rounded-3 bg-white border small text-muted">
                        <div class="fw-bold mb-2 text-dark"><i class="fas fa-shield-alt text-primary me-1"></i>Hak Akses (RBAC)</div>
                        <div class="mb-1"><span class="badge-admin me-1">ADMIN</span> Dashboard, Manajemen User, semua fitur</div>
                        <div><span class="badge-operator me-1">OPERATOR</span> Pintu Masuk, Pintu Keluar, Pembayaran</div>
                    </div>
                </div>

                <%-- ===== TABEL DAFTAR USER ===== --%>
                <div class="col-lg-8">
                    <div class="user-card">
                        <div class="p-4 border-bottom d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="fw-bold mb-0 text-dark">Daftar Pengguna Sistem</h6>
                                <small class="text-muted">
                                    Total: <strong><%= daftarUser != null ? daftarUser.size() : 0 %></strong> akun terdaftar
                                </small>
                            </div>
                            <span class="badge bg-primary bg-opacity-10 text-primary rounded-pill px-3">
                                <i class="fas fa-users me-1"></i>User List
                            </span>
                        </div>

                        <div class="p-3">
                            <% if (daftarUser == null || daftarUser.isEmpty()) { %>
                            <div class="text-center py-5 text-muted">
                                <i class="fas fa-user-slash fa-3x mb-3 opacity-25"></i>
                                <p class="fw-semibold">Belum ada user terdaftar</p>
                            </div>
                            <% } else { %>
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">
                                    <thead>
                                        <tr class="text-muted small" style="font-size:0.72rem; letter-spacing:0.5px;">
                                            <th class="pb-3">PENGGUNA</th>
                                            <th class="pb-3">USERNAME</th>
                                            <th class="pb-3">ROLE</th>
                                            <th class="pb-3">ID</th>
                                            <th class="pb-3 text-end">AKSI</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Admin u : daftarUser) {
                                            boolean isSelf = u.getIdAdmin().equals(currentAdminId);
                                            String[] colors = {"#00897b","#3b82f6","#a855f7","#f59e0b","#ef4444"};
                                            int colorIdx = Math.abs(u.getNama().hashCode()) % colors.length;
                                            String avatarColor = colors[colorIdx];
                                            char initial = u.getNama().isEmpty() ? '?' : u.getNama().charAt(0);
                                        %>
                                        <tr class="<%= isSelf ? "table-success" : "" %>">
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <div class="avatar-circle" style="background:<%= avatarColor %>; min-width:40px;">
                                                        <%= initial %>
                                                    </div>
                                                    <div>
                                                        <div class="fw-semibold text-dark small"><%= u.getNama() %></div>
                                                        <% if (isSelf) { %><div class="text-success" style="font-size:0.65rem;"><i class="fas fa-circle-check me-1"></i>Akun Anda</div><% } %>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="font-monospace small text-muted"><%= u.getUsername() %></td>
                                            <td>
                                                <% if ("ADMIN".equals(u.getRole().name())) { %>
                                                    <span class="badge-admin">👑 ADMIN</span>
                                                <% } else { %>
                                                    <span class="badge-operator">🎫 OPERATOR</span>
                                                <% } %>
                                            </td>
                                            <td class="font-monospace text-muted" style="font-size:0.68rem;"><%= u.getIdAdmin() %></td>
                                            <td class="text-end">
                                                <div class="d-flex gap-1 justify-content-end">
                                                    <a href="UserManagementServlet?editId=<%= u.getIdAdmin() %>"
                                                       class="btn btn-sm btn-warning btn-action fw-bold">
                                                        <i class="fas fa-pen"></i>
                                                    </a>
                                                    <% if (!isSelf) { %>
                                                    <button type="button"
                                                        class="btn btn-sm btn-danger btn-action fw-bold"
                                                        onclick="confirmDelete('<%= u.getIdAdmin() %>', '<%= u.getNama().replace("'","\\'") %>')">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                    <% } else { %>
                                                    <button class="btn btn-sm btn-secondary btn-action" disabled title="Tidak bisa hapus akun sendiri">
                                                        <i class="fas fa-ban"></i>
                                                    </button>
                                                    <% } %>
                                                </div>
                                            </td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    </div>
</div>

<%-- Form hidden untuk delete (POST) --%>
<form id="deleteForm" action="UserManagementServlet" method="POST" style="display:none;">
    <input type="hidden" name="act" value="delete">
    <input type="hidden" name="idAdmin" id="deleteIdAdmin" value="">
</form>

<script>
function confirmDelete(idAdmin, nama) {
    Swal.fire({
        title: 'Hapus User?',
        html: 'Akun <strong>' + nama + '</strong> akan dihapus permanen.<br><small class="text-muted">Tindakan ini tidak dapat dibatalkan.</small>',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#6b7280',
        confirmButtonText: '<i class="fas fa-trash me-1"></i>Ya, Hapus!',
        cancelButtonText: 'Batal',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById('deleteIdAdmin').value = idAdmin;
            document.getElementById('deleteForm').submit();
        }
    });
}

function togglePass() {
    const inp = document.getElementById('passwordInput');
    const ico = document.getElementById('eyeIcon');
    if (inp.type === 'password') {
        inp.type = 'text';
        ico.className = 'fas fa-eye-slash';
    } else {
        inp.type = 'password';
        ico.className = 'fas fa-eye';
    }
}
</script>

<%@ include file="includes/footer.jsp" %>
