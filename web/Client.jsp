<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, Model.Client" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Clients</title>
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
        .top-actions {
            position: absolute;
            right: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .top-actions input {
            padding: 8px 12px;
            border-radius: 30px;
            border: none;
            outline: none;
            font-family: inherit;
        }
        .top-actions button {
            border: none;
            border-radius: 30px;
            padding: 8px 16px;
            cursor: pointer;
            background: rgba(255,255,255,0.2);
            color: white;
            font-family: inherit;
            transition: all 0.3s ease;
        }
        .top-actions button:hover {
            background: rgba(255,255,255,0.4);
        }

        /* === Conteneur principal === */
        .container {
            max-width: 1400px;
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
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            flex-wrap: wrap;
            gap: 10px;
        }
        .card-header .btn-group {
            display: flex;
            gap: 10px;
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
        .btn.secondary {
            background: #e1e5ea;
            color: #4a2e5c;
        }
        .btn.secondary:hover {
            background: #d0d5dc;
        }
        .btn.pdf {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            color: white;
        }
        .btn.pdf:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
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
            flex-wrap: wrap;
        }
        .data-table button {
            border: none;
            padding: 6px 12px;
            border-radius: 20px;
            cursor: pointer;
            font-size: 0.75em;
            font-family: inherit;
            transition: all 0.3s ease;
            white-space: nowrap;
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
        .data-table .pdf-btn {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            color: white;
        }
        .data-table .pdf-btn:hover {
            transform: scale(1.05);
        }

        /* === Popup formulaire === */
        .popup-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 998;
        }
        .form-popup {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            border-radius: 24px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
            z-index: 999;
            width: 500px;
            max-width: 90%;
            max-height: 85vh;
            overflow-y: auto;
        }
        .form-popup .form-header {
            padding: 1.5rem 1.5rem 0 1.5rem;
            border-bottom: 1px solid rgba(170, 126, 208, 0.15);
        }
        .form-popup h2 {
            color: #9b6fcf;
            text-align: center;
            font-style: italic;
            letter-spacing: 1px;
            margin-bottom: 1rem;
        }
        .form-popup .form-body {
            padding: 1.5rem;
        }
        .client-form {
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
        .row input, .row select {
            padding: 10px 14px;
            border-radius: 12px;
            border: 1px solid rgba(170, 126, 208, 0.3);
            outline: none;
            font-family: inherit;
            transition: all 0.3s ease;
        }
        .row input:focus, .row select:focus {
            border-color: #9b6fcf;
            box-shadow: 0 0 0 2px rgba(155, 111, 207, 0.2);
        }
        .row select {
            background: white;
            cursor: pointer;
        }
        .form-actions {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            margin-top: 1rem;
        }
        
        /* Popup PDF */
        .pdf-popup {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            border-radius: 24px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
            z-index: 999;
            width: 400px;
            max-width: 90%;
        }
        .pdf-popup .form-header {
            padding: 1.5rem 1.5rem 0 1.5rem;
            border-bottom: 1px solid rgba(170, 126, 208, 0.15);
        }
        .pdf-popup h2 {
            color: #e74c3c;
            text-align: center;
            font-style: italic;
            letter-spacing: 1px;
            margin-bottom: 1rem;
        }
        .pdf-popup .form-body {
            padding: 1.5rem;
        }
        .row-inline {
            display: flex;
            gap: 1rem;
            justify-content: space-between;
        }
        .row-inline .row {
            flex: 1;
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
            .form-popup, .pdf-popup { width: 95%; }
            .data-table td.actions { flex-direction: column; align-items: center; }
        }
        
        /* === Style pour le champ en erreur === */
        .input-error {
            border: 2px solid #e74c3c !important;
            background-color: #ffeaea;
        }
        
        /* === Style pour le champ en lecture seule === */
        .readonly-field {
            background-color: #f5f5f5 !important;
            color: #666 !important;
            cursor: not-allowed !important;
            border-color: #ddd !important;
        }
    .data-table td:nth-child(1),
    .data-table td:nth-child(4),
    .data-table td:nth-child(5),
    input[type="tel"],
    input[type="number"] {
        font-family: 'Courier New', 'Monaco', monospace;
        font-size: 16px;
        font-weight: bold;
    }
    </style>
</head>
<body>
    <div class="topbar">
        <a href="MobileMoney.jsp">🏠</a>
        <h1>👥 Gestion des Clients</h1>
        <div class="top-actions">
            <form action="ClientServlet" method="get" id="searchForm">
                <input type="hidden" name="action" value="rechercher">
                <input type="text" id="searchInput" name="motCle" 
                       placeholder="Rechercher par nom, téléphone, email..."
                       value="<%= request.getAttribute("motCle") != null ? request.getAttribute("motCle") : "" %>">
                <button type="submit">🔍</button>
            </form>
        </div>
    </div>

    <div class="container">
        <div class="card fade-in">
            <div class="card-header">
                <div></div>
                <div class="btn-group">
                    <button class="btn primary" onclick="showAddPopup()">➕ Ajouter un client</button>
                </div>
            </div>
            <div class="table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Numéro Téléphone</th>
                            <th>Nom</th>
                            <th>Sexe</th>
                            <th>Âge</th>
                            <th>Solde (Ar)</th>
                            <th>Email</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Client> clients = (List<Client>) request.getAttribute("listeClients");
                            if (clients != null && !clients.isEmpty()) {
                                for (Client c : clients) {
                        %>
                        <tr>
                            <td><%= c.getNumTel() %></td>
                            <td><%= c.getNom() %></td>
                            <td><%= c.getSexe() != null ? c.getSexe() : "-" %></td>
                            <td><%= c.getAge() != null ? c.getAge() : "-" %> ans</td>
                            <td><%= String.format("%,d", c.getSolde()) %> Ar</td>
                            <td><%= c.getMail() != null ? c.getMail() : "-" %></td>
                            <td class="actions">
                                <button class="edit" onclick="showEditPopup(this)">✏️ Modifier</button>
                                <button class="pdf-btn" onclick="showPdfPopup(this)">📄 Générer PDF</button>
                                <button class="delete" onclick="confirmDelete(this)">🗑️ Supprimer</button>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr><td colspan="7" style="text-align: center; padding: 2rem;">Aucun client trouvé</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Overlay pour les popups -->
    <div id="popupOverlay" class="popup-overlay" onclick="closeAllPopups()"></div>

    <!-- Popup AJOUT -->
    <div id="addPopup" class="form-popup">
        <div class="form-header">
            <h2>➕ Ajouter un client</h2>
        </div>
        <div class="form-body">
            <form class="client-form" method="post" action="ClientServlet" id="addForm">
                <input type="hidden" name="action" value="ajouter">
                
                <div class="row">
                    <label for="add-numtel">Numéro de téléphone</label>
                    <input type="tel" id="add-numtel" name="numtel" required placeholder="Ex: 0324432167">
                </div>
                
                <div class="row">
                    <label for="add-nom">Nom</label>
                    <input type="text" id="add-nom" name="nom" required>
                </div>
                
                <div class="row">
                    <label for="add-sexe">Sexe</label>
                    <select id="add-sexe" name="sexe">
                        <option value="">Sélectionner</option>
                        <option value="Masculin">Masculin</option>
                        <option value="Féminin">Féminin</option>
                    </select>
                </div>
                
                <div class="row">
                    <label for="add-age">Âge</label>
                    <input type="number" id="add-age" name="age" min="0" max="150" step="1">
                </div>
                
                <div class="row">
                    <label for="add-solde">Solde (Ar)</label>
                    <input type="number" id="add-solde" name="solde" min="0" step="1" value="0">
                </div>
                
                <div class="row">
                    <label for="add-mail">Email</label>
                    <input type="email" id="add-mail" name="mail">
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn primary">💾 Enregistrer</button>
                    <button type="button" class="btn secondary" onclick="closeAddPopup()">❌ Annuler</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Popup MODIFICATION -->
    <div id="editPopup" class="form-popup">
        <div class="form-header">
            <h2>✏️ Modifier un client</h2>
        </div>
        <div class="form-body">
            <form class="client-form" method="post" action="ClientServlet" id="editForm">
                <input type="hidden" name="action" value="modifier">
                <input type="hidden" id="edit-numtel-original" name="numtelOriginal">
                
                <div class="row">
                    <label for="edit-numtel">Numéro de téléphone</label>
                    <input type="tel" id="edit-numtel" name="numtel" required>
                </div>
                
                <div class="row">
                    <label for="edit-nom">Nom</label>
                    <input type="text" id="edit-nom" name="nom" required>
                </div>
                
                <div class="row">
                    <label for="edit-sexe">Sexe</label>
                    <select id="edit-sexe" name="sexe">
                        <option value="">Sélectionner</option>
                        <option value="Masculin">Masculin</option>
                        <option value="Féminin">Féminin</option>
                    </select>
                </div>
                
                <div class="row">
                    <label for="edit-age">Âge</label>
                    <input type="number" id="edit-age" name="age" min="0" max="150" step="1">
                </div>
                
                <div class="row">
                    <label for="edit-solde">Solde (Ar)</label>
                    <input type="number" id="edit-solde" name="solde" min="0" step="1" class="readonly-field" readonly>
                </div>
                
                <div class="row">
                    <label for="edit-mail">Email</label>
                    <input type="email" id="edit-mail" name="mail">
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn primary">💾 Enregistrer</button>
                    <button type="button" class="btn secondary" onclick="closeEditPopup()">❌ Annuler</button> 
                </div>
            </form>
        </div>
    </div>

    <!-- Popup PDF pour choisir le mois et l'année -->
    <div id="pdfPopup" class="pdf-popup">
        <div class="form-header">
            <h2>📄 Générer le relevé PDF</h2>
        </div>
        <div class="form-body">
            <form class="client-form" method="post" action="ClientServlet" id="pdfForm" target="_blank">
                <input type="hidden" name="action" value="genererPdf">
                <input type="hidden" id="pdf-numtel" name="numtel">
                
                <div class="row-inline">
                    <div class="row">
                        <label for="pdf-mois">Mois</label>
                        <select id="pdf-mois" name="mois" required>
                            <option value="1">Janvier</option>
                            <option value="2">Février</option>
                            <option value="3">Mars</option>
                            <option value="4">Avril</option>
                            <option value="5">Mai</option>
                            <option value="6">Juin</option>
                            <option value="7">Juillet</option>
                            <option value="8">Août</option>
                            <option value="9">Septembre</option>
                            <option value="10">Octobre</option>
                            <option value="11">Novembre</option>
                            <option value="12">Décembre</option>
                        </select>
                    </div>
                    
                    <div class="row">
                        <label for="pdf-annee">Année</label>
                        <select id="pdf-annee" name="annee" required>
                            <%
                                int currentYear = java.time.Year.now().getValue();
                                for (int y = currentYear - 5; y <= currentYear + 1; y++) {
                            %>
                            <option value="<%= y %>" <%= y == currentYear ? "selected" : "" %>><%= y %></option>
                            <% } %>
                        </select>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn pdf">📄 Générer PDF</button>
                    <button type="button" class="btn secondary" onclick="closePdfPopup()">❌ Annuler</button>
                </div>
            </form>
        </div>
    </div>

    <div class="footer">
        <p>© 2026 Mobile Money - Gestion des clients</p>
    </div>

    <script>
        // Variable pour stocker le numéro de téléphone du client sélectionné
        let currentPdfNumtel = '';
        
        // === Gestion des popups ===
        function showAddPopup() {
            document.getElementById('addPopup').style.display = 'block';
            document.getElementById('popupOverlay').style.display = 'block';
            document.getElementById('addForm').reset();
        }
        
        function closeAddPopup() {
            document.getElementById('addPopup').style.display = 'none';
            document.getElementById('popupOverlay').style.display = 'none';
        }
        
        function showEditPopup(btn) {
            const row = btn.closest('tr');
            const numtel = row.cells[0].textContent.trim();
            const nom = row.cells[1].textContent.trim();
            const sexe = row.cells[2].textContent.trim();
            const age = row.cells[3].textContent.trim().replace(' ans', '');
            const solde = row.cells[4].textContent.trim().replace(' Ar', '').replace(/,/g, '').replace(/\s/g, '');
            const mail = row.cells[5].textContent.trim();
            
            document.getElementById('edit-numtel-original').value = numtel;
            document.getElementById('edit-numtel').value = numtel;
            document.getElementById('edit-nom').value = nom;
            document.getElementById('edit-sexe').value = sexe !== '-' ? sexe : '';
            document.getElementById('edit-age').value = age !== '-' ? age : '';
            document.getElementById('edit-solde').value = solde;
            document.getElementById('edit-mail').value = mail !== '-' ? mail : '';
            
            document.getElementById('editPopup').style.display = 'block';
            document.getElementById('popupOverlay').style.display = 'block';
        }
        
        function closeEditPopup() {
            document.getElementById('editPopup').style.display = 'none';
            document.getElementById('popupOverlay').style.display = 'none';
        }
        
        // === Popup PDF ===
        function showPdfPopup(btn) {
            const row = btn.closest('tr');
            const numtel = row.cells[0].textContent.trim();
            const nom = row.cells[1].textContent.trim();
            
            currentPdfNumtel = numtel;
            document.getElementById('pdf-numtel').value = numtel;
            
            // Optionnel : afficher le nom du client dans le titre
            const pdfTitle = document.querySelector('#pdfPopup h2');
            if (pdfTitle) {
                pdfTitle.innerHTML = `📄 Relevé pour : ${nom}`;
            }
            
            document.getElementById('pdfPopup').style.display = 'block';
            document.getElementById('popupOverlay').style.display = 'block';
        }
        
        function closePdfPopup() {
            document.getElementById('pdfPopup').style.display = 'none';
            document.getElementById('popupOverlay').style.display = 'none';
        }
        
        function closeAllPopups() {
            closeAddPopup();
            closeEditPopup();
            closePdfPopup();
        }
        
        // === Vérification du numéro de téléphone en temps réel (pour l'ajout) ===
        document.getElementById('add-numtel').addEventListener('blur', function() {
            const numtel = this.value.trim();
            if (numtel !== '') {
                const xhr = new XMLHttpRequest();
                xhr.open('GET', 'ClientServlet?action=verifierNumtel&numtel=' + encodeURIComponent(numtel), true);
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        const existe = xhr.responseText === 'true';
                        const field = document.getElementById('add-numtel');
                        if (existe) {
                            field.classList.add('input-error');
                            showTempMessage('Ce numéro de téléphone existe déjà !', 'error');
                        } else {
                            field.classList.remove('input-error');
                        }
                    }
                };
                xhr.send();
            }
        });
        
        // === Validation du formulaire d'ajout ===
        document.getElementById('addForm').addEventListener('submit', function(e) {
            const numtelField = document.getElementById('add-numtel');
            const nomField = document.getElementById('add-nom');
            const ageField = document.getElementById('add-age');
            
            if (numtelField.classList.contains('input-error')) {
                e.preventDefault();
                showTempMessage('Ce numéro de téléphone existe déjà !', 'error');
                numtelField.focus();
                return;
            }
            
            if (numtelField.value.trim() === '') {
                e.preventDefault();
                showTempMessage('Le numéro de téléphone est obligatoire', 'error');
                numtelField.focus();
                return;
            }
            
            if (nomField.value.trim() === '') {
                e.preventDefault();
                showTempMessage('Le nom est obligatoire', 'error');
                nomField.focus();
                return;
            }
            
            const age = parseInt(ageField.value);
            if (ageField.value !== '' && (isNaN(age) || age < 0 || age > 150)) {
                e.preventDefault();
                showTempMessage('L\'âge doit être entre 0 et 150 ans', 'error');
                ageField.focus();
                return;
            }
        });
        
        // === Validation du formulaire de modification ===
        document.getElementById('editForm').addEventListener('submit', function(e) {
            const nomField = document.getElementById('edit-nom');
            const ageField = document.getElementById('edit-age');
            
            if (nomField.value.trim() === '') {
                e.preventDefault();
                showTempMessage('Le nom est obligatoire', 'error');
                nomField.focus();
                return;
            }
            
            const age = parseInt(ageField.value);
            if (ageField.value !== '' && (isNaN(age) || age < 0 || age > 150)) {
                e.preventDefault();
                showTempMessage('L\'âge doit être entre 0 et 150 ans', 'error');
                ageField.focus();
                return;
            }
        });
        
        // === Suppression avec confirmation ===
        function confirmDelete(btn) {
            const row = btn.closest('tr');
            const numtel = row.cells[0].textContent.trim();
            const nom = row.cells[1].textContent.trim();
            
            if (confirm('Voulez-vous vraiment supprimer le client : ' + nom + ' (' + numtel + ') ?')) {
                window.location.href = 'ClientServlet?action=supprimer&numtel=' + encodeURIComponent(numtel);
            }
        }
        
        // === Message temporaire ===
        function showTempMessage(message, type) {
            const existingMsg = document.querySelector('.message-box');
            if (existingMsg) existingMsg.remove();
            
            const msgBox = document.createElement('div');
            msgBox.classList.add('message-box', type);
            msgBox.textContent = message;
            document.body.appendChild(msgBox);
            
            setTimeout(() => { msgBox.style.opacity = '1'; }, 100);
            setTimeout(() => {
                msgBox.style.opacity = '0';
                setTimeout(() => msgBox.remove(), 300);
            }, 5000);
        }
        
        // === Messages flash du serveur ===
        window.onload = function() {
            <% if (request.getAttribute("message") != null) { %>
                showTempMessage("<%= request.getAttribute("message") %>", "<%= request.getAttribute("typeMessage") %>");
            <% } %>
            
            // Retour automatique à la liste complète si champ vidé
            const searchInput = document.getElementById('searchInput');
            if (searchInput) {
                searchInput.addEventListener('input', function() {
                    if (this.value.trim() === '') {
                        window.location.href = 'ClientServlet?action=liste';
                    }
                });
            }
            
            // Fermeture avec Echap
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    closeAllPopups();
                }
            });
        };
    </script>
</body>
</html>