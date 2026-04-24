<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, Model.Envoi, Model.Client" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Envois d'argent</title>
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
            background: none;
            padding: 5px 15px;
            border-radius: 40px;
        }

        .top-actions input {
            padding: 8px 12px;
            border-radius: 30px;
            border: 1px solid #c692f0;
            outline: none;
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 14px;
            background: white;
            color: #4a2e5c;
        }
        .top-actions button {
            border: none;
            border-radius: 30px;
            padding: 8px 16px;
            cursor: pointer;
            background: #9b6fcf;
            color: white;
            font-family: inherit;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .top-actions button:hover {
            background: #c692f0;
            transform: scale(1.02);
        }

        .top-actions button[onclick="resetSearch()"] {
            background: #e74c3c;
        }

        .top-actions button[onclick="resetSearch()"]:hover {
            background: #c0392b;
        }

        .container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 2rem;
            position: relative;
            z-index: 1;
        }

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
        .btn.email {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            color: white;
        }
        .btn.email:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(39, 174, 96, 0.4);
        }

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
        
        /* Style pour les colonnes numériques dans le tableau */
        .data-table td:nth-child(2),  /* Envoyeur Tél */
        .data-table td:nth-child(3),  /* Récepteur Tél */
        .data-table td:nth-child(4),  /* Montant */
        .data-table td:nth-child(5){  /* Date */
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 16px;
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
        .data-table .email-btn {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            color: white;
        }
        .data-table .email-btn:hover {
            transform: scale(1.05);
        }

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
            width: 550px;
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
        .envoi-form {
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
            font-weight: 600;
        }
        .row input, .row select, .row textarea {
            padding: 10px 14px;
            border-radius: 12px;
            border: 1px solid rgba(170, 126, 208, 0.3);
            outline: none;
            font-family: inherit;
            transition: all 0.3s ease;
        }
        
        /* Style pour les inputs de type number, téléphone et montant */
        .row input[type="number"],
        .row select option[value*="0-9"],
        .row input[name*="tel"],
        .row input[type="date"],
        .row input[name*="num"],
        #add-montant,
        #edit-montant,
        #add-numenvoyeur,
        #add-numrecepteur,
        #edit-numenvoyeur,
        #edit-numrecepteur {
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 16px;
        }
        
        .row input:focus, .row select:focus, .row textarea:focus {
            border-color: #9b6fcf;
            box-shadow: 0 0 0 2px rgba(155, 111, 207, 0.2);
        }
        .row select {
            background: white;
            cursor: pointer;
        }
        .row-inline {
            display: flex;
            gap: 1rem;
            justify-content: space-between;
        }
        .row-inline .row {
            flex: 1;
        }
        .form-actions {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            margin-top: 1rem;
        }
        
        .info-solde {
            background: #f0e6ff;
            border-radius: 12px;
            padding: 12px;
            margin-bottom: 15px;
            font-size: 0.9rem;
            text-align: center;
        }
        .info-solde span {
            font-weight: bold;
            color: #9b6fcf;
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 18px;
        }
        
        .frais-info {
            background: #e8f5e9;
            border-radius: 12px;
            padding: 10px;
            margin-top: 10px;
            font-size: 0.85rem;
        }
        
        .frais-info p {
            margin: 5px 0;
        }
        
        .frais-info span {
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 15px;
        }
        
        .total-debite {
            background: #fff3e0;
            border-radius: 12px;
            padding: 10px;
            margin-top: 10px;
            font-weight: bold;
        }
        
        .total-debite span {
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 16px;
        }
        
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
        .message-box.warning {
            background: linear-gradient(135deg, #f39c12, #e67e22);
        }

        .footer {
            text-align: center;
            padding: 1.5rem 0;
            border-top: 1px solid rgba(170, 126, 208, 0.15);
            color: #765b91;
            font-size: 0.7rem;
            letter-spacing: 2px;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .fade-in {
            animation: fadeIn 0.6s ease forwards;
        }
        
        @media (max-width: 768px) {
            .container { padding: 0 1rem; }
            .topbar h1 { font-size: 1.1rem; }
            .data-table th, .data-table td { padding: 8px 6px; font-size: 0.85rem; }
            .form-popup { width: 95%; }
            .data-table td.actions { flex-direction: column; align-items: center; }
            
            .data-table td:nth-child(1),
            .data-table td:nth-child(2),
            .data-table td:nth-child(3),
            .data-table td:nth-child(4) {
                font-size: 13px;
            }
        }
        
        .input-error {
            border: 2px solid #e74c3c !important;
            background-color: #ffeaea;
        }
    </style>
</head>
<body>
    <div class="topbar">
        <a href="MobileMoney.jsp">🏠</a>
        <h1>💰 Gestion des Envois d'argent</h1>
    </div>

    <div class="container">
        <div class="card fade-in">
            <div class="card-header">
                <div class="top-actions">
                    <form action="EnvoiServlet" method="get" id="searchForm">
                        <input type="hidden" name="action" value="rechercherParDate">
                        <input type="date" id="searchDate" name="dateRecherche" 
                               value="<%= request.getAttribute("dateRecherche") != null ? request.getAttribute("dateRecherche") : "" %>">
                        <button type="submit">🔍 Rechercher</button>
                         <button type="button" onclick="resetSearch()">🔄 Réinitialiser</button>
                    </form>
                </div>
                <div class="btn-group">
                    <button class="btn primary" onclick="showAddPopup()">➕ Nouvel envoi</button>
                </div>
            </div>
            <div class="table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID Envoi</th>
                            <th>Envoyeur (Tél)</th>
                            <th>Récepteur (Tél)</th>
                            <th>Montant (Ar)</th>
                            <th>Date</th>
                            <th>Payer frais retrait</th>
                            <th>Raison</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Envoi> envois = (List<Envoi>) request.getAttribute("listeEnvois");
                            if (envois != null && !envois.isEmpty()) {
                                for (Envoi e : envois) {
                        %>
                        <tr>
                            <td><%= e.getIdEnvoyer() != null ? e.getIdEnvoyer() : "-" %></td>
                            <td><%= e.getNumEnvoyeur() != null ? e.getNumEnvoyeur() : "-" %></td>
                            <td><%= e.getNumRecepteur() != null ? e.getNumRecepteur() : "-" %></td>
                            <td><%= String.format("%,d", e.getMontant()) %> Ar</td>
                            <td><%= e.getDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(e.getDate()) : "-" %></td>
                            <td><%= e.getPayer_Frais_Retrait() != null && e.getPayer_Frais_Retrait() ? "Oui" : "Non" %></td>
                            <td><%= e.getRaison() != null ? e.getRaison() : "-" %></td>
                            <td class="actions">
                                <button class="edit" onclick="showEditPopup(this)">✏️ Modifier</button>
                                <button class="delete" onclick="confirmDelete(this)">🗑️ Supprimer</button>
                                <button class="email-btn" onclick="renvoyerEmail(this)">📧 Envoyer e-mail</button>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr><td colspan="8" style="text-align: center; padding: 2rem;">Aucun envoi trouvé</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div id="popupOverlay" class="popup-overlay" onclick="closeAllPopups()"></div>

    <!-- Popup AJOUT -->
    <div id="addPopup" class="form-popup">
        <div class="form-header">
            <h2>💰 Nouvel envoi d'argent</h2>
        </div>
        <div class="form-body">
            <form class="envoi-form" method="post" action="EnvoiServlet" id="addForm">
                <input type="hidden" name="action" value="ajouter">
                
                <div class="row">
                    <label for="add-numenvoyeur">Envoyeur (numéro de téléphone)</label>
                    <select id="add-numenvoyeur" name="numenvoyeur" required style="font-family: 'Courier New', 'Monaco', monospace; font-weight: bold; font-size: 16px;">
                        <option value="">-- Sélectionner un client --</option>
                        <%
                            List<Client> clients = (List<Client>) request.getAttribute("listeClients");
                            if (clients != null) {
                                for (Client c : clients) {
                        %>
                        <option value="<%= c.getNumTel() %>" data-solde="<%= c.getSolde() %>" data-nom="<%= c.getNom() %>" style="font-family: 'Courier New', 'Monaco', monospace;">
                            <%= c.getNumTel() %> - <%= c.getNom() %> (Solde: <%= String.format("%,d", c.getSolde()) %> Ar)
                        </option>
                        <% }} %>
                    </select>
                </div>
                
                <div id="soldeInfo" class="info-solde" style="display: none;">
                    Solde disponible : <span id="soldeDisponible">0</span> Ar
                </div>
                
                <div class="row">
                    <label for="add-numrecepteur">Récepteur (numéro de téléphone)</label>
                    <select id="add-numrecepteur" name="numrecepteur" required style="font-family: 'Courier New', 'Monaco', monospace; font-weight: bold; font-size: 16px;">
                        <option value="">-- Sélectionner un client --</option>
                        <% if (clients != null) {
                            for (Client c : clients) {
                        %>
                        <option value="<%= c.getNumTel() %>" data-nom="<%= c.getNom() %>" style="font-family: 'Courier New', 'Monaco', monospace;">
                            <%= c.getNumTel() %> - <%= c.getNom() %>
                        </option>
                        <% }} %>
                    </select>
                </div>
                
                <div class="row">
                    <label for="add-montant">Montant à envoyer (Ar)</label>
                    <input type="number" id="add-montant" name="montant" min="1" step="1" required>
                </div>
                
                <div class="row">
                    <label for="add-payer_frais">Payer les frais de retrait du récepteur ?</label>
                    <select id="add-payer_frais" name="payer_frais_retrait">
                        <option value="false">Non (le récepteur paiera ses frais)</option>
                        <option value="true">Oui (je paie aussi les frais du récepteur)</option>
                    </select>
                </div>
                
                <div id="fraisInfo" class="frais-info" style="display: none;">
                    <p>📊 Frais d'envoi : <span id="fraisEnvoi">0</span> Ar</p>
                    <p>📊 Frais de retrait : <span id="fraisRetrait">0</span> Ar</p>
                    <div class="total-debite">
                        Total à débiter de l'envoyeur : <span id="totalDebit">0</span> Ar
                    </div>
                </div>
                
                <div class="row">
                    <label for="add-raison">Raison du transfert</label>
                    <textarea id="add-raison" name="raison" rows="2" placeholder="Ex: Paiement, Aide familiale, ..."></textarea>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn primary">💾 Effectuer l'envoi</button>
                    <button type="button" class="btn secondary" onclick="closeAddPopup()">❌ Annuler</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Popup MODIFICATION -->
    <div id="editPopup" class="form-popup">
        <div class="form-header">
            <h2>✏️ Modifier un envoi</h2>
        </div>
        <div class="form-body">
            <form class="envoi-form" method="post" action="EnvoiServlet" id="editForm">
                <input type="hidden" name="action" value="modifier">
                <input type="hidden" id="edit-idenvoyer" name="idenvoyer">
                
                <div class="row">
                    <label for="edit-numenvoyeur">Envoyeur</label>
                    <select id="edit-numenvoyeur" name="numenvoyeur" required style="font-family: 'Courier New', 'Monaco', monospace; font-weight: bold; font-size: 16px;">
                        <option value="">-- Sélectionner un client --</option>
                        <% if (clients != null) {
                            for (Client c : clients) {
                        %>
                        <option value="<%= c.getNumTel() %>" style="font-family: 'Courier New', 'Monaco', monospace;"><%= c.getNumTel() %> - <%= c.getNom() %></option>
                        <% }} %>
                    </select>
                </div>
                
                <div class="row">
                    <label for="edit-numrecepteur">Récepteur</label>
                    <select id="edit-numrecepteur" name="numrecepteur" required style="font-family: 'Courier New', 'Monaco', monospace; font-weight: bold; font-size: 16px;">
                        <option value="">-- Sélectionner un client --</option>
                        <% if (clients != null) {
                            for (Client c : clients) {
                        %>
                        <option value="<%= c.getNumTel() %>" style="font-family: 'Courier New', 'Monaco', monospace;"><%= c.getNumTel() %> - <%= c.getNom() %></option>
                        <% }} %>
                    </select>
                </div>
                
                <div class="row">
                    <label for="edit-montant">Montant (Ar)</label>
                    <input type="number" id="edit-montant" name="montant" min="1" step="1" required>
                </div>
                
                <div class="row">
                    <label for="edit-payer_frais">Payer les frais de retrait ?</label>
                    <select id="edit-payer_frais" name="payer_frais_retrait">
                        <option value="false">Non</option>
                        <option value="true">Oui</option>
                    </select>
                </div>
                
                <div class="row">
                    <label for="edit-raison">Raison</label>
                    <textarea id="edit-raison" name="raison" rows="2"></textarea>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn primary">💾 Enregistrer</button>
                    <button type="button" class="btn secondary" onclick="closeEditPopup()">❌ Annuler</button>
                </div>
            </form>
        </div>
    </div>

    <div class="footer">
        <p>© 2026 Mobile Money - Gestion des envois d'argent</p>
    </div>

    <script>
        let fraisEnvoiRates = [];
        let fraisRetraitRates = [];
        
        function loadFraisRates() {
            fetch('EnvoiServlet?action=getFraisRates')
                .then(response => response.json())
                .then(data => {
                    fraisEnvoiRates = data.fraisEnvoi || [];
                    fraisRetraitRates = data.fraisRetrait || [];
                })
                .catch(error => console.error('Erreur chargement frais:', error));
        }
        
        function calculerFrais(montant, rates) {
            for (let rate of rates) {
                if (montant >= rate.montant1 && montant <= rate.montant2) {
                    return rate.frais;
                }
            }
            return 0;
        }
        
        function updateFraisDisplay() {
            const montant = parseInt(document.getElementById('add-montant').value) || 0;
            const fraisEnvoi = calculerFrais(montant, fraisEnvoiRates);
            const fraisRetrait = calculerFrais(montant, fraisRetraitRates);
            const payerFraisRetrait = document.getElementById('add-payer_frais').value === 'true';
            
            let totalDebit = montant + fraisEnvoi;
            if (payerFraisRetrait) {
                totalDebit += fraisRetrait;
            }
            
            document.getElementById('fraisEnvoi').textContent = fraisEnvoi.toLocaleString();
            document.getElementById('fraisRetrait').textContent = fraisRetrait.toLocaleString();
            document.getElementById('totalDebit').textContent = totalDebit.toLocaleString();
            
            const selectEnvoyeur = document.getElementById('add-numenvoyeur');
            const selectedOption = selectEnvoyeur.options[selectEnvoyeur.selectedIndex];
            const solde = parseInt(selectedOption?.getAttribute('data-solde') || 0);
            
            if (montant > 0 && totalDebit > solde) {
                document.getElementById('totalDebit').style.color = '#e74c3c';
                document.querySelector('#addForm button[type="submit"]').disabled = true;
                showTempMessage('Solde insuffisant pour effectuer cet envoi !', 'error');
            } else {
                document.getElementById('totalDebit').style.color = 'inherit';
                document.querySelector('#addForm button[type="submit"]').disabled = false;
            }
        }
        
        function checkSoldeEnvoyeur() {
            const select = document.getElementById('add-numenvoyeur');
            const selectedOption = select.options[select.selectedIndex];
            const solde = parseInt(selectedOption?.getAttribute('data-solde') || 0);
            
            if (select.value) {
                document.getElementById('soldeInfo').style.display = 'block';
                document.getElementById('soldeDisponible').textContent = solde.toLocaleString();
                document.getElementById('fraisInfo').style.display = 'block';
                updateFraisDisplay();
            } else {
                document.getElementById('soldeInfo').style.display = 'none';
                document.getElementById('fraisInfo').style.display = 'none';
            }
        }
        
        function checkMemePersonne() {
            const envoyeur = document.getElementById('add-numenvoyeur').value;
            const recepteur = document.getElementById('add-numrecepteur').value;
            
            if (envoyeur && recepteur && envoyeur === recepteur) {
                showTempMessage('L\'envoyeur et le récepteur ne peuvent pas être la même personne !', 'error');
                document.getElementById('add-numrecepteur').value = '';
            }
        }
        
        function showAddPopup() {
            document.getElementById('addPopup').style.display = 'block';
            document.getElementById('popupOverlay').style.display = 'block';
            document.getElementById('addForm').reset();
            document.getElementById('soldeInfo').style.display = 'none';
            document.getElementById('fraisInfo').style.display = 'none';
            loadFraisRates();
        }
        
        function closeAddPopup() {
            document.getElementById('addPopup').style.display = 'none';
            document.getElementById('popupOverlay').style.display = 'none';
        }
        
        function showEditPopup(btn) {
            const row = btn.closest('tr');
            const idenvoyer = row.cells[0].textContent.trim();
            const numenvoyeur = row.cells[1].textContent.trim();
            const numrecepteur = row.cells[2].textContent.trim();
            const montant = row.cells[3].textContent.trim().replace(' Ar', '').replace(/,/g, '').replace(/\s/g, '');
            const payerFrais = row.cells[5].textContent.trim() === 'Oui';
            const raison = row.cells[6].textContent.trim();
            
            document.getElementById('edit-idenvoyer').value = idenvoyer;
            document.getElementById('edit-numenvoyeur').value = numenvoyeur;
            document.getElementById('edit-numrecepteur').value = numrecepteur;
            document.getElementById('edit-montant').value = montant;
            document.getElementById('edit-payer_frais').value = payerFrais ? 'true' : 'false';
            document.getElementById('edit-raison').value = raison !== '-' ? raison : '';
            
            document.getElementById('editPopup').style.display = 'block';
            document.getElementById('popupOverlay').style.display = 'block';
        }
        
        function closeEditPopup() {
            document.getElementById('editPopup').style.display = 'none';
            document.getElementById('popupOverlay').style.display = 'none';
        }
        
        function closeAllPopups() {
            closeAddPopup();
            closeEditPopup();
        }
        
        function confirmDelete(btn) {
            const row = btn.closest('tr');
            const idenvoyer = row.cells[0].textContent.trim();
            
            if (confirm('Voulez-vous vraiment supprimer cet envoi (ID: ' + idenvoyer + ') ?')) {
                window.location.href = 'EnvoiServlet?action=supprimer&idenvoyer=' + encodeURIComponent(idenvoyer);
            }
        }
        
        function renvoyerEmail(btn) {
            const row = btn.closest('tr');
            const idenvoyer = row.cells[0].textContent.trim();
            
            if (confirm('Renvoyer la notification par email pour cet envoi ?')) {
                fetch('EnvoiServlet?action=renvoyerEmail&idenvoyer=' + encodeURIComponent(idenvoyer))
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            showTempMessage('Email renvoyé avec succès !', 'success');
                        } else {
                            showTempMessage('Erreur lors de l\'envoi de l\'email', 'error');
                        }
                    })
                    .catch(error => {
                        showTempMessage('Erreur réseau', 'error');
                    });
            }
        }
        
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
        
        document.addEventListener('DOMContentLoaded', function() {
            const envoyeurSelect = document.getElementById('add-numenvoyeur');
            const recepteurSelect = document.getElementById('add-numrecepteur');
            const montantInput = document.getElementById('add-montant');
            const payerFraisSelect = document.getElementById('add-payer_frais');
            
            if (envoyeurSelect) {
                envoyeurSelect.addEventListener('change', checkSoldeEnvoyeur);
            }
            if (recepteurSelect) {
                recepteurSelect.addEventListener('change', checkMemePersonne);
            }
            if (montantInput) {
                montantInput.addEventListener('input', updateFraisDisplay);
            }
            if (payerFraisSelect) {
                payerFraisSelect.addEventListener('change', updateFraisDisplay);
            }
            
            loadFraisRates();
            
            <% if (request.getAttribute("message") != null) { %>
                showTempMessage("<%= request.getAttribute("message") %>", "<%= request.getAttribute("typeMessage") %>");
            <% } %>
            
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    closeAllPopups();
                }
            });
        });
        function resetSearch() {
            // Vider le champ de date
            document.getElementById('searchDate').value = '';
            // Soumettre le formulaire avec une date vide
            document.getElementById('searchForm').submit();
        }
    </script>
</body>
</html>