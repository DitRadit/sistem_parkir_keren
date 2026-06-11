<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - ParkirPro SiResKa</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #e0f2f1 0%, #e8eaf6 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
        }
        
        /* Efek Glassmorphism Soft UI */
        .glass-card {
            background: rgba(255, 255, 255, 0.6);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.8);
            border-radius: 1.5rem;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
            padding: 2.5rem;
            width: 100%;
            max-width: 400px;
        }

        .form-control {
            background: rgba(255, 255, 255, 0.5);
            border: 1px solid rgba(255, 255, 255, 0.8);
            border-radius: 0.75rem;
            padding: 0.75rem 1rem;
        }

        .form-control:focus {
            background: rgba(255, 255, 255, 0.8);
            box-shadow: 0 0 0 0.25rem rgba(0, 191, 165, 0.25);
            border-color: #00bfa5;
        }

        .btn-custom {
            background: linear-gradient(135deg, #00897b, #00bfa5);
            color: white;
            border-radius: 0.75rem;
            padding: 0.75rem;
            font-weight: bold;
            border: none;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .btn-custom:hover {
            transform: translateY(-2px);
            color: white;
            box-shadow: 0 4px 15px rgba(0, 191, 165, 0.3);
        }
    </style>
</head>
<body>

    <div class="glass-card">
        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center bg-white rounded-circle shadow-sm mb-3" style="width: 70px; height: 70px;">
                <i class="fas fa-parking fa-2x" style="color: #00897b;"></i>
            </div>
            <h3 class="fw-bold" style="color: #2c3e50; margin-bottom: 2px;">ParkirPro</h3>
            <p class="text-muted small">Sistem Manajemen Parkir</p>
        </div>

        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2 small rounded-3 mb-3" role="alert">
                <i class="fas fa-exclamation-circle me-1"></i> <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="LoginServlet" method="POST">
            <div class="mb-3">
                <label class="form-label text-muted small fw-bold">USERNAME</label>
                <div class="input-group">
                    <span class="input-group-text bg-transparent border-end-0" style="border-color: rgba(255,255,255,0.8);">
                        <i class="fas fa-user text-muted"></i>
                    </span>
                    <input type="text" class="form-control border-start-0 ps-0" name="username" placeholder="Masukkan username" required autocomplete="off">
                </div>
            </div>
            <div class="mb-4">
                <label class="form-label text-muted small fw-bold">PASSWORD</label>
                <div class="input-group">
                    <span class="input-group-text bg-transparent border-end-0" style="border-color: rgba(255,255,255,0.8);">
                        <i class="fas fa-lock text-muted"></i>
                    </span>
                    <input type="password" class="form-control border-start-0 ps-0" name="password" placeholder="Masukkan password" required>
                </div>
            </div>
            <button type="submit" class="btn btn-custom w-100">
                <i class="fas fa-sign-in-alt me-2"></i> LOGIN MASUK
            </button>
        </form>
    </div>

</body>
</html>