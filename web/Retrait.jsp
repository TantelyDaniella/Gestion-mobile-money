<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, Model.Retrait, Model.Client" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Retraits d'argent</title>
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
        
        .data-table td:nth-child(2),
        .data-table td:nth-child(3),
        .data-table td:nth-child(4) {
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
        .retrait-form {
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
        .row input, .row select {
            padding: 10px 14px;
            border-radius: 12px;
            border: 1px solid rgba(170, 126, 208, 0.3);
            outline: none;
            font-family: inherit;
            transition: all 0.3s ease;
        }
        
        .row input[type="number"],
        .row input[name*="tel"] {
            font-family: 'Courier New', 'Monaco', monospace;
            font-weight: bold;
            font-size: 16px;
        }
        
        .row input:focus, .row select:focus {
            border-color: #9b6fcf;
            box-shadow: 0 0 0 2px rgba(155, 111, 207, 0.2);
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
        <h1>🏧 Gestion des Retraits d'argent</h1>
    </div>

    <div class="container">
        <div class="card fade-in">
            <div class="card-header">
                <div class="top-actions">
                    <form action="RetraitServlet" method="get" id="searchForm">
                        <input type="hidden" name="action" value="rechercherParDate">
                        <input type="date" id="searchDate" name="dateRecherche" 
                               value="<%= request.getAttribute("dateRecherche") != null ? request.getAttribute("dateRecherche") : "" %>">
                        <button type="submit">🔍 Rechercher</button>
                        <button type="button" onclick="resetSearch()">🔄 Réinitialiser</button>
                    </form>
                </div>
                <div class="btn-group">
                    <button class="btn primary" onclick="showAddPopup()">➕ Nouveau retrait</button>
                </div>
            </div>
            <div class="table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID Retrait</th>
                            <th>Client (Tél)</th>
                            <th>Montant retiré (Ar)</th>
                            <th>Date de retrait</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Retrait> retraits = (List<Retrait>) request.getAttribute("listeRetraits");
                            if (retraits != null && !retraits.isEmpty()) {
                                for (Retrait r : retraits) {
                        %>
                        <tr>
                            <td><%= r.getIdRecep() != null ? r.getIdRecep() : "-" %></td>
                            <td><%= r.getNumTel() != null ? r.getNumTel() : "-" %></td>
                            <td><%= String.format("%,d", r.getMontant()) %> Ar</td>
                            <td><%= r.getDateRecep() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(r.getDateRecep()) : "-" %></td>
                            <td class="actions">
                                <button class="edit" onclick="showEditPopup(this)">✏️ Modifier</button>
                                <button class="delete" onclick="confirmDelete(this)">🗑️ Supprimer</button>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr><td colspan="5" style="text-align: center; padding: 2rem;">Aucun retrait trouvé</td></tr>
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
            <h2>🏧 Nouveau retrait d'argent</h2>
        </div>
        <div class="form-body">
            <form class="retrait-form" method="post" action="RetraitServlet" id="addForm">
                <input type="hidden" name="action" value="ajouter">
                
                <div class="row">
                    <label for="add-numtel">Client (numéro de téléphone)</label>
                    <select id="add-numtel" name="numtel" required style="font-family: 'Courier New', 'Monaco', monospace; font-weight: bold; font-size: 16px;">
                        <option value="">-- Sélectionner un client --</option>
                        <%
                            List<Client> clients = (List<Client>) request.getAttribute("listeClients");
                            if (clients != null) {
                                for (Client c : clients) {
                        %>
                        <option value="<%= c.getNumTel() %>" data-solde="<%= c.getSolde() %>" data-nom="<%= c.getNom() %>">
                            <%= c.getNumTel() %> - <%= c.getNom() %> (Solde: <%= String.format("%,d", c.getSolde()) %> Ar)
                        </option>
                        <% }} %>
                    </select>
                </div>
                
                <div id="soldeInfo" class="info-solde" style="display: none;">
                    Solde disponible : <span id="soldeDisponible">0</span> Ar
                </div>
                
                <div class="row">
                    <label for="add-montant">Montant à retirer (Ar)</label>
                    <input type="number" id="add-montant" name="montant" min="1" step="1" required>
                </div>
                
                <div id="fraisInfo" class="frais-info" style="display: none;">
                    <p>📊 Frais de retrait : <span id="fraisRetrait">0</span> Ar</p>
                    <div class="total-debite">
                        Total à débiter du compte : <span id="totalDebit">0</span> Ar
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn primary">💾 Effectuer le retrait</button>
                    <button type="button" class="btn secondary" onclick="closeAddPopup()">❌ Annuler</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Popup MODIFICATION -->
    <div id="editPopup" class="form-popup">
        <div class="form-header">
            <h2>✏️ Modifier un retrait</h2>
        </div>
        <div class="form-body">
            <form class="retrait-form" method="post" action="RetraitServlet" id="editForm">
                <input type="hidden" name="action" value="modifier">
                <input type="hidden" id="edit-idrecep" name="idrecep">
                
                <div class="row">
                    <label for="edit-numtel">Client (numéro de téléphone)</label>
                    <select id="edit-numtel" name="numtel" required style="font-family: 'Courier New', 'Monaco', monospace; font-weight: bold; font-size: 16px;">
                        <option value="">-- Sélectionner un client --</option>
                        <% if (clients != null) {
                            for (Client c : clients) {
                        %>
                        <option value="<%= c.getNumTel() %>"><%= c.getNumTel() %> - <%= c.getNom() %></option>
                        <% }} %>
                    </select>
                </div>
                
                <div class="row">
                    <label for="edit-montant">Montant (Ar)</label>
                    <input type="number" id="edit-montant" name="montant" min="1" step="1" required>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn primary">💾 Enregistrer</button>
                    <button type="button" class="btn secondary" onclick="closeEditPopup()">❌ Annuler</button>
                </div>
            </form>
        </div>
    </div>

    <div class="footer">
        <p>© 2026 Mobile Money - Gestion des retraits d'argent</p>
    </div>

    <script>
        let fraisRetraitRates = [];
        
        function loadFraisRates() {
            fetch('RetraitServlet?action=getFraisRates')
                .then(response => response.json())
                .then(data => {
                    fraisRetraitRates = data.fraisRetrait || [];
                })
                .catch(error => console.error('Erreur chargement frais:', error));
        }
        
        function calculerFraisRetrait(montant) {
            for (let rate of fraisRetraitRates) {
                if (montant >= rate.montant1 && montant <= rate.montant2) {
                    return rate.frais;
                }
            }
            return 0;
        }
        
        function updateFraisDisplay() {
            const montant = parseInt(document.getElementById('add-montant').value) || 0;
            const fraisRetrait = calculerFraisRetrait(montant);
            const totalDebit = montant + fraisRetrait;
            
            document.getElementById('fraisRetrait').textContent = fraisRetrait.toLocaleString();
            document.getElementById('totalDebit').textContent = totalDebit.toLocaleString();
            
            const selectClient = document.getElementById('add-numtel');
            const selectedOption = selectClient.options[selectClient.selectedIndex];
            const solde = parseInt(selectedOption?.getAttribute('data-solde') || 0);
            
            if (montant > 0 && totalDebit > solde) {
                document.getElementById('totalDebit').style.color = '#e74c3c';
                document.querySelector('#addForm button[type="submit"]').disabled = true;
                showTempMessage('Solde insuffisant pour effectuer ce retrait !', 'error');
            } else {
                document.getElementById('totalDebit').style.color = 'inherit';
                document.querySelector('#addForm button[type="submit"]').disabled = false;
            }
        }
        
        function checkSoldeClient() {
            const select = document.getElementById('add-numtel');
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
            const idrecep = row.cells[0].textContent.trim();
            const numtel = row.cells[1].textContent.trim();
            const montant = row.cells[2].textContent.trim().replace(' Ar', '').replace(/,/g, '').replace(/\s/g, '');
            
            document.getElementById('edit-idrecep').value = idrecep;
            document.getElementById('edit-numtel').value = numtel;
            document.getElementById('edit-montant').value = montant;
            
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
            const idrecep = row.cells[0].textContent.trim();
            
            if (confirm('Voulez-vous vraiment supprimer ce retrait (ID: ' + idrecep + ') ?')) {
                window.location.href = 'RetraitServlet?action=supprimer&idrecep=' + encodeURIComponent(idrecep);
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
        
        function resetSearch() {
            document.getElementById('searchDate').value = '';
            document.getElementById('searchForm').submit();
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            const clientSelect = document.getElementById('add-numtel');
            const montantInput = document.getElementById('add-montant');
            
            if (clientSelect) {
                clientSelect.addEventListener('change', checkSoldeClient);
            }
            if (montantInput) {
                montantInput.addEventListener('input', updateFraisDisplay);
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
    </script>
</body>
</html>