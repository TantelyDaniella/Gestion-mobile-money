<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, Model.FraisRecep" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des frais de retrait</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Cormorant Garamond', 'Playfair Display', Georgia, 'Times New Roman', serif;
        }

        body {
            background: #faf5ff;
            color: #4a2e5c;
            min-height: 100vh;
            overflow-x: hidden;
            position: relative;
        }

        /* Dégradé d'arrière-plan */
        body::after {
            content: '';
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, #f9f2ff, #f3eaff);
            z-index: -2;
        }

        /* Motif Dots en arrière-plan */
        body::before {
            content: '';
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-image: radial-gradient(rgba(170, 126, 208, 0.08) 1px, transparent 1px);
            background-size: 30px 30px;
            pointer-events: none;
            z-index: -1;
        }

        /* === Barre du haut === */
        .topbar {
            background: linear-gradient(135deg, #9b6fcf, #c692f0);
            color: white;
            height: 70px;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            box-shadow: 0 4px 15px rgba(155, 111, 207, 0.2);
        }
        .topbar h1 {
            font-size: 1.5em;
            font-weight: 600;
            text-align: center;
            margin: 0 auto;
            letter-spacing: 2px;
            font-style: italic;
        }
        .topbar a {
            position: absolute;
            left: 20px;
            top: 18px;
            color: white;
            border: none;
            border-radius: 0;
            padding: 8px 12px;
            cursor: pointer;
            text-decoration: none;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.3rem;
            transition: all 0.3s ease;
        }
        .topbar a:hover {
            background: rgba(255,255,255,0.3);
            transform: scale(1.05);
        }

        /* === Conteneur principal === */
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
            position: relative;
            z-index: 1;
        }

        /* === Carte === */
        .card {
            background: #ffffff;
            border-radius: 20px;
            padding: 1.8rem;
            margin-bottom: 2rem;
            border-bottom: 1px solid rgba(170, 126, 208, 0.15);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
            transition: all 0.3s ease;
        }
        .card:hover {
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.05);
        }

        /* === En-tête de la carte === */
        .card-header {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 1.5rem;
        }

        .btn {
            border: none;
            border-radius: 30px;
            padding: 10px 24px;
            cursor: pointer;
            font-family: inherit;
            font-weight: 500;
            transition: all 0.3s ease;
            letter-spacing: 1px;
        }
        .btn.primary {
            background: linear-gradient(135deg, #9b6fcf, #c692f0);
            color: white;
        }
        .btn.primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(155, 111, 207, 0.4);
        }

        /* === Table === */
        .table-wrapper {
            overflow-x: auto;
        }
        .data-table {
            width: 100%;
            border-collapse: collapse;
        }
        .data-table th, .data-table td {
            text-align: center;
            padding: 12px 10px;
            border-bottom: 1px solid rgba(170, 126, 208, 0.15);
        }
        .data-table th {
            background: rgba(233, 217, 255, 0.3);
            font-weight: 600;
            color: #5e3a7c;
            letter-spacing: 1px;
        }
        .data-table td.actions {
            display: flex;
            gap: 8px;
            justify-content: center;
        }
        .data-table button {
            border: none;
            padding: 6px 12px;
            border-radius: 20px;
            cursor: pointer;
            font-size: 0.8em;
            font-family: inherit;
            transition: all 0.3s ease;
        }
        .data-table .edit {
            background: linear-gradient(135deg, #9b6fcf, #c692f0);
            color: white;
        }
        .data-table .edit:hover {
            transform: scale(1.05);
        }
        .data-table .delete {
            background: #e74c3c;
            color: white;
        }
        .data-table .delete:hover {
            transform: scale(1.05);
        }

        /* === Popup formulaire === */
        #form-card {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 2rem;
            border-radius: 24px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.2);
            z-index: 1000;
            width: 500px;
            max-width: 90%;
            max-height: 85vh;
            overflow-y: auto;
        }
        #form-card h2 {
            margin-bottom: 1.5rem;
            color: #9b6fcf;
            text-align: center;
            font-style: italic;
            letter-spacing: 1px;
        }
        .frais-form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        .row {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        .row label {
            font-size: 0.85rem;
            color: #765b91;
            letter-spacing: 1px;
        }
        .row input {
            padding: 10px 14px;
            border-radius: 12px;
            border: 1px solid rgba(170, 126, 208, 0.3);
            outline: none;
            font-family: inherit;
            transition: all 0.3s ease;
        }
        .row input:focus {
            border-color: #9b6fcf;
            box-shadow: 0 0 0 2px rgba(155, 111, 207, 0.2);
        }
        .form-actions {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            margin-top: 1rem;
        }
        .form-actions .btn {
            flex: 1;
        }

        /* === Overlay === */
        #overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.3);
            z-index: 999;
        }

        /* === MessageBox flottante === */
        .message-box {
            position: fixed;
            top: 100px;
            left: 50%;
            transform: translateX(-50%);
            padding: 12px 24px;
            border-radius: 40px;
            font-size: 0.9rem;
            color: white;
            z-index: 2000;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            opacity: 0;
            transition: opacity 0.3s ease;
            font-family: inherit;
            letter-spacing: 1px;
        }
        .message-box.success {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
        }
        .message-box.error {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
        }

        /* === Footer === */
        .footer {
            text-align: center;
            padding: 1.5rem 0;
            border-top: 1px solid rgba(170, 126, 208, 0.15);
            color: #765b91;
            font-size: 0.7rem;
            letter-spacing: 2px;
        }
        
        /* === Animation fade-in === */
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .fade-in {
            animation: fadeIn 0.6s ease forwards;
        }
        
        /* === Responsive === */
        @media (max-width: 768px) {
            .container { padding: 0 1rem; }
            .topbar h1 { font-size: 1.1rem; }
            .data-table th, .data-table td { padding: 8px 6px; font-size: 0.85rem; }
        }
        
        .data-table td:nth-child(2),
        .data-table td:nth-child(3),
        .data-table td:nth-child(4),input[type="number"] {
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <div class="topbar">
        <a href="MobileMoney.jsp">🏠</a>
        <h1>💰 Gestion des frais de retrait</h1>
    </div>

    <div class="container">
        <div class="card fade-in">
            <div class="card-header">
                <button class="btn primary" onclick="showForm('add')">➕ Ajouter un barème</button>
            </div>
            <div class="table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Montant min (Ar)</th>
                            <th>Montant max (Ar)</th>
                            <th>Frais de retrait (Ar)</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<FraisRecep> fraisList = (List<FraisRecep>) request.getAttribute("listeFraisRecep");
                            if (fraisList != null && !fraisList.isEmpty()) {
                                for (FraisRecep f : fraisList) {
                        %>
                        <tr>
                            <td><%= f.getIdRec() %></td>
                            <td><%= String.format("%,d", f.getMontant1()) %></td>
                            <td><%= String.format("%,d", f.getMontant2()) %></td>
                            <td><%= String.format("%,d", f.getFraisRec()) %></td>
                            <td class="actions">
                                <button class="edit" onclick="showForm('edit', this)">✏️ Modifier</button>
                                <button class="delete" onclick="confirmDelete(this)">🗑️ Supprimer</button>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr><td colspan="5" style="text-align: center; padding: 2rem;">Aucun barème de frais de retrait trouvé</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Formulaire popup -->
    <div id="overlay" onclick="hideForm()"></div>
    <div id="form-card">
        <h2 id="form-title">Ajouter / Modifier un barème</h2>
        <form class="frais-form" method="post" action="FraisRecepServlet" id="frais-form">
            <input type="hidden" name="action" id="form-action" value="ajouter">
            <input type="hidden" id="idrec" name="idrec">

            <div class="row">
                <label for="montant1">Montant minimum (Ar) *</label>
                <input type="number" id="montant1" name="montant1" required min="0" step="1">
            </div>

            <div class="row">
                <label for="montant2">Montant maximum (Ar) *</label>
                <input type="number" id="montant2" name="montant2" required min="0" step="1">
            </div>

            <div class="row">
                <label for="fraisrec">Frais de retrait (Ar) *</label>
                <input type="number" id="fraisrec" name="fraisrec" required min="0" step="1">
            </div>

            <div class="form-actions">
                <button type="submit" class="btn primary" id="submit-btn">💾 Enregistrer</button>
                <button type="button" class="btn" onclick="hideForm()" style="background: #e1e5ea;">❌ Annuler</button>
            </div>
        </form>
    </div>

    <div class="footer">
        <p>© 2026 Mobile Money - Gestion des frais de retrait</p>
    </div>

    <script>
        let currentAction = 'add';

        function showForm(type, btn = null) {
            const formCard = document.getElementById("form-card");
            const overlay = document.getElementById("overlay");
            const formTitle = document.getElementById("form-title");
            const formAction = document.getElementById("form-action");

            formCard.style.display = "block";
            overlay.style.display = "block";

            if (type === "add") {
                formTitle.textContent = "Ajouter un barème de frais de retrait";
                formAction.value = "ajouter";
                document.getElementById("frais-form").reset();
                document.getElementById("idrec").value = "";
                currentAction = 'add';
            } else if (type === "edit" && btn) {
                const row = btn.closest("tr");
                formTitle.textContent = "Modifier un barème de frais de retrait";
                formAction.value = "modifier";

                const idrec = row.cells[0].textContent.trim();
                const montant1 = row.cells[1].textContent.trim().replace(/,/g, '').replace(/\s/g, '');
                const montant2 = row.cells[2].textContent.trim().replace(/,/g, '').replace(/\s/g, '');
                const fraisrec = row.cells[3].textContent.trim().replace(/,/g, '').replace(/\s/g, '');

                document.getElementById("idrec").value = idrec;
                document.getElementById("montant1").value = montant1;
                document.getElementById("montant2").value = montant2;
                document.getElementById("fraisrec").value = fraisrec;
                currentAction = 'edit';
            }
        }

        function hideForm() {
            document.getElementById("form-card").style.display = "none";
            document.getElementById("overlay").style.display = "none";
            document.getElementById("frais-form").reset();
        }

        function showTempMessage(message, type) {
            const existingMsg = document.querySelector(".message-box");
            if (existingMsg) existingMsg.remove();

            const msgBox = document.createElement("div");
            msgBox.classList.add("message-box", type);
            msgBox.textContent = message;
            document.body.appendChild(msgBox);

            setTimeout(() => {
                msgBox.style.opacity = "1";
            }, 100);

            setTimeout(() => {
                msgBox.style.opacity = "0";
                setTimeout(() => msgBox.remove(), 300);
            }, 5000);
        }

        function confirmDelete(btn) {
            const row = btn.closest("tr");
            const idrec = row.cells[0].textContent.trim();
            const montant1 = row.cells[1].textContent.trim();
            const montant2 = row.cells[2].textContent.trim();

            if (confirm("Voulez-vous vraiment supprimer le barème : " + montant1 + " Ar - " + montant2 + " Ar ?")) {
                window.location.href = "FraisRecepServlet?action=supprimer&idrec=" + encodeURIComponent(idrec);
            }
        }

        // Validation du formulaire
        document.getElementById("frais-form").addEventListener("submit", function(e) {
            const montant1 = parseInt(document.getElementById("montant1").value);
            const montant2 = parseInt(document.getElementById("montant2").value);
            const fraisrec = parseInt(document.getElementById("fraisrec").value);

            if (isNaN(montant1) || montant1 < 0) {
                e.preventDefault();
                showTempMessage("Le montant minimum doit être un nombre positif", "error");
                return;
            }

            if (isNaN(montant2) || montant2 <= montant1) {
                e.preventDefault();
                showTempMessage("Le montant maximum doit être supérieur au montant minimum", "error");
                return;
            }

            if (isNaN(fraisrec) || fraisrec < 0) {
                e.preventDefault();
                showTempMessage("Les frais de retrait doivent être un nombre positif", "error");
                return;
            }
        });

        // Message flash
        window.onload = function() {
            <% if (request.getAttribute("message") != null) { %>
                showTempMessage("<%= request.getAttribute("message") %>", "<%= request.getAttribute("typeMessage") %>");
            <% } %>
        };
    </script>
</body>
</html>