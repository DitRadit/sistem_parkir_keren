<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistem Parkir Mall - ParkirPro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #e0f2f1 0%, #e8eaf6 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            margin: 0;
        }
        /* Glassmorphism Global Styles */
        .glass-card {
            background: rgba(255, 255, 255, 0.55);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.7);
            border-radius: 1.25rem;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
            padding: 1.5rem;
        }
        .sidebar-glass {
            background: rgba(255, 255, 255, 0.4);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border-right: 1px solid rgba(255, 255, 255, 0.5);
            min-height: 100vh;
        }
        .text-muted-custom {
            color: #6c757d;
            font-weight: 600;
        }
        .icon-box {
            width: 42px;
            height: 42px;
            border-radius: 0.75rem;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.2rem;
            margin-bottom: 1rem;
        }
        .icon-purple { background: #ebd5ff; color: #a855f7; }
        .icon-blue { background: #dbeafe; color: #3b82f6; }
        .icon-pink { background: #fce7f3; color: #ec4899; }
        
        .table-custom {
            background: transparent !important;
        }
        .table-custom th {
            font-size: 0.75rem;
            letter-spacing: 0.5px;
            color: #7c8ba1;
            padding: 12px;
        }
        .table-custom td {
            padding: 16px 12px;
            background: rgba(255, 255, 255, 0.3) !important;
            border-top: 4px solid #f1f5f9 !important; /* Spacing effect */
        }
        .table-custom tr:first-child td {
            border-top: none !important;
        }
        .nav-link-custom {
            color: #4a5568;
            font-weight: 600;
            padding: 0.8rem 1rem;
            border-radius: 0.75rem;
            display: flex;
            align-items: center;
            gap: 10px;
            transition: all 0.2s;
            text-decoration: none;
        }
        .nav-link-custom:hover, .nav-link-custom.active {
            background: rgba(0, 137, 123, 0.1);
            color: #00897b;
        }
        .banner-card {
            background: linear-gradient(135deg, #00897b, #00bfa5) !important;
            border: none !important;
            position: relative;
            overflow: hidden;
        }
    </style>
</head>
<body>